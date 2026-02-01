package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Discount Response - FRD-005 FR-64b
 * Partner discount dashboard data
 */
public class DiscountResponse {

    private List<ProductDiscountDto> products;
    private DiscountLimits limits;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public DiscountResponse() {
    }

    public List<ProductDiscountDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDiscountDto> products) {
        this.products = products;
    }

    public DiscountLimits getLimits() {
        return limits;
    }

    public void setLimits(DiscountLimits limits) {
        this.limits = limits;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static DiscountResponseBuilder builder() {
        return new DiscountResponseBuilder();
    }

    public static class DiscountResponseBuilder {
        private List<ProductDiscountDto> products;
        private DiscountLimits limits;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public DiscountResponseBuilder products(List<ProductDiscountDto> products) { this.products = products; return this; }
        public DiscountResponseBuilder limits(DiscountLimits limits) { this.limits = limits; return this; }
        public DiscountResponseBuilder page(int page) { this.page = page; return this; }
        public DiscountResponseBuilder size(int size) { this.size = size; return this; }
        public DiscountResponseBuilder totalElements(long totalElements) { this.totalElements = totalElements; return this; }
        public DiscountResponseBuilder totalPages(int totalPages) { this.totalPages = totalPages; return this; }

        public DiscountResponse build() {
            DiscountResponse instance = new DiscountResponse();
            instance.products = this.products;
            instance.limits = this.limits;
            instance.page = this.page;
            instance.size = this.size;
            instance.totalElements = this.totalElements;
            instance.totalPages = this.totalPages;
            return instance;
        }
    }

    public static class ProductDiscountDto {
        private String discountId;
        private String productId;
        private String productName;
        private String category;
        private BigDecimal basePrice;
        private BigDecimal discountPercentage;
        private BigDecimal discountedPrice;
        private String status;
        private EarningsImpact earningsImpact;
        private String adminNotes;
        private String createdAt;
        private String updatedAt;

        public ProductDiscountDto() {
        }

        public String getDiscountId() {
            return discountId;
        }

        public void setDiscountId(String discountId) {
            this.discountId = discountId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public BigDecimal getDiscountedPrice() {
            return discountedPrice;
        }

        public void setDiscountedPrice(BigDecimal discountedPrice) {
            this.discountedPrice = discountedPrice;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public EarningsImpact getEarningsImpact() {
            return earningsImpact;
        }

        public void setEarningsImpact(EarningsImpact earningsImpact) {
            this.earningsImpact = earningsImpact;
        }

        public String getAdminNotes() {
            return adminNotes;
        }

        public void setAdminNotes(String adminNotes) {
            this.adminNotes = adminNotes;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public static ProductDiscountDtoBuilder builder() {
            return new ProductDiscountDtoBuilder();
        }

        public static class ProductDiscountDtoBuilder {
            private String discountId;
            private String productId;
            private String productName;
            private String category;
            private BigDecimal basePrice;
            private BigDecimal discountPercentage;
            private BigDecimal discountedPrice;
            private String status;
            private EarningsImpact earningsImpact;
            private String adminNotes;
            private String createdAt;
            private String updatedAt;

            public ProductDiscountDtoBuilder discountId(String discountId) { this.discountId = discountId; return this; }
            public ProductDiscountDtoBuilder productId(String productId) { this.productId = productId; return this; }
            public ProductDiscountDtoBuilder productName(String productName) { this.productName = productName; return this; }
            public ProductDiscountDtoBuilder category(String category) { this.category = category; return this; }
            public ProductDiscountDtoBuilder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
            public ProductDiscountDtoBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
            public ProductDiscountDtoBuilder discountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; return this; }
            public ProductDiscountDtoBuilder status(String status) { this.status = status; return this; }
            public ProductDiscountDtoBuilder earningsImpact(EarningsImpact earningsImpact) { this.earningsImpact = earningsImpact; return this; }
            public ProductDiscountDtoBuilder adminNotes(String adminNotes) { this.adminNotes = adminNotes; return this; }
            public ProductDiscountDtoBuilder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
            public ProductDiscountDtoBuilder updatedAt(String updatedAt) { this.updatedAt = updatedAt; return this; }

            public ProductDiscountDto build() {
                ProductDiscountDto instance = new ProductDiscountDto();
                instance.discountId = this.discountId;
                instance.productId = this.productId;
                instance.productName = this.productName;
                instance.category = this.category;
                instance.basePrice = this.basePrice;
                instance.discountPercentage = this.discountPercentage;
                instance.discountedPrice = this.discountedPrice;
                instance.status = this.status;
                instance.earningsImpact = this.earningsImpact;
                instance.adminNotes = this.adminNotes;
                instance.createdAt = this.createdAt;
                instance.updatedAt = this.updatedAt;
                return instance;
            }
        }
    }

    public static class EarningsImpact {
        private BigDecimal originalEarnings;
        private BigDecimal discountedEarnings;
        private BigDecimal difference;

        public EarningsImpact() {
        }

        public BigDecimal getOriginalEarnings() {
            return originalEarnings;
        }

        public void setOriginalEarnings(BigDecimal originalEarnings) {
            this.originalEarnings = originalEarnings;
        }

        public BigDecimal getDiscountedEarnings() {
            return discountedEarnings;
        }

        public void setDiscountedEarnings(BigDecimal discountedEarnings) {
            this.discountedEarnings = discountedEarnings;
        }

        public BigDecimal getDifference() {
            return difference;
        }

        public void setDifference(BigDecimal difference) {
            this.difference = difference;
        }

        public static EarningsImpactBuilder builder() {
            return new EarningsImpactBuilder();
        }

        public static class EarningsImpactBuilder {
            private BigDecimal originalEarnings;
            private BigDecimal discountedEarnings;
            private BigDecimal difference;

            public EarningsImpactBuilder originalEarnings(BigDecimal originalEarnings) { this.originalEarnings = originalEarnings; return this; }
            public EarningsImpactBuilder discountedEarnings(BigDecimal discountedEarnings) { this.discountedEarnings = discountedEarnings; return this; }
            public EarningsImpactBuilder difference(BigDecimal difference) { this.difference = difference; return this; }

            public EarningsImpact build() {
                EarningsImpact instance = new EarningsImpact();
                instance.originalEarnings = this.originalEarnings;
                instance.discountedEarnings = this.discountedEarnings;
                instance.difference = this.difference;
                return instance;
            }
        }
    }

    public static class DiscountLimits {
        private BigDecimal minDiscount;
        private BigDecimal maxDiscount;
        private boolean autoApprove;

        public DiscountLimits() {
        }

        public BigDecimal getMinDiscount() {
            return minDiscount;
        }

        public void setMinDiscount(BigDecimal minDiscount) {
            this.minDiscount = minDiscount;
        }

        public BigDecimal getMaxDiscount() {
            return maxDiscount;
        }

        public void setMaxDiscount(BigDecimal maxDiscount) {
            this.maxDiscount = maxDiscount;
        }

        public boolean isAutoApprove() {
            return autoApprove;
        }

        public void setAutoApprove(boolean autoApprove) {
            this.autoApprove = autoApprove;
        }

        public static DiscountLimitsBuilder builder() {
            return new DiscountLimitsBuilder();
        }

        public static class DiscountLimitsBuilder {
            private BigDecimal minDiscount;
            private BigDecimal maxDiscount;
            private boolean autoApprove;

            public DiscountLimitsBuilder minDiscount(BigDecimal minDiscount) { this.minDiscount = minDiscount; return this; }
            public DiscountLimitsBuilder maxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; return this; }
            public DiscountLimitsBuilder autoApprove(boolean autoApprove) { this.autoApprove = autoApprove; return this; }

            public DiscountLimits build() {
                DiscountLimits instance = new DiscountLimits();
                instance.minDiscount = this.minDiscount;
                instance.maxDiscount = this.maxDiscount;
                instance.autoApprove = this.autoApprove;
                return instance;
            }
        }
    }
}
