package com.brandkit.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Login Attempt Entity - Tracks login attempts for security
 * 
 * FRD-001 FR-12: Security Features
 * - Rate limiting: Max 5 login attempts per IP per minute
 * - CAPTCHA after 3 failed attempts
 * - Account lockout after 5 failures
 */
@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "ip_address", nullable = false, columnDefinition = "inet")
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "attempted_at", nullable = false, updatable = false)
    private ZonedDateTime attemptedAt;

    @Column(nullable = false)
    private Boolean success = false;

    public UUID getId() {
        return this.id;
    }
    public String getEmail() {
        return this.email;
    }
    public String getIpAddress() {
        return this.ipAddress;
    }
    public ZonedDateTime getAttemptedAt() {
        return this.attemptedAt;
    }
    public Boolean getSuccess() {
        return this.success;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setAttemptedAt(ZonedDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public LoginAttempt() {
    }
    public LoginAttempt(UUID id, String email, String ipAddress, ZonedDateTime attemptedAt, Boolean success) {
        this.id = id;
        this.email = email;
        this.ipAddress = ipAddress;
        this.attemptedAt = attemptedAt;
        this.success = success;
    }
    public static LoginAttemptBuilder builder() {
        return new LoginAttemptBuilder();
    }

    public static class LoginAttemptBuilder {
        private UUID id;
        private String email;
        private String ipAddress;
        private ZonedDateTime attemptedAt;
        private Boolean success = false;

        LoginAttemptBuilder() {
        }

        public LoginAttemptBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public LoginAttemptBuilder email(String email) {
            this.email = email;
            return this;
        }

        public LoginAttemptBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public LoginAttemptBuilder attemptedAt(ZonedDateTime attemptedAt) {
            this.attemptedAt = attemptedAt;
            return this;
        }

        public LoginAttemptBuilder success(Boolean success) {
            this.success = success;
            return this;
        }

        public LoginAttempt build() {
            LoginAttempt instance = new LoginAttempt();
            instance.id = this.id;
            instance.email = this.email;
            instance.ipAddress = this.ipAddress;
            instance.attemptedAt = this.attemptedAt;
            instance.success = this.success;
            return instance;
        }
    }
}
