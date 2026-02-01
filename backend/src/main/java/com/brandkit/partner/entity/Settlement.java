package com.brandkit.partner.entity;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Settlement Entity - FRD-005 FR-61
 * Commission settlement records
 */
@Entity
@Table(name = "settlements", indexes = {
    @Index(name = "idx_settlements_partner_id", columnList = "partner_id"),
    @Index(name = "idx_settlements_status", columnList = "status"),
    @Index(name = "idx_settlements_created_at", columnList = "created_at")
})
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "settlement_number", nullable = false, unique = true, length = 20)
    private String settlementNumber; // Format: SET-YYYY-MM-XXX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "total_product_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalProductAmount = BigDecimal.ZERO;

    @Column(name = "total_platform_commission", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPlatformCommission = BigDecimal.ZERO;

    @Column(name = "total_partner_earnings", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPartnerEarnings = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_initiated_at")
    private OffsetDateTime paymentInitiatedAt;

    @Column(name = "payment_completed_at")
    private OffsetDateTime paymentCompletedAt;

    @Column(name = "payment_failed_at")
    private OffsetDateTime paymentFailedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "statement_url")
    private String statementUrl;

    @Column(name = "statement_s3_key", length = 500)
    private String statementS3Key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SettlementOrder> orders = new ArrayList<>();

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

    public void initiatePayment(String gateway) {
        this.status = SettlementStatus.PROCESSING;
        this.paymentGateway = gateway;
        this.paymentInitiatedAt = OffsetDateTime.now();
    }

    public void completePayment(String reference) {
        this.status = SettlementStatus.COMPLETED;
        this.paymentReference = reference;
        this.paymentCompletedAt = OffsetDateTime.now();
    }

    public void failPayment(String reason) {
        this.status = SettlementStatus.FAILED;
        this.failureReason = reason;
        this.paymentFailedAt = OffsetDateTime.now();
    }

    public void approve(User admin) {
        this.approvedBy = admin;
        this.approvedAt = OffsetDateTime.now();
    }

    public void addOrder(SettlementOrder order) {
        orders.add(order);
        order.setSettlement(this);
    }

    public UUID getId() {
        return this.id;
    }
    public String getSettlementNumber() {
        return this.settlementNumber;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public LocalDate getPeriodStart() {
        return this.periodStart;
    }
    public LocalDate getPeriodEnd() {
        return this.periodEnd;
    }
    public Integer getTotalOrders() {
        return this.totalOrders;
    }
    public BigDecimal getTotalProductAmount() {
        return this.totalProductAmount;
    }
    public BigDecimal getTotalPlatformCommission() {
        return this.totalPlatformCommission;
    }
    public BigDecimal getTotalPartnerEarnings() {
        return this.totalPartnerEarnings;
    }
    public SettlementStatus getStatus() {
        return this.status;
    }
    public String getPaymentGateway() {
        return this.paymentGateway;
    }
    public String getPaymentReference() {
        return this.paymentReference;
    }
    public OffsetDateTime getPaymentInitiatedAt() {
        return this.paymentInitiatedAt;
    }
    public OffsetDateTime getPaymentCompletedAt() {
        return this.paymentCompletedAt;
    }
    public OffsetDateTime getPaymentFailedAt() {
        return this.paymentFailedAt;
    }
    public String getFailureReason() {
        return this.failureReason;
    }
    public String getStatementUrl() {
        return this.statementUrl;
    }
    public String getStatementS3Key() {
        return this.statementS3Key;
    }
    public User getApprovedBy() {
        return this.approvedBy;
    }
    public OffsetDateTime getApprovedAt() {
        return this.approvedAt;
    }
    public List<SettlementOrder> getOrders() {
        return this.orders;
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
    public void setSettlementNumber(String settlementNumber) {
        this.settlementNumber = settlementNumber;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    public void setTotalProductAmount(BigDecimal totalProductAmount) {
        this.totalProductAmount = totalProductAmount;
    }
    public void setTotalPlatformCommission(BigDecimal totalPlatformCommission) {
        this.totalPlatformCommission = totalPlatformCommission;
    }
    public void setTotalPartnerEarnings(BigDecimal totalPartnerEarnings) {
        this.totalPartnerEarnings = totalPartnerEarnings;
    }
    public void setStatus(SettlementStatus status) {
        this.status = status;
    }
    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    public void setPaymentInitiatedAt(OffsetDateTime paymentInitiatedAt) {
        this.paymentInitiatedAt = paymentInitiatedAt;
    }
    public void setPaymentCompletedAt(OffsetDateTime paymentCompletedAt) {
        this.paymentCompletedAt = paymentCompletedAt;
    }
    public void setPaymentFailedAt(OffsetDateTime paymentFailedAt) {
        this.paymentFailedAt = paymentFailedAt;
    }
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    public void setStatementUrl(String statementUrl) {
        this.statementUrl = statementUrl;
    }
    public void setStatementS3Key(String statementS3Key) {
        this.statementS3Key = statementS3Key;
    }
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    public void setApprovedAt(OffsetDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    public void setOrders(List<SettlementOrder> orders) {
        this.orders = orders;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Settlement() {
    }
    public Settlement(UUID id, String settlementNumber, Partner partner, LocalDate periodStart, LocalDate periodEnd, Integer totalOrders, BigDecimal totalProductAmount, BigDecimal totalPlatformCommission, BigDecimal totalPartnerEarnings, SettlementStatus status, String paymentGateway, String paymentReference, OffsetDateTime paymentInitiatedAt, OffsetDateTime paymentCompletedAt, OffsetDateTime paymentFailedAt, String failureReason, String statementUrl, String statementS3Key, User approvedBy, OffsetDateTime approvedAt, List<SettlementOrder> orders, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.settlementNumber = settlementNumber;
        this.partner = partner;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalOrders = totalOrders;
        this.totalProductAmount = totalProductAmount;
        this.totalPlatformCommission = totalPlatformCommission;
        this.totalPartnerEarnings = totalPartnerEarnings;
        this.status = status;
        this.paymentGateway = paymentGateway;
        this.paymentReference = paymentReference;
        this.paymentInitiatedAt = paymentInitiatedAt;
        this.paymentCompletedAt = paymentCompletedAt;
        this.paymentFailedAt = paymentFailedAt;
        this.failureReason = failureReason;
        this.statementUrl = statementUrl;
        this.statementS3Key = statementS3Key;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.orders = orders;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static SettlementBuilder builder() {
        return new SettlementBuilder();
    }

    public static class SettlementBuilder {
        private UUID id;
        private String settlementNumber;
        private Partner partner;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private Integer totalOrders = 0;
        private BigDecimal totalProductAmount = BigDecimal.ZERO;
        private BigDecimal totalPlatformCommission = BigDecimal.ZERO;
        private BigDecimal totalPartnerEarnings = BigDecimal.ZERO;
        private SettlementStatus status = SettlementStatus.PENDING;
        private String paymentGateway;
        private String paymentReference;
        private OffsetDateTime paymentInitiatedAt;
        private OffsetDateTime paymentCompletedAt;
        private OffsetDateTime paymentFailedAt;
        private String failureReason;
        private String statementUrl;
        private String statementS3Key;
        private User approvedBy;
        private OffsetDateTime approvedAt;
        private List<SettlementOrder> orders = new ArrayList<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        SettlementBuilder() {
        }

        public SettlementBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SettlementBuilder settlementNumber(String settlementNumber) {
            this.settlementNumber = settlementNumber;
            return this;
        }

        public SettlementBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public SettlementBuilder periodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public SettlementBuilder periodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public SettlementBuilder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public SettlementBuilder totalProductAmount(BigDecimal totalProductAmount) {
            this.totalProductAmount = totalProductAmount;
            return this;
        }

        public SettlementBuilder totalPlatformCommission(BigDecimal totalPlatformCommission) {
            this.totalPlatformCommission = totalPlatformCommission;
            return this;
        }

        public SettlementBuilder totalPartnerEarnings(BigDecimal totalPartnerEarnings) {
            this.totalPartnerEarnings = totalPartnerEarnings;
            return this;
        }

        public SettlementBuilder status(SettlementStatus status) {
            this.status = status;
            return this;
        }

        public SettlementBuilder paymentGateway(String paymentGateway) {
            this.paymentGateway = paymentGateway;
            return this;
        }

        public SettlementBuilder paymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public SettlementBuilder paymentInitiatedAt(OffsetDateTime paymentInitiatedAt) {
            this.paymentInitiatedAt = paymentInitiatedAt;
            return this;
        }

        public SettlementBuilder paymentCompletedAt(OffsetDateTime paymentCompletedAt) {
            this.paymentCompletedAt = paymentCompletedAt;
            return this;
        }

        public SettlementBuilder paymentFailedAt(OffsetDateTime paymentFailedAt) {
            this.paymentFailedAt = paymentFailedAt;
            return this;
        }

        public SettlementBuilder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public SettlementBuilder statementUrl(String statementUrl) {
            this.statementUrl = statementUrl;
            return this;
        }

        public SettlementBuilder statementS3Key(String statementS3Key) {
            this.statementS3Key = statementS3Key;
            return this;
        }

        public SettlementBuilder approvedBy(User approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public SettlementBuilder approvedAt(OffsetDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public SettlementBuilder orders(List<SettlementOrder> orders) {
            this.orders = orders;
            return this;
        }

        public SettlementBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SettlementBuilder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Settlement build() {
            Settlement instance = new Settlement();
            instance.id = this.id;
            instance.settlementNumber = this.settlementNumber;
            instance.partner = this.partner;
            instance.periodStart = this.periodStart;
            instance.periodEnd = this.periodEnd;
            instance.totalOrders = this.totalOrders;
            instance.totalProductAmount = this.totalProductAmount;
            instance.totalPlatformCommission = this.totalPlatformCommission;
            instance.totalPartnerEarnings = this.totalPartnerEarnings;
            instance.status = this.status;
            instance.paymentGateway = this.paymentGateway;
            instance.paymentReference = this.paymentReference;
            instance.paymentInitiatedAt = this.paymentInitiatedAt;
            instance.paymentCompletedAt = this.paymentCompletedAt;
            instance.paymentFailedAt = this.paymentFailedAt;
            instance.failureReason = this.failureReason;
            instance.statementUrl = this.statementUrl;
            instance.statementS3Key = this.statementS3Key;
            instance.approvedBy = this.approvedBy;
            instance.approvedAt = this.approvedAt;
            instance.orders = this.orders;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
