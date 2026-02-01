package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.order.entity.*;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.order.repository.OrderRepository;
import com.brandkit.order.repository.OrderStatusHistoryRepository;
import com.brandkit.partner.dto.ShipmentRequest;
import com.brandkit.partner.entity.Shipment;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Shipment Service - FRD-005 FR-59
 * Shipment creation and tracking
 */
@Service
public class ShipmentService {
    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;

    /**
     * Create shipment and mark order as shipped
     * FRD-005 FR-59: Shipment Creation
     */
    @Transactional
    public Shipment createShipment(UUID partnerId, UUID orderId, ShipmentRequest request) {
        log.info("Partner {} marking order {} as shipped with tracking {}", partnerId, orderId, request.getTrackingId());

        // Validate assignment
        OrderPartnerAssignment assignment = getAndValidateAssignment(partnerId, orderId);

        // Validate status (must be READY_TO_SHIP)
        if (assignment.getStatus() != PartnerOrderStatus.READY_TO_SHIP) {
            throw new PartnerException("Order must be in READY_TO_SHIP status. Current: " + assignment.getStatus());
        }

        // Check if shipment already exists
        if (shipmentRepository.existsByOrderId(orderId)) {
            throw new PartnerException("Shipment already exists for this order");
        }

        Order order = assignment.getOrder();

        // Create shipment
        Shipment shipment = Shipment.builder()
                .order(order)
                .partner(assignment.getPartner())
                .courierName(request.getCourierName())
                .trackingId(request.getTrackingId())
                .shipDate(request.getShipDate())
                .weightKg(request.getWeightKg())
                .numPackages(request.getNumPackages() != null ? request.getNumPackages() : 1)
                .notes(request.getNotes())
                .build();

        // Generate tracking URL
        String trackingUrl = shipment.generateTrackingUrl();
        shipment.setTrackingUrl(trackingUrl);

        shipmentRepository.save(shipment);

        // Update assignment status
        assignment.ship();
        assignmentRepository.save(assignment);

        // Update main order
        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingId(request.getTrackingId());
        order.setCourierName(request.getCourierName());
        order.setTrackingUrl(trackingUrl);
        orderRepository.save(order);

        // Add status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.SHIPPED);
        history.setDescription("Order shipped via " + request.getCourierName() + ". Tracking ID: " + request.getTrackingId());
        statusHistoryRepository.save(history);

        // TODO: Send notification to client with tracking link

        log.info("Order {} marked as shipped with tracking ID {}", orderId, request.getTrackingId());
        return shipment;
    }

    /**
     * Get shipment details
     */
    @Transactional(readOnly = true)
    public Shipment getShipment(UUID partnerId, UUID orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PartnerException("Shipment not found"));

        if (!shipment.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied");
        }

        return shipment;
    }

    /**
     * Update tracking info (for webhook updates)
     */
    @Transactional
    public Shipment updateTrackingStatus(String trackingId, String status) {
        log.info("Updating tracking status for {}: {}", trackingId, status);

        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new PartnerException("Shipment not found"));

        // Update last tracking update time
        shipment.setLastTrackingUpdate(java.time.OffsetDateTime.now());
        shipmentRepository.save(shipment);

        // If delivered, update order and assignment
        if ("DELIVERED".equalsIgnoreCase(status)) {
            Order order = shipment.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            order.setActualDeliveryDate(java.time.LocalDate.now());
            orderRepository.save(order);

            OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(order.getId())
                    .orElse(null);
            if (assignment != null) {
                assignment.deliver();
                assignmentRepository.save(assignment);
            }

            // Add status history
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(OrderStatus.DELIVERED);
            history.setDescription("Order delivered");
            statusHistoryRepository.save(history);
        }

        return shipment;
    }

    private OrderPartnerAssignment getAndValidateAssignment(UUID partnerId, UUID orderId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PartnerException("Order not found"));

        if (!assignment.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied - order not assigned to you");
        }

        return assignment;
    }
}
