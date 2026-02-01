package com.brandkit.partner.repository;

import com.brandkit.partner.entity.DiscountStatus;
import com.brandkit.partner.entity.PartnerDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PartnerDiscount entity - FRD-005 FR-64b
 */
@Repository
public interface PartnerDiscountRepository extends JpaRepository<PartnerDiscount, UUID> {

    /**
     * Find discount by partner and product
     */
    Optional<PartnerDiscount> findByPartnerIdAndProductId(UUID partnerId, UUID productId);

    /**
     * Find all discounts by partner
     */
    Page<PartnerDiscount> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find discounts by partner and status
     */
    List<PartnerDiscount> findByPartnerIdAndStatus(UUID partnerId, DiscountStatus status);

    /**
     * Find all active discounts for a product
     */
    Optional<PartnerDiscount> findByProductIdAndStatus(UUID productId, DiscountStatus status);

    /**
     * Count discounts by partner and status
     */
    long countByPartnerIdAndStatus(UUID partnerId, DiscountStatus status);

    /**
     * Find all pending discounts (for admin review)
     */
    Page<PartnerDiscount> findByStatusOrderByCreatedAtAsc(DiscountStatus status, Pageable pageable);

    /**
     * Find discounts by product IDs
     */
    @Query("SELECT pd FROM PartnerDiscount pd WHERE pd.product.id IN :productIds AND pd.status = :status")
    List<PartnerDiscount> findByProductIdsAndStatus(@Param("productIds") List<UUID> productIds, @Param("status") DiscountStatus status);
}
