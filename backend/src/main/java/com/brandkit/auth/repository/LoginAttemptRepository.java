package com.brandkit.auth.repository;

import com.brandkit.auth.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Login Attempt Repository
 * 
 * FRD-001 FR-12: Security Features - Rate Limiting
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    /**
     * Count failed attempts by email in time window
     * FRD-001 FR-6: CAPTCHA after 3 failures, lockout after 5
     */
    @Query("SELECT COUNT(l) FROM LoginAttempt l " +
           "WHERE l.email = :email AND l.success = false AND l.attemptedAt > :since")
    long countFailedAttemptsByEmail(@Param("email") String email, @Param("since") ZonedDateTime since);

    /**
     * Count failed attempts by IP in time window
     * FRD-001 FR-12: Maximum 5 login attempts per IP per minute
     */
    @Query("SELECT COUNT(l) FROM LoginAttempt l " +
           "WHERE l.ipAddress = :ip AND l.success = false AND l.attemptedAt > :since")
    long countFailedAttemptsByIp(@Param("ip") String ip, @Param("since") ZonedDateTime since);

    /**
     * Count total attempts by IP in time window (for rate limiting)
     */
    @Query("SELECT COUNT(l) FROM LoginAttempt l " +
           "WHERE l.ipAddress = :ip AND l.attemptedAt > :since")
    long countAttemptsByIp(@Param("ip") String ip, @Param("since") ZonedDateTime since);

    /**
     * Delete old attempts (cleanup)
     */
    @Modifying
    @Query("DELETE FROM LoginAttempt l WHERE l.attemptedAt < :before")
    void deleteOldAttempts(@Param("before") ZonedDateTime before);
}
