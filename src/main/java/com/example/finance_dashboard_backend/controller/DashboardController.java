package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.dashboard.CategoryTotalResponse;
import com.example.finance_dashboard_backend.dto.dashboard.DashboardSummaryResponse;
import com.example.finance_dashboard_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/categories")
    public List<CategoryTotalResponse> getCategoryTotals() {
        return dashboardService.getCategoryTotals();
    }
}
