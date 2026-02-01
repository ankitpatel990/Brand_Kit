package com.brandkit.order.dto;

import com.brandkit.order.entity.DeliveryOption;
import com.brandkit.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for order - FRD-004 FR-43
 * Note: NO partner information is included (client-facing)
 */
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private OffsetDateTime orderDate;
    private OrderStatus status;
    private String statusDisplayName;

    // Items
    private List<OrderItemResponse> items;
    private Integer itemCount;
    private Integer totalQuantity;

    // Delivery
    private AddressResponse deliveryAddress;
    private DeliveryOption deliveryOption;
    private String deliveryOptionDisplayName;
    private LocalDate estimatedDeliveryStart;
    private LocalDate estimatedDeliveryEnd;
    private String estimatedDeliveryRange;
    private LocalDate actualDeliveryDate;

    // Pricing
    private OrderPricing pricing;

    // Tracking
    private TrackingInfo trackingInfo;

    // Invoice
    private String invoiceNumber;
    private String invoiceUrl;

    // Cancellation/Refund
    private OffsetDateTime cancelledAt;
    private String cancellationReason;
    private BigDecimal refundAmount;
    private String refundStatus;

    // Timestamps
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static class OrderPricing {
        private BigDecimal originalSubtotal;
        private BigDecimal subtotal;
        private BigDecimal totalDiscount;
        private BigDecimal discountPercentage;
        private BigDecimal gstAmount;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;
        private BigDecimal igstAmount;
        private BigDecimal deliveryCharges;
        private BigDecimal totalAmount;
        private BigDecimal totalSavings;

        // Getters and Setters
        public BigDecimal getOriginalSubtotal() {
            return originalSubtotal;
        }

        public void setOriginalSubtotal(BigDecimal originalSubtotal) {
            this.originalSubtotal = originalSubtotal;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public BigDecimal getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(BigDecimal totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public BigDecimal getGstAmount() {
            return gstAmount;
        }

        public void setGstAmount(BigDecimal gstAmount) {
            this.gstAmount = gstAmount;
        }

        public BigDecimal getCgstAmount() {
            return cgstAmount;
        }

        public void setCgstAmount(BigDecimal cgstAmount) {
            this.cgstAmount = cgstAmount;
        }

        public BigDecimal getSgstAmount() {
            return sgstAmount;
        }

        public void setSgstAmount(BigDecimal sgstAmount) {
            this.sgstAmount = sgstAmount;
        }

        public BigDecimal getIgstAmount() {
            return igstAmount;
        }

        public void setIgstAmount(BigDecimal igstAmount) {
            this.igstAmount = igstAmount;
        }

        public BigDecimal getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(BigDecimal deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getTotalSavings() {
            return totalSavings;
        }

        public void setTotalSavings(BigDecimal totalSavings) {
            this.totalSavings = totalSavings;
        }
    }

    public static class TrackingInfo {
        private String courierName;
        private String trackingId;
        private String trackingUrl;
        private LocalDate estimatedDelivery;

        // Getters and Setters
        public String getCourierName() {
            return courierName;
        }

        public void setCourierName(String courierName) {
            this.courierName = courierName;
        }

        public String getTrackingId() {
            return trackingId;
        }

        public void setTrackingId(String trackingId) {
            this.trackingId = trackingId;
        }

        public String getTrackingUrl() {
            return trackingUrl;
        }

        public void setTrackingUrl(String trackingUrl) {
            this.trackingUrl = trackingUrl;
        }

        public LocalDate getEstimatedDelivery() {
            return estimatedDelivery;
        }

        public void setEstimatedDelivery(LocalDate estimatedDelivery) {
            this.estimatedDelivery = estimatedDelivery;
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OffsetDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(OffsetDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public AddressResponse getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressResponse deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getDeliveryOptionDisplayName() {
        return deliveryOptionDisplayName;
    }

    public void setDeliveryOptionDisplayName(String deliveryOptionDisplayName) {
        this.deliveryOptionDisplayName = deliveryOptionDisplayName;
    }

    public LocalDate getEstimatedDeliveryStart() {
        return estimatedDeliveryStart;
    }

    public void setEstimatedDeliveryStart(LocalDate estimatedDeliveryStart) {
        this.estimatedDeliveryStart = estimatedDeliveryStart;
    }

    public LocalDate getEstimatedDeliveryEnd() {
        return estimatedDeliveryEnd;
    }

    public void setEstimatedDeliveryEnd(LocalDate estimatedDeliveryEnd) {
        this.estimatedDeliveryEnd = estimatedDeliveryEnd;
    }

    public String getEstimatedDeliveryRange() {
        return estimatedDeliveryRange;
    }

    public void setEstimatedDeliveryRange(String estimatedDeliveryRange) {
        this.estimatedDeliveryRange = estimatedDeliveryRange;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public OrderPricing getPricing() {
        return pricing;
    }

    public void setPricing(OrderPricing pricing) {
        this.pricing = pricing;
    }

    public TrackingInfo getTrackingInfo() {
        return trackingInfo;
    }

    public void setTrackingInfo(TrackingInfo trackingInfo) {
        this.trackingInfo = trackingInfo;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(OffsetDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
