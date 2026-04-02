package com.example.finance_dashboard_backend.dto.user;

import jakarta.validation.constraints.*;

public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    public String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    public String email;
    public boolean active;
}
