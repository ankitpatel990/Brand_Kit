package com.brandkit.order.dto;

import com.brandkit.order.entity.OrderStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for order status history - FRD-004 FR-46
 * Note: internalNotes is NEVER included (client-facing)
 */
public class OrderStatusHistoryResponse {

    private UUID id;
    private OrderStatus status;
    private String statusDisplayName;
    private String description;
    private OffsetDateTime timestamp;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
