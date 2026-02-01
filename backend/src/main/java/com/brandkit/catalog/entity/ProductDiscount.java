package com.brandkit.catalog.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Product Discount Entity
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
@Entity
@Table(name = "product_discounts")
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_name", length = 100)
    private String discountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountStatus status = DiscountStatus.PENDING;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private ZonedDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disabled_by")
    private User disabledBy;

    @Column(name = "disabled_at")
    private ZonedDateTime disabledAt;

    @Column(name = "disabled_reason", columnDefinition = "TEXT")
    private String disabledReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Check if the discount is currently active
     */
    public boolean isActive() {
        if (status != DiscountStatus.APPROVED) {
            return false;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (startDate != null && startDate.isAfter(now)) {
            return false;
        }
        return endDate == null || endDate.isAfter(now);
    }

    public UUID getId() {
        return this.id;
    }
    public Product getProduct() {
        return this.product;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
    }
    public String getDiscountName() {
        return this.discountName;
    }
    public DiscountStatus getStatus() {
        return this.status;
    }
    public ZonedDateTime getStartDate() {
        return this.startDate;
    }
    public ZonedDateTime getEndDate() {
        return this.endDate;
    }
    public User getApprovedBy() {
        return this.approvedBy;
    }
    public ZonedDateTime getApprovedAt() {
        return this.approvedAt;
    }
    public User getDisabledBy() {
        return this.disabledBy;
    }
    public ZonedDateTime getDisabledAt() {
        return this.disabledAt;
    }
    public String getDisabledReason() {
        return this.disabledReason;
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
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }
    public void setStatus(DiscountStatus status) {
        this.status = status;
    }
    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    public void setApprovedAt(ZonedDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    public void setDisabledBy(User disabledBy) {
        this.disabledBy = disabledBy;
    }
    public void setDisabledAt(ZonedDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public ProductDiscount() {
    }
    public ProductDiscount(UUID id, Product product, Partner partner, BigDecimal discountPercentage, String discountName, DiscountStatus status, ZonedDateTime startDate, ZonedDateTime endDate, User approvedBy, ZonedDateTime approvedAt, User disabledBy, ZonedDateTime disabledAt, String disabledReason, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.partner = partner;
        this.discountPercentage = discountPercentage;
        this.discountName = discountName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.disabledBy = disabledBy;
        this.disabledAt = disabledAt;
        this.disabledReason = disabledReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static ProductDiscountBuilder builder() {
        return new ProductDiscountBuilder();
    }

    public static class ProductDiscountBuilder {
        private UUID id;
        private Product product;
        private Partner partner;
        private BigDecimal discountPercentage;
        private String discountName;
        private DiscountStatus status = DiscountStatus.PENDING;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private User approvedBy;
        private ZonedDateTime approvedAt;
        private User disabledBy;
        private ZonedDateTime disabledAt;
        private String disabledReason;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        ProductDiscountBuilder() {
        }

        public ProductDiscountBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProductDiscountBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public ProductDiscountBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public ProductDiscountBuilder discountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public ProductDiscountBuilder discountName(String discountName) {
            this.discountName = discountName;
            return this;
        }

        public ProductDiscountBuilder status(DiscountStatus status) {
            this.status = status;
            return this;
        }

        public ProductDiscountBuilder startDate(ZonedDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public ProductDiscountBuilder endDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public ProductDiscountBuilder approvedBy(User approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public ProductDiscountBuilder approvedAt(ZonedDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public ProductDiscountBuilder disabledBy(User disabledBy) {
            this.disabledBy = disabledBy;
            return this;
        }

        public ProductDiscountBuilder disabledAt(ZonedDateTime disabledAt) {
            this.disabledAt = disabledAt;
            return this;
        }

        public ProductDiscountBuilder disabledReason(String disabledReason) {
            this.disabledReason = disabledReason;
            return this;
        }

        public ProductDiscountBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProductDiscountBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ProductDiscount build() {
            ProductDiscount instance = new ProductDiscount();
            instance.id = this.id;
            instance.product = this.product;
            instance.partner = this.partner;
            instance.discountPercentage = this.discountPercentage;
            instance.discountName = this.discountName;
            instance.status = this.status;
            instance.startDate = this.startDate;
            instance.endDate = this.endDate;
            instance.approvedBy = this.approvedBy;
            instance.approvedAt = this.approvedAt;
            instance.disabledBy = this.disabledBy;
            instance.disabledAt = this.disabledAt;
            instance.disabledReason = this.disabledReason;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
