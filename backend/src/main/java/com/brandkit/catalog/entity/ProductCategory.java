package com.brandkit.catalog.entity;

/**
 * Product Category Enum
 * FRD-002 FR-14: Category Structure
 */
public enum ProductCategory {
    BAGS("Bags"),
    PENS("Pens"),
    WATER_BOTTLES("Water Bottles"),
    DIARIES("Diaries"),
    T_SHIRTS("T-Shirts"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSlug() {
        return name().toLowerCase().replace("_", "-");
    }
}
