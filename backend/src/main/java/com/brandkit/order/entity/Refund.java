package com.brandkit.order.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Refund entity - FRD-004
 * Tracks refund processing
 */
@Entity
@Table(name = "refunds", indexes = {
    @Index(name = "idx_refunds_order_id", columnList = "order_id"),
    @Index(name = "idx_refunds_payment_id", columnList = "payment_id"),
    @Index(name = "idx_refunds_status", columnList = "status")
})
public class Refund {

    public enum RefundStatus {
        INITIATED,
        PROCESSING,
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // Refund Details
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Refund reason is required")
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    // Gateway Details
    @Column(name = "gateway_refund_id", length = 100)
    private String gatewayRefundId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RefundStatus status = RefundStatus.INITIATED;

    // Timestamps
    @Column(name = "initiated_at", nullable = false)
    private OffsetDateTime initiatedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    // Admin who initiated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by")
    private User initiatedBy;

    // Error handling
    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_description")
    private String errorDescription;

    // Failure reason (user-friendly)
    @Column(name = "failure_reason")
    private String failureReason;

    // Admin notes
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        initiatedAt = OffsetDateTime.now();
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGatewayRefundId() {
        return gatewayRefundId;
    }

    public void setGatewayRefundId(String gatewayRefundId) {
        this.gatewayRefundId = gatewayRefundId;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public OffsetDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(OffsetDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(OffsetDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public OffsetDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(OffsetDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public User getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(User initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Helper methods
    public void markAsProcessing() {
        this.status = RefundStatus.PROCESSING;
        this.processedAt = OffsetDateTime.now();
    }

    public void markAsSuccess(String gatewayRefundId) {
        this.status = RefundStatus.SUCCESS;
        this.gatewayRefundId = gatewayRefundId;
        this.completedAt = OffsetDateTime.now();
    }

    public void markAsFailed(String errorCode, String errorDescription) {
        this.status = RefundStatus.FAILED;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.failedAt = OffsetDateTime.now();
    }
}
