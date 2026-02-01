package com.brandkit.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for commission configuration
 * 
 * FRD-006 FR-72: Commission Configuration
 */
public class CommissionConfigRequest {

    @NotBlank(message = "Configuration name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;

    private Boolean isDefault;

    @Valid
    @NotEmpty(message = "At least one commission tier is required")
    private List<CommissionTierRequest> tiers;

    public CommissionConfigRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<CommissionTierRequest> getTiers() {
        return tiers;
    }

    public void setTiers(List<CommissionTierRequest> tiers) {
        this.tiers = tiers;
    }

    /**
     * Commission Tier Request
     */
    public static class CommissionTierRequest {
        
        @NotNull(message = "Minimum order value is required")
        @DecimalMin(value = "0", message = "Minimum order value cannot be negative")
        private BigDecimal minOrderValue;

        @DecimalMin(value = "0", message = "Maximum order value cannot be negative")
        private BigDecimal maxOrderValue;

        @NotNull(message = "Commission percentage is required")
        @DecimalMin(value = "0", message = "Commission percentage cannot be negative")
        @DecimalMax(value = "100", message = "Commission percentage cannot exceed 100")
        private BigDecimal commissionPercentage;

        private Integer displayOrder;

        public CommissionTierRequest() {
        }

        public BigDecimal getMinOrderValue() {
            return minOrderValue;
        }

        public void setMinOrderValue(BigDecimal minOrderValue) {
            this.minOrderValue = minOrderValue;
        }

        public BigDecimal getMaxOrderValue() {
            return maxOrderValue;
        }

        public void setMaxOrderValue(BigDecimal maxOrderValue) {
            this.maxOrderValue = maxOrderValue;
        }

        public BigDecimal getCommissionPercentage() {
            return commissionPercentage;
        }

        public void setCommissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

    /**
     * Partner Commission Override Request
     */
    public static class PartnerOverrideRequest {

        @NotNull(message = "Partner ID is required")
        private UUID partnerId;

        @NotNull(message = "Commission percentage is required")
        @DecimalMin(value = "0", message = "Commission percentage cannot be negative")
        @DecimalMax(value = "100", message = "Commission percentage cannot exceed 100")
        private BigDecimal commissionPercentage;

        private String reason;

        public PartnerOverrideRequest() {
        }

        public UUID getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(UUID partnerId) {
            this.partnerId = partnerId;
        }

        public BigDecimal getCommissionPercentage() {
            return commissionPercentage;
        }

        public void setCommissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
