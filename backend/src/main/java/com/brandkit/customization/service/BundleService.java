package com.brandkit.customization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.customization.dto.CreateBundleRequest;
import com.brandkit.customization.entity.Bundle;
import com.brandkit.customization.entity.BundleItem;
import com.brandkit.customization.repository.BundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bundle Service
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 */
@Service
public class BundleService {
    private static final Logger log = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private BundleRepository bundleRepository;

    /**
     * Create a new bundle
     */
    @Transactional
    public UUID createBundle(UUID userId, CreateBundleRequest request) {
        // Validate bundle size
        if (request.getItems().size() > 10) {
            throw new RuntimeException("Maximum 10 products per bundle");
        }
        if (request.getItems().isEmpty()) {
            throw new RuntimeException("Bundle must contain at least one product");
        }

        // Create bundle
        Bundle bundle = Bundle.builder()
                .userId(userId)
                .bundleName(request.getBundleName())
                .status("draft")
                .build();

        // Add items
        for (CreateBundleRequest.BundleItemRequest itemRequest : request.getItems()) {
            BundleItem item = BundleItem.builder()
                    .productId(itemRequest.getProductId())
                    .customizationId(itemRequest.getCustomizationId())
                    .quantity(itemRequest.getQuantity() != null ? itemRequest.getQuantity() : 1)
                    .unitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : BigDecimal.ZERO)
                    .build();
            item.calculateSubtotal();
            bundle.addItem(item);
        }

        bundle.recalculateTotal();
        bundle = bundleRepository.save(bundle);
        
        log.info("Bundle created: {} with {} items for user: {}", 
                bundle.getId(), bundle.getProductCount(), userId);
        
        return bundle.getId();
    }

    /**
     * Get user's bundles
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserBundles(UUID userId) {
        List<Bundle> bundles = bundleRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return bundles.stream()
                .map(this::bundleToMap)
                .collect(Collectors.toList());
    }

    /**
     * Get bundle by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBundle(UUID bundleId, UUID userId) {
        Bundle bundle = bundleRepository.findByIdAndUserId(bundleId, userId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));
        
        return bundleToMap(bundle);
    }

    /**
     * Delete bundle
     */
    @Transactional
    public void deleteBundle(UUID bundleId, UUID userId) {
        Bundle bundle = bundleRepository.findByIdAndUserId(bundleId, userId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));
        
        bundleRepository.delete(bundle);
        log.info("Bundle deleted: {} for user: {}", bundleId, userId);
    }

    /**
     * Add item to bundle
     */
    @Transactional
    public void addItemToBundle(UUID bundleId, UUID userId, CreateBundleRequest.BundleItemRequest itemRequest) {
        Bundle bundle = bundleRepository.findByIdAndUserId(bundleId, userId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));

        if (bundle.getItems().size() >= 10) {
            throw new RuntimeException("Maximum 10 products per bundle");
        }

        BundleItem item = BundleItem.builder()
                .productId(itemRequest.getProductId())
                .customizationId(itemRequest.getCustomizationId())
                .quantity(itemRequest.getQuantity() != null ? itemRequest.getQuantity() : 1)
                .unitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : BigDecimal.ZERO)
                .build();
        item.calculateSubtotal();
        bundle.addItem(item);
        
        bundleRepository.save(bundle);
    }

    /**
     * Remove item from bundle
     */
    @Transactional
    public void removeItemFromBundle(UUID bundleId, UUID userId, UUID itemId) {
        Bundle bundle = bundleRepository.findByIdAndUserId(bundleId, userId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));

        bundle.getItems().removeIf(item -> item.getId().equals(itemId));
        bundle.recalculateTotal();
        
        bundleRepository.save(bundle);
    }

    /**
     * Update bundle status to completed
     */
    @Transactional
    public void completeBundle(UUID bundleId, UUID userId) {
        Bundle bundle = bundleRepository.findByIdAndUserId(bundleId, userId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));

        bundle.setStatus("completed");
        bundleRepository.save(bundle);
    }

    private Map<String, Object> bundleToMap(Bundle bundle) {
        List<Map<String, Object>> items = bundle.getItems().stream()
                .map(item -> Map.<String, Object>of(
                        "id", item.getId().toString(),
                        "productId", item.getProductId().toString(),
                        "customizationId", item.getCustomizationId().toString(),
                        "quantity", item.getQuantity(),
                        "unitPrice", item.getUnitPrice(),
                        "subtotal", item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return Map.of(
                "id", bundle.getId().toString(),
                "bundleName", bundle.getBundleName(),
                "totalPrice", bundle.getTotalPrice() != null ? bundle.getTotalPrice() : BigDecimal.ZERO,
                "productCount", bundle.getProductCount(),
                "status", bundle.getStatus(),
                "items", items,
                "createdAt", bundle.getCreatedAt().toString()
        );
    }
}
