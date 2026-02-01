package com.brandkit.catalog.dto;

import java.util.List;
import java.util.UUID;

/**
 * Category Response DTO
 * FRD-002 FR-14: Category Structure
 */
public class CategoryResponse {
    private String status;
    private List<CategoryData> data;

    public CategoryResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CategoryData> getData() {
        return data;
    }

    public void setData(List<CategoryData> data) {
        this.data = data;
    }

    public static CategoryResponseBuilder builder() {
        return new CategoryResponseBuilder();
    }

    public static class CategoryData {
        private UUID id;
        private String name;
        private String slug;
        private String description;
        private String imageUrl;
        private int displayOrder;
        private int productCount;

        public CategoryData() {
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
        }

        public int getProductCount() {
            return productCount;
        }

        public void setProductCount(int productCount) {
            this.productCount = productCount;
        }

        public static CategoryDataBuilder builder() {
            return new CategoryDataBuilder();
        }

        public static class CategoryDataBuilder {
            private UUID id;
            private String name;
            private String slug;
            private String description;
            private String imageUrl;
            private int displayOrder;
            private int productCount;

            public CategoryDataBuilder id(UUID id) {
                this.id = id;
                return this;
            }

            public CategoryDataBuilder name(String name) {
                this.name = name;
                return this;
            }

            public CategoryDataBuilder slug(String slug) {
                this.slug = slug;
                return this;
            }

            public CategoryDataBuilder description(String description) {
                this.description = description;
                return this;
            }

            public CategoryDataBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public CategoryDataBuilder displayOrder(int displayOrder) {
                this.displayOrder = displayOrder;
                return this;
            }

            public CategoryDataBuilder productCount(int productCount) {
                this.productCount = productCount;
                return this;
            }

            public CategoryData build() {
                CategoryData instance = new CategoryData();
                instance.id = this.id;
                instance.name = this.name;
                instance.slug = this.slug;
                instance.description = this.description;
                instance.imageUrl = this.imageUrl;
                instance.displayOrder = this.displayOrder;
                instance.productCount = this.productCount;
                return instance;
            }
        }
    }

    public static class CategoryResponseBuilder {
        private String status;
        private List<CategoryData> data;

        public CategoryResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public CategoryResponseBuilder data(List<CategoryData> data) {
            this.data = data;
            return this;
        }

        public CategoryResponse build() {
            CategoryResponse instance = new CategoryResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
