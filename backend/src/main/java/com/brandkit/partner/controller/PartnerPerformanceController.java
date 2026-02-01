package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.PerformanceMetricsResponse;
import com.brandkit.partner.service.PartnerPerformanceService;
import com.brandkit.partner.service.PartnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Partner Performance Controller - FRD-005 FR-63
 * Performance metrics and monitoring
 */
@RestController
@RequestMapping("/api/partner/performance")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Performance", description = "Partner performance metrics APIs - Internal Portal Only")
public class PartnerPerformanceController {

    @Autowired
    private PartnerPerformanceService performanceService;
    @Autowired
    private PartnerProfileService profileService;

    /**
     * Get performance metrics
     * FRD-005 FR-63: Partner Performance Metrics
     */
    @GetMapping
    @Operation(summary = "Get performance", description = "Get partner performance metrics")
    public ResponseEntity<PerformanceMetricsResponse> getPerformanceMetrics(
            @CurrentUser User user,
            @RequestParam(defaultValue = "all_time") String period) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PerformanceMetricsResponse response = performanceService.getPerformanceMetrics(partner.getId(), period);
        return ResponseEntity.ok(response);
    }

    /**
     * Recalculate metrics (admin only)
     */
    @PostMapping("/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recalculate metrics", description = "Recalculate partner performance metrics (Admin only)")
    public ResponseEntity<Void> recalculateMetrics(@CurrentUser User user) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        performanceService.recalculateMetrics(partner.getId());
        return ResponseEntity.ok().build();
    }
}
