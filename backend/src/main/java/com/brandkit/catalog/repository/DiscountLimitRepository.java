package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.DiscountLimit;
import com.brandkit.catalog.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Discount Limit Repository
 * FRD-002 Sub-Prompt 7: Global discount limits
 */
@Repository
public interface DiscountLimitRepository extends JpaRepository<DiscountLimit, UUID> {
    
    // Find active limit for a specific category
    Optional<DiscountLimit> findByCategoryAndIsActiveTrue(ProductCategory category);
    
    // Find global limit (category is null)
    @Query("SELECT dl FROM DiscountLimit dl WHERE dl.category IS NULL AND dl.isActive = true")
    Optional<DiscountLimit> findGlobalActiveLimit();
    
    // Get limit for category with fallback to global
    @Query("""
        SELECT dl FROM DiscountLimit dl 
        WHERE dl.isActive = true 
        AND (dl.category = :category OR dl.category IS NULL)
        ORDER BY dl.category NULLS LAST
        """)
    Optional<DiscountLimit> findLimitForCategory(@Param("category") ProductCategory category);
}
