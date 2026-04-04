package com.example.finance_dashboard_backend.dto.dashboard;

import java.math.BigDecimal;

public record CategoryTotalResponse(String category, BigDecimal total) {
}
