package com.brandkit.catalog.repository;

import com.brandkit.auth.entity.UserStatus;
import com.brandkit.catalog.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Partner Repository (Internal Only)
 * FRD-002 FR-27: Partner Association
 * FRD-006 FR-69: Partner Management
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    
    Optional<Partner> findByEmail(String email);
    
    List<Partner> findByStatus(UserStatus status);
    
    List<Partner> findByCity(String city);
    
    boolean existsByEmail(String email);

    // ============================================================================
    // ADMIN DASHBOARD QUERIES - FRD-006 FR-67
    // ============================================================================

    /**
     * Count active partners
     */
    @Query("SELECT COUNT(p) FROM Partner p WHERE p.status = 'ACTIVE'")
    Long countByIsActiveTrue();

    /**
     * Count partners with unverified bank details
     */
    @Query("SELECT COUNT(p) FROM Partner p WHERE p.bankVerified = false AND p.status = 'ACTIVE'")
    Long countByBankVerifiedFalse();

    /**
     * Count low performing partners (fulfillment rate below threshold)
     */
    @Query(value = "SELECT COUNT(*) FROM partners p " +
           "JOIN partner_performance_metrics m ON p.id = m.partner_id " +
           "WHERE m.fulfillment_rate < :threshold AND p.status = 'ACTIVE'", nativeQuery = true)
    Long countLowPerformingPartners(@Param("threshold") Double threshold);

    // ============================================================================
    // ADMIN PARTNER MANAGEMENT - FRD-006 FR-69
    // ============================================================================

    /**
     * Find all partners with pagination
     */
    Page<Partner> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find partners by status
     */
    Page<Partner> findByStatusOrderByCreatedAtDesc(UserStatus status, Pageable pageable);

    /**
     * Search partners by business name or email
     */
    @Query("SELECT p FROM Partner p WHERE LOWER(p.businessName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Partner> searchPartners(@Param("search") String search, Pageable pageable);

    /**
     * Find partner by user ID
     */
    Optional<Partner> findByUserId(UUID userId);

    /**
     * Find active partners by category
     */
    @Query(value = "SELECT * FROM partners p WHERE p.status = 'ACTIVE' AND :category = ANY(p.categories)", nativeQuery = true)
    List<Partner> findActivePartnersByCategory(@Param("category") String category);
}
