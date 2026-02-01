package com.brandkit.partner.entity;

import com.brandkit.catalog.entity.Partner;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Partner Performance Metrics Entity - FRD-005 FR-63
 * Cached performance metrics for partners
 */
@Entity
@Table(name = "partner_performance_metrics", indexes = {
    @Index(name = "idx_partner_metrics_partner_id", columnList = "partner_id")
})
public class PartnerPerformanceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    private Partner partner;

    @Column(name = "total_orders_assigned")
    private Integer totalOrdersAssigned = 0;

    @Column(name = "total_orders_accepted")
    private Integer totalOrdersAccepted = 0;

    @Column(name = "total_orders_rejected")
    private Integer totalOrdersRejected = 0;

    @Column(name = "total_orders_fulfilled")
    private Integer totalOrdersFulfilled = 0;

    @Column(name = "total_orders_delivered")
    private Integer totalOrdersDelivered = 0;

    @Column(name = "fulfillment_rate", precision = 5, scale = 2)
    private BigDecimal fulfillmentRate = BigDecimal.ZERO;

    @Column(name = "average_lead_time_days", precision = 5, scale = 2)
    private BigDecimal averageLeadTimeDays = BigDecimal.ZERO;

    @Column(name = "delivery_success_rate", precision = 5, scale = 2)
    private BigDecimal deliverySuccessRate = BigDecimal.ZERO;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_revenue", precision = 14, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "last_calculated_at")
    private OffsetDateTime lastCalculatedAt;

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

    /**
     * Recalculate derived metrics
     */
    public void recalculate() {
        // Fulfillment Rate = (accepted / assigned) * 100
        if (totalOrdersAssigned > 0) {
            fulfillmentRate = new BigDecimal(totalOrdersAccepted)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalOrdersAssigned), 2, java.math.RoundingMode.HALF_UP);
        }
        
        // Delivery Success Rate = (delivered / fulfilled) * 100
        if (totalOrdersFulfilled > 0) {
            deliverySuccessRate = new BigDecimal(totalOrdersDelivered)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalOrdersFulfilled), 2, java.math.RoundingMode.HALF_UP);
        }
        
        lastCalculatedAt = OffsetDateTime.now();
    }

    /**
     * Check if fulfillment rate is below threshold
     */
    public boolean isBelowPerformanceThreshold(BigDecimal threshold) {
        return fulfillmentRate.compareTo(threshold) < 0;
    }

    public UUID getId() {
        return this.id;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public Integer getTotalOrdersAssigned() {
        return this.totalOrdersAssigned;
    }
    public Integer getTotalOrdersAccepted() {
        return this.totalOrdersAccepted;
    }
    public Integer getTotalOrdersRejected() {
        return this.totalOrdersRejected;
    }
    public Integer getTotalOrdersFulfilled() {
        return this.totalOrdersFulfilled;
    }
    public Integer getTotalOrdersDelivered() {
        return this.totalOrdersDelivered;
    }
    public BigDecimal getFulfillmentRate() {
        return this.fulfillmentRate;
    }
    public BigDecimal getAverageLeadTimeDays() {
        return this.averageLeadTimeDays;
    }
    public BigDecimal getDeliverySuccessRate() {
        return this.deliverySuccessRate;
    }
    public BigDecimal getAverageRating() {
        return this.averageRating;
    }
    public BigDecimal getTotalRevenue() {
        return this.totalRevenue;
    }
    public OffsetDateTime getLastCalculatedAt() {
        return this.lastCalculatedAt;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }
    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setTotalOrdersAssigned(Integer totalOrdersAssigned) {
        this.totalOrdersAssigned = totalOrdersAssigned;
    }
    public void setTotalOrdersAccepted(Integer totalOrdersAccepted) {
        this.totalOrdersAccepted = totalOrdersAccepted;
    }
    public void setTotalOrdersRejected(Integer totalOrdersRejected) {
        this.totalOrdersRejected = totalOrdersRejected;
    }
    public void setTotalOrdersFulfilled(Integer totalOrdersFulfilled) {
        this.totalOrdersFulfilled = totalOrdersFulfilled;
    }
    public void setTotalOrdersDelivered(Integer totalOrdersDelivered) {
        this.totalOrdersDelivered = totalOrdersDelivered;
    }
    public void setFulfillmentRate(BigDecimal fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
    }
    public void setAverageLeadTimeDays(BigDecimal averageLeadTimeDays) {
        this.averageLeadTimeDays = averageLeadTimeDays;
    }
    public void setDeliverySuccessRate(BigDecimal deliverySuccessRate) {
        this.deliverySuccessRate = deliverySuccessRate;
    }
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public void setLastCalculatedAt(OffsetDateTime lastCalculatedAt) {
        this.lastCalculatedAt = lastCalculatedAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public PartnerPerformanceMetrics() {
    }
    public PartnerPerformanceMetrics(UUID id, Partner partner, Integer totalOrdersAssigned, Integer totalOrdersAccepted, Integer totalOrdersRejected, Integer totalOrdersFulfilled, Integer totalOrdersDelivered, BigDecimal fulfillmentRate, BigDecimal averageLeadTimeDays, BigDecimal deliverySuccessRate, BigDecimal averageRating, BigDecimal totalRevenue, OffsetDateTime lastCalculatedAt, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.partner = partner;
        this.totalOrdersAssigned = totalOrdersAssigned;
        this.totalOrdersAccepted = totalOrdersAccepted;
        this.totalOrdersRejected = totalOrdersRejected;
        this.totalOrdersFulfilled = totalOrdersFulfilled;
        this.totalOrdersDelivered = totalOrdersDelivered;
        this.fulfillmentRate = fulfillmentRate;
        this.averageLeadTimeDays = averageLeadTimeDays;
        this.deliverySuccessRate = deliverySuccessRate;
        this.averageRating = averageRating;
        this.totalRevenue = totalRevenue;
        this.lastCalculatedAt = lastCalculatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static PartnerPerformanceMetricsBuilder builder() {
        return new PartnerPerformanceMetricsBuilder();
    }

    public static class PartnerPerformanceMetricsBuilder {
        private UUID id;
        private Partner partner;
        private Integer totalOrdersAssigned = 0;
        private Integer totalOrdersAccepted = 0;
        private Integer totalOrdersRejected = 0;
        private Integer totalOrdersFulfilled = 0;
        private Integer totalOrdersDelivered = 0;
        private BigDecimal fulfillmentRate = BigDecimal.ZERO;
        private BigDecimal averageLeadTimeDays = BigDecimal.ZERO;
        private BigDecimal deliverySuccessRate = BigDecimal.ZERO;
        private BigDecimal averageRating = BigDecimal.ZERO;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private OffsetDateTime lastCalculatedAt;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        PartnerPerformanceMetricsBuilder() {
        }

        public PartnerPerformanceMetricsBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PartnerPerformanceMetricsBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalOrdersAssigned(Integer totalOrdersAssigned) {
            this.totalOrdersAssigned = totalOrdersAssigned;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalOrdersAccepted(Integer totalOrdersAccepted) {
            this.totalOrdersAccepted = totalOrdersAccepted;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalOrdersRejected(Integer totalOrdersRejected) {
            this.totalOrdersRejected = totalOrdersRejected;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalOrdersFulfilled(Integer totalOrdersFulfilled) {
            this.totalOrdersFulfilled = totalOrdersFulfilled;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalOrdersDelivered(Integer totalOrdersDelivered) {
            this.totalOrdersDelivered = totalOrdersDelivered;
            return this;
        }

        public PartnerPerformanceMetricsBuilder fulfillmentRate(BigDecimal fulfillmentRate) {
            this.fulfillmentRate = fulfillmentRate;
            return this;
        }

        public PartnerPerformanceMetricsBuilder averageLeadTimeDays(BigDecimal averageLeadTimeDays) {
            this.averageLeadTimeDays = averageLeadTimeDays;
            return this;
        }

        public PartnerPerformanceMetricsBuilder deliverySuccessRate(BigDecimal deliverySuccessRate) {
            this.deliverySuccessRate = deliverySuccessRate;
            return this;
        }

        public PartnerPerformanceMetricsBuilder averageRating(BigDecimal averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public PartnerPerformanceMetricsBuilder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public PartnerPerformanceMetricsBuilder lastCalculatedAt(OffsetDateTime lastCalculatedAt) {
            this.lastCalculatedAt = lastCalculatedAt;
            return this;
        }

        public PartnerPerformanceMetricsBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PartnerPerformanceMetricsBuilder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PartnerPerformanceMetrics build() {
            PartnerPerformanceMetrics instance = new PartnerPerformanceMetrics();
            instance.id = this.id;
            instance.partner = this.partner;
            instance.totalOrdersAssigned = this.totalOrdersAssigned;
            instance.totalOrdersAccepted = this.totalOrdersAccepted;
            instance.totalOrdersRejected = this.totalOrdersRejected;
            instance.totalOrdersFulfilled = this.totalOrdersFulfilled;
            instance.totalOrdersDelivered = this.totalOrdersDelivered;
            instance.fulfillmentRate = this.fulfillmentRate;
            instance.averageLeadTimeDays = this.averageLeadTimeDays;
            instance.deliverySuccessRate = this.deliverySuccessRate;
            instance.averageRating = this.averageRating;
            instance.totalRevenue = this.totalRevenue;
            instance.lastCalculatedAt = this.lastCalculatedAt;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
