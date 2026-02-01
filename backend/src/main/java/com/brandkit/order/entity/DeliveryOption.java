package com.brandkit.order.entity;

import java.math.BigDecimal;

/**
 * Delivery option enum - FRD-004 FR-41
 */
public enum DeliveryOption {
    STANDARD("Standard Delivery", new BigDecimal("100.00"), 7, 14),
    EXPRESS("Express Delivery", new BigDecimal("300.00"), 3, 5);

    private final String displayName;
    private final BigDecimal baseCharge;
    private final int minDays;
    private final int maxDays;

    DeliveryOption(String displayName, BigDecimal baseCharge, int minDays, int maxDays) {
        this.displayName = displayName;
        this.baseCharge = baseCharge;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBaseCharge() {
        return baseCharge;
    }

    public int getMinDays() {
        return minDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    /**
     * Get delivery charge based on order total
     * Standard is free for orders > ₹10,000
     */
    public BigDecimal getDeliveryCharge(BigDecimal orderSubtotal) {
        if (this == STANDARD && orderSubtotal.compareTo(new BigDecimal("10000")) > 0) {
            return BigDecimal.ZERO;
        }
        return this.baseCharge;
    }
}
