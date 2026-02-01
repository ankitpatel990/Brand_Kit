package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Partner Order List Response - FRD-005 FR-54
 * Paginated list of orders for partner
 */
public class PartnerOrderListResponse {

    private List<OrderSummary> orders;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static class OrderSummary {
        private String orderId;
        private String orderNumber;
        private String clientName; // Masked: "Rajesh K."
        private String productName;
        private int quantity;
        private String orderDate;
        private String expectedShipDate;
        private String status;
        private String partnerStatus;
        private BigDecimal partnerEarnings;
        private List<String> actions;

        public OrderSummary() {}

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
        public String getClientName() {
            return this.clientName;
        }
        public void setClientName(String clientName) {
            this.clientName = clientName;
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
        public String getOrderDate() {
            return this.orderDate;
        }
        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }
        public String getExpectedShipDate() {
            return this.expectedShipDate;
        }
        public void setExpectedShipDate(String expectedShipDate) {
            this.expectedShipDate = expectedShipDate;
        }
        public String getStatus() {
            return this.status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getPartnerStatus() {
            return this.partnerStatus;
        }
        public void setPartnerStatus(String partnerStatus) {
            this.partnerStatus = partnerStatus;
        }
        public BigDecimal getPartnerEarnings() {
            return this.partnerEarnings;
        }
        public void setPartnerEarnings(BigDecimal partnerEarnings) {
            this.partnerEarnings = partnerEarnings;
        }
        public List<String> getActions() {
            return this.actions;
        }
        public void setActions(List<String> actions) {
            this.actions = actions;
        }

        public static OrderSummaryBuilder builder() {
            return new OrderSummaryBuilder();
        }

        public static class OrderSummaryBuilder {
            private String orderId;
            private String orderNumber;
            private String clientName;
            private String productName;
            private int quantity;
            private String orderDate;
            private String expectedShipDate;
            private String status;
            private String partnerStatus;
            private BigDecimal partnerEarnings;
            private List<String> actions;

            public OrderSummaryBuilder orderId(String orderId) {
                this.orderId = orderId;
                return this;
            }
            public OrderSummaryBuilder orderNumber(String orderNumber) {
                this.orderNumber = orderNumber;
                return this;
            }
            public OrderSummaryBuilder clientName(String clientName) {
                this.clientName = clientName;
                return this;
            }
            public OrderSummaryBuilder productName(String productName) {
                this.productName = productName;
                return this;
            }
            public OrderSummaryBuilder quantity(int quantity) {
                this.quantity = quantity;
                return this;
            }
            public OrderSummaryBuilder orderDate(String orderDate) {
                this.orderDate = orderDate;
                return this;
            }
            public OrderSummaryBuilder expectedShipDate(String expectedShipDate) {
                this.expectedShipDate = expectedShipDate;
                return this;
            }
            public OrderSummaryBuilder status(String status) {
                this.status = status;
                return this;
            }
            public OrderSummaryBuilder partnerStatus(String partnerStatus) {
                this.partnerStatus = partnerStatus;
                return this;
            }
            public OrderSummaryBuilder partnerEarnings(BigDecimal partnerEarnings) {
                this.partnerEarnings = partnerEarnings;
                return this;
            }
            public OrderSummaryBuilder actions(List<String> actions) {
                this.actions = actions;
                return this;
            }

            public OrderSummary build() {
                OrderSummary instance = new OrderSummary();
                instance.orderId = this.orderId;
                instance.orderNumber = this.orderNumber;
                instance.clientName = this.clientName;
                instance.productName = this.productName;
                instance.quantity = this.quantity;
                instance.orderDate = this.orderDate;
                instance.expectedShipDate = this.expectedShipDate;
                instance.status = this.status;
                instance.partnerStatus = this.partnerStatus;
                instance.partnerEarnings = this.partnerEarnings;
                instance.actions = this.actions;
                return instance;
            }
        }
    }

    public PartnerOrderListResponse() {}

    public List<OrderSummary> getOrders() {
        return this.orders;
    }
    public void setOrders(List<OrderSummary> orders) {
        this.orders = orders;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getTotalElements() {
        return this.totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getTotalPages() {
        return this.totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static PartnerOrderListResponseBuilder builder() {
        return new PartnerOrderListResponseBuilder();
    }

    public static class PartnerOrderListResponseBuilder {
        private List<OrderSummary> orders;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public PartnerOrderListResponseBuilder orders(List<OrderSummary> orders) {
            this.orders = orders;
            return this;
        }
        public PartnerOrderListResponseBuilder page(int page) {
            this.page = page;
            return this;
        }
        public PartnerOrderListResponseBuilder size(int size) {
            this.size = size;
            return this;
        }
        public PartnerOrderListResponseBuilder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }
        public PartnerOrderListResponseBuilder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PartnerOrderListResponse build() {
            PartnerOrderListResponse instance = new PartnerOrderListResponse();
            instance.orders = this.orders;
            instance.page = this.page;
            instance.size = this.size;
            instance.totalElements = this.totalElements;
            instance.totalPages = this.totalPages;
            return instance;
        }
    }
}
