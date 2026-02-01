package com.brandkit.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for order cancellation response - FRD-004 Sub-Prompt 8
 */
public class CancellationResponse {

    private UUID orderId;
    private String orderNumber;
    private boolean success;
    private boolean canCancel;
    private String message;
    private BigDecimal orderTotal;
    private BigDecimal refundAmount;
    private BigDecimal deductionAmount;
    private String deductionReason;
    private UUID refundId;
    private String refundStatus;
    private String refundMethod;
    private Integer estimatedRefundDays;

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(BigDecimal deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public String getDeductionReason() {
        return deductionReason;
    }

    public void setDeductionReason(String deductionReason) {
        this.deductionReason = deductionReason;
    }

    public UUID getRefundId() {
        return refundId;
    }

    public void setRefundId(UUID refundId) {
        this.refundId = refundId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;
    }

    public Integer getEstimatedRefundDays() {
        return estimatedRefundDays;
    }

    public void setEstimatedRefundDays(Integer estimatedRefundDays) {
        this.estimatedRefundDays = estimatedRefundDays;
    }
}
