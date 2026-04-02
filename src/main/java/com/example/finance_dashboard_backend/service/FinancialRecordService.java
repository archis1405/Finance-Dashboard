package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.record.CreateFinancialRecordRequest;
import com.example.finance_dashboard_backend.dto.record.FinancialRecordResponse;
import com.example.finance_dashboard_backend.model.FinancialRecord;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordService {
    private final FinancialRecordRepository financialRecordRepository;

    public FinancialRecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.amount);
        record.setType(request.type);
        record.setCategory(request.category);
        record.setDate(request.date);

        FinancialRecord saved = financialRecordRepository.save(record);

        return mapToResponse(saved);
    }

    public List<FinancialRecordResponse> getAllRecords() {
        return financialRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        FinancialRecordResponse response = new FinancialRecordResponse();
        response.id = record.getId();
        response.amount = record.getAmount();
        response.type = record.getType();
        response.category = record.getCategory();
        return response;
    }

}
