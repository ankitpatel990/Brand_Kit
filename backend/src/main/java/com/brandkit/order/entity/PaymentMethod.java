package com.brandkit.order.entity;

/**
 * Payment method enum - FRD-004 FR-42
 */
public enum PaymentMethod {
    UPI("UPI"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    NET_BANKING("Net Banking"),
    WALLET("Wallet");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
