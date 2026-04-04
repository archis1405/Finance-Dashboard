package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.record.CreateFinancialRecordRequest;
import com.example.finance_dashboard_backend.dto.record.FinancialRecordResponse;
import com.example.finance_dashboard_backend.dto.record.UpdateFinancialRecordRequest;
import com.example.finance_dashboard_backend.exception.BadRequestException;
import com.example.finance_dashboard_backend.exception.ResourceNotFoundException;
import com.example.finance_dashboard_backend.model.*;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.specification.FinancialRecordSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {
    private final FinancialRecordRepository financialRecordRepository;
    private final UserService userService;
    private final AuditService auditService;

    @Transactional
    public FinancialRecordResponse create(CreateFinancialRecordRequest request, SecurityUser actor) {
        validateDateAndAmount(request.entryDate(), request.entryDate(), request.amount(), null);
        AppUser actingUser = userService.getActiveUserByUsername(actor.getUsername());
        BusinessUnit businessUnit = resolveBusinessUnit(request.businessUnitCode(), actor);
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.amount())
                .type(request.type())
                .category(request.category().trim())
                .entryDate(request.entryDate())
                .notes(request.notes())
                .businessUnit(businessUnit)
                .createdBy(actingUser)
                .updatedBy(actingUser)
                .deleted(false)
                .build();
        FinancialRecord saved = financialRecordRepository.save(record);
        auditService.log(AuditActionType.CREATE, "FINANCIAL_RECORD", saved.getId().toString(), actingUser,
                "Created record in unit " + businessUnit.getCode());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> findAll(
            RecordType type,
            List<String> categories,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            Pageable pageable,
            SecurityUser actor) {
        validateDateAndAmount(startDate, endDate, minAmount, maxAmount);
        BusinessUnit scopedUnit = scopedBusinessUnit(actor);
        return financialRecordRepository.findAll(
                        FinancialRecordSpecifications.withFilters(type, categories, startDate, endDate, minAmount, maxAmount, keyword, scopedUnit),
                        pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FinancialRecordResponse findById(Long id, SecurityUser actor) {
        return toResponse(getAccessibleRecord(id, actor));
    }

    @Transactional
    public FinancialRecordResponse update(Long id, UpdateFinancialRecordRequest request, SecurityUser actor) {
        validateDateAndAmount(request.entryDate(), request.entryDate(), request.amount(), null);
        AppUser actingUser = userService.getActiveUserByUsername(actor.getUsername());
        FinancialRecord record = getAccessibleRecord(id, actor);
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setEntryDate(request.entryDate());
        record.setNotes(request.notes());
        record.setBusinessUnit(resolveBusinessUnit(request.businessUnitCode(), actor));
        record.setUpdatedBy(actingUser);
        FinancialRecord saved = financialRecordRepository.save(record);
        auditService.log(AuditActionType.UPDATE, "FINANCIAL_RECORD", saved.getId().toString(), actingUser,
                "Updated record");
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id, SecurityUser actor) {
        AppUser actingUser = userService.getActiveUserByUsername(actor.getUsername());
        FinancialRecord record = getAccessibleRecord(id, actor);
        record.setDeleted(true);
        record.setDeletedAt(Instant.now());
        record.setDeletedBy(actingUser);
        record.setUpdatedBy(actingUser);
        financialRecordRepository.save(record);
        auditService.log(AuditActionType.DELETE, "FINANCIAL_RECORD", record.getId().toString(), actingUser,
                "Soft deleted record");
    }

    @Transactional(readOnly = true)
    public byte[] exportCsv(
            RecordType type,
            List<String> categories,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            SecurityUser actor) {
        List<FinancialRecord> records = loadForExport(type, categories, startDate, endDate, minAmount, maxAmount, keyword, actor);
        StringBuilder builder = new StringBuilder("id,amount,type,category,entryDate,businessUnit,createdBy,notes\n");
        for (FinancialRecord record : records) {
            builder.append(record.getId()).append(',')
                    .append(record.getAmount()).append(',')
                    .append(record.getType()).append(',')
                    .append(safeCsv(record.getCategory())).append(',')
                    .append(record.getEntryDate()).append(',')
                    .append(record.getBusinessUnit().getCode()).append(',')
                    .append(record.getCreatedBy().getUsername()).append(',')
                    .append(safeCsv(record.getNotes()))
                    .append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] exportExcel(
            RecordType type,
            List<String> categories,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            SecurityUser actor) {
        List<FinancialRecord> records = loadForExport(type, categories, startDate, endDate, minAmount, maxAmount, keyword, actor);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("records");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Category");
            header.createCell(4).setCellValue("Entry Date");
            header.createCell(5).setCellValue("Business Unit");
            header.createCell(6).setCellValue("Created By");
            header.createCell(7).setCellValue("Notes");

            int rowIndex = 1;
            for (FinancialRecord record : records) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(record.getId());
                row.createCell(1).setCellValue(record.getAmount().doubleValue());
                row.createCell(2).setCellValue(record.getType().name());
                row.createCell(3).setCellValue(record.getCategory());
                row.createCell(4).setCellValue(record.getEntryDate().toString());
                row.createCell(5).setCellValue(record.getBusinessUnit().getCode());
                row.createCell(6).setCellValue(record.getCreatedBy().getUsername());
                row.createCell(7).setCellValue(record.getNotes() == null ? "" : record.getNotes());
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unable to generate Excel export", ex);
        }
    }

    @Transactional(readOnly = true)
    public FinancialRecord getAccessibleRecord(Long id, SecurityUser actor) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id " + id));
        if (record.isDeleted()) {
            throw new ResourceNotFoundException("Financial record not found with id " + id);
        }
        BusinessUnit scopedUnit = scopedBusinessUnit(actor);
        if (scopedUnit != null && !record.getBusinessUnit().getId().equals(scopedUnit.getId())) {
            throw new ResourceNotFoundException("Financial record not found with id " + id);
        }
        return record;
    }

    public FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getEntryDate(),
                record.getNotes(),
                record.getBusinessUnit().getCode(),
                record.getCreatedBy().getUsername(),
                record.getUpdatedBy() == null ? null : record.getUpdatedBy().getUsername(),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                record.getDeletedAt());
    }

    private List<FinancialRecord> loadForExport(
            RecordType type,
            List<String> categories,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            SecurityUser actor) {
        validateDateAndAmount(startDate, endDate, minAmount, maxAmount);
        return financialRecordRepository.findAll(
                FinancialRecordSpecifications.withFilters(type, categories, startDate, endDate, minAmount, maxAmount, keyword, scopedBusinessUnit(actor)),
                Sort.by(Sort.Direction.DESC, "entryDate", "createdAt"));
    }

    private BusinessUnit resolveBusinessUnit(String requestedBusinessUnitCode, SecurityUser actor) {
        if (actor.hasPermission(PermissionType.CROSS_BUSINESS_UNIT_ACCESS) && requestedBusinessUnitCode != null && !requestedBusinessUnitCode.isBlank()) {
            return userService.getBusinessUnit(requestedBusinessUnitCode);
        }
        return userService.getActiveUserByUsername(actor.getUsername()).getBusinessUnit();
    }

    private BusinessUnit scopedBusinessUnit(SecurityUser actor) {
        if (actor.hasPermission(PermissionType.CROSS_BUSINESS_UNIT_ACCESS)) {
            return null;
        }
        return userService.getActiveUserByUsername(actor.getUsername()).getBusinessUnit();
    }

    private void validateDateAndAmount(LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("endDate must be greater than or equal to startDate");
        }
        if (minAmount != null && maxAmount != null && maxAmount.compareTo(minAmount) < 0) {
            throw new BadRequestException("maxAmount must be greater than or equal to minAmount");
        }
    }

    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

}
