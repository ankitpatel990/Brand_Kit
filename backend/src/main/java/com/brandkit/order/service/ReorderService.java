package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductStatus;
import com.brandkit.order.dto.CartResponse;
import com.brandkit.order.dto.ReorderResponse;
import com.brandkit.order.entity.Order;
import com.brandkit.order.entity.OrderItem;
import com.brandkit.order.entity.OrderStatus;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for reorder functionality - FRD-004 Sub-Prompt 10
 */
@Service
@Transactional
public class ReorderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;

    /**
     * Reorder items from a past order
     */
    public ReorderResponse reorder(User user, UUID orderId) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, user.getId())
                .orElseThrow(OrderException::orderNotFound);

        // Only allow reorder for delivered orders
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new OrderException("ORD_020", "Reorder is only available for delivered orders");
        }

        ReorderResponse response = new ReorderResponse();
        response.setSuccess(true);
        response.setItemsAdded(0);
        response.setItemsUnavailable(0);
        response.setPricesUpdated(false);

        // Clear existing cart first
        cartService.clearCart(user);

        // Add items from past order to cart
        for (OrderItem orderItem : order.getItems()) {
            try {
                Product product = orderItem.getProduct();

                // Check if product is still available
                if (product.getStatus() != ProductStatus.ACTIVE) {
                    response.addUnavailableItem(
                            product.getId(),
                            orderItem.getProductName(),
                            "Product no longer available"
                    );
                    response.setItemsUnavailable(response.getItemsUnavailable() + 1);
                    continue;
                }

                // Check if customization is still available
                if (orderItem.getCustomization() != null) {
                    if (orderItem.getCustomization().getCroppedImageUrl() == null ||
                        orderItem.getCustomization().getCroppedImageUrl().isBlank()) {
                        response.addUnavailableItem(
                                product.getId(),
                                orderItem.getProductName(),
                                "Customization unavailable, please re-upload logo"
                        );
                        response.setItemsUnavailable(response.getItemsUnavailable() + 1);
                        continue;
                    }
                }

                // Add to cart
                com.brandkit.order.dto.AddToCartRequest addRequest = new com.brandkit.order.dto.AddToCartRequest();
                addRequest.setProductId(product.getId());
                addRequest.setCustomizationId(orderItem.getCustomization() != null ? 
                        orderItem.getCustomization().getId() : null);
                addRequest.setQuantity(orderItem.getQuantity());

                cartService.addToCart(user, addRequest);
                response.setItemsAdded(response.getItemsAdded() + 1);

                // Check if prices changed
                // Price comparison happens in CartService when adding items

            } catch (Exception e) {
                response.addUnavailableItem(
                        orderItem.getProduct().getId(),
                        orderItem.getProductName(),
                        "Unable to add to cart"
                );
                response.setItemsUnavailable(response.getItemsUnavailable() + 1);
            }
        }

        // Get updated cart
        CartResponse cart = cartService.getCart(user);
        response.setCart(cart);

        // Set appropriate message
        if (response.getItemsUnavailable() > 0) {
            if (response.getItemsAdded() > 0) {
                response.setMessage(String.format(
                        "%d items added to cart. %d items were unavailable.",
                        response.getItemsAdded(),
                        response.getItemsUnavailable()
                ));
            } else {
                response.setSuccess(false);
                response.setMessage("All items from this order are unavailable.");
            }
        } else {
            response.setMessage(String.format(
                    "%d items added to cart from previous order",
                    response.getItemsAdded()
            ));
        }

        // Check if prices have changed
        // Compare original order total with new cart total
        if (cart.getPricing() != null && order.getSubtotal() != null) {
            if (cart.getPricing().getSubtotal().compareTo(order.getSubtotal()) != 0) {
                response.setPricesUpdated(true);
                response.setMessage(response.getMessage() + ". Prices have been updated.");
            }
        }

        return response;
    }
}
