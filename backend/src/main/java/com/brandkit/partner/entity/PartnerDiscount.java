package com.brandkit.partner.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.entity.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Partner Discount Entity - FRD-005 FR-64b
 * Partner-defined discounts with admin approval
 */
@Entity
@Table(name = "partner_discounts", indexes = {
    @Index(name = "idx_partner_discounts_partner_id", columnList = "partner_id"),
    @Index(name = "idx_partner_discounts_product_id", columnList = "product_id"),
    @Index(name = "idx_partner_discounts_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_partner_product_discount", columnNames = {"partner_id", "product_id"})
})
public class PartnerDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountStatus status = DiscountStatus.PENDING;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "disabled_at")
    private OffsetDateTime disabledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disabled_by")
    private User disabledBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public void approve(User admin) {
        this.status = DiscountStatus.APPROVED;
        this.approvedAt = OffsetDateTime.now();
        this.approvedBy = admin;
        this.disabledAt = null;
        this.disabledBy = null;
    }

    public void disable(User admin, String reason) {
        this.status = DiscountStatus.DISABLED;
        this.disabledAt = OffsetDateTime.now();
        this.disabledBy = admin;
        this.adminNotes = reason;
    }

    public void reject(User admin, String reason) {
        this.status = DiscountStatus.REJECTED;
        this.adminNotes = reason;
        this.approvedBy = admin;
    }

    public UUID getId() {
        return this.id;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public Product getProduct() {
        return this.product;
    }
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
    }
    public DiscountStatus getStatus() {
        return this.status;
    }
    public String getAdminNotes() {
        return this.adminNotes;
    }
    public OffsetDateTime getApprovedAt() {
        return this.approvedAt;
    }
    public User getApprovedBy() {
        return this.approvedBy;
    }
    public OffsetDateTime getDisabledAt() {
        return this.disabledAt;
    }
    public User getDisabledBy() {
        return this.disabledBy;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }
    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public void setStatus(DiscountStatus status) {
        this.status = status;
    }
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    public void setApprovedAt(OffsetDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    public void setDisabledAt(OffsetDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }
    public void setDisabledBy(User disabledBy) {
        this.disabledBy = disabledBy;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public PartnerDiscount() {
    }
    public PartnerDiscount(UUID id, Partner partner, Product product, BigDecimal discountPercentage, DiscountStatus status, String adminNotes, OffsetDateTime approvedAt, User approvedBy, OffsetDateTime disabledAt, User disabledBy, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.partner = partner;
        this.product = product;
        this.discountPercentage = discountPercentage;
        this.status = status;
        this.adminNotes = adminNotes;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.disabledAt = disabledAt;
        this.disabledBy = disabledBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static PartnerDiscountBuilder builder() {
        return new PartnerDiscountBuilder();
    }

    public static class PartnerDiscountBuilder {
        private UUID id;
        private Partner partner;
        private Product product;
        private BigDecimal discountPercentage;
        private DiscountStatus status = DiscountStatus.PENDING;
        private String adminNotes;
        private OffsetDateTime approvedAt;
        private User approvedBy;
        private OffsetDateTime disabledAt;
        private User disabledBy;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        PartnerDiscountBuilder() {
        }

        public PartnerDiscountBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PartnerDiscountBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public PartnerDiscountBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public PartnerDiscountBuilder discountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public PartnerDiscountBuilder status(DiscountStatus status) {
            this.status = status;
            return this;
        }

        public PartnerDiscountBuilder adminNotes(String adminNotes) {
            this.adminNotes = adminNotes;
            return this;
        }

        public PartnerDiscountBuilder approvedAt(OffsetDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public PartnerDiscountBuilder approvedBy(User approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public PartnerDiscountBuilder disabledAt(OffsetDateTime disabledAt) {
            this.disabledAt = disabledAt;
            return this;
        }

        public PartnerDiscountBuilder disabledBy(User disabledBy) {
            this.disabledBy = disabledBy;
            return this;
        }

        public PartnerDiscountBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PartnerDiscountBuilder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PartnerDiscount build() {
            PartnerDiscount instance = new PartnerDiscount();
            instance.id = this.id;
            instance.partner = this.partner;
            instance.product = this.product;
            instance.discountPercentage = this.discountPercentage;
            instance.status = this.status;
            instance.adminNotes = this.adminNotes;
            instance.approvedAt = this.approvedAt;
            instance.approvedBy = this.approvedBy;
            instance.disabledAt = this.disabledAt;
            instance.disabledBy = this.disabledBy;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
