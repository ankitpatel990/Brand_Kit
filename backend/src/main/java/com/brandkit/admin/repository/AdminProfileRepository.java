package com.brandkit.admin.repository;

import com.brandkit.admin.entity.AdminProfile;
import com.brandkit.admin.entity.AdminRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AdminProfile entity
 * 
 * FRD-006 FR-66: Admin Authentication and Roles
 */
@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, UUID> {

    /**
     * Find admin profile by user ID
     */
    Optional<AdminProfile> findByUserId(UUID userId);

    /**
     * Check if user has admin profile
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find all active admins
     */
    Page<AdminProfile> findByIsActiveTrue(Pageable pageable);

    /**
     * Find admins by role
     */
    Page<AdminProfile> findByAdminRoleAndIsActiveTrue(AdminRole role, Pageable pageable);

    /**
     * Count admins by role
     */
    long countByAdminRole(AdminRole role);

    /**
     * Count active admins
     */
    long countByIsActiveTrue();

    /**
     * Find super admins
     */
    @Query("SELECT ap FROM AdminProfile ap WHERE ap.adminRole = 'SUPER_ADMIN' AND ap.isActive = true")
    Page<AdminProfile> findSuperAdmins(Pageable pageable);

    /**
     * Check if any super admin exists
     */
    @Query("SELECT COUNT(ap) > 0 FROM AdminProfile ap WHERE ap.adminRole = 'SUPER_ADMIN' AND ap.isActive = true")
    boolean existsActiveSuperAdmin();

    /**
     * Find admin profile by user email
     */
    @Query("SELECT ap FROM AdminProfile ap JOIN ap.user u WHERE u.email = :email")
    Optional<AdminProfile> findByUserEmail(@Param("email") String email);
}
