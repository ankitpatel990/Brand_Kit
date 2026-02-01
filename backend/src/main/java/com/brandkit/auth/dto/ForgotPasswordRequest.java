package com.brandkit.auth.dto;

import jakarta.validation.constraints.*;
/**
 * Forgot Password Request DTO
 * 
 * FRD-001 FR-7: Password Reset Workflow
 */
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public ForgotPasswordRequest() {
    }
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
    public static ForgotPasswordRequestBuilder builder() {
        return new ForgotPasswordRequestBuilder();
    }

    public static class ForgotPasswordRequestBuilder {
        private String email;

        ForgotPasswordRequestBuilder() {
        }

        public ForgotPasswordRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ForgotPasswordRequest build() {
            ForgotPasswordRequest instance = new ForgotPasswordRequest();
            instance.email = this.email;
            return instance;
        }
    }
}
