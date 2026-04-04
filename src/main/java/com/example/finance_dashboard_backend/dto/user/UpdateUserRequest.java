package com.example.finance_dashboard_backend.dto.user;

import com.example.finance_dashboard_backend.model.RoleType;
import com.example.finance_dashboard_backend.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(@NotBlank @Size(max = 100) String fullName,
                                @NotBlank @Email @Size(max = 100) String email,
                                @NotNull RoleType role,
                                @NotNull UserStatus status,
                                @NotBlank String businessUnitCode) {
}
