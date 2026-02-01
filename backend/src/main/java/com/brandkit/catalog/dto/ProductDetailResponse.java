package com.brandkit.catalog.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Product Detail Response DTO
 * FRD-002 Sub-Prompt 4: Product Detail API
 * NOTE: NO partner information is exposed
 */
public class ProductDetailResponse {
    private String status;
    private ProductDetailData data;

    public ProductDetailResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProductDetailData getData() {
        return data;
    }

    public void setData(ProductDetailData data) {
        this.data = data;
    }

    public static ProductDetailResponseBuilder builder() {
        return new ProductDetailResponseBuilder();
    }

    public static class ProductDetailData {
        private UUID productId;
        private String name;
        private String slug;
        private String category;
        private String categorySlug;
        private String shortDescription;
        private String longDescription;
        private BigDecimal basePrice;
        private BigDecimal discountedPrice;
        private BigDecimal discountPercentage;
        private boolean hasDiscount;
        private String discountName;
        private String material;
        private boolean ecoFriendly;
        private boolean customizable;
        private String customizationType;
        private PrintArea printArea;
        private List<ProductImageDto> images;
        private List<PricingTierDto> pricingTiers;
        private Specifications specifications;
        private BigDecimal aggregateRating;
        private int totalReviews;
        private int leadTimeDays;
        private String availability;
        private List<String> tags;
        private SeoInfo seo;
        private ZonedDateTime createdAt;

        public ProductDetailData() {
        }

        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getCategorySlug() { return categorySlug; }
        public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public String getLongDescription() { return longDescription; }
        public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
        public BigDecimal getBasePrice() { return basePrice; }
        public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
        public BigDecimal getDiscountedPrice() { return discountedPrice; }
        public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public boolean isHasDiscount() { return hasDiscount; }
        public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }
        public String getDiscountName() { return discountName; }
        public void setDiscountName(String discountName) { this.discountName = discountName; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public boolean isEcoFriendly() { return ecoFriendly; }
        public void setEcoFriendly(boolean ecoFriendly) { this.ecoFriendly = ecoFriendly; }
        public boolean isCustomizable() { return customizable; }
        public void setCustomizable(boolean customizable) { this.customizable = customizable; }
        public String getCustomizationType() { return customizationType; }
        public void setCustomizationType(String customizationType) { this.customizationType = customizationType; }
        public PrintArea getPrintArea() { return printArea; }
        public void setPrintArea(PrintArea printArea) { this.printArea = printArea; }
        public List<ProductImageDto> getImages() { return images; }
        public void setImages(List<ProductImageDto> images) { this.images = images; }
        public List<PricingTierDto> getPricingTiers() { return pricingTiers; }
        public void setPricingTiers(List<PricingTierDto> pricingTiers) { this.pricingTiers = pricingTiers; }
        public Specifications getSpecifications() { return specifications; }
        public void setSpecifications(Specifications specifications) { this.specifications = specifications; }
        public BigDecimal getAggregateRating() { return aggregateRating; }
        public void setAggregateRating(BigDecimal aggregateRating) { this.aggregateRating = aggregateRating; }
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        public int getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public SeoInfo getSeo() { return seo; }
        public void setSeo(SeoInfo seo) { this.seo = seo; }
        public ZonedDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

        public static ProductDetailDataBuilder builder() {
            return new ProductDetailDataBuilder();
        }

        public static class ProductDetailDataBuilder {
            private UUID productId;
            private String name;
            private String slug;
            private String category;
            private String categorySlug;
            private String shortDescription;
            private String longDescription;
            private BigDecimal basePrice;
            private BigDecimal discountedPrice;
            private BigDecimal discountPercentage;
            private boolean hasDiscount;
            private String discountName;
            private String material;
            private boolean ecoFriendly;
            private boolean customizable;
            private String customizationType;
            private PrintArea printArea;
            private List<ProductImageDto> images;
            private List<PricingTierDto> pricingTiers;
            private Specifications specifications;
            private BigDecimal aggregateRating;
            private int totalReviews;
            private int leadTimeDays;
            private String availability;
            private List<String> tags;
            private SeoInfo seo;
            private ZonedDateTime createdAt;

            public ProductDetailDataBuilder productId(UUID productId) { this.productId = productId; return this; }
            public ProductDetailDataBuilder name(String name) { this.name = name; return this; }
            public ProductDetailDataBuilder slug(String slug) { this.slug = slug; return this; }
            public ProductDetailDataBuilder category(String category) { this.category = category; return this; }
            public ProductDetailDataBuilder categorySlug(String categorySlug) { this.categorySlug = categorySlug; return this; }
            public ProductDetailDataBuilder shortDescription(String shortDescription) { this.shortDescription = shortDescription; return this; }
            public ProductDetailDataBuilder longDescription(String longDescription) { this.longDescription = longDescription; return this; }
            public ProductDetailDataBuilder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
            public ProductDetailDataBuilder discountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; return this; }
            public ProductDetailDataBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
            public ProductDetailDataBuilder hasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; return this; }
            public ProductDetailDataBuilder discountName(String discountName) { this.discountName = discountName; return this; }
            public ProductDetailDataBuilder material(String material) { this.material = material; return this; }
            public ProductDetailDataBuilder ecoFriendly(boolean ecoFriendly) { this.ecoFriendly = ecoFriendly; return this; }
            public ProductDetailDataBuilder customizable(boolean customizable) { this.customizable = customizable; return this; }
            public ProductDetailDataBuilder customizationType(String customizationType) { this.customizationType = customizationType; return this; }
            public ProductDetailDataBuilder printArea(PrintArea printArea) { this.printArea = printArea; return this; }
            public ProductDetailDataBuilder images(List<ProductImageDto> images) { this.images = images; return this; }
            public ProductDetailDataBuilder pricingTiers(List<PricingTierDto> pricingTiers) { this.pricingTiers = pricingTiers; return this; }
            public ProductDetailDataBuilder specifications(Specifications specifications) { this.specifications = specifications; return this; }
            public ProductDetailDataBuilder aggregateRating(BigDecimal aggregateRating) { this.aggregateRating = aggregateRating; return this; }
            public ProductDetailDataBuilder totalReviews(int totalReviews) { this.totalReviews = totalReviews; return this; }
            public ProductDetailDataBuilder leadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; return this; }
            public ProductDetailDataBuilder availability(String availability) { this.availability = availability; return this; }
            public ProductDetailDataBuilder tags(List<String> tags) { this.tags = tags; return this; }
            public ProductDetailDataBuilder seo(SeoInfo seo) { this.seo = seo; return this; }
            public ProductDetailDataBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

            public ProductDetailData build() {
                ProductDetailData instance = new ProductDetailData();
                instance.productId = this.productId;
                instance.name = this.name;
                instance.slug = this.slug;
                instance.category = this.category;
                instance.categorySlug = this.categorySlug;
                instance.shortDescription = this.shortDescription;
                instance.longDescription = this.longDescription;
                instance.basePrice = this.basePrice;
                instance.discountedPrice = this.discountedPrice;
                instance.discountPercentage = this.discountPercentage;
                instance.hasDiscount = this.hasDiscount;
                instance.discountName = this.discountName;
                instance.material = this.material;
                instance.ecoFriendly = this.ecoFriendly;
                instance.customizable = this.customizable;
                instance.customizationType = this.customizationType;
                instance.printArea = this.printArea;
                instance.images = this.images;
                instance.pricingTiers = this.pricingTiers;
                instance.specifications = this.specifications;
                instance.aggregateRating = this.aggregateRating;
                instance.totalReviews = this.totalReviews;
                instance.leadTimeDays = this.leadTimeDays;
                instance.availability = this.availability;
                instance.tags = this.tags;
                instance.seo = this.seo;
                instance.createdAt = this.createdAt;
                return instance;
            }
        }
    }

    public static class PrintArea {
        private BigDecimal width;
        private BigDecimal height;
        private String unit;

        public PrintArea() {}
        public BigDecimal getWidth() { return width; }
        public void setWidth(BigDecimal width) { this.width = width; }
        public BigDecimal getHeight() { return height; }
        public void setHeight(BigDecimal height) { this.height = height; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public static PrintAreaBuilder builder() { return new PrintAreaBuilder(); }

        public static class PrintAreaBuilder {
            private BigDecimal width;
            private BigDecimal height;
            private String unit;

            public PrintAreaBuilder width(BigDecimal width) { this.width = width; return this; }
            public PrintAreaBuilder height(BigDecimal height) { this.height = height; return this; }
            public PrintAreaBuilder unit(String unit) { this.unit = unit; return this; }

            public PrintArea build() {
                PrintArea instance = new PrintArea();
                instance.width = this.width;
                instance.height = this.height;
                instance.unit = this.unit;
                return instance;
            }
        }
    }

    public static class ProductImageDto {
        private UUID id;
        private String imageUrl;
        private String thumbnailUrl;
        private String mediumUrl;
        private String altText;
        private int displayOrder;
        private boolean isPrimary;

        public ProductImageDto() {}
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getMediumUrl() { return mediumUrl; }
        public void setMediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public int getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
        public boolean isPrimary() { return isPrimary; }
        public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }

        public static ProductImageDtoBuilder builder() { return new ProductImageDtoBuilder(); }

        public static class ProductImageDtoBuilder {
            private UUID id;
            private String imageUrl;
            private String thumbnailUrl;
            private String mediumUrl;
            private String altText;
            private int displayOrder;
            private boolean isPrimary;

            public ProductImageDtoBuilder id(UUID id) { this.id = id; return this; }
            public ProductImageDtoBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
            public ProductImageDtoBuilder thumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; return this; }
            public ProductImageDtoBuilder mediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; return this; }
            public ProductImageDtoBuilder altText(String altText) { this.altText = altText; return this; }
            public ProductImageDtoBuilder displayOrder(int displayOrder) { this.displayOrder = displayOrder; return this; }
            public ProductImageDtoBuilder isPrimary(boolean isPrimary) { this.isPrimary = isPrimary; return this; }

            public ProductImageDto build() {
                ProductImageDto instance = new ProductImageDto();
                instance.id = this.id;
                instance.imageUrl = this.imageUrl;
                instance.thumbnailUrl = this.thumbnailUrl;
                instance.mediumUrl = this.mediumUrl;
                instance.altText = this.altText;
                instance.displayOrder = this.displayOrder;
                instance.isPrimary = this.isPrimary;
                return instance;
            }
        }
    }

    public static class PricingTierDto {
        private int tierNumber;
        private int minQuantity;
        private Integer maxQuantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercentage;
        private String description;

        public PricingTierDto() {}
        public int getTierNumber() { return tierNumber; }
        public void setTierNumber(int tierNumber) { this.tierNumber = tierNumber; }
        public int getMinQuantity() { return minQuantity; }
        public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }
        public Integer getMaxQuantity() { return maxQuantity; }
        public void setMaxQuantity(Integer maxQuantity) { this.maxQuantity = maxQuantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public static PricingTierDtoBuilder builder() { return new PricingTierDtoBuilder(); }

        public static class PricingTierDtoBuilder {
            private int tierNumber;
            private int minQuantity;
            private Integer maxQuantity;
            private BigDecimal unitPrice;
            private BigDecimal discountPercentage;
            private String description;

            public PricingTierDtoBuilder tierNumber(int tierNumber) { this.tierNumber = tierNumber; return this; }
            public PricingTierDtoBuilder minQuantity(int minQuantity) { this.minQuantity = minQuantity; return this; }
            public PricingTierDtoBuilder maxQuantity(Integer maxQuantity) { this.maxQuantity = maxQuantity; return this; }
            public PricingTierDtoBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
            public PricingTierDtoBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
            public PricingTierDtoBuilder description(String description) { this.description = description; return this; }

            public PricingTierDto build() {
                PricingTierDto instance = new PricingTierDto();
                instance.tierNumber = this.tierNumber;
                instance.minQuantity = this.minQuantity;
                instance.maxQuantity = this.maxQuantity;
                instance.unitPrice = this.unitPrice;
                instance.discountPercentage = this.discountPercentage;
                instance.description = this.description;
                return instance;
            }
        }
    }

    public static class Specifications {
        private String material;
        private Integer weightGrams;
        private String dimensions;
        private List<String> availableColors;
        private int leadTimeDays;

        public Specifications() {}
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public Integer getWeightGrams() { return weightGrams; }
        public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
        public String getDimensions() { return dimensions; }
        public void setDimensions(String dimensions) { this.dimensions = dimensions; }
        public List<String> getAvailableColors() { return availableColors; }
        public void setAvailableColors(List<String> availableColors) { this.availableColors = availableColors; }
        public int getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }

        public static SpecificationsBuilder builder() { return new SpecificationsBuilder(); }

        public static class SpecificationsBuilder {
            private String material;
            private Integer weightGrams;
            private String dimensions;
            private List<String> availableColors;
            private int leadTimeDays;

            public SpecificationsBuilder material(String material) { this.material = material; return this; }
            public SpecificationsBuilder weightGrams(Integer weightGrams) { this.weightGrams = weightGrams; return this; }
            public SpecificationsBuilder dimensions(String dimensions) { this.dimensions = dimensions; return this; }
            public SpecificationsBuilder availableColors(List<String> availableColors) { this.availableColors = availableColors; return this; }
            public SpecificationsBuilder leadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; return this; }

            public Specifications build() {
                Specifications instance = new Specifications();
                instance.material = this.material;
                instance.weightGrams = this.weightGrams;
                instance.dimensions = this.dimensions;
                instance.availableColors = this.availableColors;
                instance.leadTimeDays = this.leadTimeDays;
                return instance;
            }
        }
    }

    public static class SeoInfo {
        private String metaTitle;
        private String metaDescription;
        private String canonicalUrl;

        public SeoInfo() {}
        public String getMetaTitle() { return metaTitle; }
        public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
        public String getMetaDescription() { return metaDescription; }
        public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
        public String getCanonicalUrl() { return canonicalUrl; }
        public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

        public static SeoInfoBuilder builder() { return new SeoInfoBuilder(); }

        public static class SeoInfoBuilder {
            private String metaTitle;
            private String metaDescription;
            private String canonicalUrl;

            public SeoInfoBuilder metaTitle(String metaTitle) { this.metaTitle = metaTitle; return this; }
            public SeoInfoBuilder metaDescription(String metaDescription) { this.metaDescription = metaDescription; return this; }
            public SeoInfoBuilder canonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; return this; }

            public SeoInfo build() {
                SeoInfo instance = new SeoInfo();
                instance.metaTitle = this.metaTitle;
                instance.metaDescription = this.metaDescription;
                instance.canonicalUrl = this.canonicalUrl;
                return instance;
            }
        }
    }

    public static class ProductDetailResponseBuilder {
        private String status;
        private ProductDetailData data;

        public ProductDetailResponseBuilder status(String status) { this.status = status; return this; }
        public ProductDetailResponseBuilder data(ProductDetailData data) { this.data = data; return this; }

        public ProductDetailResponse build() {
            ProductDetailResponse instance = new ProductDetailResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}
