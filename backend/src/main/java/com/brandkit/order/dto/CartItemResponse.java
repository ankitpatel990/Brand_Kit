package com.brandkit.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for cart item - FRD-004 FR-39
 */
public class CartItemResponse {

    private UUID cartItemId;
    private UUID productId;
    private String productName;
    private String productSlug;
    private String productImageUrl;
    private UUID customizationId;
    private String previewUrl;
    private Boolean hasCustomization;
    private Integer quantity;
    private BigDecimal originalUnitPrice;
    private BigDecimal unitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal customizationFee;
    private BigDecimal effectiveUnitPrice;
    private BigDecimal subtotal;

    // Getters and Setters
    public UUID getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public UUID getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(UUID customizationId) {
        this.customizationId = customizationId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Boolean getHasCustomization() {
        return hasCustomization;
    }

    public void setHasCustomization(Boolean hasCustomization) {
        this.hasCustomization = hasCustomization;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getCustomizationFee() {
        return customizationFee;
    }

    public void setCustomizationFee(BigDecimal customizationFee) {
        this.customizationFee = customizationFee;
    }

    public BigDecimal getEffectiveUnitPrice() {
        return effectiveUnitPrice;
    }

    public void setEffectiveUnitPrice(BigDecimal effectiveUnitPrice) {
        this.effectiveUnitPrice = effectiveUnitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
