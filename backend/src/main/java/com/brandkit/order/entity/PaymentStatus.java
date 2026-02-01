package com.brandkit.order.entity;

/**
 * Payment status enum - FRD-004 FR-42
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    EXPIRED,
    REFUNDED
}
