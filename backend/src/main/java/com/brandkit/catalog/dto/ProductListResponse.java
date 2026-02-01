package com.brandkit.catalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Product Listing Response DTO
 * FRD-002 Sub-Prompt 2: Product Listing API
 * NOTE: NO partner information is exposed
 */
public class ProductListResponse {
    private String status;
    private ProductListData data;

    public ProductListResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProductListData getData() {
        return data;
    }

    public void setData(ProductListData data) {
        this.data = data;
    }

    public static ProductListResponseBuilder builder() {
        return new ProductListResponseBuilder();
    }

    public static class ProductListData {
        private List<ProductSummary> products;
        private PaginationInfo pagination;
        private AppliedFilters appliedFilters;

        public ProductListData() {
        }

        public List<ProductSummary> getProducts() {
            return products;
        }

        public void setProducts(List<ProductSummary> products) {
            this.products = products;
        }

        public PaginationInfo getPagination() {
            return pagination;
        }

        public void setPagination(PaginationInfo pagination) {
            this.pagination = pagination;
        }

        public AppliedFilters getAppliedFilters() {
            return appliedFilters;
        }

        public void setAppliedFilters(AppliedFilters appliedFilters) {
            this.appliedFilters = appliedFilters;
        }

        public static ProductListDataBuilder builder() {
            return new ProductListDataBuilder();
        }

        public static class ProductListDataBuilder {
            private List<ProductSummary> products;
            private PaginationInfo pagination;
            private AppliedFilters appliedFilters;

            public ProductListDataBuilder products(List<ProductSummary> products) {
                this.products = products;
                return this;
            }

            public ProductListDataBuilder pagination(PaginationInfo pagination) {
                this.pagination = pagination;
                return this;
            }

            public ProductListDataBuilder appliedFilters(AppliedFilters appliedFilters) {
                this.appliedFilters = appliedFilters;
                return this;
            }

            public ProductListData build() {
                ProductListData instance = new ProductListData();
                instance.products = this.products;
                instance.pagination = this.pagination;
                instance.appliedFilters = this.appliedFilters;
                return instance;
            }
        }
    }

    public static class ProductSummary {
        private UUID productId;
        private String name;
        private String slug;
        private String category;
        private String shortDescription;
        private BigDecimal basePrice;
        private BigDecimal discountedPrice;
        private BigDecimal discountPercentage;
        private boolean hasDiscount;
        private String imageUrl;
        private boolean ecoFriendly;
        private boolean customizable;
        private String customizationType;
        private BigDecimal aggregateRating;
        private int totalReviews;
        private int leadTimeDays;
        private String availability;

        public ProductSummary() {
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

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public BigDecimal getDiscountedPrice() {
            return discountedPrice;
        }

        public void setDiscountedPrice(BigDecimal discountedPrice) {
            this.discountedPrice = discountedPrice;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public boolean isHasDiscount() {
            return hasDiscount;
        }

        public void setHasDiscount(boolean hasDiscount) {
            this.hasDiscount = hasDiscount;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isEcoFriendly() {
            return ecoFriendly;
        }

        public void setEcoFriendly(boolean ecoFriendly) {
            this.ecoFriendly = ecoFriendly;
        }

        public boolean isCustomizable() {
            return customizable;
        }

        public void setCustomizable(boolean customizable) {
            this.customizable = customizable;
        }

        public String getCustomizationType() {
            return customizationType;
        }

        public void setCustomizationType(String customizationType) {
            this.customizationType = customizationType;
        }

        public BigDecimal getAggregateRating() {
            return aggregateRating;
        }

        public void setAggregateRating(BigDecimal aggregateRating) {
            this.aggregateRating = aggregateRating;
        }

        public int getTotalReviews() {
            return totalReviews;
        }

        public void setTotalReviews(int totalReviews) {
            this.totalReviews = totalReviews;
        }

        public int getLeadTimeDays() {
            return leadTimeDays;
        }

        public void setLeadTimeDays(int leadTimeDays) {
            this.leadTimeDays = leadTimeDays;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public static ProductSummaryBuilder builder() {
            return new ProductSummaryBuilder();
        }

        public static class ProductSummaryBuilder {
            private UUID productId;
            private String name;
            private String slug;
            private String category;
            private String shortDescription;
            private BigDecimal basePrice;
            private BigDecimal discountedPrice;
            private BigDecimal discountPercentage;
            private boolean hasDiscount;
            private String imageUrl;
            private boolean ecoFriendly;
            private boolean customizable;
            private String customizationType;
            private BigDecimal aggregateRating;
            private int totalReviews;
            private int leadTimeDays;
            private String availability;

            public ProductSummaryBuilder productId(UUID productId) {
                this.productId = productId;
                return this;
            }

            public ProductSummaryBuilder name(String name) {
                this.name = name;
                return this;
            }

            public ProductSummaryBuilder slug(String slug) {
                this.slug = slug;
                return this;
            }

            public ProductSummaryBuilder category(String category) {
                this.category = category;
                return this;
            }

            public ProductSummaryBuilder shortDescription(String shortDescription) {
                this.shortDescription = shortDescription;
                return this;
            }

            public ProductSummaryBuilder basePrice(BigDecimal basePrice) {
                this.basePrice = basePrice;
                return this;
            }

            public ProductSummaryBuilder discountedPrice(BigDecimal discountedPrice) {
                this.discountedPrice = discountedPrice;
                return this;
            }

            public ProductSummaryBuilder discountPercentage(BigDecimal discountPercentage) {
                this.discountPercentage = discountPercentage;
                return this;
            }

            public ProductSummaryBuilder hasDiscount(boolean hasDiscount) {
                this.hasDiscount = hasDiscount;
                return this;
            }

            public ProductSummaryBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public ProductSummaryBuilder ecoFriendly(boolean ecoFriendly) {
                this.ecoFriendly = ecoFriendly;
                return this;
            }

            public ProductSummaryBuilder customizable(boolean customizable) {
                this.customizable = customizable;
                return this;
            }

            public ProductSummaryBuilder customizationType(String customizationType) {
                this.customizationType = customizationType;
                return this;
            }

            public ProductSummaryBuilder aggregateRating(BigDecimal aggregateRating) {
                this.aggregateRating = aggregateRating;
                return this;
            }

            public ProductSummaryBuilder totalReviews(int totalReviews) {
                this.totalReviews = totalReviews;
                return this;
            }

            public ProductSummaryBuilder leadTimeDays(int leadTimeDays) {
                this.leadTimeDays = leadTimeDays;
                return this;
            }

            public ProductSummaryBuilder availability(String availability) {
                this.availability = availability;
                return this;
            }

            public ProductSummary build() {
                ProductSummary instance = new ProductSummary();
                instance.productId = this.productId;
                instance.name = this.name;
                instance.slug = this.slug;
                instance.category = this.category;
                instance.shortDescription = this.shortDescription;
                instance.basePrice = this.basePrice;
                instance.discountedPrice = this.discountedPrice;
                instance.discountPercentage = this.discountPercentage;
                instance.hasDiscount = this.hasDiscount;
                instance.imageUrl = this.imageUrl;
                instance.ecoFriendly = this.ecoFriendly;
                instance.customizable = this.customizable;
                instance.customizationType = this.customizationType;
                instance.aggregateRating = this.aggregateRating;
                instance.totalReviews = this.totalReviews;
                instance.leadTimeDays = this.leadTimeDays;
                instance.availability = this.availability;
                return instance;
            }
        }
    }

    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalProducts;
        private int perPage;
        private boolean hasNext;
        private boolean hasPrevious;

        public PaginationInfo() {
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }

        public static PaginationInfoBuilder builder() {
            return new PaginationInfoBuilder();
        }

        public static class PaginationInfoBuilder {
            private int currentPage;
            private int totalPages;
            private long totalProducts;
            private int perPage;
            private boolean hasNext;
            private boolean hasPrevious;

            public PaginationInfoBuilder currentPage(int currentPage) {
                this.currentPage = currentPage;
                return this;
            }

            public PaginationInfoBuilder totalPages(int totalPages) {
                this.totalPages = totalPages;
                return this;
            }

            public PaginationInfoBuilder totalProducts(long totalProducts) {
                this.totalProducts = totalProducts;
                return this;
            }

            public PaginationInfoBuilder perPage(int perPage) {
                this.perPage = perPage;
                return this;
            }

            public PaginationInfoBuilder hasNext(boolean hasNext) {
                this.hasNext = hasNext;
                return this;
            }

            public PaginationInfoBuilder hasPrevious(boolean hasPrevious) {
                this.hasPrevious = hasPrevious;
                return this;
            }

            public PaginationInfo build() {
                PaginationInfo instance = new PaginationInfo();
                instance.currentPage = this.currentPage;
                instance.totalPages = this.totalPages;
                instance.totalProducts = this.totalProducts;
                instance.perPage = this.perPage;
                instance.hasNext = this.hasNext;
                instance.hasPrevious = this.hasPrevious;
                return instance;
            }
        }
    }

    public static class AppliedFilters {
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private List<String> materials;
        private Boolean ecoFriendly;
        private List<String> customizationTypes;
        private BigDecimal minRating;
        private String category;
        private String leadTime;
        private Boolean hasDiscount;

        public AppliedFilters() {
        }

        public BigDecimal getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
        }

        public BigDecimal getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
        }

        public List<String> getMaterials() {
            return materials;
        }

        public void setMaterials(List<String> materials) {
            this.materials = materials;
        }

        public Boolean getEcoFriendly() {
            return ecoFriendly;
        }

        public void setEcoFriendly(Boolean ecoFriendly) {
            this.ecoFriendly = ecoFriendly;
        }

        public List<String> getCustomizationTypes() {
            return customizationTypes;
        }

        public void setCustomizationTypes(List<String> customizationTypes) {
            this.customizationTypes = customizationTypes;
        }

        public BigDecimal getMinRating() {
            return minRating;
        }

        public void setMinRating(BigDecimal minRating) {
            this.minRating = minRating;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getLeadTime() {
            return leadTime;
        }

        public void setLeadTime(String leadTime) {
            this.leadTime = leadTime;
        }

        public Boolean getHasDiscount() {
            return hasDiscount;
        }

        public void setHasDiscount(Boolean hasDiscount) {
            this.hasDiscount = hasDiscount;
        }

        public static AppliedFiltersBuilder builder() {
            return new AppliedFiltersBuilder();
        }

        public static class AppliedFiltersBuilder {
            private BigDecimal minPrice;
            private BigDecimal maxPrice;
            private List<String> materials;
            private Boolean ecoFriendly;
            private List<String> customizationTypes;
            private BigDecimal minRating;
            private String category;
            private String leadTime;
            private Boolean hasDiscount;

            public AppliedFiltersBuilder minPrice(BigDecimal minPrice) {
                this.minPrice = minPrice;
                return this;
            }

            public AppliedFiltersBuilder maxPrice(BigDecimal maxPrice) {
                this.maxPrice = maxPrice;
                return this;
            }

            public AppliedFiltersBuilder materials(List<String> materials) {
                this.materials = materials;
                return this;
            }

            public AppliedFiltersBuilder ecoFriendly(Boolean ecoFriendly) {
                this.ecoFriendly = ecoFriendly;
                return this;
            }

            public AppliedFiltersBuilder customizationTypes(List<String> customizationTypes) {
                this.customizationTypes = customizationTypes;
                return this;
            }

            public AppliedFiltersBuilder minRating(BigDecimal minRating) {
                this.minRating = minRating;
                return this;
            }

            public AppliedFiltersBuilder category(String category) {
                this.category = category;
                return this;
            }

            public AppliedFiltersBuilder leadTime(String leadTime) {
                this.leadTime = leadTime;
                return this;
            }

            public AppliedFiltersBuilder hasDiscount(Boolean hasDiscount) {
                this.hasDiscount = hasDiscount;
                return this;
            }

            public AppliedFilters build() {
                AppliedFilters instance = new AppliedFilters();
                instance.minPrice = this.minPrice;
                instance.maxPrice = this.maxPrice;
                instance.materials = this.materials;
                instance.ecoFriendly = this.ecoFriendly;
                instance.customizationTypes = this.customizationTypes;
                instance.minRating = this.minRating;
                instance.category = this.category;
                instance.leadTime = this.leadTime;
                instance.hasDiscount = this.hasDiscount;
                return instance;
            }
        }
    }

    public static class ProductListResponseBuilder {
        private String status;
        private ProductListData data;

        public ProductListResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ProductListResponseBuilder data(ProductListData data) {
            this.data = data;
            return this;
        }

        public ProductListResponse build() {
            ProductListResponse instance = new ProductListResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
