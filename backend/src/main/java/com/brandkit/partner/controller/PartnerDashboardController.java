package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.PartnerDashboardResponse;
import com.brandkit.partner.service.PartnerDashboardService;
import com.brandkit.partner.service.PartnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Partner Dashboard Controller - FRD-005 FR-52
 * Dashboard overview with key metrics
 */
@RestController
@RequestMapping("/api/partner/dashboard")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Dashboard", description = "Partner dashboard APIs - Internal Portal Only")
public class PartnerDashboardController {

    @Autowired
    private PartnerDashboardService dashboardService;
    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get partner dashboard summary
     * FRD-005 FR-52: Partner Dashboard Home
     */
    @GetMapping
    @Operation(summary = "Get dashboard summary", description = "Get partner dashboard with metrics and alerts")
    public ResponseEntity<PartnerDashboardResponse> getDashboard(@CurrentUser User user) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerDashboardResponse response = dashboardService.getDashboard(partner.getId());
        return ResponseEntity.ok(response);
    }
}
