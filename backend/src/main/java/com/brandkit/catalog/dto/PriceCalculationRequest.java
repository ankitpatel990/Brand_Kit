package com.brandkit.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
/**
 * Price Calculation Request DTO
 * FRD-002 Sub-Prompt 6: Dynamic Price Calculator
 */
public class PriceCalculationRequest {
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Minimum quantity is 1")
    @Max(value = 10000, message = "Maximum quantity is 10,000")
    private Integer quantity;
    
    private Boolean customization;
    
    private String customizationType;

    public Integer getQuantity() {
        return this.quantity;
    }
    public Boolean getCustomization() {
        return this.customization;
    }
    public String getCustomizationType() {
        return this.customizationType;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public void setCustomization(Boolean customization) {
        this.customization = customization;
    }
    public void setCustomizationType(String customizationType) {
        this.customizationType = customizationType;
    }
}
