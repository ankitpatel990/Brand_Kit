package com.brandkit.order.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * Request DTO for initiating payment - FRD-004 FR-42
 */
public class PaymentInitiateRequest {

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
