package com.brandkit.catalog.dto.admin;

import com.brandkit.catalog.dto.ProductDetailResponse;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin Product Response DTO
 * FRD-002 Sub-Prompt 5: Admin Product Management
 * This DTO includes partner information for admin use
 */
public class AdminProductResponse {
    private String status;
    private AdminProductData data;

    public AdminProductResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AdminProductData getData() {
        return data;
    }

    public void setData(AdminProductData data) {
        this.data = data;
    }

    public static AdminProductResponseBuilder builder() {
        return new AdminProductResponseBuilder();
    }

    public static class AdminProductResponseBuilder {
        private String status;
        private AdminProductData data;

        public AdminProductResponseBuilder status(String status) { this.status = status; return this; }
        public AdminProductResponseBuilder data(AdminProductData data) { this.data = data; return this; }

        public AdminProductResponse build() {
            AdminProductResponse instance = new AdminProductResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }

    public static class AdminProductData {
        private UUID productId;
        private String name;
        private String slug;
        private String category;
        private String shortDescription;
        private String longDescription;
        private BigDecimal basePrice;
        private String material;
        private boolean ecoFriendly;
        private boolean customizable;
        private String customizationType;
        private ProductDetailResponse.PrintArea printArea;
        private List<ProductDetailResponse.ProductImageDto> images;
        private List<ProductDetailResponse.PricingTierDto> pricingTiers;
        private ProductDetailResponse.Specifications specifications;
        private BigDecimal aggregateRating;
        private int totalReviews;
        private int totalOrders;
        private int leadTimeDays;
        private String productStatus;
        private String availability;
        private List<String> tags;
        private ProductDetailResponse.SeoInfo seo;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private PartnerInfo partner;
        private List<DiscountInfo> discounts;

        public AdminProductData() {
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

        public String getLongDescription() {
            return longDescription;
        }

        public void setLongDescription(String longDescription) {
            this.longDescription = longDescription;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
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

        public ProductDetailResponse.PrintArea getPrintArea() {
            return printArea;
        }

        public void setPrintArea(ProductDetailResponse.PrintArea printArea) {
            this.printArea = printArea;
        }

        public List<ProductDetailResponse.ProductImageDto> getImages() {
            return images;
        }

        public void setImages(List<ProductDetailResponse.ProductImageDto> images) {
            this.images = images;
        }

        public List<ProductDetailResponse.PricingTierDto> getPricingTiers() {
            return pricingTiers;
        }

        public void setPricingTiers(List<ProductDetailResponse.PricingTierDto> pricingTiers) {
            this.pricingTiers = pricingTiers;
        }

        public ProductDetailResponse.Specifications getSpecifications() {
            return specifications;
        }

        public void setSpecifications(ProductDetailResponse.Specifications specifications) {
            this.specifications = specifications;
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

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getLeadTimeDays() {
            return leadTimeDays;
        }

        public void setLeadTimeDays(int leadTimeDays) {
            this.leadTimeDays = leadTimeDays;
        }

        public String getProductStatus() {
            return productStatus;
        }

        public void setProductStatus(String productStatus) {
            this.productStatus = productStatus;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public ProductDetailResponse.SeoInfo getSeo() {
            return seo;
        }

        public void setSeo(ProductDetailResponse.SeoInfo seo) {
            this.seo = seo;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public ZonedDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public PartnerInfo getPartner() {
            return partner;
        }

        public void setPartner(PartnerInfo partner) {
            this.partner = partner;
        }

        public List<DiscountInfo> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(List<DiscountInfo> discounts) {
            this.discounts = discounts;
        }

        public static AdminProductDataBuilder builder() {
            return new AdminProductDataBuilder();
        }

        public static class AdminProductDataBuilder {
            private UUID productId;
            private String name;
            private String slug;
            private String category;
            private String shortDescription;
            private String longDescription;
            private BigDecimal basePrice;
            private String material;
            private boolean ecoFriendly;
            private boolean customizable;
            private String customizationType;
            private ProductDetailResponse.PrintArea printArea;
            private List<ProductDetailResponse.ProductImageDto> images;
            private List<ProductDetailResponse.PricingTierDto> pricingTiers;
            private ProductDetailResponse.Specifications specifications;
            private BigDecimal aggregateRating;
            private int totalReviews;
            private int totalOrders;
            private int leadTimeDays;
            private String productStatus;
            private String availability;
            private List<String> tags;
            private ProductDetailResponse.SeoInfo seo;
            private ZonedDateTime createdAt;
            private ZonedDateTime updatedAt;
            private PartnerInfo partner;
            private List<DiscountInfo> discounts;

            public AdminProductDataBuilder productId(UUID productId) { this.productId = productId; return this; }
            public AdminProductDataBuilder name(String name) { this.name = name; return this; }
            public AdminProductDataBuilder slug(String slug) { this.slug = slug; return this; }
            public AdminProductDataBuilder category(String category) { this.category = category; return this; }
            public AdminProductDataBuilder shortDescription(String shortDescription) { this.shortDescription = shortDescription; return this; }
            public AdminProductDataBuilder longDescription(String longDescription) { this.longDescription = longDescription; return this; }
            public AdminProductDataBuilder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
            public AdminProductDataBuilder material(String material) { this.material = material; return this; }
            public AdminProductDataBuilder ecoFriendly(boolean ecoFriendly) { this.ecoFriendly = ecoFriendly; return this; }
            public AdminProductDataBuilder customizable(boolean customizable) { this.customizable = customizable; return this; }
            public AdminProductDataBuilder customizationType(String customizationType) { this.customizationType = customizationType; return this; }
            public AdminProductDataBuilder printArea(ProductDetailResponse.PrintArea printArea) { this.printArea = printArea; return this; }
            public AdminProductDataBuilder images(List<ProductDetailResponse.ProductImageDto> images) { this.images = images; return this; }
            public AdminProductDataBuilder pricingTiers(List<ProductDetailResponse.PricingTierDto> pricingTiers) { this.pricingTiers = pricingTiers; return this; }
            public AdminProductDataBuilder specifications(ProductDetailResponse.Specifications specifications) { this.specifications = specifications; return this; }
            public AdminProductDataBuilder aggregateRating(BigDecimal aggregateRating) { this.aggregateRating = aggregateRating; return this; }
            public AdminProductDataBuilder totalReviews(int totalReviews) { this.totalReviews = totalReviews; return this; }
            public AdminProductDataBuilder totalOrders(int totalOrders) { this.totalOrders = totalOrders; return this; }
            public AdminProductDataBuilder leadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; return this; }
            public AdminProductDataBuilder productStatus(String productStatus) { this.productStatus = productStatus; return this; }
            public AdminProductDataBuilder status(String status) { this.productStatus = status; return this; }
            public AdminProductDataBuilder availability(String availability) { this.availability = availability; return this; }
            public AdminProductDataBuilder tags(List<String> tags) { this.tags = tags; return this; }
            public AdminProductDataBuilder seo(ProductDetailResponse.SeoInfo seo) { this.seo = seo; return this; }
            public AdminProductDataBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
            public AdminProductDataBuilder updatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
            public AdminProductDataBuilder partner(PartnerInfo partner) { this.partner = partner; return this; }
            public AdminProductDataBuilder discounts(List<DiscountInfo> discounts) { this.discounts = discounts; return this; }

            public AdminProductData build() {
                AdminProductData instance = new AdminProductData();
                instance.productId = this.productId;
                instance.name = this.name;
                instance.slug = this.slug;
                instance.category = this.category;
                instance.shortDescription = this.shortDescription;
                instance.longDescription = this.longDescription;
                instance.basePrice = this.basePrice;
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
                instance.totalOrders = this.totalOrders;
                instance.leadTimeDays = this.leadTimeDays;
                instance.productStatus = this.productStatus;
                instance.availability = this.availability;
                instance.tags = this.tags;
                instance.seo = this.seo;
                instance.createdAt = this.createdAt;
                instance.updatedAt = this.updatedAt;
                instance.partner = this.partner;
                instance.discounts = this.discounts;
                return instance;
            }
        }
    }

    public static class PartnerInfo {
        private UUID partnerId;
        private String businessName;
        private String email;
        private String phone;
        private String location;
        private String city;
        private BigDecimal commissionRate;
        private int fulfillmentSlaDays;

        public PartnerInfo() {
        }

        public UUID getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(UUID partnerId) {
            this.partnerId = partnerId;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public BigDecimal getCommissionRate() {
            return commissionRate;
        }

        public void setCommissionRate(BigDecimal commissionRate) {
            this.commissionRate = commissionRate;
        }

        public int getFulfillmentSlaDays() {
            return fulfillmentSlaDays;
        }

        public void setFulfillmentSlaDays(int fulfillmentSlaDays) {
            this.fulfillmentSlaDays = fulfillmentSlaDays;
        }

        public static PartnerInfoBuilder builder() {
            return new PartnerInfoBuilder();
        }

        public static class PartnerInfoBuilder {
            private UUID partnerId;
            private String businessName;
            private String email;
            private String phone;
            private String location;
            private String city;
            private BigDecimal commissionRate;
            private int fulfillmentSlaDays;

            public PartnerInfoBuilder partnerId(UUID partnerId) { this.partnerId = partnerId; return this; }
            public PartnerInfoBuilder businessName(String businessName) { this.businessName = businessName; return this; }
            public PartnerInfoBuilder email(String email) { this.email = email; return this; }
            public PartnerInfoBuilder phone(String phone) { this.phone = phone; return this; }
            public PartnerInfoBuilder location(String location) { this.location = location; return this; }
            public PartnerInfoBuilder city(String city) { this.city = city; return this; }
            public PartnerInfoBuilder commissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; return this; }
            public PartnerInfoBuilder fulfillmentSlaDays(int fulfillmentSlaDays) { this.fulfillmentSlaDays = fulfillmentSlaDays; return this; }

            public PartnerInfo build() {
                PartnerInfo instance = new PartnerInfo();
                instance.partnerId = this.partnerId;
                instance.businessName = this.businessName;
                instance.email = this.email;
                instance.phone = this.phone;
                instance.location = this.location;
                instance.city = this.city;
                instance.commissionRate = this.commissionRate;
                instance.fulfillmentSlaDays = this.fulfillmentSlaDays;
                return instance;
            }
        }
    }

    public static class DiscountInfo {
        private UUID discountId;
        private BigDecimal discountPercentage;
        private String discountName;
        private String discountStatus;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private ZonedDateTime createdAt;

        public DiscountInfo() {
        }

        public UUID getDiscountId() {
            return discountId;
        }

        public void setDiscountId(UUID discountId) {
            this.discountId = discountId;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public String getDiscountName() {
            return discountName;
        }

        public void setDiscountName(String discountName) {
            this.discountName = discountName;
        }

        public String getDiscountStatus() {
            return discountStatus;
        }

        public void setDiscountStatus(String discountStatus) {
            this.discountStatus = discountStatus;
        }

        public ZonedDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(ZonedDateTime startDate) {
            this.startDate = startDate;
        }

        public ZonedDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public static DiscountInfoBuilder builder() {
            return new DiscountInfoBuilder();
        }

        public static class DiscountInfoBuilder {
            private UUID discountId;
            private BigDecimal discountPercentage;
            private String discountName;
            private String discountStatus;
            private ZonedDateTime startDate;
            private ZonedDateTime endDate;
            private ZonedDateTime createdAt;

            public DiscountInfoBuilder discountId(UUID discountId) { this.discountId = discountId; return this; }
            public DiscountInfoBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
            public DiscountInfoBuilder discountName(String discountName) { this.discountName = discountName; return this; }
            public DiscountInfoBuilder discountStatus(String discountStatus) { this.discountStatus = discountStatus; return this; }
            public DiscountInfoBuilder status(String status) { this.discountStatus = status; return this; }
            public DiscountInfoBuilder startDate(ZonedDateTime startDate) { this.startDate = startDate; return this; }
            public DiscountInfoBuilder endDate(ZonedDateTime endDate) { this.endDate = endDate; return this; }
            public DiscountInfoBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

            public DiscountInfo build() {
                DiscountInfo instance = new DiscountInfo();
                instance.discountId = this.discountId;
                instance.discountPercentage = this.discountPercentage;
                instance.discountName = this.discountName;
                instance.discountStatus = this.discountStatus;
                instance.startDate = this.startDate;
                instance.endDate = this.endDate;
                instance.createdAt = this.createdAt;
                return instance;
            }
        }
    }
}
