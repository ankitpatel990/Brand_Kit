package com.brandkit.auth.dto;

import com.brandkit.auth.validation.PasswordStrength;
import jakarta.validation.constraints.*;
/**
 * Change Password Request DTO
 * 
 * FRD-001 FR-10: User Profile Management
 * Requires current password verification before changing
 */
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @PasswordStrength
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        if (newPassword == null || confirmPassword == null) {
            return false;
        }
        return newPassword.equals(confirmPassword);
    }

    @AssertTrue(message = "New password must be different from current password")
    public boolean isNewPasswordDifferent() {
        if (currentPassword == null || newPassword == null) {
            return true; // Let other validations handle null
        }
        return !currentPassword.equals(newPassword);
    }

    public String getCurrentPassword() {
        return this.currentPassword;
    }
    public String getNewPassword() {
        return this.newPassword;
    }
    public String getConfirmPassword() {
        return this.confirmPassword;
    }
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public ChangePasswordRequest() {
    }
    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    public static ChangePasswordRequestBuilder builder() {
        return new ChangePasswordRequestBuilder();
    }

    public static class ChangePasswordRequestBuilder {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        ChangePasswordRequestBuilder() {
        }

        public ChangePasswordRequestBuilder currentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
            return this;
        }

        public ChangePasswordRequestBuilder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ChangePasswordRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public ChangePasswordRequest build() {
            ChangePasswordRequest instance = new ChangePasswordRequest();
            instance.currentPassword = this.currentPassword;
            instance.newPassword = this.newPassword;
            instance.confirmPassword = this.confirmPassword;
            return instance;
        }
    }
}
