package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.order.entity.Order;
import com.brandkit.order.entity.OrderPartnerAssignment;
import com.brandkit.order.entity.PartnerOrderStatus;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.partner.dto.SettlementDetailResponse;
import com.brandkit.partner.dto.SettlementDetailResponse.OrderBreakdown;
import com.brandkit.partner.dto.SettlementResponse;
import com.brandkit.partner.dto.SettlementResponse.*;
import com.brandkit.partner.entity.Settlement;
import com.brandkit.partner.entity.SettlementOrder;
import com.brandkit.partner.entity.SettlementStatus;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.SettlementOrderRepository;
import com.brandkit.partner.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Settlement Service - FRD-005 FR-60, FR-61, FR-62
 * Commission calculation and settlement management
 */
@Service
public class SettlementService {
    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    /**
     * Commission calculation result
     * FRD-005 FR-60: Commission Calculation
     */
    public static class CommissionDetails {
        private final BigDecimal productAmount;
        private final BigDecimal discountAmount;
        private final BigDecimal finalAmount;
        private final BigDecimal commissionPercentage;
        private final BigDecimal platformCommission;
        private final BigDecimal partnerEarnings;

        public CommissionDetails(
                BigDecimal productAmount,
                BigDecimal discountAmount,
                BigDecimal finalAmount,
                BigDecimal commissionPercentage,
                BigDecimal platformCommission,
                BigDecimal partnerEarnings) {
            this.productAmount = productAmount;
            this.discountAmount = discountAmount;
            this.finalAmount = finalAmount;
            this.commissionPercentage = commissionPercentage;
            this.platformCommission = platformCommission;
            this.partnerEarnings = partnerEarnings;
        }

        public BigDecimal getProductAmount() { return productAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public BigDecimal getCommissionPercentage() { return commissionPercentage; }
        public BigDecimal getPlatformCommission() { return platformCommission; }
        public BigDecimal getPartnerEarnings() { return partnerEarnings; }
    }

    @Autowired
    private SettlementRepository settlementRepository;
    @Autowired
    private SettlementOrderRepository settlementOrderRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private PartnerNotificationService notificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Get settlement dashboard
     * FRD-005 FR-61: Commission Settlement Dashboard
     */
    @Transactional(readOnly = true)
    public SettlementResponse getSettlementDashboard(UUID partnerId, int page, int size) {
        log.debug("Getting settlement dashboard for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        // Build summary
        SettlementSummary summary = buildSettlementSummary(partnerId);

        // Get settlements list
        Page<Settlement> settlements = settlementRepository
                .findByPartnerIdOrderByCreatedAtDesc(partnerId, PageRequest.of(page, size));

        List<SettlementDto> settlementDtos = settlements.getContent().stream()
                .map(this::mapToSettlementDto)
                .collect(Collectors.toList());

        return SettlementResponse.builder()
                .summary(summary)
                .settlements(settlementDtos)
                .page(page)
                .size(size)
                .totalElements(settlements.getTotalElements())
                .totalPages(settlements.getTotalPages())
                .build();
    }

    /**
     * Get settlement details with order breakdown
     */
    @Transactional(readOnly = true)
    public SettlementDetailResponse getSettlementDetails(UUID partnerId, UUID settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new PartnerException("Settlement not found"));

        if (!settlement.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied");
        }

        List<SettlementOrder> orders = settlementOrderRepository.findBySettlementId(settlementId);

        List<OrderBreakdown> orderBreakdowns = orders.stream()
                .map(this::mapToOrderBreakdown)
                .collect(Collectors.toList());

        return SettlementDetailResponse.builder()
                .settlementId(settlement.getId().toString())
                .settlementNumber(settlement.getSettlementNumber())
                .periodStart(settlement.getPeriodStart().format(DATE_FORMATTER))
                .periodEnd(settlement.getPeriodEnd().format(DATE_FORMATTER))
                .totalOrders(settlement.getTotalOrders())
                .totalProductAmount(settlement.getTotalProductAmount())
                .totalPlatformCommission(settlement.getTotalPlatformCommission())
                .totalPartnerEarnings(settlement.getTotalPartnerEarnings())
                .status(settlement.getStatus().name())
                .paymentDate(settlement.getPaymentCompletedAt() != null ?
                        settlement.getPaymentCompletedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
                .paymentReference(settlement.getPaymentReference())
                .statementUrl(settlement.getStatementUrl())
                .orders(orderBreakdowns)
                .build();
    }

    /**
     * Calculate commission for an order
     * FRD-005 FR-60: Commission Calculation
     */
    public CommissionDetails calculateCommission(Order order, Partner partner) {
        BigDecimal productAmount = order.getOriginalSubtotal();
        BigDecimal discountAmount = order.getTotalDiscount() != null ? order.getTotalDiscount() : BigDecimal.ZERO;
        BigDecimal finalAmount = productAmount.subtract(discountAmount);

        // Get commission rate based on order value
        BigDecimal commissionPercentage = getCommissionRate(finalAmount, partner);

        BigDecimal platformCommission = finalAmount.multiply(commissionPercentage)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal partnerEarnings = finalAmount.subtract(platformCommission);

        return new CommissionDetails(
                productAmount,
                discountAmount,
                finalAmount,
                commissionPercentage,
                platformCommission,
                partnerEarnings
        );
    }

    private BigDecimal getCommissionRate(BigDecimal amount, Partner partner) {
        // Use partner's configured rate, or default tiered rates
        if (partner.getCommissionRate() != null) {
            return partner.getCommissionRate();
        }

        // Tiered commission: 10% for <10k, 12% for 10k-50k, 15% for >50k
        if (amount.compareTo(new BigDecimal(10000)) < 0) {
            return new BigDecimal("10.00");
        } else if (amount.compareTo(new BigDecimal(50000)) < 0) {
            return new BigDecimal("12.00");
        } else {
            return new BigDecimal("15.00");
        }
    }

    private SettlementSummary buildSettlementSummary(UUID partnerId) {
        // Total earnings all time
        BigDecimal totalEarnings = settlementRepository
                .sumPartnerEarningsByPartnerIdAndStatus(partnerId, SettlementStatus.COMPLETED);
        if (totalEarnings == null) totalEarnings = BigDecimal.ZERO;

        // Pending settlement (delivered orders not yet settled)
        BigDecimal pendingSettlement = calculatePendingSettlement(partnerId);

        // Last settlement
        Settlement lastSettlement = settlementRepository
                .findFirstByPartnerIdAndStatusOrderByCreatedAtDesc(partnerId, SettlementStatus.COMPLETED)
                .orElse(null);

        LastSettlement lastSettlementInfo = null;
        if (lastSettlement != null) {
            lastSettlementInfo = LastSettlement.builder()
                    .amount(lastSettlement.getTotalPartnerEarnings())
                    .date(lastSettlement.getPaymentCompletedAt() != null ?
                            lastSettlement.getPaymentCompletedAt().format(DATE_FORMATTER) : null)
                    .build();
        }

        // Next settlement date (5th of next month)
        LocalDate nextSettlementDate = LocalDate.now().withDayOfMonth(1).plusMonths(1).withDayOfMonth(5);

        return SettlementSummary.builder()
                .totalEarningsAllTime(totalEarnings)
                .pendingSettlement(pendingSettlement)
                .lastSettlement(lastSettlementInfo)
                .nextSettlementDate(nextSettlementDate.format(DATE_FORMATTER))
                .build();
    }

    private BigDecimal calculatePendingSettlement(UUID partnerId) {
        // Find delivered orders that haven't been included in a settlement
        List<OrderPartnerAssignment> deliveredOrders = assignmentRepository
                .findByPartnerIdAndStatus(partnerId, PartnerOrderStatus.DELIVERED);

        BigDecimal pending = BigDecimal.ZERO;
        Partner partner = partnerRepository.findById(partnerId).orElse(null);

        for (OrderPartnerAssignment assignment : deliveredOrders) {
            // Check if order is already in a settlement
            if (!settlementOrderRepository.existsByOrderId(assignment.getOrder().getId())) {
                CommissionDetails commission = calculateCommission(assignment.getOrder(), partner);
                pending = pending.add(commission.getPartnerEarnings());
            }
        }

        return pending;
    }

    private SettlementDto mapToSettlementDto(Settlement settlement) {
        String period = settlement.getPeriodStart().format(MONTH_FORMATTER);

        return SettlementDto.builder()
                .settlementId(settlement.getId().toString())
                .settlementNumber(settlement.getSettlementNumber())
                .period(period)
                .orderCount(settlement.getTotalOrders())
                .totalAmount(settlement.getTotalPartnerEarnings())
                .status(settlement.getStatus().name())
                .date(settlement.getPaymentCompletedAt() != null ?
                        settlement.getPaymentCompletedAt().format(DATE_FORMATTER) :
                        settlement.getCreatedAt().format(DATE_FORMATTER))
                .statementUrl(settlement.getStatementUrl())
                .build();
    }

    private OrderBreakdown mapToOrderBreakdown(SettlementOrder so) {
        Order order = so.getOrder();
        String productName = order.getItems().isEmpty() ? "N/A" : order.getItems().get(0).getProductName();
        int quantity = order.getTotalQuantity();

        return OrderBreakdown.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .productName(productName)
                .quantity(quantity)
                .productAmount(so.getProductAmount())
                .discountAmount(so.getDiscountAmount())
                .finalAmount(so.getFinalAmount())
                .commissionPercentage(so.getCommissionPercentage())
                .platformCommission(so.getPlatformCommission())
                .partnerEarnings(so.getPartnerEarnings())
                .deliveredDate(order.getActualDeliveryDate() != null ?
                        order.getActualDeliveryDate().format(DATE_FORMATTER) : null)
                .build();
    }
}
