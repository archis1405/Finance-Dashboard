package com.example.finance_dashboard_backend.dto.auth;

import com.example.finance_dashboard_backend.model.RoleType;
import com.example.finance_dashboard_backend.model.UserStatus;

import java.util.Set;

public record AuthUserResponse(Long id,
                               String username,
                               String fullName,
                               String email,
                               RoleType role,
                               Set<String> permissions,
                               UserStatus status,
                               String businessUnitCode) {
}
