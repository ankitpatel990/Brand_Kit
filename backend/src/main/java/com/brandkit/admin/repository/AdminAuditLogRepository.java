package com.brandkit.admin.repository;

import com.brandkit.admin.entity.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AdminAuditLog entity
 * 
 * FRD-006 FR-75: System Logs
 * FRD-006 NFR-129: All admin actions recorded in audit log
 */
@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, UUID> {

    /**
     * Find all logs by admin ID
     */
    Page<AdminAuditLog> findByAdminIdOrderByCreatedAtDesc(UUID adminId, Pageable pageable);

    /**
     * Find logs by action type
     */
    Page<AdminAuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType, Pageable pageable);

    /**
     * Find logs by entity type and ID
     */
    Page<AdminAuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, UUID entityId, Pageable pageable);

    /**
     * Find logs within date range
     */
    @Query("SELECT l FROM AdminAuditLog l WHERE l.createdAt BETWEEN :startDate AND :endDate ORDER BY l.createdAt DESC")
    Page<AdminAuditLog> findByDateRange(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            Pageable pageable);

    /**
     * Find recent logs for an entity
     */
    List<AdminAuditLog> findTop10ByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, UUID entityId);

    /**
     * Search logs by keyword in action or entity
     */
    @Query("SELECT l FROM AdminAuditLog l WHERE " +
           "LOWER(l.actionType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.entityType) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY l.createdAt DESC")
    Page<AdminAuditLog> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Count logs by admin in date range (for activity tracking)
     */
    @Query("SELECT COUNT(l) FROM AdminAuditLog l WHERE l.admin.id = :adminId AND l.createdAt >= :since")
    long countByAdminIdSince(@Param("adminId") UUID adminId, @Param("since") ZonedDateTime since);
}
