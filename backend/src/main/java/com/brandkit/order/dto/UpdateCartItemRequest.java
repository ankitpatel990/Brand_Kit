package com.brandkit.order.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for updating cart item quantity - FRD-004 FR-39
 */
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10,000")
    private Integer quantity;

    // Getters and Setters
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
