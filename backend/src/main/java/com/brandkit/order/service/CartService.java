package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductDiscount;
import com.brandkit.catalog.entity.ProductStatus;
import com.brandkit.catalog.repository.ProductRepository;
import com.brandkit.customization.entity.Customization;
import com.brandkit.customization.repository.CustomizationRepository;
import com.brandkit.order.dto.*;
import com.brandkit.order.entity.Cart;
import com.brandkit.order.entity.CartItem;
import com.brandkit.order.entity.DeliveryOption;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.CartItemRepository;
import com.brandkit.order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for shopping cart operations - FRD-004 FR-39, Sub-Prompt 1
 */
@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomizationRepository customizationRepository;
    @Autowired
    private PriceCalculationService priceCalculationService;

    /**
     * Get or create cart for user
     */
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    /**
     * Get cart for user
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        return mapToCartResponse(cart);
    }

    /**
     * Add item to cart
     */
    public CartResponse addToCart(User user, AddToCartRequest request) {
        Cart cart = getOrCreateCart(user);

        // Validate product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new OrderException("ORD_002", "Product not found"));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw OrderException.productUnavailable();
        }

        // Validate quantity
        if (request.getQuantity() < 1 || request.getQuantity() > 10000) {
            throw OrderException.invalidQuantity();
        }

        // Validate customization if provided
        Customization customization = null;
        if (request.getCustomizationId() != null) {
            customization = customizationRepository.findById(request.getCustomizationId())
                    .orElseThrow(OrderException::customizationMissing);
        }

        // Check for existing item with same product and customization
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductIdAndCustomizationId(
                        cart.getId(), 
                        request.getProductId(), 
                        request.getCustomizationId());

        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > 10000) {
                throw OrderException.invalidQuantity();
            }
            item.setQuantity(newQuantity);
            updateItemPricing(item, product);
            cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setCustomization(customization);
            item.setQuantity(request.getQuantity());
            updateItemPricing(item, product);
            cart.addItem(item);
            cartItemRepository.save(item);
        }

        return getCart(user);
    }

    /**
     * Update cart item quantity
     */
    public CartResponse updateCartItem(User user, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(OrderException::cartEmpty);

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(OrderException::cartItemNotFound);

        // Validate quantity
        if (request.getQuantity() < 1 || request.getQuantity() > 10000) {
            throw OrderException.invalidQuantity();
        }

        item.setQuantity(request.getQuantity());
        updateItemPricing(item, item.getProduct());
        cartItemRepository.save(item);

        return getCart(user);
    }

    /**
     * Remove item from cart
     */
    public CartResponse removeCartItem(User user, UUID itemId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(OrderException::cartEmpty);

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(OrderException::cartItemNotFound);

        cart.removeItem(item);
        cartItemRepository.delete(item);

        return getCart(user);
    }

    /**
     * Clear cart
     */
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteAllByCartId(cart.getId());
            cart.clearItems();
        }
    }

    /**
     * Get cart item count for header badge
     */
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        return cartRepository.findByUserId(user.getId())
                .map(Cart::getItemCount)
                .orElse(0);
    }

    /**
     * Update item pricing based on current product prices and discounts
     */
    private void updateItemPricing(CartItem item, Product product) {
        // Calculate tier price based on quantity
        BigDecimal tierPrice = priceCalculationService.calculateTierPrice(product, item.getQuantity());
        
        // Get active discount
        Optional<ProductDiscount> discount = priceCalculationService.getActiveDiscount(product.getId());
        
        BigDecimal discountPercentage = BigDecimal.ZERO;
        BigDecimal discountedPrice = tierPrice;
        
        if (discount.isPresent()) {
            discountPercentage = discount.get().getDiscountPercentage();
            discountedPrice = priceCalculationService.applyDiscount(tierPrice, discountPercentage);
        }

        // Get customization fee
        BigDecimal customizationFee = BigDecimal.ZERO;
        if (item.getCustomization() != null) {
            customizationFee = priceCalculationService.getCustomizationFee(product);
        }

        item.setUnitPrice(discountedPrice);
        item.setDiscountPercentage(discountPercentage);
        item.setCustomizationFee(customizationFee);
        item.calculateSubtotal();
    }

    /**
     * Map cart entity to response DTO
     */
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser() != null ? cart.getUser().getId() : null);

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal originalSubtotal = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : cart.getItems()) {
            CartItemResponse itemResponse = mapToCartItemResponse(item);
            itemResponses.add(itemResponse);

            // Calculate original subtotal (before discounts)
            BigDecimal originalItemSubtotal = item.getProduct().getBasePrice()
                    .add(item.getCustomizationFee())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            originalSubtotal = originalSubtotal.add(originalItemSubtotal);

            subtotal = subtotal.add(item.getSubtotal());
            totalQuantity += item.getQuantity();
        }

        response.setItems(itemResponses);
        response.setItemCount(cart.getItemCount());
        response.setTotalQuantity(totalQuantity);

        // Calculate pricing
        CartResponse.CartPricing pricing = new CartResponse.CartPricing();
        pricing.setOriginalSubtotal(originalSubtotal);
        pricing.setSubtotal(subtotal);
        pricing.setTotalDiscount(originalSubtotal.subtract(subtotal));
        pricing.setGst(priceCalculationService.calculateGst(subtotal));
        pricing.setFreeDeliveryEligible(priceCalculationService.isEligibleForFreeDelivery(subtotal));
        pricing.setFreeDeliveryThreshold(priceCalculationService.getFreeDeliveryThreshold());
        
        // Default to standard delivery for cart display
        pricing.setDeliveryCharges(priceCalculationService.calculateDeliveryCharge(subtotal, DeliveryOption.STANDARD));
        pricing.setTotal(priceCalculationService.calculateTotal(subtotal, pricing.getGst(), pricing.getDeliveryCharges()));

        response.setPricing(pricing);

        return response;
    }

    /**
     * Map cart item entity to response DTO
     */
    private CartItemResponse mapToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setProductSlug(item.getProduct().getSlug());
        
        // Get primary image
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            response.setProductImageUrl(item.getProduct().getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .findFirst()
                    .map(img -> img.getImageUrl())
                    .orElse(item.getProduct().getImages().get(0).getImageUrl()));
        }

        response.setCustomizationId(item.getCustomization() != null ? item.getCustomization().getId() : null);
        response.setPreviewUrl(item.getCustomization() != null ? item.getCustomization().getPreviewImageUrl() : null);
        response.setHasCustomization(item.getCustomization() != null);
        response.setQuantity(item.getQuantity());
        response.setOriginalUnitPrice(item.getProduct().getBasePrice());
        response.setUnitPrice(item.getUnitPrice());
        response.setDiscountPercentage(item.getDiscountPercentage());
        response.setCustomizationFee(item.getCustomizationFee());
        response.setEffectiveUnitPrice(item.getEffectiveUnitPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}
