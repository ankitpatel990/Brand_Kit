package com.brandkit.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.dto.*;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Profile Controller
 * 
 * FRD-001 FR-10: User Profile Management
 * - GET /api/auth/profile - Returns current user data
 * - PUT /api/auth/profile - Update profile (name, company, phone)
 * - POST /api/auth/change-password - Change password
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Profile", description = "User profile management APIs")
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private AuthService authService;

    /**
     * GET /api/auth/profile
     * FRD-001 FR-10: Returns current user data (no password hash)
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user profile", description = "Get current authenticated user profile")
    public ResponseEntity<UserProfileResponse> getProfile(@CurrentUser UserPrincipal currentUser) {
        UserProfileResponse profile = authService.getProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/auth/profile
     * FRD-001 FR-10: Update name, company, phone
     * Email and user_type are read-only
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update profile", description = "Update user profile information")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody ProfileUpdateRequest request) {
        UserProfileResponse profile = authService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(profile);
    }

    /**
     * POST /api/auth/change-password
     * FRD-001 FR-10: Requires current password verification
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password", description = "Change user password (requires current password)")
    public ResponseEntity<Map<String, String>> changePassword(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Password changed successfully"
        ));
    }
}
