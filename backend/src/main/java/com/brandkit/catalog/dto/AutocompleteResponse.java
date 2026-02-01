package com.brandkit.catalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Autocomplete Response DTO
 * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
 */
public class AutocompleteResponse {
    private String status;
    private AutocompleteData data;

    public AutocompleteResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AutocompleteData getData() {
        return data;
    }

    public void setData(AutocompleteData data) {
        this.data = data;
    }

    public static AutocompleteResponseBuilder builder() {
        return new AutocompleteResponseBuilder();
    }

    public static class AutocompleteData {
        private String query;
        private List<ProductSuggestion> products;
        private List<CategorySuggestion> categories;

        public AutocompleteData() {
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<ProductSuggestion> getProducts() {
            return products;
        }

        public void setProducts(List<ProductSuggestion> products) {
            this.products = products;
        }

        public List<CategorySuggestion> getCategories() {
            return categories;
        }

        public void setCategories(List<CategorySuggestion> categories) {
            this.categories = categories;
        }

        public static AutocompleteDataBuilder builder() {
            return new AutocompleteDataBuilder();
        }

        public static class AutocompleteDataBuilder {
            private String query;
            private List<ProductSuggestion> products;
            private List<CategorySuggestion> categories;

            public AutocompleteDataBuilder query(String query) {
                this.query = query;
                return this;
            }

            public AutocompleteDataBuilder products(List<ProductSuggestion> products) {
                this.products = products;
                return this;
            }

            public AutocompleteDataBuilder categories(List<CategorySuggestion> categories) {
                this.categories = categories;
                return this;
            }

            public AutocompleteData build() {
                AutocompleteData instance = new AutocompleteData();
                instance.query = this.query;
                instance.products = this.products;
                instance.categories = this.categories;
                return instance;
            }
        }
    }

    public static class ProductSuggestion {
        private UUID productId;
        private String name;
        private String slug;
        private String category;
        private BigDecimal basePrice;
        private String imageUrl;

        public ProductSuggestion() {
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public static ProductSuggestionBuilder builder() {
            return new ProductSuggestionBuilder();
        }

        public static class ProductSuggestionBuilder {
            private UUID productId;
            private String name;
            private String slug;
            private String category;
            private BigDecimal basePrice;
            private String imageUrl;

            public ProductSuggestionBuilder productId(UUID productId) {
                this.productId = productId;
                return this;
            }

            public ProductSuggestionBuilder name(String name) {
                this.name = name;
                return this;
            }

            public ProductSuggestionBuilder slug(String slug) {
                this.slug = slug;
                return this;
            }

            public ProductSuggestionBuilder category(String category) {
                this.category = category;
                return this;
            }

            public ProductSuggestionBuilder basePrice(BigDecimal basePrice) {
                this.basePrice = basePrice;
                return this;
            }

            public ProductSuggestionBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public ProductSuggestion build() {
                ProductSuggestion instance = new ProductSuggestion();
                instance.productId = this.productId;
                instance.name = this.name;
                instance.slug = this.slug;
                instance.category = this.category;
                instance.basePrice = this.basePrice;
                instance.imageUrl = this.imageUrl;
                return instance;
            }
        }
    }

    public static class CategorySuggestion {
        private String name;
        private String slug;
        private long productCount;

        public CategorySuggestion() {
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

        public long getProductCount() {
            return productCount;
        }

        public void setProductCount(long productCount) {
            this.productCount = productCount;
        }

        public static CategorySuggestionBuilder builder() {
            return new CategorySuggestionBuilder();
        }

        public static class CategorySuggestionBuilder {
            private String name;
            private String slug;
            private long productCount;

            public CategorySuggestionBuilder name(String name) {
                this.name = name;
                return this;
            }

            public CategorySuggestionBuilder slug(String slug) {
                this.slug = slug;
                return this;
            }

            public CategorySuggestionBuilder productCount(long productCount) {
                this.productCount = productCount;
                return this;
            }

            public CategorySuggestion build() {
                CategorySuggestion instance = new CategorySuggestion();
                instance.name = this.name;
                instance.slug = this.slug;
                instance.productCount = this.productCount;
                return instance;
            }
        }
    }

    public static class AutocompleteResponseBuilder {
        private String status;
        private AutocompleteData data;

        public AutocompleteResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AutocompleteResponseBuilder data(AutocompleteData data) {
            this.data = data;
            return this;
        }

        public AutocompleteResponse build() {
            AutocompleteResponse instance = new AutocompleteResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
