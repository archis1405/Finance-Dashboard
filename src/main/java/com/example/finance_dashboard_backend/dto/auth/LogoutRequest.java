package com.example.finance_dashboard_backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank String refreshToken) {
}
