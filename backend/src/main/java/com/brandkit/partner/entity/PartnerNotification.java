package com.brandkit.partner.entity;

import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.Order;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Partner Notification Entity - FRD-005 FR-53
 * In-app notifications for partners
 */
@Entity
@Table(name = "partner_notifications", indexes = {
    @Index(name = "idx_partner_notifications_partner_id", columnList = "partner_id"),
    @Index(name = "idx_partner_notifications_is_read", columnList = "partner_id, is_read"),
    @Index(name = "idx_partner_notifications_created_at", columnList = "created_at")
})
public class PartnerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public NotificationType getNotificationType() {
        return this.notificationType;
    }
    public String getTitle() {
        return this.title;
    }
    public String getMessage() {
        return this.message;
    }
    public Order getOrder() {
        return this.order;
    }
    public Boolean getIsRead() {
        return this.isRead;
    }
    public OffsetDateTime getReadAt() {
        return this.readAt;
    }
    public String getMetadata() {
        return this.metadata;
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
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    public void setReadAt(OffsetDateTime readAt) {
        this.readAt = readAt;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public PartnerNotification() {
    }
    public PartnerNotification(UUID id, Partner partner, NotificationType notificationType, String title, String message, Order order, Boolean isRead, OffsetDateTime readAt, String metadata, OffsetDateTime createdAt) {
        this.id = id;
        this.partner = partner;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.order = order;
        this.isRead = isRead;
        this.readAt = readAt;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }
    public static PartnerNotificationBuilder builder() {
        return new PartnerNotificationBuilder();
    }

    public static class PartnerNotificationBuilder {
        private UUID id;
        private Partner partner;
        private NotificationType notificationType;
        private String title;
        private String message;
        private Order order;
        private Boolean isRead = false;
        private OffsetDateTime readAt;
        private String metadata;
        private OffsetDateTime createdAt;

        PartnerNotificationBuilder() {
        }

        public PartnerNotificationBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PartnerNotificationBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public PartnerNotificationBuilder notificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public PartnerNotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PartnerNotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public PartnerNotificationBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public PartnerNotificationBuilder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public PartnerNotificationBuilder readAt(OffsetDateTime readAt) {
            this.readAt = readAt;
            return this;
        }

        public PartnerNotificationBuilder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public PartnerNotificationBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PartnerNotification build() {
            PartnerNotification instance = new PartnerNotification();
            instance.id = this.id;
            instance.partner = this.partner;
            instance.notificationType = this.notificationType;
            instance.title = this.title;
            instance.message = this.message;
            instance.order = this.order;
            instance.isRead = this.isRead;
            instance.readAt = this.readAt;
            instance.metadata = this.metadata;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
