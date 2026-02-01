package com.brandkit.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.service.AdminAuditService;
import com.brandkit.auth.dto.UserProfileResponse;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserStatus;
import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.auth.repository.UserSessionRepository;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Admin User Management Controller
 * 
 * FRD-006 Sub-Prompt 3: User Management Interface
 * - User list with filters and search
 * - User detail view
 * - Activate/Deactivate users
 * - Send password reset links
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User Management", description = "Admin APIs for managing users")
public class AdminUserController {
    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserSessionRepository sessionRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private AdminAuditService auditService;

    /**
     * GET /api/admin/users
     * FRD-006 FR-68: User list with filtering and pagination
     */
    @GetMapping
    @Operation(summary = "List all users", description = "Get paginated list of users with optional filters")
    public ResponseEntity<Page<UserProfileResponse>> listUsers(
            @RequestParam(required = false) UserType role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;
        
        if (search != null && !search.isEmpty()) {
            users = userRepository.searchUsers(search, pageable);
        } else if (role != null && status != null) {
            users = userRepository.findByUserTypeAndStatus(role, status, pageable);
        } else if (role != null) {
            users = userRepository.findByUserType(role, pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(status, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        Page<UserProfileResponse> response = users.map(UserProfileResponse::fromEntity);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/users/{id}
     * FRD-006 FR-68: View specific user details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user details", description = "Get detailed user information by ID")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserProfileResponse.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/admin/users/{id}/status
     * FRD-006 FR-68: Activate/deactivate user accounts
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "Activate or deactivate a user account")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request,
            @CurrentUser User admin) {
        
        UserStatus newStatus = UserStatus.valueOf(request.get("status"));
        String reason = request.get("reason");
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserStatus oldStatus = user.getStatus();
        user.setStatus(newStatus);
        userRepository.save(user);

        // If deactivating, invalidate all sessions
        if (newStatus == UserStatus.INACTIVE) {
            sessionRepository.revokeAllUserSessions(id, ZonedDateTime.now());
            log.info("Admin {} deactivated user {} and revoked all sessions. Reason: {}", 
                    admin.getEmail(), user.getEmail(), reason);
        }

        // Log the action
        auditService.logAction(
                admin,
                "UPDATE_USER_STATUS",
                "USER",
                id,
                Map.of("status", oldStatus.name()),
                Map.of("status", newStatus.name(), "reason", reason != null ? reason : "")
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User status updated to " + newStatus
        ));
    }

    /**
     * POST /api/admin/users/{id}/send-reset-link
     * FRD-006 FR-68: Send password reset links to users
     */
    @PostMapping("/{id}/send-reset-link")
    @Operation(summary = "Send password reset", description = "Send password reset email to user")
    public ResponseEntity<?> sendPasswordResetLink(
            @PathVariable UUID id,
            @CurrentUser User admin) {
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        authService.requestPasswordReset(user.getEmail());

        auditService.logActionAsync(
                admin,
                "SEND_PASSWORD_RESET",
                "USER",
                id,
                null,
                Map.of("email", user.getEmail())
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Password reset email sent to " + user.getEmail()
        ));
    }

    /**
     * GET /api/admin/users/{id}/sessions
     * FRD-006 FR-68: View user activity (sessions)
     */
    @GetMapping("/{id}/sessions")
    @Operation(summary = "Get user sessions", description = "View all active sessions for a user")
    public ResponseEntity<?> getUserSessions(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var sessions = sessionRepository.findByUserIdAndIsRevokedFalse(id);
        long activeCount = sessionRepository.countByUserIdAndIsRevokedFalse(id);

        return ResponseEntity.ok(Map.of(
                "userId", id,
                "activeSessionCount", activeCount,
                "lastLoginAt", user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : "Never",
                "sessions", sessions.stream().map(s -> Map.of(
                        "id", s.getId(),
                        "createdAt", s.getCreatedAt(),
                        "expiresAt", s.getExpiresAt(),
                        "ipAddress", s.getIpAddress() != null ? s.getIpAddress() : "Unknown",
                        "userAgent", s.getUserAgent() != null ? s.getUserAgent() : "Unknown"
                )).collect(java.util.stream.Collectors.toList())
        ));
    }

    /**
     * DELETE /api/admin/users/{id}/sessions
     * FRD-006: Revoke all user sessions (force logout)
     */
    @DeleteMapping("/{id}/sessions")
    @Operation(summary = "Revoke all sessions", description = "Force logout user from all devices")
    public ResponseEntity<?> revokeAllSessions(
            @PathVariable UUID id,
            @CurrentUser User admin) {
        
        sessionRepository.revokeAllUserSessions(id, ZonedDateTime.now());
        
        auditService.logActionAsync(
                admin,
                "REVOKE_ALL_SESSIONS",
                "USER",
                id,
                null,
                null
        );
        
        log.info("Admin {} revoked all sessions for user {}", admin.getEmail(), id);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All sessions revoked"
        ));
    }

    /**
     * GET /api/admin/users/stats
     * FRD-006 FR-68: User statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get aggregated user statistics")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "clientCount", userRepository.countByUserType(UserType.CLIENT),
                "partnerCount", userRepository.countByUserType(UserType.PARTNER),
                "adminCount", userRepository.countByUserType(UserType.ADMIN),
                "activeUsers", userRepository.countByStatus(UserStatus.ACTIVE),
                "pendingVerification", userRepository.countByStatus(UserStatus.PENDING_VERIFICATION),
                "inactiveUsers", userRepository.countByStatus(UserStatus.INACTIVE)
        ));
    }
}
