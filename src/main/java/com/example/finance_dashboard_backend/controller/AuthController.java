package com.example.finance_dashboard_backend.controller;

import com.example.finance_dashboard_backend.dto.auth.*;
import com.example.finance_dashboard_backend.security.SecurityUser;
import com.example.finance_dashboard_backend.service.AuthService;
import com.example.finance_dashboard_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal SecurityUser principal) {
        return userService.getAuthenticatedUser(principal);
    }
}
