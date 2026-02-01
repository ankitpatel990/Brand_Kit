package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.DiscountRequest;
import com.brandkit.partner.dto.DiscountResponse;
import com.brandkit.partner.dto.DiscountResponse.ProductDiscountDto;
import com.brandkit.partner.service.PartnerDiscountService;
import com.brandkit.partner.service.PartnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Partner Discount Controller - FRD-005 FR-64b
 * Partner discount management
 */
@RestController
@RequestMapping("/api/partner/discounts")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Discounts", description = "Partner discount management APIs - Internal Portal Only")
public class PartnerDiscountController {

    @Autowired
    private PartnerDiscountService discountService;
    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get discounts dashboard
     * FRD-005 FR-64b: Partner Discount Management
     */
    @GetMapping
    @Operation(summary = "Get discounts", description = "Get discount dashboard for partner")
    public ResponseEntity<DiscountResponse> getDiscounts(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        DiscountResponse response = discountService.getDiscounts(partner.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update discount
     */
    @PostMapping
    @Operation(summary = "Create/Update discount", description = "Create or update a product discount")
    public ResponseEntity<ProductDiscountDto> createOrUpdateDiscount(
            @CurrentUser User user,
            @Valid @RequestBody DiscountRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        ProductDiscountDto response = discountService.createOrUpdateDiscount(partner.getId(), request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete discount
     */
    @DeleteMapping("/{discountId}")
    @Operation(summary = "Delete discount", description = "Remove a product discount")
    public ResponseEntity<Void> deleteDiscount(
            @CurrentUser User user,
            @PathVariable UUID discountId) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        discountService.deleteDiscount(partner.getId(), discountId, user);
        return ResponseEntity.ok().build();
    }
}
