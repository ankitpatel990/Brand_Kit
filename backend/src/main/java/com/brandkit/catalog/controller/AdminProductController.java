package com.brandkit.catalog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.dto.ProductListResponse;
import com.brandkit.catalog.dto.admin.*;
import com.brandkit.catalog.service.AdminProductService;
import com.brandkit.catalog.service.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Admin Product Controller
 * FRD-002 Sub-Prompt 5: Admin Product Management
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private static final Logger log = LoggerFactory.getLogger(AdminProductController.class);

    @Autowired
    private AdminProductService adminProductService;
    @Autowired
    private DiscountService discountService;

    /**
     * Create a new product
     * POST /api/admin/products
     */
    @PostMapping
    public ResponseEntity<AdminProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            @CurrentUser User admin
    ) {
        log.info("POST /api/admin/products - admin: {}", admin.getEmail());
        
        AdminProductResponse response = adminProductService.createProduct(request, admin);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a product
     * PUT /api/admin/products/:productId
     */
    @PutMapping("/{productId}")
    public ResponseEntity<AdminProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request,
            @CurrentUser User admin
    ) {
        log.info("PUT /api/admin/products/{} - admin: {}", productId, admin.getEmail());
        
        AdminProductResponse response = adminProductService.updateProduct(productId, request, admin);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a product (soft delete)
     * DELETE /api/admin/products/:productId
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable UUID productId,
            @CurrentUser User admin
    ) {
        log.info("DELETE /api/admin/products/{} - admin: {}", productId, admin.getEmail());
        
        adminProductService.deleteProduct(productId, admin);
        
        return ResponseEntity.ok(Map.of("status", "success", "message", "Product deleted successfully"));
    }

    /**
     * Get product details (includes partner info for admin)
     * GET /api/admin/products/:productId
     */
    @GetMapping("/{productId}")
    public ResponseEntity<AdminProductResponse> getProduct(@PathVariable UUID productId) {
        log.debug("GET /api/admin/products/{}", productId);
        
        AdminProductResponse response = adminProductService.getProductForAdmin(productId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * List all products for admin
     * GET /api/admin/products
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID partnerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        log.debug("GET /api/admin/products - category: {}, status: {}, page: {}", category, status, page);
        
        ProductListResponse response = adminProductService.listProductsForAdmin(
                category, status, partnerId, search, page, limit
        );
        
        return ResponseEntity.ok(response);
    }

    // ==================== Discount Management ====================

    /**
     * List all discounts
     * GET /api/admin/discounts
     */
    @GetMapping("/discounts")
    public ResponseEntity<DiscountListResponse> listDiscounts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        log.debug("GET /api/admin/discounts - status: {}, page: {}", status, page);
        
        DiscountListResponse response = discountService.listDiscounts(status, page, limit);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Approve a discount
     * PUT /api/admin/discounts/:discountId/approve
     */
    @PutMapping("/discounts/{discountId}/approve")
    public ResponseEntity<DiscountListResponse.DiscountDetail> approveDiscount(
            @PathVariable UUID discountId,
            @CurrentUser User admin,
            HttpServletRequest request
    ) {
        log.info("PUT /api/admin/discounts/{}/approve - admin: {}", discountId, admin.getEmail());
        
        String ipAddress = request.getRemoteAddr();
        DiscountListResponse.DiscountDetail response = discountService.approveDiscount(discountId, admin, ipAddress);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Disable a discount
     * PUT /api/admin/discounts/:discountId/disable
     */
    @PutMapping("/discounts/{discountId}/disable")
    public ResponseEntity<DiscountListResponse.DiscountDetail> disableDiscount(
            @PathVariable UUID discountId,
            @Valid @RequestBody DiscountApprovalRequest request,
            @CurrentUser User admin,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /api/admin/discounts/{}/disable - admin: {}", discountId, admin.getEmail());
        
        String ipAddress = httpRequest.getRemoteAddr();
        DiscountListResponse.DiscountDetail response = discountService.disableDiscount(discountId, request, admin, ipAddress);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Set global discount limits
     * POST /api/admin/discounts/limits
     */
    @PostMapping("/discounts/limits")
    public ResponseEntity<Map<String, String>> setDiscountLimits(
            @Valid @RequestBody DiscountLimitRequest request,
            @CurrentUser User admin
    ) {
        log.info("POST /api/admin/discounts/limits - admin: {}", admin.getEmail());
        
        discountService.setDiscountLimits(request, admin);
        
        return ResponseEntity.ok(Map.of("status", "success", "message", "Discount limits updated successfully"));
    }

    /**
     * Get discount audit log
     * GET /api/admin/discounts/audit
     */
    @GetMapping("/discounts/audit")
    public ResponseEntity<DiscountAuditResponse> getDiscountAuditLog(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "50") int limit
    ) {
        log.debug("GET /api/admin/discounts/audit - page: {}", page);
        
        DiscountAuditResponse response = discountService.getAuditLog(page, limit);
        
        return ResponseEntity.ok(response);
    }
}
