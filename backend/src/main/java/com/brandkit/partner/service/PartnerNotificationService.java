package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.Order;
import com.brandkit.partner.dto.NotificationResponse;
import com.brandkit.partner.dto.NotificationResponse.NotificationDto;
import com.brandkit.partner.entity.NotificationType;
import com.brandkit.partner.entity.PartnerNotification;
import com.brandkit.partner.repository.PartnerNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Partner Notification Service - FRD-005 FR-53
 * In-app notifications for partners
 */
@Service
public class PartnerNotificationService {
    private static final Logger log = LoggerFactory.getLogger(PartnerNotificationService.class);

    @Autowired
    private PartnerNotificationRepository notificationRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Get notifications for partner
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotifications(UUID partnerId, int page, int size) {
        Page<PartnerNotification> notifications = notificationRepository
                .findByPartnerIdOrderByCreatedAtDesc(partnerId, PageRequest.of(page, size));

        long unreadCount = notificationRepository.countByPartnerIdAndIsReadFalse(partnerId);

        List<NotificationDto> dtos = notifications.getContent().stream()
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());

        return NotificationResponse.builder()
                .notifications(dtos)
                .unreadCount(unreadCount)
                .page(page)
                .size(size)
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .build();
    }

    /**
     * Get unread notification count
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID partnerId) {
        return notificationRepository.countByPartnerIdAndIsReadFalse(partnerId);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(UUID partnerId, UUID notificationId) {
        PartnerNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getPartner().getId().equals(partnerId)) {
            throw new IllegalArgumentException("Access denied");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public int markAllAsRead(UUID partnerId) {
        return notificationRepository.markAllAsReadByPartnerId(partnerId);
    }

    /**
     * Create new order notification
     */
    @Transactional
    public void createNewOrderNotification(Partner partner, Order order) {
        String productName = order.getItems().isEmpty() ? "N/A" : order.getItems().get(0).getProductName();

        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .notificationType(NotificationType.NEW_ORDER)
                .title("New Order Assigned")
                .message("New order " + order.getOrderNumber() + " for " + productName + " (" + order.getTotalQuantity() + " units)")
                .order(order)
                .build();

        notificationRepository.save(notification);
        log.info("Created new order notification for partner {}: order {}", partner.getId(), order.getOrderNumber());
    }

    /**
     * Create status update notification
     */
    @Transactional
    public void createStatusUpdateNotification(Partner partner, Order order, String status) {
        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .notificationType(NotificationType.STATUS_UPDATE)
                .title("Order Status Updated")
                .message("Order " + order.getOrderNumber() + " status updated to: " + status)
                .order(order)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Create settlement processed notification
     */
    @Transactional
    public void createSettlementNotification(Partner partner, String settlementNumber, String amount) {
        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .notificationType(NotificationType.SETTLEMENT_PROCESSED)
                .title("Settlement Processed")
                .message("Settlement " + settlementNumber + " for ₹" + amount + " has been processed")
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Create discount approval notification
     */
    @Transactional
    public void createDiscountApprovalNotification(Partner partner, String productName, boolean approved) {
        NotificationType type = approved ? NotificationType.DISCOUNT_APPROVED : NotificationType.DISCOUNT_DISABLED;
        String title = approved ? "Discount Approved" : "Discount Disabled";
        String message = approved ?
                "Your discount for " + productName + " has been approved and is now active" :
                "Your discount for " + productName + " has been disabled by admin";

        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .notificationType(type)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Create performance alert notification
     */
    @Transactional
    public void createPerformanceAlertNotification(Partner partner, String message) {
        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .notificationType(NotificationType.PERFORMANCE_ALERT)
                .title("Performance Alert")
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    private NotificationDto mapToDto(PartnerNotification notification) {
        return NotificationDto.builder()
                .id(notification.getId().toString())
                .type(notification.getNotificationType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .orderId(notification.getOrder() != null ? notification.getOrder().getId().toString() : null)
                .orderNumber(notification.getOrder() != null ? notification.getOrder().getOrderNumber() : null)
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt().format(DATETIME_FORMATTER))
                .build();
    }
}
