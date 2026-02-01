package com.brandkit.catalog.dto.admin;

import com.brandkit.catalog.entity.AvailabilityStatus;
import com.brandkit.catalog.entity.CustomizationType;
import com.brandkit.catalog.entity.ProductCategory;
import com.brandkit.catalog.entity.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Create Product Request DTO
 * FRD-002 Sub-Prompt 5: Admin Product Management
 */
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 5, max = 200, message = "Product name must be between 5 and 200 characters")
    private String name;
    
    @NotNull(message = "Category is required")
    private ProductCategory category;
    
    @NotBlank(message = "Short description is required")
    @Size(min = 50, max = 300, message = "Short description must be between 50 and 300 characters")
    private String shortDescription;
    
    @NotBlank(message = "Long description is required")
    @Size(min = 100, max = 2000, message = "Long description must be between 100 and 2000 characters")
    private String longDescription;
    
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "1.00", message = "Base price must be at least ₹1")
    @DecimalMax(value = "100000.00", message = "Base price cannot exceed ₹100,000")
    private BigDecimal basePrice;
    
    @NotBlank(message = "Material is required")
    @Size(min = 2, max = 100, message = "Material must be between 2 and 100 characters")
    private String material;
    
    private Boolean ecoFriendly = false;
    
    private Boolean customizationAvailable = false;
    
    private CustomizationType customizationType = CustomizationType.NONE;
    
    @DecimalMin(value = "0.1", message = "Print area width must be at least 0.1 cm")
    @DecimalMax(value = "100", message = "Print area width cannot exceed 100 cm")
    private BigDecimal printAreaWidth;
    
    @DecimalMin(value = "0.1", message = "Print area height must be at least 0.1 cm")
    @DecimalMax(value = "100", message = "Print area height cannot exceed 100 cm")
    private BigDecimal printAreaHeight;
    
    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weightGrams;
    
    @Size(max = 100, message = "Dimensions cannot exceed 100 characters")
    private String dimensions;
    
    private List<String> availableColors;
    
    @NotNull(message = "Partner ID is required")
    private UUID partnerId;
    
    @NotNull(message = "Lead time is required")
    @Min(value = 1, message = "Lead time must be at least 1 day")
    @Max(value = 90, message = "Lead time cannot exceed 90 days")
    private Integer leadTimeDays = 7;
    
    private ProductStatus status = ProductStatus.ACTIVE;
    
    private AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;
    
    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<@Size(min = 2, max = 20, message = "Each tag must be between 2 and 20 characters") String> tags;
    
    @Size(max = 200, message = "Meta title cannot exceed 200 characters")
    private String metaTitle;
    
    @Size(max = 300, message = "Meta description cannot exceed 300 characters")
    private String metaDescription;
    
    @NotNull(message = "Pricing tiers are required")
    @Size(min = 3, max = 5, message = "Must have between 3 and 5 pricing tiers")
    @Valid
    private List<PricingTierRequest> pricingTiers;
    
    public static class PricingTierRequest {
        @NotNull(message = "Tier number is required")
        @Min(value = 1, message = "Tier number must be at least 1")
        private Integer tierNumber;
        
        @NotNull(message = "Minimum quantity is required")
        @Min(value = 1, message = "Minimum quantity must be at least 1")
        private Integer minQuantity;
        
        private Integer maxQuantity;
        
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be positive")
        private BigDecimal unitPrice;
        
        @DecimalMin(value = "0", message = "Discount percentage cannot be negative")
        @DecimalMax(value = "100", message = "Discount percentage cannot exceed 100%")
        private BigDecimal discountPercentage = BigDecimal.ZERO;

        public PricingTierRequest() {}

        public Integer getTierNumber() {
            return this.tierNumber;
        }
        public void setTierNumber(Integer tierNumber) {
            this.tierNumber = tierNumber;
        }
        public Integer getMinQuantity() {
            return this.minQuantity;
        }
        public void setMinQuantity(Integer minQuantity) {
            this.minQuantity = minQuantity;
        }
        public Integer getMaxQuantity() {
            return this.maxQuantity;
        }
        public void setMaxQuantity(Integer maxQuantity) {
            this.maxQuantity = maxQuantity;
        }
        public BigDecimal getUnitPrice() {
            return this.unitPrice;
        }
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
        public BigDecimal getDiscountPercentage() {
            return this.discountPercentage;
        }
        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }
    }

    public CreateProductRequest() {}

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ProductCategory getCategory() {
        return this.category;
    }
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    public String getShortDescription() {
        return this.shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public String getLongDescription() {
        return this.longDescription;
    }
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
    public BigDecimal getBasePrice() {
        return this.basePrice;
    }
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    public String getMaterial() {
        return this.material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public Boolean getEcoFriendly() {
        return this.ecoFriendly;
    }
    public void setEcoFriendly(Boolean ecoFriendly) {
        this.ecoFriendly = ecoFriendly;
    }
    public Boolean getCustomizationAvailable() {
        return this.customizationAvailable;
    }
    public void setCustomizationAvailable(Boolean customizationAvailable) {
        this.customizationAvailable = customizationAvailable;
    }
    public CustomizationType getCustomizationType() {
        return this.customizationType;
    }
    public void setCustomizationType(CustomizationType customizationType) {
        this.customizationType = customizationType;
    }
    public BigDecimal getPrintAreaWidth() {
        return this.printAreaWidth;
    }
    public void setPrintAreaWidth(BigDecimal printAreaWidth) {
        this.printAreaWidth = printAreaWidth;
    }
    public BigDecimal getPrintAreaHeight() {
        return this.printAreaHeight;
    }
    public void setPrintAreaHeight(BigDecimal printAreaHeight) {
        this.printAreaHeight = printAreaHeight;
    }
    public Integer getWeightGrams() {
        return this.weightGrams;
    }
    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }
    public String getDimensions() {
        return this.dimensions;
    }
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    public List<String> getAvailableColors() {
        return this.availableColors;
    }
    public void setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
    }
    public UUID getPartnerId() {
        return this.partnerId;
    }
    public void setPartnerId(UUID partnerId) {
        this.partnerId = partnerId;
    }
    public Integer getLeadTimeDays() {
        return this.leadTimeDays;
    }
    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }
    public ProductStatus getStatus() {
        return this.status;
    }
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    public AvailabilityStatus getAvailability() {
        return this.availability;
    }
    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability;
    }
    public List<String> getTags() {
        return this.tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public String getMetaTitle() {
        return this.metaTitle;
    }
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    public String getMetaDescription() {
        return this.metaDescription;
    }
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    public List<PricingTierRequest> getPricingTiers() {
        return this.pricingTiers;
    }
    public void setPricingTiers(List<PricingTierRequest> pricingTiers) {
        this.pricingTiers = pricingTiers;
    }
}
