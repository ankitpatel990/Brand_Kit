package com.brandkit.catalog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.security.CurrentUser;
import com.brandkit.catalog.entity.ProductImage;
import com.brandkit.catalog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Image Controller
 * FRD-002 Sub-Prompt 8: Product Image Management and CDN Integration
 */
@RestController
@RequestMapping("/api/admin/products/{productId}/images")
@PreAuthorize("hasRole('ADMIN')")
public class ImageController {
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    /**
     * Upload image
     * POST /api/admin/products/:productId/images
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadImage(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            @RequestParam(value = "altText", required = false) String altText,
            @CurrentUser User admin
    ) {
        log.info("POST /api/admin/products/{}/images - admin: {}", productId, admin.getEmail());

        ProductImage image = imageService.uploadImage(productId, file, isPrimary, altText);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Image uploaded successfully",
                "data", Map.of(
                        "id", image.getId(),
                        "imageUrl", image.getImageUrl(),
                        "thumbnailUrl", image.getThumbnailUrl(),
                        "isPrimary", image.getIsPrimary(),
                        "displayOrder", image.getDisplayOrder()
                )
        ));
    }

    /**
     * Get all images for a product
     * GET /api/admin/products/:productId/images
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getImages(@PathVariable UUID productId) {
        log.debug("GET /api/admin/products/{}/images", productId);

        List<ProductImage> images = imageService.getProductImages(productId);

        List<Map<String, Object>> imageData = images.stream()
                .map(img -> Map.<String, Object>of(
                        "id", img.getId(),
                        "imageUrl", img.getImageUrl(),
                        "thumbnailUrl", img.getThumbnailUrl() != null ? img.getThumbnailUrl() : "",
                        "mediumUrl", img.getMediumUrl() != null ? img.getMediumUrl() : "",
                        "altText", img.getAltText() != null ? img.getAltText() : "",
                        "displayOrder", img.getDisplayOrder(),
                        "isPrimary", img.getIsPrimary()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", imageData
        ));
    }

    /**
     * Set primary image
     * PUT /api/admin/products/:productId/images/:imageId/primary
     */
    @PutMapping("/{imageId}/primary")
    public ResponseEntity<Map<String, String>> setPrimaryImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId,
            @CurrentUser User admin
    ) {
        log.info("PUT /api/admin/products/{}/images/{}/primary - admin: {}", 
                productId, imageId, admin.getEmail());

        imageService.setPrimaryImage(productId, imageId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Primary image updated"
        ));
    }

    /**
     * Reorder images
     * PUT /api/admin/products/:productId/images/reorder
     */
    @PutMapping("/reorder")
    public ResponseEntity<Map<String, String>> reorderImages(
            @PathVariable UUID productId,
            @RequestBody Map<String, List<UUID>> request,
            @CurrentUser User admin
    ) {
        log.info("PUT /api/admin/products/{}/images/reorder - admin: {}", productId, admin.getEmail());

        List<UUID> imageIds = request.get("imageIds");
        if (imageIds == null || imageIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "imageIds required"
            ));
        }

        imageService.reorderImages(productId, imageIds);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Images reordered"
        ));
    }

    /**
     * Delete image
     * DELETE /api/admin/products/:productId/images/:imageId
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId,
            @CurrentUser User admin
    ) {
        log.info("DELETE /api/admin/products/{}/images/{} - admin: {}", 
                productId, imageId, admin.getEmail());

        imageService.deleteImage(productId, imageId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Image deleted"
        ));
    }
}
