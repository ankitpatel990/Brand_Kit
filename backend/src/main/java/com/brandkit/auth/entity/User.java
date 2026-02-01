package com.brandkit.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User Entity - Primary user accounts table for BrandKit platform
 * 
 * FRD-001: User Registration and Authentication System
 * - FR-1: Email-Based Registration
 * - FR-3: Google OAuth Integration
 * - FR-4: LinkedIn OAuth Integration
 * - FR-8: Role-Based Access Control
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private UserType userType = UserType.CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, columnDefinition = "VARCHAR(20)")
    private AuthProvider authProvider = AuthProvider.EMAIL;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "linkedin_id", length = 255)
    private String linkedinId;

    @Column(name = "last_login_at")
    private ZonedDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private ZonedDateTime lockedUntil;

    @Column(name = "terms_accepted_at")
    private ZonedDateTime termsAcceptedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    // Constructors
    public User() {
    }

    public User(UUID id, String email, String passwordHash, String fullName, String companyName,
                String phone, UserType userType, UserStatus status, Boolean emailVerified,
                String profilePictureUrl, AuthProvider authProvider, String googleId, String linkedinId,
                ZonedDateTime lastLoginAt, Integer failedLoginAttempts, ZonedDateTime lockedUntil,
                ZonedDateTime termsAcceptedAt, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.companyName = companyName;
        this.phone = phone;
        this.userType = userType;
        this.status = status;
        this.emailVerified = emailVerified;
        this.profilePictureUrl = profilePictureUrl;
        this.authProvider = authProvider;
        this.googleId = googleId;
        this.linkedinId = linkedinId;
        this.lastLoginAt = lastLoginAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
        this.termsAcceptedAt = termsAcceptedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhone() {
        return phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public ZonedDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public ZonedDateTime getLockedUntil() {
        return lockedUntil;
    }

    public ZonedDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setLinkedinId(String linkedinId) {
        this.linkedinId = linkedinId;
    }

    public void setLastLoginAt(ZonedDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public void setLockedUntil(ZonedDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public void setTermsAcceptedAt(ZonedDateTime termsAcceptedAt) {
        this.termsAcceptedAt = termsAcceptedAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private UUID id;
        private String email;
        private String passwordHash;
        private String fullName;
        private String companyName;
        private String phone;
        private UserType userType = UserType.CLIENT;
        private UserStatus status = UserStatus.PENDING_VERIFICATION;
        private Boolean emailVerified = false;
        private String profilePictureUrl;
        private AuthProvider authProvider = AuthProvider.EMAIL;
        private String googleId;
        private String linkedinId;
        private ZonedDateTime lastLoginAt;
        private Integer failedLoginAttempts = 0;
        private ZonedDateTime lockedUntil;
        private ZonedDateTime termsAcceptedAt;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        UserBuilder() {
        }

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public UserBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserBuilder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public UserBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public UserBuilder authProvider(AuthProvider authProvider) {
            this.authProvider = authProvider;
            return this;
        }

        public UserBuilder googleId(String googleId) {
            this.googleId = googleId;
            return this;
        }

        public UserBuilder linkedinId(String linkedinId) {
            this.linkedinId = linkedinId;
            return this;
        }

        public UserBuilder lastLoginAt(ZonedDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserBuilder failedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }

        public UserBuilder lockedUntil(ZonedDateTime lockedUntil) {
            this.lockedUntil = lockedUntil;
            return this;
        }

        public UserBuilder termsAcceptedAt(ZonedDateTime termsAcceptedAt) {
            this.termsAcceptedAt = termsAcceptedAt;
            return this;
        }

        public UserBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.email = this.email;
            user.passwordHash = this.passwordHash;
            user.fullName = this.fullName;
            user.companyName = this.companyName;
            user.phone = this.phone;
            user.userType = this.userType;
            user.status = this.status;
            user.emailVerified = this.emailVerified;
            user.profilePictureUrl = this.profilePictureUrl;
            user.authProvider = this.authProvider;
            user.googleId = this.googleId;
            user.linkedinId = this.linkedinId;
            user.lastLoginAt = this.lastLoginAt;
            user.failedLoginAttempts = this.failedLoginAttempts;
            user.lockedUntil = this.lockedUntil;
            user.termsAcceptedAt = this.termsAcceptedAt;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            return user;
        }
    }

    // Business methods
    /**
     * Check if account is currently locked
     * FRD-001 BR-8: Failed Login Lockout
     */
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(ZonedDateTime.now());
    }

    /**
     * Check if user can login (active and verified)
     */
    public boolean canLogin() {
        return status == UserStatus.ACTIVE && !isLocked();
    }

    /**
     * Increment failed login attempts
     * FRD-001 FR-6: CAPTCHA after 3 failures, lockout after 5
     */
    public void incrementFailedAttempts(int lockoutThreshold, int lockoutMinutes) {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= lockoutThreshold) {
            this.lockedUntil = ZonedDateTime.now().plusMinutes(lockoutMinutes);
        }
    }

    /**
     * Reset failed login attempts on successful login
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.lastLoginAt = ZonedDateTime.now();
    }

    /**
     * Activate user account after email verification
     * FRD-001 FR-2: Email Verification
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.emailVerified = true;
    }

    /**
     * Deactivate user account
     * FRD-001 FR-11: Account Status Management
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
}
