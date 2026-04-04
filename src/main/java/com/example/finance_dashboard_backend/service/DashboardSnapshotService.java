package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.dashboard.DashboardSnapshotResponse;
import com.example.finance_dashboard_backend.model.*;
import com.example.finance_dashboard_backend.repository.BusinessUnitRepository;
import com.example.finance_dashboard_backend.repository.DashboardSnapshotRepository;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import com.example.finance_dashboard_backend.specification.FinancialRecordSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class DashboardSnapshotService {
    private final ReentrantLock snapshotGenerationLock = new ReentrantLock();

    private final DashboardSnapshotRepository snapshotRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final AuditService auditService;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void generateDailySnapshots() {
        LocalDate day = LocalDate.now().minusDays(1);
        businessUnitRepository.findAll().forEach(unit -> createOrUpdateSnapshot(SnapshotPeriodType.DAILY, day, day, unit, null));
    }

    @Transactional
    @Scheduled(cron = "0 0 2 1 * *")
    public void generateMonthlySnapshots() {
        YearMonth month = YearMonth.now().minusMonths(1);
        businessUnitRepository.findAll().forEach(unit -> createOrUpdateSnapshot(
                SnapshotPeriodType.MONTHLY,
                month.atDay(1),
                month.atEndOfMonth(),
                unit,
                null));
    }

    @Transactional
    public DashboardSnapshot createOrUpdateSnapshot(
            SnapshotPeriodType periodType,
            LocalDate startDate,
            LocalDate endDate,
            BusinessUnit businessUnit,
            AppUser actor) {
        snapshotGenerationLock.lock();
        try {
            List<FinancialRecord> records = financialRecordRepository.findAll(
                    FinancialRecordSpecifications.withFilters(null, null, startDate, endDate, null, null, null, businessUnit));
            BigDecimal income = sumByType(records, RecordType.INCOME);
            BigDecimal expense = sumByType(records, RecordType.EXPENSE);
            DashboardSnapshot snapshot = snapshotRepository
                    .findByScopeForUpdate(periodType, startDate, endDate, businessUnit)
                    .orElse(DashboardSnapshot.builder()
                            .periodType(periodType)
                            .periodStart(startDate)
                            .periodEnd(endDate)
                            .businessUnit(businessUnit)
                            .build());
            snapshot.setTotalIncome(income);
            snapshot.setTotalExpense(expense);
            snapshot.setNetBalance(income.subtract(expense));
            snapshot.setTotalRecords(records.size());
            DashboardSnapshot saved;
            try {
                saved = snapshotRepository.saveAndFlush(snapshot);
            } catch (DataIntegrityViolationException ex) {
                saved = snapshotRepository.findByScopeForUpdate(periodType, startDate, endDate, businessUnit)
                        .orElseThrow(() -> ex);
                saved.setTotalIncome(income);
                saved.setTotalExpense(expense);
                saved.setNetBalance(income.subtract(expense));
                saved.setTotalRecords(records.size());
                saved = snapshotRepository.saveAndFlush(saved);
            }
            auditService.log(AuditActionType.SNAPSHOT_GENERATED, "DASHBOARD_SNAPSHOT", saved.getId().toString(), actor,
                    "Generated " + periodType + " snapshot for " + businessUnit.getCode());
            return saved;
        } finally {
            snapshotGenerationLock.unlock();
        }
    }

    @Transactional(readOnly = true)
    public List<DashboardSnapshotResponse> findMonthlySnapshots(BusinessUnit businessUnit) {
        return snapshotRepository.findByPeriodTypeAndBusinessUnitOrderByPeriodStartDesc(SnapshotPeriodType.MONTHLY, businessUnit)
                .stream()
                .map(snapshot -> new DashboardSnapshotResponse(
                        snapshot.getId(),
                        snapshot.getPeriodType(),
                        snapshot.getPeriodStart(),
                        snapshot.getPeriodEnd(),
                        snapshot.getBusinessUnit() == null ? null : snapshot.getBusinessUnit().getCode(),
                        snapshot.getTotalIncome(),
                        snapshot.getTotalExpense(),
                        snapshot.getNetBalance(),
                        snapshot.getTotalRecords(),
                        snapshot.getCreatedAt()))
                .toList();
    }

    private BigDecimal sumByType(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
