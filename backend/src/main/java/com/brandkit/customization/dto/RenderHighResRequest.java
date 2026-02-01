package com.brandkit.customization.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Render High-Resolution Image Request DTO
 * FRD-003 Sub-Prompt 4: Server-Side High-Resolution Rendering
 */
public class RenderHighResRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Logo file ID is required")
    private UUID logoFileId;

    @NotNull(message = "Crop data is required")
    private CropDataRequest cropData;

    public UUID getOrderId() {
        return this.orderId;
    }
    public UUID getProductId() {
        return this.productId;
    }
    public UUID getLogoFileId() {
        return this.logoFileId;
    }
    public CropDataRequest getCropData() {
        return this.cropData;
    }
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setLogoFileId(UUID logoFileId) {
        this.logoFileId = logoFileId;
    }
    public void setCropData(CropDataRequest cropData) {
        this.cropData = cropData;
    }
    public RenderHighResRequest() {
    }
    public RenderHighResRequest(UUID orderId, UUID productId, UUID logoFileId, CropDataRequest cropData) {
        this.orderId = orderId;
        this.productId = productId;
        this.logoFileId = logoFileId;
        this.cropData = cropData;
    }
    public static RenderHighResRequestBuilder builder() {
        return new RenderHighResRequestBuilder();
    }

    public static class RenderHighResRequestBuilder {
        private UUID orderId;
        private UUID productId;
        private UUID logoFileId;
        private CropDataRequest cropData;

        RenderHighResRequestBuilder() {
        }

        public RenderHighResRequestBuilder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public RenderHighResRequestBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public RenderHighResRequestBuilder logoFileId(UUID logoFileId) {
            this.logoFileId = logoFileId;
            return this;
        }

        public RenderHighResRequestBuilder cropData(CropDataRequest cropData) {
            this.cropData = cropData;
            return this;
        }

        public RenderHighResRequest build() {
            RenderHighResRequest instance = new RenderHighResRequest();
            instance.orderId = this.orderId;
            instance.productId = this.productId;
            instance.logoFileId = this.logoFileId;
            instance.cropData = this.cropData;
            return instance;
        }
    }
}
