package com.brandkit.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User Session Entity - JWT refresh token storage
 * 
 * FRD-001 FR-9: Session Management
 * - Stores refresh tokens for JWT-based authentication
 * - Tracks device info, IP address for security auditing
 * - Supports session revocation for logout and security
 */
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, length = 255)
    private String refreshTokenHash;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "revoked_at")
    private ZonedDateTime revokedAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    /**
     * Check if session is valid (not revoked and not expired)
     */
    public boolean isValid() {
        return !isRevoked && expiresAt.isAfter(ZonedDateTime.now());
    }

    /**
     * Revoke this session
     */
    public void revoke() {
        this.isRevoked = true;
        this.revokedAt = ZonedDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public User getUser() {
        return this.user;
    }
    public String getRefreshTokenHash() {
        return this.refreshTokenHash;
    }
    public String getDeviceInfo() {
        return this.deviceInfo;
    }
    public String getIpAddress() {
        return this.ipAddress;
    }
    public String getUserAgent() {
        return this.userAgent;
    }
    public ZonedDateTime getExpiresAt() {
        return this.expiresAt;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getRevokedAt() {
        return this.revokedAt;
    }
    public Boolean getIsRevoked() {
        return this.isRevoked;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setRevokedAt(ZonedDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }
    public UserSession() {
    }
    public UserSession(UUID id, User user, String refreshTokenHash, String deviceInfo, String ipAddress, String userAgent, ZonedDateTime expiresAt, ZonedDateTime createdAt, ZonedDateTime revokedAt, Boolean isRevoked) {
        this.id = id;
        this.user = user;
        this.refreshTokenHash = refreshTokenHash;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
        this.isRevoked = isRevoked;
    }
    public static UserSessionBuilder builder() {
        return new UserSessionBuilder();
    }

    public static class UserSessionBuilder {
        private UUID id;
        private User user;
        private String refreshTokenHash;
        private String deviceInfo;
        private String ipAddress;
        private String userAgent;
        private ZonedDateTime expiresAt;
        private ZonedDateTime createdAt;
        private ZonedDateTime revokedAt;
        private Boolean isRevoked = false;

        UserSessionBuilder() {
        }

        public UserSessionBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserSessionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserSessionBuilder refreshTokenHash(String refreshTokenHash) {
            this.refreshTokenHash = refreshTokenHash;
            return this;
        }

        public UserSessionBuilder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public UserSessionBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public UserSessionBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public UserSessionBuilder expiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public UserSessionBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserSessionBuilder revokedAt(ZonedDateTime revokedAt) {
            this.revokedAt = revokedAt;
            return this;
        }

        public UserSessionBuilder isRevoked(Boolean isRevoked) {
            this.isRevoked = isRevoked;
            return this;
        }

        public UserSession build() {
            UserSession instance = new UserSession();
            instance.id = this.id;
            instance.user = this.user;
            instance.refreshTokenHash = this.refreshTokenHash;
            instance.deviceInfo = this.deviceInfo;
            instance.ipAddress = this.ipAddress;
            instance.userAgent = this.userAgent;
            instance.expiresAt = this.expiresAt;
            instance.createdAt = this.createdAt;
            instance.revokedAt = this.revokedAt;
            instance.isRevoked = this.isRevoked;
            return instance;
        }
    }
}
