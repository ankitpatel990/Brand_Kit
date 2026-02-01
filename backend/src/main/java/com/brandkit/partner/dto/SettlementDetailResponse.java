package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Settlement Detail Response - FRD-005 FR-61
 * Detailed settlement breakdown with orders
 */
public class SettlementDetailResponse {

    private String settlementId;
    private String settlementNumber;
    private String periodStart;
    private String periodEnd;
    private int totalOrders;
    private BigDecimal totalProductAmount;
    private BigDecimal totalPlatformCommission;
    private BigDecimal totalPartnerEarnings;
    private String status;
    private String paymentDate;
    private String paymentReference;
    private String statementUrl;
    private List<OrderBreakdown> orders;

    public static class OrderBreakdown {
        private String orderId;
        private String orderNumber;
        private String productName;
        private int quantity;
        private BigDecimal productAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private BigDecimal commissionPercentage;
        private BigDecimal platformCommission;
        private BigDecimal partnerEarnings;
        private String deliveredDate;

        public OrderBreakdown() {}

        public String getOrderId() {
            return this.orderId;
        }
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        public String getOrderNumber() {
            return this.orderNumber;
        }
        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }
        public String getProductName() {
            return this.productName;
        }
        public void setProductName(String productName) {
            this.productName = productName;
        }
        public int getQuantity() {
            return this.quantity;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        public BigDecimal getProductAmount() {
            return this.productAmount;
        }
        public void setProductAmount(BigDecimal productAmount) {
            this.productAmount = productAmount;
        }
        public BigDecimal getDiscountAmount() {
            return this.discountAmount;
        }
        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }
        public BigDecimal getFinalAmount() {
            return this.finalAmount;
        }
        public void setFinalAmount(BigDecimal finalAmount) {
            this.finalAmount = finalAmount;
        }
        public BigDecimal getCommissionPercentage() {
            return this.commissionPercentage;
        }
        public void setCommissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
        }
        public BigDecimal getPlatformCommission() {
            return this.platformCommission;
        }
        public void setPlatformCommission(BigDecimal platformCommission) {
            this.platformCommission = platformCommission;
        }
        public BigDecimal getPartnerEarnings() {
            return this.partnerEarnings;
        }
        public void setPartnerEarnings(BigDecimal partnerEarnings) {
            this.partnerEarnings = partnerEarnings;
        }
        public String getDeliveredDate() {
            return this.deliveredDate;
        }
        public void setDeliveredDate(String deliveredDate) {
            this.deliveredDate = deliveredDate;
        }

        public static OrderBreakdownBuilder builder() {
            return new OrderBreakdownBuilder();
        }

        public static class OrderBreakdownBuilder {
            private String orderId;
            private String orderNumber;
            private String productName;
            private int quantity;
            private BigDecimal productAmount;
            private BigDecimal discountAmount;
            private BigDecimal finalAmount;
            private BigDecimal commissionPercentage;
            private BigDecimal platformCommission;
            private BigDecimal partnerEarnings;
            private String deliveredDate;

            public OrderBreakdownBuilder orderId(String orderId) {
                this.orderId = orderId;
                return this;
            }
            public OrderBreakdownBuilder orderNumber(String orderNumber) {
                this.orderNumber = orderNumber;
                return this;
            }
            public OrderBreakdownBuilder productName(String productName) {
                this.productName = productName;
                return this;
            }
            public OrderBreakdownBuilder quantity(int quantity) {
                this.quantity = quantity;
                return this;
            }
            public OrderBreakdownBuilder productAmount(BigDecimal productAmount) {
                this.productAmount = productAmount;
                return this;
            }
            public OrderBreakdownBuilder discountAmount(BigDecimal discountAmount) {
                this.discountAmount = discountAmount;
                return this;
            }
            public OrderBreakdownBuilder finalAmount(BigDecimal finalAmount) {
                this.finalAmount = finalAmount;
                return this;
            }
            public OrderBreakdownBuilder commissionPercentage(BigDecimal commissionPercentage) {
                this.commissionPercentage = commissionPercentage;
                return this;
            }
            public OrderBreakdownBuilder platformCommission(BigDecimal platformCommission) {
                this.platformCommission = platformCommission;
                return this;
            }
            public OrderBreakdownBuilder partnerEarnings(BigDecimal partnerEarnings) {
                this.partnerEarnings = partnerEarnings;
                return this;
            }
            public OrderBreakdownBuilder deliveredDate(String deliveredDate) {
                this.deliveredDate = deliveredDate;
                return this;
            }

            public OrderBreakdown build() {
                OrderBreakdown instance = new OrderBreakdown();
                instance.orderId = this.orderId;
                instance.orderNumber = this.orderNumber;
                instance.productName = this.productName;
                instance.quantity = this.quantity;
                instance.productAmount = this.productAmount;
                instance.discountAmount = this.discountAmount;
                instance.finalAmount = this.finalAmount;
                instance.commissionPercentage = this.commissionPercentage;
                instance.platformCommission = this.platformCommission;
                instance.partnerEarnings = this.partnerEarnings;
                instance.deliveredDate = this.deliveredDate;
                return instance;
            }
        }
    }

    public SettlementDetailResponse() {}

    public String getSettlementId() {
        return this.settlementId;
    }
    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }
    public String getSettlementNumber() {
        return this.settlementNumber;
    }
    public void setSettlementNumber(String settlementNumber) {
        this.settlementNumber = settlementNumber;
    }
    public String getPeriodStart() {
        return this.periodStart;
    }
    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }
    public String getPeriodEnd() {
        return this.periodEnd;
    }
    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }
    public int getTotalOrders() {
        return this.totalOrders;
    }
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }
    public BigDecimal getTotalProductAmount() {
        return this.totalProductAmount;
    }
    public void setTotalProductAmount(BigDecimal totalProductAmount) {
        this.totalProductAmount = totalProductAmount;
    }
    public BigDecimal getTotalPlatformCommission() {
        return this.totalPlatformCommission;
    }
    public void setTotalPlatformCommission(BigDecimal totalPlatformCommission) {
        this.totalPlatformCommission = totalPlatformCommission;
    }
    public BigDecimal getTotalPartnerEarnings() {
        return this.totalPartnerEarnings;
    }
    public void setTotalPartnerEarnings(BigDecimal totalPartnerEarnings) {
        this.totalPartnerEarnings = totalPartnerEarnings;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPaymentDate() {
        return this.paymentDate;
    }
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public String getPaymentReference() {
        return this.paymentReference;
    }
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    public String getStatementUrl() {
        return this.statementUrl;
    }
    public void setStatementUrl(String statementUrl) {
        this.statementUrl = statementUrl;
    }
    public List<OrderBreakdown> getOrders() {
        return this.orders;
    }
    public void setOrders(List<OrderBreakdown> orders) {
        this.orders = orders;
    }

    public static SettlementDetailResponseBuilder builder() {
        return new SettlementDetailResponseBuilder();
    }

    public static class SettlementDetailResponseBuilder {
        private String settlementId;
        private String settlementNumber;
        private String periodStart;
        private String periodEnd;
        private int totalOrders;
        private BigDecimal totalProductAmount;
        private BigDecimal totalPlatformCommission;
        private BigDecimal totalPartnerEarnings;
        private String status;
        private String paymentDate;
        private String paymentReference;
        private String statementUrl;
        private List<OrderBreakdown> orders;

        public SettlementDetailResponseBuilder settlementId(String settlementId) {
            this.settlementId = settlementId;
            return this;
        }
        public SettlementDetailResponseBuilder settlementNumber(String settlementNumber) {
            this.settlementNumber = settlementNumber;
            return this;
        }
        public SettlementDetailResponseBuilder periodStart(String periodStart) {
            this.periodStart = periodStart;
            return this;
        }
        public SettlementDetailResponseBuilder periodEnd(String periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }
        public SettlementDetailResponseBuilder totalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }
        public SettlementDetailResponseBuilder totalProductAmount(BigDecimal totalProductAmount) {
            this.totalProductAmount = totalProductAmount;
            return this;
        }
        public SettlementDetailResponseBuilder totalPlatformCommission(BigDecimal totalPlatformCommission) {
            this.totalPlatformCommission = totalPlatformCommission;
            return this;
        }
        public SettlementDetailResponseBuilder totalPartnerEarnings(BigDecimal totalPartnerEarnings) {
            this.totalPartnerEarnings = totalPartnerEarnings;
            return this;
        }
        public SettlementDetailResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        public SettlementDetailResponseBuilder paymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }
        public SettlementDetailResponseBuilder paymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }
        public SettlementDetailResponseBuilder statementUrl(String statementUrl) {
            this.statementUrl = statementUrl;
            return this;
        }
        public SettlementDetailResponseBuilder orders(List<OrderBreakdown> orders) {
            this.orders = orders;
            return this;
        }

        public SettlementDetailResponse build() {
            SettlementDetailResponse instance = new SettlementDetailResponse();
            instance.settlementId = this.settlementId;
            instance.settlementNumber = this.settlementNumber;
            instance.periodStart = this.periodStart;
            instance.periodEnd = this.periodEnd;
            instance.totalOrders = this.totalOrders;
            instance.totalProductAmount = this.totalProductAmount;
            instance.totalPlatformCommission = this.totalPlatformCommission;
            instance.totalPartnerEarnings = this.totalPartnerEarnings;
            instance.status = this.status;
            instance.paymentDate = this.paymentDate;
            instance.paymentReference = this.paymentReference;
            instance.statementUrl = this.statementUrl;
            instance.orders = this.orders;
            return instance;
        }
    }
}
