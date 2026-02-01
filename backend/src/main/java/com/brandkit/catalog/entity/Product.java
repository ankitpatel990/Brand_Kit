package com.brandkit.catalog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Product Entity
 * FRD-002 FR-15: Product Data Model
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 250)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(name = "short_description", nullable = false, length = 300)
    private String shortDescription;

    @Column(name = "long_description", nullable = false, columnDefinition = "TEXT")
    private String longDescription;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(length = 100)
    private String material;

    @Column(name = "eco_friendly", nullable = false)
    private Boolean ecoFriendly = false;

    @Column(name = "customization_available", nullable = false)
    private Boolean customizationAvailable = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "customization_type", nullable = false)
    private CustomizationType customizationType = CustomizationType.NONE;

    @Column(name = "print_area_width", precision = 6, scale = 2)
    private BigDecimal printAreaWidth;

    @Column(name = "print_area_height", precision = 6, scale = 2)
    private BigDecimal printAreaHeight;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(length = 100)
    private String dimensions;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "available_colors", columnDefinition = "TEXT[]")
    private String[] availableColors;

    // Partner ID - INTERNAL ONLY, never exposed to clients
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays = 7;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;

    @Column(name = "aggregate_rating", precision = 2, scale = 1)
    private BigDecimal aggregateRating = BigDecimal.ZERO;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "TEXT[]")
    private String[] tags;

    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 300)
    private String metaDescription;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    // One-to-many relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("tierNumber ASC")
    private List<PricingTier> pricingTiers = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductDiscount> discounts = new ArrayList<>();

    // Helper methods
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    public void addPricingTier(PricingTier tier) {
        pricingTiers.add(tier);
        tier.setProduct(this);
    }

    public void removePricingTier(PricingTier tier) {
        pricingTiers.remove(tier);
        tier.setProduct(null);
    }

    public ProductImage getPrimaryImage() {
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }

    public ProductDiscount getActiveDiscount() {
        ZonedDateTime now = ZonedDateTime.now();
        return discounts.stream()
                .filter(d -> d.getStatus() == DiscountStatus.APPROVED)
                .filter(d -> d.getStartDate() == null || d.getStartDate().isBefore(now))
                .filter(d -> d.getEndDate() == null || d.getEndDate().isAfter(now))
                .findFirst()
                .orElse(null);
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getSlug() {
        return this.slug;
    }
    public ProductCategory getCategory() {
        return this.category;
    }
    public String getShortDescription() {
        return this.shortDescription;
    }
    public String getLongDescription() {
        return this.longDescription;
    }
    public BigDecimal getBasePrice() {
        return this.basePrice;
    }
    public String getMaterial() {
        return this.material;
    }
    public Boolean getEcoFriendly() {
        return this.ecoFriendly;
    }
    public Boolean getCustomizationAvailable() {
        return this.customizationAvailable;
    }
    public CustomizationType getCustomizationType() {
        return this.customizationType;
    }
    public BigDecimal getPrintAreaWidth() {
        return this.printAreaWidth;
    }
    public BigDecimal getPrintAreaHeight() {
        return this.printAreaHeight;
    }
    public Integer getWeightGrams() {
        return this.weightGrams;
    }
    public String getDimensions() {
        return this.dimensions;
    }
    public String[] getAvailableColors() {
        return this.availableColors;
    }
    public void setAvailableColors(String[] availableColors) {
        this.availableColors = availableColors;
    }
    public String[] getTags() {
        return this.tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public Integer getLeadTimeDays() {
        return this.leadTimeDays;
    }
    public ProductStatus getStatus() {
        return this.status;
    }
    public AvailabilityStatus getAvailability() {
        return this.availability;
    }
    public BigDecimal getAggregateRating() {
        return this.aggregateRating;
    }
    public Integer getTotalReviews() {
        return this.totalReviews;
    }
    public Integer getTotalOrders() {
        return this.totalOrders;
    }
    public String getMetaTitle() {
        return this.metaTitle;
    }
    public String getMetaDescription() {
        return this.metaDescription;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public List<ProductImage> getImages() {
        return this.images;
    }
    public List<PricingTier> getPricingTiers() {
        return this.pricingTiers;
    }
    public List<ProductDiscount> getDiscounts() {
        return this.discounts;
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
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public void setEcoFriendly(Boolean ecoFriendly) {
        this.ecoFriendly = ecoFriendly;
    }
    public void setCustomizationAvailable(Boolean customizationAvailable) {
        this.customizationAvailable = customizationAvailable;
    }
    public void setCustomizationType(CustomizationType customizationType) {
        this.customizationType = customizationType;
    }
    public void setPrintAreaWidth(BigDecimal printAreaWidth) {
        this.printAreaWidth = printAreaWidth;
    }
    public void setPrintAreaHeight(BigDecimal printAreaHeight) {
        this.printAreaHeight = printAreaHeight;
    }
    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability;
    }
    public void setAggregateRating(BigDecimal aggregateRating) {
        this.aggregateRating = aggregateRating;
    }
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
    public void setPricingTiers(List<PricingTier> pricingTiers) {
        this.pricingTiers = pricingTiers;
    }
    public void setDiscounts(List<ProductDiscount> discounts) {
        this.discounts = discounts;
    }
    public Product() {
    }
    public Product(UUID id, String name, String slug, ProductCategory category, String shortDescription, String longDescription, BigDecimal basePrice, String material, Boolean ecoFriendly, Boolean customizationAvailable, CustomizationType customizationType, BigDecimal printAreaWidth, BigDecimal printAreaHeight, Integer weightGrams, String dimensions, Partner partner, Integer leadTimeDays, ProductStatus status, AvailabilityStatus availability, BigDecimal aggregateRating, Integer totalReviews, Integer totalOrders, String metaTitle, String metaDescription, ZonedDateTime createdAt, ZonedDateTime updatedAt, List<ProductImage> images, List<PricingTier> pricingTiers, List<ProductDiscount> discounts) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.category = category;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.basePrice = basePrice;
        this.material = material;
        this.ecoFriendly = ecoFriendly;
        this.customizationAvailable = customizationAvailable;
        this.customizationType = customizationType;
        this.printAreaWidth = printAreaWidth;
        this.printAreaHeight = printAreaHeight;
        this.weightGrams = weightGrams;
        this.dimensions = dimensions;
        this.partner = partner;
        this.leadTimeDays = leadTimeDays;
        this.status = status;
        this.availability = availability;
        this.aggregateRating = aggregateRating;
        this.totalReviews = totalReviews;
        this.totalOrders = totalOrders;
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
        this.pricingTiers = pricingTiers;
        this.discounts = discounts;
    }
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private UUID id;
        private String name;
        private String slug;
        private ProductCategory category;
        private String shortDescription;
        private String longDescription;
        private BigDecimal basePrice;
        private String material;
        private Boolean ecoFriendly = false;
        private Boolean customizationAvailable = false;
        private CustomizationType customizationType = CustomizationType.NONE;
        private BigDecimal printAreaWidth;
        private BigDecimal printAreaHeight;
        private Integer weightGrams;
        private String dimensions;
        private String[] availableColors;
        private Partner partner;
        private Integer leadTimeDays = 7;
        private ProductStatus status = ProductStatus.ACTIVE;
        private AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;
        private BigDecimal aggregateRating = BigDecimal.ZERO;
        private Integer totalReviews = 0;
        private Integer totalOrders = 0;
        private String metaTitle;
        private String[] tags;
        private String metaDescription;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private List<ProductImage> images = new ArrayList<>();
        private List<PricingTier> pricingTiers = new ArrayList<>();
        private List<ProductDiscount> discounts = new ArrayList<>();

        ProductBuilder() {
        }

        public ProductBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public ProductBuilder category(ProductCategory category) {
            this.category = category;
            return this;
        }

        public ProductBuilder shortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public ProductBuilder longDescription(String longDescription) {
            this.longDescription = longDescription;
            return this;
        }

        public ProductBuilder basePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public ProductBuilder material(String material) {
            this.material = material;
            return this;
        }

        public ProductBuilder ecoFriendly(Boolean ecoFriendly) {
            this.ecoFriendly = ecoFriendly;
            return this;
        }

        public ProductBuilder customizationAvailable(Boolean customizationAvailable) {
            this.customizationAvailable = customizationAvailable;
            return this;
        }

        public ProductBuilder customizationType(CustomizationType customizationType) {
            this.customizationType = customizationType;
            return this;
        }

        public ProductBuilder printAreaWidth(BigDecimal printAreaWidth) {
            this.printAreaWidth = printAreaWidth;
            return this;
        }

        public ProductBuilder printAreaHeight(BigDecimal printAreaHeight) {
            this.printAreaHeight = printAreaHeight;
            return this;
        }

        public ProductBuilder weightGrams(Integer weightGrams) {
            this.weightGrams = weightGrams;
            return this;
        }

        public ProductBuilder dimensions(String dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public ProductBuilder availableColors(String[] availableColors) {
            this.availableColors = availableColors;
            return this;
        }

        public ProductBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public ProductBuilder leadTimeDays(Integer leadTimeDays) {
            this.leadTimeDays = leadTimeDays;
            return this;
        }

        public ProductBuilder status(ProductStatus status) {
            this.status = status;
            return this;
        }

        public ProductBuilder availability(AvailabilityStatus availability) {
            this.availability = availability;
            return this;
        }

        public ProductBuilder aggregateRating(BigDecimal aggregateRating) {
            this.aggregateRating = aggregateRating;
            return this;
        }

        public ProductBuilder totalReviews(Integer totalReviews) {
            this.totalReviews = totalReviews;
            return this;
        }

        public ProductBuilder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public ProductBuilder metaTitle(String metaTitle) {
            this.metaTitle = metaTitle;
            return this;
        }

        public ProductBuilder metaDescription(String metaDescription) {
            this.metaDescription = metaDescription;
            return this;
        }

        public ProductBuilder tags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public ProductBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProductBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ProductBuilder images(List<ProductImage> images) {
            this.images = images;
            return this;
        }

        public ProductBuilder pricingTiers(List<PricingTier> pricingTiers) {
            this.pricingTiers = pricingTiers;
            return this;
        }

        public ProductBuilder discounts(List<ProductDiscount> discounts) {
            this.discounts = discounts;
            return this;
        }

        public Product build() {
            Product instance = new Product();
            instance.id = this.id;
            instance.name = this.name;
            instance.slug = this.slug;
            instance.category = this.category;
            instance.shortDescription = this.shortDescription;
            instance.longDescription = this.longDescription;
            instance.basePrice = this.basePrice;
            instance.material = this.material;
            instance.ecoFriendly = this.ecoFriendly;
            instance.customizationAvailable = this.customizationAvailable;
            instance.customizationType = this.customizationType;
            instance.printAreaWidth = this.printAreaWidth;
            instance.printAreaHeight = this.printAreaHeight;
            instance.weightGrams = this.weightGrams;
            instance.dimensions = this.dimensions;
            instance.availableColors = this.availableColors;
            instance.partner = this.partner;
            instance.leadTimeDays = this.leadTimeDays;
            instance.status = this.status;
            instance.availability = this.availability;
            instance.aggregateRating = this.aggregateRating;
            instance.totalReviews = this.totalReviews;
            instance.totalOrders = this.totalOrders;
            instance.metaTitle = this.metaTitle;
            instance.metaDescription = this.metaDescription;
            instance.tags = this.tags;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            instance.images = this.images;
            instance.pricingTiers = this.pricingTiers;
            instance.discounts = this.discounts;
            return instance;
        }
    }
}
