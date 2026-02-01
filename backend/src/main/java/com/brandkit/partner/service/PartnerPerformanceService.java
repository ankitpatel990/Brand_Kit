package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.order.entity.OrderPartnerAssignment;
import com.brandkit.order.entity.PartnerOrderStatus;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.partner.dto.PerformanceMetricsResponse;
import com.brandkit.partner.dto.PerformanceMetricsResponse.*;
import com.brandkit.partner.entity.PartnerPerformanceMetrics;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.PartnerPerformanceMetricsRepository;
import com.brandkit.partner.repository.SettlementRepository;
import com.brandkit.partner.entity.SettlementStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Partner Performance Service - FRD-005 FR-63
 * Performance metrics and monitoring
 */
@Service
public class PartnerPerformanceService {
    private static final Logger log = LoggerFactory.getLogger(PartnerPerformanceService.class);

    @Autowired
    private PartnerPerformanceMetricsRepository metricsRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private SettlementRepository settlementRepository;
    @Autowired
    private PartnerNotificationService notificationService;

    private static final BigDecimal FULFILLMENT_THRESHOLD = new BigDecimal("85.00");
    private static final BigDecimal WARNING_THRESHOLD = new BigDecimal("90.00");

    /**
     * Get performance metrics for partner
     * FRD-005 FR-63: Partner Performance Metrics
     */
    @Transactional(readOnly = true)
    public PerformanceMetricsResponse getPerformanceMetrics(UUID partnerId, String period) {
        log.debug("Getting performance metrics for partner: {}, period: {}", partnerId, period);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        // Get or create metrics
        PartnerPerformanceMetrics metrics = metricsRepository.findByPartnerId(partnerId)
                .orElseGet(() -> createDefaultMetrics(partner));

        // Build response
        Metrics metricsDto = Metrics.builder()
                .fulfillmentRate(metrics.getFulfillmentRate())
                .averageLeadTime(metrics.getAverageLeadTimeDays())
                .deliverySuccessRate(metrics.getDeliverySuccessRate())
                .averageRating(metrics.getAverageRating())
                .totalOrdersFulfilled(metrics.getTotalOrdersFulfilled())
                .totalRevenue(metrics.getTotalRevenue())
                .totalOrdersAssigned(metrics.getTotalOrdersAssigned())
                .totalOrdersAccepted(metrics.getTotalOrdersAccepted())
                .totalOrdersRejected(metrics.getTotalOrdersRejected())
                .build();

        // Get platform benchmarks
        Benchmarks benchmarks = getBenchmarks();

        // Generate alerts
        List<AlertDto> alerts = generateAlerts(metrics, partner);

        return PerformanceMetricsResponse.builder()
                .period(period != null ? period : "All Time")
                .metrics(metricsDto)
                .benchmark(benchmarks)
                .alerts(alerts)
                .build();
    }

    /**
     * Recalculate performance metrics for a partner
     */
    @Transactional
    public void recalculateMetrics(UUID partnerId) {
        log.info("Recalculating metrics for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        PartnerPerformanceMetrics metrics = metricsRepository.findByPartnerId(partnerId)
                .orElseGet(() -> createDefaultMetrics(partner));

        // Count orders by status
        long assigned = getTotalAssignedOrders(partnerId);
        long accepted = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.ACCEPTED) +
                       assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.IN_PRODUCTION) +
                       assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.READY_TO_SHIP) +
                       assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.SHIPPED) +
                       assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.DELIVERED);
        long rejected = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.REJECTED);
        long delivered = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.DELIVERED);
        long shipped = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.SHIPPED) + delivered;

        metrics.setTotalOrdersAssigned((int) assigned);
        metrics.setTotalOrdersAccepted((int) accepted);
        metrics.setTotalOrdersRejected((int) rejected);
        metrics.setTotalOrdersFulfilled((int) shipped);
        metrics.setTotalOrdersDelivered((int) delivered);

        // Calculate fulfillment rate
        if (assigned > 0) {
            metrics.setFulfillmentRate(new BigDecimal(accepted)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(assigned), 2, RoundingMode.HALF_UP));
        }

        // Calculate delivery success rate
        if (shipped > 0) {
            metrics.setDeliverySuccessRate(new BigDecimal(delivered)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(shipped), 2, RoundingMode.HALF_UP));
        }

        // Calculate average lead time
        BigDecimal avgLeadTime = calculateAverageLeadTime(partnerId);
        metrics.setAverageLeadTimeDays(avgLeadTime);

        // Get total revenue from settlements
        BigDecimal totalRevenue = settlementRepository
                .sumPartnerEarningsByPartnerIdAndStatus(partnerId, SettlementStatus.COMPLETED);
        metrics.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        metrics.recalculate();
        metricsRepository.save(metrics);

        // Check for performance alerts
        checkPerformanceAlerts(metrics, partner);

        log.info("Metrics recalculated for partner {}: fulfillment={}%, leadTime={}d",
                partnerId, metrics.getFulfillmentRate(), metrics.getAverageLeadTimeDays());
    }

    private long getTotalAssignedOrders(UUID partnerId) {
        long total = 0;
        for (PartnerOrderStatus status : PartnerOrderStatus.values()) {
            total += assignmentRepository.countByPartnerIdAndStatus(partnerId, status);
        }
        return total;
    }

    private BigDecimal calculateAverageLeadTime(UUID partnerId) {
        // Get delivered orders and calculate average time from acceptance to shipping
        List<OrderPartnerAssignment> deliveredOrders = assignmentRepository
                .findByPartnerIdAndStatus(partnerId, PartnerOrderStatus.DELIVERED);

        if (deliveredOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long totalDays = 0;
        int count = 0;

        for (OrderPartnerAssignment assignment : deliveredOrders) {
            if (assignment.getAcceptedAt() != null && assignment.getShippedAt() != null) {
                Duration duration = Duration.between(assignment.getAcceptedAt(), assignment.getShippedAt());
                totalDays += duration.toDays();
                count++;
            }
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(totalDays).divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }

    private PartnerPerformanceMetrics createDefaultMetrics(Partner partner) {
        PartnerPerformanceMetrics metrics = PartnerPerformanceMetrics.builder()
                .partner(partner)
                .build();
        return metricsRepository.save(metrics);
    }

    private Benchmarks getBenchmarks() {
        BigDecimal avgFulfillment = metricsRepository.calculatePlatformAverageFulfillmentRate();
        BigDecimal avgLeadTime = metricsRepository.calculatePlatformAverageLeadTime();

        return Benchmarks.builder()
                .platformAverageFulfillment(avgFulfillment != null ? avgFulfillment : new BigDecimal("95.00"))
                .platformAverageLeadTime(avgLeadTime != null ? avgLeadTime : new BigDecimal("5.00"))
                .build();
    }

    private List<AlertDto> generateAlerts(PartnerPerformanceMetrics metrics, Partner partner) {
        List<AlertDto> alerts = new ArrayList<>();

        // Low fulfillment rate alert
        if (metrics.getFulfillmentRate().compareTo(FULFILLMENT_THRESHOLD) < 0) {
            alerts.add(AlertDto.builder()
                    .type("WARNING")
                    .message("Your fulfillment rate (" + metrics.getFulfillmentRate() +
                            "%) is below the minimum threshold (85%). Improve to avoid reduced order assignments.")
                    .build());
        } else if (metrics.getFulfillmentRate().compareTo(WARNING_THRESHOLD) < 0) {
            alerts.add(AlertDto.builder()
                    .type("INFO")
                    .message("Your fulfillment rate (" + metrics.getFulfillmentRate() +
                            "%) is below target (90%). Consider accepting more orders.")
                    .build());
        }

        // High lead time alert
        BigDecimal sladays = new BigDecimal(partner.getFulfillmentSlaDays());
        if (metrics.getAverageLeadTimeDays().compareTo(sladays) > 0) {
            alerts.add(AlertDto.builder()
                    .type("WARNING")
                    .message("Your average lead time (" + metrics.getAverageLeadTimeDays() +
                            " days) exceeds your SLA (" + partner.getFulfillmentSlaDays() + " days).")
                    .build());
        }

        return alerts;
    }

    private void checkPerformanceAlerts(PartnerPerformanceMetrics metrics, Partner partner) {
        if (metrics.getFulfillmentRate().compareTo(FULFILLMENT_THRESHOLD) < 0) {
            notificationService.createPerformanceAlertNotification(partner,
                    "Your fulfillment rate has dropped below 85%. You may receive fewer order assignments.");
        }
    }
}
