package com.brandkit.order.repository;

import com.brandkit.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for OrderItem entity - FRD-004 FR-43
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Find all items for an order
     */
    List<OrderItem> findByOrderId(UUID orderId);

    /**
     * Find items by order ID with product details
     */
    @Query("SELECT oi FROM OrderItem oi " +
           "LEFT JOIN FETCH oi.product " +
           "LEFT JOIN FETCH oi.customization " +
           "WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithDetails(@Param("orderId") UUID orderId);

    /**
     * Count items in an order
     */
    long countByOrderId(UUID orderId);

    /**
     * Find items by product ID
     */
    List<OrderItem> findByProductId(UUID productId);
}
