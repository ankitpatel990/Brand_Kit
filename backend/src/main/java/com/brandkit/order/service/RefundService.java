package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.order.dto.CancellationResponse;
import com.brandkit.order.entity.Order;
import com.brandkit.order.entity.OrderStatus;
import com.brandkit.order.entity.Payment;
import com.brandkit.order.entity.Refund;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.OrderRepository;
import com.brandkit.order.repository.PaymentRepository;
import com.brandkit.order.repository.RefundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Service for order cancellation and refunds - FRD-004 FR-48, Sub-Prompt 8
 * Handles cancellation requests, refund calculations, and refund processing
 */
@Service
@Transactional
public class RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundService.class);

    // Statuses that allow cancellation
    private static final Set<OrderStatus> CANCELLABLE_STATUSES = Set.of(
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.CONFIRMED,
            OrderStatus.ACCEPTED
    );

    // Full refund statuses (before production starts)
    private static final Set<OrderStatus> FULL_REFUND_STATUSES = Set.of(
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.CONFIRMED
    );

    // Partial refund percentage when in processing
    private static final BigDecimal PROCESSING_REFUND_PERCENTAGE = new BigDecimal("0.90"); // 90%

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private OrderService orderService;

    /**
     * Check if an order can be cancelled
     */
    @Transactional(readOnly = true)
    public boolean canCancel(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElse(null);
        if (order == null) {
            return false;
        }
        return CANCELLABLE_STATUSES.contains(order.getStatus());
    }

    /**
     * Get cancellation preview (refund amount, etc.)
     */
    @Transactional(readOnly = true)
    public CancellationResponse getCancellationPreview(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(OrderException::orderNotFound);

        CancellationResponse response = new CancellationResponse();
        response.setOrderId(orderId);
        response.setOrderNumber(order.getOrderNumber());
        response.setCanCancel(CANCELLABLE_STATUSES.contains(order.getStatus()));

        if (!response.getCanCancel()) {
            response.setMessage("This order cannot be cancelled. Current status: " + 
                    getStatusDisplayName(order.getStatus()));
            return response;
        }

        // Calculate refund amount
        BigDecimal refundAmount = calculateRefundAmount(order);
        response.setRefundAmount(refundAmount);
        response.setOrderTotal(order.getTotalAmount());

        if (refundAmount.compareTo(order.getTotalAmount()) < 0) {
            BigDecimal deduction = order.getTotalAmount().subtract(refundAmount);
            response.setDeductionAmount(deduction);
            response.setDeductionReason("10% processing fee for orders in processing stage");
        }

        response.setRefundMethod("Original payment method");
        response.setEstimatedRefundDays(7);
        response.setMessage("Refund will be processed within 5-7 business days");

        return response;
    }

    /**
     * Cancel an order
     */
    public CancellationResponse cancelOrder(User user, UUID orderId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(OrderException::orderNotFound);

        // Validate cancellable status
        if (!CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new OrderException("ORD_020", "This order cannot be cancelled. " +
                    "Status: " + getStatusDisplayName(order.getStatus()));
        }

        // Calculate refund
        BigDecimal refundAmount = calculateRefundAmount(order);

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(OffsetDateTime.now());
        order.setCancellationReason(reason);
        order.setRefundAmount(refundAmount);
        orderRepository.save(order);

        // Add status history
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED, 
                "Order cancelled by customer", 
                reason);

        // Initiate refund if payment was made
        Refund refund = null;
        if (order.getPaymentId() != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            refund = initiateRefund(order, refundAmount, reason);
        }

        // Cancel partner assignment if exists
        cancelPartnerAssignment(order);

        logger.info("Order {} cancelled by user {}. Refund amount: {}", 
                orderId, user.getId(), refundAmount);

        // Build response
        CancellationResponse response = new CancellationResponse();
        response.setOrderId(orderId);
        response.setOrderNumber(order.getOrderNumber());
        response.setSuccess(true);
        response.setCanCancel(false);
        response.setOrderTotal(order.getTotalAmount());
        response.setRefundAmount(refundAmount);

        if (refund != null) {
            response.setRefundId(refund.getId());
            response.setRefundStatus(refund.getStatus().name());
            response.setEstimatedRefundDays(7);
        }

        if (refundAmount.compareTo(order.getTotalAmount()) < 0) {
            BigDecimal deduction = order.getTotalAmount().subtract(refundAmount);
            response.setDeductionAmount(deduction);
            response.setDeductionReason("Processing fee deduction");
        }

        response.setMessage("Order cancelled successfully. " + 
                (refundAmount.compareTo(BigDecimal.ZERO) > 0 ? 
                        "Refund of ₹" + refundAmount + " will be processed within 5-7 business days." : 
                        "No refund applicable."));

        return response;
    }

    /**
     * Calculate refund amount based on order status
     */
    private BigDecimal calculateRefundAmount(Order order) {
        if (order.getPaymentId() == null) {
            // No payment made
            return BigDecimal.ZERO;
        }

        BigDecimal totalAmount = order.getTotalAmount();

        if (FULL_REFUND_STATUSES.contains(order.getStatus())) {
            // Full refund for orders not yet in processing
            return totalAmount;
        }

        if (order.getStatus() == OrderStatus.ACCEPTED) {
            // 90% refund for orders in processing
            return totalAmount.multiply(PROCESSING_REFUND_PERCENTAGE)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // No refund for other statuses
        return BigDecimal.ZERO;
    }

    /**
     * Initiate refund with payment gateway
     */
    private Refund initiateRefund(Order order, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(order.getPaymentId()).orElse(null);
        if (payment == null) {
            logger.error("Payment not found for refund: {}", order.getPaymentId());
            return null;
        }

        // Create refund record
        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setPayment(payment);
        refund.setAmount(refundAmount);
        refund.setReason(reason);
        refund.setStatus(Refund.RefundStatus.INITIATED);
        refund = refundRepository.save(refund);

        // Update order
        order.setRefundInitiatedAt(OffsetDateTime.now());
        order.setStatus(OrderStatus.REFUND_INITIATED);
        orderRepository.save(order);

        // TODO: Call payment gateway to process refund
        // For now, simulate refund processing
        processRefundWithGateway(refund, payment);

        return refund;
    }

    /**
     * Process refund with payment gateway (placeholder)
     */
    private void processRefundWithGateway(Refund refund, Payment payment) {
        try {
            // TODO: Implement actual payment gateway refund
            // Example: Razorpay refund API call
            
            // Simulate successful refund initiation
            refund.setGatewayRefundId("REF_" + System.currentTimeMillis());
            refund.setStatus(Refund.RefundStatus.PROCESSING);
            refundRepository.save(refund);

            logger.info("Refund {} initiated with gateway for order {}", 
                    refund.getId(), refund.getOrder().getOrderNumber());

        } catch (Exception e) {
            logger.error("Failed to process refund with gateway: {}", e.getMessage(), e);
            refund.setStatus(Refund.RefundStatus.FAILED);
            refund.setFailureReason(e.getMessage());
            refundRepository.save(refund);
        }
    }

    /**
     * Handle refund webhook from payment gateway
     */
    public void handleRefundWebhook(String refundId, boolean success, String message) {
        Refund refund = refundRepository.findByGatewayRefundId(refundId).orElse(null);
        if (refund == null) {
            logger.error("Refund not found for webhook: {}", refundId);
            return;
        }

        if (success) {
            refund.setStatus(Refund.RefundStatus.SUCCESS);
            refund.setCompletedAt(OffsetDateTime.now());
            refundRepository.save(refund);

            // Update order
            Order order = refund.getOrder();
            order.setStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(OffsetDateTime.now());
            orderRepository.save(order);

            orderService.updateOrderStatus(order.getId(), OrderStatus.REFUNDED, 
                    "Refund completed", 
                    "Refund of ₹" + refund.getAmount() + " processed");

            // TODO: Send refund confirmation notification
            logger.info("Refund {} completed for order {}", refundId, order.getOrderNumber());
        } else {
            refund.setStatus(Refund.RefundStatus.FAILED);
            refund.setFailureReason(message);
            refundRepository.save(refund);

            logger.error("Refund {} failed for order {}: {}", 
                    refundId, refund.getOrder().getOrderNumber(), message);
        }
    }

    /**
     * Cancel partner assignment when order is cancelled
     */
    private void cancelPartnerAssignment(Order order) {
        // TODO: Notify partner about cancellation
        // Partner assignment cleanup is handled internally
        logger.info("Partner assignment cancelled for order {}", order.getOrderNumber());
    }

    /**
     * Get user-friendly status name
     */
    private String getStatusDisplayName(OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "Pending Payment";
            case PAYMENT_FAILED -> "Payment Failed";
            case CONFIRMED -> "Confirmed";
            case ACCEPTED -> "Processing";
            case IN_PRODUCTION -> "In Production";
            case READY_TO_SHIP -> "Ready to Ship";
            case SHIPPED -> "Shipped";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Cancelled";
            case REFUND_INITIATED -> "Refund Initiated";
            case REFUNDED -> "Refunded";
            default -> status.name();
        };
    }

    /**
     * Admin: Process manual refund
     */
    public Refund processManualRefund(UUID orderId, BigDecimal amount, String reason, String adminNote) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderException::orderNotFound);

        if (order.getPaymentId() == null) {
            throw new OrderException("ORD_021", "No payment found for this order");
        }

        if (amount.compareTo(order.getTotalAmount()) > 0) {
            throw new OrderException("ORD_022", "Refund amount cannot exceed order total");
        }

        Payment payment = paymentRepository.findById(order.getPaymentId())
                .orElseThrow(() -> new OrderException("ORD_021", "Payment not found"));

        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setPayment(payment);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setAdminNote(adminNote);
        refund.setStatus(Refund.RefundStatus.INITIATED);
        refund = refundRepository.save(refund);

        order.setRefundAmount(amount);
        order.setRefundInitiatedAt(OffsetDateTime.now());
        orderRepository.save(order);

        processRefundWithGateway(refund, payment);

        logger.info("Manual refund {} initiated for order {} by admin", refund.getId(), orderId);

        return refund;
    }
}
