package com.brandkit.order.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.order.dto.*;
import com.brandkit.order.service.CartService;
import com.brandkit.order.service.CartValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for shopping cart operations - FRD-004 FR-39, FR-40
 */
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartValidationService cartValidationService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Get user's cart
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.getCart(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * Add item to cart
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody AddToCartRequest request) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.addToCart(user, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * Update cart item quantity
     * PUT /api/cart/item/:itemId
     */
    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.updateCartItem(user, itemId, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove item from cart
     * DELETE /api/cart/item/:itemId
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID itemId) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.removeCartItem(user, itemId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Clear cart
     * DELETE /api/cart
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearCart(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
    }

    /**
     * Get cart item count (for header badge)
     * GET /api/cart/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        int count = cartService.getCartItemCount(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Validate cart before checkout
     * POST /api/checkout/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<CartValidationResponse> validateCart(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        CartValidationResponse validation = cartValidationService.validateCart(user);
        return ResponseEntity.ok(validation);
    }

    /**
     * Remove invalid items from cart
     * POST /api/cart/remove-invalid
     */
    @PostMapping("/remove-invalid")
    public ResponseEntity<CartResponse> removeInvalidItems(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody List<UUID> invalidItemIds) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartValidationService.removeInvalidItems(user, invalidItemIds);
        return ResponseEntity.ok(cart);
    }
}
