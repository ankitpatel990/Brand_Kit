package com.brandkit.order.entity;

import com.brandkit.catalog.entity.Partner;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Order partner assignment entity (INTERNAL ONLY) - FRD-004 FR-45
 * Maps orders to fulfillment partners - NEVER exposed to clients
 */
@Entity
@Table(name = "order_partner_assignments", indexes = {
    @Index(name = "idx_order_partner_order_id", columnList = "order_id"),
    @Index(name = "idx_order_partner_partner_id", columnList = "partner_id"),
    @Index(name = "idx_order_partner_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_order_partner", columnNames = {"order_id", "partner_id"})
})
public class OrderPartnerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartnerOrderStatus status = PartnerOrderStatus.AWAITING_ACCEPTANCE;

    // Timestamps
    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @Column(name = "rejected_at")
    private OffsetDateTime rejectedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Production tracking (internal)
    @Column(name = "production_started_at")
    private OffsetDateTime productionStartedAt;

    @Column(name = "production_completed_at")
    private OffsetDateTime productionCompletedAt;

    @Column(name = "shipped_at")
    private OffsetDateTime shippedAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    // Internal notes
    @Column(name = "internal_notes")
    private String internalNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        assignedAt = OffsetDateTime.now();
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

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public PartnerOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PartnerOrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(OffsetDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public OffsetDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(OffsetDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public OffsetDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(OffsetDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public OffsetDateTime getProductionStartedAt() {
        return productionStartedAt;
    }

    public void setProductionStartedAt(OffsetDateTime productionStartedAt) {
        this.productionStartedAt = productionStartedAt;
    }

    public OffsetDateTime getProductionCompletedAt() {
        return productionCompletedAt;
    }

    public void setProductionCompletedAt(OffsetDateTime productionCompletedAt) {
        this.productionCompletedAt = productionCompletedAt;
    }

    public OffsetDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(OffsetDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Helper methods
    public void accept() {
        this.status = PartnerOrderStatus.ACCEPTED;
        this.acceptedAt = OffsetDateTime.now();
    }

    public void reject(String reason) {
        this.status = PartnerOrderStatus.REJECTED;
        this.rejectedAt = OffsetDateTime.now();
        this.rejectionReason = reason;
    }

    public void startProduction() {
        this.status = PartnerOrderStatus.IN_PRODUCTION;
        this.productionStartedAt = OffsetDateTime.now();
    }

    public void completeProduction() {
        this.status = PartnerOrderStatus.READY_TO_SHIP;
        this.productionCompletedAt = OffsetDateTime.now();
    }

    public void ship() {
        this.status = PartnerOrderStatus.SHIPPED;
        this.shippedAt = OffsetDateTime.now();
    }

    public void deliver() {
        this.status = PartnerOrderStatus.DELIVERED;
        this.deliveredAt = OffsetDateTime.now();
    }
}
