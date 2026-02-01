package com.brandkit.partner.dto;

import jakarta.validation.constraints.Size;
/**
 * Proof Upload Request - FRD-005 FR-58
 * Request for uploading proof images
 */
public class ProofUploadRequest {

    @Size(max = 200, message = "Caption cannot exceed 200 characters")
    private String caption;

    private Integer displayOrder;

    public String getCaption() {
        return this.caption;
    }
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public ProofUploadRequest() {
    }
    public ProofUploadRequest(String caption, Integer displayOrder) {
        this.caption = caption;
        this.displayOrder = displayOrder;
    }
    public static ProofUploadRequestBuilder builder() {
        return new ProofUploadRequestBuilder();
    }

    public static class ProofUploadRequestBuilder {
        private String caption;
        private Integer displayOrder;

        ProofUploadRequestBuilder() {
        }

        public ProofUploadRequestBuilder caption(String caption) {
            this.caption = caption;
            return this;
        }

        public ProofUploadRequestBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public ProofUploadRequest build() {
            ProofUploadRequest instance = new ProofUploadRequest();
            instance.caption = this.caption;
            instance.displayOrder = this.displayOrder;
            return instance;
        }
    }
}
