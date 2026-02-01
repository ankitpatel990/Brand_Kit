package com.brandkit.order.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * PIN code serviceability entity - FRD-004 BR-37
 * Checks if a PIN code is serviceable for delivery
 */
@Entity
@Table(name = "pin_code_serviceability")
public class PinCodeServiceability {

    @Id
    @Column(name = "pin_code", length = 6)
    private String pinCode;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "is_serviceable", nullable = false)
    private Boolean isServiceable = true;

    @Column(name = "standard_delivery_days")
    private Integer standardDeliveryDays = 10;

    @Column(name = "express_available")
    private Boolean expressAvailable = true;

    @Column(name = "express_delivery_days")
    private Integer expressDeliveryDays = 4;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getIsServiceable() {
        return isServiceable;
    }

    public void setIsServiceable(Boolean isServiceable) {
        this.isServiceable = isServiceable;
    }

    public Integer getStandardDeliveryDays() {
        return standardDeliveryDays;
    }

    public void setStandardDeliveryDays(Integer standardDeliveryDays) {
        this.standardDeliveryDays = standardDeliveryDays;
    }

    public Boolean getExpressAvailable() {
        return expressAvailable;
    }

    public void setExpressAvailable(Boolean expressAvailable) {
        this.expressAvailable = expressAvailable;
    }

    public Integer getExpressDeliveryDays() {
        return expressDeliveryDays;
    }

    public void setExpressDeliveryDays(Integer expressDeliveryDays) {
        this.expressDeliveryDays = expressDeliveryDays;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Get delivery days based on delivery option
     */
    public Integer getDeliveryDays(DeliveryOption option) {
        return switch (option) {
            case EXPRESS -> expressDeliveryDays;
            case STANDARD -> standardDeliveryDays;
        };
    }

    /**
     * Check if express delivery is available
     */
    public boolean canUseExpress() {
        return isServiceable && expressAvailable;
    }
}
