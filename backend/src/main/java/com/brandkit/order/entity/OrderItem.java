package com.brandkit.order.entity;

import com.brandkit.catalog.entity.Product;
import com.brandkit.customization.entity.Customization;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Order item entity - FRD-004 FR-43
 * Captures product snapshot at order time
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order_id", columnList = "order_id"),
    @Index(name = "idx_order_items_product_id", columnList = "product_id")
})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customization_id")
    private Customization customization;

    // Product snapshot (captured at order time)
    @NotBlank
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @NotBlank
    @Column(name = "product_slug", nullable = false, length = 250)
    private String productSlug;

    @Column(name = "product_image_url")
    private String productImageUrl;

    @Column(name = "preview_image_url")
    private String previewImageUrl; // Customized preview

    @Column(name = "hsn_code", length = 10)
    private String hsnCode;

    // Pricing
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10,000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0", message = "Original unit price must be non-negative")
    @Column(name = "original_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalUnitPrice;

    @DecimalMin(value = "0", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100", message = "Discount percentage must not exceed 100")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Unit price must be non-negative")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0", message = "Customization fee must be non-negative")
    @Column(name = "customization_fee", precision = 10, scale = 2)
    private BigDecimal customizationFee = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Subtotal must be non-negative")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Print-ready assets (for partner - INTERNAL)
    @Column(name = "print_ready_image_url")
    private String printReadyImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        calculateSubtotal();
    }

    /**
     * Calculate subtotal based on quantity, unit price, and customization fee
     */
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            BigDecimal effectivePrice = unitPrice.add(customizationFee != null ? customizationFee : BigDecimal.ZERO);
            this.subtotal = effectivePrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customization getCustomization() {
        return customization;
    }

    public void setCustomization(Customization customization) {
        this.customization = customization;
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

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public BigDecimal getCustomizationFee() {
        return customizationFee;
    }

    public void setCustomizationFee(BigDecimal customizationFee) {
        this.customizationFee = customizationFee;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getPrintReadyImageUrl() {
        return printReadyImageUrl;
    }

    public void setPrintReadyImageUrl(String printReadyImageUrl) {
        this.printReadyImageUrl = printReadyImageUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Get the effective unit price including customization fee
     */
    public BigDecimal getEffectiveUnitPrice() {
        return unitPrice.add(customizationFee != null ? customizationFee : BigDecimal.ZERO);
    }

    /**
     * Get the total discount amount for this item
     */
    public BigDecimal getDiscountAmount() {
        if (originalUnitPrice != null && unitPrice != null) {
            return originalUnitPrice.subtract(unitPrice).multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
