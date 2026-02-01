package com.brandkit.order.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * Request DTO for adding item to cart - FRD-004 FR-39
 */
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    private UUID customizationId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10,000")
    private Integer quantity;

    // Getters and Setters
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(UUID customizationId) {
        this.customizationId = customizationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
