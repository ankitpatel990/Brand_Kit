package com.brandkit.partner.dto;

import jakarta.validation.constraints.NotBlank;
/**
 * Status Update Request - FRD-005 FR-57
 * Request for updating production status
 */
public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // IN_PRODUCTION, PROOF_READY, PRODUCTION_COMPLETE, READY_TO_SHIP

    private String notes;

    public String getStatus() {
        return this.status;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public StatusUpdateRequest() {
    }
    public StatusUpdateRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }
    public static StatusUpdateRequestBuilder builder() {
        return new StatusUpdateRequestBuilder();
    }

    public static class StatusUpdateRequestBuilder {
        private String status;
        private String notes;

        StatusUpdateRequestBuilder() {
        }

        public StatusUpdateRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        public StatusUpdateRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public StatusUpdateRequest build() {
            StatusUpdateRequest instance = new StatusUpdateRequest();
            instance.status = this.status;
            instance.notes = this.notes;
            return instance;
        }
    }
}
