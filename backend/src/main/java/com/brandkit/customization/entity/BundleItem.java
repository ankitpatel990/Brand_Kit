package com.brandkit.customization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bundle Item Entity
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 * 
 * Links a customization to a bundle with quantity and pricing.
 */
@Entity
@Table(name = "bundle_items")
public class BundleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", nullable = false)
    private Bundle bundle;

    @Column(name = "customization_id", nullable = false)
    private UUID customizationId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper method to calculate subtotal
    public void calculateSubtotal() {
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateSubtotal();
    }

    public UUID getId() {
        return this.id;
    }
    public Bundle getBundle() {
        return this.bundle;
    }
    public UUID getCustomizationId() {
        return this.customizationId;
    }
    public UUID getProductId() {
        return this.productId;
    }
    public Integer getQuantity() {
        return this.quantity;
    }
    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }
    public BigDecimal getSubtotal() {
        return this.subtotal;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    public void setCustomizationId(UUID customizationId) {
        this.customizationId = customizationId;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public BundleItem() {
    }
    public BundleItem(UUID id, Bundle bundle, UUID customizationId, UUID productId, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal, LocalDateTime createdAt) {
        this.id = id;
        this.bundle = bundle;
        this.customizationId = customizationId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.createdAt = createdAt;
    }
    public static BundleItemBuilder builder() {
        return new BundleItemBuilder();
    }

    public static class BundleItemBuilder {
        private UUID id;
        private Bundle bundle;
        private UUID customizationId;
        private UUID productId;
        private Integer quantity = 1;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private LocalDateTime createdAt;

        BundleItemBuilder() {
        }

        public BundleItemBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public BundleItemBuilder bundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public BundleItemBuilder customizationId(UUID customizationId) {
            this.customizationId = customizationId;
            return this;
        }

        public BundleItemBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public BundleItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public BundleItemBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public BundleItemBuilder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public BundleItemBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BundleItem build() {
            BundleItem instance = new BundleItem();
            instance.id = this.id;
            instance.bundle = this.bundle;
            instance.customizationId = this.customizationId;
            instance.productId = this.productId;
            instance.quantity = this.quantity;
            instance.unitPrice = this.unitPrice;
            instance.subtotal = this.subtotal;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
