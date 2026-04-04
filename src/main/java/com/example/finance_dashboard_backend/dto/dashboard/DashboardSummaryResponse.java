package com.example.finance_dashboard_backend.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryResponse(String businessUnitCode,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       BigDecimal totalIncome,
                                       BigDecimal totalExpense,
                                       BigDecimal netBalance,
                                       long totalRecords,
                                       List<CategoryTotalResponse> categoryTotals,
                                       List<FinancialRecordResponse> recentActivity) {
}
