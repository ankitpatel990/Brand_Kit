package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.DiscountStatus;
import com.brandkit.catalog.entity.ProductDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Product Discount Repository
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, UUID> {
    
    List<ProductDiscount> findByProductId(UUID productId);
    
    List<ProductDiscount> findByPartnerId(UUID partnerId);
    
    Page<ProductDiscount> findByStatus(DiscountStatus status, Pageable pageable);
    
    // Find active discount for a product
    @Query("""
        SELECT pd FROM ProductDiscount pd 
        WHERE pd.product.id = :productId 
        AND pd.status = 'APPROVED'
        AND (pd.startDate IS NULL OR pd.startDate <= :now)
        AND (pd.endDate IS NULL OR pd.endDate > :now)
        ORDER BY pd.createdAt DESC
        """)
    Optional<ProductDiscount> findActiveDiscountForProduct(
            @Param("productId") UUID productId, 
            @Param("now") ZonedDateTime now);
    
    // Find all pending discounts for admin review
    Page<ProductDiscount> findByStatusOrderByCreatedAtDesc(DiscountStatus status, Pageable pageable);
    
    // Find discounts by partner and status
    List<ProductDiscount> findByPartnerIdAndStatus(UUID partnerId, DiscountStatus status);
    
    // Check for existing active discount
    boolean existsByProductIdAndStatus(UUID productId, DiscountStatus status);
}
