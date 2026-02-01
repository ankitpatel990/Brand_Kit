package com.brandkit.auth.dto;

import jakarta.validation.constraints.*;
/**
 * Profile Update Request DTO
 * 
 * FRD-001 FR-10: User Profile Management
 * Editable fields: Full Name, Company Name, Phone Number
 * Read-only fields: Email, User Type (enforced at service layer)
 */
public class ProfileUpdateRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    private String companyName;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Phone must be in format: +91-XXXXXXXXXX")
    private String phone;

    public String getFullName() {
        return this.fullName;
    }
    public String getCompanyName() {
        return this.companyName;
    }
    public String getPhone() {
        return this.phone;
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
    public ProfileUpdateRequest() {
    }
    public ProfileUpdateRequest(String fullName, String companyName, String phone) {
        this.fullName = fullName;
        this.companyName = companyName;
        this.phone = phone;
    }
    public static ProfileUpdateRequestBuilder builder() {
        return new ProfileUpdateRequestBuilder();
    }

    public static class ProfileUpdateRequestBuilder {
        private String fullName;
        private String companyName;
        private String phone;

        ProfileUpdateRequestBuilder() {
        }

        public ProfileUpdateRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public ProfileUpdateRequestBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public ProfileUpdateRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ProfileUpdateRequest build() {
            ProfileUpdateRequest instance = new ProfileUpdateRequest();
            instance.fullName = this.fullName;
            instance.companyName = this.companyName;
            instance.phone = this.phone;
            return instance;
        }
    }
}
