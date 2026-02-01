package com.brandkit.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.dto.CommissionConfigRequest;
import com.brandkit.admin.dto.CommissionConfigRequest.CommissionTierRequest;
import com.brandkit.admin.dto.CommissionConfigResponse;
import com.brandkit.admin.entity.CommissionConfig;
import com.brandkit.admin.entity.CommissionTier;
import com.brandkit.admin.repository.CommissionConfigRepository;
import com.brandkit.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service for commission configuration management
 * 
 * FRD-006 FR-72: Commission Configuration
 * - Default commission rate
 * - Tiered commission by order value
 * - Partner-specific commission overrides
 */
@Service
public class CommissionService {
    private static final Logger log = LoggerFactory.getLogger(CommissionService.class);

    @Autowired
    private CommissionConfigRepository configRepository;
    
    @Autowired
    private AdminAuditService auditService;

    /**
     * Get the default commission configuration
     */
    @Transactional(readOnly = true)
    public CommissionConfigResponse getDefaultConfig() {
        CommissionConfig config = configRepository.findDefaultConfigWithTiers()
                .orElseThrow(() -> new IllegalStateException("Default commission config not found"));
        return CommissionConfigResponse.fromEntity(config);
    }

    /**
     * Get all commission configurations
     */
    @Transactional(readOnly = true)
    public List<CommissionConfigResponse> getAllConfigs() {
        return configRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(CommissionConfigResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get commission configuration by ID
     */
    @Transactional(readOnly = true)
    public CommissionConfigResponse getConfigById(UUID configId) {
        CommissionConfig config = configRepository.findByIdWithTiers(configId)
                .orElseThrow(() -> new IllegalArgumentException("Commission config not found"));
        return CommissionConfigResponse.fromEntity(config);
    }

    /**
     * Create or update commission configuration
     */
    @Transactional
    public CommissionConfigResponse saveConfig(CommissionConfigRequest request, User admin) {
        // Validate tiers don't overlap
        validateTiers(request.getTiers());

        // Create new config
        CommissionConfig config = CommissionConfig.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isDefault(request.getIsDefault() != null && request.getIsDefault())
                .isActive(true)
                .createdBy(admin)
                .build();

        // If setting as default, unset current default
        if (config.getIsDefault()) {
            configRepository.findByIsDefaultTrueAndIsActiveTrue()
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        configRepository.save(existing);
                    });
        }

        config = configRepository.save(config);

        // Add tiers
        for (int i = 0; i < request.getTiers().size(); i++) {
            CommissionTierRequest tierReq = request.getTiers().get(i);
            CommissionTier tier = CommissionTier.builder()
                    .config(config)
                    .minOrderValue(tierReq.getMinOrderValue())
                    .maxOrderValue(tierReq.getMaxOrderValue())
                    .commissionPercentage(tierReq.getCommissionPercentage())
                    .displayOrder(tierReq.getDisplayOrder() != null ? tierReq.getDisplayOrder() : i)
                    .build();
            config.addTier(tier);
        }

        config = configRepository.save(config);

        // Log the action
        auditService.logAction(
                admin,
                "CREATE_COMMISSION_CONFIG",
                "COMMISSION_CONFIG",
                config.getId(),
                null,
                config
        );

        log.info("Commission config created: {} by {}", config.getName(), admin.getEmail());
        
        return CommissionConfigResponse.fromEntity(config);
    }

    /**
     * Update commission configuration
     */
    @Transactional
    public CommissionConfigResponse updateConfig(UUID configId, CommissionConfigRequest request, User admin) {
        CommissionConfig config = configRepository.findByIdWithTiers(configId)
                .orElseThrow(() -> new IllegalArgumentException("Commission config not found"));

        // Validate tiers
        validateTiers(request.getTiers());

        // Store old values for audit
        CommissionConfigResponse oldConfig = CommissionConfigResponse.fromEntity(config);

        // Update config
        config.setName(request.getName());
        config.setDescription(request.getDescription());

        // Handle default flag
        if (request.getIsDefault() != null && request.getIsDefault() && !config.getIsDefault()) {
            configRepository.findByIsDefaultTrueAndIsActiveTrue()
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(configId)) {
                            existing.setIsDefault(false);
                            configRepository.save(existing);
                        }
                    });
            config.setIsDefault(true);
        }

        // Clear and rebuild tiers
        config.clearTiers();
        for (int i = 0; i < request.getTiers().size(); i++) {
            CommissionTierRequest tierReq = request.getTiers().get(i);
            CommissionTier tier = CommissionTier.builder()
                    .config(config)
                    .minOrderValue(tierReq.getMinOrderValue())
                    .maxOrderValue(tierReq.getMaxOrderValue())
                    .commissionPercentage(tierReq.getCommissionPercentage())
                    .displayOrder(tierReq.getDisplayOrder() != null ? tierReq.getDisplayOrder() : i)
                    .build();
            config.addTier(tier);
        }

        config = configRepository.save(config);

        // Log the action
        auditService.logAction(
                admin,
                "UPDATE_COMMISSION_CONFIG",
                "COMMISSION_CONFIG",
                config.getId(),
                oldConfig,
                CommissionConfigResponse.fromEntity(config)
        );

        log.info("Commission config updated: {} by {}", config.getName(), admin.getEmail());

        return CommissionConfigResponse.fromEntity(config);
    }

    /**
     * Calculate commission rate for an order
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateCommissionRate(BigDecimal orderAmount) {
        CommissionConfig config = configRepository.findDefaultConfigWithTiers()
                .orElseThrow(() -> new IllegalStateException("Default commission config not found"));

        // Find matching tier
        return config.getTiers().stream()
                .filter(tier -> tier.contains(orderAmount))
                .findFirst()
                .map(CommissionTier::getCommissionPercentage)
                .orElse(BigDecimal.valueOf(12.00)); // Default 12%
    }

    /**
     * Calculate commission amount for an order
     */
    public BigDecimal calculateCommissionAmount(BigDecimal orderAmount) {
        BigDecimal rate = calculateCommissionRate(orderAmount);
        return orderAmount.multiply(rate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Validate tiers don't overlap and cover all values
     */
    private void validateTiers(List<CommissionTierRequest> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new IllegalArgumentException("At least one commission tier is required");
        }

        // Sort by min value
        List<CommissionTierRequest> sortedTiers = tiers.stream()
                .sorted(Comparator.comparing(CommissionTierRequest::getMinOrderValue))
                .collect(java.util.stream.Collectors.toList());

        // Check first tier starts at 0
        if (sortedTiers.get(0).getMinOrderValue().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("First tier must start at ₹0");
        }

        // Check for gaps and overlaps
        for (int i = 0; i < sortedTiers.size() - 1; i++) {
            CommissionTierRequest current = sortedTiers.get(i);
            CommissionTierRequest next = sortedTiers.get(i + 1);

            if (current.getMaxOrderValue() == null) {
                throw new IllegalArgumentException("Only the last tier can have unlimited max value");
            }

            // Check for gap
            BigDecimal expectedNextMin = current.getMaxOrderValue().add(BigDecimal.ONE);
            if (next.getMinOrderValue().compareTo(expectedNextMin) != 0) {
                if (next.getMinOrderValue().compareTo(current.getMaxOrderValue()) <= 0) {
                    throw new IllegalArgumentException("Commission tiers cannot overlap");
                } else {
                    throw new IllegalArgumentException("Commission tiers cannot have gaps");
                }
            }
        }

        // Last tier should have no max (unlimited)
        CommissionTierRequest lastTier = sortedTiers.get(sortedTiers.size() - 1);
        if (lastTier.getMaxOrderValue() != null) {
            log.warn("Last tier has max value set - should be unlimited for full coverage");
        }
    }
}
