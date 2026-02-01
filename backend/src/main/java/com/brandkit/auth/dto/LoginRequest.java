package com.brandkit.auth.dto;

import jakarta.validation.constraints.*;
/**
 * Login Request DTO
 * 
 * FRD-001 FR-6: Login Functionality
 * Input validation as per section 7 specifications
 */
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Remember Me option
     * FRD-001 FR-6: Extends refresh token to 30 days
     */
    private Boolean rememberMe = false;

    /**
     * reCAPTCHA token for verification after 3 failed attempts
     * FRD-001 FR-6, FR-12: CAPTCHA verification
     */
    private String captchaToken;

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public Boolean getRememberMe() {
        return this.rememberMe;
    }
    public String getCaptchaToken() {
        return this.captchaToken;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
    public LoginRequest() {
    }
    public LoginRequest(String email, String password, Boolean rememberMe, String captchaToken) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
        this.captchaToken = captchaToken;
    }
    public static LoginRequestBuilder builder() {
        return new LoginRequestBuilder();
    }

    public static class LoginRequestBuilder {
        private String email;
        private String password;
        private Boolean rememberMe;
        private String captchaToken;

        LoginRequestBuilder() {
        }

        public LoginRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public LoginRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequestBuilder rememberMe(Boolean rememberMe) {
            this.rememberMe = rememberMe;
            return this;
        }

        public LoginRequestBuilder captchaToken(String captchaToken) {
            this.captchaToken = captchaToken;
            return this;
        }

        public LoginRequest build() {
            LoginRequest instance = new LoginRequest();
            instance.email = this.email;
            instance.password = this.password;
            instance.rememberMe = this.rememberMe;
            instance.captchaToken = this.captchaToken;
            return instance;
        }
    }
}
