package com.brandkit.partner.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Partner Profile Request - FRD-005 FR-64
 * Request for updating partner profile
 */
public class PartnerProfileRequest {

    @Size(min = 2, max = 200, message = "Business name must be 2-200 characters")
    private String businessName;

    @Size(min = 2, max = 100, message = "Owner name must be 2-100 characters")
    private String ownerName;

    @Pattern(regexp = "^(\\+91)?[0-9]{10}$", message = "Invalid phone number")
    private String phone;

    @Size(min = 10, max = 500, message = "Business address must be 10-500 characters")
    private String businessAddress;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GSTIN format")
    private String gstin;

    // Bank details
    @Size(min = 2, max = 100, message = "Account holder name must be 2-100 characters")
    private String bankAccountHolder;

    @Size(min = 2, max = 100, message = "Bank name must be 2-100 characters")
    private String bankName;

    @Pattern(regexp = "^[0-9]{8,18}$", message = "Invalid account number")
    private String bankAccountNumber;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String bankIfscCode;

    // Capacity settings
    @Min(value = 1, message = "Max concurrent orders must be at least 1")
    @Max(value = 1000, message = "Max concurrent orders cannot exceed 1000")
    private Integer maxConcurrentOrders;

    private Boolean isAcceptingOrders;

    private List<String> categories;

    public String getBusinessName() {
        return this.businessName;
    }
    public String getOwnerName() {
        return this.ownerName;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getBusinessAddress() {
        return this.businessAddress;
    }
    public String getCity() {
        return this.city;
    }
    public String getState() {
        return this.state;
    }
    public String getGstin() {
        return this.gstin;
    }
    public String getBankAccountHolder() {
        return this.bankAccountHolder;
    }
    public String getBankName() {
        return this.bankName;
    }
    public String getBankAccountNumber() {
        return this.bankAccountNumber;
    }
    public String getBankIfscCode() {
        return this.bankIfscCode;
    }
    public Integer getMaxConcurrentOrders() {
        return this.maxConcurrentOrders;
    }
    public Boolean getIsAcceptingOrders() {
        return this.isAcceptingOrders;
    }
    public List<String> getCategories() {
        return this.categories;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setGstin(String gstin) {
        this.gstin = gstin;
    }
    public void setBankAccountHolder(String bankAccountHolder) {
        this.bankAccountHolder = bankAccountHolder;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    public void setBankIfscCode(String bankIfscCode) {
        this.bankIfscCode = bankIfscCode;
    }
    public void setMaxConcurrentOrders(Integer maxConcurrentOrders) {
        this.maxConcurrentOrders = maxConcurrentOrders;
    }
    public void setIsAcceptingOrders(Boolean isAcceptingOrders) {
        this.isAcceptingOrders = isAcceptingOrders;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    public PartnerProfileRequest() {
    }
    public PartnerProfileRequest(String businessName, String ownerName, String phone, String businessAddress, String city, String state, String gstin, String bankAccountHolder, String bankName, String bankAccountNumber, String bankIfscCode, Integer maxConcurrentOrders, Boolean isAcceptingOrders, List<String> categories) {
        this.businessName = businessName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.businessAddress = businessAddress;
        this.city = city;
        this.state = state;
        this.gstin = gstin;
        this.bankAccountHolder = bankAccountHolder;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.bankIfscCode = bankIfscCode;
        this.maxConcurrentOrders = maxConcurrentOrders;
        this.isAcceptingOrders = isAcceptingOrders;
        this.categories = categories;
    }
    public static PartnerProfileRequestBuilder builder() {
        return new PartnerProfileRequestBuilder();
    }

    public static class PartnerProfileRequestBuilder {
        private String businessName;
        private String ownerName;
        private String phone;
        private String businessAddress;
        private String city;
        private String state;
        private String gstin;
        private String bankAccountHolder;
        private String bankName;
        private String bankAccountNumber;
        private String bankIfscCode;
        private Integer maxConcurrentOrders;
        private Boolean isAcceptingOrders;
        private List<String> categories;

        PartnerProfileRequestBuilder() {
        }

        public PartnerProfileRequestBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public PartnerProfileRequestBuilder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public PartnerProfileRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public PartnerProfileRequestBuilder businessAddress(String businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }

        public PartnerProfileRequestBuilder city(String city) {
            this.city = city;
            return this;
        }

        public PartnerProfileRequestBuilder state(String state) {
            this.state = state;
            return this;
        }

        public PartnerProfileRequestBuilder gstin(String gstin) {
            this.gstin = gstin;
            return this;
        }

        public PartnerProfileRequestBuilder bankAccountHolder(String bankAccountHolder) {
            this.bankAccountHolder = bankAccountHolder;
            return this;
        }

        public PartnerProfileRequestBuilder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public PartnerProfileRequestBuilder bankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
            return this;
        }

        public PartnerProfileRequestBuilder bankIfscCode(String bankIfscCode) {
            this.bankIfscCode = bankIfscCode;
            return this;
        }

        public PartnerProfileRequestBuilder maxConcurrentOrders(Integer maxConcurrentOrders) {
            this.maxConcurrentOrders = maxConcurrentOrders;
            return this;
        }

        public PartnerProfileRequestBuilder isAcceptingOrders(Boolean isAcceptingOrders) {
            this.isAcceptingOrders = isAcceptingOrders;
            return this;
        }

        public PartnerProfileRequestBuilder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public PartnerProfileRequest build() {
            PartnerProfileRequest instance = new PartnerProfileRequest();
            instance.businessName = this.businessName;
            instance.ownerName = this.ownerName;
            instance.phone = this.phone;
            instance.businessAddress = this.businessAddress;
            instance.city = this.city;
            instance.state = this.state;
            instance.gstin = this.gstin;
            instance.bankAccountHolder = this.bankAccountHolder;
            instance.bankName = this.bankName;
            instance.bankAccountNumber = this.bankAccountNumber;
            instance.bankIfscCode = this.bankIfscCode;
            instance.maxConcurrentOrders = this.maxConcurrentOrders;
            instance.isAcceptingOrders = this.isAcceptingOrders;
            instance.categories = this.categories;
            return instance;
        }
    }
}
