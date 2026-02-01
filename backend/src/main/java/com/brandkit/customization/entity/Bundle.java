package com.brandkit.customization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bundle Entity
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 * 
 * Represents a collection of customized products grouped together.
 * Maximum 10 products per bundle.
 */
@Entity
@Table(name = "bundles")
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "bundle_name", nullable = false, length = 255)
    private String bundleName;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "product_count", nullable = false)
    private Integer productCount = 0;

    @Column(name = "status", length = 50)
    private String status = "draft"; // draft, completed

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BundleItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public void addItem(BundleItem item) {
        items.add(item);
        item.setBundle(this);
        productCount = items.size();
        recalculateTotal();
    }

    public void removeItem(BundleItem item) {
        items.remove(item);
        item.setBundle(null);
        productCount = items.size();
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalPrice = items.stream()
                .map(BundleItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() {
        return this.id;
    }
    public UUID getUserId() {
        return this.userId;
    }
    public String getBundleName() {
        return this.bundleName;
    }
    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }
    public Integer getProductCount() {
        return this.productCount;
    }
    public String getStatus() {
        return this.status;
    }
    public List<BundleItem> getItems() {
        return this.items;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setItems(List<BundleItem> items) {
        this.items = items;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Bundle() {
    }
    public Bundle(UUID id, UUID userId, String bundleName, BigDecimal totalPrice, Integer productCount, String status, List<BundleItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bundleName = bundleName;
        this.totalPrice = totalPrice;
        this.productCount = productCount;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static BundleBuilder builder() {
        return new BundleBuilder();
    }

    public static class BundleBuilder {
        private UUID id;
        private UUID userId;
        private String bundleName;
        private BigDecimal totalPrice;
        private Integer productCount = 0;
        private String status = "draft";
        private List<BundleItem> items = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        BundleBuilder() {
        }

        public BundleBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public BundleBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public BundleBuilder bundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        public BundleBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BundleBuilder productCount(Integer productCount) {
            this.productCount = productCount;
            return this;
        }

        public BundleBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BundleBuilder items(List<BundleItem> items) {
            this.items = items;
            return this;
        }

        public BundleBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BundleBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Bundle build() {
            Bundle instance = new Bundle();
            instance.id = this.id;
            instance.userId = this.userId;
            instance.bundleName = this.bundleName;
            instance.totalPrice = this.totalPrice;
            instance.productCount = this.productCount;
            instance.status = this.status;
            instance.items = this.items;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
