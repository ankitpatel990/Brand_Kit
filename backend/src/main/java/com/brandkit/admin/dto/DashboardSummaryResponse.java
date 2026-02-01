package com.brandkit.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for admin dashboard summary
 * 
 * FRD-006 FR-67: Admin Dashboard Home
 */
public class DashboardSummaryResponse {

    // Metrics Cards
    private MetricsData metrics;
    
    // Charts Data
    private ChartsData charts;
    
    // Recent Activity
    private RecentActivity recentActivity;
    
    // Alerts
    private List<AlertItem> alerts;

    public DashboardSummaryResponse() {}

    public MetricsData getMetrics() {
        return this.metrics;
    }
    public void setMetrics(MetricsData metrics) {
        this.metrics = metrics;
    }
    public ChartsData getCharts() {
        return this.charts;
    }
    public void setCharts(ChartsData charts) {
        this.charts = charts;
    }
    public RecentActivity getRecentActivity() {
        return this.recentActivity;
    }
    public void setRecentActivity(RecentActivity recentActivity) {
        this.recentActivity = recentActivity;
    }
    public List<AlertItem> getAlerts() {
        return this.alerts;
    }
    public void setAlerts(List<AlertItem> alerts) {
        this.alerts = alerts;
    }

    public static DashboardSummaryResponseBuilder builder() {
        return new DashboardSummaryResponseBuilder();
    }

    public static class DashboardSummaryResponseBuilder {
        private MetricsData metrics;
        private ChartsData charts;
        private RecentActivity recentActivity;
        private List<AlertItem> alerts;

        public DashboardSummaryResponseBuilder metrics(MetricsData metrics) {
            this.metrics = metrics;
            return this;
        }
        public DashboardSummaryResponseBuilder charts(ChartsData charts) {
            this.charts = charts;
            return this;
        }
        public DashboardSummaryResponseBuilder recentActivity(RecentActivity recentActivity) {
            this.recentActivity = recentActivity;
            return this;
        }
        public DashboardSummaryResponseBuilder alerts(List<AlertItem> alerts) {
            this.alerts = alerts;
            return this;
        }

        public DashboardSummaryResponse build() {
            DashboardSummaryResponse instance = new DashboardSummaryResponse();
            instance.metrics = this.metrics;
            instance.charts = this.charts;
            instance.recentActivity = this.recentActivity;
            instance.alerts = this.alerts;
            return instance;
        }
    }
    
    /**
     * Metrics Cards Data
     */
    public static class MetricsData {
        private BigDecimal totalRevenueThisMonth;
        private Double revenueChangePercent;
        private Long totalOrdersThisMonth;
        private Double ordersChangePercent;
        private Long activeUsers;
        private Long activePartners;
        private Long pendingOrders;
        private Long pendingSettlements;

        public MetricsData() {}

        public BigDecimal getTotalRevenueThisMonth() {
            return this.totalRevenueThisMonth;
        }
        public void setTotalRevenueThisMonth(BigDecimal totalRevenueThisMonth) {
            this.totalRevenueThisMonth = totalRevenueThisMonth;
        }
        public Double getRevenueChangePercent() {
            return this.revenueChangePercent;
        }
        public void setRevenueChangePercent(Double revenueChangePercent) {
            this.revenueChangePercent = revenueChangePercent;
        }
        public Long getTotalOrdersThisMonth() {
            return this.totalOrdersThisMonth;
        }
        public void setTotalOrdersThisMonth(Long totalOrdersThisMonth) {
            this.totalOrdersThisMonth = totalOrdersThisMonth;
        }
        public Double getOrdersChangePercent() {
            return this.ordersChangePercent;
        }
        public void setOrdersChangePercent(Double ordersChangePercent) {
            this.ordersChangePercent = ordersChangePercent;
        }
        public Long getActiveUsers() {
            return this.activeUsers;
        }
        public void setActiveUsers(Long activeUsers) {
            this.activeUsers = activeUsers;
        }
        public Long getActivePartners() {
            return this.activePartners;
        }
        public void setActivePartners(Long activePartners) {
            this.activePartners = activePartners;
        }
        public Long getPendingOrders() {
            return this.pendingOrders;
        }
        public void setPendingOrders(Long pendingOrders) {
            this.pendingOrders = pendingOrders;
        }
        public Long getPendingSettlements() {
            return this.pendingSettlements;
        }
        public void setPendingSettlements(Long pendingSettlements) {
            this.pendingSettlements = pendingSettlements;
        }

        public static MetricsDataBuilder builder() {
            return new MetricsDataBuilder();
        }

        public static class MetricsDataBuilder {
            private BigDecimal totalRevenueThisMonth;
            private Double revenueChangePercent;
            private Long totalOrdersThisMonth;
            private Double ordersChangePercent;
            private Long activeUsers;
            private Long activePartners;
            private Long pendingOrders;
            private Long pendingSettlements;

            public MetricsDataBuilder totalRevenueThisMonth(BigDecimal totalRevenueThisMonth) {
                this.totalRevenueThisMonth = totalRevenueThisMonth;
                return this;
            }
            public MetricsDataBuilder revenueChangePercent(Double revenueChangePercent) {
                this.revenueChangePercent = revenueChangePercent;
                return this;
            }
            public MetricsDataBuilder totalOrdersThisMonth(Long totalOrdersThisMonth) {
                this.totalOrdersThisMonth = totalOrdersThisMonth;
                return this;
            }
            public MetricsDataBuilder ordersChangePercent(Double ordersChangePercent) {
                this.ordersChangePercent = ordersChangePercent;
                return this;
            }
            public MetricsDataBuilder activeUsers(Long activeUsers) {
                this.activeUsers = activeUsers;
                return this;
            }
            public MetricsDataBuilder activePartners(Long activePartners) {
                this.activePartners = activePartners;
                return this;
            }
            public MetricsDataBuilder pendingOrders(Long pendingOrders) {
                this.pendingOrders = pendingOrders;
                return this;
            }
            public MetricsDataBuilder pendingSettlements(Long pendingSettlements) {
                this.pendingSettlements = pendingSettlements;
                return this;
            }

            public MetricsData build() {
                MetricsData instance = new MetricsData();
                instance.totalRevenueThisMonth = this.totalRevenueThisMonth;
                instance.revenueChangePercent = this.revenueChangePercent;
                instance.totalOrdersThisMonth = this.totalOrdersThisMonth;
                instance.ordersChangePercent = this.ordersChangePercent;
                instance.activeUsers = this.activeUsers;
                instance.activePartners = this.activePartners;
                instance.pendingOrders = this.pendingOrders;
                instance.pendingSettlements = this.pendingSettlements;
                return instance;
            }
        }
    }

    /**
     * Charts Data
     */
    public static class ChartsData {
        private List<TrendDataPoint> revenueTrend;
        private List<TrendDataPoint> ordersTrend;
        private Map<String, Long> orderStatusDistribution;

        public ChartsData() {}

        public List<TrendDataPoint> getRevenueTrend() {
            return this.revenueTrend;
        }
        public void setRevenueTrend(List<TrendDataPoint> revenueTrend) {
            this.revenueTrend = revenueTrend;
        }
        public List<TrendDataPoint> getOrdersTrend() {
            return this.ordersTrend;
        }
        public void setOrdersTrend(List<TrendDataPoint> ordersTrend) {
            this.ordersTrend = ordersTrend;
        }
        public Map<String, Long> getOrderStatusDistribution() {
            return this.orderStatusDistribution;
        }
        public void setOrderStatusDistribution(Map<String, Long> orderStatusDistribution) {
            this.orderStatusDistribution = orderStatusDistribution;
        }

        public static ChartsDataBuilder builder() {
            return new ChartsDataBuilder();
        }

        public static class ChartsDataBuilder {
            private List<TrendDataPoint> revenueTrend;
            private List<TrendDataPoint> ordersTrend;
            private Map<String, Long> orderStatusDistribution;

            public ChartsDataBuilder revenueTrend(List<TrendDataPoint> revenueTrend) {
                this.revenueTrend = revenueTrend;
                return this;
            }
            public ChartsDataBuilder ordersTrend(List<TrendDataPoint> ordersTrend) {
                this.ordersTrend = ordersTrend;
                return this;
            }
            public ChartsDataBuilder orderStatusDistribution(Map<String, Long> orderStatusDistribution) {
                this.orderStatusDistribution = orderStatusDistribution;
                return this;
            }

            public ChartsData build() {
                ChartsData instance = new ChartsData();
                instance.revenueTrend = this.revenueTrend;
                instance.ordersTrend = this.ordersTrend;
                instance.orderStatusDistribution = this.orderStatusDistribution;
                return instance;
            }
        }
    }

    /**
     * Trend Data Point
     */
    public static class TrendDataPoint {
        private String date;
        private BigDecimal value;

        public TrendDataPoint() {}

        public String getDate() {
            return this.date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public BigDecimal getValue() {
            return this.value;
        }
        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public static TrendDataPointBuilder builder() {
            return new TrendDataPointBuilder();
        }

        public static class TrendDataPointBuilder {
            private String date;
            private BigDecimal value;

            public TrendDataPointBuilder date(String date) {
                this.date = date;
                return this;
            }
            public TrendDataPointBuilder value(BigDecimal value) {
                this.value = value;
                return this;
            }

            public TrendDataPoint build() {
                TrendDataPoint instance = new TrendDataPoint();
                instance.date = this.date;
                instance.value = this.value;
                return instance;
            }
        }
    }

    /**
     * Recent Activity
     */
    public static class RecentActivity {
        private List<RecentOrder> recentOrders;
        private List<RecentUser> recentRegistrations;
        private List<RecentPartnerAction> recentPartnerActions;

        public RecentActivity() {}

        public List<RecentOrder> getRecentOrders() {
            return this.recentOrders;
        }
        public void setRecentOrders(List<RecentOrder> recentOrders) {
            this.recentOrders = recentOrders;
        }
        public List<RecentUser> getRecentRegistrations() {
            return this.recentRegistrations;
        }
        public void setRecentRegistrations(List<RecentUser> recentRegistrations) {
            this.recentRegistrations = recentRegistrations;
        }
        public List<RecentPartnerAction> getRecentPartnerActions() {
            return this.recentPartnerActions;
        }
        public void setRecentPartnerActions(List<RecentPartnerAction> recentPartnerActions) {
            this.recentPartnerActions = recentPartnerActions;
        }

        public static RecentActivityBuilder builder() {
            return new RecentActivityBuilder();
        }

        public static class RecentActivityBuilder {
            private List<RecentOrder> recentOrders;
            private List<RecentUser> recentRegistrations;
            private List<RecentPartnerAction> recentPartnerActions;

            public RecentActivityBuilder recentOrders(List<RecentOrder> recentOrders) {
                this.recentOrders = recentOrders;
                return this;
            }
            public RecentActivityBuilder recentRegistrations(List<RecentUser> recentRegistrations) {
                this.recentRegistrations = recentRegistrations;
                return this;
            }
            public RecentActivityBuilder recentPartnerActions(List<RecentPartnerAction> recentPartnerActions) {
                this.recentPartnerActions = recentPartnerActions;
                return this;
            }

            public RecentActivity build() {
                RecentActivity instance = new RecentActivity();
                instance.recentOrders = this.recentOrders;
                instance.recentRegistrations = this.recentRegistrations;
                instance.recentPartnerActions = this.recentPartnerActions;
                return instance;
            }
        }
    }

    /**
     * Recent Order Summary
     */
    public static class RecentOrder {
        private String orderId;
        private String clientName;
        private BigDecimal amount;
        private String status;
        private String createdAt;

        public RecentOrder() {}

        public String getOrderId() {
            return this.orderId;
        }
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        public String getClientName() {
            return this.clientName;
        }
        public void setClientName(String clientName) {
            this.clientName = clientName;
        }
        public BigDecimal getAmount() {
            return this.amount;
        }
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        public String getStatus() {
            return this.status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getCreatedAt() {
            return this.createdAt;
        }
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public static RecentOrderBuilder builder() {
            return new RecentOrderBuilder();
        }

        public static class RecentOrderBuilder {
            private String orderId;
            private String clientName;
            private BigDecimal amount;
            private String status;
            private String createdAt;

            public RecentOrderBuilder orderId(String orderId) {
                this.orderId = orderId;
                return this;
            }
            public RecentOrderBuilder clientName(String clientName) {
                this.clientName = clientName;
                return this;
            }
            public RecentOrderBuilder amount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }
            public RecentOrderBuilder status(String status) {
                this.status = status;
                return this;
            }
            public RecentOrderBuilder createdAt(String createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public RecentOrder build() {
                RecentOrder instance = new RecentOrder();
                instance.orderId = this.orderId;
                instance.clientName = this.clientName;
                instance.amount = this.amount;
                instance.status = this.status;
                instance.createdAt = this.createdAt;
                return instance;
            }
        }
    }

    /**
     * Recent User Registration
     */
    public static class RecentUser {
        private String name;
        private String email;
        private String registeredAt;

        public RecentUser() {}

        public String getName() {
            return this.name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return this.email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getRegisteredAt() {
            return this.registeredAt;
        }
        public void setRegisteredAt(String registeredAt) {
            this.registeredAt = registeredAt;
        }

        public static RecentUserBuilder builder() {
            return new RecentUserBuilder();
        }

        public static class RecentUserBuilder {
            private String name;
            private String email;
            private String registeredAt;

            public RecentUserBuilder name(String name) {
                this.name = name;
                return this;
            }
            public RecentUserBuilder email(String email) {
                this.email = email;
                return this;
            }
            public RecentUserBuilder registeredAt(String registeredAt) {
                this.registeredAt = registeredAt;
                return this;
            }

            public RecentUser build() {
                RecentUser instance = new RecentUser();
                instance.name = this.name;
                instance.email = this.email;
                instance.registeredAt = this.registeredAt;
                return instance;
            }
        }
    }

    /**
     * Recent Partner Action
     */
    public static class RecentPartnerAction {
        private String partnerName;
        private String action;
        private String actionAt;

        public RecentPartnerAction() {}

        public String getPartnerName() {
            return this.partnerName;
        }
        public void setPartnerName(String partnerName) {
            this.partnerName = partnerName;
        }
        public String getAction() {
            return this.action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public String getActionAt() {
            return this.actionAt;
        }
        public void setActionAt(String actionAt) {
            this.actionAt = actionAt;
        }

        public static RecentPartnerActionBuilder builder() {
            return new RecentPartnerActionBuilder();
        }

        public static class RecentPartnerActionBuilder {
            private String partnerName;
            private String action;
            private String actionAt;

            public RecentPartnerActionBuilder partnerName(String partnerName) {
                this.partnerName = partnerName;
                return this;
            }
            public RecentPartnerActionBuilder action(String action) {
                this.action = action;
                return this;
            }
            public RecentPartnerActionBuilder actionAt(String actionAt) {
                this.actionAt = actionAt;
                return this;
            }

            public RecentPartnerAction build() {
                RecentPartnerAction instance = new RecentPartnerAction();
                instance.partnerName = this.partnerName;
                instance.action = this.action;
                instance.actionAt = this.actionAt;
                return instance;
            }
        }
    }

    /**
     * Alert Item
     */
    public static class AlertItem {
        private String type;
        private String priority;
        private String message;
        private String link;
        private Integer count;

        public AlertItem() {}

        public String getType() {
            return this.type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getPriority() {
            return this.priority;
        }
        public void setPriority(String priority) {
            this.priority = priority;
        }
        public String getMessage() {
            return this.message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getLink() {
            return this.link;
        }
        public void setLink(String link) {
            this.link = link;
        }
        public Integer getCount() {
            return this.count;
        }
        public void setCount(Integer count) {
            this.count = count;
        }

        public static AlertItemBuilder builder() {
            return new AlertItemBuilder();
        }

        public static class AlertItemBuilder {
            private String type;
            private String priority;
            private String message;
            private String link;
            private Integer count;

            public AlertItemBuilder type(String type) {
                this.type = type;
                return this;
            }
            public AlertItemBuilder priority(String priority) {
                this.priority = priority;
                return this;
            }
            public AlertItemBuilder message(String message) {
                this.message = message;
                return this;
            }
            public AlertItemBuilder link(String link) {
                this.link = link;
                return this;
            }
            public AlertItemBuilder count(Integer count) {
                this.count = count;
                return this;
            }

            public AlertItem build() {
                AlertItem instance = new AlertItem();
                instance.type = this.type;
                instance.priority = this.priority;
                instance.message = this.message;
                instance.link = this.link;
                instance.count = this.count;
                return instance;
            }
        }
    }
}
