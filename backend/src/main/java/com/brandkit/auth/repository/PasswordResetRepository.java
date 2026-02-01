package com.brandkit.auth.repository;

import com.brandkit.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Password Reset Repository
 * 
 * FRD-001 FR-7: Password Reset Workflow
 */
@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    /**
     * Find valid reset token
     */
    Optional<PasswordReset> findByTokenHashAndIsUsedFalse(String tokenHash);

    /**
     * Invalidate all previous reset tokens for user
     * When generating a new token, old ones should be invalidated
     */
    @Modifying
    @Query("UPDATE PasswordReset p SET p.isUsed = true, p.usedAt = :now " +
           "WHERE p.user.id = :userId AND p.isUsed = false")
    void invalidateUserTokens(@Param("userId") UUID userId, @Param("now") ZonedDateTime now);

    /**
     * Delete expired tokens (cleanup)
     */
    @Modifying
    @Query("DELETE FROM PasswordReset p WHERE p.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") ZonedDateTime now);
}
