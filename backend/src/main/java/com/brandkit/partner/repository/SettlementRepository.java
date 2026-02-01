package com.brandkit.partner.repository;

import com.brandkit.partner.entity.Settlement;
import com.brandkit.partner.entity.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Settlement entity - FRD-005 FR-61
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    /**
     * Find settlement by settlement number
     */
    Optional<Settlement> findBySettlementNumber(String settlementNumber);

    /**
     * Find settlements by partner ID
     */
    Page<Settlement> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find settlements by partner ID and status
     */
    List<Settlement> findByPartnerIdAndStatusOrderByCreatedAtDesc(UUID partnerId, SettlementStatus status);

    /**
     * Find latest settlement for partner
     */
    Optional<Settlement> findFirstByPartnerIdAndStatusOrderByCreatedAtDesc(UUID partnerId, SettlementStatus status);

    /**
     * Find pending settlements for processing
     */
    List<Settlement> findByStatusOrderByCreatedAtAsc(SettlementStatus status);

    /**
     * Calculate total earnings for partner
     */
    @Query("SELECT COALESCE(SUM(s.totalPartnerEarnings), 0) FROM Settlement s WHERE s.partner.id = :partnerId AND s.status = :status")
    BigDecimal sumPartnerEarningsByPartnerIdAndStatus(@Param("partnerId") UUID partnerId, @Param("status") SettlementStatus status);

    /**
     * Find settlement for period
     */
    Optional<Settlement> findByPartnerIdAndPeriodStartAndPeriodEnd(UUID partnerId, LocalDate periodStart, LocalDate periodEnd);

    // ============================================================================
    // ADMIN DASHBOARD QUERIES - FRD-006 FR-67
    // ============================================================================

    /**
     * Count settlements by status (string for flexibility)
     */
    @Query("SELECT COUNT(s) FROM Settlement s WHERE CAST(s.status AS string) = :status")
    Long countByStatus(@Param("status") String status);

    // ============================================================================
    // ADMIN SETTLEMENT MANAGEMENT - FRD-006 FR-73
    // ============================================================================

    /**
     * Find all settlements with pagination
     */
    Page<Settlement> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find settlements by status with pagination
     */
    Page<Settlement> findByStatusOrderByCreatedAtDesc(SettlementStatus status, Pageable pageable);

    /**
     * Sum total completed settlements for current month
     */
    @Query("SELECT COALESCE(SUM(s.totalPartnerEarnings), 0) FROM Settlement s WHERE s.status = 'COMPLETED' AND s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedSettlementsInPeriod(@Param("startDate") java.time.ZonedDateTime startDate, @Param("endDate") java.time.ZonedDateTime endDate);
}
