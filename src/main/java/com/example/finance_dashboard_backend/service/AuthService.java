package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ReentrantLock tokenLifecycleLock = new ReentrantLock();

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final AuditService auditService;

}
