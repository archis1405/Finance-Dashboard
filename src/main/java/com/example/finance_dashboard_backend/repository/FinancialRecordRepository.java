package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.BusinessUnit;
import com.example.finance_dashboard_backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord,Long>, JpaSpecificationExecutor<FinancialRecord> {

    List<FinancialRecord> findTop5ByDeletedFalseOrderByEntryDateDescCreatedAtDesc();

    List<FinancialRecord> findTop5ByDeletedFalseAndBusinessUnitOrderByEntryDateDescCreatedAtDesc(BusinessUnit businessUnit);

}
