package com.brandkit.partner.controller;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.partner.dto.*;
import com.brandkit.partner.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Partner Order Controller - FRD-005 FR-54, FR-55, FR-56, FR-57, FR-58, FR-59
 * Order management for partners
 */
@RestController
@RequestMapping("/api/partner/orders")
@PreAuthorize("hasRole('PARTNER') or hasRole('ADMIN')")
@Tag(name = "Partner Orders", description = "Partner order management APIs - Internal Portal Only")
public class PartnerOrderController {

    @Autowired
    private PartnerOrderService orderService;
    @Autowired
    private PartnerProfileService profileService;
    @Autowired
    private ProofUploadService proofUploadService;
    @Autowired
    private ShipmentService shipmentService;

    /**
     * Get paginated order list
     * FRD-005 FR-54: Order List View
     */
    @GetMapping
    @Operation(summary = "Get orders list", description = "Get paginated list of partner's orders")
    public ResponseEntity<PartnerOrderListResponse> getOrders(
            @CurrentUser User user,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerOrderListResponse response = orderService.getOrders(partner.getId(), status, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order details
     * FRD-005 FR-55: Order Details View
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details", description = "Get detailed order information")
    public ResponseEntity<PartnerOrderResponse> getOrderDetails(
            @CurrentUser User user,
            @PathVariable UUID orderId) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerOrderResponse response = orderService.getOrderDetails(partner.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Accept order
     * FRD-005 FR-56: Order Acceptance
     */
    @PostMapping("/{orderId}/accept")
    @Operation(summary = "Accept order", description = "Accept an order for fulfillment")
    public ResponseEntity<PartnerOrderResponse> acceptOrder(
            @CurrentUser User user,
            @PathVariable UUID orderId) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerOrderResponse response = orderService.acceptOrder(partner.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject order
     * FRD-005 FR-56: Order Rejection
     */
    @PostMapping("/{orderId}/reject")
    @Operation(summary = "Reject order", description = "Reject an order with reason")
    public ResponseEntity<Void> rejectOrder(
            @CurrentUser User user,
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderActionRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        orderService.rejectOrder(partner.getId(), orderId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Update production status
     * FRD-005 FR-57: Production Status Updates
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update status", description = "Update order production status")
    public ResponseEntity<PartnerOrderResponse> updateStatus(
            @CurrentUser User user,
            @PathVariable UUID orderId,
            @Valid @RequestBody StatusUpdateRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        PartnerOrderResponse response = orderService.updateStatus(partner.getId(), orderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload proof images
     * FRD-005 FR-58: Proof Upload Functionality
     */
    @PostMapping("/{orderId}/proof")
    @Operation(summary = "Upload proofs", description = "Upload proof sample images")
    public ResponseEntity<List<PartnerOrderResponse.ProofImageDto>> uploadProofs(
            @CurrentUser User user,
            @PathVariable UUID orderId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) List<String> captions) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        
        // Convert captions to request objects
        List<ProofUploadRequest> metadata = null;
        if (captions != null) {
            metadata = captions.stream()
                    .map(caption -> ProofUploadRequest.builder().caption(caption).build())
                    .collect(java.util.stream.Collectors.toList());
        }
        
        List<PartnerOrderResponse.ProofImageDto> response = proofUploadService.uploadProofs(
                partner.getId(), orderId, files, metadata);
        return ResponseEntity.ok(response);
    }

    /**
     * Get proof images
     */
    @GetMapping("/{orderId}/proof")
    @Operation(summary = "Get proofs", description = "Get proof images for an order")
    public ResponseEntity<List<PartnerOrderResponse.ProofImageDto>> getProofs(
            @CurrentUser User user,
            @PathVariable UUID orderId) {
        List<PartnerOrderResponse.ProofImageDto> response = proofUploadService.getProofImages(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark as shipped
     * FRD-005 FR-59: Shipment Creation
     */
    @PostMapping("/{orderId}/ship")
    @Operation(summary = "Mark as shipped", description = "Mark order as shipped with tracking")
    public ResponseEntity<Void> markAsShipped(
            @CurrentUser User user,
            @PathVariable UUID orderId,
            @Valid @RequestBody ShipmentRequest request) {
        Partner partner = profileService.getPartnerByUserId(user.getId());
        shipmentService.createShipment(partner.getId(), orderId, request);
        return ResponseEntity.ok().build();
    }
}
