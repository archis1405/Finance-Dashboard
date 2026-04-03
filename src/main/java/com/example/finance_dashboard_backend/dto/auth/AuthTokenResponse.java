package com.example.finance_dashboard_backend.dto.auth;

public record AuthTokenResponse(String accessToken,
                                String refreshToken,
                                String tokenType,
                                long expiresInSeconds,
                                AuthUserResponse user) {
}
