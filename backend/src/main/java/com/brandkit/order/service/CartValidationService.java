package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductStatus;
import com.brandkit.order.dto.CartResponse;
import com.brandkit.order.dto.CartValidationResponse;
import com.brandkit.order.entity.Cart;
import com.brandkit.order.entity.CartItem;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.CartItemRepository;
import com.brandkit.order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for cart validation before checkout - FRD-004 FR-40, Sub-Prompt 2
 */
@Service
@Transactional
public class CartValidationService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;

    /**
     * Validate cart before checkout
     */
    public CartValidationResponse validateCart(User user) {
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(OrderException::cartEmpty);

        if (cart.isEmpty()) {
            throw OrderException.cartEmpty();
        }

        CartValidationResponse response = new CartValidationResponse();
        response.setIsValid(true);
        response.setPricesUpdated(false);

        for (CartItem item : cart.getItems()) {
            validateCartItem(item, response);
        }

        // Validate single partner constraint (MVP)
        validateSinglePartner(cart, response);

        // Get updated cart
        CartResponse updatedCart = cartService.getCart(user);
        response.setUpdatedCart(updatedCart);

        return response;
    }

    /**
     * Validate individual cart item
     */
    private void validateCartItem(CartItem item, CartValidationResponse response) {
        Product product = item.getProduct();

        // Check product availability
        if (product.getStatus() != ProductStatus.ACTIVE) {
            response.addError(item.getId(), "ORD_002", 
                    "Product '" + product.getName() + "' is no longer available");
            return;
        }

        // Check partner availability (internal check, generic message to client)
        if (product.getPartner() == null || 
            product.getPartner().getStatus() == null ||
            !product.getPartner().getStatus().name().equals("ACTIVE")) {
            // Generic message - NO partner details exposed
            response.addError(item.getId(), "ORD_009", 
                    "Unable to process order for '" + product.getName() + "'");
            return;
        }

        // Check quantity limits
        if (item.getQuantity() < 1 || item.getQuantity() > 10000) {
            response.addError(item.getId(), "ORD_003", 
                    "Quantity must be between 1 and 10,000 for '" + product.getName() + "'");
            return;
        }

        // Check customization integrity
        if (item.getCustomization() != null) {
            if (item.getCustomization().getCroppedImageUrl() == null ||
                item.getCustomization().getCroppedImageUrl().isBlank()) {
                response.addError(item.getId(), "ORD_016", 
                        "Customization data missing for '" + product.getName() + "'");
            }
        }

        // Note: Price recalculation happens automatically in CartService
    }

    /**
     * Validate that all items are from the same partner (MVP constraint)
     */
    private void validateSinglePartner(Cart cart, CartValidationResponse response) {
        List<UUID> partnerIds = cartItemRepository.findDistinctPartnerIdsByCartId(cart.getId());
        
        if (partnerIds.size() > 1) {
            // Generic error message - NO partner details exposed
            response.addError(null, "ORD_008", 
                    "Unable to process these items together. Please complete your current order first");
        }
    }

    /**
     * Remove invalid items from cart
     */
    public CartResponse removeInvalidItems(User user, List<UUID> invalidItemIds) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(OrderException::cartEmpty);

        for (UUID itemId : invalidItemIds) {
            cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                    .ifPresent(item -> {
                        cart.removeItem(item);
                        cartItemRepository.delete(item);
                    });
        }

        return cartService.getCart(user);
    }
}
