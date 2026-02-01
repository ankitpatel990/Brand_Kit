package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.order.dto.*;
import com.brandkit.order.entity.*;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for order management - FRD-004 FR-43, FR-46, FR-47
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private PinCodeServiceabilityRepository pinCodeRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private PriceCalculationService priceCalculationService;

    /**
     * Create order from cart (pre-payment)
     */
    public OrderResponse createOrder(User user, CheckoutRequest request) {
        // Validate terms acceptance
        if (!request.getTermsAccepted()) {
            throw OrderException.termsNotAccepted();
        }

        // Get cart
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(OrderException::cartEmpty);

        if (cart.isEmpty()) {
            throw OrderException.cartEmpty();
        }

        // Get and validate delivery address
        Address address = addressRepository.findByIdAndUserId(request.getDeliveryAddressId(), user.getId())
                .orElseThrow(OrderException::addressNotFound);

        PinCodeServiceability pinCode = pinCodeRepository.findByPinCode(address.getPinCode())
                .filter(PinCodeServiceability::getIsServiceable)
                .orElseThrow(OrderException::pinCodeNotServiceable);

        // Check express availability if selected
        if (request.getDeliveryOption() == DeliveryOption.EXPRESS && !pinCode.canUseExpress()) {
            throw new OrderException("ORD_004", "Express delivery not available for this PIN code");
        }

        // Calculate pricing
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal originalSubtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getSubtotal());
            BigDecimal originalItemSubtotal = item.getProduct().getBasePrice()
                    .add(item.getCustomizationFee())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            originalSubtotal = originalSubtotal.add(originalItemSubtotal);
        }

        boolean isInterState = priceCalculationService.isInterState(address.getState());
        BigDecimal gstAmount = priceCalculationService.calculateGst(subtotal);
        BigDecimal cgstAmount = isInterState ? BigDecimal.ZERO : priceCalculationService.calculateCgst(subtotal);
        BigDecimal sgstAmount = isInterState ? BigDecimal.ZERO : priceCalculationService.calculateSgst(subtotal);
        BigDecimal igstAmount = isInterState ? priceCalculationService.calculateIgst(subtotal) : BigDecimal.ZERO;
        BigDecimal deliveryCharges = priceCalculationService.calculateDeliveryCharge(subtotal, request.getDeliveryOption());
        BigDecimal totalAmount = priceCalculationService.calculateTotal(subtotal, gstAmount, deliveryCharges);

        // Calculate estimated delivery dates
        int deliveryDays = pinCode.getDeliveryDays(request.getDeliveryOption());
        LocalDate estimatedStart = LocalDate.now().plusDays(deliveryDays);
        LocalDate estimatedEnd = LocalDate.now().plusDays(deliveryDays + 
                (request.getDeliveryOption() == DeliveryOption.EXPRESS ? 2 : 7));

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setDeliveryAddress(address);
        order.setDeliveryOption(request.getDeliveryOption());
        order.setEstimatedDeliveryStart(estimatedStart);
        order.setEstimatedDeliveryEnd(estimatedEnd);
        order.setSubtotal(subtotal);
        order.setOriginalSubtotal(originalSubtotal);
        order.setTotalDiscount(originalSubtotal.subtract(subtotal));
        order.setGstAmount(gstAmount);
        order.setCgstAmount(cgstAmount);
        order.setSgstAmount(sgstAmount);
        order.setIgstAmount(igstAmount);
        order.setDeliveryCharges(deliveryCharges);
        order.setTotalAmount(totalAmount);
        order.setTermsAccepted(true);
        order.setPaymentTimeoutAt(OffsetDateTime.now().plusMinutes(15));
        order.setNotes(request.getNotes());

        // Get partner from first cart item (MVP: single partner per order)
        if (!cart.getItems().isEmpty() && cart.getItems().get(0).getProduct().getPartner() != null) {
            order.setPartner(cart.getItems().get(0).getProduct().getPartner());
        }

        order = orderRepository.save(order);

        // Create order items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setCustomization(cartItem.getCustomization());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductSlug(cartItem.getProduct().getSlug());
            
            // Get primary image
            if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
                orderItem.setProductImageUrl(cartItem.getProduct().getImages().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(cartItem.getProduct().getImages().get(0).getImageUrl()));
            }
            
            if (cartItem.getCustomization() != null) {
                orderItem.setPreviewImageUrl(cartItem.getCustomization().getPreviewImageUrl());
            }
            
            orderItem.setHsnCode("6109"); // Default HSN for promotional items
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOriginalUnitPrice(cartItem.getProduct().getBasePrice());
            orderItem.setDiscountPercentage(cartItem.getDiscountPercentage());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setCustomizationFee(cartItem.getCustomizationFee());
            orderItem.calculateSubtotal();

            order.addItem(orderItem);
        }

        // Create initial status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.PENDING_PAYMENT);
        history.setDescription("Order created, awaiting payment");
        statusHistoryRepository.save(history);

        // Clear cart after order creation
        cartService.clearCart(user);

        return mapToOrderResponse(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(User user, UUID orderId) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, user.getId())
                .orElseThrow(OrderException::orderNotFound);
        return mapToOrderResponse(order);
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(User user, String orderNumber) {
        Order order = orderRepository.findByOrderNumberAndUserId(orderNumber, user.getId())
                .orElseThrow(OrderException::orderNotFound);
        return mapToOrderResponse(order);
    }

    /**
     * Get user's orders with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderListResponse> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToOrderListResponse);
    }

    /**
     * Get user's orders filtered by status
     */
    @Transactional(readOnly = true)
    public Page<OrderListResponse> getUserOrdersByStatus(User user, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), status, pageable)
                .map(this::mapToOrderListResponse);
    }

    /**
     * Search orders by order number
     */
    @Transactional(readOnly = true)
    public Page<OrderListResponse> searchOrders(User user, String searchTerm, Pageable pageable) {
        return orderRepository.searchByOrderNumber(user.getId(), "%" + searchTerm + "%", pageable)
                .map(this::mapToOrderListResponse);
    }

    /**
     * Get order status history
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderStatusHistory(User user, UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(OrderException::orderNotFound);

        return statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId())
                .stream()
                .map(this::mapToStatusHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update order status (internal use)
     */
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus, String description, String internalNotes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderException::orderNotFound);

        order.setStatus(newStatus);
        orderRepository.save(order);

        // Create status history entry
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setDescription(description);
        history.setInternalNotes(internalNotes);
        statusHistoryRepository.save(history);
    }

    /**
     * Confirm order after successful payment
     */
    public OrderResponse confirmOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(OrderException::orderNotFound);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw OrderException.orderNotModifiable();
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Create status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.CONFIRMED);
        history.setDescription("Payment received, order confirmed");
        statusHistoryRepository.save(history);

        return mapToOrderResponse(order);
    }

    /**
     * Map order entity to response DTO
     * Note: Partner information is NEVER included
     */
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setOrderDate(order.getCreatedAt());
        response.setStatus(order.getStatus());
        response.setStatusDisplayName(order.getStatus().getClientDisplayName());

        // Map items
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        response.setItems(items);
        response.setItemCount(items.size());
        response.setTotalQuantity(items.stream().mapToInt(OrderItemResponse::getQuantity).sum());

        // Map delivery address
        if (order.getDeliveryAddress() != null) {
            AddressResponse addressResponse = new AddressResponse();
            addressResponse.setId(order.getDeliveryAddress().getId());
            addressResponse.setFullName(order.getDeliveryAddress().getFullName());
            addressResponse.setPhone(order.getDeliveryAddress().getPhone());
            addressResponse.setAddressLine1(order.getDeliveryAddress().getAddressLine1());
            addressResponse.setAddressLine2(order.getDeliveryAddress().getAddressLine2());
            addressResponse.setCity(order.getDeliveryAddress().getCity());
            addressResponse.setState(order.getDeliveryAddress().getState());
            addressResponse.setPinCode(order.getDeliveryAddress().getPinCode());
            addressResponse.setFormattedAddress(order.getDeliveryAddress().getFormattedAddress());
            response.setDeliveryAddress(addressResponse);
        }

        response.setDeliveryOption(order.getDeliveryOption());
        response.setDeliveryOptionDisplayName(order.getDeliveryOption().getDisplayName());
        response.setEstimatedDeliveryStart(order.getEstimatedDeliveryStart());
        response.setEstimatedDeliveryEnd(order.getEstimatedDeliveryEnd());
        response.setEstimatedDeliveryRange(order.getEstimatedDeliveryRange());
        response.setActualDeliveryDate(order.getActualDeliveryDate());

        // Map pricing
        OrderResponse.OrderPricing pricing = new OrderResponse.OrderPricing();
        pricing.setOriginalSubtotal(order.getOriginalSubtotal());
        pricing.setSubtotal(order.getSubtotal());
        pricing.setTotalDiscount(order.getTotalDiscount());
        pricing.setGstAmount(order.getGstAmount());
        pricing.setCgstAmount(order.getCgstAmount());
        pricing.setSgstAmount(order.getSgstAmount());
        pricing.setIgstAmount(order.getIgstAmount());
        pricing.setDeliveryCharges(order.getDeliveryCharges());
        pricing.setTotalAmount(order.getTotalAmount());
        pricing.setTotalSavings(order.getTotalDiscount());
        response.setPricing(pricing);

        // Map tracking info
        if (order.getTrackingId() != null) {
            OrderResponse.TrackingInfo tracking = new OrderResponse.TrackingInfo();
            tracking.setCourierName(order.getCourierName());
            tracking.setTrackingId(order.getTrackingId());
            tracking.setTrackingUrl(order.getTrackingUrl());
            tracking.setEstimatedDelivery(order.getEstimatedDeliveryEnd());
            response.setTrackingInfo(tracking);
        }

        response.setInvoiceNumber(order.getInvoiceNumber());
        response.setInvoiceUrl(order.getInvoiceUrl());
        response.setCancelledAt(order.getCancelledAt());
        response.setCancellationReason(order.getCancellationReason());
        response.setRefundAmount(order.getRefundAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        return response;
    }

    /**
     * Map order item entity to response DTO
     */
    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProductName());
        response.setProductSlug(item.getProductSlug());
        response.setProductImageUrl(item.getProductImageUrl());
        response.setPreviewImageUrl(item.getPreviewImageUrl());
        response.setCustomizationId(item.getCustomization() != null ? item.getCustomization().getId() : null);
        response.setHasCustomization(item.getCustomization() != null);
        response.setHsnCode(item.getHsnCode());
        response.setQuantity(item.getQuantity());
        response.setOriginalUnitPrice(item.getOriginalUnitPrice());
        response.setDiscountPercentage(item.getDiscountPercentage());
        response.setUnitPrice(item.getUnitPrice());
        response.setCustomizationFee(item.getCustomizationFee());
        response.setEffectiveUnitPrice(item.getEffectiveUnitPrice());
        response.setSubtotal(item.getSubtotal());
        response.setDiscountAmount(item.getDiscountAmount());
        return response;
    }

    /**
     * Map order to list response DTO
     */
    private OrderListResponse mapToOrderListResponse(Order order) {
        OrderListResponse response = new OrderListResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setOrderDate(order.getCreatedAt());
        response.setStatus(order.getStatus());
        response.setStatusDisplayName(order.getStatus().getClientDisplayName());
        response.setItemCount(order.getItems().size());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setTotalAmount(order.getTotalAmount());

        // Get first product info
        if (!order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            response.setFirstProductName(firstItem.getProductName());
            response.setFirstProductImageUrl(firstItem.getProductImageUrl());
        }

        response.setCanReorder(order.getStatus() == OrderStatus.DELIVERED);
        response.setHasInvoice(order.getInvoiceUrl() != null);

        return response;
    }

    /**
     * Map status history to response DTO
     * Note: internalNotes is NEVER included
     */
    private OrderStatusHistoryResponse mapToStatusHistoryResponse(OrderStatusHistory history) {
        OrderStatusHistoryResponse response = new OrderStatusHistoryResponse();
        response.setId(history.getId());
        response.setStatus(history.getStatus());
        response.setStatusDisplayName(history.getStatus().getClientDisplayName());
        response.setDescription(history.getDescription());
        response.setTimestamp(history.getCreatedAt());
        return response;
    }
}
