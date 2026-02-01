package com.brandkit.catalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserType;
import com.brandkit.catalog.dto.ProductListResponse;
import com.brandkit.catalog.dto.admin.*;
import com.brandkit.catalog.entity.*;
import com.brandkit.catalog.exception.CatalogException;
import com.brandkit.catalog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Discount Service
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
@Service
@Transactional
public class DiscountService {
    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);

    @Autowired
    private ProductDiscountRepository discountRepository;
    @Autowired
    private DiscountLimitRepository limitRepository;
    @Autowired
    private DiscountAuditLogRepository auditLogRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    /**
     * Partner creates a discount proposal
     */
    public DiscountListResponse.DiscountDetail createDiscount(DiscountRequest request, User partner, String ipAddress) {
        log.info("Partner {} creating discount for product {}", partner.getEmail(), request.getProductId());

        // Get product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));

        // Validate partner owns the product
        Partner partnerEntity = partnerRepository.findByEmail(partner.getEmail())
                .orElseThrow(() -> new CatalogException("CAT_013", "Partner not found"));

        if (product.getPartner() == null || !product.getPartner().getId().equals(partnerEntity.getId())) {
            throw new CatalogException("CAT_001", "You can only create discounts for your own products");
        }

        // Check existing active discount
        if (discountRepository.existsByProductIdAndStatus(product.getId(), DiscountStatus.APPROVED)) {
            throw new CatalogException("CAT_012", "Product already has an active discount");
        }

        // Validate against limits
        DiscountLimit limit = limitRepository.findLimitForCategory(product.getCategory())
                .orElse(null);

        if (limit != null) {
            if (request.getDiscountPercentage().compareTo(limit.getMaxDiscountPercentage()) > 0) {
                throw new CatalogException("CAT_012", 
                        "Discount exceeds maximum limit of " + limit.getMaxDiscountPercentage() + "%");
            }
            if (request.getDiscountPercentage().compareTo(limit.getMinDiscountPercentage()) < 0) {
                throw new CatalogException("CAT_012", 
                        "Discount must be at least " + limit.getMinDiscountPercentage() + "%");
            }
        }

        // Create discount
        ProductDiscount discount = ProductDiscount.builder()
                .product(product)
                .partner(partnerEntity)
                .discountPercentage(request.getDiscountPercentage())
                .discountName(request.getDiscountName())
                .status(DiscountStatus.PENDING)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        discount = discountRepository.save(discount);

        // Create audit log
        createAuditLog(discount, "CREATED", partner, ipAddress, null, 
                Map.of("discountPercentage", request.getDiscountPercentage(), "status", "PENDING"), null);

        log.info("Discount created: {} for product {} (pending approval)", discount.getId(), product.getId());

        return mapToDiscountDetail(discount);
    }

    /**
     * Admin approves a discount
     */
    public DiscountListResponse.DiscountDetail approveDiscount(UUID discountId, User admin, String ipAddress) {
        log.info("Admin {} approving discount {}", admin.getEmail(), discountId);

        ProductDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Discount not found"));

        if (discount.getStatus() != DiscountStatus.PENDING) {
            throw new CatalogException("CAT_012", "Only pending discounts can be approved");
        }

        Map<String, Object> oldValue = Map.of("status", discount.getStatus().name());

        discount.setStatus(DiscountStatus.APPROVED);
        discount.setApprovedBy(admin);
        discount.setApprovedAt(ZonedDateTime.now());

        discount = discountRepository.save(discount);

        // Create audit log
        createAuditLog(discount, "APPROVED", admin, ipAddress, oldValue,
                Map.of("status", "APPROVED"), null);

        log.info("Discount {} approved by {}", discountId, admin.getEmail());

        return mapToDiscountDetail(discount);
    }

    /**
     * Admin disables a discount
     */
    public DiscountListResponse.DiscountDetail disableDiscount(UUID discountId, DiscountApprovalRequest request, User admin, String ipAddress) {
        log.info("Admin {} disabling discount {}", admin.getEmail(), discountId);

        ProductDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Discount not found"));

        Map<String, Object> oldValue = Map.of("status", discount.getStatus().name());

        discount.setStatus(DiscountStatus.DISABLED);
        discount.setDisabledBy(admin);
        discount.setDisabledAt(ZonedDateTime.now());
        discount.setDisabledReason(request.getReason());

        discount = discountRepository.save(discount);

        // Create audit log
        createAuditLog(discount, "DISABLED", admin, ipAddress, oldValue,
                Map.of("status", "DISABLED", "reason", request.getReason() != null ? request.getReason() : ""),
                request.getReason());

        log.info("Discount {} disabled by {}", discountId, admin.getEmail());

        return mapToDiscountDetail(discount);
    }

    /**
     * Admin sets global discount limits
     */
    public void setDiscountLimits(DiscountLimitRequest request, User admin) {
        log.info("Admin {} setting discount limits", admin.getEmail());

        if (request.getMinDiscountPercentage().compareTo(request.getMaxDiscountPercentage()) > 0) {
            throw new CatalogException("CAT_012", "Minimum discount cannot exceed maximum discount");
        }

        // Deactivate existing limit for this category
        DiscountLimit existingLimit;
        if (request.getCategory() == null) {
            existingLimit = limitRepository.findGlobalActiveLimit().orElse(null);
        } else {
            existingLimit = limitRepository.findByCategoryAndIsActiveTrue(request.getCategory()).orElse(null);
        }

        if (existingLimit != null) {
            existingLimit.setIsActive(false);
            limitRepository.save(existingLimit);
        }

        // Create new limit
        DiscountLimit newLimit = DiscountLimit.builder()
                .minDiscountPercentage(request.getMinDiscountPercentage())
                .maxDiscountPercentage(request.getMaxDiscountPercentage())
                .category(request.getCategory())
                .setBy(admin)
                .isActive(true)
                .build();

        limitRepository.save(newLimit);

        log.info("Discount limits set: {}% - {}% for category: {}", 
                request.getMinDiscountPercentage(), request.getMaxDiscountPercentage(),
                request.getCategory() != null ? request.getCategory().name() : "ALL");
    }

    /**
     * List all discounts (admin)
     */
    @Transactional(readOnly = true)
    public DiscountListResponse listDiscounts(String status, int page, int limit) {
        int pageSize = Math.min(limit > 0 ? limit : 20, 100);
        int pageNumber = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ProductDiscount> discountPage;
        if (status != null && !status.isBlank()) {
            try {
                DiscountStatus discountStatus = DiscountStatus.valueOf(status.toUpperCase());
                discountPage = discountRepository.findByStatus(discountStatus, pageable);
            } catch (IllegalArgumentException e) {
                discountPage = discountRepository.findAll(pageable);
            }
        } else {
            discountPage = discountRepository.findAll(pageable);
        }

        List<DiscountListResponse.DiscountDetail> discounts = discountPage.getContent().stream()
                .map(this::mapToDiscountDetail)
                .collect(Collectors.toList());

        return DiscountListResponse.builder()
                .status("success")
                .data(DiscountListResponse.DiscountListData.builder()
                        .discounts(discounts)
                        .pagination(ProductListResponse.PaginationInfo.builder()
                                .currentPage(page)
                                .totalPages(discountPage.getTotalPages())
                                .totalProducts(discountPage.getTotalElements())
                                .perPage(pageSize)
                                .hasNext(discountPage.hasNext())
                                .hasPrevious(discountPage.hasPrevious())
                                .build())
                        .build())
                .build();
    }

    /**
     * Get discount audit log
     */
    @Transactional(readOnly = true)
    public DiscountAuditResponse getAuditLog(int page, int limit) {
        int pageSize = Math.min(limit > 0 ? limit : 50, 200);
        int pageNumber = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<DiscountAuditLog> auditPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<DiscountAuditResponse.AuditEntry> auditEntries = auditPage.getContent().stream()
                .map(log -> DiscountAuditResponse.AuditEntry.builder()
                        .id(log.getId())
                        .discountId(log.getDiscount().getId())
                        .action(log.getAction())
                        .performedByName(log.getPerformedBy().getFullName())
                        .performedByRole(log.getPerformedByRole().name())
                        .oldValue(log.getOldValue())
                        .newValue(log.getNewValue())
                        .reason(log.getReason())
                        .ipAddress(log.getIpAddress())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return DiscountAuditResponse.builder()
                .status("success")
                .data(DiscountAuditResponse.DiscountAuditData.builder()
                        .auditLog(auditEntries)
                        .pagination(ProductListResponse.PaginationInfo.builder()
                                .currentPage(page)
                                .totalPages(auditPage.getTotalPages())
                                .totalProducts(auditPage.getTotalElements())
                                .perPage(pageSize)
                                .hasNext(auditPage.hasNext())
                                .hasPrevious(auditPage.hasPrevious())
                                .build())
                        .build())
                .build();
    }

    // ==================== Helper Methods ====================

    private void createAuditLog(ProductDiscount discount, String action, User user, String ipAddress,
                                Map<String, Object> oldValue, Map<String, Object> newValue, String reason) {
        DiscountAuditLog auditLog = DiscountAuditLog.builder()
                .discount(discount)
                .action(action)
                .performedBy(user)
                .performedByRole(user.getUserType())
                .oldValue(oldValue)
                .newValue(newValue)
                .reason(reason)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
    }

    private DiscountListResponse.DiscountDetail mapToDiscountDetail(ProductDiscount discount) {
        Product product = discount.getProduct();
        Partner partner = discount.getPartner();

        DiscountListResponse.ApprovalInfo approvalInfo = null;
        if (discount.getApprovedBy() != null || discount.getDisabledBy() != null) {
            approvalInfo = DiscountListResponse.ApprovalInfo.builder()
                    .approvedByName(discount.getApprovedBy() != null ? discount.getApprovedBy().getFullName() : null)
                    .approvedAt(discount.getApprovedAt())
                    .disabledByName(discount.getDisabledBy() != null ? discount.getDisabledBy().getFullName() : null)
                    .disabledAt(discount.getDisabledAt())
                    .disabledReason(discount.getDisabledReason())
                    .build();
        }

        return DiscountListResponse.DiscountDetail.builder()
                .discountId(discount.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productCategory(product.getCategory().getDisplayName())
                .partnerId(partner.getId())
                .partnerName(partner.getBusinessName())
                .discountPercentage(discount.getDiscountPercentage())
                .discountName(discount.getDiscountName())
                .status(discount.getStatus().name())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .createdAt(discount.getCreatedAt())
                .approvalInfo(approvalInfo)
                .build();
    }
}
