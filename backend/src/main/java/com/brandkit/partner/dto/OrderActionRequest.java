package com.brandkit.partner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * Order Action Request - FRD-005 FR-56
 * Request for accepting or rejecting orders
 */
public class OrderActionRequest {

    // For rejection
    private String reason;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public String getReason() {
        return this.reason;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public OrderActionRequest() {
    }
    public OrderActionRequest(String reason, String notes) {
        this.reason = reason;
        this.notes = notes;
    }
    public static OrderActionRequestBuilder builder() {
        return new OrderActionRequestBuilder();
    }

    public static class OrderActionRequestBuilder {
        private String reason;
        private String notes;

        OrderActionRequestBuilder() {
        }

        public OrderActionRequestBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public OrderActionRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public OrderActionRequest build() {
            OrderActionRequest instance = new OrderActionRequest();
            instance.reason = this.reason;
            instance.notes = this.notes;
            return instance;
        }
    }
}
