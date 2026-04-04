package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.dashboard.DashboardSnapshotResponse;
import com.example.finance_dashboard_backend.dto.dashboard.DashboardSummaryResponse;
import com.example.finance_dashboard_backend.dto.dashboard.TrendPointResponse;
import com.example.finance_dashboard_backend.dto.record.FinancialRecordResponse;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.service.DashboardService;
import com.example.finance_dashboard_backend.service.DashboardSnapshotService;
import com.example.finance_dashboard_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final DashboardSnapshotService dashboardSnapshotService;
    private final UserService userService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    public DashboardSummaryResponse summary(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal SecurityUser principal) {
        return dashboardService.getSummary(startDate, endDate, principal);
    }

    @GetMapping("/trends/monthly")
    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    public List<TrendPointResponse> monthlyTrends(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal SecurityUser principal) {
        return dashboardService.getMonthlyTrends(startDate, endDate, principal);
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    public List<FinancialRecordResponse> recentActivity(@AuthenticationPrincipal SecurityUser principal) {
        return dashboardService.getRecentActivity(principal);
    }

    @GetMapping("/snapshots/monthly")
    @PreAuthorize("hasAuthority('SNAPSHOT_READ')")
    public List<DashboardSnapshotResponse> monthlySnapshots(@AuthenticationPrincipal SecurityUser principal) {
        return dashboardSnapshotService.findMonthlySnapshots(
                userService.getActiveUserByUsername(principal.getUsername()).getBusinessUnit());
    }
}
