package com.brandkit.order.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * Request DTO for reorder - FRD-004 Sub-Prompt 10
 */
public class ReorderRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
