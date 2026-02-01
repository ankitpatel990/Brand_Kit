package com.brandkit.order.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.order.dto.AddressRequest;
import com.brandkit.order.dto.AddressResponse;
import com.brandkit.order.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for address management - FRD-004 FR-41
 */
@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("isAuthenticated()")
public class AddressController {

    @Autowired
    private AddressService addressService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Get all addresses for the current user
     * GET /api/addresses
     */
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        List<AddressResponse> addresses = addressService.getUserAddresses(user);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get address by ID
     * GET /api/addresses/:id
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        User user = userPrincipal.getUser(userRepository);
        AddressResponse address = addressService.getAddress(user, id);
        return ResponseEntity.ok(address);
    }

    /**
     * Get default address
     * GET /api/addresses/default
     */
    @GetMapping("/default")
    public ResponseEntity<AddressResponse> getDefaultAddress(@CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser(userRepository);
        AddressResponse address = addressService.getDefaultAddress(user);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

    /**
     * Create new address
     * POST /api/addresses
     */
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        User user = userPrincipal.getUser(userRepository);
        AddressResponse address = addressService.createAddress(user, request);
        return ResponseEntity.ok(address);
    }

    /**
     * Update address
     * PUT /api/addresses/:id
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request) {
        User user = userPrincipal.getUser(userRepository);
        AddressResponse address = addressService.updateAddress(user, id, request);
        return ResponseEntity.ok(address);
    }

    /**
     * Delete address
     * DELETE /api/addresses/:id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAddress(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        User user = userPrincipal.getUser(userRepository);
        addressService.deleteAddress(user, id);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
    }

    /**
     * Set address as default
     * PUT /api/addresses/:id/default
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable UUID id) {
        User user = userPrincipal.getUser(userRepository);
        AddressResponse address = addressService.setDefaultAddress(user, id);
        return ResponseEntity.ok(address);
    }

    /**
     * Check PIN code serviceability
     * GET /api/addresses/check-pincode/:pincode
     */
    @GetMapping("/check-pincode/{pincode}")
    public ResponseEntity<Map<String, Object>> checkPinCode(@PathVariable String pincode) {
        boolean serviceable = addressService.isPinCodeServiceable(pincode);
        return ResponseEntity.ok(Map.of(
                "pinCode", pincode,
                "serviceable", serviceable,
                "message", serviceable ? "Delivery available to this PIN code" : "We don't deliver to this PIN code yet"
        ));
    }
}
