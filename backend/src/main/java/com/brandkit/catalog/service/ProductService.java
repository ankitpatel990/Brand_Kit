package com.brandkit.catalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.dto.*;
import com.brandkit.catalog.dto.admin.*;
import com.brandkit.catalog.entity.*;
import com.brandkit.catalog.exception.CatalogException;
import com.brandkit.catalog.repository.*;
import com.brandkit.catalog.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Product Service
 * FRD-002: Product Catalog Management
 */
@Service
@Transactional(readOnly = true)
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository imageRepository;
    @Autowired
    private PricingTierRepository pricingTierRepository;
    @Autowired
    private ProductDiscountRepository discountRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 50;
    private static final BigDecimal CUSTOMIZATION_FEE_BASE = new BigDecimal("10.00");
    private static final BigDecimal CUSTOMIZATION_FEE_EMBROIDERY = new BigDecimal("25.00");
    private static final BigDecimal CUSTOMIZATION_FEE_ENGRAVING = new BigDecimal("50.00");

    /**
     * Get paginated product list with filters
     * FRD-002 Sub-Prompt 2: Product Listing API with Filters
     */
    public ProductListResponse getProducts(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> materials,
            Boolean ecoFriendly,
            List<String> customizationTypes,
            BigDecimal minRating,
            Boolean hasDiscount,
            String leadTime,
            String sort,
            int page,
            int limit
    ) {
        // Validate and normalize pagination
        int pageSize = Math.min(limit > 0 ? limit : DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
        int pageNumber = Math.max(page - 1, 0);

        // Build sort
        Sort sortOrder = buildSort(sort);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrder);

        // Build specification for filtering
        Specification<Product> spec = Specification.where(ProductSpecification.isActive());

        if (category != null && !category.isBlank()) {
            try {
                ProductCategory cat = ProductCategory.valueOf(category.toUpperCase().replace("-", "_"));
                spec = spec.and(ProductSpecification.hasCategory(cat));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category: {}", category);
            }
        }

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(ProductSpecification.priceRange(minPrice, maxPrice));
        }

        if (materials != null && !materials.isEmpty()) {
            spec = spec.and(ProductSpecification.hasMaterials(materials));
        }

        if (Boolean.TRUE.equals(ecoFriendly)) {
            spec = spec.and(ProductSpecification.isEcoFriendly());
        }

        if (customizationTypes != null && !customizationTypes.isEmpty()) {
            spec = spec.and(ProductSpecification.hasCustomizationTypes(customizationTypes));
        }

        if (minRating != null) {
            spec = spec.and(ProductSpecification.hasMinimumRating(minRating));
        }

        if (Boolean.TRUE.equals(hasDiscount)) {
            spec = spec.and(ProductSpecification.hasActiveDiscount());
        }

        if (leadTime != null && !leadTime.isBlank()) {
            spec = spec.and(ProductSpecification.hasLeadTime(leadTime));
        }

        // Execute query
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // Map to response
        List<ProductListResponse.ProductSummary> products = productPage.getContent().stream()
                .map(this::mapToProductSummary)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .status("success")
                .data(ProductListResponse.ProductListData.builder()
                        .products(products)
                        .pagination(ProductListResponse.PaginationInfo.builder()
                                .currentPage(page)
                                .totalPages(productPage.getTotalPages())
                                .totalProducts(productPage.getTotalElements())
                                .perPage(pageSize)
                                .hasNext(productPage.hasNext())
                                .hasPrevious(productPage.hasPrevious())
                                .build())
                        .appliedFilters(ProductListResponse.AppliedFilters.builder()
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .materials(materials)
                                .ecoFriendly(ecoFriendly)
                                .customizationTypes(customizationTypes)
                                .minRating(minRating)
                                .category(category)
                                .leadTime(leadTime)
                                .hasDiscount(hasDiscount)
                                .build())
                        .build())
                .build();
    }

    /**
     * Search products with full-text search
     * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
     */
    public ProductSearchResponse searchProducts(String query, int page, int limit) {
        if (query == null || query.length() < 2) {
            throw new CatalogException("CAT_003", "Minimum 2 characters required for search");
        }

        int pageSize = Math.min(limit > 0 ? limit : DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
        int pageNumber = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Sanitize query
        String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9\\s]", " ").trim();

        Page<Product> productPage = productRepository.searchProducts(sanitizedQuery, pageable);

        List<ProductListResponse.ProductSummary> products = productPage.getContent().stream()
                .map(this::mapToProductSummary)
                .collect(Collectors.toList());

        // Get suggestions if no results
        List<String> suggestions = new ArrayList<>();
        List<ProductSearchResponse.CategorySuggestion> categorySuggestions = new ArrayList<>();

        if (products.isEmpty()) {
            suggestions = productRepository.findProductNameSuggestions(sanitizedQuery.split("\\s+")[0]);
            categorySuggestions = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                    .map(c -> ProductSearchResponse.CategorySuggestion.builder()
                            .name(c.getName())
                            .slug(c.getSlug())
                            .productCount(c.getProductCount())
                            .build())
                    .collect(Collectors.toList());
        }

        return ProductSearchResponse.builder()
                .status("success")
                .data(ProductSearchResponse.SearchData.builder()
                        .query(query)
                        .products(products)
                        .pagination(ProductListResponse.PaginationInfo.builder()
                                .currentPage(page)
                                .totalPages(productPage.getTotalPages())
                                .totalProducts(productPage.getTotalElements())
                                .perPage(pageSize)
                                .hasNext(productPage.hasNext())
                                .hasPrevious(productPage.hasPrevious())
                                .build())
                        .suggestions(suggestions)
                        .categorySuggestions(categorySuggestions)
                        .build())
                .build();
    }

    /**
     * Get autocomplete suggestions
     * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
     */
    public AutocompleteResponse getAutocomplete(String query) {
        if (query == null || query.length() < 2) {
            return AutocompleteResponse.builder()
                    .status("success")
                    .data(AutocompleteResponse.AutocompleteData.builder()
                            .query(query)
                            .products(Collections.emptyList())
                            .categories(Collections.emptyList())
                            .build())
                    .build();
        }

        String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9\\s]", " ").trim();

        // Get product suggestions (top 5)
        Page<Product> products = productRepository.searchProducts(
                sanitizedQuery, 
                PageRequest.of(0, 5)
        );

        List<AutocompleteResponse.ProductSuggestion> productSuggestions = products.getContent().stream()
                .map(p -> AutocompleteResponse.ProductSuggestion.builder()
                        .productId(p.getId())
                        .name(p.getName())
                        .slug(p.getSlug())
                        .category(p.getCategory().getDisplayName())
                        .basePrice(p.getBasePrice())
                        .imageUrl(p.getPrimaryImage() != null ? p.getPrimaryImage().getImageUrl() : null)
                        .build())
                .collect(Collectors.toList());

        // Get matching categories
        List<AutocompleteResponse.CategorySuggestion> categorySuggestions = categoryRepository
                .findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .filter(c -> c.getName().toLowerCase().contains(sanitizedQuery.toLowerCase()))
                .limit(3)
                .map(c -> AutocompleteResponse.CategorySuggestion.builder()
                        .name(c.getName())
                        .slug(c.getSlug())
                        .productCount(c.getProductCount())
                        .build())
                .collect(Collectors.toList());

        return AutocompleteResponse.builder()
                .status("success")
                .data(AutocompleteResponse.AutocompleteData.builder()
                        .query(query)
                        .products(productSuggestions)
                        .categories(categorySuggestions)
                        .build())
                .build();
    }

    /**
     * Get product details by ID or slug
     * FRD-002 Sub-Prompt 4: Product Detail API
     * NOTE: Partner details are NOT exposed
     */
    public ProductDetailResponse getProductDetail(String productIdOrSlug) {
        Product product = findProductByIdOrSlug(productIdOrSlug);

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new CatalogException("CAT_014", "This product is no longer available");
        }

        return mapToProductDetailResponse(product);
    }

    /**
     * Calculate price for given quantity
     * FRD-002 Sub-Prompt 6: Dynamic Price Calculator
     */
    public PriceCalculationResponse calculatePrice(String productIdOrSlug, PriceCalculationRequest request) {
        Product product = findProductByIdOrSlug(productIdOrSlug);

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new CatalogException("CAT_014", "This product is no longer available");
        }

        int quantity = request.getQuantity();

        // Find applicable tier
        PricingTier applicableTier = pricingTierRepository.findApplicableTier(product.getId(), quantity)
                .orElseThrow(() -> new CatalogException("CAT_012", "No pricing tier available for this quantity"));

        BigDecimal unitPrice = applicableTier.getUnitPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // Calculate customization fee
        BigDecimal customizationFee = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getCustomization()) && product.getCustomizationAvailable()) {
            customizationFee = getCustomizationFee(product.getCustomizationType())
                    .multiply(BigDecimal.valueOf(quantity));
        }

        // Get active discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountName = null;
        boolean hasDiscount = false;

        ProductDiscount activeDiscount = discountRepository
                .findActiveDiscountForProduct(product.getId(), ZonedDateTime.now())
                .orElse(null);

        if (activeDiscount != null) {
            hasDiscount = true;
            discountName = activeDiscount.getDiscountName();
            discountAmount = subtotal.multiply(activeDiscount.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalPrice = subtotal.add(customizationFee).subtract(discountAmount);

        // Calculate savings compared to base tier
        PricingTier baseTier = product.getPricingTiers().get(0);
        BigDecimal baseTotalIfOneByOne = baseTier.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal savingsAmount = baseTotalIfOneByOne.subtract(subtotal);
        BigDecimal savingsPercentage = savingsAmount.divide(baseTotalIfOneByOne, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        return PriceCalculationResponse.builder()
                .status("success")
                .data(PriceCalculationResponse.PriceCalculationData.builder()
                        .quantity(quantity)
                        .applicableTier(PriceCalculationResponse.ApplicableTier.builder()
                                .tierNumber(applicableTier.getTierNumber())
                                .minQuantity(applicableTier.getMinQuantity())
                                .maxQuantity(applicableTier.getMaxQuantity())
                                .unitPrice(applicableTier.getUnitPrice())
                                .build())
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .customizationFee(customizationFee)
                        .discountAmount(discountAmount)
                        .totalPrice(totalPrice)
                        .savings(PriceCalculationResponse.Savings.builder()
                                .amount(savingsAmount)
                                .percentage(savingsPercentage)
                                .description(savingsPercentage.compareTo(BigDecimal.ZERO) > 0 
                                        ? "Save ₹" + savingsAmount + " (" + savingsPercentage + "%)"
                                        : null)
                                .build())
                        .hasDiscount(hasDiscount)
                        .discountName(discountName)
                        .build())
                .build();
    }

    /**
     * Get all categories
     * FRD-002 FR-14: Category Structure
     */
    public CategoryResponse getCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();

        List<CategoryResponse.CategoryData> categoryData = categories.stream()
                .map(c -> CategoryResponse.CategoryData.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .description(c.getDescription())
                        .imageUrl(c.getImageUrl())
                        .displayOrder(c.getDisplayOrder())
                        .productCount(c.getProductCount())
                        .build())
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .status("success")
                .data(categoryData)
                .build();
    }

    // ==================== Helper Methods ====================

    private Product findProductByIdOrSlug(String productIdOrSlug) {
        // Try UUID first
        try {
            UUID id = UUID.fromString(productIdOrSlug);
            return productRepository.findById(id)
                    .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));
        } catch (IllegalArgumentException e) {
            // Not a UUID, try slug
            return productRepository.findBySlug(productIdOrSlug)
                    .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));
        }
    }

    private ProductListResponse.ProductSummary mapToProductSummary(Product product) {
        ProductImage primaryImage = product.getPrimaryImage();
        ProductDiscount activeDiscount = product.getActiveDiscount();

        BigDecimal discountedPrice = product.getBasePrice();
        BigDecimal discountPercentage = null;
        boolean hasDiscount = false;

        if (activeDiscount != null) {
            hasDiscount = true;
            discountPercentage = activeDiscount.getDiscountPercentage();
            discountedPrice = product.getBasePrice().multiply(
                    BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
            ).setScale(2, RoundingMode.HALF_UP);
        }

        return ProductListResponse.ProductSummary.builder()
                .productId(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .category(product.getCategory().getDisplayName())
                .shortDescription(product.getShortDescription())
                .basePrice(product.getBasePrice())
                .discountedPrice(discountedPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .imageUrl(primaryImage != null ? primaryImage.getImageUrl() : null)
                .ecoFriendly(product.getEcoFriendly())
                .customizable(product.getCustomizationAvailable())
                .customizationType(product.getCustomizationType().getDisplayName())
                .aggregateRating(product.getAggregateRating())
                .totalReviews(product.getTotalReviews())
                .leadTimeDays(product.getLeadTimeDays())
                .availability(product.getAvailability().name())
                .build();
    }

    private ProductDetailResponse mapToProductDetailResponse(Product product) {
        ProductDiscount activeDiscount = product.getActiveDiscount();

        BigDecimal discountedPrice = product.getBasePrice();
        BigDecimal discountPercentage = null;
        boolean hasDiscount = false;
        String discountName = null;

        if (activeDiscount != null) {
            hasDiscount = true;
            discountPercentage = activeDiscount.getDiscountPercentage();
            discountName = activeDiscount.getDiscountName();
            discountedPrice = product.getBasePrice().multiply(
                    BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
            ).setScale(2, RoundingMode.HALF_UP);
        }

        List<ProductDetailResponse.ProductImageDto> images = product.getImages().stream()
                .map(img -> ProductDetailResponse.ProductImageDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .thumbnailUrl(img.getThumbnailUrl())
                        .mediumUrl(img.getMediumUrl())
                        .altText(img.getAltText())
                        .displayOrder(img.getDisplayOrder())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .collect(Collectors.toList());

        List<ProductDetailResponse.PricingTierDto> pricingTiers = product.getPricingTiers().stream()
                .map(tier -> ProductDetailResponse.PricingTierDto.builder()
                        .tierNumber(tier.getTierNumber())
                        .minQuantity(tier.getMinQuantity())
                        .maxQuantity(tier.getMaxQuantity())
                        .unitPrice(tier.getUnitPrice())
                        .discountPercentage(tier.getDiscountPercentage())
                        .description(formatTierDescription(tier))
                        .build())
                .collect(Collectors.toList());

        return ProductDetailResponse.builder()
                .status("success")
                .data(ProductDetailResponse.ProductDetailData.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .category(product.getCategory().getDisplayName())
                        .categorySlug(product.getCategory().getSlug())
                        .shortDescription(product.getShortDescription())
                        .longDescription(product.getLongDescription())
                        .basePrice(product.getBasePrice())
                        .discountedPrice(discountedPrice)
                        .discountPercentage(discountPercentage)
                        .hasDiscount(hasDiscount)
                        .discountName(discountName)
                        .material(product.getMaterial())
                        .ecoFriendly(product.getEcoFriendly())
                        .customizable(product.getCustomizationAvailable())
                        .customizationType(product.getCustomizationType().getDisplayName())
                        .printArea(product.getCustomizationAvailable() ? ProductDetailResponse.PrintArea.builder()
                                .width(product.getPrintAreaWidth())
                                .height(product.getPrintAreaHeight())
                                .unit("cm")
                                .build() : null)
                        .images(images)
                        .pricingTiers(pricingTiers)
                        .specifications(ProductDetailResponse.Specifications.builder()
                                .material(product.getMaterial())
                                .weightGrams(product.getWeightGrams())
                                .dimensions(product.getDimensions())
                                .availableColors(product.getAvailableColors() != null 
                                        ? Arrays.asList(product.getAvailableColors()) : null)
                                .leadTimeDays(product.getLeadTimeDays())
                                .build())
                        .aggregateRating(product.getAggregateRating())
                        .totalReviews(product.getTotalReviews())
                        .leadTimeDays(product.getLeadTimeDays())
                        .availability(product.getAvailability().name())
                        .tags(product.getTags() != null ? Arrays.asList(product.getTags()) : null)
                        .seo(ProductDetailResponse.SeoInfo.builder()
                                .metaTitle(product.getMetaTitle() != null ? product.getMetaTitle() 
                                        : product.getName() + " | BrandKit")
                                .metaDescription(product.getMetaDescription() != null ? product.getMetaDescription()
                                        : product.getShortDescription().substring(0, Math.min(150, product.getShortDescription().length())))
                                .canonicalUrl("/products/" + product.getCategory().getSlug() + "/" + product.getSlug())
                                .build())
                        .createdAt(product.getCreatedAt())
                        .build())
                .build();
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "totalOrders"); // Default: popular
        }

        return switch (sort.toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "basePrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "basePrice");
            case "rating" -> Sort.by(Sort.Direction.DESC, "aggregateRating");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "popular" -> Sort.by(Sort.Direction.DESC, "totalOrders");
            default -> Sort.by(Sort.Direction.DESC, "totalOrders");
        };
    }

    private BigDecimal getCustomizationFee(CustomizationType type) {
        return switch (type) {
            case LOGO_PRINT -> CUSTOMIZATION_FEE_BASE;
            case EMBROIDERY -> CUSTOMIZATION_FEE_EMBROIDERY;
            case ENGRAVING -> CUSTOMIZATION_FEE_ENGRAVING;
            case NONE -> BigDecimal.ZERO;
        };
    }

    private String formatTierDescription(PricingTier tier) {
        if (tier.getMaxQuantity() == null) {
            return tier.getMinQuantity() + "+ units";
        }
        return tier.getMinQuantity() + "-" + tier.getMaxQuantity() + " units";
    }
}
