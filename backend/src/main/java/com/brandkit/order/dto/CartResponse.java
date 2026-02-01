package com.brandkit.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for cart - FRD-004 FR-39
 */
public class CartResponse {

    private UUID cartId;
    private UUID userId;
    private List<CartItemResponse> items;
    private Integer itemCount;
    private Integer totalQuantity;
    private CartPricing pricing;

    public static class CartPricing {
        private BigDecimal originalSubtotal;
        private BigDecimal subtotal;
        private BigDecimal totalDiscount;
        private BigDecimal gst;
        private BigDecimal deliveryCharges;
        private BigDecimal total;
        private Boolean freeDeliveryEligible;
        private BigDecimal freeDeliveryThreshold;

        // Getters and Setters
        public BigDecimal getOriginalSubtotal() {
            return originalSubtotal;
        }

        public void setOriginalSubtotal(BigDecimal originalSubtotal) {
            this.originalSubtotal = originalSubtotal;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public BigDecimal getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(BigDecimal totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public BigDecimal getGst() {
            return gst;
        }

        public void setGst(BigDecimal gst) {
            this.gst = gst;
        }

        public BigDecimal getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(BigDecimal deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public Boolean getFreeDeliveryEligible() {
            return freeDeliveryEligible;
        }

        public void setFreeDeliveryEligible(Boolean freeDeliveryEligible) {
            this.freeDeliveryEligible = freeDeliveryEligible;
        }

        public BigDecimal getFreeDeliveryThreshold() {
            return freeDeliveryThreshold;
        }

        public void setFreeDeliveryThreshold(BigDecimal freeDeliveryThreshold) {
            this.freeDeliveryThreshold = freeDeliveryThreshold;
        }
    }

    // Getters and Setters
    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public CartPricing getPricing() {
        return pricing;
    }

    public void setPricing(CartPricing pricing) {
        this.pricing = pricing;
    }
}
