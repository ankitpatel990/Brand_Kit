package com.brandkit.order.repository;

import com.brandkit.order.entity.Order;
import com.brandkit.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Order entity - FRD-004 FR-43
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find order by ID and user ID (security check)
     */
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find order by order number and user ID (security check)
     */
    Optional<Order> findByOrderNumberAndUserId(String orderNumber, UUID userId);

    /**
     * Find all orders for a user with pagination
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Find orders by user ID and status
     */
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatus status, Pageable pageable);

    /**
     * Find orders by user ID and multiple statuses
     */
    Page<Order> findByUserIdAndStatusInOrderByCreatedAtDesc(UUID userId, List<OrderStatus> statuses, Pageable pageable);

    /**
     * Find order with items eagerly loaded
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") UUID orderId);

    /**
     * Find order with items and address
     */
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.items " +
           "LEFT JOIN FETCH o.deliveryAddress " +
           "WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithDetails(@Param("orderId") UUID orderId, @Param("userId") UUID userId);

    /**
     * Find orders by status (for admin)
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    /**
     * Find orders by partner ID (internal)
     */
    Page<Order> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find pending payment orders that have expired
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.paymentTimeoutAt < :now")
    List<Order> findExpiredPendingPaymentOrders(@Param("now") OffsetDateTime now);

    /**
     * Count orders by user ID
     */
    long countByUserId(UUID userId);

    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Search orders by order number
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderNumber LIKE :search")
    Page<Order> searchByOrderNumber(@Param("userId") UUID userId, @Param("search") String search, Pageable pageable);

    // ============================================================================
    // ADMIN DASHBOARD QUERIES - FRD-006 FR-67
    // ============================================================================

    /**
     * Sum total amount within date range
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status NOT IN ('CANCELLED', 'PAYMENT_FAILED')")
    Optional<java.math.BigDecimal> sumTotalAmountByDateRange(@Param("startDate") java.time.ZonedDateTime startDate, @Param("endDate") java.time.ZonedDateTime endDate);

    /**
     * Count orders within date range
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") java.time.ZonedDateTime startDate, @Param("endDate") java.time.ZonedDateTime endDate);

    /**
     * Get daily revenue trend
     */
    @Query(value = "SELECT DATE(created_at) as date, COALESCE(SUM(total_amount), 0) as revenue FROM orders WHERE DATE(created_at) BETWEEN :startDate AND :endDate AND status NOT IN ('CANCELLED', 'PAYMENT_FAILED') GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyRevenueTrend(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    /**
     * Get daily orders trend
     */
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM orders WHERE DATE(created_at) BETWEEN :startDate AND :endDate GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyOrdersTrend(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    /**
     * Get order status distribution
     */
    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusDistribution();

    /**
     * Find recent orders for dashboard
     */
    List<Order> findTop10ByOrderByCreatedAtDesc();

    /**
     * Count pending orders (awaiting partner acceptance)
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('CONFIRMED', 'PENDING_ACCEPTANCE')")
    Long countPendingOrders();

    /**
     * Count orders requiring reassignment (partner rejected)
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PARTNER_REJECTED'")
    Long countByStatusRequiringReassignment();

    // ============================================================================
    // ADMIN ORDER MANAGEMENT - FRD-006 FR-71
    // ============================================================================

    /**
     * Find all orders with pagination (admin view)
     */
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find orders by partner ID with pagination
     */
    Page<Order> findByPartnerIdAndStatusOrderByCreatedAtDesc(UUID partnerId, OrderStatus status, Pageable pageable);

    /**
     * Search orders by order ID or client email (admin)
     */
    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:search% OR o.user.email LIKE %:search%")
    Page<Order> searchOrdersAdmin(@Param("search") String search, Pageable pageable);
}
