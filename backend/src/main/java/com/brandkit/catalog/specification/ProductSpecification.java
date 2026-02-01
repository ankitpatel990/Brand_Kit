package com.brandkit.catalog.specification;

import com.brandkit.catalog.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Product Specification for dynamic query building
 * FRD-002 FR-19: Filtering System
 */
public class ProductSpecification {

    private ProductSpecification() {
        // Utility class
    }

    /**
     * Filter active products only
     */
    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), ProductStatus.ACTIVE);
    }

    /**
     * Filter by category
     */
    public static Specification<Product> hasCategory(ProductCategory category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    /**
     * Filter by price range
     */
    public static Specification<Product> priceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("basePrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
            }
            return cb.conjunction();
        };
    }

    /**
     * Filter by materials (OR logic within category)
     */
    public static Specification<Product> hasMaterials(List<String> materials) {
        return (root, query, cb) -> {
            if (materials == null || materials.isEmpty()) {
                return cb.conjunction();
            }
            
            List<Predicate> predicates = materials.stream()
                    .map(material -> cb.like(cb.lower(root.get("material")), "%" + material.toLowerCase() + "%"))
                    .collect(java.util.stream.Collectors.toList());
            
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter eco-friendly products
     */
    public static Specification<Product> isEcoFriendly() {
        return (root, query, cb) -> cb.isTrue(root.get("ecoFriendly"));
    }

    /**
     * Filter by customization types
     */
    public static Specification<Product> hasCustomizationTypes(List<String> types) {
        return (root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return cb.conjunction();
            }
            
            List<CustomizationType> customizationTypes = types.stream()
                    .map(t -> {
                        try {
                            return CustomizationType.valueOf(t.toUpperCase().replace(" ", "_"));
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(t -> t != null)
                    .collect(java.util.stream.Collectors.toList());
            
            if (customizationTypes.isEmpty()) {
                return cb.conjunction();
            }
            
            Predicate isCustomizable = cb.isTrue(root.get("customizationAvailable"));
            Predicate hasType = root.get("customizationType").in(customizationTypes);
            
            return cb.and(isCustomizable, hasType);
        };
    }

    /**
     * Filter by minimum rating
     */
    public static Specification<Product> hasMinimumRating(BigDecimal minRating) {
        return (root, query, cb) -> {
            if (minRating == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("aggregateRating"), minRating);
        };
    }

    /**
     * Filter products with active discounts
     */
    public static Specification<Product> hasActiveDiscount() {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ProductDiscount> discountRoot = subquery.from(ProductDiscount.class);
            
            ZonedDateTime now = ZonedDateTime.now();
            
            subquery.select(cb.count(discountRoot));
            subquery.where(
                    cb.equal(discountRoot.get("product"), root),
                    cb.equal(discountRoot.get("status"), DiscountStatus.APPROVED),
                    cb.or(
                            cb.isNull(discountRoot.get("startDate")),
                            cb.lessThanOrEqualTo(discountRoot.get("startDate"), now)
                    ),
                    cb.or(
                            cb.isNull(discountRoot.get("endDate")),
                            cb.greaterThan(discountRoot.get("endDate"), now)
                    )
            );
            
            return cb.greaterThan(subquery, 0L);
        };
    }

    /**
     * Filter by lead time
     */
    public static Specification<Product> hasLeadTime(String leadTime) {
        return (root, query, cb) -> {
            if (leadTime == null || leadTime.isBlank()) {
                return cb.conjunction();
            }
            
            return switch (leadTime.toLowerCase()) {
                case "<7", "less_than_7", "quick" -> cb.lessThan(root.get("leadTimeDays"), 7);
                case "7-14", "medium" -> cb.and(
                        cb.greaterThanOrEqualTo(root.get("leadTimeDays"), 7),
                        cb.lessThanOrEqualTo(root.get("leadTimeDays"), 14)
                );
                case "14+", "14_plus", "long" -> cb.greaterThan(root.get("leadTimeDays"), 14);
                default -> cb.conjunction();
            };
        };
    }

    /**
     * Filter by availability status
     */
    public static Specification<Product> hasAvailability(AvailabilityStatus availability) {
        return (root, query, cb) -> cb.equal(root.get("availability"), availability);
    }

    /**
     * Filter by partner (admin only)
     */
    public static Specification<Product> hasPartner(java.util.UUID partnerId) {
        return (root, query, cb) -> cb.equal(root.get("partner").get("id"), partnerId);
    }

    /**
     * Search by name or description (simple)
     */
    public static Specification<Product> searchByText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isBlank()) {
                return cb.conjunction();
            }
            
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("shortDescription")), pattern)
            );
        };
    }
}
