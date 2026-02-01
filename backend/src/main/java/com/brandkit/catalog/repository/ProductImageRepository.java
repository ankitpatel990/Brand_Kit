package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Product Image Repository
 * FRD-002 Sub-Prompt 8: Product Image Management
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(UUID productId);
    
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(UUID productId);
    
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId AND pi.id != :imageId")
    void clearPrimaryExcept(@Param("productId") UUID productId, @Param("imageId") UUID imageId);
    
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
    
    long countByProductId(UUID productId);
    
    @Query("SELECT MAX(pi.displayOrder) FROM ProductImage pi WHERE pi.product.id = :productId")
    Integer findMaxDisplayOrder(@Param("productId") UUID productId);
}
