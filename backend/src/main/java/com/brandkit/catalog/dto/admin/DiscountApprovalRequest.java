package com.brandkit.catalog.dto.admin;

import jakarta.validation.constraints.Size;
/**
 * Discount Approval Request DTO
 * FRD-002 Sub-Prompt 7: Admin approves/disables partner discounts
 */
public class DiscountApprovalRequest {
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
