package com.example.finance_dashboard_backend.dto.user;

import com.example.finance_dashboard_backend.model.RoleType;
import com.example.finance_dashboard_backend.model.UserStatus;
import jakarta.validation.constraints.*;

public record CreateUserRequest(@NotBlank @Size(max = 50) String username,
                                @NotBlank @Size(max = 100) String fullName,
                                @NotBlank @Email @Size(max = 100) String email,
                                @NotBlank
                                @Size(min = 8, max = 100)
                                @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "password must contain at least one letter and one digit")
                                String password,
                                @NotNull RoleType role,
                                @NotNull UserStatus status,
                                @NotBlank String businessUnitCode) {
}
