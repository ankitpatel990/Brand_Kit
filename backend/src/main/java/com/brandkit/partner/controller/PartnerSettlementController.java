package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.SettlementDetailResponse;
import com.brandkit.partner.dto.SettlementResponse;
import com.brandkit.partner.service.PartnerProfileService;
import com.brandkit.partner.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Partner Settlement Controller - FRD-005 FR-61
 * Commission and settlement management
 */
@RestController
@RequestMapping("/api/partner/settlements")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Settlements", description = "Partner settlement APIs - Internal Portal Only")
public class PartnerSettlementController {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get settlement dashboard
     * FRD-005 FR-61: Commission Settlement Dashboard
     */
    @GetMapping
    @Operation(summary = "Get settlements", description = "Get settlement dashboard with summary")
    public ResponseEntity<SettlementResponse> getSettlements(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        SettlementResponse response = settlementService.getSettlementDashboard(partner.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get settlement details with order breakdown
     */
    @GetMapping("/{settlementId}")
    @Operation(summary = "Get settlement details", description = "Get detailed settlement breakdown")
    public ResponseEntity<SettlementDetailResponse> getSettlementDetails(
            @CurrentUser User user,
            @PathVariable UUID settlementId) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        SettlementDetailResponse response = settlementService.getSettlementDetails(partner.getId(), settlementId);
        return ResponseEntity.ok(response);
    }
}
