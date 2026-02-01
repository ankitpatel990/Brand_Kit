package com.brandkit.catalog.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Discount Audit Log Entity
 * FRD-002 Sub-Prompt 7: Discount audit trail
 */
@Entity(name = "CatalogDiscountAuditLog")
@Table(name = "discount_audit_log")
public class DiscountAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", nullable = false)
    private ProductDiscount discount;

    @Column(nullable = false, length = 50)
    private String action; // CREATED, APPROVED, DISABLED, UPDATED, EXPIRED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "performed_by_role", nullable = false)
    private UserType performedByRole;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private Map<String, Object> oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private Map<String, Object> newValue;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    public UUID getId() {
        return this.id;
    }
    public ProductDiscount getDiscount() {
        return this.discount;
    }
    public String getAction() {
        return this.action;
    }
    public User getPerformedBy() {
        return this.performedBy;
    }
    public UserType getPerformedByRole() {
        return this.performedByRole;
    }
    public Map<String, Object> getOldValue() {
        return this.oldValue;
    }
    public Map<String, Object> getNewValue() {
        return this.newValue;
    }
    public String getReason() {
        return this.reason;
    }
    public String getIpAddress() {
        return this.ipAddress;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setDiscount(ProductDiscount discount) {
        this.discount = discount;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }
    public void setPerformedByRole(UserType performedByRole) {
        this.performedByRole = performedByRole;
    }
    public void setOldValue(Map<String, Object> oldValue) {
        this.oldValue = oldValue;
    }
    public void setNewValue(Map<String, Object> newValue) {
        this.newValue = newValue;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public DiscountAuditLog() {
    }
    public DiscountAuditLog(UUID id, ProductDiscount discount, String action, User performedBy, UserType performedByRole, Map<String, Object> oldValue, Map<String, Object> newValue, String reason, String ipAddress, ZonedDateTime createdAt) {
        this.id = id;
        this.discount = discount;
        this.action = action;
        this.performedBy = performedBy;
        this.performedByRole = performedByRole;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.reason = reason;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }
    public static DiscountAuditLogBuilder builder() {
        return new DiscountAuditLogBuilder();
    }

    public static class DiscountAuditLogBuilder {
        private UUID id;
        private ProductDiscount discount;
        private String action;
        private User performedBy;
        private UserType performedByRole;
        private Map<String, Object> oldValue;
        private Map<String, Object> newValue;
        private String reason;
        private String ipAddress;
        private ZonedDateTime createdAt;

        DiscountAuditLogBuilder() {
        }

        public DiscountAuditLogBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DiscountAuditLogBuilder discount(ProductDiscount discount) {
            this.discount = discount;
            return this;
        }

        public DiscountAuditLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public DiscountAuditLogBuilder performedBy(User performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public DiscountAuditLogBuilder performedByRole(UserType performedByRole) {
            this.performedByRole = performedByRole;
            return this;
        }

        public DiscountAuditLogBuilder oldValue(Map<String, Object> oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public DiscountAuditLogBuilder newValue(Map<String, Object> newValue) {
            this.newValue = newValue;
            return this;
        }

        public DiscountAuditLogBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public DiscountAuditLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public DiscountAuditLogBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DiscountAuditLog build() {
            DiscountAuditLog instance = new DiscountAuditLog();
            instance.id = this.id;
            instance.discount = this.discount;
            instance.action = this.action;
            instance.performedBy = this.performedBy;
            instance.performedByRole = this.performedByRole;
            instance.oldValue = this.oldValue;
            instance.newValue = this.newValue;
            instance.reason = this.reason;
            instance.ipAddress = this.ipAddress;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
