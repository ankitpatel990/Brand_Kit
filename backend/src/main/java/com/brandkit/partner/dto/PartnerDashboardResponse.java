package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Partner Dashboard Response - FRD-005 FR-52
 * Dashboard summary with key metrics
 */
public class PartnerDashboardResponse {

    private SummaryCards summary;
    private List<RecentOrderDto> recentOrders;
    private List<AlertDto> alerts;
    private DiscountSummary discountStatus;
    private long unreadNotifications;

    public PartnerDashboardResponse() {
    }

    public SummaryCards getSummary() {
        return summary;
    }

    public void setSummary(SummaryCards summary) {
        this.summary = summary;
    }

    public List<RecentOrderDto> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrderDto> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }

    public DiscountSummary getDiscountStatus() {
        return discountStatus;
    }

    public void setDiscountStatus(DiscountSummary discountStatus) {
        this.discountStatus = discountStatus;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public static PartnerDashboardResponseBuilder builder() {
        return new PartnerDashboardResponseBuilder();
    }

    public static class PartnerDashboardResponseBuilder {
        private SummaryCards summary;
        private List<RecentOrderDto> recentOrders;
        private List<AlertDto> alerts;
        private DiscountSummary discountStatus;
        private long unreadNotifications;

        public PartnerDashboardResponseBuilder summary(SummaryCards summary) { this.summary = summary; return this; }
        public PartnerDashboardResponseBuilder recentOrders(List<RecentOrderDto> recentOrders) { this.recentOrders = recentOrders; return this; }
        public PartnerDashboardResponseBuilder alerts(List<AlertDto> alerts) { this.alerts = alerts; return this; }
        public PartnerDashboardResponseBuilder discountStatus(DiscountSummary discountStatus) { this.discountStatus = discountStatus; return this; }
        public PartnerDashboardResponseBuilder unreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; return this; }

        public PartnerDashboardResponse build() {
            PartnerDashboardResponse instance = new PartnerDashboardResponse();
            instance.summary = this.summary;
            instance.recentOrders = this.recentOrders;
            instance.alerts = this.alerts;
            instance.discountStatus = this.discountStatus;
            instance.unreadNotifications = this.unreadNotifications;
            return instance;
        }
    }

    public static class SummaryCards {
        private long pendingOrders;
        private long activeOrders;
        private long readyToShip;
        private BigDecimal revenueThisMonth;
        private long activeDiscounts;

        public SummaryCards() {
        }

        public long getPendingOrders() {
            return pendingOrders;
        }

        public void setPendingOrders(long pendingOrders) {
            this.pendingOrders = pendingOrders;
        }

        public long getActiveOrders() {
            return activeOrders;
        }

        public void setActiveOrders(long activeOrders) {
            this.activeOrders = activeOrders;
        }

        public long getReadyToShip() {
            return readyToShip;
        }

        public void setReadyToShip(long readyToShip) {
            this.readyToShip = readyToShip;
        }

        public BigDecimal getRevenueThisMonth() {
            return revenueThisMonth;
        }

        public void setRevenueThisMonth(BigDecimal revenueThisMonth) {
            this.revenueThisMonth = revenueThisMonth;
        }

        public long getActiveDiscounts() {
            return activeDiscounts;
        }

        public void setActiveDiscounts(long activeDiscounts) {
            this.activeDiscounts = activeDiscounts;
        }

        public static SummaryCardsBuilder builder() {
            return new SummaryCardsBuilder();
        }

        public static class SummaryCardsBuilder {
            private long pendingOrders;
            private long activeOrders;
            private long readyToShip;
            private BigDecimal revenueThisMonth;
            private long activeDiscounts;

            public SummaryCardsBuilder pendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; return this; }
            public SummaryCardsBuilder activeOrders(long activeOrders) { this.activeOrders = activeOrders; return this; }
            public SummaryCardsBuilder readyToShip(long readyToShip) { this.readyToShip = readyToShip; return this; }
            public SummaryCardsBuilder revenueThisMonth(BigDecimal revenueThisMonth) { this.revenueThisMonth = revenueThisMonth; return this; }
            public SummaryCardsBuilder activeDiscounts(long activeDiscounts) { this.activeDiscounts = activeDiscounts; return this; }

            public SummaryCards build() {
                SummaryCards instance = new SummaryCards();
                instance.pendingOrders = this.pendingOrders;
                instance.activeOrders = this.activeOrders;
                instance.readyToShip = this.readyToShip;
                instance.revenueThisMonth = this.revenueThisMonth;
                instance.activeDiscounts = this.activeDiscounts;
                return instance;
            }
        }
    }

    public static class RecentOrderDto {
        private String orderId;
        private String orderNumber;
        private String productName;
        private int quantity;
        private String status;
        private String orderDate;
        private String expectedShipDate;
        private BigDecimal partnerEarnings;

        public RecentOrderDto() {
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public String getExpectedShipDate() {
            return expectedShipDate;
        }

        public void setExpectedShipDate(String expectedShipDate) {
            this.expectedShipDate = expectedShipDate;
        }

        public BigDecimal getPartnerEarnings() {
            return partnerEarnings;
        }

        public void setPartnerEarnings(BigDecimal partnerEarnings) {
            this.partnerEarnings = partnerEarnings;
        }

        public static RecentOrderDtoBuilder builder() {
            return new RecentOrderDtoBuilder();
        }

        public static class RecentOrderDtoBuilder {
            private String orderId;
            private String orderNumber;
            private String productName;
            private int quantity;
            private String status;
            private String orderDate;
            private String expectedShipDate;
            private BigDecimal partnerEarnings;

            public RecentOrderDtoBuilder orderId(String orderId) { this.orderId = orderId; return this; }
            public RecentOrderDtoBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
            public RecentOrderDtoBuilder productName(String productName) { this.productName = productName; return this; }
            public RecentOrderDtoBuilder quantity(int quantity) { this.quantity = quantity; return this; }
            public RecentOrderDtoBuilder status(String status) { this.status = status; return this; }
            public RecentOrderDtoBuilder orderDate(String orderDate) { this.orderDate = orderDate; return this; }
            public RecentOrderDtoBuilder expectedShipDate(String expectedShipDate) { this.expectedShipDate = expectedShipDate; return this; }
            public RecentOrderDtoBuilder partnerEarnings(BigDecimal partnerEarnings) { this.partnerEarnings = partnerEarnings; return this; }

            public RecentOrderDto build() {
                RecentOrderDto instance = new RecentOrderDto();
                instance.orderId = this.orderId;
                instance.orderNumber = this.orderNumber;
                instance.productName = this.productName;
                instance.quantity = this.quantity;
                instance.status = this.status;
                instance.orderDate = this.orderDate;
                instance.expectedShipDate = this.expectedShipDate;
                instance.partnerEarnings = this.partnerEarnings;
                return instance;
            }
        }
    }

    public static class AlertDto {
        private String type;
        private String title;
        private String message;
        private String actionUrl;
        private String orderId;

        public AlertDto() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getActionUrl() {
            return actionUrl;
        }

        public void setActionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public static AlertDtoBuilder builder() {
            return new AlertDtoBuilder();
        }

        public static class AlertDtoBuilder {
            private String type;
            private String title;
            private String message;
            private String actionUrl;
            private String orderId;

            public AlertDtoBuilder type(String type) { this.type = type; return this; }
            public AlertDtoBuilder title(String title) { this.title = title; return this; }
            public AlertDtoBuilder message(String message) { this.message = message; return this; }
            public AlertDtoBuilder actionUrl(String actionUrl) { this.actionUrl = actionUrl; return this; }
            public AlertDtoBuilder orderId(String orderId) { this.orderId = orderId; return this; }

            public AlertDto build() {
                AlertDto instance = new AlertDto();
                instance.type = this.type;
                instance.title = this.title;
                instance.message = this.message;
                instance.actionUrl = this.actionUrl;
                instance.orderId = this.orderId;
                return instance;
            }
        }
    }

    public static class DiscountSummary {
        private long active;
        private long pending;
        private long disabled;

        public DiscountSummary() {
        }

        public long getActive() {
            return active;
        }

        public void setActive(long active) {
            this.active = active;
        }

        public long getPending() {
            return pending;
        }

        public void setPending(long pending) {
            this.pending = pending;
        }

        public long getDisabled() {
            return disabled;
        }

        public void setDisabled(long disabled) {
            this.disabled = disabled;
        }

        public static DiscountSummaryBuilder builder() {
            return new DiscountSummaryBuilder();
        }

        public static class DiscountSummaryBuilder {
            private long active;
            private long pending;
            private long disabled;

            public DiscountSummaryBuilder active(long active) { this.active = active; return this; }
            public DiscountSummaryBuilder pending(long pending) { this.pending = pending; return this; }
            public DiscountSummaryBuilder disabled(long disabled) { this.disabled = disabled; return this; }

            public DiscountSummary build() {
                DiscountSummary instance = new DiscountSummary();
                instance.active = this.active;
                instance.pending = this.pending;
                instance.disabled = this.disabled;
                return instance;
            }
        }
    }
}
