package com.example.finance_dashboard_backend.dto.dashboard;

import com.example.finance_dashboard_backend.model.SnapshotPeriodType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DashboardSnapshotResponse(Long id,
                                        SnapshotPeriodType periodType,
                                        LocalDate periodStart,
                                        LocalDate periodEnd,
                                        String businessUnitCode,
                                        BigDecimal totalIncome,
                                        BigDecimal totalExpense,
                                        BigDecimal netBalance,
                                        long totalRecords,
                                        Instant createdAt) {
}
