package com.brandkit.catalog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.dto.admin.DiscountListResponse;
import com.brandkit.catalog.dto.admin.DiscountRequest;
import com.brandkit.catalog.service.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Partner Discount Controller
 * FRD-002 Sub-Prompt 7: Partner Discount Management
 */
@RestController
@RequestMapping("/api/admin/partner/discounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPartnerDiscountController {
    private static final Logger log = LoggerFactory.getLogger(AdminPartnerDiscountController.class);

    @Autowired
    private DiscountService discountService;

    /**
     * Partner creates a discount proposal
     * POST /api/partner/discounts
     */
    @PostMapping
    public ResponseEntity<DiscountListResponse.DiscountDetail> createDiscount(
            @Valid @RequestBody DiscountRequest request,
            @CurrentUser User partner,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /api/partner/discounts - partner: {}, product: {}", 
                partner.getEmail(), request.getProductId());
        
        String ipAddress = httpRequest.getRemoteAddr();
        DiscountListResponse.DiscountDetail response = discountService.createDiscount(request, partner, ipAddress);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
