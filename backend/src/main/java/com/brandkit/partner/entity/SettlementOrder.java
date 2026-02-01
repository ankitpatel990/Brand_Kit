package com.brandkit.partner.entity;

import com.brandkit.order.entity.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Settlement Order Entity - FRD-005 FR-61
 * Orders included in each settlement
 */
@Entity
@Table(name = "settlement_orders", indexes = {
    @Index(name = "idx_settlement_orders_settlement_id", columnList = "settlement_id"),
    @Index(name = "idx_settlement_orders_order_id", columnList = "order_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_settlement_order", columnNames = {"settlement_id", "order_id"})
})
public class SettlementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal productAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "commission_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    @Column(name = "platform_commission", nullable = false, precision = 12, scale = 2)
    private BigDecimal platformCommission;

    @Column(name = "partner_earnings", nullable = false, precision = 12, scale = 2)
    private BigDecimal partnerEarnings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public Settlement getSettlement() {
        return this.settlement;
    }
    public Order getOrder() {
        return this.order;
    }
    public BigDecimal getProductAmount() {
        return this.productAmount;
    }
    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }
    public BigDecimal getFinalAmount() {
        return this.finalAmount;
    }
    public BigDecimal getCommissionPercentage() {
        return this.commissionPercentage;
    }
    public BigDecimal getPlatformCommission() {
        return this.platformCommission;
    }
    public BigDecimal getPartnerEarnings() {
        return this.partnerEarnings;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public void setProductAmount(BigDecimal productAmount) {
        this.productAmount = productAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }
    public void setPlatformCommission(BigDecimal platformCommission) {
        this.platformCommission = platformCommission;
    }
    public void setPartnerEarnings(BigDecimal partnerEarnings) {
        this.partnerEarnings = partnerEarnings;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public SettlementOrder() {
    }
    public SettlementOrder(UUID id, Settlement settlement, Order order, BigDecimal productAmount, BigDecimal discountAmount, BigDecimal finalAmount, BigDecimal commissionPercentage, BigDecimal platformCommission, BigDecimal partnerEarnings, OffsetDateTime createdAt) {
        this.id = id;
        this.settlement = settlement;
        this.order = order;
        this.productAmount = productAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.commissionPercentage = commissionPercentage;
        this.platformCommission = platformCommission;
        this.partnerEarnings = partnerEarnings;
        this.createdAt = createdAt;
    }
    public static SettlementOrderBuilder builder() {
        return new SettlementOrderBuilder();
    }

    public static class SettlementOrderBuilder {
        private UUID id;
        private Settlement settlement;
        private Order order;
        private BigDecimal productAmount;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal finalAmount;
        private BigDecimal commissionPercentage;
        private BigDecimal platformCommission;
        private BigDecimal partnerEarnings;
        private OffsetDateTime createdAt;

        SettlementOrderBuilder() {
        }

        public SettlementOrderBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SettlementOrderBuilder settlement(Settlement settlement) {
            this.settlement = settlement;
            return this;
        }

        public SettlementOrderBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public SettlementOrderBuilder productAmount(BigDecimal productAmount) {
            this.productAmount = productAmount;
            return this;
        }

        public SettlementOrderBuilder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public SettlementOrderBuilder finalAmount(BigDecimal finalAmount) {
            this.finalAmount = finalAmount;
            return this;
        }

        public SettlementOrderBuilder commissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
            return this;
        }

        public SettlementOrderBuilder platformCommission(BigDecimal platformCommission) {
            this.platformCommission = platformCommission;
            return this;
        }

        public SettlementOrderBuilder partnerEarnings(BigDecimal partnerEarnings) {
            this.partnerEarnings = partnerEarnings;
            return this;
        }

        public SettlementOrderBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SettlementOrder build() {
            SettlementOrder instance = new SettlementOrder();
            instance.id = this.id;
            instance.settlement = this.settlement;
            instance.order = this.order;
            instance.productAmount = this.productAmount;
            instance.discountAmount = this.discountAmount;
            instance.finalAmount = this.finalAmount;
            instance.commissionPercentage = this.commissionPercentage;
            instance.platformCommission = this.platformCommission;
            instance.partnerEarnings = this.partnerEarnings;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
