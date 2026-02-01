package com.brandkit.auth.repository;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserStatus;
import com.brandkit.auth.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * User Repository
 * 
 * FRD-001: User data access layer
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive)
     * FRD-001 FR-13: Duplicate Account Prevention
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if email exists
     * FRD-001 FR-1: Email uniqueness check during registration
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find by Google ID for OAuth
     * FRD-001 FR-3: Google OAuth Integration
     */
    Optional<User> findByGoogleId(String googleId);

    /**
     * Find by LinkedIn ID for OAuth
     * FRD-001 FR-4: LinkedIn OAuth Integration
     */
    Optional<User> findByLinkedinId(String linkedinId);

    /**
     * Find all users by type (for admin)
     * FRD-001 FR-11: Account Status Management
     */
    Page<User> findByUserType(UserType userType, Pageable pageable);

    /**
     * Find all users by status (for admin)
     * FRD-001 FR-11: Account Status Management
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find all users by type and status (for admin filtering)
     */
    Page<User> findByUserTypeAndStatus(UserType userType, UserStatus status, Pageable pageable);

    /**
     * Search users by name or email (for admin)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    /**
     * Count users by type
     */
    long countByUserType(UserType userType);

    /**
     * Count users by status
     */
    long countByStatus(UserStatus status);

    /**
     * Update last login timestamp
     * FRD-001 FR-6: Update last_login timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :timestamp WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("timestamp") ZonedDateTime timestamp);

    /**
     * Update user status
     * FRD-001 FR-11: Activate/Deactivate accounts
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    void updateStatus(@Param("userId") UUID userId, @Param("status") UserStatus status);

    /**
     * Unlock expired locked accounts
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = NULL, u.failedLoginAttempts = 0 " +
           "WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil < :now")
    void unlockExpiredAccounts(@Param("now") ZonedDateTime now);

    // ============================================================================
    // ADMIN DASHBOARD QUERIES - FRD-006 FR-67
    // ============================================================================

    /**
     * Count active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    Long countByStatusActive();

    /**
     * Find recent user registrations
     */
    java.util.List<User> findTop5ByOrderByCreatedAtDesc();

    /**
     * Check if email exists (case-sensitive version)
     */
    boolean existsByEmail(String email);
}
