package com.brandkit.catalog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Product Image Entity
 * FRD-002 Sub-Prompt 8: Product Image Management
 */
@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "medium_url", columnDefinition = "TEXT")
    private String mediumUrl;

    @Column(name = "alt_text", length = 200)
    private String altText;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "file_size_bytes")
    private Integer fileSizeBytes;

    @Column
    private Integer width;

    @Column
    private Integer height;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    public UUID getId() {
        return this.id;
    }
    public Product getProduct() {
        return this.product;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }
    public String getMediumUrl() {
        return this.mediumUrl;
    }
    public String getAltText() {
        return this.altText;
    }
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    public Boolean getIsPrimary() {
        return this.isPrimary;
    }
    public Integer getFileSizeBytes() {
        return this.fileSizeBytes;
    }
    public Integer getWidth() {
        return this.width;
    }
    public Integer getHeight() {
        return this.height;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }
    public void setAltText(String altText) {
        this.altText = altText;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    public void setFileSizeBytes(Integer fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public ProductImage() {
    }
    public ProductImage(UUID id, Product product, String imageUrl, String thumbnailUrl, String mediumUrl, String altText, Integer displayOrder, Boolean isPrimary, Integer fileSizeBytes, Integer width, Integer height, ZonedDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.mediumUrl = mediumUrl;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
        this.fileSizeBytes = fileSizeBytes;
        this.width = width;
        this.height = height;
        this.createdAt = createdAt;
    }
    public static ProductImageBuilder builder() {
        return new ProductImageBuilder();
    }

    public static class ProductImageBuilder {
        private UUID id;
        private Product product;
        private String imageUrl;
        private String thumbnailUrl;
        private String mediumUrl;
        private String altText;
        private Integer displayOrder = 0;
        private Boolean isPrimary = false;
        private Integer fileSizeBytes;
        private Integer width;
        private Integer height;
        private ZonedDateTime createdAt;

        ProductImageBuilder() {
        }

        public ProductImageBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProductImageBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public ProductImageBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ProductImageBuilder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public ProductImageBuilder mediumUrl(String mediumUrl) {
            this.mediumUrl = mediumUrl;
            return this;
        }

        public ProductImageBuilder altText(String altText) {
            this.altText = altText;
            return this;
        }

        public ProductImageBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public ProductImageBuilder isPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public ProductImageBuilder fileSizeBytes(Integer fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
            return this;
        }

        public ProductImageBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public ProductImageBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public ProductImageBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProductImage build() {
            ProductImage instance = new ProductImage();
            instance.id = this.id;
            instance.product = this.product;
            instance.imageUrl = this.imageUrl;
            instance.thumbnailUrl = this.thumbnailUrl;
            instance.mediumUrl = this.mediumUrl;
            instance.altText = this.altText;
            instance.displayOrder = this.displayOrder;
            instance.isPrimary = this.isPrimary;
            instance.fileSizeBytes = this.fileSizeBytes;
            instance.width = this.width;
            instance.height = this.height;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}
