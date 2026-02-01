package com.brandkit.customization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Product;
import com.brandkit.catalog.entity.ProductStatus;
import com.brandkit.catalog.repository.ProductRepository;
import com.brandkit.customization.dto.CropDataRequest;
import com.brandkit.customization.entity.LogoFile;
import com.brandkit.customization.repository.LogoFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Customization Validation Service
 * FRD-003 Sub-Prompt 10: Customization Validation
 * 
 * Server-side validation for customizations:
 * - Logo file integrity (not corrupted)
 * - Crop coordinates within valid bounds
 * - Product active and customizable
 * - Logo resolution sufficient for print (min 150 DPI at print size)
 */
@Service
public class CustomizationValidationService {
    private static final Logger log = LoggerFactory.getLogger(CustomizationValidationService.class);

    /**
     * Validation error class
     */
    public static class ValidationError {
        private final String code;
        private final String message;
        private final String field;

        public ValidationError(String code, String message, String field) {
            this.code = code;
            this.message = message;
            this.field = field;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
        public String getField() { return field; }
    }

    /**
     * Validation warning class
     */
    public static class ValidationWarning {
        private final String code;
        private final String message;
        private final String field;

        public ValidationWarning(String code, String message, String field) {
            this.code = code;
            this.message = message;
            this.field = field;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
        public String getField() { return field; }
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<ValidationError> errors;
        private final List<ValidationWarning> warnings;

        public ValidationResult(boolean valid, List<ValidationError> errors, List<ValidationWarning> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }

        public boolean isValid() { return valid; }
        public List<ValidationError> getErrors() { return errors; }
        public List<ValidationWarning> getWarnings() { return warnings; }
    }

    @Autowired
    private LogoFileRepository logoFileRepository;
    @Autowired
    private ProductRepository productRepository;

    private static final double MIN_PRINT_DPI = 150.0;
    private static final double CM_TO_INCH = 0.393701;

    /**
     * Validate customization before adding to cart
     */
    public ValidationResult validateCustomization(
            UUID logoFileId,
            UUID productId,
            CropDataRequest cropData
    ) {
        List<ValidationError> errors = new ArrayList<>();
        List<ValidationWarning> warnings = new ArrayList<>();

        // Validate logo file exists and is valid
        LogoFile logoFile = validateLogoFile(logoFileId, errors);

        // Validate product exists and is customizable
        Product product = validateProduct(productId, errors);

        // Validate crop data
        validateCropData(cropData, product, errors);

        // Validate logo resolution for print quality
        if (logoFile != null && product != null && errors.isEmpty()) {
            validateLogoResolution(logoFile, product, warnings);
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validate logo file exists and is not corrupted
     */
    private LogoFile validateLogoFile(UUID logoFileId, List<ValidationError> errors) {
        if (logoFileId == null) {
            errors.add(new ValidationError(
                    "CUST_NO_LOGO",
                    "Please upload a logo",
                    "logo"
            ));
            return null;
        }

        var logoFileOpt = logoFileRepository.findById(logoFileId);
        if (logoFileOpt.isEmpty()) {
            errors.add(new ValidationError(
                    "CUST_013",
                    "Logo file not found in system",
                    "logo"
            ));
            return null;
        }

        LogoFile logoFile = logoFileOpt.get();

        // Check if file has been validated
        if (!Boolean.TRUE.equals(logoFile.getIsValidated())) {
            errors.add(new ValidationError(
                    "CUST_016",
                    "Logo file validation pending. Please wait and try again.",
                    "logo"
            ));
            return null;
        }

        return logoFile;
    }

    /**
     * Validate product exists and is customizable
     */
    private Product validateProduct(UUID productId, List<ValidationError> errors) {
        if (productId == null) {
            errors.add(new ValidationError(
                    "CUST_008",
                    "Product ID is required",
                    "product"
            ));
            return null;
        }

        var productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            errors.add(new ValidationError(
                    "CUST_008",
                    "Product not found",
                    "product"
            ));
            return null;
        }

        Product product = productOpt.get();

        // Check if product is active
        if (product.getStatus() != ProductStatus.ACTIVE) {
            errors.add(new ValidationError(
                    "CUST_008",
                    "Product not available for customization",
                    "product"
            ));
            return null;
        }

        // Check if product is customizable
        if (!Boolean.TRUE.equals(product.getCustomizationAvailable())) {
            errors.add(new ValidationError(
                    "CUST_008",
                    "This product does not support customization",
                    "product"
            ));
            return null;
        }

        return product;
    }

    /**
     * Validate crop coordinates
     */
    private void validateCropData(CropDataRequest cropData, Product product, List<ValidationError> errors) {
        if (cropData == null) {
            errors.add(new ValidationError(
                    "CUST_005",
                    "Crop data is required",
                    "crop"
            ));
            return;
        }

        // Check minimum size
        if (cropData.getWidth() < 100 || cropData.getHeight() < 100) {
            errors.add(new ValidationError(
                    "CUST_005",
                    "Crop area is too small. Minimum size: 100×100px",
                    "crop"
            ));
        }

        // Check positive coordinates
        if (cropData.getX() < 0 || cropData.getY() < 0) {
            errors.add(new ValidationError(
                    "CUST_005",
                    "Invalid crop area. Please recrop logo.",
                    "crop"
            ));
        }

        // Check zoom range
        if (cropData.getZoom() < 1.0 || cropData.getZoom() > 3.0) {
            errors.add(new ValidationError(
                    "CUST_005",
                    "Invalid zoom level. Must be between 1x and 3x.",
                    "crop"
            ));
        }

        // Validate aspect ratio matches product print area
        if (product != null && product.getPrintAreaWidth() != null && product.getPrintAreaHeight() != null) {
            double productAspectRatio = product.getPrintAreaWidth().doubleValue() / 
                    product.getPrintAreaHeight().doubleValue();
            double cropAspectRatio = cropData.getWidth() / cropData.getHeight();

            // Allow 5% tolerance for aspect ratio matching
            double tolerance = 0.05;
            if (Math.abs(productAspectRatio - cropAspectRatio) / productAspectRatio > tolerance) {
                errors.add(new ValidationError(
                        "CUST_006",
                        "Crop aspect ratio must match product print area",
                        "crop"
                ));
            }
        }
    }

    /**
     * Validate logo resolution for print quality
     */
    private void validateLogoResolution(LogoFile logoFile, Product product, List<ValidationWarning> warnings) {
        if (logoFile.getWidth() == null || logoFile.getHeight() == null) {
            return; // SVG files may not have dimensions
        }

        if (product.getPrintAreaWidth() == null || product.getPrintAreaHeight() == null) {
            return;
        }

        // Calculate print size in inches
        double printWidthInches = product.getPrintAreaWidth().doubleValue() * CM_TO_INCH;
        double printHeightInches = product.getPrintAreaHeight().doubleValue() * CM_TO_INCH;

        // Calculate effective DPI
        double widthDPI = logoFile.getWidth() / printWidthInches;
        double heightDPI = logoFile.getHeight() / printHeightInches;
        double effectiveDPI = Math.min(widthDPI, heightDPI);

        if (effectiveDPI < MIN_PRINT_DPI) {
            warnings.add(new ValidationWarning(
                    "CUST_003",
                    String.format(
                            "Logo may appear pixelated when printed (%.0f DPI). For best results, upload higher resolution (min 1000×1000px).",
                            effectiveDPI
                    ),
                    "logo"
            ));
        }
    }

    /**
     * Validate customization for high-res rendering
     */
    public ValidationResult validateForRendering(
            UUID orderId,
            UUID productId,
            UUID logoFileId,
            CropDataRequest cropData
    ) {
        List<ValidationError> errors = new ArrayList<>();
        List<ValidationWarning> warnings = new ArrayList<>();

        // Basic validation
        ValidationResult basicResult = validateCustomization(logoFileId, productId, cropData);
        errors.addAll(basicResult.getErrors());
        warnings.addAll(basicResult.getWarnings());

        if (!errors.isEmpty()) {
            return new ValidationResult(false, errors, warnings);
        }

        // Additional validation for rendering
        if (orderId == null) {
            errors.add(new ValidationError(
                    "CUST_014",
                    "Order ID is required for print image generation",
                    "order"
            ));
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
}
