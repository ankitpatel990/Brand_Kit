package com.brandkit.partner.repository;

import com.brandkit.partner.entity.DiscountAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for DiscountAuditLog entity - FRD-005 FR-64b
 */
@Repository
public interface PartnerDiscountAuditLogRepository extends JpaRepository<DiscountAuditLog, UUID> {

    /**
     * Find audit logs by partner
     */
    Page<DiscountAuditLog> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find audit logs by product
     */
    Page<DiscountAuditLog> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    /**
     * Find audit logs by partner and product
     */
    Page<DiscountAuditLog> findByPartnerIdAndProductIdOrderByCreatedAtDesc(
            UUID partnerId, UUID productId, Pageable pageable);
}
