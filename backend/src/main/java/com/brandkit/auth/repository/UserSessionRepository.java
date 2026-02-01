package com.brandkit.auth.repository;

import com.brandkit.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Session Repository
 * 
 * FRD-001 FR-9: Session Management
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    /**
     * Find active session by refresh token hash
     */
    Optional<UserSession> findByRefreshTokenHashAndIsRevokedFalse(String refreshTokenHash);

    /**
     * Find all active sessions for a user
     * FRD-001 BR-6: Session Concurrency - Multiple sessions allowed
     */
    List<UserSession> findByUserIdAndIsRevokedFalse(UUID userId);

    /**
     * Revoke all sessions for a user
     * FRD-001 FR-7: Invalidate all existing sessions after password reset
     * FRD-001 BR-7: Account Deactivation Effect
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isRevoked = true, s.revokedAt = :now " +
           "WHERE s.user.id = :userId AND s.isRevoked = false")
    void revokeAllUserSessions(@Param("userId") UUID userId, @Param("now") ZonedDateTime now);

    /**
     * Revoke specific session (logout)
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isRevoked = true, s.revokedAt = :now " +
           "WHERE s.id = :sessionId")
    void revokeSession(@Param("sessionId") UUID sessionId, @Param("now") ZonedDateTime now);

    /**
     * Delete expired sessions (cleanup)
     */
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now OR s.isRevoked = true")
    void deleteExpiredSessions(@Param("now") ZonedDateTime now);

    /**
     * Count active sessions for user
     */
    long countByUserIdAndIsRevokedFalse(UUID userId);
}
