package com.brandkit.catalog.dto;

import java.math.BigDecimal;

/**
 * Price Calculation Response DTO
 * FRD-002 Sub-Prompt 6: Dynamic Price Calculator
 */
public class PriceCalculationResponse {
    private String status;
    private PriceCalculationData data;

    public PriceCalculationResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PriceCalculationData getData() {
        return data;
    }

    public void setData(PriceCalculationData data) {
        this.data = data;
    }

    public static PriceCalculationResponseBuilder builder() {
        return new PriceCalculationResponseBuilder();
    }

    public static class PriceCalculationData {
        private int quantity;
        private ApplicableTier applicableTier;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private BigDecimal customizationFee;
        private BigDecimal discountAmount;
        private BigDecimal totalPrice;
        private Savings savings;
        private boolean hasDiscount;
        private String discountName;

        public PriceCalculationData() {
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public ApplicableTier getApplicableTier() {
            return applicableTier;
        }

        public void setApplicableTier(ApplicableTier applicableTier) {
            this.applicableTier = applicableTier;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public BigDecimal getCustomizationFee() {
            return customizationFee;
        }

        public void setCustomizationFee(BigDecimal customizationFee) {
            this.customizationFee = customizationFee;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Savings getSavings() {
            return savings;
        }

        public void setSavings(Savings savings) {
            this.savings = savings;
        }

        public boolean isHasDiscount() {
            return hasDiscount;
        }

        public void setHasDiscount(boolean hasDiscount) {
            this.hasDiscount = hasDiscount;
        }

        public String getDiscountName() {
            return discountName;
        }

        public void setDiscountName(String discountName) {
            this.discountName = discountName;
        }

        public static PriceCalculationDataBuilder builder() {
            return new PriceCalculationDataBuilder();
        }

        public static class PriceCalculationDataBuilder {
            private int quantity;
            private ApplicableTier applicableTier;
            private BigDecimal unitPrice;
            private BigDecimal subtotal;
            private BigDecimal customizationFee;
            private BigDecimal discountAmount;
            private BigDecimal totalPrice;
            private Savings savings;
            private boolean hasDiscount;
            private String discountName;

            public PriceCalculationDataBuilder quantity(int quantity) {
                this.quantity = quantity;
                return this;
            }

            public PriceCalculationDataBuilder applicableTier(ApplicableTier applicableTier) {
                this.applicableTier = applicableTier;
                return this;
            }

            public PriceCalculationDataBuilder unitPrice(BigDecimal unitPrice) {
                this.unitPrice = unitPrice;
                return this;
            }

            public PriceCalculationDataBuilder subtotal(BigDecimal subtotal) {
                this.subtotal = subtotal;
                return this;
            }

            public PriceCalculationDataBuilder customizationFee(BigDecimal customizationFee) {
                this.customizationFee = customizationFee;
                return this;
            }

            public PriceCalculationDataBuilder discountAmount(BigDecimal discountAmount) {
                this.discountAmount = discountAmount;
                return this;
            }

            public PriceCalculationDataBuilder totalPrice(BigDecimal totalPrice) {
                this.totalPrice = totalPrice;
                return this;
            }

            public PriceCalculationDataBuilder savings(Savings savings) {
                this.savings = savings;
                return this;
            }

            public PriceCalculationDataBuilder hasDiscount(boolean hasDiscount) {
                this.hasDiscount = hasDiscount;
                return this;
            }

            public PriceCalculationDataBuilder discountName(String discountName) {
                this.discountName = discountName;
                return this;
            }

            public PriceCalculationData build() {
                PriceCalculationData instance = new PriceCalculationData();
                instance.quantity = this.quantity;
                instance.applicableTier = this.applicableTier;
                instance.unitPrice = this.unitPrice;
                instance.subtotal = this.subtotal;
                instance.customizationFee = this.customizationFee;
                instance.discountAmount = this.discountAmount;
                instance.totalPrice = this.totalPrice;
                instance.savings = this.savings;
                instance.hasDiscount = this.hasDiscount;
                instance.discountName = this.discountName;
                return instance;
            }
        }
    }

    public static class ApplicableTier {
        private int tierNumber;
        private int minQuantity;
        private Integer maxQuantity;
        private BigDecimal unitPrice;

        public ApplicableTier() {
        }

        public int getTierNumber() {
            return tierNumber;
        }

        public void setTierNumber(int tierNumber) {
            this.tierNumber = tierNumber;
        }

        public int getMinQuantity() {
            return minQuantity;
        }

        public void setMinQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
        }

        public Integer getMaxQuantity() {
            return maxQuantity;
        }

        public void setMaxQuantity(Integer maxQuantity) {
            this.maxQuantity = maxQuantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public static ApplicableTierBuilder builder() {
            return new ApplicableTierBuilder();
        }

        public static class ApplicableTierBuilder {
            private int tierNumber;
            private int minQuantity;
            private Integer maxQuantity;
            private BigDecimal unitPrice;

            public ApplicableTierBuilder tierNumber(int tierNumber) {
                this.tierNumber = tierNumber;
                return this;
            }

            public ApplicableTierBuilder minQuantity(int minQuantity) {
                this.minQuantity = minQuantity;
                return this;
            }

            public ApplicableTierBuilder maxQuantity(Integer maxQuantity) {
                this.maxQuantity = maxQuantity;
                return this;
            }

            public ApplicableTierBuilder unitPrice(BigDecimal unitPrice) {
                this.unitPrice = unitPrice;
                return this;
            }

            public ApplicableTier build() {
                ApplicableTier instance = new ApplicableTier();
                instance.tierNumber = this.tierNumber;
                instance.minQuantity = this.minQuantity;
                instance.maxQuantity = this.maxQuantity;
                instance.unitPrice = this.unitPrice;
                return instance;
            }
        }
    }

    public static class Savings {
        private BigDecimal amount;
        private BigDecimal percentage;
        private String description;

        public Savings() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static SavingsBuilder builder() {
            return new SavingsBuilder();
        }

        public static class SavingsBuilder {
            private BigDecimal amount;
            private BigDecimal percentage;
            private String description;

            public SavingsBuilder amount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public SavingsBuilder percentage(BigDecimal percentage) {
                this.percentage = percentage;
                return this;
            }

            public SavingsBuilder description(String description) {
                this.description = description;
                return this;
            }

            public Savings build() {
                Savings instance = new Savings();
                instance.amount = this.amount;
                instance.percentage = this.percentage;
                instance.description = this.description;
                return instance;
            }
        }
    }

    public static class PriceCalculationResponseBuilder {
        private String status;
        private PriceCalculationData data;

        public PriceCalculationResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public PriceCalculationResponseBuilder data(PriceCalculationData data) {
            this.data = data;
            return this;
        }

        public PriceCalculationResponse build() {
            PriceCalculationResponse instance = new PriceCalculationResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
