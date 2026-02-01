package com.brandkit.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Email Verification Entity - Stores email verification tokens
 * 
 * FRD-001 FR-2: Email Verification
 * - Token expires in 24 hours
 * - Can only be used once
 * - Hash stored for security
 */
@Entity
@Table(name = "email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    @Column(name = "verified_at")
    private ZonedDateTime verifiedAt;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Check if token is valid (not used and not expired)
     * FRD-001 BR-9: Token Reuse Prevention
     */
    public boolean isValid() {
        return !isUsed && expiresAt.isAfter(ZonedDateTime.now());
    }

    /**
     * Mark token as used and record verification time
     */
    public void markAsVerified() {
        this.isUsed = true;
        this.verifiedAt = ZonedDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public User getUser() {
        return this.user;
    }
    public String getTokenHash() {
        return this.tokenHash;
    }
    public ZonedDateTime getExpiresAt() {
        return this.expiresAt;
    }
    public ZonedDateTime getVerifiedAt() {
        return this.verifiedAt;
    }
    public Boolean getIsUsed() {
        return this.isUsed;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public void setVerifiedAt(ZonedDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public EmailVerification() {
    }
    public EmailVerification(UUID id, User user, String tokenHash, ZonedDateTime expiresAt, ZonedDateTime verifiedAt, Boolean isUsed, ZonedDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }
    public static EmailVerificationBuilder builder() {
        return new EmailVerificationBuilder();
    }

    public static class EmailVerificationBuilder {
        private UUID id;
        private User user;
        private String tokenHash;
        private ZonedDateTime expiresAt;
        private ZonedDateTime verifiedAt;
        private Boolean isUsed = false;
        private ZonedDateTime createdAt;

        EmailVerificationBuilder() {
        }

        public EmailVerificationBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public EmailVerificationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public EmailVerificationBuilder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public EmailVerificationBuilder expiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public EmailVerificationBuilder verifiedAt(ZonedDateTime verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public EmailVerificationBuilder isUsed(Boolean isUsed) {
            this.isUsed = isUsed;
            return this;
        }

        public EmailVerificationBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmailVerification build() {
            EmailVerification instance = new EmailVerification();
            instance.id = this.id;
            instance.user = this.user;
            instance.tokenHash = this.tokenHash;
            instance.expiresAt = this.expiresAt;
            instance.verifiedAt = this.verifiedAt;
            instance.isUsed = this.isUsed;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
