package com.brandkit.catalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductImage;
import com.brandkit.catalog.exception.CatalogException;
import com.brandkit.catalog.repository.ProductImageRepository;
import com.brandkit.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Image Service
 * FRD-002 Sub-Prompt 8: Product Image Management and CDN Integration
 */
@Service
@Transactional
public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ProductImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;

    @Value("${app.cdn.base-url:https://cdn.brandkit.com}")
    private String cdnBaseUrl;

    @Value("${app.storage.max-images-per-product:8}")
    private int maxImagesPerProduct;

    @Value("${app.storage.max-file-size:5242880}") // 5MB
    private long maxFileSize;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private static final int MIN_IMAGE_DIMENSION = 800;

    /**
     * Upload image for a product
     */
    public ProductImage uploadImage(UUID productId, MultipartFile file, boolean isPrimary, String altText) {
        log.info("Uploading image for product: {}", productId);

        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Product not found"));

        // Validate file
        validateImageFile(file);

        // Check image count
        long currentCount = imageRepository.countByProductId(productId);
        if (currentCount >= maxImagesPerProduct) {
            throw new CatalogException("CAT_010", "Maximum " + maxImagesPerProduct + " images allowed per product");
        }

        // Generate file name and upload to storage
        String fileName = generateFileName(productId, file);
        String imageUrl = uploadToStorage(file, fileName);
        
        // Generate thumbnails
        String thumbnailUrl = generateThumbnail(imageUrl, 200);
        String mediumUrl = generateThumbnail(imageUrl, 400);

        // Get next display order
        Integer maxOrder = imageRepository.findMaxDisplayOrder(productId);
        int displayOrder = (maxOrder != null ? maxOrder : -1) + 1;

        // Create image record
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .mediumUrl(mediumUrl)
                .altText(altText != null ? altText : product.getName())
                .displayOrder(displayOrder)
                .isPrimary(isPrimary || currentCount == 0) // First image is always primary
                .fileSizeBytes((int) file.getSize())
                .build();

        // If marking as primary, clear other primaries
        if (productImage.getIsPrimary()) {
            imageRepository.clearPrimaryExcept(productId, UUID.randomUUID()); // Temporary UUID, will be replaced
        }

        productImage = imageRepository.save(productImage);

        // Update primary status after save
        if (productImage.getIsPrimary()) {
            imageRepository.clearPrimaryExcept(productId, productImage.getId());
        }

        log.info("Image uploaded successfully: {} for product: {}", productImage.getId(), productId);

        return productImage;
    }

    /**
     * Set image as primary
     */
    public ProductImage setPrimaryImage(UUID productId, UUID imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Image not found"));

        if (!image.getProduct().getId().equals(productId)) {
            throw new CatalogException("CAT_001", "Image does not belong to this product");
        }

        // Clear other primaries and set this one
        imageRepository.clearPrimaryExcept(productId, imageId);
        image.setIsPrimary(true);
        
        return imageRepository.save(image);
    }

    /**
     * Reorder images
     */
    public void reorderImages(UUID productId, List<UUID> imageIds) {
        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        
        for (int i = 0; i < imageIds.size(); i++) {
            UUID imageId = imageIds.get(i);
            ProductImage image = images.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new CatalogException("CAT_001", "Image not found: " + imageId));
            
            image.setDisplayOrder(i);
            imageRepository.save(image);
        }

        log.info("Images reordered for product: {}", productId);
    }

    /**
     * Delete image
     */
    public void deleteImage(UUID productId, UUID imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CatalogException("CAT_001", "Image not found"));

        if (!image.getProduct().getId().equals(productId)) {
            throw new CatalogException("CAT_001", "Image does not belong to this product");
        }

        // Delete from storage
        deleteFromStorage(image.getImageUrl());
        deleteFromStorage(image.getThumbnailUrl());
        deleteFromStorage(image.getMediumUrl());

        // Delete from database
        imageRepository.delete(image);

        // If this was primary, set next image as primary
        if (image.getIsPrimary()) {
            List<ProductImage> remainingImages = imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
            if (!remainingImages.isEmpty()) {
                remainingImages.get(0).setIsPrimary(true);
                imageRepository.save(remainingImages.get(0));
            }
        }

        log.info("Image deleted: {} from product: {}", imageId, productId);
    }

    /**
     * Get all images for a product
     */
    @Transactional(readOnly = true)
    public List<ProductImage> getProductImages(UUID productId) {
        return imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    // ==================== Helper Methods ====================

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CatalogException("CAT_010", "Image file is required");
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new CatalogException("CAT_010", "Image file size exceeds maximum of 5MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new CatalogException("CAT_010", "Invalid image format. Allowed: JPG, PNG, WebP");
        }

        // TODO: Check image dimensions (requires image processing library)
        // This would validate MIN_IMAGE_DIMENSION
    }

    private String generateFileName(UUID productId, MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        return String.format("products/%s/%s.%s", productId, UUID.randomUUID(), extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Upload file to storage (S3/GCS)
     * TODO: Implement actual cloud storage integration
     */
    private String uploadToStorage(MultipartFile file, String fileName) {
        // Placeholder implementation - would integrate with AWS S3 or GCS
        log.info("Uploading to storage: {}", fileName);
        
        // Return CDN URL
        return cdnBaseUrl + "/" + fileName;
    }

    /**
     * Generate thumbnail URL
     * TODO: Implement actual image transformation
     */
    private String generateThumbnail(String imageUrl, int size) {
        // Placeholder - would use image transformation service (e.g., CloudFront + Lambda, Imgix)
        return imageUrl.replace("/products/", "/products/thumbnails/" + size + "/");
    }

    /**
     * Delete file from storage
     * TODO: Implement actual cloud storage deletion
     */
    private void deleteFromStorage(String url) {
        if (url == null) return;
        log.info("Deleting from storage: {}", url);
        // Placeholder - would delete from S3/GCS
    }

    public ProductImageRepository getImageRepository() {
        return this.imageRepository;
    }
    public ProductRepository getProductRepository() {
        return this.productRepository;
    }
    public String getCdnBaseUrl() {
        return this.cdnBaseUrl;
    }
    public int getMaxImagesPerProduct() {
        return this.maxImagesPerProduct;
    }
    public long getMaxFileSize() {
        return this.maxFileSize;
    }
}
