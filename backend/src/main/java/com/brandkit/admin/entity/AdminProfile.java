package com.brandkit.admin.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Admin Profile Entity
 * Extended profile information for admin users with role-based permissions
 * 
 * FRD-006 FR-66: Admin Authentication and Roles
 */
@Entity
@Table(name = "admin_profiles")
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_role", nullable = false)
    private AdminRole adminRole = AdminRole.OPERATIONS_ADMIN;

    @Column(length = 100)
    private String department;

    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "can_create_admins", nullable = false)
    private Boolean canCreateAdmins = false;

    @Column(name = "can_access_system_settings", nullable = false)
    private Boolean canAccessSystemSettings = false;

    @Column(name = "can_configure_commission", nullable = false)
    private Boolean canConfigureCommission = false;

    @Column(name = "can_manage_discounts", nullable = false)
    private Boolean canManageDiscounts = true;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_activity_at")
    private ZonedDateTime lastActivityAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Check if admin has Super Admin role
     */
    public boolean isSuperAdmin() {
        return adminRole == AdminRole.SUPER_ADMIN;
    }

    /**
     * Update permissions based on admin role
     */
    @PrePersist
    @PreUpdate
    public void updatePermissions() {
        if (adminRole == AdminRole.SUPER_ADMIN) {
            this.canCreateAdmins = true;
            this.canAccessSystemSettings = true;
            this.canConfigureCommission = true;
            this.canManageDiscounts = true;
        } else {
            // Operations Admin has limited permissions
            this.canCreateAdmins = false;
            this.canAccessSystemSettings = false;
            this.canConfigureCommission = false;
            // Keep discount management enabled for Operations Admin
        }
    }

    /**
     * Update last activity timestamp
     */
    public void recordActivity() {
        this.lastActivityAt = ZonedDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public User getUser() {
        return this.user;
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
    public User getCreatedBy() {
        return this.createdBy;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
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
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public AdminProfile() {
    }
    public AdminProfile(UUID id, User user, AdminRole adminRole, String department, String employeeId, Boolean canCreateAdmins, Boolean canAccessSystemSettings, Boolean canConfigureCommission, Boolean canManageDiscounts, Boolean isActive, ZonedDateTime lastActivityAt, User createdBy, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.adminRole = adminRole;
        this.department = department;
        this.employeeId = employeeId;
        this.canCreateAdmins = canCreateAdmins;
        this.canAccessSystemSettings = canAccessSystemSettings;
        this.canConfigureCommission = canConfigureCommission;
        this.canManageDiscounts = canManageDiscounts;
        this.isActive = isActive;
        this.lastActivityAt = lastActivityAt;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static AdminProfileBuilder builder() {
        return new AdminProfileBuilder();
    }

    public static class AdminProfileBuilder {
        private UUID id;
        private User user;
        private AdminRole adminRole = AdminRole.OPERATIONS_ADMIN;
        private String department;
        private String employeeId;
        private Boolean canCreateAdmins = false;
        private Boolean canAccessSystemSettings = false;
        private Boolean canConfigureCommission = false;
        private Boolean canManageDiscounts = true;
        private Boolean isActive = true;
        private ZonedDateTime lastActivityAt;
        private User createdBy;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        AdminProfileBuilder() {
        }

        public AdminProfileBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public AdminProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public AdminProfileBuilder adminRole(AdminRole adminRole) {
            this.adminRole = adminRole;
            return this;
        }

        public AdminProfileBuilder department(String department) {
            this.department = department;
            return this;
        }

        public AdminProfileBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public AdminProfileBuilder canCreateAdmins(Boolean canCreateAdmins) {
            this.canCreateAdmins = canCreateAdmins;
            return this;
        }

        public AdminProfileBuilder canAccessSystemSettings(Boolean canAccessSystemSettings) {
            this.canAccessSystemSettings = canAccessSystemSettings;
            return this;
        }

        public AdminProfileBuilder canConfigureCommission(Boolean canConfigureCommission) {
            this.canConfigureCommission = canConfigureCommission;
            return this;
        }

        public AdminProfileBuilder canManageDiscounts(Boolean canManageDiscounts) {
            this.canManageDiscounts = canManageDiscounts;
            return this;
        }

        public AdminProfileBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public AdminProfileBuilder lastActivityAt(ZonedDateTime lastActivityAt) {
            this.lastActivityAt = lastActivityAt;
            return this;
        }

        public AdminProfileBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AdminProfileBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AdminProfileBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AdminProfile build() {
            AdminProfile instance = new AdminProfile();
            instance.id = this.id;
            instance.user = this.user;
            instance.adminRole = this.adminRole;
            instance.department = this.department;
            instance.employeeId = this.employeeId;
            instance.canCreateAdmins = this.canCreateAdmins;
            instance.canAccessSystemSettings = this.canAccessSystemSettings;
            instance.canConfigureCommission = this.canConfigureCommission;
            instance.canManageDiscounts = this.canManageDiscounts;
            instance.isActive = this.isActive;
            instance.lastActivityAt = this.lastActivityAt;
            instance.createdBy = this.createdBy;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}
