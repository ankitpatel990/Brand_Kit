package com.brandkit.order.entity;

/**
 * Order status enum - FRD-004 FR-46
 * Status progression for orders (client-facing)
 */
public enum OrderStatus {
    PENDING_PAYMENT("Pending Payment"),
    PAYMENT_FAILED("Payment Failed"),
    CONFIRMED("Confirmed"),
    ACCEPTED("Processing"), // Internal: partner_accepted, shown to client as "Processing"
    IN_PRODUCTION("In Production"),
    READY_TO_SHIP("Ready to Ship"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REFUND_INITIATED("Refund Initiated"),
    REFUNDED("Refunded");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get client-facing display name (hides internal terminology)
     */
    public String getClientDisplayName() {
        return switch (this) {
            case ACCEPTED -> "Processing";
            case IN_PRODUCTION -> "In Production";
            default -> this.displayName;
        };
    }

    /**
     * Check if order can be modified
     */
    public boolean isModifiable() {
        return this == PENDING_PAYMENT || this == PAYMENT_FAILED;
    }

    /**
     * Check if order is in a final state
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Check if order is active
     */
    public boolean isActive() {
        return this == CONFIRMED || this == ACCEPTED || this == IN_PRODUCTION || 
               this == READY_TO_SHIP || this == SHIPPED || this == OUT_FOR_DELIVERY;
    }
}
