package com.brandkit.order.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for reorder - FRD-004 Sub-Prompt 10
 */
public class ReorderResponse {

    private Boolean success;
    private Integer itemsAdded;
    private Integer itemsUnavailable;
    private List<UnavailableItem> unavailableItems = new ArrayList<>();
    private Boolean pricesUpdated;
    private CartResponse cart;
    private String message;

    public static class UnavailableItem {
        private UUID productId;
        private String productName;
        private String reason;

        public UnavailableItem() {}

        public UnavailableItem(UUID productId, String productName, String reason) {
            this.productId = productId;
            this.productName = productName;
            this.reason = reason;
        }

        // Getters and Setters
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

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    // Getters and Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getItemsAdded() {
        return itemsAdded;
    }

    public void setItemsAdded(Integer itemsAdded) {
        this.itemsAdded = itemsAdded;
    }

    public Integer getItemsUnavailable() {
        return itemsUnavailable;
    }

    public void setItemsUnavailable(Integer itemsUnavailable) {
        this.itemsUnavailable = itemsUnavailable;
    }

    public List<UnavailableItem> getUnavailableItems() {
        return unavailableItems;
    }

    public void setUnavailableItems(List<UnavailableItem> unavailableItems) {
        this.unavailableItems = unavailableItems;
    }

    public Boolean getPricesUpdated() {
        return pricesUpdated;
    }

    public void setPricesUpdated(Boolean pricesUpdated) {
        this.pricesUpdated = pricesUpdated;
    }

    public CartResponse getCart() {
        return cart;
    }

    public void setCart(CartResponse cart) {
        this.cart = cart;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Helper methods
    public void addUnavailableItem(UUID productId, String productName, String reason) {
        unavailableItems.add(new UnavailableItem(productId, productName, reason));
    }
}
