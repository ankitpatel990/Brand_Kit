package com.brandkit.catalog.dto;

import java.util.List;

/**
 * Product Search Response DTO
 * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
 */
public class ProductSearchResponse {
    private String status;
    private SearchData data;

    public ProductSearchResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SearchData getData() {
        return data;
    }

    public void setData(SearchData data) {
        this.data = data;
    }

    public static ProductSearchResponseBuilder builder() {
        return new ProductSearchResponseBuilder();
    }

    public static class SearchData {
        private String query;
        private List<ProductListResponse.ProductSummary> products;
        private ProductListResponse.PaginationInfo pagination;
        private List<String> suggestions;
        private List<CategorySuggestion> categorySuggestions;

        public SearchData() {
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<ProductListResponse.ProductSummary> getProducts() {
            return products;
        }

        public void setProducts(List<ProductListResponse.ProductSummary> products) {
            this.products = products;
        }

        public ProductListResponse.PaginationInfo getPagination() {
            return pagination;
        }

        public void setPagination(ProductListResponse.PaginationInfo pagination) {
            this.pagination = pagination;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }

        public List<CategorySuggestion> getCategorySuggestions() {
            return categorySuggestions;
        }

        public void setCategorySuggestions(List<CategorySuggestion> categorySuggestions) {
            this.categorySuggestions = categorySuggestions;
        }

        public static SearchDataBuilder builder() {
            return new SearchDataBuilder();
        }

        public static class SearchDataBuilder {
            private String query;
            private List<ProductListResponse.ProductSummary> products;
            private ProductListResponse.PaginationInfo pagination;
            private List<String> suggestions;
            private List<CategorySuggestion> categorySuggestions;

            public SearchDataBuilder query(String query) {
                this.query = query;
                return this;
            }

            public SearchDataBuilder products(List<ProductListResponse.ProductSummary> products) {
                this.products = products;
                return this;
            }

            public SearchDataBuilder pagination(ProductListResponse.PaginationInfo pagination) {
                this.pagination = pagination;
                return this;
            }

            public SearchDataBuilder suggestions(List<String> suggestions) {
                this.suggestions = suggestions;
                return this;
            }

            public SearchDataBuilder categorySuggestions(List<CategorySuggestion> categorySuggestions) {
                this.categorySuggestions = categorySuggestions;
                return this;
            }

            public SearchData build() {
                SearchData instance = new SearchData();
                instance.query = this.query;
                instance.products = this.products;
                instance.pagination = this.pagination;
                instance.suggestions = this.suggestions;
                instance.categorySuggestions = this.categorySuggestions;
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

    public static class ProductSearchResponseBuilder {
        private String status;
        private SearchData data;

        public ProductSearchResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ProductSearchResponseBuilder data(SearchData data) {
            this.data = data;
            return this;
        }

        public ProductSearchResponse build() {
            ProductSearchResponse instance = new ProductSearchResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
