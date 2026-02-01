package com.brandkit.catalog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Pricing Tier Entity
 * FRD-002 FR-16: Pricing Tier Structure
 */
@Entity
@Table(name = "pricing_tiers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "tier_number"})
})
public class PricingTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "tier_number", nullable = false)
    private Integer tierNumber;

    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity;

    @Column(name = "max_quantity")
    private Integer maxQuantity; // NULL means no upper limit

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Check if quantity falls within this tier
     */
    public boolean containsQuantity(int quantity) {
        if (quantity < minQuantity) {
            return false;
        }
        return maxQuantity == null || quantity <= maxQuantity;
    }

    public UUID getId() {
        return this.id;
    }
    public Product getProduct() {
        return this.product;
    }
    public Integer getTierNumber() {
        return this.tierNumber;
    }
    public Integer getMinQuantity() {
        return this.minQuantity;
    }
    public Integer getMaxQuantity() {
        return this.maxQuantity;
    }
    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
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
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setTierNumber(Integer tierNumber) {
        this.tierNumber = tierNumber;
    }
    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }
    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public PricingTier() {
    }
    public PricingTier(UUID id, Product product, Integer tierNumber, Integer minQuantity, Integer maxQuantity, BigDecimal unitPrice, BigDecimal discountPercentage, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.tierNumber = tierNumber;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.unitPrice = unitPrice;
        this.discountPercentage = discountPercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static PricingTierBuilder builder() {
        return new PricingTierBuilder();
    }

    public static class PricingTierBuilder {
        private UUID id;
        private Product product;
        private Integer tierNumber;
        private Integer minQuantity;
        private Integer maxQuantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercentage = BigDecimal.ZERO;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        PricingTierBuilder() {
        }

        public PricingTierBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PricingTierBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public PricingTierBuilder tierNumber(Integer tierNumber) {
            this.tierNumber = tierNumber;
            return this;
        }

        public PricingTierBuilder minQuantity(Integer minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }

        public PricingTierBuilder maxQuantity(Integer maxQuantity) {
            this.maxQuantity = maxQuantity;
            return this;
        }

        public PricingTierBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public PricingTierBuilder discountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public PricingTierBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PricingTierBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PricingTier build() {
            PricingTier instance = new PricingTier();
            instance.id = this.id;
            instance.product = this.product;
            instance.tierNumber = this.tierNumber;
            instance.minQuantity = this.minQuantity;
            instance.maxQuantity = this.maxQuantity;
            instance.unitPrice = this.unitPrice;
            instance.discountPercentage = this.discountPercentage;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
