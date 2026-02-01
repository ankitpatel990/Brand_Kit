package com.brandkit.order.service;

import com.brandkit.order.dto.DeliveryTrackingResponse;
import com.brandkit.order.entity.*;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.OrderRepository;
import com.brandkit.order.repository.PinCodeServiceabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for delivery partner integration - FRD-004 Sub-Prompt 7
 * Handles serviceability checks, tracking, and delivery updates
 */
@Service
@Transactional
public class DeliveryIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryIntegrationService.class);

    /**
     * Result object for estimated delivery calculation
     */
    public static class EstimatedDeliveryResult {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public EstimatedDeliveryResult(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }

        public String getFormattedRange() {
            return startDate.toString() + " to " + endDate.toString();
        }
    }

    // Standard delivery: 5-7 business days
    private static final int STANDARD_DELIVERY_MIN_DAYS = 5;
    private static final int STANDARD_DELIVERY_MAX_DAYS = 7;

    // Express delivery: 2-3 business days
    private static final int EXPRESS_DELIVERY_MIN_DAYS = 2;
    private static final int EXPRESS_DELIVERY_MAX_DAYS = 3;

    @Value("${brandkit.delivery.standard.cost:0}")
    private BigDecimal standardDeliveryCost;

    @Value("${brandkit.delivery.express.cost:150}")
    private BigDecimal expressDeliveryCost;

    @Value("${brandkit.delivery.free.threshold:1000}")
    private BigDecimal freeDeliveryThreshold;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PinCodeServiceabilityRepository pinCodeRepository;
    @Autowired
    private OrderService orderService;

    /**
     * Check if a PIN code is serviceable
     */
    @Transactional(readOnly = true)
    public boolean isPinCodeServiceable(String pinCode) {
        return pinCodeRepository.findByPinCode(pinCode)
                .map(PinCodeServiceability::getIsServiceable)
                .orElse(true); // Default to serviceable if not in database
    }

    /**
     * Get available delivery options for a PIN code
     */
    @Transactional(readOnly = true)
    public List<DeliveryOption> getAvailableDeliveryOptions(String pinCode, BigDecimal orderTotal) {
        PinCodeServiceability serviceability = pinCodeRepository.findByPinCode(pinCode)
                .orElse(null);

        if (serviceability == null || !serviceability.getIsServiceable()) {
            throw OrderException.pinCodeNotServiceable();
        }

        if (serviceability.getExpressAvailable()) {
            return List.of(DeliveryOption.STANDARD, DeliveryOption.EXPRESS);
        }

        return List.of(DeliveryOption.STANDARD);
    }

    /**
     * Calculate delivery charges
     */
    public BigDecimal calculateDeliveryCharges(DeliveryOption deliveryOption, BigDecimal orderTotal) {
        // Free delivery for orders above threshold (standard only)
        if (deliveryOption == DeliveryOption.STANDARD && 
            orderTotal.compareTo(freeDeliveryThreshold) >= 0) {
            return BigDecimal.ZERO;
        }

        return switch (deliveryOption) {
            case STANDARD -> standardDeliveryCost;
            case EXPRESS -> expressDeliveryCost;
        };
    }

    /**
     * Calculate estimated delivery dates
     */
    public EstimatedDeliveryResult calculateEstimatedDelivery(DeliveryOption deliveryOption) {
        LocalDate today = LocalDate.now();

        return switch (deliveryOption) {
            case STANDARD -> new EstimatedDeliveryResult(
                    addBusinessDays(today, STANDARD_DELIVERY_MIN_DAYS),
                    addBusinessDays(today, STANDARD_DELIVERY_MAX_DAYS)
            );
            case EXPRESS -> new EstimatedDeliveryResult(
                    addBusinessDays(today, EXPRESS_DELIVERY_MIN_DAYS),
                    addBusinessDays(today, EXPRESS_DELIVERY_MAX_DAYS)
            );
        };
    }

    /**
     * Get tracking information for an order
     * Note: Tracking info is exposed to client without partner details
     */
    @Transactional(readOnly = true)
    public DeliveryTrackingResponse getTrackingInfo(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(OrderException::orderNotFound);

        DeliveryTrackingResponse response = new DeliveryTrackingResponse();
        response.setOrderId(orderId);
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus().name());
        response.setStatusDescription(getStatusDescription(order.getStatus()));

        if (order.getTrackingId() != null) {
            response.setTrackingId(order.getTrackingId());
            response.setCourierName(order.getCourierName());
            response.setTrackingUrl(order.getTrackingUrl());
        }

        if (order.getEstimatedDeliveryStart() != null) {
            response.setEstimatedDeliveryStart(order.getEstimatedDeliveryStart());
            response.setEstimatedDeliveryEnd(order.getEstimatedDeliveryEnd());
        }

        if (order.getActualDeliveryDate() != null) {
            response.setDeliveredOn(order.getActualDeliveryDate());
        }

        return response;
    }

    /**
     * Update tracking information (called by webhook or scheduled job)
     */
    public void updateTrackingInfo(UUID orderId, String trackingId, String courierName, String trackingUrl) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.error("Order not found for tracking update: {}", orderId);
            return;
        }

        order.setTrackingId(trackingId);
        order.setCourierName(courierName);
        order.setTrackingUrl(trackingUrl);
        orderRepository.save(order);

        logger.info("Tracking info updated for order {}: {}", orderId, trackingId);
    }

    /**
     * Mark order as shipped (called by partner or webhook)
     */
    public void markOrderShipped(UUID orderId, String trackingId, String courierName, String trackingUrl) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.error("Order not found for shipping update: {}", orderId);
            return;
        }

        order.setTrackingId(trackingId);
        order.setCourierName(courierName);
        order.setTrackingUrl(trackingUrl);
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        // Add status history
        orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED, 
                "Your order has been shipped", 
                "Tracking: " + trackingId);

        // TODO: Send shipping notification to client
        logger.info("Order {} marked as shipped with tracking {}", orderId, trackingId);
    }

    /**
     * Mark order as out for delivery (called by webhook)
     */
    public void markOutForDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return;
        }

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);

        orderService.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY, 
                "Your order is out for delivery", 
                null);

        // TODO: Send notification
        logger.info("Order {} is out for delivery", orderId);
    }

    /**
     * Mark order as delivered (called by webhook or manual confirmation)
     */
    public void markOrderDelivered(UUID orderId, LocalDate deliveryDate) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return;
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryDate(deliveryDate != null ? deliveryDate : LocalDate.now());
        orderRepository.save(order);

        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED, 
                "Order delivered successfully", 
                "Delivered on " + order.getActualDeliveryDate());

        // TODO: Send delivery confirmation notification
        // TODO: Request delivery feedback/review
        logger.info("Order {} delivered on {}", orderId, order.getActualDeliveryDate());
    }

    /**
     * Handle delivery failure (called by webhook)
     */
    public void handleDeliveryFailure(UUID orderId, String reason, boolean willRetry) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return;
        }

        if (willRetry) {
            // Just add a note, courier will retry
            orderService.updateOrderStatus(orderId, order.getStatus(), 
                    "Delivery attempt failed, courier will retry", 
                    reason);
        } else {
            // Multiple failed attempts - needs attention
            order.setNotes(order.getNotes() != null ? 
                    order.getNotes() + "\nDelivery failed: " + reason : 
                    "Delivery failed: " + reason);
            orderRepository.save(order);

            // TODO: Notify admin and customer
            logger.warn("Delivery failed for order {}: {}", orderId, reason);
        }
    }

    /**
     * Get user-friendly status description
     */
    private String getStatusDescription(OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "Awaiting payment";
            case PAYMENT_FAILED -> "Payment failed";
            case CONFIRMED -> "Order confirmed";
            case ACCEPTED -> "Order is being processed";
            case IN_PRODUCTION -> "Your order is being prepared";
            case READY_TO_SHIP -> "Ready for dispatch";
            case SHIPPED -> "In transit";
            case OUT_FOR_DELIVERY -> "Out for delivery today";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Order cancelled";
            case REFUND_INITIATED -> "Refund in progress";
            case REFUNDED -> "Refund completed";
            default -> status.name();
        };
    }

    /**
     * Add business days to a date (skip weekends)
     */
    private LocalDate addBusinessDays(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            // Skip weekends (Saturday = 6, Sunday = 7)
            if (result.getDayOfWeek().getValue() < 6) {
                addedDays++;
            }
        }
        return result;
    }

    public BigDecimal getStandardDeliveryCost() {
        return this.standardDeliveryCost;
    }
    public BigDecimal getExpressDeliveryCost() {
        return this.expressDeliveryCost;
    }
    public BigDecimal getFreeDeliveryThreshold() {
        return this.freeDeliveryThreshold;
    }
    public OrderRepository getOrderRepository() {
        return this.orderRepository;
    }
    public PinCodeServiceabilityRepository getPinCodeRepository() {
        return this.pinCodeRepository;
    }
    public OrderService getOrderService() {
        return this.orderService;
    }
}
