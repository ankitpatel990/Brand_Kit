package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductCategory;
import com.brandkit.catalog.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Product Repository
 * FRD-002 Sub-Prompt 2: Product Listing API
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    
    // Find by slug (for SEO-friendly URLs)
    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);
    
    Optional<Product> findBySlug(String slug);
    
    // Find by ID and status
    Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);
    
    // Find active products by category
    Page<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable);
    
    // Count products by category
    long countByCategoryAndStatus(ProductCategory category, ProductStatus status);
    
    // Search products with full-text search
    @Query(value = """
        SELECT p.* FROM products p 
        WHERE p.status = 'ACTIVE'
        AND (
            to_tsvector('english', coalesce(p.name, '') || ' ' || coalesce(p.short_description, '') || ' ' || coalesce(array_to_string(p.tags, ' '), ''))
            @@ plainto_tsquery('english', :query)
            OR p.name ILIKE '%' || :query || '%'
            OR p.short_description ILIKE '%' || :query || '%'
        )
        ORDER BY 
            CASE WHEN p.name ILIKE :query THEN 1
                 WHEN p.name ILIKE :query || '%' THEN 2
                 WHEN p.name ILIKE '%' || :query || '%' THEN 3
                 ELSE 4 
            END,
            p.aggregate_rating DESC
        """, 
        countQuery = """
            SELECT COUNT(*) FROM products p 
            WHERE p.status = 'ACTIVE'
            AND (
                to_tsvector('english', coalesce(p.name, '') || ' ' || coalesce(p.short_description, '') || ' ' || coalesce(array_to_string(p.tags, ' '), ''))
                @@ plainto_tsquery('english', :query)
                OR p.name ILIKE '%' || :query || '%'
                OR p.short_description ILIKE '%' || :query || '%'
            )
        """,
        nativeQuery = true)
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    // Autocomplete suggestions
    @Query(value = """
        SELECT DISTINCT p.name FROM products p 
        WHERE p.status = 'ACTIVE'
        AND (p.name ILIKE :query || '%' OR p.name ILIKE '% ' || :query || '%')
        ORDER BY p.name
        LIMIT 5
        """, nativeQuery = true)
    List<String> findProductNameSuggestions(@Param("query") String query);
    
    // Find products with active discounts
    @Query("SELECT p FROM Product p JOIN p.discounts d WHERE p.status = 'ACTIVE' AND d.status = 'APPROVED'")
    Page<Product> findProductsWithActiveDiscounts(Pageable pageable);
    
    // Find eco-friendly products
    Page<Product> findByEcoFriendlyTrueAndStatus(ProductStatus status, Pageable pageable);
    
    // Find customizable products
    Page<Product> findByCustomizationAvailableTrueAndStatus(ProductStatus status, Pageable pageable);
    
    // Get popular products
    Page<Product> findByStatusOrderByTotalOrdersDesc(ProductStatus status, Pageable pageable);
    
    // Get newest products
    Page<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status, Pageable pageable);
    
    // Find products by partner (admin only)
    Page<Product> findByPartnerId(UUID partnerId, Pageable pageable);
    
    // Price range query
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.basePrice BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByStatusAndPriceRange(
            @Param("status") ProductStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    // Check if slug exists
    boolean existsBySlug(String slug);
}
