package com.brandkit.admin.dto;

import com.brandkit.admin.entity.CommissionConfig;
import com.brandkit.admin.entity.CommissionTier;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Response DTO for commission configuration
 * 
 * FRD-006 FR-72: Commission Configuration
 */
public class CommissionConfigResponse {

    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Boolean isActive;
    private List<CommissionTierResponse> tiers;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String createdByName;

    public CommissionConfigResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<CommissionTierResponse> getTiers() {
        return tiers;
    }

    public void setTiers(List<CommissionTierResponse> tiers) {
        this.tiers = tiers;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public static CommissionConfigResponseBuilder builder() {
        return new CommissionConfigResponseBuilder();
    }

    /**
     * Create from entity
     */
    public static CommissionConfigResponse fromEntity(CommissionConfig config) {
        return CommissionConfigResponse.builder()
                .id(config.getId())
                .name(config.getName())
                .description(config.getDescription())
                .isDefault(config.getIsDefault())
                .isActive(config.getIsActive())
                .tiers(config.getTiers().stream()
                        .map(CommissionTierResponse::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .createdByName(config.getCreatedBy() != null ? config.getCreatedBy().getFullName() : null)
                .build();
    }

    /**
     * Commission Tier Response
     */
    public static class CommissionTierResponse {
        private UUID id;
        private BigDecimal minOrderValue;
        private BigDecimal maxOrderValue;
        private BigDecimal commissionPercentage;
        private Integer displayOrder;
        private String displayString;

        public CommissionTierResponse() {
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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

        public String getDisplayString() {
            return displayString;
        }

        public void setDisplayString(String displayString) {
            this.displayString = displayString;
        }

        public static CommissionTierResponseBuilder builder() {
            return new CommissionTierResponseBuilder();
        }

        public static CommissionTierResponse fromEntity(CommissionTier tier) {
            return CommissionTierResponse.builder()
                    .id(tier.getId())
                    .minOrderValue(tier.getMinOrderValue())
                    .maxOrderValue(tier.getMaxOrderValue())
                    .commissionPercentage(tier.getCommissionPercentage())
                    .displayOrder(tier.getDisplayOrder())
                    .displayString(tier.getDisplayString())
                    .build();
        }

        public static class CommissionTierResponseBuilder {
            private UUID id;
            private BigDecimal minOrderValue;
            private BigDecimal maxOrderValue;
            private BigDecimal commissionPercentage;
            private Integer displayOrder;
            private String displayString;

            CommissionTierResponseBuilder() {
            }

            public CommissionTierResponseBuilder id(UUID id) {
                this.id = id;
                return this;
            }

            public CommissionTierResponseBuilder minOrderValue(BigDecimal minOrderValue) {
                this.minOrderValue = minOrderValue;
                return this;
            }

            public CommissionTierResponseBuilder maxOrderValue(BigDecimal maxOrderValue) {
                this.maxOrderValue = maxOrderValue;
                return this;
            }

            public CommissionTierResponseBuilder commissionPercentage(BigDecimal commissionPercentage) {
                this.commissionPercentage = commissionPercentage;
                return this;
            }

            public CommissionTierResponseBuilder displayOrder(Integer displayOrder) {
                this.displayOrder = displayOrder;
                return this;
            }

            public CommissionTierResponseBuilder displayString(String displayString) {
                this.displayString = displayString;
                return this;
            }

            public CommissionTierResponse build() {
                CommissionTierResponse instance = new CommissionTierResponse();
                instance.id = this.id;
                instance.minOrderValue = this.minOrderValue;
                instance.maxOrderValue = this.maxOrderValue;
                instance.commissionPercentage = this.commissionPercentage;
                instance.displayOrder = this.displayOrder;
                instance.displayString = this.displayString;
                return instance;
            }
        }
    }

    public static class CommissionConfigResponseBuilder {
        private UUID id;
        private String name;
        private String description;
        private Boolean isDefault;
        private Boolean isActive;
        private List<CommissionTierResponse> tiers;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private String createdByName;

        CommissionConfigResponseBuilder() {
        }

        public CommissionConfigResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CommissionConfigResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CommissionConfigResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CommissionConfigResponseBuilder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public CommissionConfigResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CommissionConfigResponseBuilder tiers(List<CommissionTierResponse> tiers) {
            this.tiers = tiers;
            return this;
        }

        public CommissionConfigResponseBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommissionConfigResponseBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CommissionConfigResponseBuilder createdByName(String createdByName) {
            this.createdByName = createdByName;
            return this;
        }

        public CommissionConfigResponse build() {
            CommissionConfigResponse instance = new CommissionConfigResponse();
            instance.id = this.id;
            instance.name = this.name;
            instance.description = this.description;
            instance.isDefault = this.isDefault;
            instance.isActive = this.isActive;
            instance.tiers = this.tiers;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            instance.createdByName = this.createdByName;
            return instance;
        }
    }
}
