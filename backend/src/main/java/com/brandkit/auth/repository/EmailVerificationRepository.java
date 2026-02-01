package com.brandkit.auth.repository;

import com.brandkit.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Email Verification Repository
 * 
 * FRD-001 FR-2: Email Verification
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    /**
     * Find valid verification token
     */
    Optional<EmailVerification> findByTokenHashAndIsUsedFalse(String tokenHash);

    /**
     * Find latest token for user (for resend)
     */
    Optional<EmailVerification> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Invalidate all previous verification tokens for user
     * When resending verification, old tokens should be invalidated
     */
    @Modifying
    @Query("UPDATE EmailVerification e SET e.isUsed = true, e.verifiedAt = :now " +
           "WHERE e.user.id = :userId AND e.isUsed = false")
    void invalidateUserTokens(@Param("userId") UUID userId, @Param("now") ZonedDateTime now);

    /**
     * Delete expired tokens (cleanup)
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") ZonedDateTime now);

    /**
     * Check if user has pending verification
     */
    boolean existsByUserIdAndIsUsedFalseAndExpiresAtAfter(UUID userId, ZonedDateTime now);
}
