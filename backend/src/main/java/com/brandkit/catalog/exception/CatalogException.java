package com.brandkit.catalog.exception;

/**
 * Catalog Exception
 * FRD-002 Error Handling
 */
public class CatalogException extends RuntimeException {
    
    private final String errorCode;
    
    public CatalogException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public CatalogException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
