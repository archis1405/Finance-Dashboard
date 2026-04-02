package com.example.finance_dashboard_backend.dto.record;

import java.time.LocalDate;

public class CreateFinancialRecordRequest {
    public Double amount;
    public String type;
    public String category;
    public LocalDate date;
}
