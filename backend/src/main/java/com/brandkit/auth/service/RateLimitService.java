package com.brandkit.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Service
 * 
 * FRD-001 FR-12: Security Features
 * - Rate limiting: Maximum 5 login attempts per IP per minute
 * - CAPTCHA: After 3 failed attempts
 * - Account lockout: After 5 failed attempts for 15 minutes
 * 
 * Uses Redis for distributed rate limiting
 */
@Service
public class RateLimitService {
    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Value("${app.security.rate-limit-requests}")
    private int rateLimitRequests;

    @Value("${app.security.rate-limit-window}")
    private Duration rateLimitWindow;

    @Value("${app.security.captcha-threshold}")
    private int captchaThreshold;

    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;

    @Value("${app.security.lockout-duration}")
    private Duration lockoutDuration;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String FAILED_ATTEMPTS_PREFIX = "failed_attempts:";

    /**
     * Check if Redis is available
     */
    private boolean isRedisAvailable() {
        return redisTemplate != null;
    }

    /**
     * Check if request is rate limited
     * FRD-001 FR-12: Maximum 5 login attempts per IP per minute
     */
    public boolean isRateLimited(String ipAddress) {
        if (!isRedisAvailable()) {
            log.debug("Redis not available, skipping rate limit check");
            return false;
        }
        String key = RATE_LIMIT_PREFIX + ipAddress;
        String count = redisTemplate.opsForValue().get(key);
        
        if (count != null && Integer.parseInt(count) >= rateLimitRequests) {
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            return true;
        }
        
        return false;
    }

    /**
     * Increment rate limit counter
     */
    public void incrementRateLimit(String ipAddress) {
        if (!isRedisAvailable()) {
            return;
        }
        String key = RATE_LIMIT_PREFIX + ipAddress;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count != null && count == 1) {
            redisTemplate.expire(key, rateLimitWindow.toSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * Record failed login attempt
     * FRD-001 FR-6: Track failed attempts for CAPTCHA and lockout
     */
    public void recordFailedAttempt(String email, String ipAddress) {
        if (!isRedisAvailable()) {
            log.debug("Redis not available, skipping failed attempt recording");
            return;
        }
        String key = FAILED_ATTEMPTS_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count != null && count == 1) {
            redisTemplate.expire(key, lockoutDuration.toMinutes(), TimeUnit.MINUTES);
        }
        
        log.debug("Failed login attempt #{} for email: {}", count, email);
    }

    /**
     * Clear failed attempts on successful login
     */
    public void clearFailedAttempts(String email) {
        if (!isRedisAvailable()) {
            return;
        }
        String key = FAILED_ATTEMPTS_PREFIX + email;
        redisTemplate.delete(key);
    }

    /**
     * Get failed attempt count for email
     */
    public int getFailedAttemptCount(String email) {
        if (!isRedisAvailable()) {
            return 0;
        }
        String key = FAILED_ATTEMPTS_PREFIX + email;
        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count) : 0;
    }

    /**
     * Check if CAPTCHA is required
     * FRD-001 FR-6: CAPTCHA after 3 failed attempts
     */
    public boolean isCaptchaRequired(String email) {
        return getFailedAttemptCount(email) >= captchaThreshold;
    }

    /**
     * Check if account should be locked
     * FRD-001 FR-6: Lockout after 5 failed attempts
     */
    public boolean shouldLockAccount(String email) {
        return getFailedAttemptCount(email) >= maxLoginAttempts;
    }

    /**
     * Get lockout duration in minutes
     */
    public int getLockoutMinutes() {
        return (int) lockoutDuration.toMinutes();
    }

    public StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
    public LoginAttemptRepository getLoginAttemptRepository() {
        return this.loginAttemptRepository;
    }
    public int getRateLimitRequests() {
        return this.rateLimitRequests;
    }
    public Duration getRateLimitWindow() {
        return this.rateLimitWindow;
    }
    public int getCaptchaThreshold() {
        return this.captchaThreshold;
    }
    public int getMaxLoginAttempts() {
        return this.maxLoginAttempts;
    }
    public Duration getLockoutDuration() {
        return this.lockoutDuration;
    }
}
