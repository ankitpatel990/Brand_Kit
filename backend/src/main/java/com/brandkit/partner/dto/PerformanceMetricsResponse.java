package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Performance Metrics Response - FRD-005 FR-63
 * Partner performance dashboard data
 */
public class PerformanceMetricsResponse {

    private String period;
    private Metrics metrics;
    private Benchmarks benchmark;
    private List<AlertDto> alerts;

    public static class Metrics {
        private BigDecimal fulfillmentRate;
        private BigDecimal averageLeadTime;
        private BigDecimal deliverySuccessRate;
        private BigDecimal averageRating;
        private int totalOrdersFulfilled;
        private BigDecimal totalRevenue;
        private int totalOrdersAssigned;
        private int totalOrdersAccepted;
        private int totalOrdersRejected;

        public Metrics() {}

        public BigDecimal getFulfillmentRate() {
            return this.fulfillmentRate;
        }
        public void setFulfillmentRate(BigDecimal fulfillmentRate) {
            this.fulfillmentRate = fulfillmentRate;
        }
        public BigDecimal getAverageLeadTime() {
            return this.averageLeadTime;
        }
        public void setAverageLeadTime(BigDecimal averageLeadTime) {
            this.averageLeadTime = averageLeadTime;
        }
        public BigDecimal getDeliverySuccessRate() {
            return this.deliverySuccessRate;
        }
        public void setDeliverySuccessRate(BigDecimal deliverySuccessRate) {
            this.deliverySuccessRate = deliverySuccessRate;
        }
        public BigDecimal getAverageRating() {
            return this.averageRating;
        }
        public void setAverageRating(BigDecimal averageRating) {
            this.averageRating = averageRating;
        }
        public int getTotalOrdersFulfilled() {
            return this.totalOrdersFulfilled;
        }
        public void setTotalOrdersFulfilled(int totalOrdersFulfilled) {
            this.totalOrdersFulfilled = totalOrdersFulfilled;
        }
        public BigDecimal getTotalRevenue() {
            return this.totalRevenue;
        }
        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
        public int getTotalOrdersAssigned() {
            return this.totalOrdersAssigned;
        }
        public void setTotalOrdersAssigned(int totalOrdersAssigned) {
            this.totalOrdersAssigned = totalOrdersAssigned;
        }
        public int getTotalOrdersAccepted() {
            return this.totalOrdersAccepted;
        }
        public void setTotalOrdersAccepted(int totalOrdersAccepted) {
            this.totalOrdersAccepted = totalOrdersAccepted;
        }
        public int getTotalOrdersRejected() {
            return this.totalOrdersRejected;
        }
        public void setTotalOrdersRejected(int totalOrdersRejected) {
            this.totalOrdersRejected = totalOrdersRejected;
        }

        public static MetricsBuilder builder() {
            return new MetricsBuilder();
        }

        public static class MetricsBuilder {
            private BigDecimal fulfillmentRate;
            private BigDecimal averageLeadTime;
            private BigDecimal deliverySuccessRate;
            private BigDecimal averageRating;
            private int totalOrdersFulfilled;
            private BigDecimal totalRevenue;
            private int totalOrdersAssigned;
            private int totalOrdersAccepted;
            private int totalOrdersRejected;

            public MetricsBuilder fulfillmentRate(BigDecimal fulfillmentRate) {
                this.fulfillmentRate = fulfillmentRate;
                return this;
            }
            public MetricsBuilder averageLeadTime(BigDecimal averageLeadTime) {
                this.averageLeadTime = averageLeadTime;
                return this;
            }
            public MetricsBuilder deliverySuccessRate(BigDecimal deliverySuccessRate) {
                this.deliverySuccessRate = deliverySuccessRate;
                return this;
            }
            public MetricsBuilder averageRating(BigDecimal averageRating) {
                this.averageRating = averageRating;
                return this;
            }
            public MetricsBuilder totalOrdersFulfilled(int totalOrdersFulfilled) {
                this.totalOrdersFulfilled = totalOrdersFulfilled;
                return this;
            }
            public MetricsBuilder totalRevenue(BigDecimal totalRevenue) {
                this.totalRevenue = totalRevenue;
                return this;
            }
            public MetricsBuilder totalOrdersAssigned(int totalOrdersAssigned) {
                this.totalOrdersAssigned = totalOrdersAssigned;
                return this;
            }
            public MetricsBuilder totalOrdersAccepted(int totalOrdersAccepted) {
                this.totalOrdersAccepted = totalOrdersAccepted;
                return this;
            }
            public MetricsBuilder totalOrdersRejected(int totalOrdersRejected) {
                this.totalOrdersRejected = totalOrdersRejected;
                return this;
            }

            public Metrics build() {
                Metrics instance = new Metrics();
                instance.fulfillmentRate = this.fulfillmentRate;
                instance.averageLeadTime = this.averageLeadTime;
                instance.deliverySuccessRate = this.deliverySuccessRate;
                instance.averageRating = this.averageRating;
                instance.totalOrdersFulfilled = this.totalOrdersFulfilled;
                instance.totalRevenue = this.totalRevenue;
                instance.totalOrdersAssigned = this.totalOrdersAssigned;
                instance.totalOrdersAccepted = this.totalOrdersAccepted;
                instance.totalOrdersRejected = this.totalOrdersRejected;
                return instance;
            }
        }
    }

    public static class Benchmarks {
        private BigDecimal platformAverageFulfillment;
        private BigDecimal platformAverageLeadTime;

        public Benchmarks() {}

        public BigDecimal getPlatformAverageFulfillment() {
            return this.platformAverageFulfillment;
        }
        public void setPlatformAverageFulfillment(BigDecimal platformAverageFulfillment) {
            this.platformAverageFulfillment = platformAverageFulfillment;
        }
        public BigDecimal getPlatformAverageLeadTime() {
            return this.platformAverageLeadTime;
        }
        public void setPlatformAverageLeadTime(BigDecimal platformAverageLeadTime) {
            this.platformAverageLeadTime = platformAverageLeadTime;
        }

        public static BenchmarksBuilder builder() {
            return new BenchmarksBuilder();
        }

        public static class BenchmarksBuilder {
            private BigDecimal platformAverageFulfillment;
            private BigDecimal platformAverageLeadTime;

            public BenchmarksBuilder platformAverageFulfillment(BigDecimal platformAverageFulfillment) {
                this.platformAverageFulfillment = platformAverageFulfillment;
                return this;
            }
            public BenchmarksBuilder platformAverageLeadTime(BigDecimal platformAverageLeadTime) {
                this.platformAverageLeadTime = platformAverageLeadTime;
                return this;
            }

            public Benchmarks build() {
                Benchmarks instance = new Benchmarks();
                instance.platformAverageFulfillment = this.platformAverageFulfillment;
                instance.platformAverageLeadTime = this.platformAverageLeadTime;
                return instance;
            }
        }
    }

    public static class AlertDto {
        private String type; // WARNING, INFO
        private String message;

        public AlertDto() {}

        public String getType() {
            return this.type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getMessage() {
            return this.message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

        public static AlertDtoBuilder builder() {
            return new AlertDtoBuilder();
        }

        public static class AlertDtoBuilder {
            private String type;
            private String message;

            public AlertDtoBuilder type(String type) {
                this.type = type;
                return this;
            }
            public AlertDtoBuilder message(String message) {
                this.message = message;
                return this;
            }

            public AlertDto build() {
                AlertDto instance = new AlertDto();
                instance.type = this.type;
                instance.message = this.message;
                return instance;
            }
        }
    }

    public PerformanceMetricsResponse() {}

    public String getPeriod() {
        return this.period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public Metrics getMetrics() {
        return this.metrics;
    }
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }
    public Benchmarks getBenchmark() {
        return this.benchmark;
    }
    public void setBenchmark(Benchmarks benchmark) {
        this.benchmark = benchmark;
    }
    public List<AlertDto> getAlerts() {
        return this.alerts;
    }
    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }

    public static PerformanceMetricsResponseBuilder builder() {
        return new PerformanceMetricsResponseBuilder();
    }

    public static class PerformanceMetricsResponseBuilder {
        private String period;
        private Metrics metrics;
        private Benchmarks benchmark;
        private List<AlertDto> alerts;

        public PerformanceMetricsResponseBuilder period(String period) {
            this.period = period;
            return this;
        }
        public PerformanceMetricsResponseBuilder metrics(Metrics metrics) {
            this.metrics = metrics;
            return this;
        }
        public PerformanceMetricsResponseBuilder benchmark(Benchmarks benchmark) {
            this.benchmark = benchmark;
            return this;
        }
        public PerformanceMetricsResponseBuilder alerts(List<AlertDto> alerts) {
            this.alerts = alerts;
            return this;
        }

        public PerformanceMetricsResponse build() {
            PerformanceMetricsResponse instance = new PerformanceMetricsResponse();
            instance.period = this.period;
            instance.metrics = this.metrics;
            instance.benchmark = this.benchmark;
            instance.alerts = this.alerts;
            return instance;
        }
    }
}
