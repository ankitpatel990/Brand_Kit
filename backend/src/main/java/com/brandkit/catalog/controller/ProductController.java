package com.brandkit.catalog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.dto.*;
import com.brandkit.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Controller - Public API (No partner info exposed)
 * FRD-002: Product Catalog Management
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Get products with filters
     * FRD-002 Sub-Prompt 2: Product Listing API with Filters
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> material,
            @RequestParam(required = false) Boolean ecoFriendly,
            @RequestParam(required = false) List<String> customizationType,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean hasDiscount,
            @RequestParam(required = false) String leadTime,
            @RequestParam(required = false, defaultValue = "popular") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        log.debug("GET /api/products - category: {}, page: {}", category, page);
        
        ProductListResponse response = productService.getProducts(
                category, minPrice, maxPrice, material, ecoFriendly,
                customizationType, minRating, hasDiscount, leadTime,
                sort, page, limit
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search products
     * FRD-002 Sub-Prompt 3: Product Search
     * GET /api/products/search?q={query}
     */
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponse> searchProducts(
            @RequestParam("q") String query,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        log.debug("GET /api/products/search - query: {}", query);
        
        ProductSearchResponse response = productService.searchProducts(query, page, limit);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Autocomplete suggestions
     * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
     * GET /api/products/autocomplete?q={query}
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponse> getAutocomplete(
            @RequestParam("q") String query
    ) {
        log.debug("GET /api/products/autocomplete - query: {}", query);
        
        AutocompleteResponse response = productService.getAutocomplete(query);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get product details
     * FRD-002 Sub-Prompt 4: Product Detail API
     * GET /api/products/:productId
     * NOTE: Partner details are NOT exposed
     */
    @GetMapping("/{productIdOrSlug}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @PathVariable String productIdOrSlug
    ) {
        log.debug("GET /api/products/{}", productIdOrSlug);
        
        ProductDetailResponse response = productService.getProductDetail(productIdOrSlug);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculate price for quantity
     * FRD-002 Sub-Prompt 6: Dynamic Price Calculator
     * POST /api/products/:productId/calculate-price
     */
    @PostMapping("/{productIdOrSlug}/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @PathVariable String productIdOrSlug,
            @Valid @RequestBody PriceCalculationRequest request
    ) {
        log.debug("POST /api/products/{}/calculate-price - quantity: {}", productIdOrSlug, request.getQuantity());
        
        PriceCalculationResponse response = productService.calculatePrice(productIdOrSlug, request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories
     * FRD-002 FR-14: Category Structure
     * GET /api/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<CategoryResponse> getCategories() {
        log.debug("GET /api/products/categories");
        
        CategoryResponse response = productService.getCategories();
        
        return ResponseEntity.ok(response);
    }
}
