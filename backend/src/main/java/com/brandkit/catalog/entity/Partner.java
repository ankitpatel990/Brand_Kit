package com.brandkit.catalog.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Partner Entity (Internal Only - Fulfillment Partners)
 * FRD-002 FR-27: Partner Association (Internal Only)
 * FRD-005: Partner Dashboard - Extended with profile fields
 * NEVER exposed to client-facing APIs
 */
@Entity
@Table(name = "partners", indexes = {
    @Index(name = "idx_partners_user_id", columnList = "user_id")
})
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Link to user account for authentication
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String location;

    @Column(name = "business_address", columnDefinition = "TEXT")
    private String businessAddress;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state = "Gujarat";

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Column(name = "gstin", length = 20)
    private String gstin;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00");

    @Column(name = "fulfillment_sla_days", nullable = false)
    private Integer fulfillmentSlaDays = 7;

    // Capacity settings - FRD-005 FR-64
    @Column(name = "max_concurrent_orders")
    private Integer maxConcurrentOrders = 20;

    @Column(name = "is_accepting_orders")
    private Boolean isAcceptingOrders = true;

    // Bank details - FRD-005 FR-64
    @Column(name = "bank_account_holder", length = 100)
    private String bankAccountHolder;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc_code", length = 15)
    private String bankIfscCode;

    @Column(name = "bank_verified")
    private Boolean bankVerified = false;

    // Profile completion - FRD-005 FR-51
    @Column(name = "profile_completed")
    private Boolean profileCompleted = false;

    @Column(name = "first_login_at")
    private ZonedDateTime firstLoginAt;

    // Categories partner handles
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "categories", columnDefinition = "text[]")
    private String[] categories;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Check if partner can accept new orders
     */
    public boolean canAcceptOrders() {
        return status == UserStatus.ACTIVE && 
               Boolean.TRUE.equals(isAcceptingOrders) && 
               Boolean.TRUE.equals(profileCompleted);
    }

    /**
     * Check if bank details are complete
     */
    public boolean hasBankDetails() {
        return bankAccountHolder != null && 
               bankName != null && 
               bankAccountNumber != null && 
               bankIfscCode != null;
    }

    public UUID getId() {
        return this.id;
    }
    public User getUser() {
        return this.user;
    }
    public String getBusinessName() {
        return this.businessName;
    }
    public String getOwnerName() {
        return this.ownerName;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getLocation() {
        return this.location;
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
    public String getGstNumber() {
        return this.gstNumber;
    }
    public String getGstin() {
        return this.gstin;
    }
    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }
    public UserStatus getStatus() {
        return this.status;
    }
    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }
    public Integer getFulfillmentSlaDays() {
        return this.fulfillmentSlaDays;
    }
    public Integer getMaxConcurrentOrders() {
        return this.maxConcurrentOrders;
    }
    public Boolean getIsAcceptingOrders() {
        return this.isAcceptingOrders;
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
    public Boolean getBankVerified() {
        return this.bankVerified;
    }
    public Boolean getProfileCompleted() {
        return this.profileCompleted;
    }
    public ZonedDateTime getFirstLoginAt() {
        return this.firstLoginAt;
    }
    public String[] getCategories() {
        return this.categories;
    }
    public void setCategories(String[] categories) {
        this.categories = categories;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setLocation(String location) {
        this.location = location;
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
    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }
    public void setGstin(String gstin) {
        this.gstin = gstin;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    public void setFulfillmentSlaDays(Integer fulfillmentSlaDays) {
        this.fulfillmentSlaDays = fulfillmentSlaDays;
    }
    public void setMaxConcurrentOrders(Integer maxConcurrentOrders) {
        this.maxConcurrentOrders = maxConcurrentOrders;
    }
    public void setIsAcceptingOrders(Boolean isAcceptingOrders) {
        this.isAcceptingOrders = isAcceptingOrders;
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
    public void setBankVerified(Boolean bankVerified) {
        this.bankVerified = bankVerified;
    }
    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }
    public void setFirstLoginAt(ZonedDateTime firstLoginAt) {
        this.firstLoginAt = firstLoginAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Partner() {
    }
    public Partner(UUID id, User user, String businessName, String ownerName, String email, String phone, String location, String businessAddress, String city, String state, String gstNumber, String gstin, String profilePictureUrl, UserStatus status, BigDecimal commissionRate, Integer fulfillmentSlaDays, Integer maxConcurrentOrders, Boolean isAcceptingOrders, String bankAccountHolder, String bankName, String bankAccountNumber, String bankIfscCode, Boolean bankVerified, Boolean profileCompleted, ZonedDateTime firstLoginAt, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.businessName = businessName;
        this.ownerName = ownerName;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.businessAddress = businessAddress;
        this.city = city;
        this.state = state;
        this.gstNumber = gstNumber;
        this.gstin = gstin;
        this.profilePictureUrl = profilePictureUrl;
        this.status = status;
        this.commissionRate = commissionRate;
        this.fulfillmentSlaDays = fulfillmentSlaDays;
        this.maxConcurrentOrders = maxConcurrentOrders;
        this.isAcceptingOrders = isAcceptingOrders;
        this.bankAccountHolder = bankAccountHolder;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.bankIfscCode = bankIfscCode;
        this.bankVerified = bankVerified;
        this.profileCompleted = profileCompleted;
        this.firstLoginAt = firstLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static PartnerBuilder builder() {
        return new PartnerBuilder();
    }

    public static class PartnerBuilder {
        private UUID id;
        private User user;
        private String businessName;
        private String ownerName;
        private String email;
        private String phone;
        private String location;
        private String businessAddress;
        private String city;
        private String state = "Gujarat";
        private String gstNumber;
        private String gstin;
        private String profilePictureUrl;
        private UserStatus status = UserStatus.ACTIVE;
        private BigDecimal commissionRate = new BigDecimal("10.00");
        private Integer fulfillmentSlaDays = 7;
        private Integer maxConcurrentOrders = 20;
        private Boolean isAcceptingOrders = true;
        private String bankAccountHolder;
        private String bankName;
        private String bankAccountNumber;
        private String bankIfscCode;
        private Boolean bankVerified = false;
        private Boolean profileCompleted = false;
        private ZonedDateTime firstLoginAt;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        PartnerBuilder() {
        }

        public PartnerBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PartnerBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PartnerBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public PartnerBuilder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public PartnerBuilder email(String email) {
            this.email = email;
            return this;
        }

        public PartnerBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public PartnerBuilder location(String location) {
            this.location = location;
            return this;
        }

        public PartnerBuilder businessAddress(String businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }

        public PartnerBuilder city(String city) {
            this.city = city;
            return this;
        }

        public PartnerBuilder state(String state) {
            this.state = state;
            return this;
        }

        public PartnerBuilder gstNumber(String gstNumber) {
            this.gstNumber = gstNumber;
            return this;
        }

        public PartnerBuilder gstin(String gstin) {
            this.gstin = gstin;
            return this;
        }

        public PartnerBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public PartnerBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public PartnerBuilder commissionRate(BigDecimal commissionRate) {
            this.commissionRate = commissionRate;
            return this;
        }

        public PartnerBuilder fulfillmentSlaDays(Integer fulfillmentSlaDays) {
            this.fulfillmentSlaDays = fulfillmentSlaDays;
            return this;
        }

        public PartnerBuilder maxConcurrentOrders(Integer maxConcurrentOrders) {
            this.maxConcurrentOrders = maxConcurrentOrders;
            return this;
        }

        public PartnerBuilder isAcceptingOrders(Boolean isAcceptingOrders) {
            this.isAcceptingOrders = isAcceptingOrders;
            return this;
        }

        public PartnerBuilder bankAccountHolder(String bankAccountHolder) {
            this.bankAccountHolder = bankAccountHolder;
            return this;
        }

        public PartnerBuilder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public PartnerBuilder bankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
            return this;
        }

        public PartnerBuilder bankIfscCode(String bankIfscCode) {
            this.bankIfscCode = bankIfscCode;
            return this;
        }

        public PartnerBuilder bankVerified(Boolean bankVerified) {
            this.bankVerified = bankVerified;
            return this;
        }

        public PartnerBuilder profileCompleted(Boolean profileCompleted) {
            this.profileCompleted = profileCompleted;
            return this;
        }

        public PartnerBuilder firstLoginAt(ZonedDateTime firstLoginAt) {
            this.firstLoginAt = firstLoginAt;
            return this;
        }

        public PartnerBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PartnerBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Partner build() {
            Partner instance = new Partner();
            instance.id = this.id;
            instance.user = this.user;
            instance.businessName = this.businessName;
            instance.ownerName = this.ownerName;
            instance.email = this.email;
            instance.phone = this.phone;
            instance.location = this.location;
            instance.businessAddress = this.businessAddress;
            instance.city = this.city;
            instance.state = this.state;
            instance.gstNumber = this.gstNumber;
            instance.gstin = this.gstin;
            instance.profilePictureUrl = this.profilePictureUrl;
            instance.status = this.status;
            instance.commissionRate = this.commissionRate;
            instance.fulfillmentSlaDays = this.fulfillmentSlaDays;
            instance.maxConcurrentOrders = this.maxConcurrentOrders;
            instance.isAcceptingOrders = this.isAcceptingOrders;
            instance.bankAccountHolder = this.bankAccountHolder;
            instance.bankName = this.bankName;
            instance.bankAccountNumber = this.bankAccountNumber;
            instance.bankIfscCode = this.bankIfscCode;
            instance.bankVerified = this.bankVerified;
            instance.profileCompleted = this.profileCompleted;
            instance.firstLoginAt = this.firstLoginAt;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
