package com.brandkit.catalog.dto.admin;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Discount Request DTO (Partner creates discount)
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
public class DiscountRequest {
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount must be greater than 0%")
    @DecimalMax(value = "25.00", message = "Discount cannot exceed 25%")
    private BigDecimal discountPercentage;
    
    @Size(max = 100, message = "Discount name cannot exceed 100 characters")
    private String discountName;
    
    private ZonedDateTime startDate;
    
    private ZonedDateTime endDate;

    public UUID getProductId() {
        return this.productId;
    }
    public BigDecimal getDiscountPercentage() {
        return this.discountPercentage;
    }
    public String getDiscountName() {
        return this.discountName;
    }
    public ZonedDateTime getStartDate() {
        return this.startDate;
    }
    public ZonedDateTime getEndDate() {
        return this.endDate;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }
    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
}
