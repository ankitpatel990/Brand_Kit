package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Settlement Response - FRD-005 FR-61
 * Settlement dashboard data
 */
public class SettlementResponse {

    private SettlementSummary summary;
    private List<SettlementDto> settlements;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public SettlementResponse() {
    }

    public SettlementSummary getSummary() {
        return summary;
    }

    public void setSummary(SettlementSummary summary) {
        this.summary = summary;
    }

    public List<SettlementDto> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<SettlementDto> settlements) {
        this.settlements = settlements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static SettlementResponseBuilder builder() {
        return new SettlementResponseBuilder();
    }

    public static class SettlementResponseBuilder {
        private SettlementSummary summary;
        private List<SettlementDto> settlements;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public SettlementResponseBuilder summary(SettlementSummary summary) { this.summary = summary; return this; }
        public SettlementResponseBuilder settlements(List<SettlementDto> settlements) { this.settlements = settlements; return this; }
        public SettlementResponseBuilder page(int page) { this.page = page; return this; }
        public SettlementResponseBuilder size(int size) { this.size = size; return this; }
        public SettlementResponseBuilder totalElements(long totalElements) { this.totalElements = totalElements; return this; }
        public SettlementResponseBuilder totalPages(int totalPages) { this.totalPages = totalPages; return this; }

        public SettlementResponse build() {
            SettlementResponse instance = new SettlementResponse();
            instance.summary = this.summary;
            instance.settlements = this.settlements;
            instance.page = this.page;
            instance.size = this.size;
            instance.totalElements = this.totalElements;
            instance.totalPages = this.totalPages;
            return instance;
        }
    }

    public static class SettlementSummary {
        private BigDecimal totalEarningsAllTime;
        private BigDecimal pendingSettlement;
        private LastSettlement lastSettlement;
        private String nextSettlementDate;

        public SettlementSummary() {
        }

        public BigDecimal getTotalEarningsAllTime() {
            return totalEarningsAllTime;
        }

        public void setTotalEarningsAllTime(BigDecimal totalEarningsAllTime) {
            this.totalEarningsAllTime = totalEarningsAllTime;
        }

        public BigDecimal getPendingSettlement() {
            return pendingSettlement;
        }

        public void setPendingSettlement(BigDecimal pendingSettlement) {
            this.pendingSettlement = pendingSettlement;
        }

        public LastSettlement getLastSettlement() {
            return lastSettlement;
        }

        public void setLastSettlement(LastSettlement lastSettlement) {
            this.lastSettlement = lastSettlement;
        }

        public String getNextSettlementDate() {
            return nextSettlementDate;
        }

        public void setNextSettlementDate(String nextSettlementDate) {
            this.nextSettlementDate = nextSettlementDate;
        }

        public static SettlementSummaryBuilder builder() {
            return new SettlementSummaryBuilder();
        }

        public static class SettlementSummaryBuilder {
            private BigDecimal totalEarningsAllTime;
            private BigDecimal pendingSettlement;
            private LastSettlement lastSettlement;
            private String nextSettlementDate;

            public SettlementSummaryBuilder totalEarningsAllTime(BigDecimal totalEarningsAllTime) { this.totalEarningsAllTime = totalEarningsAllTime; return this; }
            public SettlementSummaryBuilder pendingSettlement(BigDecimal pendingSettlement) { this.pendingSettlement = pendingSettlement; return this; }
            public SettlementSummaryBuilder lastSettlement(LastSettlement lastSettlement) { this.lastSettlement = lastSettlement; return this; }
            public SettlementSummaryBuilder nextSettlementDate(String nextSettlementDate) { this.nextSettlementDate = nextSettlementDate; return this; }

            public SettlementSummary build() {
                SettlementSummary instance = new SettlementSummary();
                instance.totalEarningsAllTime = this.totalEarningsAllTime;
                instance.pendingSettlement = this.pendingSettlement;
                instance.lastSettlement = this.lastSettlement;
                instance.nextSettlementDate = this.nextSettlementDate;
                return instance;
            }
        }
    }

    public static class LastSettlement {
        private BigDecimal amount;
        private String date;

        public LastSettlement() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public static LastSettlementBuilder builder() {
            return new LastSettlementBuilder();
        }

        public static class LastSettlementBuilder {
            private BigDecimal amount;
            private String date;

            public LastSettlementBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
            public LastSettlementBuilder date(String date) { this.date = date; return this; }

            public LastSettlement build() {
                LastSettlement instance = new LastSettlement();
                instance.amount = this.amount;
                instance.date = this.date;
                return instance;
            }
        }
    }

    public static class SettlementDto {
        private String settlementId;
        private String settlementNumber;
        private String period;
        private int orderCount;
        private BigDecimal totalAmount;
        private String status;
        private String settlementDate;
        private String statementUrl;

        public SettlementDto() {
        }

        public String getSettlementId() {
            return settlementId;
        }

        public void setSettlementId(String settlementId) {
            this.settlementId = settlementId;
        }

        public String getSettlementNumber() {
            return settlementNumber;
        }

        public void setSettlementNumber(String settlementNumber) {
            this.settlementNumber = settlementNumber;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSettlementDate() {
            return settlementDate;
        }

        public void setSettlementDate(String settlementDate) {
            this.settlementDate = settlementDate;
        }

        public String getStatementUrl() {
            return statementUrl;
        }

        public void setStatementUrl(String statementUrl) {
            this.statementUrl = statementUrl;
        }

        public static SettlementDtoBuilder builder() {
            return new SettlementDtoBuilder();
        }

        public static class SettlementDtoBuilder {
            private String settlementId;
            private String settlementNumber;
            private String period;
            private int orderCount;
            private BigDecimal totalAmount;
            private String status;
            private String settlementDate;
            private String statementUrl;

            public SettlementDtoBuilder settlementId(String settlementId) { this.settlementId = settlementId; return this; }
            public SettlementDtoBuilder settlementNumber(String settlementNumber) { this.settlementNumber = settlementNumber; return this; }
            public SettlementDtoBuilder period(String period) { this.period = period; return this; }
            public SettlementDtoBuilder orderCount(int orderCount) { this.orderCount = orderCount; return this; }
            public SettlementDtoBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
            public SettlementDtoBuilder status(String status) { this.status = status; return this; }
            public SettlementDtoBuilder settlementDate(String settlementDate) { this.settlementDate = settlementDate; return this; }
            public SettlementDtoBuilder date(String date) { this.settlementDate = date; return this; }
            public SettlementDtoBuilder statementUrl(String statementUrl) { this.statementUrl = statementUrl; return this; }

            public SettlementDto build() {
                SettlementDto instance = new SettlementDto();
                instance.settlementId = this.settlementId;
                instance.settlementNumber = this.settlementNumber;
                instance.period = this.period;
                instance.orderCount = this.orderCount;
                instance.totalAmount = this.totalAmount;
                instance.status = this.status;
                instance.settlementDate = this.settlementDate;
                instance.statementUrl = this.statementUrl;
                return instance;
            }
        }
    }
}
