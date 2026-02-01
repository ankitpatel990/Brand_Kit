package com.brandkit.catalog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Category Entity
 * FRD-002 FR-14: Category Structure
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "product_count", nullable = false)
    private Integer productCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getSlug() {
        return this.slug;
    }
    public String getDescription() {
        return this.description;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public Integer getProductCount() {
        return this.productCount;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Category() {
    }
    public Category(UUID id, String name, String slug, String description, String imageUrl, Integer displayOrder, Boolean isActive, Integer productCount, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.productCount = productCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private UUID id;
        private String name;
        private String slug;
        private String description;
        private String imageUrl;
        private Integer displayOrder = 0;
        private Boolean isActive = true;
        private Integer productCount = 0;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        CategoryBuilder() {
        }

        public CategoryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public CategoryBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CategoryBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CategoryBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public CategoryBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CategoryBuilder productCount(Integer productCount) {
            this.productCount = productCount;
            return this;
        }

        public CategoryBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CategoryBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Category build() {
            Category instance = new Category();
            instance.id = this.id;
            instance.name = this.name;
            instance.slug = this.slug;
            instance.description = this.description;
            instance.imageUrl = this.imageUrl;
            instance.displayOrder = this.displayOrder;
            instance.isActive = this.isActive;
            instance.productCount = this.productCount;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
