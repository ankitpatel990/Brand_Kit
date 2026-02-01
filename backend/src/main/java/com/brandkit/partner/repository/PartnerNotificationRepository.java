package com.brandkit.partner.repository;

import com.brandkit.partner.entity.NotificationType;
import com.brandkit.partner.entity.PartnerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for PartnerNotification entity - FRD-005 FR-53
 */
@Repository
public interface PartnerNotificationRepository extends JpaRepository<PartnerNotification, UUID> {

    /**
     * Find notifications by partner ID
     */
    Page<PartnerNotification> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find unread notifications by partner ID
     */
    List<PartnerNotification> findByPartnerIdAndIsReadFalseOrderByCreatedAtDesc(UUID partnerId);

    /**
     * Count unread notifications by partner ID
     */
    long countByPartnerIdAndIsReadFalse(UUID partnerId);

    /**
     * Find notifications by partner ID and type
     */
    List<PartnerNotification> findByPartnerIdAndNotificationTypeOrderByCreatedAtDesc(
            UUID partnerId, NotificationType type);

    /**
     * Mark all notifications as read for partner
     */
    @Modifying
    @Query("UPDATE PartnerNotification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.partner.id = :partnerId AND n.isRead = false")
    int markAllAsReadByPartnerId(@Param("partnerId") UUID partnerId);

    /**
     * Delete old read notifications (for cleanup)
     */
    @Modifying
    @Query("DELETE FROM PartnerNotification n WHERE n.isRead = true AND n.createdAt < :cutoffDate")
    int deleteOldReadNotifications(@Param("cutoffDate") java.time.OffsetDateTime cutoffDate);
}
