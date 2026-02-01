package com.brandkit.catalog.dto.admin;

import com.brandkit.catalog.entity.ProductCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Discount Limit Request DTO
 * FRD-002 Sub-Prompt 7: Admin sets global discount limits
 */
public class DiscountLimitRequest {
    
    @NotNull(message = "Minimum discount percentage is required")
    @DecimalMin(value = "0", message = "Minimum discount cannot be negative")
    private BigDecimal minDiscountPercentage;
    
    @NotNull(message = "Maximum discount percentage is required")
    @DecimalMax(value = "50", message = "Maximum discount cannot exceed 50%")
    private BigDecimal maxDiscountPercentage;
    
    private ProductCategory category; // NULL for global limit

    public BigDecimal getMinDiscountPercentage() {
        return this.minDiscountPercentage;
    }
    public BigDecimal getMaxDiscountPercentage() {
        return this.maxDiscountPercentage;
    }
    public ProductCategory getCategory() {
        return this.category;
    }
    public void setMinDiscountPercentage(BigDecimal minDiscountPercentage) {
        this.minDiscountPercentage = minDiscountPercentage;
    }
    public void setMaxDiscountPercentage(BigDecimal maxDiscountPercentage) {
        this.maxDiscountPercentage = maxDiscountPercentage;
    }
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
}
