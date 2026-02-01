package com.brandkit.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.dto.DashboardSummaryResponse;
import com.brandkit.admin.dto.DashboardSummaryResponse.*;
import com.brandkit.admin.entity.AdminNotification;
import com.brandkit.admin.repository.AdminNotificationRepository;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.order.repository.OrderRepository;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.partner.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for admin dashboard data
 * 
 * FRD-006 FR-67: Admin Dashboard Home
 */
@Service
public class AdminDashboardService {
    private static final Logger log = LoggerFactory.getLogger(AdminDashboardService.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    @Autowired
    private SettlementRepository settlementRepository;
    
    @Autowired
    private AdminNotificationRepository notificationRepository;

    /**
     * Get comprehensive dashboard summary
     */
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime startOfLastMonth = startOfMonth.minusMonths(1);

        return DashboardSummaryResponse.builder()
                .metrics(getMetricsData(now, startOfMonth, startOfLastMonth))
                .charts(getChartsData(now))
                .recentActivity(getRecentActivity())
                .alerts(getAlerts())
                .build();
    }

    /**
     * Get metrics cards data
     */
    private MetricsData getMetricsData(ZonedDateTime now, ZonedDateTime startOfMonth, ZonedDateTime startOfLastMonth) {
        // This month's revenue and orders
        BigDecimal thisMonthRevenue = orderRepository.sumTotalAmountByDateRange(startOfMonth, now)
                .orElse(BigDecimal.ZERO);
        Long thisMonthOrders = orderRepository.countByDateRange(startOfMonth, now);

        // Last month's revenue and orders (for comparison)
        BigDecimal lastMonthRevenue = orderRepository.sumTotalAmountByDateRange(startOfLastMonth, startOfMonth)
                .orElse(BigDecimal.ZERO);
        Long lastMonthOrders = orderRepository.countByDateRange(startOfLastMonth, startOfMonth);

        // Calculate change percentages
        Double revenueChange = calculatePercentChange(lastMonthRevenue, thisMonthRevenue);
        Double ordersChange = calculatePercentChange(lastMonthOrders, thisMonthOrders);

        // Active counts
        Long activeUsers = userRepository.countByStatusActive();
        Long activePartners = partnerRepository.countByIsActiveTrue();
        Long pendingOrders = orderRepository.countPendingOrders();
        Long pendingSettlements = settlementRepository.countByStatus("PENDING");

        return MetricsData.builder()
                .totalRevenueThisMonth(thisMonthRevenue)
                .revenueChangePercent(revenueChange)
                .totalOrdersThisMonth(thisMonthOrders)
                .ordersChangePercent(ordersChange)
                .activeUsers(activeUsers)
                .activePartners(activePartners)
                .pendingOrders(pendingOrders)
                .pendingSettlements(pendingSettlements)
                .build();
    }

    /**
     * Get charts data (30-day trends, status distribution)
     */
    private ChartsData getChartsData(ZonedDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Revenue trend (last 30 days)
        List<TrendDataPoint> revenueTrend = orderRepository.getDailyRevenueTrend(thirtyDaysAgo, today)
                .stream()
                .map(row -> TrendDataPoint.builder()
                        .date(((java.sql.Date) row[0]).toLocalDate().format(formatter))
                        .value((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());

        // Orders trend (last 30 days)
        List<TrendDataPoint> ordersTrend = orderRepository.getDailyOrdersTrend(thirtyDaysAgo, today)
                .stream()
                .map(row -> TrendDataPoint.builder()
                        .date(((java.sql.Date) row[0]).toLocalDate().format(formatter))
                        .value(BigDecimal.valueOf((Long) row[1]))
                        .build())
                .collect(Collectors.toList());

        // Order status distribution
        Map<String, Long> statusDistribution = orderRepository.getOrderStatusDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return ChartsData.builder()
                .revenueTrend(revenueTrend)
                .ordersTrend(ordersTrend)
                .orderStatusDistribution(statusDistribution)
                .build();
    }

    /**
     * Get recent activity
     */
    private RecentActivity getRecentActivity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Recent orders
        List<RecentOrder> recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(order -> RecentOrder.builder()
                        .orderId(order.getOrderNumber())
                        .clientName(order.getUser().getFullName())
                        .amount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .createdAt(order.getCreatedAt().format(formatter))
                        .build())
                .collect(Collectors.toList());

        // Recent registrations
        List<RecentUser> recentRegistrations = userRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(user -> RecentUser.builder()
                        .name(user.getFullName())
                        .email(user.getEmail())
                        .registeredAt(user.getCreatedAt().format(formatter))
                        .build())
                .collect(Collectors.toList());

        // Recent partner actions (from audit log or notifications)
        List<RecentPartnerAction> recentPartnerActions = new ArrayList<>();
        // This would be populated from audit logs or partner activity table

        return RecentActivity.builder()
                .recentOrders(recentOrders)
                .recentRegistrations(recentRegistrations)
                .recentPartnerActions(recentPartnerActions)
                .build();
    }

    /**
     * Get active alerts
     */
    private List<AlertItem> getAlerts() {
        List<AlertItem> alerts = new ArrayList<>();

        // Pending order rejections
        Long rejectedOrders = orderRepository.countByStatusRequiringReassignment();
        if (rejectedOrders > 0) {
            alerts.add(AlertItem.builder()
                    .type("ORDER_REJECTION")
                    .priority("CRITICAL")
                    .message(rejectedOrders + " orders requiring reassignment")
                    .link("/admin/orders?status=PARTNER_REJECTED")
                    .count(rejectedOrders.intValue())
                    .build());
        }

        // Failed settlements
        Long failedSettlements = settlementRepository.countByStatus("FAILED");
        if (failedSettlements > 0) {
            alerts.add(AlertItem.builder()
                    .type("SETTLEMENT_FAILED")
                    .priority("HIGH")
                    .message(failedSettlements + " failed settlements")
                    .link("/admin/settlements?status=FAILED")
                    .count(failedSettlements.intValue())
                    .build());
        }

        // Low performing partners
        Long lowPerformingPartners = partnerRepository.countLowPerformingPartners(85.0);
        if (lowPerformingPartners > 0) {
            alerts.add(AlertItem.builder()
                    .type("LOW_PERFORMANCE")
                    .priority("MEDIUM")
                    .message(lowPerformingPartners + " partners with low fulfillment rate")
                    .link("/admin/partners?performance=LOW")
                    .count(lowPerformingPartners.intValue())
                    .build());
        }

        // Pending bank verifications
        Long pendingBankVerifications = partnerRepository.countByBankVerifiedFalse();
        if (pendingBankVerifications > 0) {
            alerts.add(AlertItem.builder()
                    .type("BANK_VERIFICATION")
                    .priority("MEDIUM")
                    .message(pendingBankVerifications + " partners awaiting bank verification")
                    .link("/admin/partners?bankVerified=false")
                    .count(pendingBankVerifications.intValue())
                    .build());
        }

        return alerts;
    }

    /**
     * Calculate percentage change
     */
    private Double calculatePercentChange(Number oldValue, Number newValue) {
        if (oldValue == null || oldValue.doubleValue() == 0) {
            return newValue.doubleValue() > 0 ? 100.0 : 0.0;
        }
        double change = ((newValue.doubleValue() - oldValue.doubleValue()) / oldValue.doubleValue()) * 100;
        return BigDecimal.valueOf(change).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
