package com.brandkit.customization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.customization.dto.CropDataRequest;
import com.brandkit.customization.entity.LogoFile;
import com.brandkit.customization.repository.LogoFileRepository;
import com.brandkit.customization.repository.CustomizationRepository;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductImage;
import com.brandkit.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * Image Rendering Service
 * FRD-003 Sub-Prompt 4: Server-Side High-Resolution Rendering
 * 
 * Generates print-ready high-resolution images (300 DPI) for production.
 */
@Service
public class ImageRenderingService {
    private static final Logger log = LoggerFactory.getLogger(ImageRenderingService.class);

    @Autowired
    private LogoFileRepository logoFileRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomizationRepository customizationRepository;
    private static final int TARGET_DPI = 300;
    private static final double DPI_SCALE = TARGET_DPI / 72.0; // Assuming 72 DPI base

    /**
     * Render high-resolution print-ready image
     * 
     * @param orderId Order ID
     * @param productId Product ID
     * @param logoFileId Logo file ID
     * @param cropData Crop coordinates and zoom
     * @return Print-ready image URL
     */
    @Transactional
    public String renderHighResImage(
            UUID orderId,
            UUID productId,
            UUID logoFileId,
            CropDataRequest cropData
    ) {
        try {
            // Load logo file
            LogoFile logoFile = logoFileRepository.findById(logoFileId)
                    .orElseThrow(() -> new RuntimeException("Logo file not found: " + logoFileId));

            // Load product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            // Validate product has print area
            if (product.getPrintAreaWidth() == null || product.getPrintAreaHeight() == null) {
                throw new RuntimeException("Product print area not configured");
            }

            // Load images
            ProductImage firstImage = product.getImages().isEmpty() ? null : product.getImages().get(0);
            if (firstImage == null) {
                throw new RuntimeException("Product has no images");
            }
            BufferedImage productImage = loadImageFromUrl(firstImage.getImageUrl());
            BufferedImage logoImage = loadImageFromUrl(logoFile.getFileUrl());

            // Calculate high-res dimensions
            int highResWidth = (int) (productImage.getWidth() * DPI_SCALE);
            int highResHeight = (int) (productImage.getHeight() * DPI_SCALE);

            // Scale product image to high-res
            BufferedImage highResProduct = scaleImage(productImage, highResWidth, highResHeight);

            // Calculate print area in high-res coordinates
            // For now, assume print area is centered (will be configurable via admin)
            double printAreaWidthCm = product.getPrintAreaWidth().doubleValue();
            double printAreaHeightCm = product.getPrintAreaHeight().doubleValue();
            
            // Convert cm to pixels (assuming 1cm = 37.8 pixels at 96 DPI, scale for 300 DPI)
            double cmToPixel = 37.8 * DPI_SCALE;
            double printAreaWidth = printAreaWidthCm * cmToPixel;
            double printAreaHeight = printAreaHeightCm * cmToPixel;
            
            // Center print area on product image
            double printAreaX = (highResWidth - printAreaWidth) / 2.0;
            double printAreaY = (highResHeight - printAreaHeight) / 2.0;

            // Scale crop coordinates for high-res
            double cropX = cropData.getX() * DPI_SCALE;
            double cropY = cropData.getY() * DPI_SCALE;
            double cropWidth = cropData.getWidth() * DPI_SCALE;
            double cropHeight = cropData.getHeight() * DPI_SCALE;

            // Extract cropped logo region
            BufferedImage croppedLogo = logoImage.getSubimage(
                    (int) cropX,
                    (int) cropY,
                    (int) cropWidth,
                    (int) cropHeight
            );

            // Resize cropped logo to fit print area
            BufferedImage resizedLogo = scaleImage(
                    croppedLogo,
                    (int) printAreaWidth,
                    (int) printAreaHeight
            );

            // Composite logo onto product
            Graphics2D g = highResProduct.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(
                    resizedLogo,
                    (int) printAreaX,
                    (int) printAreaY,
                    (int) printAreaWidth,
                    (int) printAreaHeight,
                    null
            );
            g.dispose();

            // Convert to PNG bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(highResProduct, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            // TODO: Upload to S3 and return URL
            // For now, return placeholder
            String printImageUrl = "https://s3.brandkit.com/prints/" + orderId + "_" + productId + "_print.png";
            
            log.info("High-res image generated: {}x{}px, {} DPI", highResWidth, highResHeight, TARGET_DPI);
            
            return printImageUrl;

        } catch (Exception e) {
            log.error("Failed to render high-res image", e);
            throw new RuntimeException("Failed to generate print-ready image: " + e.getMessage(), e);
        }
    }

    private BufferedImage loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    private BufferedImage scaleImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return scaled;
    }
}
