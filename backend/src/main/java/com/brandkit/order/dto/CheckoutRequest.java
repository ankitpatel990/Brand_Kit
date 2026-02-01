package com.brandkit.order.dto;

import com.brandkit.order.entity.DeliveryOption;
import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * Request DTO for checkout - FRD-004 FR-41
 */
public class CheckoutRequest {

    @NotNull(message = "Delivery address is required")
    private UUID deliveryAddressId;

    @NotNull(message = "Delivery option is required")
    private DeliveryOption deliveryOption;

    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "Please accept Terms & Conditions")
    private Boolean termsAccepted;

    private String notes;

    // Getters and Setters
    public UUID getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(UUID deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
