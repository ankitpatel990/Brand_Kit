package com.brandkit.customization.controller;

import com.brandkit.auth.security.CurrentUser;
import com.brandkit.auth.security.UserPrincipal;
import com.brandkit.customization.dto.CreateBundleRequest;
import com.brandkit.customization.dto.RenderHighResRequest;
import com.brandkit.customization.dto.SaveDraftRequest;
import com.brandkit.customization.service.BundleService;
import com.brandkit.customization.service.CustomizationService;
import com.brandkit.customization.service.CustomizationValidationService;
import com.brandkit.customization.service.ImageRenderingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Customization Controller
 * FRD-003: Customization Engine API Endpoints
 * 
 * Handles logo upload, cropping, preview, draft saving, and high-res rendering.
 */
@RestController
@RequestMapping("/api/customization")
@Tag(name = "Customization", description = "Customization Engine APIs")
public class CustomizationController {

    @Autowired
    private CustomizationService customizationService;
    @Autowired
    private ImageRenderingService imageRenderingService;
    @Autowired
    private BundleService bundleService;
    @Autowired
    private CustomizationValidationService validationService;

    /**
     * POST /api/customization/save-draft
     * FRD-003 Sub-Prompt 7: Save Draft Customization
     */
    @PostMapping("/save-draft")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Save customization draft", description = "Save work-in-progress customization")
    public ResponseEntity<Map<String, Object>> saveDraft(
            @Valid @RequestBody SaveDraftRequest request,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UUID draftId = customizationService.saveDraft(userPrincipal.getId(), request);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Draft saved successfully",
                "data", Map.of("draftId", draftId.toString())
        ));
    }

    /**
     * GET /api/customization/drafts
     * FRD-003 Sub-Prompt 7: List user's drafts
     */
    @GetMapping("/drafts")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get user's drafts", description = "Retrieve all saved customization drafts")
    public ResponseEntity<Map<String, Object>> getDrafts(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        var drafts = customizationService.getUserDrafts(userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", drafts
        ));
    }

    /**
     * GET /api/customization/draft/:draftId
     * FRD-003 Sub-Prompt 7: Load specific draft
     */
    @GetMapping("/draft/{draftId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get draft by ID", description = "Load a specific customization draft")
    public ResponseEntity<Map<String, Object>> getDraft(
            @PathVariable UUID draftId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        var draft = customizationService.getDraft(draftId, userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", draft
        ));
    }

    /**
     * DELETE /api/customization/draft/:draftId
     * FRD-003 Sub-Prompt 7: Delete draft
     */
    @DeleteMapping("/draft/{draftId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Delete draft", description = "Delete a saved customization draft")
    public ResponseEntity<Map<String, String>> deleteDraft(
            @PathVariable UUID draftId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        customizationService.deleteDraft(draftId, userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Draft deleted successfully"
        ));
    }

    /**
     * POST /api/customization/render-high-res
     * FRD-003 Sub-Prompt 4: Server-Side High-Resolution Rendering
     */
    @PostMapping("/render-high-res")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Render high-res image", description = "Generate print-ready 300 DPI image")
    public ResponseEntity<Map<String, Object>> renderHighRes(
            @Valid @RequestBody RenderHighResRequest request
    ) {
        // Validate before rendering
        var validation = validationService.validateForRendering(
                request.getOrderId(),
                request.getProductId(),
                request.getLogoFileId(),
                request.getCropData()
        );

        if (!validation.isValid()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Validation failed",
                    "errors", validation.getErrors()
            ));
        }

        String printImageUrl = imageRenderingService.renderHighResImage(
                request.getOrderId(),
                request.getProductId(),
                request.getLogoFileId(),
                request.getCropData()
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Print-ready image generated successfully",
                "data", Map.of(
                        "orderId", request.getOrderId().toString(),
                        "productId", request.getProductId().toString(),
                        "printImageUrl", printImageUrl,
                        "resolution", "300 DPI",
                        "generatedAt", java.time.Instant.now().toString()
                )
        ));
    }

    // ==================== Bundle Endpoints ====================

    /**
     * POST /api/customization/bundles
     * FRD-003 Sub-Prompt 6: Create Bundle
     */
    @PostMapping("/bundles")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create bundle", description = "Create a multi-product customization bundle")
    public ResponseEntity<Map<String, Object>> createBundle(
            @Valid @RequestBody CreateBundleRequest request,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UUID bundleId = bundleService.createBundle(userPrincipal.getId(), request);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Bundle created successfully",
                "data", Map.of("bundleId", bundleId.toString())
        ));
    }

    /**
     * GET /api/customization/bundles
     * FRD-003 Sub-Prompt 6: List user's bundles
     */
    @GetMapping("/bundles")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get user's bundles", description = "Retrieve all bundles for the user")
    public ResponseEntity<Map<String, Object>> getBundles(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        var bundles = bundleService.getUserBundles(userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", bundles
        ));
    }

    /**
     * GET /api/customization/bundles/:bundleId
     * FRD-003 Sub-Prompt 6: Get specific bundle
     */
    @GetMapping("/bundles/{bundleId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get bundle by ID", description = "Load a specific bundle")
    public ResponseEntity<Map<String, Object>> getBundle(
            @PathVariable UUID bundleId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        var bundle = bundleService.getBundle(bundleId, userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", bundle
        ));
    }

    /**
     * DELETE /api/customization/bundles/:bundleId
     * FRD-003 Sub-Prompt 6: Delete bundle
     */
    @DeleteMapping("/bundles/{bundleId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Delete bundle", description = "Delete a bundle")
    public ResponseEntity<Map<String, String>> deleteBundle(
            @PathVariable UUID bundleId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        bundleService.deleteBundle(bundleId, userPrincipal.getId());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Bundle deleted successfully"
        ));
    }

    /**
     * POST /api/customization/validate
     * FRD-003 Sub-Prompt 10: Validate customization
     */
    @PostMapping("/validate")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Validate customization", description = "Validate customization before adding to cart")
    public ResponseEntity<Map<String, Object>> validateCustomization(
            @RequestParam UUID logoFileId,
            @RequestParam UUID productId,
            @Valid @RequestBody com.brandkit.customization.dto.CropDataRequest cropData
    ) {
        var validation = validationService.validateCustomization(logoFileId, productId, cropData);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "valid", validation.isValid(),
                "errors", validation.getErrors(),
                "warnings", validation.getWarnings()
        ));
    }
}
