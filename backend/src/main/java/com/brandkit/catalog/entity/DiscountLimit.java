package com.brandkit.catalog.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Discount Limit Entity
 * FRD-002 Sub-Prompt 7: Global discount limits set by admin
 */
@Entity
@Table(name = "discount_limits")
public class DiscountLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "min_discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal minDiscountPercentage = BigDecimal.ZERO;

    @Column(name = "max_discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxDiscountPercentage = new BigDecimal("25.00");

    @Enumerated(EnumType.STRING)
    @Column
    private ProductCategory category; // NULL means applies to all categories

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_by")
    private User setBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public UUID getId() {
        return this.id;
    }
    public BigDecimal getMinDiscountPercentage() {
        return this.minDiscountPercentage;
    }
    public BigDecimal getMaxDiscountPercentage() {
        return this.maxDiscountPercentage;
    }
    public ProductCategory getCategory() {
        return this.category;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public User getSetBy() {
        return this.setBy;
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
    public void setMinDiscountPercentage(BigDecimal minDiscountPercentage) {
        this.minDiscountPercentage = minDiscountPercentage;
    }
    public void setMaxDiscountPercentage(BigDecimal maxDiscountPercentage) {
        this.maxDiscountPercentage = maxDiscountPercentage;
    }
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setSetBy(User setBy) {
        this.setBy = setBy;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public DiscountLimit() {
    }
    public DiscountLimit(UUID id, BigDecimal minDiscountPercentage, BigDecimal maxDiscountPercentage, ProductCategory category, Boolean isActive, User setBy, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.minDiscountPercentage = minDiscountPercentage;
        this.maxDiscountPercentage = maxDiscountPercentage;
        this.category = category;
        this.isActive = isActive;
        this.setBy = setBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static DiscountLimitBuilder builder() {
        return new DiscountLimitBuilder();
    }

    public static class DiscountLimitBuilder {
        private UUID id;
        private BigDecimal minDiscountPercentage = BigDecimal.ZERO;
        private BigDecimal maxDiscountPercentage = new BigDecimal("25.00");
        private ProductCategory category;
        private Boolean isActive = true;
        private User setBy;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        DiscountLimitBuilder() {
        }

        public DiscountLimitBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DiscountLimitBuilder minDiscountPercentage(BigDecimal minDiscountPercentage) {
            this.minDiscountPercentage = minDiscountPercentage;
            return this;
        }

        public DiscountLimitBuilder maxDiscountPercentage(BigDecimal maxDiscountPercentage) {
            this.maxDiscountPercentage = maxDiscountPercentage;
            return this;
        }

        public DiscountLimitBuilder category(ProductCategory category) {
            this.category = category;
            return this;
        }

        public DiscountLimitBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public DiscountLimitBuilder setBy(User setBy) {
            this.setBy = setBy;
            return this;
        }

        public DiscountLimitBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DiscountLimitBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DiscountLimit build() {
            DiscountLimit instance = new DiscountLimit();
            instance.id = this.id;
            instance.minDiscountPercentage = this.minDiscountPercentage;
            instance.maxDiscountPercentage = this.maxDiscountPercentage;
            instance.category = this.category;
            instance.isActive = this.isActive;
            instance.setBy = this.setBy;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
