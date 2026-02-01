package com.brandkit.customization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Create Bundle Request DTO
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 */
public class CreateBundleRequest {

    @NotBlank(message = "Bundle name is required")
    @Size(min = 5, max = 100, message = "Bundle name must be 5-100 characters")
    private String bundleName;

    @NotEmpty(message = "Bundle must contain at least one product")
    @Size(max = 10, message = "Bundle cannot contain more than 10 products")
    private List<BundleItemRequest> items;

    public static class BundleItemRequest {
        private UUID productId;
        private UUID customizationId;
        private Integer quantity;
        private BigDecimal unitPrice;

        public BundleItemRequest() {}

        public UUID getProductId() {
            return this.productId;
        }
        public void setProductId(UUID productId) {
            this.productId = productId;
        }
        public UUID getCustomizationId() {
            return this.customizationId;
        }
        public void setCustomizationId(UUID customizationId) {
            this.customizationId = customizationId;
        }
        public Integer getQuantity() {
            return this.quantity;
        }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        public BigDecimal getUnitPrice() {
            return this.unitPrice;
        }
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
    }

    public CreateBundleRequest() {}

    public String getBundleName() {
        return this.bundleName;
    }
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
    public List<BundleItemRequest> getItems() {
        return this.items;
    }
    public void setItems(List<BundleItemRequest> items) {
        this.items = items;
    }
}
