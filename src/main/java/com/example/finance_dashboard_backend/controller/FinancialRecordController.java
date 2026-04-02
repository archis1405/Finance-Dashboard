package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.model.FinancialRecord;
import com.example.finance_dashboard_backend.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    // CREATE RECORD
    @PostMapping
    public FinancialRecord createRecord(@RequestBody FinancialRecord record) {
        return recordService.createRecord(record);
    }

    // GET ALL RECORDS
    @GetMapping
    public List<FinancialRecord> getAllRecords() {
        return recordService.getAllRecords();
    }

}
