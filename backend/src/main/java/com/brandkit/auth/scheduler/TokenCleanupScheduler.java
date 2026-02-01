package com.brandkit.auth.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Token Cleanup Scheduler
 * 
 * FRD-001: Automatic cleanup of expired tokens and sessions
 * Runs periodically to maintain database hygiene
 */
@Component
public class TokenCleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);

    @Autowired
    private UserSessionRepository sessionRepository;
    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Cleanup expired tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Running token cleanup task");
        ZonedDateTime now = ZonedDateTime.now();

        // Delete expired sessions
        sessionRepository.deleteExpiredSessions(now);
        
        // Delete expired password reset tokens
        passwordResetRepository.deleteExpiredTokens(now);
        
        // Delete expired email verification tokens
        emailVerificationRepository.deleteExpiredTokens(now);
        
        // Delete old login attempts (older than 24 hours)
        loginAttemptRepository.deleteOldAttempts(now.minusHours(24));

        log.info("Token cleanup completed");
    }

    /**
     * Unlock expired account lockouts every 5 minutes
     * FRD-001 BR-8: Account lockout expires after 15 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    @Transactional
    public void unlockExpiredAccounts() {
        userRepository.unlockExpiredAccounts(ZonedDateTime.now());
    }
}
