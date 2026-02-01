package com.brandkit.catalog.entity;

/**
 * Customization Type Enum
 * FRD-002 FR-15: Product Data Model
 */
public enum CustomizationType {
    LOGO_PRINT("Logo Print"),
    EMBROIDERY("Embroidery"),
    ENGRAVING("Engraving"),
    NONE("None");

    private final String displayName;

    CustomizationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
