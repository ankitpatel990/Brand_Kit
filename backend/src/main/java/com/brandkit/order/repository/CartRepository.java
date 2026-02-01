package com.brandkit.order.repository;

import com.brandkit.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Cart entity - FRD-004 FR-39
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    /**
     * Find cart by user ID
     */
    Optional<Cart> findByUserId(UUID userId);

    /**
     * Find cart by session ID (for guests)
     */
    Optional<Cart> findBySessionId(String sessionId);

    /**
     * Find cart with items eagerly loaded
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") UUID userId);

    /**
     * Find cart with items eagerly loaded by session ID
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.sessionId = :sessionId")
    Optional<Cart> findBySessionIdWithItems(@Param("sessionId") String sessionId);

    /**
     * Check if user has a cart
     */
    boolean existsByUserId(UUID userId);

    /**
     * Delete cart by user ID
     */
    void deleteByUserId(UUID userId);
}
