package com.brandkit.order.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.order.dto.*;
import com.brandkit.order.entity.OrderStatus;
import com.brandkit.order.service.OrderService;
import com.brandkit.order.service.ReorderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for order management - FRD-004 FR-43, FR-46, FR-47, FR-50
 * Note: NO partner information is exposed in any response
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ReorderService reorderService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Create order from cart
     * POST /api/orders/create
     */
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody CheckoutRequest request) {
        User user = userPrincipal.getUser(userRepository);
        OrderResponse order = orderService.createOrder(user, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Get user's orders with pagination
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getOrders(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search) {
        User user = userPrincipal.getUser(userRepository);
        Pageable pageable = PageRequest.of(page, size);

        Page<OrderListResponse> orders;
        if (search != null && !search.isBlank()) {
            orders = orderService.searchOrders(user, search, pageable);
        } else if (status != null) {
            orders = orderService.getUserOrdersByStatus(user, status, pageable);
        } else {
            orders = orderService.getUserOrders(user, pageable);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID
     * GET /api/orders/:orderId
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID orderId) {
        User user = userPrincipal.getUser(userRepository);
        OrderResponse order = orderService.getOrder(user, orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Get order by order number
     * GET /api/orders/number/:orderNumber
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String orderNumber) {
        User user = userPrincipal.getUser(userRepository);
        OrderResponse order = orderService.getOrderByNumber(user, orderNumber);
        return ResponseEntity.ok(order);
    }

    /**
     * Get order status history
     * GET /api/orders/:orderId/status-history
     */
    @GetMapping("/{orderId}/status-history")
    public ResponseEntity<List<OrderStatusHistoryResponse>> getOrderStatusHistory(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID orderId) {
        User user = userPrincipal.getUser(userRepository);
        List<OrderStatusHistoryResponse> history = orderService.getOrderStatusHistory(user, orderId);
        return ResponseEntity.ok(history);
    }

    /**
     * Reorder from past order
     * POST /api/orders/:orderId/reorder
     */
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<ReorderResponse> reorder(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID orderId) {
        User user = userPrincipal.getUser(userRepository);
        ReorderResponse response = reorderService.reorder(user, orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get invoice download URL
     * GET /api/orders/:orderId/invoice
     */
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<Object> getInvoice(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID orderId) {
        User user = userPrincipal.getUser(userRepository);
        OrderResponse order = orderService.getOrder(user, orderId);
        
        if (order.getInvoiceUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Return invoice URL for download
        return ResponseEntity.ok(java.util.Map.of(
                "invoiceNumber", order.getInvoiceNumber(),
                "invoiceUrl", order.getInvoiceUrl()
        ));
    }
}
