package com.brandkit.order.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.order.dto.*;
import com.brandkit.order.entity.DeliveryOption;
import com.brandkit.order.entity.PinCodeServiceability;
import com.brandkit.order.repository.PinCodeServiceabilityRepository;
import com.brandkit.order.service.AddressService;
import com.brandkit.order.service.CartService;
import com.brandkit.order.service.CartValidationService;
import com.brandkit.order.service.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for checkout flow - FRD-004 FR-41
 */
@RestController
@RequestMapping("/api/checkout")
@PreAuthorize("isAuthenticated()")
public class CheckoutController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartValidationService cartValidationService;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private PriceCalculationService priceCalculationService;
    
    @Autowired
    private PinCodeServiceabilityRepository pinCodeRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Validate cart before checkout
     * POST /api/checkout/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<CartValidationResponse> validateCart(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        CartValidationResponse validation = cartValidationService.validateCart(user);
        return ResponseEntity.ok(validation);
    }

    /**
     * Get checkout summary (Step 3: Review)
     * GET /api/checkout/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<CartResponse> getCheckoutSummary(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.getCart(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * Get delivery options for a PIN code (Step 2: Delivery Options)
     * GET /api/checkout/delivery-options/:pinCode
     */
    @GetMapping("/delivery-options/{pinCode}")
    public ResponseEntity<List<DeliveryOptionResponse>> getDeliveryOptions(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String pinCode) {
        User user = userPrincipal.getUser(userRepository);
        CartResponse cart = cartService.getCart(user);
        BigDecimal subtotal = cart.getPricing() != null ? cart.getPricing().getSubtotal() : BigDecimal.ZERO;

        PinCodeServiceability pinCodeData = pinCodeRepository.findByPinCode(pinCode).orElse(null);

        List<DeliveryOptionResponse> options = new ArrayList<>();

        // Standard Delivery
        DeliveryOptionResponse standard = new DeliveryOptionResponse();
        standard.setOption(DeliveryOption.STANDARD);
        standard.setDisplayName(DeliveryOption.STANDARD.getDisplayName());
        
        BigDecimal standardCharge = priceCalculationService.calculateDeliveryCharge(subtotal, DeliveryOption.STANDARD);
        standard.setCharge(standardCharge);
        standard.setIsFree(standardCharge.compareTo(BigDecimal.ZERO) == 0);
        
        if (pinCodeData != null) {
            int days = pinCodeData.getStandardDeliveryDays();
            standard.setDeliveryTimeRange(days + "-" + (days + 7) + " days");
            standard.setEstimatedDeliveryStart(LocalDate.now().plusDays(days));
            standard.setEstimatedDeliveryEnd(LocalDate.now().plusDays(days + 7));
            standard.setIsAvailable(pinCodeData.getIsServiceable());
            if (!pinCodeData.getIsServiceable()) {
                standard.setUnavailableReason("Delivery not available to this PIN code");
            }
        } else {
            standard.setDeliveryTimeRange("7-14 days");
            standard.setEstimatedDeliveryStart(LocalDate.now().plusDays(7));
            standard.setEstimatedDeliveryEnd(LocalDate.now().plusDays(14));
            standard.setIsAvailable(false);
            standard.setUnavailableReason("PIN code not found");
        }
        options.add(standard);

        // Express Delivery
        DeliveryOptionResponse express = new DeliveryOptionResponse();
        express.setOption(DeliveryOption.EXPRESS);
        express.setDisplayName(DeliveryOption.EXPRESS.getDisplayName());
        express.setCharge(DeliveryOption.EXPRESS.getBaseCharge());
        express.setIsFree(false);
        
        if (pinCodeData != null && pinCodeData.getExpressAvailable()) {
            int days = pinCodeData.getExpressDeliveryDays();
            express.setDeliveryTimeRange(days + "-" + (days + 2) + " days");
            express.setEstimatedDeliveryStart(LocalDate.now().plusDays(days));
            express.setEstimatedDeliveryEnd(LocalDate.now().plusDays(days + 2));
            express.setIsAvailable(true);
        } else {
            express.setDeliveryTimeRange("3-5 days");
            express.setEstimatedDeliveryStart(LocalDate.now().plusDays(3));
            express.setEstimatedDeliveryEnd(LocalDate.now().plusDays(5));
            express.setIsAvailable(false);
            express.setUnavailableReason("Express delivery not available for this PIN code");
        }
        options.add(express);

        return ResponseEntity.ok(options);
    }

    /**
     * Get user's saved addresses (Step 1: Address Selection)
     * GET /api/checkout/addresses
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        List<AddressResponse> addresses = addressService.getUserAddresses(user);
        return ResponseEntity.ok(addresses);
    }
}
