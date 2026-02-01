package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Partner Profile Response - FRD-005 FR-64
 * Partner profile data
 */
public class PartnerProfileResponse {

    private String partnerId;
    private String email;
    private String businessName;
    private String ownerName;
    private String phone;
    private String businessAddress;
    private String city;
    private String state;
    private String gstin;
    private String profilePictureUrl;
    private boolean profileCompleted;
    private BankDetails bankDetails;
    private CapacitySettings capacitySettings;
    private List<String> categories;
    private BigDecimal commissionRate;
    private String status;
    private String createdAt;

    public PartnerProfileResponse() {
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    public CapacitySettings getCapacitySettings() {
        return capacitySettings;
    }

    public void setCapacitySettings(CapacitySettings capacitySettings) {
        this.capacitySettings = capacitySettings;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static PartnerProfileResponseBuilder builder() {
        return new PartnerProfileResponseBuilder();
    }

    public static class PartnerProfileResponseBuilder {
        private String partnerId;
        private String email;
        private String businessName;
        private String ownerName;
        private String phone;
        private String businessAddress;
        private String city;
        private String state;
        private String gstin;
        private String profilePictureUrl;
        private boolean profileCompleted;
        private BankDetails bankDetails;
        private CapacitySettings capacitySettings;
        private List<String> categories;
        private BigDecimal commissionRate;
        private String status;
        private String createdAt;

        public PartnerProfileResponseBuilder partnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }
        public PartnerProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }
        public PartnerProfileResponseBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }
        public PartnerProfileResponseBuilder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }
        public PartnerProfileResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }
        public PartnerProfileResponseBuilder businessAddress(String businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }
        public PartnerProfileResponseBuilder city(String city) {
            this.city = city;
            return this;
        }
        public PartnerProfileResponseBuilder state(String state) {
            this.state = state;
            return this;
        }
        public PartnerProfileResponseBuilder gstin(String gstin) {
            this.gstin = gstin;
            return this;
        }
        public PartnerProfileResponseBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }
        public PartnerProfileResponseBuilder profileCompleted(boolean profileCompleted) {
            this.profileCompleted = profileCompleted;
            return this;
        }
        public PartnerProfileResponseBuilder bankDetails(BankDetails bankDetails) {
            this.bankDetails = bankDetails;
            return this;
        }
        public PartnerProfileResponseBuilder capacitySettings(CapacitySettings capacitySettings) {
            this.capacitySettings = capacitySettings;
            return this;
        }
        public PartnerProfileResponseBuilder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }
        public PartnerProfileResponseBuilder commissionRate(BigDecimal commissionRate) {
            this.commissionRate = commissionRate;
            return this;
        }
        public PartnerProfileResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        public PartnerProfileResponseBuilder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PartnerProfileResponse build() {
            PartnerProfileResponse instance = new PartnerProfileResponse();
            instance.partnerId = this.partnerId;
            instance.email = this.email;
            instance.businessName = this.businessName;
            instance.ownerName = this.ownerName;
            instance.phone = this.phone;
            instance.businessAddress = this.businessAddress;
            instance.city = this.city;
            instance.state = this.state;
            instance.gstin = this.gstin;
            instance.profilePictureUrl = this.profilePictureUrl;
            instance.profileCompleted = this.profileCompleted;
            instance.bankDetails = this.bankDetails;
            instance.capacitySettings = this.capacitySettings;
            instance.categories = this.categories;
            instance.commissionRate = this.commissionRate;
            instance.status = this.status;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }

    public static class BankDetails {
        private String accountHolder;
        private String bankName;
        private String accountNumber;
        private String ifscCode;
        private boolean verified;

        public BankDetails() {
        }

        public String getAccountHolder() {
            return accountHolder;
        }

        public void setAccountHolder(String accountHolder) {
            this.accountHolder = accountHolder;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(String ifscCode) {
            this.ifscCode = ifscCode;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public static BankDetailsBuilder builder() {
            return new BankDetailsBuilder();
        }

        public static class BankDetailsBuilder {
            private String accountHolder;
            private String bankName;
            private String accountNumber;
            private String ifscCode;
            private boolean verified;

            public BankDetailsBuilder accountHolder(String accountHolder) {
                this.accountHolder = accountHolder;
                return this;
            }
            public BankDetailsBuilder bankName(String bankName) {
                this.bankName = bankName;
                return this;
            }
            public BankDetailsBuilder accountNumber(String accountNumber) {
                this.accountNumber = accountNumber;
                return this;
            }
            public BankDetailsBuilder ifscCode(String ifscCode) {
                this.ifscCode = ifscCode;
                return this;
            }
            public BankDetailsBuilder verified(boolean verified) {
                this.verified = verified;
                return this;
            }

            public BankDetails build() {
                BankDetails instance = new BankDetails();
                instance.accountHolder = this.accountHolder;
                instance.bankName = this.bankName;
                instance.accountNumber = this.accountNumber;
                instance.ifscCode = this.ifscCode;
                instance.verified = this.verified;
                return instance;
            }
        }
    }

    public static class CapacitySettings {
        private int maxConcurrentOrders;
        private boolean isAcceptingOrders;
        private int currentActiveOrders;

        public CapacitySettings() {
        }

        public int getMaxConcurrentOrders() {
            return maxConcurrentOrders;
        }

        public void setMaxConcurrentOrders(int maxConcurrentOrders) {
            this.maxConcurrentOrders = maxConcurrentOrders;
        }

        public boolean isAcceptingOrders() {
            return isAcceptingOrders;
        }

        public void setAcceptingOrders(boolean isAcceptingOrders) {
            this.isAcceptingOrders = isAcceptingOrders;
        }

        public int getCurrentActiveOrders() {
            return currentActiveOrders;
        }

        public void setCurrentActiveOrders(int currentActiveOrders) {
            this.currentActiveOrders = currentActiveOrders;
        }

        public static CapacitySettingsBuilder builder() {
            return new CapacitySettingsBuilder();
        }

        public static class CapacitySettingsBuilder {
            private int maxConcurrentOrders;
            private boolean isAcceptingOrders;
            private int currentActiveOrders;

            public CapacitySettingsBuilder maxConcurrentOrders(int maxConcurrentOrders) {
                this.maxConcurrentOrders = maxConcurrentOrders;
                return this;
            }
            public CapacitySettingsBuilder isAcceptingOrders(boolean isAcceptingOrders) {
                this.isAcceptingOrders = isAcceptingOrders;
                return this;
            }
            public CapacitySettingsBuilder currentActiveOrders(int currentActiveOrders) {
                this.currentActiveOrders = currentActiveOrders;
                return this;
            }

            public CapacitySettings build() {
                CapacitySettings instance = new CapacitySettings();
                instance.maxConcurrentOrders = this.maxConcurrentOrders;
                instance.isAcceptingOrders = this.isAcceptingOrders;
                instance.currentActiveOrders = this.currentActiveOrders;
                return instance;
            }
        }
    }
}
