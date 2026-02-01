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
 * Admin Notification Entity
 * Admin alerts and notifications for critical platform events
 * 
 * FRD-006 FR-77: Admin Notifications
 */
@Entity
@Table(name = "admin_notifications")
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType = NotificationType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "action_url", columnDefinition = "text")
    private String actionUrl;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "read_by")
    private User readBy;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @Column(name = "dismissed_at")
    private ZonedDateTime dismissedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Notification Type Enum
     */
    public enum NotificationType {
        ORDER_REJECTION,
        SETTLEMENT_FAILED,
        LOW_PERFORMANCE,
        HIGH_VALUE_ORDER,
        SYSTEM_ERROR,
        NEW_PARTNER,
        DISCOUNT_ABUSE,
        USER_FLAGGED,
        BANK_VERIFICATION,
        GENERAL
    }

    /**
     * Notification Priority Enum
     */
    public enum NotificationPriority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(User admin) {
        this.isRead = true;
        this.readBy = admin;
        this.readAt = ZonedDateTime.now();
    }

    /**
     * Dismiss notification
     */
    public void dismiss() {
        this.dismissedAt = ZonedDateTime.now();
    }

    /**
     * Factory method for critical alerts
     */
    public static AdminNotification createCriticalAlert(
            String title,
            String message,
            String actionUrl,
            NotificationType type,
            String entityType,
            UUID entityId) {
        return AdminNotification.builder()
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .notificationType(type)
                .priority(NotificationPriority.CRITICAL)
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }

    public UUID getId() {
        return this.id;
    }
    public NotificationType getNotificationType() {
        return this.notificationType;
    }
    public NotificationPriority getPriority() {
        return this.priority;
    }
    public String getTitle() {
        return this.title;
    }
    public String getMessage() {
        return this.message;
    }
    public String getActionUrl() {
        return this.actionUrl;
    }
    public String getEntityType() {
        return this.entityType;
    }
    public UUID getEntityId() {
        return this.entityId;
    }
    public Boolean getIsRead() {
        return this.isRead;
    }
    public User getReadBy() {
        return this.readBy;
    }
    public ZonedDateTime getReadAt() {
        return this.readAt;
    }
    public ZonedDateTime getDismissedAt() {
        return this.dismissedAt;
    }
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    public void setReadBy(User readBy) {
        this.readBy = readBy;
    }
    public void setReadAt(ZonedDateTime readAt) {
        this.readAt = readAt;
    }
    public void setDismissedAt(ZonedDateTime dismissedAt) {
        this.dismissedAt = dismissedAt;
    }
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public AdminNotification() {
    }
    public AdminNotification(UUID id, NotificationType notificationType, NotificationPriority priority, String title, String message, String actionUrl, String entityType, UUID entityId, Boolean isRead, User readBy, ZonedDateTime readAt, ZonedDateTime dismissedAt, Map<String, Object> metadata, ZonedDateTime createdAt) {
        this.id = id;
        this.notificationType = notificationType;
        this.priority = priority;
        this.title = title;
        this.message = message;
        this.actionUrl = actionUrl;
        this.entityType = entityType;
        this.entityId = entityId;
        this.isRead = isRead;
        this.readBy = readBy;
        this.readAt = readAt;
        this.dismissedAt = dismissedAt;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }
    public static AdminNotificationBuilder builder() {
        return new AdminNotificationBuilder();
    }

    public static class AdminNotificationBuilder {
        private UUID id;
        private NotificationType notificationType = NotificationType.GENERAL;
        private NotificationPriority priority = NotificationPriority.MEDIUM;
        private String title;
        private String message;
        private String actionUrl;
        private String entityType;
        private UUID entityId;
        private Boolean isRead = false;
        private User readBy;
        private ZonedDateTime readAt;
        private ZonedDateTime dismissedAt;
        private Map<String, Object> metadata;
        private ZonedDateTime createdAt;

        AdminNotificationBuilder() {
        }

        public AdminNotificationBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AdminNotificationBuilder notificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public AdminNotificationBuilder priority(NotificationPriority priority) {
            this.priority = priority;
            return this;
        }

        public AdminNotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AdminNotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AdminNotificationBuilder actionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
            return this;
        }

        public AdminNotificationBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public AdminNotificationBuilder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }

        public AdminNotificationBuilder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public AdminNotificationBuilder readBy(User readBy) {
            this.readBy = readBy;
            return this;
        }

        public AdminNotificationBuilder readAt(ZonedDateTime readAt) {
            this.readAt = readAt;
            return this;
        }

        public AdminNotificationBuilder dismissedAt(ZonedDateTime dismissedAt) {
            this.dismissedAt = dismissedAt;
            return this;
        }

        public AdminNotificationBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public AdminNotificationBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AdminNotification build() {
            AdminNotification instance = new AdminNotification();
            instance.id = this.id;
            instance.notificationType = this.notificationType;
            instance.priority = this.priority;
            instance.title = this.title;
            instance.message = this.message;
            instance.actionUrl = this.actionUrl;
            instance.entityType = this.entityType;
            instance.entityId = this.entityId;
            instance.isRead = this.isRead;
            instance.readBy = this.readBy;
            instance.readAt = this.readAt;
            instance.dismissedAt = this.dismissedAt;
            instance.metadata = this.metadata;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
