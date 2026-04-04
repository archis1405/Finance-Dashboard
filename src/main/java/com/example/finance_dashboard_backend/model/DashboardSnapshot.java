package com.example.finance_dashboard_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dashboard_snapshots", uniqueConstraints = @UniqueConstraint(name = "uk_dashboard_snapshot_scope", columnNames = {"period_type", "period_start", "period_end", "business_unit_id"}))
public class DashboardSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SnapshotPeriodType periodType;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_unit_id")
    private BusinessUnit businessUnit;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalIncome;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalExpense;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal netBalance;

    @Column(nullable = false)
    private long totalRecords;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
