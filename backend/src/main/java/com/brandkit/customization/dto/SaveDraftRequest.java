package com.brandkit.customization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Save Draft Request DTO
 * FRD-003 Sub-Prompt 7: Save Draft Customization
 */
public class SaveDraftRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Logo file URL is required")
    private String logoFileUrl;

    @NotBlank(message = "Logo file name is required")
    private String logoFileName;

    @NotNull(message = "Logo file size is required")
    private Long logoFileSize;

    private String logoDimensions; // JSON string

    @NotNull(message = "Crop data is required")
    private CropDataRequest cropData;

    @NotBlank(message = "Cropped image URL is required")
    private String croppedImageUrl;

    private String previewImageUrl;

    private UUID bundleId;
    private String bundleName;

    public UUID getProductId() {
        return this.productId;
    }
    public String getLogoFileUrl() {
        return this.logoFileUrl;
    }
    public String getLogoFileName() {
        return this.logoFileName;
    }
    public Long getLogoFileSize() {
        return this.logoFileSize;
    }
    public String getLogoDimensions() {
        return this.logoDimensions;
    }
    public CropDataRequest getCropData() {
        return this.cropData;
    }
    public String getCroppedImageUrl() {
        return this.croppedImageUrl;
    }
    public String getPreviewImageUrl() {
        return this.previewImageUrl;
    }
    public UUID getBundleId() {
        return this.bundleId;
    }
    public String getBundleName() {
        return this.bundleName;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setLogoFileUrl(String logoFileUrl) {
        this.logoFileUrl = logoFileUrl;
    }
    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }
    public void setLogoFileSize(Long logoFileSize) {
        this.logoFileSize = logoFileSize;
    }
    public void setLogoDimensions(String logoDimensions) {
        this.logoDimensions = logoDimensions;
    }
    public void setCropData(CropDataRequest cropData) {
        this.cropData = cropData;
    }
    public void setCroppedImageUrl(String croppedImageUrl) {
        this.croppedImageUrl = croppedImageUrl;
    }
    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }
    public void setBundleId(UUID bundleId) {
        this.bundleId = bundleId;
    }
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
    public SaveDraftRequest() {
    }
    public SaveDraftRequest(UUID productId, String logoFileUrl, String logoFileName, Long logoFileSize, String logoDimensions, CropDataRequest cropData, String croppedImageUrl, String previewImageUrl, UUID bundleId, String bundleName) {
        this.productId = productId;
        this.logoFileUrl = logoFileUrl;
        this.logoFileName = logoFileName;
        this.logoFileSize = logoFileSize;
        this.logoDimensions = logoDimensions;
        this.cropData = cropData;
        this.croppedImageUrl = croppedImageUrl;
        this.previewImageUrl = previewImageUrl;
        this.bundleId = bundleId;
        this.bundleName = bundleName;
    }
    public static SaveDraftRequestBuilder builder() {
        return new SaveDraftRequestBuilder();
    }

    public static class SaveDraftRequestBuilder {
        private UUID productId;
        private String logoFileUrl;
        private String logoFileName;
        private Long logoFileSize;
        private String logoDimensions;
        private CropDataRequest cropData;
        private String croppedImageUrl;
        private String previewImageUrl;
        private UUID bundleId;
        private String bundleName;

        SaveDraftRequestBuilder() {
        }

        public SaveDraftRequestBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public SaveDraftRequestBuilder logoFileUrl(String logoFileUrl) {
            this.logoFileUrl = logoFileUrl;
            return this;
        }

        public SaveDraftRequestBuilder logoFileName(String logoFileName) {
            this.logoFileName = logoFileName;
            return this;
        }

        public SaveDraftRequestBuilder logoFileSize(Long logoFileSize) {
            this.logoFileSize = logoFileSize;
            return this;
        }

        public SaveDraftRequestBuilder logoDimensions(String logoDimensions) {
            this.logoDimensions = logoDimensions;
            return this;
        }

        public SaveDraftRequestBuilder cropData(CropDataRequest cropData) {
            this.cropData = cropData;
            return this;
        }

        public SaveDraftRequestBuilder croppedImageUrl(String croppedImageUrl) {
            this.croppedImageUrl = croppedImageUrl;
            return this;
        }

        public SaveDraftRequestBuilder previewImageUrl(String previewImageUrl) {
            this.previewImageUrl = previewImageUrl;
            return this;
        }

        public SaveDraftRequestBuilder bundleId(UUID bundleId) {
            this.bundleId = bundleId;
            return this;
        }

        public SaveDraftRequestBuilder bundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        public SaveDraftRequest build() {
            SaveDraftRequest instance = new SaveDraftRequest();
            instance.productId = this.productId;
            instance.logoFileUrl = this.logoFileUrl;
            instance.logoFileName = this.logoFileName;
            instance.logoFileSize = this.logoFileSize;
            instance.logoDimensions = this.logoDimensions;
            instance.cropData = this.cropData;
            instance.croppedImageUrl = this.croppedImageUrl;
            instance.previewImageUrl = this.previewImageUrl;
            instance.bundleId = this.bundleId;
            instance.bundleName = this.bundleName;
            return instance;
        }
    }
}
