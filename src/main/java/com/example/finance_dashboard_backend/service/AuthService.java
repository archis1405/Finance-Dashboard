package com.example.finance_dashboard_backend.service;

import com.example.finance_dashboard_backend.dto.auth.AuthTokenResponse;
import com.example.finance_dashboard_backend.dto.auth.LoginRequest;
import com.example.finance_dashboard_backend.dto.auth.LogoutRequest;
import com.example.finance_dashboard_backend.dto.auth.RefreshTokenRequest;
import com.example.finance_dashboard_backend.exception.BadRequestException;
import com.example.finance_dashboard_backend.exception.ResourceNotFoundException;
import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.model.AuditActionType;
import com.example.finance_dashboard_backend.model.RefreshToken;
import com.example.finance_dashboard_backend.model.TokenType;
import com.example.finance_dashboard_backend.repository.RefreshTokenRepository;
import com.example.finance_dashboard_backend.security.JwtService;
import com.example.finance_dashboard_backend.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
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


    @Value("${app.jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Transactional
    public AuthTokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        AppUser user = userService.getActiveUserByUsername(securityUser.getUsername());
        String accessToken = jwtService.generateAccessToken(securityUser);
        String refreshTokenValue = jwtService.generateRefreshToken(securityUser);
        tokenLifecycleLock.lock();
        try {
            persistRefreshToken(user, refreshTokenValue);
        } finally {
            tokenLifecycleLock.unlock();
        }
        auditService.log(AuditActionType.LOGIN, "AUTH", user.getId().toString(), user, "User logged in");
        return new AuthTokenResponse(accessToken, refreshTokenValue, "Bearer", jwtService.getAccessTokenSeconds(), userService.getAuthenticatedUser(securityUser));
    }

    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        String refreshTokenValue = request.refreshToken();
        if (!jwtService.isRefreshToken(refreshTokenValue)) {
            throw new BadRequestException("Invalid refresh token type");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashForUpdate(hash(refreshTokenValue)).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token is expired or revoked");
        }

        AppUser user = userService.getUser(refreshToken.getUser().getId());
        SecurityUser securityUser = new SecurityUser(user);

        refreshToken.setRevoked(true);

        String newRefreshToken = jwtService.generateRefreshToken(securityUser);

        tokenLifecycleLock.lock();

        try{
            persistRefreshToken(user, newRefreshToken);
        }
        finally {
            tokenLifecycleLock.unlock();
        }
        auditService.log(AuditActionType.REFRESH, "AUTH", user.getId().toString(), user, "Refreshed access token");

        return new AuthTokenResponse(jwtService.generateAccessToken(securityUser), newRefreshToken, "Bearer", jwtService.getAccessTokenSeconds(), userService.getAuthenticatedUser(securityUser));
    }

    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashForUpdate(hash(request.refreshToken())).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);

        auditService.log(AuditActionType.LOGOUT, "AUTH", refreshToken.getUser().getId().toString(), refreshToken.getUser(), "Logged out");
    }

    @Transactional
    @Scheduled(cron = "0 15 3 * * *")
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now().minus(1, ChronoUnit.DAYS));
    }

    private void persistRefreshToken(AppUser user, String tokenValue) {
        refreshTokenRepository.revokeActiveTokensForUser(user);

        refreshTokenRepository.save(RefreshToken.builder().tokenHash(hash(tokenValue)).user(user).tokenType(TokenType.REFRESH).expiresAt(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS)).revoked(false).build());
    }

    private String hash(String rawValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawValue.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash token", ex);
        }
    }
}
