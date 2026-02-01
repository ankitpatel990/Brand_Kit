package com.brandkit.partner.repository;

import com.brandkit.partner.entity.PartnerPerformanceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PartnerPerformanceMetrics entity - FRD-005 FR-63
 */
@Repository
public interface PartnerPerformanceMetricsRepository extends JpaRepository<PartnerPerformanceMetrics, UUID> {

    /**
     * Find metrics by partner ID
     */
    Optional<PartnerPerformanceMetrics> findByPartnerId(UUID partnerId);

    /**
     * Calculate platform average fulfillment rate
     */
    @Query("SELECT AVG(m.fulfillmentRate) FROM PartnerPerformanceMetrics m WHERE m.totalOrdersAssigned > 0")
    BigDecimal calculatePlatformAverageFulfillmentRate();

    /**
     * Calculate platform average lead time
     */
    @Query("SELECT AVG(m.averageLeadTimeDays) FROM PartnerPerformanceMetrics m WHERE m.totalOrdersFulfilled > 0")
    BigDecimal calculatePlatformAverageLeadTime();
}
