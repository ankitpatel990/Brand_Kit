package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.PricingTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Pricing Tier Repository
 * FRD-002 FR-16: Pricing Tier Structure
 */
@Repository
public interface PricingTierRepository extends JpaRepository<PricingTier, UUID> {
    
    List<PricingTier> findByProductIdOrderByTierNumberAsc(UUID productId);
    
    @Query("SELECT pt FROM PricingTier pt WHERE pt.product.id = :productId AND pt.minQuantity <= :quantity AND (pt.maxQuantity IS NULL OR pt.maxQuantity >= :quantity)")
    Optional<PricingTier> findApplicableTier(@Param("productId") UUID productId, @Param("quantity") int quantity);
    
    @Modifying
    @Query("DELETE FROM PricingTier pt WHERE pt.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
    
    @Query("SELECT MAX(pt.tierNumber) FROM PricingTier pt WHERE pt.product.id = :productId")
    Integer findMaxTierNumber(@Param("productId") UUID productId);
}
