package com.brandkit.order.dto;

import com.brandkit.order.entity.DeliveryOption;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for delivery options - FRD-004 FR-41
 */
public class DeliveryOptionResponse {

    private DeliveryOption option;
    private String displayName;
    private BigDecimal charge;
    private Boolean isFree;
    private String deliveryTimeRange;
    private LocalDate estimatedDeliveryStart;
    private LocalDate estimatedDeliveryEnd;
    private Boolean isAvailable;
    private String unavailableReason;

    // Getters and Setters
    public DeliveryOption getOption() {
        return option;
    }

    public void setOption(DeliveryOption option) {
        this.option = option;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public String getDeliveryTimeRange() {
        return deliveryTimeRange;
    }

    public void setDeliveryTimeRange(String deliveryTimeRange) {
        this.deliveryTimeRange = deliveryTimeRange;
    }

    public LocalDate getEstimatedDeliveryStart() {
        return estimatedDeliveryStart;
    }

    public void setEstimatedDeliveryStart(LocalDate estimatedDeliveryStart) {
        this.estimatedDeliveryStart = estimatedDeliveryStart;
    }

    public LocalDate getEstimatedDeliveryEnd() {
        return estimatedDeliveryEnd;
    }

    public void setEstimatedDeliveryEnd(LocalDate estimatedDeliveryEnd) {
        this.estimatedDeliveryEnd = estimatedDeliveryEnd;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }

    public void setUnavailableReason(String unavailableReason) {
        this.unavailableReason = unavailableReason;
    }
}
