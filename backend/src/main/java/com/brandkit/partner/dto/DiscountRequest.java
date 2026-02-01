package com.brandkit.partner.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Discount Request - FRD-005 FR-64b
 * Request for creating/updating partner discount
 */
public class DiscountRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0", message = "Discount cannot be negative")
    @DecimalMax(value = "100", message = "Discount cannot exceed 100%")
    private BigDecimal discountPercentage;

    public UUID getProductId() {
        return this.productId;
    }
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public DiscountRequest() {
    }
    public DiscountRequest(UUID productId, BigDecimal discountPercentage) {
        this.productId = productId;
        this.discountPercentage = discountPercentage;
    }
    public static DiscountRequestBuilder builder() {
        return new DiscountRequestBuilder();
    }

    public static class DiscountRequestBuilder {
        private UUID productId;
        private BigDecimal discountPercentage;

        DiscountRequestBuilder() {
        }

        public DiscountRequestBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public DiscountRequestBuilder discountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public DiscountRequest build() {
            DiscountRequest instance = new DiscountRequest();
            instance.productId = this.productId;
            instance.discountPercentage = this.discountPercentage;
            return instance;
        }
    }
}
