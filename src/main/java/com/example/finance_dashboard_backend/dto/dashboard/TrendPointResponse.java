package com.example.finance_dashboard_backend.dto.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;

public record TrendPointResponse(YearMonth period, BigDecimal income, BigDecimal expense, BigDecimal net) {
}
