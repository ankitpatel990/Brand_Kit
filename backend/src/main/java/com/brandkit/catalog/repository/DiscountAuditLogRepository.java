package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.DiscountAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Discount Audit Log Repository
 * FRD-002 Sub-Prompt 7: Discount audit trail
 */
@Repository
public interface DiscountAuditLogRepository extends JpaRepository<DiscountAuditLog, UUID> {
    
    List<DiscountAuditLog> findByDiscountIdOrderByCreatedAtDesc(UUID discountId);
    
    Page<DiscountAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<DiscountAuditLog> findByPerformedByIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
