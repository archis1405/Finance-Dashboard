package com.example.finance_dashboard_backend.repository;

import com.example.finance_dashboard_backend.model.AppUser;
import com.example.finance_dashboard_backend.model.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select rt from RefreshToken rt where rt.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashForUpdate(String tokenHash);

    List<RefreshToken> findByUserAndRevokedFalse(AppUser user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken rt set rt.revoked = true where rt.user = :user and rt.revoked = false")
    int revokeActiveTokensForUser(AppUser user);

    void deleteByExpiresAtBefore(Instant threshold);
}
