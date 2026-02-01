package com.brandkit.partner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Shipment Request - FRD-005 FR-59
 * Request for marking order as shipped
 */
public class ShipmentRequest {

    @NotBlank(message = "Courier name is required")
    @Size(max = 100, message = "Courier name cannot exceed 100 characters")
    private String courierName;

    @NotBlank(message = "Tracking ID is required")
    @Size(min = 5, max = 50, message = "Tracking ID must be 5-50 characters")
    private String trackingId;

    @NotNull(message = "Ship date is required")
    @PastOrPresent(message = "Ship date cannot be in the future")
    private LocalDate shipDate;

    private BigDecimal weightKg;

    private Integer numPackages;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public String getCourierName() {
        return this.courierName;
    }
    public String getTrackingId() {
        return this.trackingId;
    }
    public LocalDate getShipDate() {
        return this.shipDate;
    }
    public BigDecimal getWeightKg() {
        return this.weightKg;
    }
    public Integer getNumPackages() {
        return this.numPackages;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    public void setShipDate(LocalDate shipDate) {
        this.shipDate = shipDate;
    }
    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }
    public void setNumPackages(Integer numPackages) {
        this.numPackages = numPackages;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public ShipmentRequest() {
    }
    public ShipmentRequest(String courierName, String trackingId, LocalDate shipDate, BigDecimal weightKg, Integer numPackages, String notes) {
        this.courierName = courierName;
        this.trackingId = trackingId;
        this.shipDate = shipDate;
        this.weightKg = weightKg;
        this.numPackages = numPackages;
        this.notes = notes;
    }
    public static ShipmentRequestBuilder builder() {
        return new ShipmentRequestBuilder();
    }

    public static class ShipmentRequestBuilder {
        private String courierName;
        private String trackingId;
        private LocalDate shipDate;
        private BigDecimal weightKg;
        private Integer numPackages;
        private String notes;

        ShipmentRequestBuilder() {
        }

        public ShipmentRequestBuilder courierName(String courierName) {
            this.courierName = courierName;
            return this;
        }

        public ShipmentRequestBuilder trackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public ShipmentRequestBuilder shipDate(LocalDate shipDate) {
            this.shipDate = shipDate;
            return this;
        }

        public ShipmentRequestBuilder weightKg(BigDecimal weightKg) {
            this.weightKg = weightKg;
            return this;
        }

        public ShipmentRequestBuilder numPackages(Integer numPackages) {
            this.numPackages = numPackages;
            return this;
        }

        public ShipmentRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ShipmentRequest build() {
            ShipmentRequest instance = new ShipmentRequest();
            instance.courierName = this.courierName;
            instance.trackingId = this.trackingId;
            instance.shipDate = this.shipDate;
            instance.weightKg = this.weightKg;
            instance.numPackages = this.numPackages;
            instance.notes = this.notes;
            return instance;
        }
    }
}
