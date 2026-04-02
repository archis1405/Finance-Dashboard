package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.model.FinancialRecord;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialRecordService {
    private final FinancialRecordRepository financialRecordRepository;

    public FinancialRecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    public FinancialRecord createRecord(FinancialRecord record) {
        return financialRecordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return financialRecordRepository.findAll();
    }

}
