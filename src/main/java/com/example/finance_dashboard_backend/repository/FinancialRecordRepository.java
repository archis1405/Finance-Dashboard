package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord,Long> {

    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = 'income'")
    Double getTotalIncome();

    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = 'expense'")
    Double getTotalExpense();

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r GROUP BY r.category")
    List<Object[]> getCategoryTotals();

}
