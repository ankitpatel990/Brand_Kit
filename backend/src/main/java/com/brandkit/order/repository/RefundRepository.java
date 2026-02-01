package com.brandkit.order.repository;

import com.brandkit.order.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Refund entity - FRD-004
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    /**
     * Find refunds by order ID
     */
    List<Refund> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

    /**
     * Find refund by gateway refund ID
     */
    Optional<Refund> findByGatewayRefundId(String gatewayRefundId);

    /**
     * Find refunds by status
     */
    List<Refund> findByStatus(Refund.RefundStatus status);
}
