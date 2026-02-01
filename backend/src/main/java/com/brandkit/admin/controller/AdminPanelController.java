package com.brandkit.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.dto.*;
import com.brandkit.admin.entity.AdminRole;
import com.brandkit.admin.service.*;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Comprehensive Admin Panel Controller
 * 
 * FRD-006: Admin Panel for Platform Management and Operations
 * 
 * Sub-Prompt 1: Admin Role-Based Access Control
 * Sub-Prompt 2: Admin Dashboard Home
 * Sub-Prompt 7: Commission Configuration
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Panel", description = "Admin Panel APIs for platform management")
public class AdminPanelController {
    private static final Logger log = LoggerFactory.getLogger(AdminPanelController.class);

    @Autowired
    private AdminProfileService adminProfileService;
    
    @Autowired
    private AdminDashboardService dashboardService;
    
    @Autowired
    private CommissionService commissionService;
    
    @Autowired
    private AdminAuditService auditService;

    // ============================================================================
    // SUB-PROMPT 1: Admin Role-Based Access Control
    // ============================================================================

    /**
     * Create new admin account (Super Admin only)
     * POST /api/admin/admins
     */
    @PostMapping("/admins")
    @Operation(summary = "Create admin account", description = "Create a new admin account (Super Admin only)")
    public ResponseEntity<?> createAdmin(
            @Valid @RequestBody CreateAdminRequest request,
            @CurrentUser User currentUser) {
        
        try {
            AdminProfileResponse response = adminProfileService.createAdmin(request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Admin account created successfully",
                    "data", response
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "code", "ADM_009",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "code", "ADM_001",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all admin accounts
     * GET /api/admin/admins
     */
    @GetMapping("/admins")
    @Operation(summary = "List all admins", description = "Get paginated list of admin accounts")
    public ResponseEntity<Page<AdminProfileResponse>> listAdmins(
            @RequestParam(required = false) AdminRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<AdminProfileResponse> admins = role != null
                ? adminProfileService.getAdminsByRole(role, pageable)
                : adminProfileService.getAllAdmins(pageable);
        
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admin profile by ID
     * GET /api/admin/admins/{id}
     */
    @GetMapping("/admins/{id}")
    @Operation(summary = "Get admin profile", description = "Get admin profile by user ID")
    public ResponseEntity<?> getAdminProfile(@PathVariable UUID id) {
        try {
            AdminProfileResponse response = adminProfileService.getAdminProfile(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "code", "ADM_008",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Update admin role (Super Admin only)
     * PUT /api/admin/admins/{id}/role
     */
    @PutMapping("/admins/{id}/role")
    @Operation(summary = "Update admin role", description = "Update admin role (Super Admin only)")
    public ResponseEntity<?> updateAdminRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request,
            @CurrentUser User currentUser) {
        
        try {
            AdminRole newRole = AdminRole.valueOf(request.get("role"));
            AdminProfileResponse response = adminProfileService.updateAdminRole(id, newRole, currentUser);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Admin role updated successfully",
                    "data", response
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "code", "ADM_009",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Deactivate admin account (Super Admin only)
     * POST /api/admin/admins/{id}/deactivate
     */
    @PostMapping("/admins/{id}/deactivate")
    @Operation(summary = "Deactivate admin", description = "Deactivate admin account (Super Admin only)")
    public ResponseEntity<?> deactivateAdmin(
            @PathVariable UUID id,
            @CurrentUser User currentUser) {
        
        try {
            adminProfileService.deactivateAdmin(id, currentUser);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Admin account deactivated successfully"
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "code", "ADM_009",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Check current admin's permissions
     * GET /api/admin/me/permissions
     */
    @GetMapping("/me/permissions")
    @Operation(summary = "Get my permissions", description = "Get current admin's permissions")
    public ResponseEntity<?> getMyPermissions(@CurrentUser User currentUser) {
        AdminProfileResponse profile = adminProfileService.getAdminProfile(currentUser.getId());
        return ResponseEntity.ok(Map.of(
                "adminRole", profile.getAdminRole(),
                "canCreateAdmins", profile.getCanCreateAdmins(),
                "canAccessSystemSettings", profile.getCanAccessSystemSettings(),
                "canConfigureCommission", profile.getCanConfigureCommission(),
                "canManageDiscounts", profile.getCanManageDiscounts()
        ));
    }

    // ============================================================================
    // SUB-PROMPT 2: Admin Dashboard Home
    // ============================================================================

    /**
     * Get dashboard summary
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard summary", description = "Get comprehensive dashboard with metrics and charts")
    public ResponseEntity<DashboardSummaryResponse> getDashboard() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    // ============================================================================
    // SUB-PROMPT 7: Commission Configuration
    // ============================================================================

    /**
     * Get commission configuration
     * GET /api/admin/commissions
     */
    @GetMapping("/commissions")
    @Operation(summary = "Get commission config", description = "Get current commission configuration")
    public ResponseEntity<CommissionConfigResponse> getCommissionConfig() {
        CommissionConfigResponse config = commissionService.getDefaultConfig();
        return ResponseEntity.ok(config);
    }

    /**
     * Get all commission configurations
     * GET /api/admin/commissions/all
     */
    @GetMapping("/commissions/all")
    @Operation(summary = "Get all commission configs", description = "Get all commission configurations")
    public ResponseEntity<List<CommissionConfigResponse>> getAllCommissionConfigs() {
        List<CommissionConfigResponse> configs = commissionService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    /**
     * Create commission configuration (Super Admin only)
     * POST /api/admin/commissions
     */
    @PostMapping("/commissions")
    @Operation(summary = "Create commission config", description = "Create new commission configuration")
    public ResponseEntity<?> createCommissionConfig(
            @Valid @RequestBody CommissionConfigRequest request,
            @CurrentUser User currentUser) {
        
        // Check permission
        if (!adminProfileService.canAccessFeature(currentUser.getId(), "COMMISSION_CONFIG")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "code", "ADM_009",
                    "message", "You don't have permission to configure commissions"
            ));
        }

        try {
            CommissionConfigResponse response = commissionService.saveConfig(request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Commission configuration created successfully",
                    "data", response
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "code", "ADM_006",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Update commission configuration (Super Admin only)
     * PUT /api/admin/commissions/{id}
     */
    @PutMapping("/commissions/{id}")
    @Operation(summary = "Update commission config", description = "Update commission configuration")
    public ResponseEntity<?> updateCommissionConfig(
            @PathVariable UUID id,
            @Valid @RequestBody CommissionConfigRequest request,
            @CurrentUser User currentUser) {
        
        if (!adminProfileService.canAccessFeature(currentUser.getId(), "COMMISSION_CONFIG")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "code", "ADM_009",
                    "message", "You don't have permission to configure commissions"
            ));
        }

        try {
            CommissionConfigResponse response = commissionService.updateConfig(id, request, currentUser);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Commission configuration updated successfully",
                    "data", response
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "code", "ADM_006",
                    "message", e.getMessage()
            ));
        }
    }

    // ============================================================================
    // AUDIT LOGS
    // ============================================================================

    /**
     * Get audit logs
     * GET /api/admin/logs
     */
    @GetMapping("/logs")
    @Operation(summary = "Get audit logs", description = "Get admin audit logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(auditService.searchLogs(keyword, pageable));
        } else if (entityType != null && entityId != null) {
            return ResponseEntity.ok(auditService.getLogsByEntity(entityType, entityId, pageable));
        } else if (actionType != null) {
            return ResponseEntity.ok(auditService.getLogsByAction(actionType, pageable));
        }
        
        // Default: return all logs
        return ResponseEntity.ok(auditService.searchLogs("", pageable));
    }
}
