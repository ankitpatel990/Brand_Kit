package com.brandkit.partner.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Partner Order Response - FRD-005 FR-54, FR-55
 * Order details for partner view
 */
public class PartnerOrderResponse {

    private String orderId;
    private String orderNumber;
    private String orderDate;
    private String status;
    private String partnerStatus;
    private String expectedShipDate;
    private ProductDetails product;
    private DeliveryInfo delivery;
    private CommissionInfo commission;
    private List<String> actions;
    private List<StatusHistoryItem> timeline;
    private List<ProofImageDto> proofs;
    private String notes;

    public PartnerOrderResponse() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPartnerStatus() {
        return partnerStatus;
    }

    public void setPartnerStatus(String partnerStatus) {
        this.partnerStatus = partnerStatus;
    }

    public String getExpectedShipDate() {
        return expectedShipDate;
    }

    public void setExpectedShipDate(String expectedShipDate) {
        this.expectedShipDate = expectedShipDate;
    }

    public ProductDetails getProduct() {
        return product;
    }

    public void setProduct(ProductDetails product) {
        this.product = product;
    }

    public DeliveryInfo getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryInfo delivery) {
        this.delivery = delivery;
    }

    public CommissionInfo getCommission() {
        return commission;
    }

    public void setCommission(CommissionInfo commission) {
        this.commission = commission;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<StatusHistoryItem> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<StatusHistoryItem> timeline) {
        this.timeline = timeline;
    }

    public List<ProofImageDto> getProofs() {
        return proofs;
    }

    public void setProofs(List<ProofImageDto> proofs) {
        this.proofs = proofs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static PartnerOrderResponseBuilder builder() {
        return new PartnerOrderResponseBuilder();
    }

    public static class PartnerOrderResponseBuilder {
        private String orderId;
        private String orderNumber;
        private String orderDate;
        private String status;
        private String partnerStatus;
        private String expectedShipDate;
        private ProductDetails product;
        private DeliveryInfo delivery;
        private CommissionInfo commission;
        private List<String> actions;
        private List<StatusHistoryItem> timeline;
        private List<ProofImageDto> proofs;
        private String notes;

        public PartnerOrderResponseBuilder orderId(String orderId) { this.orderId = orderId; return this; }
        public PartnerOrderResponseBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public PartnerOrderResponseBuilder orderDate(String orderDate) { this.orderDate = orderDate; return this; }
        public PartnerOrderResponseBuilder status(String status) { this.status = status; return this; }
        public PartnerOrderResponseBuilder partnerStatus(String partnerStatus) { this.partnerStatus = partnerStatus; return this; }
        public PartnerOrderResponseBuilder expectedShipDate(String expectedShipDate) { this.expectedShipDate = expectedShipDate; return this; }
        public PartnerOrderResponseBuilder product(ProductDetails product) { this.product = product; return this; }
        public PartnerOrderResponseBuilder delivery(DeliveryInfo delivery) { this.delivery = delivery; return this; }
        public PartnerOrderResponseBuilder commission(CommissionInfo commission) { this.commission = commission; return this; }
        public PartnerOrderResponseBuilder actions(List<String> actions) { this.actions = actions; return this; }
        public PartnerOrderResponseBuilder timeline(List<StatusHistoryItem> timeline) { this.timeline = timeline; return this; }
        public PartnerOrderResponseBuilder proofs(List<ProofImageDto> proofs) { this.proofs = proofs; return this; }
        public PartnerOrderResponseBuilder notes(String notes) { this.notes = notes; return this; }

        public PartnerOrderResponse build() {
            PartnerOrderResponse instance = new PartnerOrderResponse();
            instance.orderId = this.orderId;
            instance.orderNumber = this.orderNumber;
            instance.orderDate = this.orderDate;
            instance.status = this.status;
            instance.partnerStatus = this.partnerStatus;
            instance.expectedShipDate = this.expectedShipDate;
            instance.product = this.product;
            instance.delivery = this.delivery;
            instance.commission = this.commission;
            instance.actions = this.actions;
            instance.timeline = this.timeline;
            instance.proofs = this.proofs;
            instance.notes = this.notes;
            return instance;
        }
    }

    public static class ProductDetails {
        private String productId;
        private String name;
        private String category;
        private int quantity;
        private String customizationType;
        private String printReadyImageUrl;
        private String previewImageUrl;
        private String specifications;

        public ProductDetails() {
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getCustomizationType() {
            return customizationType;
        }

        public void setCustomizationType(String customizationType) {
            this.customizationType = customizationType;
        }

        public String getPrintReadyImageUrl() {
            return printReadyImageUrl;
        }

        public void setPrintReadyImageUrl(String printReadyImageUrl) {
            this.printReadyImageUrl = printReadyImageUrl;
        }

        public String getPreviewImageUrl() {
            return previewImageUrl;
        }

        public void setPreviewImageUrl(String previewImageUrl) {
            this.previewImageUrl = previewImageUrl;
        }

        public String getSpecifications() {
            return specifications;
        }

        public void setSpecifications(String specifications) {
            this.specifications = specifications;
        }

        public static ProductDetailsBuilder builder() {
            return new ProductDetailsBuilder();
        }

        public static class ProductDetailsBuilder {
            private String productId;
            private String name;
            private String category;
            private int quantity;
            private String customizationType;
            private String printReadyImageUrl;
            private String previewImageUrl;
            private String specifications;

            public ProductDetailsBuilder productId(String productId) { this.productId = productId; return this; }
            public ProductDetailsBuilder name(String name) { this.name = name; return this; }
            public ProductDetailsBuilder category(String category) { this.category = category; return this; }
            public ProductDetailsBuilder quantity(int quantity) { this.quantity = quantity; return this; }
            public ProductDetailsBuilder customizationType(String customizationType) { this.customizationType = customizationType; return this; }
            public ProductDetailsBuilder printReadyImageUrl(String printReadyImageUrl) { this.printReadyImageUrl = printReadyImageUrl; return this; }
            public ProductDetailsBuilder previewImageUrl(String previewImageUrl) { this.previewImageUrl = previewImageUrl; return this; }
            public ProductDetailsBuilder specifications(String specifications) { this.specifications = specifications; return this; }

            public ProductDetails build() {
                ProductDetails instance = new ProductDetails();
                instance.productId = this.productId;
                instance.name = this.name;
                instance.category = this.category;
                instance.quantity = this.quantity;
                instance.customizationType = this.customizationType;
                instance.printReadyImageUrl = this.printReadyImageUrl;
                instance.previewImageUrl = this.previewImageUrl;
                instance.specifications = this.specifications;
                return instance;
            }
        }
    }

    public static class DeliveryInfo {
        private String city;
        private String state;
        private String pinCode;
        private String fullAddress;
        private String deliveryOption;
        private boolean addressRevealed;

        public DeliveryInfo() {
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPinCode() {
            return pinCode;
        }

        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }

        public String getFullAddress() {
            return fullAddress;
        }

        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }

        public String getDeliveryOption() {
            return deliveryOption;
        }

        public void setDeliveryOption(String deliveryOption) {
            this.deliveryOption = deliveryOption;
        }

        public boolean isAddressRevealed() {
            return addressRevealed;
        }

        public void setAddressRevealed(boolean addressRevealed) {
            this.addressRevealed = addressRevealed;
        }

        public static DeliveryInfoBuilder builder() {
            return new DeliveryInfoBuilder();
        }

        public static class DeliveryInfoBuilder {
            private String city;
            private String state;
            private String pinCode;
            private String fullAddress;
            private String deliveryOption;
            private boolean addressRevealed;

            public DeliveryInfoBuilder city(String city) { this.city = city; return this; }
            public DeliveryInfoBuilder state(String state) { this.state = state; return this; }
            public DeliveryInfoBuilder pinCode(String pinCode) { this.pinCode = pinCode; return this; }
            public DeliveryInfoBuilder fullAddress(String fullAddress) { this.fullAddress = fullAddress; return this; }
            public DeliveryInfoBuilder deliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; return this; }
            public DeliveryInfoBuilder addressRevealed(boolean addressRevealed) { this.addressRevealed = addressRevealed; return this; }

            public DeliveryInfo build() {
                DeliveryInfo instance = new DeliveryInfo();
                instance.city = this.city;
                instance.state = this.state;
                instance.pinCode = this.pinCode;
                instance.fullAddress = this.fullAddress;
                instance.deliveryOption = this.deliveryOption;
                instance.addressRevealed = this.addressRevealed;
                return instance;
            }
        }
    }

    public static class CommissionInfo {
        private BigDecimal productAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private BigDecimal commissionPercentage;
        private BigDecimal platformCommission;
        private BigDecimal partnerEarnings;

        public CommissionInfo() {
        }

        public BigDecimal getProductAmount() {
            return productAmount;
        }

        public void setProductAmount(BigDecimal productAmount) {
            this.productAmount = productAmount;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }

        public BigDecimal getFinalAmount() {
            return finalAmount;
        }

        public void setFinalAmount(BigDecimal finalAmount) {
            this.finalAmount = finalAmount;
        }

        public BigDecimal getCommissionPercentage() {
            return commissionPercentage;
        }

        public void setCommissionPercentage(BigDecimal commissionPercentage) {
            this.commissionPercentage = commissionPercentage;
        }

        public BigDecimal getPlatformCommission() {
            return platformCommission;
        }

        public void setPlatformCommission(BigDecimal platformCommission) {
            this.platformCommission = platformCommission;
        }

        public BigDecimal getPartnerEarnings() {
            return partnerEarnings;
        }

        public void setPartnerEarnings(BigDecimal partnerEarnings) {
            this.partnerEarnings = partnerEarnings;
        }

        public static CommissionInfoBuilder builder() {
            return new CommissionInfoBuilder();
        }

        public static class CommissionInfoBuilder {
            private BigDecimal productAmount;
            private BigDecimal discountAmount;
            private BigDecimal finalAmount;
            private BigDecimal commissionPercentage;
            private BigDecimal platformCommission;
            private BigDecimal partnerEarnings;

            public CommissionInfoBuilder productAmount(BigDecimal productAmount) { this.productAmount = productAmount; return this; }
            public CommissionInfoBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
            public CommissionInfoBuilder finalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; return this; }
            public CommissionInfoBuilder commissionPercentage(BigDecimal commissionPercentage) { this.commissionPercentage = commissionPercentage; return this; }
            public CommissionInfoBuilder platformCommission(BigDecimal platformCommission) { this.platformCommission = platformCommission; return this; }
            public CommissionInfoBuilder partnerEarnings(BigDecimal partnerEarnings) { this.partnerEarnings = partnerEarnings; return this; }

            public CommissionInfo build() {
                CommissionInfo instance = new CommissionInfo();
                instance.productAmount = this.productAmount;
                instance.discountAmount = this.discountAmount;
                instance.finalAmount = this.finalAmount;
                instance.commissionPercentage = this.commissionPercentage;
                instance.platformCommission = this.platformCommission;
                instance.partnerEarnings = this.partnerEarnings;
                return instance;
            }
        }
    }

    public static class StatusHistoryItem {
        private String status;
        private String description;
        private String timestamp;

        public StatusHistoryItem() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public static StatusHistoryItemBuilder builder() {
            return new StatusHistoryItemBuilder();
        }

        public static class StatusHistoryItemBuilder {
            private String status;
            private String description;
            private String timestamp;

            public StatusHistoryItemBuilder status(String status) { this.status = status; return this; }
            public StatusHistoryItemBuilder description(String description) { this.description = description; return this; }
            public StatusHistoryItemBuilder timestamp(String timestamp) { this.timestamp = timestamp; return this; }

            public StatusHistoryItem build() {
                StatusHistoryItem instance = new StatusHistoryItem();
                instance.status = this.status;
                instance.description = this.description;
                instance.timestamp = this.timestamp;
                return instance;
            }
        }
    }

    public static class ProofImageDto {
        private String id;
        private String imageUrl;
        private String caption;
        private String uploadedAt;

        public ProofImageDto() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(String uploadedAt) {
            this.uploadedAt = uploadedAt;
        }

        public static ProofImageDtoBuilder builder() {
            return new ProofImageDtoBuilder();
        }

        public static class ProofImageDtoBuilder {
            private String id;
            private String imageUrl;
            private String caption;
            private String uploadedAt;

            public ProofImageDtoBuilder id(String id) {
                this.id = id;
                return this;
            }
            public ProofImageDtoBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }
            public ProofImageDtoBuilder caption(String caption) {
                this.caption = caption;
                return this;
            }
            public ProofImageDtoBuilder uploadedAt(String uploadedAt) {
                this.uploadedAt = uploadedAt;
                return this;
            }

            public ProofImageDto build() {
                ProofImageDto instance = new ProofImageDto();
                instance.id = this.id;
                instance.imageUrl = this.imageUrl;
                instance.caption = this.caption;
                instance.uploadedAt = this.uploadedAt;
                return instance;
            }
        }
    }
}
