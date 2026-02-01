package com.brandkit.auth.dto;

import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.validation.PasswordStrength;
import jakarta.validation.constraints.*;
/**
 * Registration Request DTO
 * 
 * FRD-001 FR-1: Email-Based Registration
 * Input validation as per section 7 specifications
 */
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @PasswordStrength
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    private String companyName;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Phone must be in format: +91-XXXXXXXXXX")
    private String phone;

    @NotNull(message = "User type is required")
    private UserType userType;

    @AssertTrue(message = "You must accept the Terms & Conditions")
    private Boolean termsAccepted;

    /**
     * Validate passwords match
     * FRD-001 Section 7: Confirm Password must match password
     */
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    public String getFullName() {
        return this.fullName;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getConfirmPassword() {
        return this.confirmPassword;
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
    public Boolean getTermsAccepted() {
        return this.termsAccepted;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
    public RegisterRequest() {
    }
    public RegisterRequest(String fullName, String email, String password, String confirmPassword, String companyName, String phone, UserType userType, Boolean termsAccepted) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.companyName = companyName;
        this.phone = phone;
        this.userType = userType;
        this.termsAccepted = termsAccepted;
    }
    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public static class RegisterRequestBuilder {
        private String fullName;
        private String email;
        private String password;
        private String confirmPassword;
        private String companyName;
        private String phone;
        private UserType userType;
        private Boolean termsAccepted;

        RegisterRequestBuilder() {
        }

        public RegisterRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public RegisterRequestBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public RegisterRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegisterRequestBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public RegisterRequestBuilder termsAccepted(Boolean termsAccepted) {
            this.termsAccepted = termsAccepted;
            return this;
        }

        public RegisterRequest build() {
            RegisterRequest instance = new RegisterRequest();
            instance.fullName = this.fullName;
            instance.email = this.email;
            instance.password = this.password;
            instance.confirmPassword = this.confirmPassword;
            instance.companyName = this.companyName;
            instance.phone = this.phone;
            instance.userType = this.userType;
            instance.termsAccepted = this.termsAccepted;
            return instance;
        }
    }
}
