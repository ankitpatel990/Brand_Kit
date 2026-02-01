package com.brandkit.order.repository;

import com.brandkit.order.entity.Payment;
import com.brandkit.order.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Payment entity - FRD-004 FR-42
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payment by gateway order ID
     */
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    /**
     * Find payment by gateway payment ID
     */
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

    /**
     * Find payments by order ID
     */
    List<Payment> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

    /**
     * Find latest payment for an order
     */
    Optional<Payment> findFirstByOrderIdOrderByCreatedAtDesc(UUID orderId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find expired pending payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.expiresAt < :now")
    List<Payment> findExpiredPendingPayments(@Param("now") OffsetDateTime now);

    /**
     * Find successful payment for an order
     */
    Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
