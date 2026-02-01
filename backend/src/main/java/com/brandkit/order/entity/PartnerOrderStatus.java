package com.brandkit.order.entity;

/**
 * Partner order status enum (INTERNAL ONLY) - FRD-004 FR-45
 * These statuses are NEVER exposed to clients
 */
public enum PartnerOrderStatus {
    AWAITING_ACCEPTANCE,
    ACCEPTED,
    REJECTED,
    IN_PRODUCTION,
    READY_TO_SHIP,
    SHIPPED,
    DELIVERED
}
