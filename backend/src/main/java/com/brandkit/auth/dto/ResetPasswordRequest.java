package com.brandkit.auth.dto;

import com.brandkit.auth.validation.PasswordStrength;
import jakarta.validation.constraints.*;
/**
 * Reset Password Request DTO
 * 
 * FRD-001 FR-7: Password Reset Workflow
 */
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Password is required")
    @PasswordStrength
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    public String getToken() {
        return this.token;
    }
    public String getPassword() {
        return this.password;
    }
    public String getConfirmPassword() {
        return this.confirmPassword;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public ResetPasswordRequest() {
    }
    public ResetPasswordRequest(String token, String password, String confirmPassword) {
        this.token = token;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
    public static ResetPasswordRequestBuilder builder() {
        return new ResetPasswordRequestBuilder();
    }

    public static class ResetPasswordRequestBuilder {
        private String token;
        private String password;
        private String confirmPassword;

        ResetPasswordRequestBuilder() {
        }

        public ResetPasswordRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ResetPasswordRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public ResetPasswordRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public ResetPasswordRequest build() {
            ResetPasswordRequest instance = new ResetPasswordRequest();
            instance.token = this.token;
            instance.password = this.password;
            instance.confirmPassword = this.confirmPassword;
            return instance;
        }
    }
}
