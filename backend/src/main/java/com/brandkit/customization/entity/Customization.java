package com.brandkit.customization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customization Entity
 * FRD-003: Completed customizations linked to orders
 * 
 * Stores finalized customization data with print-ready images.
 */
@Entity
@Table(name = "customizations")
public class Customization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    // Logo file information
    @Column(name = "logo_file_id", nullable = false)
    private UUID logoFileId;

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

    // Preview and print images
    @Column(name = "preview_image_url", length = 2048)
    private String previewImageUrl;

    @Column(name = "print_image_url", length = 2048)
    private String printImageUrl; // High-res print-ready image (300 DPI)

    // Status
    @Column(name = "status", length = 50)
    private String status = "pending"; // pending, processing, completed, failed

    @Column(name = "print_image_generated_at")
    private LocalDateTime printImageGeneratedAt;

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
    public UUID getLogoFileId() {
        return this.logoFileId;
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
    public String getPrintImageUrl() {
        return this.printImageUrl;
    }
    public String getStatus() {
        return this.status;
    }
    public LocalDateTime getPrintImageGeneratedAt() {
        return this.printImageGeneratedAt;
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
    public void setLogoFileId(UUID logoFileId) {
        this.logoFileId = logoFileId;
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
    public void setPrintImageUrl(String printImageUrl) {
        this.printImageUrl = printImageUrl;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPrintImageGeneratedAt(LocalDateTime printImageGeneratedAt) {
        this.printImageGeneratedAt = printImageGeneratedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Customization() {
    }
    public Customization(UUID id, UUID userId, UUID productId, UUID logoFileId, String logoFileUrl, String logoFileName, Long logoFileSize, String logoDimensions, String cropData, String croppedImageUrl, String previewImageUrl, String printImageUrl, String status, LocalDateTime printImageGeneratedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.logoFileId = logoFileId;
        this.logoFileUrl = logoFileUrl;
        this.logoFileName = logoFileName;
        this.logoFileSize = logoFileSize;
        this.logoDimensions = logoDimensions;
        this.cropData = cropData;
        this.croppedImageUrl = croppedImageUrl;
        this.previewImageUrl = previewImageUrl;
        this.printImageUrl = printImageUrl;
        this.status = status;
        this.printImageGeneratedAt = printImageGeneratedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static CustomizationBuilder builder() {
        return new CustomizationBuilder();
    }

    public static class CustomizationBuilder {
        private UUID id;
        private UUID userId;
        private UUID productId;
        private UUID logoFileId;
        private String logoFileUrl;
        private String logoFileName;
        private Long logoFileSize;
        private String logoDimensions;
        private String cropData;
        private String croppedImageUrl;
        private String previewImageUrl;
        private String printImageUrl;
        private String status = "pending";
        private LocalDateTime printImageGeneratedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        CustomizationBuilder() {
        }

        public CustomizationBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CustomizationBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public CustomizationBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public CustomizationBuilder logoFileId(UUID logoFileId) {
            this.logoFileId = logoFileId;
            return this;
        }

        public CustomizationBuilder logoFileUrl(String logoFileUrl) {
            this.logoFileUrl = logoFileUrl;
            return this;
        }

        public CustomizationBuilder logoFileName(String logoFileName) {
            this.logoFileName = logoFileName;
            return this;
        }

        public CustomizationBuilder logoFileSize(Long logoFileSize) {
            this.logoFileSize = logoFileSize;
            return this;
        }

        public CustomizationBuilder logoDimensions(String logoDimensions) {
            this.logoDimensions = logoDimensions;
            return this;
        }

        public CustomizationBuilder cropData(String cropData) {
            this.cropData = cropData;
            return this;
        }

        public CustomizationBuilder croppedImageUrl(String croppedImageUrl) {
            this.croppedImageUrl = croppedImageUrl;
            return this;
        }

        public CustomizationBuilder previewImageUrl(String previewImageUrl) {
            this.previewImageUrl = previewImageUrl;
            return this;
        }

        public CustomizationBuilder printImageUrl(String printImageUrl) {
            this.printImageUrl = printImageUrl;
            return this;
        }

        public CustomizationBuilder status(String status) {
            this.status = status;
            return this;
        }

        public CustomizationBuilder printImageGeneratedAt(LocalDateTime printImageGeneratedAt) {
            this.printImageGeneratedAt = printImageGeneratedAt;
            return this;
        }

        public CustomizationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CustomizationBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Customization build() {
            Customization instance = new Customization();
            instance.id = this.id;
            instance.userId = this.userId;
            instance.productId = this.productId;
            instance.logoFileId = this.logoFileId;
            instance.logoFileUrl = this.logoFileUrl;
            instance.logoFileName = this.logoFileName;
            instance.logoFileSize = this.logoFileSize;
            instance.logoDimensions = this.logoDimensions;
            instance.cropData = this.cropData;
            instance.croppedImageUrl = this.croppedImageUrl;
            instance.previewImageUrl = this.previewImageUrl;
            instance.printImageUrl = this.printImageUrl;
            instance.status = this.status;
            instance.printImageGeneratedAt = this.printImageGeneratedAt;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
