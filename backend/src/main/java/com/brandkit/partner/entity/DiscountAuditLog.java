package com.brandkit.partner.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.entity.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Discount Audit Log Entity - FRD-005 FR-64b
 * Audit trail for discount changes
 */
@Entity(name = "PartnerDiscountAuditLog")
@Table(name = "partner_discount_audit_log", indexes = {
    @Index(name = "idx_partner_discount_audit_partner_id", columnList = "partner_id"),
    @Index(name = "idx_partner_discount_audit_product_id", columnList = "product_id"),
    @Index(name = "idx_partner_discount_audit_created_at", columnList = "created_at")
})
public class DiscountAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "old_discount", precision = 5, scale = 2)
    private BigDecimal oldDiscount;

    @Column(name = "new_discount", precision = 5, scale = 2)
    private BigDecimal newDiscount;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private DiscountStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private DiscountStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "changed_by_role", nullable = false, length = 20)
    private String changedByRole;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
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
    public BigDecimal getOldDiscount() {
        return this.oldDiscount;
    }
    public BigDecimal getNewDiscount() {
        return this.newDiscount;
    }
    public DiscountStatus getOldStatus() {
        return this.oldStatus;
    }
    public DiscountStatus getNewStatus() {
        return this.newStatus;
    }
    public User getChangedBy() {
        return this.changedBy;
    }
    public String getChangedByRole() {
        return this.changedByRole;
    }
    public String getReason() {
        return this.reason;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
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
    public void setOldDiscount(BigDecimal oldDiscount) {
        this.oldDiscount = oldDiscount;
    }
    public void setNewDiscount(BigDecimal newDiscount) {
        this.newDiscount = newDiscount;
    }
    public void setOldStatus(DiscountStatus oldStatus) {
        this.oldStatus = oldStatus;
    }
    public void setNewStatus(DiscountStatus newStatus) {
        this.newStatus = newStatus;
    }
    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }
    public void setChangedByRole(String changedByRole) {
        this.changedByRole = changedByRole;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public DiscountAuditLog() {
    }
    public DiscountAuditLog(UUID id, Partner partner, Product product, BigDecimal oldDiscount, BigDecimal newDiscount, DiscountStatus oldStatus, DiscountStatus newStatus, User changedBy, String changedByRole, String reason, OffsetDateTime createdAt) {
        this.id = id;
        this.partner = partner;
        this.product = product;
        this.oldDiscount = oldDiscount;
        this.newDiscount = newDiscount;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedByRole = changedByRole;
        this.reason = reason;
        this.createdAt = createdAt;
    }
    public static DiscountAuditLogBuilder builder() {
        return new DiscountAuditLogBuilder();
    }

    public static class DiscountAuditLogBuilder {
        private UUID id;
        private Partner partner;
        private Product product;
        private BigDecimal oldDiscount;
        private BigDecimal newDiscount;
        private DiscountStatus oldStatus;
        private DiscountStatus newStatus;
        private User changedBy;
        private String changedByRole;
        private String reason;
        private OffsetDateTime createdAt;

        DiscountAuditLogBuilder() {
        }

        public DiscountAuditLogBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DiscountAuditLogBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public DiscountAuditLogBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public DiscountAuditLogBuilder oldDiscount(BigDecimal oldDiscount) {
            this.oldDiscount = oldDiscount;
            return this;
        }

        public DiscountAuditLogBuilder newDiscount(BigDecimal newDiscount) {
            this.newDiscount = newDiscount;
            return this;
        }

        public DiscountAuditLogBuilder oldStatus(DiscountStatus oldStatus) {
            this.oldStatus = oldStatus;
            return this;
        }

        public DiscountAuditLogBuilder newStatus(DiscountStatus newStatus) {
            this.newStatus = newStatus;
            return this;
        }

        public DiscountAuditLogBuilder changedBy(User changedBy) {
            this.changedBy = changedBy;
            return this;
        }

        public DiscountAuditLogBuilder changedByRole(String changedByRole) {
            this.changedByRole = changedByRole;
            return this;
        }

        public DiscountAuditLogBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public DiscountAuditLogBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DiscountAuditLog build() {
            DiscountAuditLog instance = new DiscountAuditLog();
            instance.id = this.id;
            instance.partner = this.partner;
            instance.product = this.product;
            instance.oldDiscount = this.oldDiscount;
            instance.newDiscount = this.newDiscount;
            instance.oldStatus = this.oldStatus;
            instance.newStatus = this.newStatus;
            instance.changedBy = this.changedBy;
            instance.changedByRole = this.changedByRole;
            instance.reason = this.reason;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
