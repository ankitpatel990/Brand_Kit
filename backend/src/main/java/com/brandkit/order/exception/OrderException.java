package com.brandkit.order.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for order-related errors - FRD-004
 */
public class OrderException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public OrderException(String message) {
        super(message);
        this.errorCode = "ORD_000";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public OrderException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public OrderException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    // Predefined exceptions based on FRD-004 error codes
    public static OrderException cartEmpty() {
        return new OrderException("ORD_001", "Your cart is empty", HttpStatus.BAD_REQUEST);
    }

    public static OrderException productUnavailable() {
        return new OrderException("ORD_002", "Some products are no longer available", HttpStatus.BAD_REQUEST);
    }

    public static OrderException invalidQuantity() {
        return new OrderException("ORD_003", "Quantity must be between 1 and 10,000", HttpStatus.BAD_REQUEST);
    }

    public static OrderException pinCodeNotServiceable() {
        return new OrderException("ORD_004", "We don't deliver to this PIN code yet", HttpStatus.BAD_REQUEST);
    }

    public static OrderException paymentFailed() {
        return new OrderException("ORD_005", "Payment failed. Please retry", HttpStatus.PAYMENT_REQUIRED);
    }

    public static OrderException paymentTimeout() {
        return new OrderException("ORD_006", "Payment session expired", HttpStatus.REQUEST_TIMEOUT);
    }

    public static OrderException paymentGatewayError() {
        return new OrderException("ORD_007", "Payment service unavailable. Try again later", HttpStatus.BAD_GATEWAY);
    }

    public static OrderException multiplePartnersInCart() {
        return new OrderException("ORD_008", "Unable to process order. Please contact support", HttpStatus.BAD_REQUEST);
    }

    public static OrderException partnerUnavailable() {
        return new OrderException("ORD_009", "Order processing delayed. We'll notify you soon", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static OrderException addressValidationFailed() {
        return new OrderException("ORD_010", "Invalid address. Please check details", HttpStatus.BAD_REQUEST);
    }

    public static OrderException invoiceGenerationFailed() {
        return new OrderException("ORD_011", "Invoice unavailable. Contact support", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static OrderException orderNotFound() {
        return new OrderException("ORD_012", "Order not found", HttpStatus.NOT_FOUND);
    }

    public static OrderException trackingUnavailable() {
        return new OrderException("ORD_013", "Tracking information not yet available", HttpStatus.NOT_FOUND);
    }

    public static OrderException deliveryFailed() {
        return new OrderException("ORD_014", "Delivery attempted but failed. Courier will retry", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static OrderException reorderItemsUnavailable() {
        return new OrderException("ORD_015", "Some items from previous order are unavailable", HttpStatus.BAD_REQUEST);
    }

    public static OrderException customizationMissing() {
        return new OrderException("ORD_016", "Customization data missing", HttpStatus.BAD_REQUEST);
    }

    public static OrderException termsNotAccepted() {
        return new OrderException("ORD_017", "Please accept Terms & Conditions", HttpStatus.BAD_REQUEST);
    }

    public static OrderException cartItemNotFound() {
        return new OrderException("ORD_018", "Cart item not found", HttpStatus.NOT_FOUND);
    }

    public static OrderException addressNotFound() {
        return new OrderException("ORD_019", "Address not found", HttpStatus.NOT_FOUND);
    }

    public static OrderException orderNotModifiable() {
        return new OrderException("ORD_020", "Order cannot be modified", HttpStatus.BAD_REQUEST);
    }
}
