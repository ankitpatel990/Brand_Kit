package com.brandkit.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Commission Tier Entity
 * Individual tier in a commission configuration
 * 
 * FRD-006 FR-72: Tiered Commission by Order Value
 */
@Entity
@Table(name = "commission_tiers")
public class CommissionTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    private CommissionConfig config;

    @Column(name = "min_order_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "max_order_value", precision = 14, scale = 2)
    private BigDecimal maxOrderValue;

    @Column(name = "commission_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Check if an order value falls within this tier
     */
    public boolean contains(BigDecimal orderValue) {
        boolean aboveMin = orderValue.compareTo(minOrderValue) >= 0;
        boolean belowMax = maxOrderValue == null || orderValue.compareTo(maxOrderValue) <= 0;
        return aboveMin && belowMax;
    }

    /**
     * Get display string for this tier
     */
    public String getDisplayString() {
        if (maxOrderValue == null) {
            return String.format("₹%s+ → %s%%", minOrderValue.toPlainString(), commissionPercentage.toPlainString());
        }
        return String.format("₹%s - ₹%s → %s%%", 
                minOrderValue.toPlainString(), 
                maxOrderValue.toPlainString(), 
                commissionPercentage.toPlainString());
    }

    public UUID getId() {
        return this.id;
    }
    public CommissionConfig getConfig() {
        return this.config;
    }
    public BigDecimal getMinOrderValue() {
        return this.minOrderValue;
    }
    public BigDecimal getMaxOrderValue() {
        return this.maxOrderValue;
    }
    public BigDecimal getCommissionPercentage() {
        return this.commissionPercentage;
    }
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setConfig(CommissionConfig config) {
        this.config = config;
    }
    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }
    public void setMaxOrderValue(BigDecimal maxOrderValue) {
        this.maxOrderValue = maxOrderValue;
    }
    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public CommissionTier() {
    }
    public CommissionTier(UUID id, CommissionConfig config, BigDecimal minOrderValue, BigDecimal maxOrderValue, BigDecimal commissionPercentage, Integer displayOrder, ZonedDateTime createdAt) {
        this.id = id;
        this.config = config;
        this.minOrderValue = minOrderValue;
        this.maxOrderValue = maxOrderValue;
        this.commissionPercentage = commissionPercentage;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }
    public static CommissionTierBuilder builder() {
        return new CommissionTierBuilder();
    }

    public static class CommissionTierBuilder {
        private UUID id;
        private CommissionConfig config;
        private BigDecimal minOrderValue = BigDecimal.ZERO;
        private BigDecimal maxOrderValue;
        private BigDecimal commissionPercentage;
        private Integer displayOrder = 0;
        private ZonedDateTime createdAt;

        CommissionTierBuilder() {
        }

        public CommissionTierBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CommissionTierBuilder config(CommissionConfig config) {
            this.config = config;
            return this;
        }

        public CommissionTierBuilder minOrderValue(BigDecimal minOrderValue) {
            this.minOrderValue = minOrderValue;
            return this;
        }

        public CommissionTierBuilder maxOrderValue(BigDecimal maxOrderValue) {
            this.maxOrderValue = maxOrderValue;
            return this;
        }

        public CommissionTierBuilder commissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
            return this;
        }

        public CommissionTierBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public CommissionTierBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommissionTier build() {
            CommissionTier instance = new CommissionTier();
            instance.id = this.id;
            instance.config = this.config;
            instance.minOrderValue = this.minOrderValue;
            instance.maxOrderValue = this.maxOrderValue;
            instance.commissionPercentage = this.commissionPercentage;
            instance.displayOrder = this.displayOrder;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
