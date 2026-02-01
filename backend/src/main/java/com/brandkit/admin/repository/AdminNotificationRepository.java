package com.brandkit.admin.repository;

import com.brandkit.admin.entity.AdminNotification;
import com.brandkit.admin.entity.AdminNotification.NotificationPriority;
import com.brandkit.admin.entity.AdminNotification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AdminNotification entity
 * 
 * FRD-006 FR-77: Admin Notifications
 */
@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {

    /**
     * Find unread notifications
     */
    Page<AdminNotification> findByIsReadFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find all notifications ordered by creation date
     */
    Page<AdminNotification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find notifications by type
     */
    Page<AdminNotification> findByNotificationTypeOrderByCreatedAtDesc(
            NotificationType type, Pageable pageable);

    /**
     * Find notifications by priority
     */
    Page<AdminNotification> findByPriorityOrderByCreatedAtDesc(
            NotificationPriority priority, Pageable pageable);

    /**
     * Find critical unread notifications
     */
    List<AdminNotification> findByPriorityAndIsReadFalseOrderByCreatedAtDesc(
            NotificationPriority priority);

    /**
     * Count unread notifications
     */
    long countByIsReadFalse();

    /**
     * Count unread critical notifications
     */
    long countByPriorityAndIsReadFalse(NotificationPriority priority);

    /**
     * Find recent notifications (last 20)
     */
    List<AdminNotification> findTop20ByOrderByCreatedAtDesc();

    /**
     * Find unread notifications for dashboard
     */
    List<AdminNotification> findTop20ByIsReadFalseOrderByPriorityDescCreatedAtDesc();

    /**
     * Mark all as read
     */
    @Modifying
    @Query("UPDATE AdminNotification n SET n.isRead = true, n.readAt = :now WHERE n.isRead = false")
    int markAllAsRead(@Param("now") ZonedDateTime now);

    /**
     * Find notifications by entity
     */
    List<AdminNotification> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, UUID entityId);

    /**
     * Delete old read notifications (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM AdminNotification n WHERE n.isRead = true AND n.readAt < :before")
    int deleteOldReadNotifications(@Param("before") ZonedDateTime before);
}
