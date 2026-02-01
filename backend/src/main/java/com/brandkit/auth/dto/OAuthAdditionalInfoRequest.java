package com.brandkit.auth.dto;

import com.brandkit.auth.entity.UserType;
import jakarta.validation.constraints.*;
/**
 * OAuth Additional Info Request DTO
 * 
 * FRD-001 FR-3, FR-4: Google/LinkedIn OAuth Integration
 * FRD-001 BR-10: Mandatory Fields for Social Auth
 * 
 * When a new user registers via OAuth, they must provide additional
 * mandatory fields before accessing the platform.
 */
public class OAuthAdditionalInfoRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    private String companyName;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Phone must be in format: +91-XXXXXXXXXX")
    private String phone;

    @NotNull(message = "User type is required")
    private UserType userType;

    @AssertTrue(message = "You must accept the Terms & Conditions")
    private Boolean termsAccepted;

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
    public OAuthAdditionalInfoRequest() {
    }
    public OAuthAdditionalInfoRequest(String companyName, String phone, UserType userType, Boolean termsAccepted) {
        this.companyName = companyName;
        this.phone = phone;
        this.userType = userType;
        this.termsAccepted = termsAccepted;
    }
    public static OAuthAdditionalInfoRequestBuilder builder() {
        return new OAuthAdditionalInfoRequestBuilder();
    }

    public static class OAuthAdditionalInfoRequestBuilder {
        private String companyName;
        private String phone;
        private UserType userType;
        private Boolean termsAccepted;

        OAuthAdditionalInfoRequestBuilder() {
        }

        public OAuthAdditionalInfoRequestBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public OAuthAdditionalInfoRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public OAuthAdditionalInfoRequestBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public OAuthAdditionalInfoRequestBuilder termsAccepted(Boolean termsAccepted) {
            this.termsAccepted = termsAccepted;
            return this;
        }

        public OAuthAdditionalInfoRequest build() {
            OAuthAdditionalInfoRequest instance = new OAuthAdditionalInfoRequest();
            instance.companyName = this.companyName;
            instance.phone = this.phone;
            instance.userType = this.userType;
            instance.termsAccepted = this.termsAccepted;
            return instance;
        }
    }
}
