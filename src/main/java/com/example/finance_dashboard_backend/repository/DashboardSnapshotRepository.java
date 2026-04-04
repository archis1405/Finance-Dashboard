package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.BusinessUnit;
import com.example.finance_dashboard_backend.model.DashboardSnapshot;
import com.example.finance_dashboard_backend.model.SnapshotPeriodType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DashboardSnapshotRepository extends JpaRepository<DashboardSnapshot,Long> {
    Optional<DashboardSnapshot> findByPeriodTypeAndPeriodStartAndPeriodEndAndBusinessUnit(
            SnapshotPeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            BusinessUnit businessUnit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ds from DashboardSnapshot ds
            where ds.periodType = :periodType
              and ds.periodStart = :periodStart 
              and ds.periodEnd = :periodEnd and ds.businessUnit = :businessUnit
            """)
    Optional<DashboardSnapshot> findByScopeForUpdate(
            SnapshotPeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            BusinessUnit businessUnit);

    List<DashboardSnapshot> findByPeriodTypeAndBusinessUnitOrderByPeriodStartDesc(SnapshotPeriodType periodType, BusinessUnit businessUnit);
}
