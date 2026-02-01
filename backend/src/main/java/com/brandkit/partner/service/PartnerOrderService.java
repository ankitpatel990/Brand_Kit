package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.order.entity.*;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.order.repository.OrderRepository;
import com.brandkit.order.repository.OrderStatusHistoryRepository;
import com.brandkit.partner.dto.*;
import com.brandkit.partner.dto.PartnerOrderResponse.*;
import com.brandkit.partner.entity.NotificationType;
import com.brandkit.partner.entity.ProofImage;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.ProofImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Partner Order Service - FRD-005 FR-54, FR-55, FR-56, FR-57
 * Order management for partners
 */
@Service
public class PartnerOrderService {
    private static final Logger log = LoggerFactory.getLogger(PartnerOrderService.class);

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;
    @Autowired
    private ProofImageRepository proofImageRepository;
    @Autowired
    private PartnerNotificationService notificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Get paginated order list for partner
     * FRD-005 FR-54: Order List View
     */
    @Transactional(readOnly = true)
    public PartnerOrderListResponse getOrders(UUID partnerId, String status, int page, int size) {
        log.debug("Getting orders for partner: {}, status: {}", partnerId, status);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderPartnerAssignment> assignments;

        if (status != null && !status.isEmpty()) {
            PartnerOrderStatus partnerStatus = PartnerOrderStatus.valueOf(status.toUpperCase());
            assignments = assignmentRepository.findByPartnerIdAndStatusOrderByCreatedAtDesc(partnerId, partnerStatus, pageable);
        } else {
            assignments = assignmentRepository.findByPartnerIdOrderByCreatedAtDesc(partnerId, pageable);
        }

        List<PartnerOrderListResponse.OrderSummary> orders = assignments.getContent().stream()
                .map(this::mapToOrderSummary)
                .collect(Collectors.toList());

        return PartnerOrderListResponse.builder()
                .orders(orders)
                .page(page)
                .size(size)
                .totalElements(assignments.getTotalElements())
                .totalPages(assignments.getTotalPages())
                .build();
    }

    private PartnerOrderListResponse.OrderSummary mapToOrderSummary(OrderPartnerAssignment assignment) {
        Order order = assignment.getOrder();
        String productName = order.getItems().isEmpty() ? "N/A" : order.getItems().get(0).getProductName();

        // Mask client name: "Rajesh Kumar" → "Rajesh K."
        String clientName = maskClientName(order.getUser().getFullName());

        // Calculate partner earnings
        BigDecimal partnerEarnings = calculatePartnerEarnings(order, assignment.getPartner());

        // Get available actions
        List<String> actions = getAvailableActions(assignment.getStatus());

        return PartnerOrderListResponse.OrderSummary.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .clientName(clientName)
                .productName(productName)
                .quantity(order.getTotalQuantity())
                .orderDate(order.getCreatedAt().format(DATE_FORMATTER))
                .expectedShipDate(order.getEstimatedDeliveryStart() != null ?
                        order.getEstimatedDeliveryStart().format(DATE_FORMATTER) : null)
                .status(order.getStatus().name())
                .partnerStatus(assignment.getStatus().name())
                .partnerEarnings(partnerEarnings)
                .actions(actions)
                .build();
    }

    /**
     * Get order details for partner
     * FRD-005 FR-55: Order Details View
     */
    @Transactional(readOnly = true)
    public PartnerOrderResponse getOrderDetails(UUID partnerId, UUID orderId) {
        log.debug("Getting order details for partner: {}, order: {}", partnerId, orderId);

        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PartnerException("Order not found"));

        // Verify partner owns this order
        if (!assignment.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied - order not assigned to you");
        }

        return mapToOrderResponse(assignment);
    }

    private PartnerOrderResponse mapToOrderResponse(OrderPartnerAssignment assignment) {
        Order order = assignment.getOrder();
        Partner partner = assignment.getPartner();
        boolean isAccepted = assignment.getStatus() != PartnerOrderStatus.AWAITING_ACCEPTANCE;

        // Product details
        ProductDetails product = buildProductDetails(order);

        // Delivery info (full address only after acceptance)
        DeliveryInfo delivery = buildDeliveryInfo(order, isAccepted);

        // Commission info
        CommissionInfo commission = buildCommissionInfo(order, partner);

        // Status timeline
        List<StatusHistoryItem> timeline = buildTimeline(order);

        // Proof images
        List<ProofImageDto> proofs = getProofImages(order.getId());

        // Available actions
        List<String> actions = getAvailableActions(assignment.getStatus());

        return PartnerOrderResponse.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getCreatedAt().format(DATETIME_FORMATTER))
                .status(order.getStatus().name())
                .partnerStatus(assignment.getStatus().name())
                .expectedShipDate(order.getEstimatedDeliveryStart() != null ?
                        order.getEstimatedDeliveryStart().format(DATE_FORMATTER) : null)
                .product(product)
                .delivery(delivery)
                .commission(commission)
                .timeline(timeline)
                .proofs(proofs)
                .actions(actions)
                .notes(assignment.getInternalNotes())
                .build();
    }

    /**
     * Accept order
     * FRD-005 FR-56: Order Acceptance
     */
    @Transactional
    public PartnerOrderResponse acceptOrder(UUID partnerId, UUID orderId) {
        log.info("Partner {} accepting order {}", partnerId, orderId);

        OrderPartnerAssignment assignment = getAndValidateAssignment(partnerId, orderId);

        if (assignment.getStatus() != PartnerOrderStatus.AWAITING_ACCEPTANCE) {
            throw new PartnerException("Order cannot be accepted - current status: " + assignment.getStatus());
        }

        // Accept order
        assignment.accept();
        assignmentRepository.save(assignment);

        // Update main order status
        Order order = assignment.getOrder();
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        // Add status history
        addStatusHistory(order, OrderStatus.ACCEPTED, "Order accepted by partner");

        // TODO: Send notification to client (without partner name)
        log.info("Order {} accepted by partner {}", orderId, partnerId);

        return mapToOrderResponse(assignment);
    }

    /**
     * Reject order
     * FRD-005 FR-56: Order Rejection
     */
    @Transactional
    public void rejectOrder(UUID partnerId, UUID orderId, OrderActionRequest request) {
        log.info("Partner {} rejecting order {} with reason: {}", partnerId, orderId, request.getReason());

        OrderPartnerAssignment assignment = getAndValidateAssignment(partnerId, orderId);

        if (assignment.getStatus() != PartnerOrderStatus.AWAITING_ACCEPTANCE) {
            throw new PartnerException("Order cannot be rejected - current status: " + assignment.getStatus());
        }

        // Reject order
        assignment.reject(request.getReason());
        assignmentRepository.save(assignment);

        // Notify admin for reassignment (not client)
        log.info("Order {} rejected by partner {}, admin notified for reassignment", orderId, partnerId);
    }

    /**
     * Update production status
     * FRD-005 FR-57: Production Status Updates
     */
    @Transactional
    public PartnerOrderResponse updateStatus(UUID partnerId, UUID orderId, StatusUpdateRequest request) {
        log.info("Partner {} updating order {} status to: {}", partnerId, orderId, request.getStatus());

        OrderPartnerAssignment assignment = getAndValidateAssignment(partnerId, orderId);

        PartnerOrderStatus currentStatus = assignment.getStatus();
        PartnerOrderStatus newStatus = PartnerOrderStatus.valueOf(request.getStatus().toUpperCase());

        // Validate status progression (cannot skip statuses)
        validateStatusProgression(currentStatus, newStatus);

        // Update status
        updatePartnerOrderStatus(assignment, newStatus);
        assignmentRepository.save(assignment);

        // Update main order status
        Order order = assignment.getOrder();
        OrderStatus orderStatus = mapToOrderStatus(newStatus);
        order.setStatus(orderStatus);
        orderRepository.save(order);

        // Add status history
        addStatusHistory(order, orderStatus, "Status updated by partner");

        // Send notification to client
        log.info("Order {} status updated to {} by partner {}", orderId, newStatus, partnerId);

        return mapToOrderResponse(assignment);
    }

    private OrderPartnerAssignment getAndValidateAssignment(UUID partnerId, UUID orderId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PartnerException("Order not found"));

        if (!assignment.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied - order not assigned to you");
        }

        return assignment;
    }

    private void validateStatusProgression(PartnerOrderStatus current, PartnerOrderStatus next) {
        // Define valid progressions
        boolean valid = switch (current) {
            case ACCEPTED -> next == PartnerOrderStatus.IN_PRODUCTION;
            case IN_PRODUCTION -> next == PartnerOrderStatus.READY_TO_SHIP;
            case READY_TO_SHIP -> next == PartnerOrderStatus.SHIPPED;
            case SHIPPED -> next == PartnerOrderStatus.DELIVERED;
            default -> false;
        };

        if (!valid) {
            throw new PartnerException("Invalid status progression: " + current + " → " + next);
        }
    }

    private void updatePartnerOrderStatus(OrderPartnerAssignment assignment, PartnerOrderStatus status) {
        switch (status) {
            case IN_PRODUCTION -> assignment.startProduction();
            case READY_TO_SHIP -> assignment.completeProduction();
            case SHIPPED -> assignment.ship();
            case DELIVERED -> assignment.deliver();
            default -> assignment.setStatus(status);
        }
    }

    private OrderStatus mapToOrderStatus(PartnerOrderStatus partnerStatus) {
        return switch (partnerStatus) {
            case ACCEPTED -> OrderStatus.ACCEPTED;
            case IN_PRODUCTION -> OrderStatus.IN_PRODUCTION;
            case READY_TO_SHIP -> OrderStatus.READY_TO_SHIP;
            case SHIPPED -> OrderStatus.SHIPPED;
            case DELIVERED -> OrderStatus.DELIVERED;
            default -> OrderStatus.CONFIRMED;
        };
    }

    private void addStatusHistory(Order order, OrderStatus status, String description) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setDescription(description);
        statusHistoryRepository.save(history);
    }

    private ProductDetails buildProductDetails(Order order) {
        if (order.getItems().isEmpty()) {
            return ProductDetails.builder().name("N/A").build();
        }

        OrderItem item = order.getItems().get(0);
        return ProductDetails.builder()
                .productId(item.getProduct().getId().toString())
                .name(item.getProductName())
                .category(item.getProduct().getCategory() != null ?
                        item.getProduct().getCategory().getDisplayName() : "N/A")
                .quantity(item.getQuantity())
                .customizationType(item.getCustomization() != null ?
                        item.getCustomization().getStatus() : null)
                .printReadyImageUrl(item.getPrintReadyImageUrl())
                .previewImageUrl(item.getPreviewImageUrl())
                .specifications(item.getProduct().getShortDescription())
                .build();
    }

    private DeliveryInfo buildDeliveryInfo(Order order, boolean isAccepted) {
        Address address = order.getDeliveryAddress();

        return DeliveryInfo.builder()
                .city(address.getCity())
                .state(address.getState())
                .pinCode(address.getPinCode())
                .fullAddress(isAccepted ? buildFullAddress(address) : null)
                .deliveryOption(order.getDeliveryOption().name())
                .addressRevealed(isAccepted)
                .build();
    }

    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getAddressLine1());
        if (address.getAddressLine2() != null) {
            sb.append(", ").append(address.getAddressLine2());
        }
        sb.append(", ").append(address.getCity());
        sb.append(", ").append(address.getState());
        sb.append(" - ").append(address.getPinCode());
        return sb.toString();
    }

    private CommissionInfo buildCommissionInfo(Order order, Partner partner) {
        BigDecimal productAmount = order.getOriginalSubtotal();
        BigDecimal discountAmount = order.getTotalDiscount();
        BigDecimal finalAmount = order.getSubtotal();
        BigDecimal commissionPercentage = partner.getCommissionRate();
        BigDecimal platformCommission = finalAmount.multiply(commissionPercentage)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal partnerEarnings = finalAmount.subtract(platformCommission);

        return CommissionInfo.builder()
                .productAmount(productAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .commissionPercentage(commissionPercentage)
                .platformCommission(platformCommission)
                .partnerEarnings(partnerEarnings)
                .build();
    }

    private List<StatusHistoryItem> buildTimeline(Order order) {
        return order.getStatusHistory().stream()
                .map(h -> StatusHistoryItem.builder()
                        .status(h.getStatus().name())
                        .description(h.getDescription())
                        .timestamp(h.getCreatedAt().format(DATETIME_FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProofImageDto> getProofImages(UUID orderId) {
        return proofImageRepository.findByOrderIdOrderByDisplayOrderAsc(orderId).stream()
                .map(img -> ProofImageDto.builder()
                        .id(img.getId().toString())
                        .imageUrl(img.getImageUrl())
                        .caption(img.getCaption())
                        .uploadedAt(img.getCreatedAt().format(DATETIME_FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }

    private List<String> getAvailableActions(PartnerOrderStatus status) {
        return switch (status) {
            case AWAITING_ACCEPTANCE -> List.of("ACCEPT", "REJECT");
            case ACCEPTED -> List.of("START_PRODUCTION");
            case IN_PRODUCTION -> List.of("UPLOAD_PROOF", "MARK_READY_TO_SHIP");
            case READY_TO_SHIP -> List.of("MARK_SHIPPED");
            case SHIPPED -> List.of("VIEW_TRACKING");
            case DELIVERED -> List.of();
            default -> List.of();
        };
    }

    private String maskClientName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "Client";
        }
        String[] parts = fullName.split(" ");
        if (parts.length == 1) {
            return parts[0];
        }
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + ".";
    }

    private BigDecimal calculatePartnerEarnings(Order order, Partner partner) {
        BigDecimal commission = order.getSubtotal()
                .multiply(partner.getCommissionRate())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return order.getSubtotal().subtract(commission);
    }
}
