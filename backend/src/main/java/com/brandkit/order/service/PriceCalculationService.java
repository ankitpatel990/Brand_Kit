package com.brandkit.order.service;

import com.brandkit.catalog.entity.PricingTier;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductDiscount;
import com.brandkit.catalog.repository.PricingTierRepository;
import com.brandkit.catalog.repository.ProductDiscountRepository;
import com.brandkit.order.entity.DeliveryOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for price calculations - FRD-004 FR-49, Sub-Prompt 9
 */
@Service
public class PriceCalculationService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18"); // 18%
    private static final BigDecimal CGST_RATE = new BigDecimal("0.09"); // 9%
    private static final BigDecimal SGST_RATE = new BigDecimal("0.09"); // 9%
    private static final BigDecimal IGST_RATE = new BigDecimal("0.18"); // 18%
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("10000");
    private static final String BRANDKIT_STATE = "Gujarat"; // BrandKit is based in Gujarat

    @Autowired
    private PricingTierRepository pricingTierRepository;
    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    /**
     * Calculate unit price based on quantity tier
     */
    public BigDecimal calculateTierPrice(Product product, int quantity) {
        List<PricingTier> tiers = pricingTierRepository.findByProductIdOrderByTierNumberAsc(product.getId());
        
        if (tiers.isEmpty()) {
            return product.getBasePrice();
        }

        // Find the applicable tier based on quantity
        for (int i = tiers.size() - 1; i >= 0; i--) {
            PricingTier tier = tiers.get(i);
            if (quantity >= tier.getMinQuantity()) {
                if (tier.getMaxQuantity() == null || quantity <= tier.getMaxQuantity()) {
                    return tier.getUnitPrice();
                }
            }
        }

        return product.getBasePrice();
    }

    /**
     * Get active discount for a product
     */
    public Optional<ProductDiscount> getActiveDiscount(UUID productId) {
        return productDiscountRepository.findActiveDiscountForProduct(productId, java.time.ZonedDateTime.now());
    }

    /**
     * Calculate discounted price
     */
    public BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercentage) {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        return price.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get customization fee based on customization type
     */
    public BigDecimal getCustomizationFee(Product product) {
        if (!product.getCustomizationAvailable()) {
            return BigDecimal.ZERO;
        }

        return switch (product.getCustomizationType()) {
            case LOGO_PRINT -> new BigDecimal("20.00");
            case EMBROIDERY -> new BigDecimal("50.00");
            case ENGRAVING -> new BigDecimal("30.00");
            default -> BigDecimal.ZERO;
        };
    }

    /**
     * Calculate item subtotal
     */
    public BigDecimal calculateItemSubtotal(BigDecimal unitPrice, BigDecimal customizationFee, int quantity) {
        BigDecimal effectivePrice = unitPrice.add(customizationFee);
        return effectivePrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate GST amount
     */
    public BigDecimal calculateGst(BigDecimal subtotal) {
        return subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate CGST amount (for intra-state)
     */
    public BigDecimal calculateCgst(BigDecimal subtotal) {
        return subtotal.multiply(CGST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate SGST amount (for intra-state)
     */
    public BigDecimal calculateSgst(BigDecimal subtotal) {
        return subtotal.multiply(SGST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate IGST amount (for inter-state)
     */
    public BigDecimal calculateIgst(BigDecimal subtotal) {
        return subtotal.multiply(IGST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Check if order is inter-state (different state from BrandKit)
     */
    public boolean isInterState(String deliveryState) {
        return !BRANDKIT_STATE.equalsIgnoreCase(deliveryState);
    }

    /**
     * Calculate delivery charges
     */
    public BigDecimal calculateDeliveryCharge(BigDecimal subtotal, DeliveryOption option) {
        if (option == DeliveryOption.STANDARD && subtotal.compareTo(FREE_DELIVERY_THRESHOLD) > 0) {
            return BigDecimal.ZERO;
        }
        return option.getBaseCharge();
    }

    /**
     * Check if eligible for free delivery
     */
    public boolean isEligibleForFreeDelivery(BigDecimal subtotal) {
        return subtotal.compareTo(FREE_DELIVERY_THRESHOLD) > 0;
    }

    /**
     * Get free delivery threshold
     */
    public BigDecimal getFreeDeliveryThreshold() {
        return FREE_DELIVERY_THRESHOLD;
    }

    /**
     * Calculate total order amount
     */
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal gst, BigDecimal deliveryCharges) {
        return subtotal.add(gst).add(deliveryCharges).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate discount savings
     */
    public BigDecimal calculateSavings(BigDecimal originalSubtotal, BigDecimal discountedSubtotal) {
        return originalSubtotal.subtract(discountedSubtotal).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Data class for calculated pricing
     */
    public static class CalculatedPricing {
        private BigDecimal originalSubtotal;
        private BigDecimal subtotal;
        private BigDecimal totalDiscount;
        private BigDecimal gstAmount;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;
        private BigDecimal igstAmount;
        private BigDecimal deliveryCharges;
        private BigDecimal total;
        private boolean isInterState;
        private boolean freeDeliveryEligible;

        // Getters and Setters
        public BigDecimal getOriginalSubtotal() { return originalSubtotal; }
        public void setOriginalSubtotal(BigDecimal originalSubtotal) { this.originalSubtotal = originalSubtotal; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public BigDecimal getTotalDiscount() { return totalDiscount; }
        public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }
        public BigDecimal getGstAmount() { return gstAmount; }
        public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
        public BigDecimal getCgstAmount() { return cgstAmount; }
        public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }
        public BigDecimal getSgstAmount() { return sgstAmount; }
        public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }
        public BigDecimal getIgstAmount() { return igstAmount; }
        public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }
        public BigDecimal getDeliveryCharges() { return deliveryCharges; }
        public void setDeliveryCharges(BigDecimal deliveryCharges) { this.deliveryCharges = deliveryCharges; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        public boolean isInterState() { return isInterState; }
        public void setInterState(boolean interState) { isInterState = interState; }
        public boolean isFreeDeliveryEligible() { return freeDeliveryEligible; }
        public void setFreeDeliveryEligible(boolean freeDeliveryEligible) { this.freeDeliveryEligible = freeDeliveryEligible; }
    }
}
