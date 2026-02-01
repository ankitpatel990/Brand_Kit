package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.catalog.repository.ProductRepository;
import com.brandkit.partner.dto.DiscountRequest;
import com.brandkit.partner.dto.DiscountResponse;
import com.brandkit.partner.dto.DiscountResponse.*;
import com.brandkit.partner.entity.DiscountAuditLog;
import com.brandkit.partner.entity.DiscountStatus;
import com.brandkit.partner.entity.PartnerDiscount;
import com.brandkit.partner.entity.PlatformSetting;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.PartnerDiscountAuditLogRepository;
import com.brandkit.partner.repository.PartnerDiscountRepository;
import com.brandkit.partner.repository.PlatformSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Partner Discount Service - FRD-005 FR-64b
 * Partner discount management
 */
@Service
public class PartnerDiscountService {
    private static final Logger log = LoggerFactory.getLogger(PartnerDiscountService.class);

    @Autowired
    private PartnerDiscountRepository discountRepository;
    @Autowired
    private PartnerDiscountAuditLogRepository auditLogRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PlatformSettingRepository settingRepository;
    @Autowired
    private PartnerNotificationService notificationService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Get discounts dashboard for partner
     */
    @Transactional(readOnly = true)
    public DiscountResponse getDiscounts(UUID partnerId, int page, int size) {
        log.debug("Getting discounts for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        // Get discount limits
        DiscountLimits limits = getDiscountLimits();

        // Get partner's discounts
        Page<PartnerDiscount> discounts = discountRepository
                .findByPartnerIdOrderByCreatedAtDesc(partnerId, PageRequest.of(page, size));

        List<ProductDiscountDto> productDiscounts = discounts.getContent().stream()
                .map(d -> mapToProductDiscountDto(d, partner))
                .collect(java.util.stream.Collectors.toList());

        return DiscountResponse.builder()
                .products(productDiscounts)
                .limits(limits)
                .page(page)
                .size(size)
                .totalElements(discounts.getTotalElements())
                .totalPages(discounts.getTotalPages())
                .build();
    }

    /**
     * Create or update discount
     * FRD-005 FR-64b: Define Discount
     */
    @Transactional
    public ProductDiscountDto createOrUpdateDiscount(UUID partnerId, DiscountRequest request, User user) {
        log.info("Partner {} creating/updating discount for product {}: {}%",
                partnerId, request.getProductId(), request.getDiscountPercentage());

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new PartnerException("Product not found"));

        // Validate discount against platform limits
        DiscountLimits limits = getDiscountLimits();
        validateDiscountPercentage(request.getDiscountPercentage(), limits);

        // Check for existing discount
        Optional<PartnerDiscount> existingOpt = discountRepository
                .findByPartnerIdAndProductId(partnerId, request.getProductId());

        PartnerDiscount discount;
        BigDecimal oldDiscount = null;
        DiscountStatus oldStatus = null;

        if (existingOpt.isPresent()) {
            discount = existingOpt.get();
            oldDiscount = discount.getDiscountPercentage();
            oldStatus = discount.getStatus();

            discount.setDiscountPercentage(request.getDiscountPercentage());
            discount.setStatus(limits.isAutoApprove() ? DiscountStatus.APPROVED : DiscountStatus.PENDING);
        } else {
            discount = PartnerDiscount.builder()
                    .partner(partner)
                    .product(product)
                    .discountPercentage(request.getDiscountPercentage())
                    .status(limits.isAutoApprove() ? DiscountStatus.APPROVED : DiscountStatus.PENDING)
                    .build();
        }

        discountRepository.save(discount);

        // Create audit log
        createAuditLog(partner, product, oldDiscount, request.getDiscountPercentage(),
                oldStatus, discount.getStatus(), user, "PARTNER", null);

        log.info("Discount created/updated for product {}: {}% (status: {})",
                request.getProductId(), request.getDiscountPercentage(), discount.getStatus());

        return mapToProductDiscountDto(discount, partner);
    }

    /**
     * Delete discount
     */
    @Transactional
    public void deleteDiscount(UUID partnerId, UUID discountId, User user) {
        PartnerDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new PartnerException("Discount not found"));

        if (!discount.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied");
        }

        // Create audit log
        createAuditLog(discount.getPartner(), discount.getProduct(),
                discount.getDiscountPercentage(), null,
                discount.getStatus(), null, user, "PARTNER", "Discount removed by partner");

        discountRepository.delete(discount);
        log.info("Discount {} deleted by partner {}", discountId, partnerId);
    }

    /**
     * Admin: Approve discount
     */
    @Transactional
    public void approveDiscount(UUID discountId, User admin) {
        PartnerDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new PartnerException("Discount not found"));

        DiscountStatus oldStatus = discount.getStatus();
        discount.approve(admin);
        discountRepository.save(discount);

        // Create audit log
        createAuditLog(discount.getPartner(), discount.getProduct(),
                discount.getDiscountPercentage(), discount.getDiscountPercentage(),
                oldStatus, DiscountStatus.APPROVED, admin, "ADMIN", "Approved by admin");

        // Notify partner
        notificationService.createDiscountApprovalNotification(
                discount.getPartner(), discount.getProduct().getName(), true);

        log.info("Discount {} approved by admin {}", discountId, admin.getId());
    }

    /**
     * Admin: Disable discount
     */
    @Transactional
    public void disableDiscount(UUID discountId, User admin, String reason) {
        PartnerDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new PartnerException("Discount not found"));

        DiscountStatus oldStatus = discount.getStatus();
        discount.disable(admin, reason);
        discountRepository.save(discount);

        // Create audit log
        createAuditLog(discount.getPartner(), discount.getProduct(),
                discount.getDiscountPercentage(), discount.getDiscountPercentage(),
                oldStatus, DiscountStatus.DISABLED, admin, "ADMIN", reason);

        // Notify partner
        notificationService.createDiscountApprovalNotification(
                discount.getPartner(), discount.getProduct().getName(), false);

        log.info("Discount {} disabled by admin {} with reason: {}", discountId, admin.getId(), reason);
    }

    private void validateDiscountPercentage(BigDecimal percentage, DiscountLimits limits) {
        if (percentage.compareTo(limits.getMinDiscount()) < 0) {
            throw new PartnerException("Discount cannot be less than " + limits.getMinDiscount() + "%");
        }
        if (percentage.compareTo(limits.getMaxDiscount()) > 0) {
            throw new PartnerException("Discount cannot exceed " + limits.getMaxDiscount() + "%");
        }
    }

    private DiscountLimits getDiscountLimits() {
        BigDecimal minDiscount = getSettingValue("MIN_PARTNER_DISCOUNT", "0");
        BigDecimal maxDiscount = getSettingValue("MAX_PARTNER_DISCOUNT", "25");
        boolean autoApprove = Boolean.parseBoolean(
                getSettingStringValue("AUTO_APPROVE_PARTNER_DISCOUNT", "false"));

        return DiscountLimits.builder()
                .minDiscount(minDiscount)
                .maxDiscount(maxDiscount)
                .autoApprove(autoApprove)
                .build();
    }

    private BigDecimal getSettingValue(String key, String defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(s -> new BigDecimal(s.getSettingValue()))
                .orElse(new BigDecimal(defaultValue));
    }

    private String getSettingStringValue(String key, String defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(PlatformSetting::getSettingValue)
                .orElse(defaultValue);
    }

    private void createAuditLog(Partner partner, Product product,
                                BigDecimal oldDiscount, BigDecimal newDiscount,
                                DiscountStatus oldStatus, DiscountStatus newStatus,
                                User user, String role, String reason) {
        DiscountAuditLog log = DiscountAuditLog.builder()
                .partner(partner)
                .product(product)
                .oldDiscount(oldDiscount)
                .newDiscount(newDiscount)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(user)
                .changedByRole(role)
                .reason(reason)
                .build();

        auditLogRepository.save(log);
    }

    private ProductDiscountDto mapToProductDiscountDto(PartnerDiscount discount, Partner partner) {
        Product product = discount.getProduct();
        BigDecimal basePrice = product.getBasePrice();
        BigDecimal discountPercentage = discount.getDiscountPercentage();
        BigDecimal discountedPrice = basePrice.multiply(
                BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));

        // Calculate earnings impact
        BigDecimal commissionRate = partner.getCommissionRate();
        BigDecimal originalEarnings = basePrice.multiply(
                BigDecimal.ONE.subtract(commissionRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));
        BigDecimal discountedEarnings = discountedPrice.multiply(
                BigDecimal.ONE.subtract(commissionRate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));

        EarningsImpact impact = EarningsImpact.builder()
                .originalEarnings(originalEarnings.setScale(2, RoundingMode.HALF_UP))
                .discountedEarnings(discountedEarnings.setScale(2, RoundingMode.HALF_UP))
                .difference(originalEarnings.subtract(discountedEarnings).setScale(2, RoundingMode.HALF_UP))
                .build();

        return ProductDiscountDto.builder()
                .discountId(discount.getId().toString())
                .productId(product.getId().toString())
                .productName(product.getName())
                .category(product.getCategory() != null ? product.getCategory().getDisplayName() : null)
                .basePrice(basePrice)
                .discountPercentage(discountPercentage)
                .discountedPrice(discountedPrice.setScale(2, RoundingMode.HALF_UP))
                .status(discount.getStatus().name())
                .earningsImpact(impact)
                .adminNotes(discount.getAdminNotes())
                .createdAt(discount.getCreatedAt().format(DATETIME_FORMATTER))
                .updatedAt(discount.getUpdatedAt().format(DATETIME_FORMATTER))
                .build();
    }
}
