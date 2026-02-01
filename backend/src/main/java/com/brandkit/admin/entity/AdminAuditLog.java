package com.brandkit.admin.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Admin Audit Log Entity
 * Immutable audit trail for all admin actions
 * 
 * FRD-006 FR-75: System Logs
 * FRD-006 NFR-129: All admin actions recorded in audit log (immutable)
 */
@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_info", columnDefinition = "jsonb")
    private Map<String, Object> additionalInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Factory method for creating audit log entries
     */
    public static AdminAuditLog create(
            User admin,
            String actionType,
            String entityType,
            UUID entityId,
            Map<String, Object> oldValues,
            Map<String, Object> newValues) {
        return AdminAuditLog.builder()
                .admin(admin)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .oldValues(oldValues)
                .newValues(newValues)
                .build();
    }

    public UUID getId() {
        return this.id;
    }
    public User getAdmin() {
        return this.admin;
    }
    public String getActionType() {
        return this.actionType;
    }
    public String getEntityType() {
        return this.entityType;
    }
    public UUID getEntityId() {
        return this.entityId;
    }
    public Map<String, Object> getOldValues() {
        return this.oldValues;
    }
    public Map<String, Object> getNewValues() {
        return this.newValues;
    }
    public String getIpAddress() {
        return this.ipAddress;
    }
    public String getUserAgent() {
        return this.userAgent;
    }
    public Map<String, Object> getAdditionalInfo() {
        return this.additionalInfo;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    public void setOldValues(Map<String, Object> oldValues) {
        this.oldValues = oldValues;
    }
    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public AdminAuditLog() {
    }
    public AdminAuditLog(UUID id, User admin, String actionType, String entityType, UUID entityId, Map<String, Object> oldValues, Map<String, Object> newValues, String ipAddress, String userAgent, Map<String, Object> additionalInfo, ZonedDateTime createdAt) {
        this.id = id;
        this.admin = admin;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.additionalInfo = additionalInfo;
        this.createdAt = createdAt;
    }
    public static AdminAuditLogBuilder builder() {
        return new AdminAuditLogBuilder();
    }

    public static class AdminAuditLogBuilder {
        private UUID id;
        private User admin;
        private String actionType;
        private String entityType;
        private UUID entityId;
        private Map<String, Object> oldValues;
        private Map<String, Object> newValues;
        private String ipAddress;
        private String userAgent;
        private Map<String, Object> additionalInfo;
        private ZonedDateTime createdAt;

        AdminAuditLogBuilder() {
        }

        public AdminAuditLogBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AdminAuditLogBuilder admin(User admin) {
            this.admin = admin;
            return this;
        }

        public AdminAuditLogBuilder actionType(String actionType) {
            this.actionType = actionType;
            return this;
        }

        public AdminAuditLogBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public AdminAuditLogBuilder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }

        public AdminAuditLogBuilder oldValues(Map<String, Object> oldValues) {
            this.oldValues = oldValues;
            return this;
        }

        public AdminAuditLogBuilder newValues(Map<String, Object> newValues) {
            this.newValues = newValues;
            return this;
        }

        public AdminAuditLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public AdminAuditLogBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AdminAuditLogBuilder additionalInfo(Map<String, Object> additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public AdminAuditLogBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AdminAuditLog build() {
            AdminAuditLog instance = new AdminAuditLog();
            instance.id = this.id;
            instance.admin = this.admin;
            instance.actionType = this.actionType;
            instance.entityType = this.entityType;
            instance.entityId = this.entityId;
            instance.oldValues = this.oldValues;
            instance.newValues = this.newValues;
            instance.ipAddress = this.ipAddress;
            instance.userAgent = this.userAgent;
            instance.additionalInfo = this.additionalInfo;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
