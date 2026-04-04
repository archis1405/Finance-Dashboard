package com.example.finance_dashboard_backend.dto.record;

import com.example.finance_dashboard_backend.model.RecordType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FinancialRecordResponse(Long id,
                                      BigDecimal amount,
                                      RecordType type,
                                      String category,
                                      LocalDate entryDate,
                                      String notes,
                                      String businessUnitCode,
                                      String createdBy,
                                      String updatedBy,
                                      Instant createdAt,
                                      Instant updatedAt,
                                      Instant deletedAt) {
}
