package com.brandkit.catalog.dto.admin;

import com.brandkit.catalog.dto.ProductListResponse;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Discount List Response DTO
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
public class DiscountListResponse {
    private String status;
    private DiscountListData data;

    public DiscountListResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DiscountListData getData() {
        return data;
    }

    public void setData(DiscountListData data) {
        this.data = data;
    }

    public static DiscountListResponseBuilder builder() {
        return new DiscountListResponseBuilder();
    }

    public static class DiscountListResponseBuilder {
        private String status;
        private DiscountListData data;

        public DiscountListResponseBuilder status(String status) { this.status = status; return this; }
        public DiscountListResponseBuilder data(DiscountListData data) { this.data = data; return this; }

        public DiscountListResponse build() {
            DiscountListResponse instance = new DiscountListResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }

    public static class DiscountListData {
        private List<DiscountDetail> discounts;
        private ProductListResponse.PaginationInfo pagination;

        public DiscountListData() {
        }

        public List<DiscountDetail> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(List<DiscountDetail> discounts) {
            this.discounts = discounts;
        }

        public ProductListResponse.PaginationInfo getPagination() {
            return pagination;
        }

        public void setPagination(ProductListResponse.PaginationInfo pagination) {
            this.pagination = pagination;
        }

        public static DiscountListDataBuilder builder() {
            return new DiscountListDataBuilder();
        }

        public static class DiscountListDataBuilder {
            private List<DiscountDetail> discounts;
            private ProductListResponse.PaginationInfo pagination;

            public DiscountListDataBuilder discounts(List<DiscountDetail> discounts) { this.discounts = discounts; return this; }
            public DiscountListDataBuilder pagination(ProductListResponse.PaginationInfo pagination) { this.pagination = pagination; return this; }

            public DiscountListData build() {
                DiscountListData instance = new DiscountListData();
                instance.discounts = this.discounts;
                instance.pagination = this.pagination;
                return instance;
            }
        }
    }

    public static class DiscountDetail {
        private UUID discountId;
        private UUID productId;
        private String productName;
        private String productCategory;
        private UUID partnerId;
        private String partnerName;
        private BigDecimal discountPercentage;
        private String discountName;
        private String discountStatus;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private ZonedDateTime createdAt;
        private ApprovalInfo approvalInfo;

        public DiscountDetail() {
        }

        public UUID getDiscountId() {
            return discountId;
        }

        public void setDiscountId(UUID discountId) {
            this.discountId = discountId;
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

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public UUID getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(UUID partnerId) {
            this.partnerId = partnerId;
        }

        public String getPartnerName() {
            return partnerName;
        }

        public void setPartnerName(String partnerName) {
            this.partnerName = partnerName;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public String getDiscountName() {
            return discountName;
        }

        public void setDiscountName(String discountName) {
            this.discountName = discountName;
        }

        public String getDiscountStatus() {
            return discountStatus;
        }

        public void setDiscountStatus(String discountStatus) {
            this.discountStatus = discountStatus;
        }

        public ZonedDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(ZonedDateTime startDate) {
            this.startDate = startDate;
        }

        public ZonedDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public ApprovalInfo getApprovalInfo() {
            return approvalInfo;
        }

        public void setApprovalInfo(ApprovalInfo approvalInfo) {
            this.approvalInfo = approvalInfo;
        }

        public static DiscountDetailBuilder builder() {
            return new DiscountDetailBuilder();
        }

        public static class DiscountDetailBuilder {
            private UUID discountId;
            private UUID productId;
            private String productName;
            private String productCategory;
            private UUID partnerId;
            private String partnerName;
            private BigDecimal discountPercentage;
            private String discountName;
            private String discountStatus;
            private ZonedDateTime startDate;
            private ZonedDateTime endDate;
            private ZonedDateTime createdAt;
            private ApprovalInfo approvalInfo;

            public DiscountDetailBuilder discountId(UUID discountId) { this.discountId = discountId; return this; }
            public DiscountDetailBuilder productId(UUID productId) { this.productId = productId; return this; }
            public DiscountDetailBuilder productName(String productName) { this.productName = productName; return this; }
            public DiscountDetailBuilder productCategory(String productCategory) { this.productCategory = productCategory; return this; }
            public DiscountDetailBuilder partnerId(UUID partnerId) { this.partnerId = partnerId; return this; }
            public DiscountDetailBuilder partnerName(String partnerName) { this.partnerName = partnerName; return this; }
            public DiscountDetailBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
            public DiscountDetailBuilder discountName(String discountName) { this.discountName = discountName; return this; }
            public DiscountDetailBuilder discountStatus(String discountStatus) { this.discountStatus = discountStatus; return this; }
            public DiscountDetailBuilder status(String status) { this.discountStatus = status; return this; }
            public DiscountDetailBuilder startDate(ZonedDateTime startDate) { this.startDate = startDate; return this; }
            public DiscountDetailBuilder endDate(ZonedDateTime endDate) { this.endDate = endDate; return this; }
            public DiscountDetailBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }
            public DiscountDetailBuilder approvalInfo(ApprovalInfo approvalInfo) { this.approvalInfo = approvalInfo; return this; }

            public DiscountDetail build() {
                DiscountDetail instance = new DiscountDetail();
                instance.discountId = this.discountId;
                instance.productId = this.productId;
                instance.productName = this.productName;
                instance.productCategory = this.productCategory;
                instance.partnerId = this.partnerId;
                instance.partnerName = this.partnerName;
                instance.discountPercentage = this.discountPercentage;
                instance.discountName = this.discountName;
                instance.discountStatus = this.discountStatus;
                instance.startDate = this.startDate;
                instance.endDate = this.endDate;
                instance.createdAt = this.createdAt;
                instance.approvalInfo = this.approvalInfo;
                return instance;
            }
        }
    }

    public static class ApprovalInfo {
        private String approvedByName;
        private ZonedDateTime approvedAt;
        private String disabledByName;
        private ZonedDateTime disabledAt;
        private String disabledReason;

        public ApprovalInfo() {
        }

        public String getApprovedByName() {
            return approvedByName;
        }

        public void setApprovedByName(String approvedByName) {
            this.approvedByName = approvedByName;
        }

        public ZonedDateTime getApprovedAt() {
            return approvedAt;
        }

        public void setApprovedAt(ZonedDateTime approvedAt) {
            this.approvedAt = approvedAt;
        }

        public String getDisabledByName() {
            return disabledByName;
        }

        public void setDisabledByName(String disabledByName) {
            this.disabledByName = disabledByName;
        }

        public ZonedDateTime getDisabledAt() {
            return disabledAt;
        }

        public void setDisabledAt(ZonedDateTime disabledAt) {
            this.disabledAt = disabledAt;
        }

        public String getDisabledReason() {
            return disabledReason;
        }

        public void setDisabledReason(String disabledReason) {
            this.disabledReason = disabledReason;
        }

        public static ApprovalInfoBuilder builder() {
            return new ApprovalInfoBuilder();
        }

        public static class ApprovalInfoBuilder {
            private String approvedByName;
            private ZonedDateTime approvedAt;
            private String disabledByName;
            private ZonedDateTime disabledAt;
            private String disabledReason;

            public ApprovalInfoBuilder approvedByName(String approvedByName) { this.approvedByName = approvedByName; return this; }
            public ApprovalInfoBuilder approvedAt(ZonedDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
            public ApprovalInfoBuilder disabledByName(String disabledByName) { this.disabledByName = disabledByName; return this; }
            public ApprovalInfoBuilder disabledAt(ZonedDateTime disabledAt) { this.disabledAt = disabledAt; return this; }
            public ApprovalInfoBuilder disabledReason(String disabledReason) { this.disabledReason = disabledReason; return this; }

            public ApprovalInfo build() {
                ApprovalInfo instance = new ApprovalInfo();
                instance.approvedByName = this.approvedByName;
                instance.approvedAt = this.approvedAt;
                instance.disabledByName = this.disabledByName;
                instance.disabledAt = this.disabledAt;
                instance.disabledReason = this.disabledReason;
                return instance;
            }
        }
    }
}
