package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.dashboard.CategoryTotalResponse;
import com.example.finance_dashboard_backend.dto.dashboard.DashboardSummaryResponse;
import com.example.finance_dashboard_backend.dto.dashboard.TrendPointResponse;
import com.example.finance_dashboard_backend.dto.record.FinancialRecordResponse;
import com.example.finance_dashboard_backend.exception.BadRequestException;
import com.example.finance_dashboard_backend.model.BusinessUnit;
import com.example.finance_dashboard_backend.model.FinancialRecord;
import com.example.finance_dashboard_backend.model.PermissionType;
import com.example.finance_dashboard_backend.model.RecordType;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.specification.FinancialRecordSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialRecordService financialRecordService;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard-summary", key = "T(java.lang.String).valueOf(#actor.username).concat(':').concat(T(java.lang.String).valueOf(#startDate)).concat(':').concat(T(java.lang.String).valueOf(#endDate))")
    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate, SecurityUser actor) {
        List<FinancialRecord> records = loadFilteredRecords(startDate, endDate, actor);

        BigDecimal totalIncome = sumByType(records, RecordType.INCOME);
        BigDecimal totalExpense = sumByType(records, RecordType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categoryMap = new LinkedHashMap<>();
        for (FinancialRecord record : records) {
            categoryMap.merge(record.getCategory(), record.getAmount(), BigDecimal::add);
        }

        List<CategoryTotalResponse> categoryTotals = categoryMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getValue()))
                .toList();

        BusinessUnit scopedUnit = scopedBusinessUnit(actor);
        List<FinancialRecordResponse> recentActivity = (scopedUnit == null
                ? financialRecordRepository.findTop5ByDeletedFalseOrderByEntryDateDescCreatedAtDesc()
                : financialRecordRepository.findTop5ByDeletedFalseAndBusinessUnitOrderByEntryDateDescCreatedAtDesc(scopedUnit))
                .stream()
                .map(financialRecordService::toResponse)
                .toList();

        return new DashboardSummaryResponse(
                scopedUnit == null ? "ALL" : scopedUnit.getCode(),
                startDate,
                endDate,
                totalIncome,
                totalExpense,
                netBalance,
                records.size(),
                categoryTotals,
                recentActivity);
    }

    @Transactional(readOnly = true)
    public List<TrendPointResponse> getMonthlyTrends(LocalDate startDate, LocalDate endDate, SecurityUser actor) {
        List<FinancialRecord> records = loadFilteredRecords(startDate, endDate, actor);

        Map<YearMonth, BigDecimal> incomeByMonth = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> expenseByMonth = new LinkedHashMap<>();

        for (FinancialRecord record : records) {
            YearMonth month = YearMonth.from(record.getEntryDate());
            if (record.getType() == RecordType.INCOME) {
                incomeByMonth.merge(month, record.getAmount(), BigDecimal::add);
            } else {
                expenseByMonth.merge(month, record.getAmount(), BigDecimal::add);
            }
        }

        List<YearMonth> allMonths = new ArrayList<>();
        allMonths.addAll(incomeByMonth.keySet());
        expenseByMonth.keySet().stream()
                .filter(month -> !allMonths.contains(month))
                .forEach(allMonths::add);

        return allMonths.stream()
                .sorted(Comparator.naturalOrder())
                .map(month -> {
                    BigDecimal income = incomeByMonth.getOrDefault(month, BigDecimal.ZERO);
                    BigDecimal expense = expenseByMonth.getOrDefault(month, BigDecimal.ZERO);
                    return new TrendPointResponse(month, income, expense, income.subtract(expense));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecentActivity(SecurityUser actor) {
        BusinessUnit scopedUnit = scopedBusinessUnit(actor);
        return (scopedUnit == null
                ? financialRecordRepository.findTop5ByDeletedFalseOrderByEntryDateDescCreatedAtDesc()
                : financialRecordRepository.findTop5ByDeletedFalseAndBusinessUnitOrderByEntryDateDescCreatedAtDesc(scopedUnit))
                .stream()
                .map(financialRecordService::toResponse)
                .toList();
    }

    private List<FinancialRecord> loadFilteredRecords(LocalDate startDate, LocalDate endDate, SecurityUser actor) {
        validateDateRange(startDate, endDate);
        return financialRecordRepository.findAll(
                FinancialRecordSpecifications.withFilters(null, null, startDate, endDate, null, null, null, scopedBusinessUnit(actor)),
                Sort.by(Sort.Direction.DESC, "entryDate", "createdAt"));
    }

    private BusinessUnit scopedBusinessUnit(SecurityUser actor) {
        if (actor.hasPermission(PermissionType.CROSS_BUSINESS_UNIT_ACCESS)) {
            return null;
        }
        return userService.getActiveUserByUsername(actor.getUsername()).getBusinessUnit();
    }

    private BigDecimal sumByType(List<FinancialRecord> records, RecordType recordType) {
        return records.stream()
                .filter(record -> record.getType() == recordType)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("endDate must be greater than or equal to startDate");
        }
    }
}
