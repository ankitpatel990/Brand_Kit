package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.order.entity.OrderPartnerAssignment;
import com.brandkit.order.entity.PartnerOrderStatus;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.partner.dto.PartnerDashboardResponse;
import com.brandkit.partner.dto.PartnerDashboardResponse.*;
import com.brandkit.partner.entity.DiscountStatus;
import com.brandkit.partner.entity.SettlementStatus;
import com.brandkit.partner.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Partner Dashboard Service - FRD-005 FR-52
 * Dashboard overview with key metrics
 */
@Service
public class PartnerDashboardService {
    private static final Logger log = LoggerFactory.getLogger(PartnerDashboardService.class);

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private PartnerNotificationRepository notificationRepository;
    @Autowired
    private PartnerDiscountRepository discountRepository;
    @Autowired
    private SettlementRepository settlementRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Get partner dashboard summary
     * FRD-005 FR-52: Partner Dashboard Home
     */
    @Transactional(readOnly = true)
    public PartnerDashboardResponse getDashboard(UUID partnerId) {
        log.debug("Getting dashboard for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        // Build summary cards
        SummaryCards summary = buildSummaryCards(partnerId);

        // Get recent orders
        List<RecentOrderDto> recentOrders = getRecentOrders(partnerId);

        // Get alerts
        List<AlertDto> alerts = buildAlerts(partnerId);

        // Get discount summary
        DiscountSummary discountStatus = buildDiscountSummary(partnerId);

        // Get unread notification count
        long unreadNotifications = notificationRepository.countByPartnerIdAndIsReadFalse(partnerId);

        return PartnerDashboardResponse.builder()
                .summary(summary)
                .recentOrders(recentOrders)
                .alerts(alerts)
                .discountStatus(discountStatus)
                .unreadNotifications(unreadNotifications)
                .build();
    }

    private SummaryCards buildSummaryCards(UUID partnerId) {
        long pendingOrders = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.AWAITING_ACCEPTANCE);
        long activeOrders = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.IN_PRODUCTION);
        long readyToShip = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.READY_TO_SHIP);

        // Calculate this month's revenue from completed settlements
        BigDecimal revenueThisMonth = settlementRepository.sumPartnerEarningsByPartnerIdAndStatus(
                partnerId, SettlementStatus.COMPLETED);
        if (revenueThisMonth == null) {
            revenueThisMonth = BigDecimal.ZERO;
        }

        long activeDiscounts = discountRepository.countByPartnerIdAndStatus(partnerId, DiscountStatus.APPROVED);

        return SummaryCards.builder()
                .pendingOrders(pendingOrders)
                .activeOrders(activeOrders)
                .readyToShip(readyToShip)
                .revenueThisMonth(revenueThisMonth)
                .activeDiscounts(activeDiscounts)
                .build();
    }

    private List<RecentOrderDto> getRecentOrders(UUID partnerId) {
        List<OrderPartnerAssignment> assignments = assignmentRepository
                .findByPartnerIdOrderByCreatedAtDesc(partnerId, PageRequest.of(0, 10))
                .getContent();

        List<RecentOrderDto> recentOrders = new ArrayList<>();
        for (OrderPartnerAssignment assignment : assignments) {
            var order = assignment.getOrder();
            String productName = order.getItems().isEmpty() ? "N/A" : order.getItems().get(0).getProductName();
            int quantity = order.getTotalQuantity();

            // Calculate partner earnings (simplified - actual calculation in commission service)
            BigDecimal partnerEarnings = order.getSubtotal()
                    .multiply(BigDecimal.ONE.subtract(assignment.getPartner().getCommissionRate().divide(new BigDecimal(100))));

            recentOrders.add(RecentOrderDto.builder()
                    .orderId(order.getId().toString())
                    .orderNumber(order.getOrderNumber())
                    .productName(productName)
                    .quantity(quantity)
                    .status(assignment.getStatus().name())
                    .orderDate(order.getCreatedAt().format(DATE_FORMATTER))
                    .expectedShipDate(order.getEstimatedDeliveryStart() != null ?
                            order.getEstimatedDeliveryStart().format(DATE_FORMATTER) : null)
                    .partnerEarnings(partnerEarnings)
                    .build());
        }
        return recentOrders;
    }

    private List<AlertDto> buildAlerts(UUID partnerId) {
        List<AlertDto> alerts = new ArrayList<>();

        // Alert for pending orders
        long pendingCount = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.AWAITING_ACCEPTANCE);
        if (pendingCount > 0) {
            alerts.add(AlertDto.builder()
                    .type("ORDER_PENDING")
                    .title("Orders Awaiting Acceptance")
                    .message(pendingCount + " order(s) require your action")
                    .actionUrl("/partner/orders?status=AWAITING_ACCEPTANCE")
                    .build());
        }

        // Alert for orders ready to ship
        long readyToShipCount = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.READY_TO_SHIP);
        if (readyToShipCount > 0) {
            alerts.add(AlertDto.builder()
                    .type("READY_TO_SHIP")
                    .title("Ready to Ship")
                    .message(readyToShipCount + " order(s) are ready to be shipped")
                    .actionUrl("/partner/orders?status=READY_TO_SHIP")
                    .build());
        }

        // Alert for pending discounts
        long pendingDiscounts = discountRepository.countByPartnerIdAndStatus(partnerId, DiscountStatus.PENDING);
        if (pendingDiscounts > 0) {
            alerts.add(AlertDto.builder()
                    .type("DISCOUNT_PENDING")
                    .title("Pending Discount Approvals")
                    .message(pendingDiscounts + " discount(s) awaiting admin approval")
                    .actionUrl("/partner/discounts?status=PENDING")
                    .build());
        }

        return alerts;
    }

    private DiscountSummary buildDiscountSummary(UUID partnerId) {
        long active = discountRepository.countByPartnerIdAndStatus(partnerId, DiscountStatus.APPROVED);
        long pending = discountRepository.countByPartnerIdAndStatus(partnerId, DiscountStatus.PENDING);
        long disabled = discountRepository.countByPartnerIdAndStatus(partnerId, DiscountStatus.DISABLED);

        return DiscountSummary.builder()
                .active(active)
                .pending(pending)
                .disabled(disabled)
                .build();
    }
}
