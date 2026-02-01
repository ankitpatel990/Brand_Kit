package com.brandkit.catalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.catalog.dto.ProductDetailResponse;
import com.brandkit.catalog.dto.ProductListResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Admin Product Service
 * FRD-002 Sub-Prompt 5: Admin Product Management
 */
@Service
@Transactional
public class AdminProductService {
    private static final Logger log = LoggerFactory.getLogger(AdminProductService.class);

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

    /**
     * Create a new product
     */
    public AdminProductResponse createProduct(CreateProductRequest request, User admin) {
        log.info("Admin {} creating new product: {}", admin.getEmail(), request.getName());

        // Validate partner exists
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new CatalogException("CAT_013", "Selected partner does not exist"));

        // Validate customization fields
        if (Boolean.TRUE.equals(request.getCustomizationAvailable())) {
            if (request.getCustomizationType() == null || request.getCustomizationType() == CustomizationType.NONE) {
                throw new CatalogException("CAT_011", "Customization type is required when customization is enabled");
            }
            if (request.getPrintAreaWidth() == null || request.getPrintAreaHeight() == null) {
                throw new CatalogException("CAT_011", "Print area dimensions are required when customization is enabled");
            }
        }

        // Validate pricing tiers
        validatePricingTiers(request.getPricingTiers());

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .shortDescription(request.getShortDescription())
                .longDescription(request.getLongDescription())
                .basePrice(request.getBasePrice())
                .material(request.getMaterial())
                .ecoFriendly(request.getEcoFriendly())
                .customizationAvailable(request.getCustomizationAvailable())
                .customizationType(request.getCustomizationType())
                .printAreaWidth(request.getPrintAreaWidth())
                .printAreaHeight(request.getPrintAreaHeight())
                .weightGrams(request.getWeightGrams())
                .dimensions(request.getDimensions())
                .availableColors(request.getAvailableColors() != null 
                        ? request.getAvailableColors().toArray(new String[0]) : null)
                .partner(partner)
                .leadTimeDays(request.getLeadTimeDays())
                .status(request.getStatus())
                .availability(request.getAvailability())
                .tags(request.getTags() != null ? request.getTags().toArray(new String[0]) : null)
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .build();

        product = productRepository.save(product);

        // Create pricing tiers
        for (CreateProductRequest.PricingTierRequest tierRequest : request.getPricingTiers()) {
            PricingTier tier = PricingTier.builder()
                    .product(product)
                    .tierNumber(tierRequest.getTierNumber())
                    .minQuantity(tierRequest.getMinQuantity())
                    .maxQuantity(tierRequest.getMaxQuantity())
                    .unitPrice(tierRequest.getUnitPrice())
                    .discountPercentage(tierRequest.getDiscountPercentage())
                    .build();
            pricingTierRepository.save(tier);
            product.getPricingTiers().add(tier);
        }

        log.info("Product created successfully: {} (ID: {})", product.getName(), product.getId());

        return mapToAdminProductResponse(product);
    }

    /**
     * Update an existing product
     */
    public AdminProductResponse updateProduct(UUID productId, UpdateProductRequest request, User admin) {
        log.info("Admin {} updating product: {}", admin.getEmail(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getLongDescription() != null) {
            product.setLongDescription(request.getLongDescription());
        }
        if (request.getBasePrice() != null) {
            product.setBasePrice(request.getBasePrice());
        }
        if (request.getMaterial() != null) {
            product.setMaterial(request.getMaterial());
        }
        if (request.getEcoFriendly() != null) {
            product.setEcoFriendly(request.getEcoFriendly());
        }
        if (request.getCustomizationAvailable() != null) {
            product.setCustomizationAvailable(request.getCustomizationAvailable());
        }
        if (request.getCustomizationType() != null) {
            product.setCustomizationType(request.getCustomizationType());
        }
        if (request.getPrintAreaWidth() != null) {
            product.setPrintAreaWidth(request.getPrintAreaWidth());
        }
        if (request.getPrintAreaHeight() != null) {
            product.setPrintAreaHeight(request.getPrintAreaHeight());
        }
        if (request.getWeightGrams() != null) {
            product.setWeightGrams(request.getWeightGrams());
        }
        if (request.getDimensions() != null) {
            product.setDimensions(request.getDimensions());
        }
        if (request.getAvailableColors() != null) {
            product.setAvailableColors(request.getAvailableColors().toArray(new String[0]));
        }
        if (request.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new CatalogException("CAT_013", "Selected partner does not exist"));
            product.setPartner(partner);
        }
        if (request.getLeadTimeDays() != null) {
            product.setLeadTimeDays(request.getLeadTimeDays());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getAvailability() != null) {
            product.setAvailability(request.getAvailability());
        }
        if (request.getTags() != null) {
            product.setTags(request.getTags().toArray(new String[0]));
        }
        if (request.getMetaTitle() != null) {
            product.setMetaTitle(request.getMetaTitle());
        }
        if (request.getMetaDescription() != null) {
            product.setMetaDescription(request.getMetaDescription());
        }

        // Update pricing tiers if provided
        if (request.getPricingTiers() != null && !request.getPricingTiers().isEmpty()) {
            validatePricingTiers(request.getPricingTiers());
            
            // Delete existing tiers and add new ones
            pricingTierRepository.deleteByProductId(productId);
            product.getPricingTiers().clear();
            
            for (CreateProductRequest.PricingTierRequest tierRequest : request.getPricingTiers()) {
                PricingTier tier = PricingTier.builder()
                        .product(product)
                        .tierNumber(tierRequest.getTierNumber())
                        .minQuantity(tierRequest.getMinQuantity())
                        .maxQuantity(tierRequest.getMaxQuantity())
                        .unitPrice(tierRequest.getUnitPrice())
                        .discountPercentage(tierRequest.getDiscountPercentage())
                        .build();
                pricingTierRepository.save(tier);
                product.getPricingTiers().add(tier);
            }
        }

        product = productRepository.save(product);
        log.info("Product updated successfully: {} (ID: {})", product.getName(), product.getId());

        return mapToAdminProductResponse(product);
    }

    /**
     * Soft delete a product
     */
    public void deleteProduct(UUID productId, User admin) {
        log.info("Admin {} deleting product: {}", admin.getEmail(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));

        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);

        log.info("Product soft-deleted: {} (ID: {})", product.getName(), productId);
    }

    /**
     * Get product by ID for admin (includes partner info)
     */
    @Transactional(readOnly = true)
    public AdminProductResponse getProductForAdmin(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));

        return mapToAdminProductResponse(product);
    }

    /**
     * List all products for admin (includes partner info)
     */
    @Transactional(readOnly = true)
    public ProductListResponse listProductsForAdmin(
            String category,
            String status,
            UUID partnerId,
            String search,
            int page,
            int limit
    ) {
        int pageSize = Math.min(limit > 0 ? limit : 20, 100);
        int pageNumber = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Product> spec = Specification.where(null);

        if (category != null && !category.isBlank()) {
            try {
                ProductCategory cat = ProductCategory.valueOf(category.toUpperCase().replace("-", "_"));
                spec = spec.and(ProductSpecification.hasCategory(cat));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category: {}", category);
            }
        }

        if (status != null && !status.isBlank()) {
            try {
                ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), productStatus));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", status);
            }
        }

        if (partnerId != null) {
            spec = spec.and(ProductSpecification.hasPartner(partnerId));
        }

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProductSpecification.searchByText(search));
        }

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductListResponse.ProductSummary> products = productPage.getContent().stream()
                .map(this::mapToProductSummaryForAdmin)
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
                        .build())
                .build();
    }

    // ==================== Helper Methods ====================

    private void validatePricingTiers(List<CreateProductRequest.PricingTierRequest> tiers) {
        if (tiers == null || tiers.size() < 3) {
            throw new CatalogException("CAT_012", "At least 3 pricing tiers are required");
        }

        // Sort by tier number
        List<CreateProductRequest.PricingTierRequest> sortedTiers = tiers.stream()
                .sorted((a, b) -> a.getTierNumber().compareTo(b.getTierNumber()))
                .collect(java.util.stream.Collectors.toList());

        // Validate first tier starts at 1
        if (sortedTiers.get(0).getMinQuantity() != 1) {
            throw new CatalogException("CAT_012", "First tier must start at quantity 1");
        }

        // Validate no overlaps and sequential
        for (int i = 0; i < sortedTiers.size() - 1; i++) {
            CreateProductRequest.PricingTierRequest current = sortedTiers.get(i);
            CreateProductRequest.PricingTierRequest next = sortedTiers.get(i + 1);

            if (current.getMaxQuantity() == null) {
                throw new CatalogException("CAT_012", "Only the last tier can have unlimited max quantity");
            }

            if (current.getMaxQuantity() + 1 != next.getMinQuantity()) {
                throw new CatalogException("CAT_012", "Pricing tiers must be sequential with no gaps or overlaps");
            }

            // Unit price should decrease or stay same
            if (current.getUnitPrice().compareTo(next.getUnitPrice()) < 0) {
                throw new CatalogException("CAT_012", "Unit price cannot increase in higher tiers");
            }
        }
    }

    private AdminProductResponse mapToAdminProductResponse(Product product) {
        Partner partner = product.getPartner();

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
                        .build())
                .collect(Collectors.toList());

        List<AdminProductResponse.DiscountInfo> discounts = product.getDiscounts().stream()
                .map(d -> AdminProductResponse.DiscountInfo.builder()
                        .discountId(d.getId())
                        .discountPercentage(d.getDiscountPercentage())
                        .discountName(d.getDiscountName())
                        .status(d.getStatus().name())
                        .startDate(d.getStartDate())
                        .endDate(d.getEndDate())
                        .createdAt(d.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return AdminProductResponse.builder()
                .status("success")
                .data(AdminProductResponse.AdminProductData.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .category(product.getCategory().getDisplayName())
                        .shortDescription(product.getShortDescription())
                        .longDescription(product.getLongDescription())
                        .basePrice(product.getBasePrice())
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
                        .totalOrders(product.getTotalOrders())
                        .leadTimeDays(product.getLeadTimeDays())
                        .status(product.getStatus().name())
                        .availability(product.getAvailability().name())
                        .tags(product.getTags() != null ? Arrays.asList(product.getTags()) : null)
                        .seo(ProductDetailResponse.SeoInfo.builder()
                                .metaTitle(product.getMetaTitle())
                                .metaDescription(product.getMetaDescription())
                                .canonicalUrl("/products/" + product.getCategory().getSlug() + "/" + product.getSlug())
                                .build())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .partner(partner != null ? AdminProductResponse.PartnerInfo.builder()
                                .partnerId(partner.getId())
                                .businessName(partner.getBusinessName())
                                .email(partner.getEmail())
                                .phone(partner.getPhone())
                                .location(partner.getLocation())
                                .city(partner.getCity())
                                .commissionRate(partner.getCommissionRate())
                                .fulfillmentSlaDays(partner.getFulfillmentSlaDays())
                                .build() : null)
                        .discounts(discounts)
                        .build())
                .build();
    }

    private ProductListResponse.ProductSummary mapToProductSummaryForAdmin(Product product) {
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
}
