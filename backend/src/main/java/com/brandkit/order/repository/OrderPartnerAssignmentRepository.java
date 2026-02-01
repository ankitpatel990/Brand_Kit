package com.brandkit.order.repository;

import com.brandkit.order.entity.OrderPartnerAssignment;
import com.brandkit.order.entity.PartnerOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for OrderPartnerAssignment entity (INTERNAL ONLY) - FRD-004 FR-45
 * This data is NEVER exposed to clients
 */
@Repository
public interface OrderPartnerAssignmentRepository extends JpaRepository<OrderPartnerAssignment, UUID> {

    /**
     * Find assignment by order ID
     */
    Optional<OrderPartnerAssignment> findByOrderId(UUID orderId);

    /**
     * Find assignments by partner ID
     */
    Page<OrderPartnerAssignment> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find assignments by partner ID and status
     */
    Page<OrderPartnerAssignment> findByPartnerIdAndStatusOrderByCreatedAtDesc(
            UUID partnerId, PartnerOrderStatus status, Pageable pageable);

    /**
     * Find pending assignments for a partner
     */
    List<OrderPartnerAssignment> findByPartnerIdAndStatus(UUID partnerId, PartnerOrderStatus status);

    /**
     * Count assignments by status for a partner
     */
    long countByPartnerIdAndStatus(UUID partnerId, PartnerOrderStatus status);
}
