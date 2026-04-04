package com.example.finance_dashboard_backend.dto.user;

import com.example.finance_dashboard_backend.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(@NotNull UserStatus status) {
}
