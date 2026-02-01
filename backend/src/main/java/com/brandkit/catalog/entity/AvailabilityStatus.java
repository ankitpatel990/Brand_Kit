package com.brandkit.catalog.entity;

/**
 * Availability Status Enum
 * FRD-002 FR-24: Product Availability Status
 */
public enum AvailabilityStatus {
    AVAILABLE("In Stock - Ready to Customize"),
    LIMITED("Limited Availability"),
    OUT_OF_STOCK("Currently Unavailable"),
    COMING_SOON("Coming Soon - Notify Me");

    private final String displayMessage;

    AvailabilityStatus(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }
}
