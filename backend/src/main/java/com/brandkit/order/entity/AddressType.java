package com.brandkit.order.entity;

/**
 * Address type enum - FRD-004 FR-41
 */
public enum AddressType {
    HOME("Home"),
    OFFICE("Office"),
    OTHER("Other");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
