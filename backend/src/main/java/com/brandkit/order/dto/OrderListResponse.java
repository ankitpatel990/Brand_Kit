package com.brandkit.order.dto;

import com.brandkit.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for order list item - FRD-004 FR-47
 * Simplified order info for list views
 */
public class OrderListResponse {

    private UUID id;
    private String orderNumber;
    private OffsetDateTime orderDate;
    private OrderStatus status;
    private String statusDisplayName;
    private Integer itemCount;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private String firstProductName;
    private String firstProductImageUrl;
    private Boolean canReorder;
    private Boolean hasInvoice;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OffsetDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(OffsetDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFirstProductName() {
        return firstProductName;
    }

    public void setFirstProductName(String firstProductName) {
        this.firstProductName = firstProductName;
    }

    public String getFirstProductImageUrl() {
        return firstProductImageUrl;
    }

    public void setFirstProductImageUrl(String firstProductImageUrl) {
        this.firstProductImageUrl = firstProductImageUrl;
    }

    public Boolean getCanReorder() {
        return canReorder;
    }

    public void setCanReorder(Boolean canReorder) {
        this.canReorder = canReorder;
    }

    public Boolean getHasInvoice() {
        return hasInvoice;
    }

    public void setHasInvoice(Boolean hasInvoice) {
        this.hasInvoice = hasInvoice;
    }
}
