package com.example.finance_dashboard_backend.dto.user;

import com.example.finance_dashboard_backend.model.RoleType;
import com.example.finance_dashboard_backend.model.UserStatus;

import java.time.Instant;
import java.util.Set;

public record UserResponse(Long id,
                           String username,
                           String fullName,
                           String email,
                           RoleType role,
                           Set<String> permissions,
                           UserStatus status,
                           String businessUnitCode,
                           Instant createdAt,
                           Instant updatedAt) {
}
