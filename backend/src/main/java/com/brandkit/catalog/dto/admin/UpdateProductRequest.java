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
 * Update Product Request DTO
 * FRD-002 Sub-Prompt 5: Admin Product Management
 */
public class UpdateProductRequest {
    
    @Size(min = 5, max = 200, message = "Product name must be between 5 and 200 characters")
    private String name;
    
    private ProductCategory category;
    
    @Size(min = 50, max = 300, message = "Short description must be between 50 and 300 characters")
    private String shortDescription;
    
    @Size(min = 100, max = 2000, message = "Long description must be between 100 and 2000 characters")
    private String longDescription;
    
    @DecimalMin(value = "1.00", message = "Base price must be at least ₹1")
    @DecimalMax(value = "100000.00", message = "Base price cannot exceed ₹100,000")
    private BigDecimal basePrice;
    
    @Size(min = 2, max = 100, message = "Material must be between 2 and 100 characters")
    private String material;
    
    private Boolean ecoFriendly;
    
    private Boolean customizationAvailable;
    
    private CustomizationType customizationType;
    
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
    
    private UUID partnerId;
    
    @Min(value = 1, message = "Lead time must be at least 1 day")
    @Max(value = 90, message = "Lead time cannot exceed 90 days")
    private Integer leadTimeDays;
    
    private ProductStatus status;
    
    private AvailabilityStatus availability;
    
    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<@Size(min = 2, max = 20, message = "Each tag must be between 2 and 20 characters") String> tags;
    
    @Size(max = 200, message = "Meta title cannot exceed 200 characters")
    private String metaTitle;
    
    @Size(max = 300, message = "Meta description cannot exceed 300 characters")
    private String metaDescription;
    
    @Size(min = 3, max = 5, message = "Must have between 3 and 5 pricing tiers")
    @Valid
    private List<CreateProductRequest.PricingTierRequest> pricingTiers;

    public String getName() {
        return this.name;
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
    public List<String> getAvailableColors() {
        return this.availableColors;
    }
    public UUID getPartnerId() {
        return this.partnerId;
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
    public List<@Size(min = 2, max = 20, message = "Each tag must be between 2 and 20 characters") String> getTags() {
        return this.tags;
    }
    public String getMetaTitle() {
        return this.metaTitle;
    }
    public String getMetaDescription() {
        return this.metaDescription;
    }
    public List<CreateProductRequest.PricingTierRequest> getPricingTiers() {
        return this.pricingTiers;
    }
    public void setName(String name) {
        this.name = name;
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
    public void setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
    }
    public void setPartnerId(UUID partnerId) {
        this.partnerId = partnerId;
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
    public void setTags(List<@Size(min = 2, max = 20, message = "Each tag must be between 2 and 20 characters") String> tags) {
        this.tags = tags;
    }
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    public void setPricingTiers(List<CreateProductRequest.PricingTierRequest> pricingTiers) {
        this.pricingTiers = pricingTiers;
    }
}
