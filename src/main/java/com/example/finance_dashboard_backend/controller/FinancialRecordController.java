package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.record.CreateFinancialRecordRequest;
import com.example.finance_dashboard_backend.dto.record.FinancialRecordResponse;
import com.example.finance_dashboard_backend.dto.record.UpdateFinancialRecordRequest;
import com.example.finance_dashboard_backend.model.RecordType;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    private final FinancialRecordService financialRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('RECORD_WRITE')")
    public FinancialRecordResponse create(
            @Valid @RequestBody CreateFinancialRecordRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return financialRecordService.create(request, principal);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RECORD_READ')")
    public Page<FinancialRecordResponse> list(
            RecordType type,
            @RequestParam(required = false) List<String> categories,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            Pageable pageable,
            @AuthenticationPrincipal SecurityUser principal) {
        return financialRecordService.findAll(type, categories, startDate, endDate, minAmount, maxAmount, keyword, pageable, principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RECORD_READ')")
    public FinancialRecordResponse findById(@PathVariable Long id, @AuthenticationPrincipal SecurityUser principal) {
        return financialRecordService.findById(id, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RECORD_WRITE')")
    public FinancialRecordResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFinancialRecordRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return financialRecordService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('RECORD_WRITE')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal SecurityUser principal) {
        financialRecordService.delete(id, principal);
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    @PreAuthorize("hasAuthority('EXPORT_RECORDS')")
    public ResponseEntity<ByteArrayResource> exportCsv(
            RecordType type,
            @RequestParam(required = false) List<String> categories,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            @AuthenticationPrincipal SecurityUser principal) {
        byte[] data = financialRecordService.exportCsv(type, categories, startDate, endDate, minAmount, maxAmount, keyword, principal);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("financial-records.csv").build().toString())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(data));
    }

    @GetMapping(value = "/export.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('EXPORT_RECORDS')")
    public ResponseEntity<ByteArrayResource> exportExcel(
            RecordType type,
            @RequestParam(required = false) List<String> categories,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword,
            @AuthenticationPrincipal SecurityUser principal) {
        byte[] data = financialRecordService.exportExcel(type, categories, startDate, endDate, minAmount, maxAmount, keyword, principal);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("financial-records.xlsx").build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(data));
    }

}
