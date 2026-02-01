package com.brandkit.auth.dto;

import com.brandkit.auth.entity.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

/**
 * Authentication Response DTO
 * 
 * FRD-001 Section 7: Output format for successful login/registration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String status;
    private String message;
    private AuthData data;

    public AuthResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthData getData() {
        return data;
    }

    public void setData(AuthData data) {
        this.data = data;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    /**
     * Create success response for login
     */
    public static AuthResponse loginSuccess(UUID userId, String email, String fullName, 
                                            UserType role, String accessToken, 
                                            String refreshToken, long expiresIn) {
        return AuthResponse.builder()
                .status("success")
                .message("Login successful")
                .data(AuthData.builder()
                        .userId(userId)
                        .email(email)
                        .fullName(fullName)
                        .role(role)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(expiresIn)
                        .build())
                .build();
    }

    /**
     * Create success response for registration
     */
    public static AuthResponse registrationSuccess(UUID userId, String email) {
        return AuthResponse.builder()
                .status("success")
                .message("Registration successful. Verification email sent to " + email)
                .data(AuthData.builder()
                        .userId(userId)
                        .email(email)
                        .verificationRequired(true)
                        .build())
                .build();
    }

    public static class AuthData {
        private UUID userId;
        private String email;
        private String fullName;
        private UserType role;
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private Boolean verificationRequired;

        public AuthData() {
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public UserType getRole() {
            return role;
        }

        public void setRole(UserType role) {
            this.role = role;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public Boolean getVerificationRequired() {
            return verificationRequired;
        }

        public void setVerificationRequired(Boolean verificationRequired) {
            this.verificationRequired = verificationRequired;
        }

        public static AuthDataBuilder builder() {
            return new AuthDataBuilder();
        }

        public static class AuthDataBuilder {
            private UUID userId;
            private String email;
            private String fullName;
            private UserType role;
            private String accessToken;
            private String refreshToken;
            private Long expiresIn;
            private Boolean verificationRequired;

            AuthDataBuilder() {
            }

            public AuthDataBuilder userId(UUID userId) {
                this.userId = userId;
                return this;
            }

            public AuthDataBuilder email(String email) {
                this.email = email;
                return this;
            }

            public AuthDataBuilder fullName(String fullName) {
                this.fullName = fullName;
                return this;
            }

            public AuthDataBuilder role(UserType role) {
                this.role = role;
                return this;
            }

            public AuthDataBuilder accessToken(String accessToken) {
                this.accessToken = accessToken;
                return this;
            }

            public AuthDataBuilder refreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
                return this;
            }

            public AuthDataBuilder expiresIn(Long expiresIn) {
                this.expiresIn = expiresIn;
                return this;
            }

            public AuthDataBuilder verificationRequired(Boolean verificationRequired) {
                this.verificationRequired = verificationRequired;
                return this;
            }

            public AuthData build() {
                AuthData instance = new AuthData();
                instance.userId = this.userId;
                instance.email = this.email;
                instance.fullName = this.fullName;
                instance.role = this.role;
                instance.accessToken = this.accessToken;
                instance.refreshToken = this.refreshToken;
                instance.expiresIn = this.expiresIn;
                instance.verificationRequired = this.verificationRequired;
                return instance;
            }
        }
    }

    public static class AuthResponseBuilder {
        private String status;
        private String message;
        private AuthData data;

        AuthResponseBuilder() {
        }

        public AuthResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponseBuilder data(AuthData data) {
            this.data = data;
            return this;
        }

        public AuthResponse build() {
            AuthResponse instance = new AuthResponse();
            instance.status = this.status;
            instance.message = this.message;
            instance.data = this.data;
            return instance;
        }
    }
}
