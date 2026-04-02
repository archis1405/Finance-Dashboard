package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.dashboard.CategoryTotalResponse;
import com.example.finance_dashboard_backend.dto.dashboard.DashboardSummaryResponse;
import com.example.finance_dashboard_backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardSummaryResponse getSummary() {
        Double income = financialRecordRepository.getTotalIncome();
        Double expense = financialRecordRepository.getTotalExpense();

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.totalIncome = income;
        response.totalExpense = expense;
        response.netBalance = income - expense;

        return response;
    }

    public List<CategoryTotalResponse> getCategoryTotals() {
        return financialRecordRepository.getCategoryTotals()
                .stream()
                .map(obj -> {
                    CategoryTotalResponse res = new CategoryTotalResponse();
                    res.category = (String) obj[0];
                    res.total = (Double) obj[1];
                    return res;
                })
                .collect(Collectors.toList());
    }
}
