package com.brandkit.order.repository;

import com.brandkit.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CartItem entity - FRD-004 FR-39
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    /**
     * Find all items in a cart
     */
    List<CartItem> findByCartId(UUID cartId);

    /**
     * Find cart item by cart, product, and customization
     */
    Optional<CartItem> findByCartIdAndProductIdAndCustomizationId(
            UUID cartId, UUID productId, UUID customizationId);

    /**
     * Find cart item by ID and cart ID (security check)
     */
    Optional<CartItem> findByIdAndCartId(UUID itemId, UUID cartId);

    /**
     * Count items in a cart
     */
    long countByCartId(UUID cartId);

    /**
     * Delete all items in a cart
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") UUID cartId);

    /**
     * Find items by product ID (for validation)
     */
    List<CartItem> findByProductId(UUID productId);

    /**
     * Get distinct partner IDs from cart items
     */
    @Query("SELECT DISTINCT p.partner.id FROM CartItem ci " +
           "JOIN ci.product p WHERE ci.cart.id = :cartId AND p.partner IS NOT NULL")
    List<UUID> findDistinctPartnerIdsByCartId(@Param("cartId") UUID cartId);
}
