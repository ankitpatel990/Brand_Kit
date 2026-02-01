package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.NotificationResponse;
import com.brandkit.partner.service.PartnerNotificationService;
import com.brandkit.partner.service.PartnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Partner Notification Controller - FRD-005 FR-53
 * In-app notifications for partners
 */
@RestController
@RequestMapping("/api/partner/notifications")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Notifications", description = "Partner notification APIs - Internal Portal Only")
public class PartnerNotificationController {

    @Autowired
    private PartnerNotificationService notificationService;
    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get notifications
     * FRD-005 FR-53: Order Notifications
     */
    @GetMapping
    @Operation(summary = "Get notifications", description = "Get partner notifications")
    public ResponseEntity<NotificationResponse> getNotifications(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        NotificationResponse response = notificationService.getNotifications(partner.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get unread count
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get unread notification count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@CurrentUser User user) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        long count = notificationService.getUnreadCount(partner.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(
            @CurrentUser User user,
            @PathVariable UUID notificationId) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        notificationService.markAsRead(partner.getId(), notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@CurrentUser User user) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        int count = notificationService.markAllAsRead(partner.getId());
        return ResponseEntity.ok(Map.of("marked", count));
    }
}
