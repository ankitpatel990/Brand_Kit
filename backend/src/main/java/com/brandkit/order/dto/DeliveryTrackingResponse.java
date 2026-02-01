package com.brandkit.order.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for delivery tracking information - FRD-004 Sub-Prompt 7
 * Note: This DTO never exposes internal partner information
 */
public class DeliveryTrackingResponse {

    private UUID orderId;
    private String orderNumber;
    private String status;
    private String statusDescription;
    private String trackingId;
    private String courierName;
    private String trackingUrl;
    private LocalDate estimatedDeliveryStart;
    private LocalDate estimatedDeliveryEnd;
    private LocalDate deliveredOn;

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
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

    public LocalDate getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(LocalDate deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    /**
     * Get formatted estimated delivery range
     */
    public String getEstimatedDeliveryRange() {
        if (estimatedDeliveryStart != null && estimatedDeliveryEnd != null) {
            return estimatedDeliveryStart.toString() + " to " + estimatedDeliveryEnd.toString();
        }
        return null;
    }
}
