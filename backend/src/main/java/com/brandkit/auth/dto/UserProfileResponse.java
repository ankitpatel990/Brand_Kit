package com.brandkit.auth.dto;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.entity.UserStatus;
import com.brandkit.auth.entity.AuthProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User Profile Response DTO
 * 
 * FRD-001 FR-10: User Profile Management
 * Returns user data without sensitive fields (password hash)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String companyName;
    private String phone;
    private UserType userType;
    private UserStatus status;
    private Boolean emailVerified;
    private String profilePictureUrl;
    private AuthProvider authProvider;
    private ZonedDateTime lastLoginAt;
    private ZonedDateTime createdAt;

    /**
     * Convert User entity to ProfileResponse
     */
    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .companyName(user.getCompanyName())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .authProvider(user.getAuthProvider())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UUID getId() {
        return this.id;
    }
    public String getEmail() {
        return this.email;
    }
    public String getFullName() {
        return this.fullName;
    }
    public String getCompanyName() {
        return this.companyName;
    }
    public String getPhone() {
        return this.phone;
    }
    public UserType getUserType() {
        return this.userType;
    }
    public UserStatus getStatus() {
        return this.status;
    }
    public Boolean getEmailVerified() {
        return this.emailVerified;
    }
    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }
    public AuthProvider getAuthProvider() {
        return this.authProvider;
    }
    public ZonedDateTime getLastLoginAt() {
        return this.lastLoginAt;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public void setLastLoginAt(ZonedDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public UserProfileResponse() {
    }
    public UserProfileResponse(UUID id, String email, String fullName, String companyName, String phone, UserType userType, UserStatus status, Boolean emailVerified, String profilePictureUrl, AuthProvider authProvider, ZonedDateTime lastLoginAt, ZonedDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.companyName = companyName;
        this.phone = phone;
        this.userType = userType;
        this.status = status;
        this.emailVerified = emailVerified;
        this.profilePictureUrl = profilePictureUrl;
        this.authProvider = authProvider;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }
    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }

    public static class UserProfileResponseBuilder {
        private UUID id;
        private String email;
        private String fullName;
        private String companyName;
        private String phone;
        private UserType userType;
        private UserStatus status;
        private Boolean emailVerified;
        private String profilePictureUrl;
        private AuthProvider authProvider;
        private ZonedDateTime lastLoginAt;
        private ZonedDateTime createdAt;

        UserProfileResponseBuilder() {
        }

        public UserProfileResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserProfileResponseBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public UserProfileResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserProfileResponseBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserProfileResponseBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserProfileResponseBuilder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public UserProfileResponseBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public UserProfileResponseBuilder authProvider(AuthProvider authProvider) {
            this.authProvider = authProvider;
            return this;
        }

        public UserProfileResponseBuilder lastLoginAt(ZonedDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserProfileResponseBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileResponse build() {
            UserProfileResponse instance = new UserProfileResponse();
            instance.id = this.id;
            instance.email = this.email;
            instance.fullName = this.fullName;
            instance.companyName = this.companyName;
            instance.phone = this.phone;
            instance.userType = this.userType;
            instance.status = this.status;
            instance.emailVerified = this.emailVerified;
            instance.profilePictureUrl = this.profilePictureUrl;
            instance.authProvider = this.authProvider;
            instance.lastLoginAt = this.lastLoginAt;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
