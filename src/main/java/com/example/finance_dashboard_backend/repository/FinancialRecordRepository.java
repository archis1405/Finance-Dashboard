package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord,Long> {
}
