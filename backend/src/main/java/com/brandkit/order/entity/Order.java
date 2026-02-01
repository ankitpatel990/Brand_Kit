package com.brandkit.order.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order entity - FRD-004 FR-43
 * Note: partner_id is INTERNAL ONLY and NEVER exposed in client-facing APIs
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_id", columnList = "user_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_order_number", columnList = "order_number"),
    @Index(name = "idx_orders_created_at", columnList = "created_at")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", unique = true, length = 20)
    private String orderNumber; // Format: BK-YYYYMMDD-XXX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    // Delivery Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_option", nullable = false)
    private DeliveryOption deliveryOption = DeliveryOption.STANDARD;

    @Column(name = "estimated_delivery_start")
    private LocalDate estimatedDeliveryStart;

    @Column(name = "estimated_delivery_end")
    private LocalDate estimatedDeliveryEnd;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    // Pricing
    @DecimalMin(value = "0", message = "Subtotal must be non-negative")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0", message = "Original subtotal must be non-negative")
    @Column(name = "original_subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalSubtotal;

    @Column(name = "total_discount", precision = 12, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "GST amount must be non-negative")
    @Column(name = "gst_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal gstAmount;

    @Column(name = "cgst_amount", precision = 12, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 12, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 12, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "delivery_charges", precision = 10, scale = 2)
    private BigDecimal deliveryCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Total amount must be non-negative")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    // Payment Information
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "payment_timeout_at")
    private OffsetDateTime paymentTimeoutAt;

    // Tracking
    @Column(name = "tracking_id", length = 50)
    private String trackingId;

    @Column(name = "courier_name", length = 100)
    private String courierName;

    @Column(name = "tracking_url")
    private String trackingUrl;

    // Internal partner assignment (NEVER exposed to clients)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    // Invoice
    @Column(name = "invoice_number", length = 20)
    private String invoiceNumber;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    // T&C acceptance
    @Column(name = "terms_accepted", nullable = false)
    private Boolean termsAccepted = false;

    @Column(name = "terms_accepted_at")
    private OffsetDateTime termsAcceptedAt;

    // Cancellation/Refund
    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_initiated_at")
    private OffsetDateTime refundInitiatedAt;

    @Column(name = "refunded_at")
    private OffsetDateTime refundedAt;

    // Metadata
    @Column(name = "notes")
    private String notes;

    // Order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // Status history
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
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

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getOriginalSubtotal() {
        return originalSubtotal;
    }

    public void setOriginalSubtotal(BigDecimal originalSubtotal) {
        this.originalSubtotal = originalSubtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
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

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public OffsetDateTime getPaymentTimeoutAt() {
        return paymentTimeoutAt;
    }

    public void setPaymentTimeoutAt(OffsetDateTime paymentTimeoutAt) {
        this.paymentTimeoutAt = paymentTimeoutAt;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
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

    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
        if (termsAccepted) {
            this.termsAcceptedAt = OffsetDateTime.now();
        }
    }

    public OffsetDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
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

    public OffsetDateTime getRefundInitiatedAt() {
        return refundInitiatedAt;
    }

    public void setRefundInitiatedAt(OffsetDateTime refundInitiatedAt) {
        this.refundInitiatedAt = refundInitiatedAt;
    }

    public OffsetDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(OffsetDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<OrderStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Helper methods
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /**
     * Get estimated delivery date range as string
     */
    public String getEstimatedDeliveryRange() {
        if (estimatedDeliveryStart != null && estimatedDeliveryEnd != null) {
            return estimatedDeliveryStart.toString() + " to " + estimatedDeliveryEnd.toString();
        }
        return null;
    }
}
