package com.brandkit.order.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for cart validation - FRD-004 FR-40
 */
public class CartValidationResponse {

    private Boolean isValid;
    private List<ValidationError> errors = new ArrayList<>();
    private List<UUID> invalidItemIds = new ArrayList<>();
    private Boolean pricesUpdated;
    private CartResponse updatedCart;

    public static class ValidationError {
        private UUID itemId;
        private String errorCode;
        private String message;

        public ValidationError() {}

        public ValidationError(UUID itemId, String errorCode, String message) {
            this.itemId = itemId;
            this.errorCode = errorCode;
            this.message = message;
        }

        // Getters and Setters
        public UUID getItemId() {
            return itemId;
        }

        public void setItemId(UUID itemId) {
            this.itemId = itemId;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Getters and Setters
    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<UUID> getInvalidItemIds() {
        return invalidItemIds;
    }

    public void setInvalidItemIds(List<UUID> invalidItemIds) {
        this.invalidItemIds = invalidItemIds;
    }

    public Boolean getPricesUpdated() {
        return pricesUpdated;
    }

    public void setPricesUpdated(Boolean pricesUpdated) {
        this.pricesUpdated = pricesUpdated;
    }

    public CartResponse getUpdatedCart() {
        return updatedCart;
    }

    public void setUpdatedCart(CartResponse updatedCart) {
        this.updatedCart = updatedCart;
    }

    // Helper methods
    public void addError(UUID itemId, String errorCode, String message) {
        errors.add(new ValidationError(itemId, errorCode, message));
        if (itemId != null && !invalidItemIds.contains(itemId)) {
            invalidItemIds.add(itemId);
        }
        isValid = false;
    }

    public static CartValidationResponse valid(CartResponse cart) {
        CartValidationResponse response = new CartValidationResponse();
        response.setIsValid(true);
        response.setPricesUpdated(false);
        response.setUpdatedCart(cart);
        return response;
    }
}
