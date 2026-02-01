package com.brandkit.catalog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Catalog Exception Handler
 * FRD-002 Error Handling
 */
@RestControllerAdvice(basePackages = "com.brandkit.catalog")
public class CatalogExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CatalogExceptionHandler.class);

    @ExceptionHandler(CatalogException.class)
    public ResponseEntity<ErrorResponse> handleCatalogException(CatalogException ex) {
        log.warn("Catalog exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());
        
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();
        
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .status("error")
                .message("Validation failed")
                .errorCode("CAT_011")
                .fieldErrors(fieldErrors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }

    private HttpStatus mapErrorCodeToStatus(String errorCode) {
        return switch (errorCode) {
            case "CAT_001" -> HttpStatus.NOT_FOUND;           // Product not found
            case "CAT_002" -> HttpStatus.OK;                   // No products in category
            case "CAT_003" -> HttpStatus.OK;                   // No search results
            case "CAT_004" -> HttpStatus.BAD_REQUEST;          // Invalid product ID
            case "CAT_007", "CAT_008", "CAT_011", "CAT_012" -> HttpStatus.BAD_REQUEST;
            case "CAT_009" -> HttpStatus.CONFLICT;             // Duplicate product name
            case "CAT_010" -> HttpStatus.PAYLOAD_TOO_LARGE;    // Image too large
            case "CAT_013" -> HttpStatus.NOT_FOUND;            // Partner not found
            case "CAT_014" -> HttpStatus.FORBIDDEN;            // Inactive product
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
