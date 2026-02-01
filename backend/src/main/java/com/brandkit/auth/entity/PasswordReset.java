package com.brandkit.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Password Reset Entity - Stores password reset tokens
 * 
 * FRD-001 FR-7: Password Reset Workflow
 * - Token expires in 1 hour
 * - Can only be used once
 * - Hash stored for security (not plain token)
 */
@Entity
@Table(name = "password_resets")
public class PasswordReset {

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

    @Column(name = "used_at")
    private ZonedDateTime usedAt;

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
     * Mark token as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = ZonedDateTime.now();
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
    public ZonedDateTime getUsedAt() {
        return this.usedAt;
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
    public void setUsedAt(ZonedDateTime usedAt) {
        this.usedAt = usedAt;
    }
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public PasswordReset() {
    }
    public PasswordReset(UUID id, User user, String tokenHash, ZonedDateTime expiresAt, ZonedDateTime usedAt, Boolean isUsed, ZonedDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }
    public static PasswordResetBuilder builder() {
        return new PasswordResetBuilder();
    }

    public static class PasswordResetBuilder {
        private UUID id;
        private User user;
        private String tokenHash;
        private ZonedDateTime expiresAt;
        private ZonedDateTime usedAt;
        private Boolean isUsed = false;
        private ZonedDateTime createdAt;

        PasswordResetBuilder() {
        }

        public PasswordResetBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PasswordResetBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PasswordResetBuilder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public PasswordResetBuilder expiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public PasswordResetBuilder usedAt(ZonedDateTime usedAt) {
            this.usedAt = usedAt;
            return this;
        }

        public PasswordResetBuilder isUsed(Boolean isUsed) {
            this.isUsed = isUsed;
            return this;
        }

        public PasswordResetBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PasswordReset build() {
            PasswordReset instance = new PasswordReset();
            instance.id = this.id;
            instance.user = this.user;
            instance.tokenHash = this.tokenHash;
            instance.expiresAt = this.expiresAt;
            instance.usedAt = this.usedAt;
            instance.isUsed = this.isUsed;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
