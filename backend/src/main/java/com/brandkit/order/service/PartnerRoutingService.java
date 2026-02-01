package com.brandkit.order.service;

import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.*;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for partner order routing (INTERNAL ONLY) - FRD-004 FR-45, Sub-Prompt 5
 * This service is NEVER exposed to clients - all operations are internal
 */
@Service
@Transactional
public class PartnerRoutingService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerRoutingService.class);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;
    @Autowired
    private OrderService orderService;

    /**
     * Route order to partner after payment confirmation
     * This is an INTERNAL process - client never sees partner details
     */
    public void routeOrderToPartner(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        
        if (order == null) {
            logger.error("Order not found for routing: {}", orderId);
            return;
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            logger.warn("Order {} is not confirmed, skipping routing", orderId);
            return;
        }

        Partner partner = order.getPartner();
        
        if (partner == null) {
            logger.error("No partner assigned to order {}", orderId);
            // Notify admin for manual intervention
            notifyAdminForManualRouting(order);
            return;
        }

        // Check partner status (internal)
        if (partner.getStatus() == null || !partner.getStatus().name().equals("ACTIVE")) {
            logger.warn("Partner {} is not active for order {}", partner.getId(), orderId);
            notifyAdminForManualRouting(order);
            return;
        }

        // Create partner assignment
        OrderPartnerAssignment assignment = new OrderPartnerAssignment();
        assignment.setOrder(order);
        assignment.setPartner(partner);
        assignment.setStatus(PartnerOrderStatus.AWAITING_ACCEPTANCE);
        assignmentRepository.save(assignment);

        logger.info("Order {} routed to partner {}", orderId, partner.getId());

        // Send notification to partner (via internal partner portal)
        sendPartnerNotification(order, partner);
    }

    /**
     * Partner accepts order (called from partner portal)
     */
    public void acceptOrder(UUID orderId, UUID partnerId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId).orElse(null);
        
        if (assignment == null || !assignment.getPartner().getId().equals(partnerId)) {
            logger.error("Invalid assignment for order {} and partner {}", orderId, partnerId);
            return;
        }

        assignment.accept();
        assignmentRepository.save(assignment);

        // Update order status (client sees "Processing" not "partner_accepted")
        orderService.updateOrderStatus(orderId, OrderStatus.ACCEPTED, 
                "Order accepted for fulfillment", 
                "Partner " + partnerId + " accepted order");

        logger.info("Order {} accepted by partner {}", orderId, partnerId);
    }

    /**
     * Partner rejects order (called from partner portal)
     */
    public void rejectOrder(UUID orderId, UUID partnerId, String reason) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId).orElse(null);
        
        if (assignment == null || !assignment.getPartner().getId().equals(partnerId)) {
            logger.error("Invalid assignment for order {} and partner {}", orderId, partnerId);
            return;
        }

        assignment.reject(reason);
        assignmentRepository.save(assignment);

        // Notify admin for manual reassignment
        notifyAdminForManualRouting(assignment.getOrder());

        logger.info("Order {} rejected by partner {} - Reason: {}", orderId, partnerId, reason);
    }

    /**
     * Partner starts production (called from partner portal)
     */
    public void startProduction(UUID orderId, UUID partnerId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId).orElse(null);
        
        if (assignment == null || !assignment.getPartner().getId().equals(partnerId)) {
            return;
        }

        assignment.startProduction();
        assignmentRepository.save(assignment);

        // Update order status (client sees "In Production")
        orderService.updateOrderStatus(orderId, OrderStatus.IN_PRODUCTION, 
                "Production started", 
                "Partner started production");

        logger.info("Production started for order {} by partner {}", orderId, partnerId);
    }

    /**
     * Partner marks order as ready to ship (called from partner portal)
     */
    public void markReadyToShip(UUID orderId, UUID partnerId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId).orElse(null);
        
        if (assignment == null || !assignment.getPartner().getId().equals(partnerId)) {
            return;
        }

        assignment.completeProduction();
        assignmentRepository.save(assignment);

        orderService.updateOrderStatus(orderId, OrderStatus.READY_TO_SHIP, 
                "Ready for dispatch", 
                "Partner marked ready to ship");

        logger.info("Order {} ready to ship by partner {}", orderId, partnerId);
    }

    /**
     * Partner ships order with tracking info (called from partner portal)
     */
    public void shipOrder(UUID orderId, UUID partnerId, String trackingId, String courierName, String trackingUrl) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId).orElse(null);
        
        if (assignment == null || !assignment.getPartner().getId().equals(partnerId)) {
            return;
        }

        assignment.ship();
        assignmentRepository.save(assignment);

        // Update order with tracking info
        Order order = assignment.getOrder();
        order.setTrackingId(trackingId);
        order.setCourierName(courierName);
        order.setTrackingUrl(trackingUrl);
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        logger.info("Order {} shipped by partner {} - Tracking: {}", orderId, partnerId, trackingId);

        // TODO: Send shipping notification to client (no partner details)
    }

    /**
     * Send notification to partner (internal)
     */
    private void sendPartnerNotification(Order order, Partner partner) {
        // TODO: Implement partner notification via email/SMS
        // This notification goes to internal partner portal only
        logger.info("Notification sent to partner {} for order {}", partner.getId(), order.getOrderNumber());
    }

    /**
     * Notify admin for manual routing (internal)
     */
    private void notifyAdminForManualRouting(Order order) {
        // TODO: Implement admin notification for manual intervention
        // Client only sees generic "Order processing" message
        logger.warn("Admin notification: Manual routing required for order {}", order.getOrderNumber());
    }
}
