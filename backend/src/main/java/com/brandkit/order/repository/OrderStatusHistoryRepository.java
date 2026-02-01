package com.brandkit.order.repository;

import com.brandkit.order.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for OrderStatusHistory entity - FRD-004 FR-46
 */
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

    /**
     * Find all status history for an order
     */
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(UUID orderId);

    /**
     * Find latest status for an order
     */
    OrderStatusHistory findFirstByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
