package com.brandkit.order.entity;

import com.brandkit.catalog.entity.Product;
import com.brandkit.customization.entity.Customization;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Cart item entity - FRD-004 FR-39
 */
@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_items_cart_id", columnList = "cart_id"),
    @Index(name = "idx_cart_items_product_id", columnList = "product_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_cart_product_customization", 
                      columnNames = {"cart_id", "product_id", "customization_id"})
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customization_id")
    private Customization customization;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10,000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @DecimalMin(value = "0", message = "Unit price must be non-negative")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100", message = "Discount percentage must not exceed 100")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Customization fee must be non-negative")
    @Column(name = "customization_fee", precision = 10, scale = 2)
    private BigDecimal customizationFee = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Subtotal must be non-negative")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        calculateSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
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

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
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
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Get the effective unit price including customization fee
     */
    public BigDecimal getEffectiveUnitPrice() {
        return unitPrice.add(customizationFee != null ? customizationFee : BigDecimal.ZERO);
    }
}
