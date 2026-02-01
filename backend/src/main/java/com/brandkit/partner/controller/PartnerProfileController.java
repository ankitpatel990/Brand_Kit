package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.PartnerProfileRequest;
import com.brandkit.partner.dto.PartnerProfileResponse;
import com.brandkit.partner.service.PartnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Partner Profile Controller - FRD-005 FR-64
 * Partner profile management
 */
@RestController
@RequestMapping("/api/partner/profile")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Profile", description = "Partner profile management APIs - Internal Portal Only")
public class PartnerProfileController {

    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get partner profile
     * FRD-005 FR-64: Partner Profile Management
     */
    @GetMapping
    @Operation(summary = "Get profile", description = "Get partner profile information")
    public ResponseEntity<PartnerProfileResponse> getProfile(@CurrentUser User user) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerProfileResponse response = profileService.getProfile(partner.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Update partner profile
     */
    @PutMapping
    @Operation(summary = "Update profile", description = "Update partner profile information")
    public ResponseEntity<PartnerProfileResponse> updateProfile(
            @CurrentUser User user,
            @Valid @RequestBody PartnerProfileRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerProfileResponse response = profileService.updateProfile(partner.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete first login profile setup
     */
    @PostMapping("/complete")
    @Operation(summary = "Complete profile", description = "Complete first login profile setup")
    public ResponseEntity<PartnerProfileResponse> completeProfile(
            @CurrentUser User user,
            @Valid @RequestBody PartnerProfileRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerProfileResponse response = profileService.completeProfile(partner.getId(), request);
        return ResponseEntity.ok(response);
    }
}
