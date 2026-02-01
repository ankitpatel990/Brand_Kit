package com.brandkit.admin.dto;

import com.brandkit.admin.entity.AdminProfile;
import com.brandkit.admin.entity.AdminRole;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for admin profile
 * 
 * FRD-006 FR-66: Admin Authentication and Roles
 */
public class AdminProfileResponse {

    private UUID id;
    private UUID userId;
    private String email;
    private String fullName;
    private String phone;
    private String profilePictureUrl;
    private AdminRole adminRole;
    private String department;
    private String employeeId;
    private Boolean canCreateAdmins;
    private Boolean canAccessSystemSettings;
    private Boolean canConfigureCommission;
    private Boolean canManageDiscounts;
    private Boolean isActive;
    private ZonedDateTime lastActivityAt;
    private ZonedDateTime createdAt;
    private String createdByName;

    /**
     * Create from entity
     */
    public static AdminProfileResponse fromEntity(AdminProfile profile) {
        return AdminProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .email(profile.getUser().getEmail())
                .fullName(profile.getUser().getFullName())
                .phone(profile.getUser().getPhone())
                .profilePictureUrl(profile.getUser().getProfilePictureUrl())
                .adminRole(profile.getAdminRole())
                .department(profile.getDepartment())
                .employeeId(profile.getEmployeeId())
                .canCreateAdmins(profile.getCanCreateAdmins())
                .canAccessSystemSettings(profile.getCanAccessSystemSettings())
                .canConfigureCommission(profile.getCanConfigureCommission())
                .canManageDiscounts(profile.getCanManageDiscounts())
                .isActive(profile.getIsActive())
                .lastActivityAt(profile.getLastActivityAt())
                .createdAt(profile.getCreatedAt())
                .createdByName(profile.getCreatedBy() != null ? profile.getCreatedBy().getFullName() : null)
                .build();
    }

    public UUID getId() {
        return this.id;
    }
    public UUID getUserId() {
        return this.userId;
    }
    public String getEmail() {
        return this.email;
    }
    public String getFullName() {
        return this.fullName;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }
    public AdminRole getAdminRole() {
        return this.adminRole;
    }
    public String getDepartment() {
        return this.department;
    }
    public String getEmployeeId() {
        return this.employeeId;
    }
    public Boolean getCanCreateAdmins() {
        return this.canCreateAdmins;
    }
    public Boolean getCanAccessSystemSettings() {
        return this.canAccessSystemSettings;
    }
    public Boolean getCanConfigureCommission() {
        return this.canConfigureCommission;
    }
    public Boolean getCanManageDiscounts() {
        return this.canManageDiscounts;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public ZonedDateTime getLastActivityAt() {
        return this.lastActivityAt;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public String getCreatedByName() {
        return this.createdByName;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public void setCanCreateAdmins(Boolean canCreateAdmins) {
        this.canCreateAdmins = canCreateAdmins;
    }
    public void setCanAccessSystemSettings(Boolean canAccessSystemSettings) {
        this.canAccessSystemSettings = canAccessSystemSettings;
    }
    public void setCanConfigureCommission(Boolean canConfigureCommission) {
        this.canConfigureCommission = canConfigureCommission;
    }
    public void setCanManageDiscounts(Boolean canManageDiscounts) {
        this.canManageDiscounts = canManageDiscounts;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setLastActivityAt(ZonedDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    public AdminProfileResponse() {
    }
    public AdminProfileResponse(UUID id, UUID userId, String email, String fullName, String phone, String profilePictureUrl, AdminRole adminRole, String department, String employeeId, Boolean canCreateAdmins, Boolean canAccessSystemSettings, Boolean canConfigureCommission, Boolean canManageDiscounts, Boolean isActive, ZonedDateTime lastActivityAt, ZonedDateTime createdAt, String createdByName) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.profilePictureUrl = profilePictureUrl;
        this.adminRole = adminRole;
        this.department = department;
        this.employeeId = employeeId;
        this.canCreateAdmins = canCreateAdmins;
        this.canAccessSystemSettings = canAccessSystemSettings;
        this.canConfigureCommission = canConfigureCommission;
        this.canManageDiscounts = canManageDiscounts;
        this.isActive = isActive;
        this.lastActivityAt = lastActivityAt;
        this.createdAt = createdAt;
        this.createdByName = createdByName;
    }
    public static AdminProfileResponseBuilder builder() {
        return new AdminProfileResponseBuilder();
    }

    public static class AdminProfileResponseBuilder {
        private UUID id;
        private UUID userId;
        private String email;
        private String fullName;
        private String phone;
        private String profilePictureUrl;
        private AdminRole adminRole;
        private String department;
        private String employeeId;
        private Boolean canCreateAdmins;
        private Boolean canAccessSystemSettings;
        private Boolean canConfigureCommission;
        private Boolean canManageDiscounts;
        private Boolean isActive;
        private ZonedDateTime lastActivityAt;
        private ZonedDateTime createdAt;
        private String createdByName;

        AdminProfileResponseBuilder() {
        }

        public AdminProfileResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AdminProfileResponseBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public AdminProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AdminProfileResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AdminProfileResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public AdminProfileResponseBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public AdminProfileResponseBuilder adminRole(AdminRole adminRole) {
            this.adminRole = adminRole;
            return this;
        }

        public AdminProfileResponseBuilder department(String department) {
            this.department = department;
            return this;
        }

        public AdminProfileResponseBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public AdminProfileResponseBuilder canCreateAdmins(Boolean canCreateAdmins) {
            this.canCreateAdmins = canCreateAdmins;
            return this;
        }

        public AdminProfileResponseBuilder canAccessSystemSettings(Boolean canAccessSystemSettings) {
            this.canAccessSystemSettings = canAccessSystemSettings;
            return this;
        }

        public AdminProfileResponseBuilder canConfigureCommission(Boolean canConfigureCommission) {
            this.canConfigureCommission = canConfigureCommission;
            return this;
        }

        public AdminProfileResponseBuilder canManageDiscounts(Boolean canManageDiscounts) {
            this.canManageDiscounts = canManageDiscounts;
            return this;
        }

        public AdminProfileResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public AdminProfileResponseBuilder lastActivityAt(ZonedDateTime lastActivityAt) {
            this.lastActivityAt = lastActivityAt;
            return this;
        }

        public AdminProfileResponseBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AdminProfileResponseBuilder createdByName(String createdByName) {
            this.createdByName = createdByName;
            return this;
        }

        public AdminProfileResponse build() {
            AdminProfileResponse instance = new AdminProfileResponse();
            instance.id = this.id;
            instance.userId = this.userId;
            instance.email = this.email;
            instance.fullName = this.fullName;
            instance.phone = this.phone;
            instance.profilePictureUrl = this.profilePictureUrl;
            instance.adminRole = this.adminRole;
            instance.department = this.department;
            instance.employeeId = this.employeeId;
            instance.canCreateAdmins = this.canCreateAdmins;
            instance.canAccessSystemSettings = this.canAccessSystemSettings;
            instance.canConfigureCommission = this.canConfigureCommission;
            instance.canManageDiscounts = this.canManageDiscounts;
            instance.isActive = this.isActive;
            instance.lastActivityAt = this.lastActivityAt;
            instance.createdAt = this.createdAt;
            instance.createdByName = this.createdByName;
            return instance;
        }
    }
}
