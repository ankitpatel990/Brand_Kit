package com.brandkit.customization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customization Draft Entity
 * FRD-003 Sub-Prompt 7: Draft Customization Save/Load
 * 
 * Stores work-in-progress customizations that users can save and return to later.
 * Drafts expire after 30 days.
 */
@Entity
@Table(name = "customization_drafts")
public class CustomizationDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    // Logo file information
    @Column(name = "logo_file_url", nullable = false, length = 2048)
    private String logoFileUrl;

    @Column(name = "logo_file_name", nullable = false, length = 255)
    private String logoFileName;

    @Column(name = "logo_file_size", nullable = false)
    private Long logoFileSize;

    @Column(name = "logo_dimensions", columnDefinition = "jsonb")
    private String logoDimensions; // JSON: {width: number, height: number}

    // Crop data (JSON)
    @Column(name = "crop_data", nullable = false, columnDefinition = "jsonb")
    private String cropData; // JSON: {x, y, width, height, zoom, aspectRatio}

    @Column(name = "cropped_image_url", nullable = false, length = 2048)
    private String croppedImageUrl;

    // Preview
    @Column(name = "preview_image_url", length = 2048)
    private String previewImageUrl;

    // Bundle information
    @Column(name = "bundle_id")
    private UUID bundleId;

    @Column(name = "bundle_name", length = 255)
    private String bundleName;

    // Status
    @Column(name = "status", length = 50)
    private String status = "draft"; // draft, completed

    // Expiry
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getId() {
        return this.id;
    }
    public UUID getUserId() {
        return this.userId;
    }
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
    public String getCropData() {
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
    public String getStatus() {
        return this.status;
    }
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
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
    public void setCropData(String cropData) {
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
    public void setStatus(String status) {
        this.status = status;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public CustomizationDraft() {
    }
    public CustomizationDraft(UUID id, UUID userId, UUID productId, String logoFileUrl, String logoFileName, Long logoFileSize, String logoDimensions, String cropData, String croppedImageUrl, String previewImageUrl, UUID bundleId, String bundleName, String status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
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
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static CustomizationDraftBuilder builder() {
        return new CustomizationDraftBuilder();
    }

    public static class CustomizationDraftBuilder {
        private UUID id;
        private UUID userId;
        private UUID productId;
        private String logoFileUrl;
        private String logoFileName;
        private Long logoFileSize;
        private String logoDimensions;
        private String cropData;
        private String croppedImageUrl;
        private String previewImageUrl;
        private UUID bundleId;
        private String bundleName;
        private String status = "draft";
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        CustomizationDraftBuilder() {
        }

        public CustomizationDraftBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CustomizationDraftBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public CustomizationDraftBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public CustomizationDraftBuilder logoFileUrl(String logoFileUrl) {
            this.logoFileUrl = logoFileUrl;
            return this;
        }

        public CustomizationDraftBuilder logoFileName(String logoFileName) {
            this.logoFileName = logoFileName;
            return this;
        }

        public CustomizationDraftBuilder logoFileSize(Long logoFileSize) {
            this.logoFileSize = logoFileSize;
            return this;
        }

        public CustomizationDraftBuilder logoDimensions(String logoDimensions) {
            this.logoDimensions = logoDimensions;
            return this;
        }

        public CustomizationDraftBuilder cropData(String cropData) {
            this.cropData = cropData;
            return this;
        }

        public CustomizationDraftBuilder croppedImageUrl(String croppedImageUrl) {
            this.croppedImageUrl = croppedImageUrl;
            return this;
        }

        public CustomizationDraftBuilder previewImageUrl(String previewImageUrl) {
            this.previewImageUrl = previewImageUrl;
            return this;
        }

        public CustomizationDraftBuilder bundleId(UUID bundleId) {
            this.bundleId = bundleId;
            return this;
        }

        public CustomizationDraftBuilder bundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        public CustomizationDraftBuilder status(String status) {
            this.status = status;
            return this;
        }

        public CustomizationDraftBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public CustomizationDraftBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CustomizationDraftBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CustomizationDraft build() {
            CustomizationDraft instance = new CustomizationDraft();
            instance.id = this.id;
            instance.userId = this.userId;
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
            instance.status = this.status;
            instance.expiresAt = this.expiresAt;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
