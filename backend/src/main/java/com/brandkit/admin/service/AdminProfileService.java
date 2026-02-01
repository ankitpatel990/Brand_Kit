package com.brandkit.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.dto.AdminProfileResponse;
import com.brandkit.admin.dto.CreateAdminRequest;
import com.brandkit.admin.entity.AdminProfile;
import com.brandkit.admin.entity.AdminRole;
import com.brandkit.admin.repository.AdminProfileRepository;
import com.brandkit.auth.entity.AuthProvider;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserStatus;
import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Service for admin profile management
 * 
 * FRD-006 FR-66: Admin Authentication and Roles
 */
@Service
public class AdminProfileService {
    private static final Logger log = LoggerFactory.getLogger(AdminProfileService.class);

    @Autowired
    private AdminProfileRepository adminProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AdminAuditService auditService;

    /**
     * Create a new admin account
     * Only Super Admins can create new admins
     * 
     * @param request Admin creation request
     * @param createdBy The admin creating this account
     * @return Created admin profile response with generated password
     */
    @Transactional
    public AdminProfileResponse createAdmin(CreateAdminRequest request, User createdBy) {
        // Verify the creator is a Super Admin
        AdminProfile creatorProfile = adminProfileRepository.findByUserId(createdBy.getId())
                .orElseThrow(() -> new IllegalStateException("Creator admin profile not found"));
        
        if (!creatorProfile.isSuperAdmin()) {
            throw new SecurityException("Only Super Admins can create new admin accounts");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Generate password if not provided
        String password = request.getPassword();
        if (password == null || password.isEmpty()) {
            password = generateSecurePassword();
        }

        // Create user account
        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(password))
                .userType(UserType.ADMIN)
                .status(UserStatus.ACTIVE)
                .emailVerified(true) // Admin accounts don't need email verification
                .authProvider(AuthProvider.EMAIL)
                .build();
        
        user = userRepository.save(user);

        // Create admin profile
        AdminProfile profile = AdminProfile.builder()
                .user(user)
                .adminRole(request.getAdminRole())
                .department(request.getDepartment())
                .employeeId(request.getEmployeeId())
                .createdBy(createdBy)
                .build();

        profile = adminProfileRepository.save(profile);

        // Log the action
        auditService.logAction(
                createdBy,
                "CREATE_ADMIN",
                "ADMIN",
                profile.getId(),
                null,
                profile
        );

        log.info("Admin account created: {} by {}", user.getEmail(), createdBy.getEmail());

        AdminProfileResponse response = AdminProfileResponse.fromEntity(profile);
        // Note: In production, send password via secure email, not in response
        return response;
    }

    /**
     * Get admin profile by user ID
     */
    public AdminProfileResponse getAdminProfile(UUID userId) {
        AdminProfile profile = adminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Admin profile not found"));
        return AdminProfileResponse.fromEntity(profile);
    }

    /**
     * Get all admin profiles
     */
    public Page<AdminProfileResponse> getAllAdmins(Pageable pageable) {
        return adminProfileRepository.findByIsActiveTrue(pageable)
                .map(AdminProfileResponse::fromEntity);
    }

    /**
     * Get admins by role
     */
    public Page<AdminProfileResponse> getAdminsByRole(AdminRole role, Pageable pageable) {
        return adminProfileRepository.findByAdminRoleAndIsActiveTrue(role, pageable)
                .map(AdminProfileResponse::fromEntity);
    }

    /**
     * Update admin role (Super Admin only)
     */
    @Transactional
    public AdminProfileResponse updateAdminRole(UUID adminId, AdminRole newRole, User updatedBy) {
        AdminProfile updaterProfile = adminProfileRepository.findByUserId(updatedBy.getId())
                .orElseThrow(() -> new IllegalStateException("Updater admin profile not found"));
        
        if (!updaterProfile.isSuperAdmin()) {
            throw new SecurityException("Only Super Admins can update admin roles");
        }

        AdminProfile profile = adminProfileRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin profile not found"));

        AdminRole oldRole = profile.getAdminRole();
        profile.setAdminRole(newRole);
        profile = adminProfileRepository.save(profile);

        auditService.logAction(
                updatedBy,
                "UPDATE_ADMIN_ROLE",
                "ADMIN",
                adminId,
                oldRole,
                newRole
        );

        log.info("Admin role updated: {} from {} to {} by {}", 
                profile.getUser().getEmail(), oldRole, newRole, updatedBy.getEmail());

        return AdminProfileResponse.fromEntity(profile);
    }

    /**
     * Deactivate admin account
     */
    @Transactional
    public void deactivateAdmin(UUID adminId, User deactivatedBy) {
        AdminProfile deactivatorProfile = adminProfileRepository.findByUserId(deactivatedBy.getId())
                .orElseThrow(() -> new IllegalStateException("Deactivator admin profile not found"));
        
        if (!deactivatorProfile.isSuperAdmin()) {
            throw new SecurityException("Only Super Admins can deactivate admin accounts");
        }

        AdminProfile profile = adminProfileRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin profile not found"));

        // Prevent self-deactivation
        if (profile.getUser().getId().equals(deactivatedBy.getId())) {
            throw new IllegalArgumentException("Cannot deactivate your own account");
        }

        profile.setIsActive(false);
        profile.getUser().setStatus(UserStatus.INACTIVE);
        adminProfileRepository.save(profile);
        userRepository.save(profile.getUser());

        auditService.logAction(
                deactivatedBy,
                "DEACTIVATE_ADMIN",
                "ADMIN",
                adminId,
                true,
                false
        );

        log.info("Admin account deactivated: {} by {}", 
                profile.getUser().getEmail(), deactivatedBy.getEmail());
    }

    /**
     * Check if user has Super Admin role
     */
    public boolean isSuperAdmin(UUID userId) {
        return adminProfileRepository.findByUserId(userId)
                .map(AdminProfile::isSuperAdmin)
                .orElse(false);
    }

    /**
     * Check if admin can access feature
     */
    public boolean canAccessFeature(UUID userId, String feature) {
        AdminProfile profile = adminProfileRepository.findByUserId(userId).orElse(null);
        if (profile == null || !profile.getIsActive()) {
            return false;
        }

        return switch (feature) {
            case "CREATE_ADMIN" -> profile.getCanCreateAdmins();
            case "SYSTEM_SETTINGS" -> profile.getCanAccessSystemSettings();
            case "COMMISSION_CONFIG" -> profile.getCanConfigureCommission();
            case "MANAGE_DISCOUNTS" -> profile.getCanManageDiscounts();
            default -> profile.isSuperAdmin(); // Super Admin has access to everything
        };
    }

    /**
     * Generate a secure random password
     */
    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        String password = Base64.getEncoder().encodeToString(bytes);
        // Ensure it has mixed case and special char
        return password.substring(0, 8) + "@" + password.substring(8, 12);
    }

    /**
     * Record admin activity
     */
    @Transactional
    public void recordActivity(UUID userId) {
        adminProfileRepository.findByUserId(userId)
                .ifPresent(profile -> {
                    profile.recordActivity();
                    adminProfileRepository.save(profile);
                });
    }
}
