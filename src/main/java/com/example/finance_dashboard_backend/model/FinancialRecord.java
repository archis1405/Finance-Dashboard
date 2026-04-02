package com.example.finance_dashboard_backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "financial_records")
public class FinancialRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String type;
    private String category;
    private LocalDate date;
}
