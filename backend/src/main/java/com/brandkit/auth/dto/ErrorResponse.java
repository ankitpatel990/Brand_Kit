package com.brandkit.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Error Response DTO
 * 
 * FRD-001 Section 9: Error Handling
 * Standardized error format for all authentication errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String status;
    private String message;
    private String errorCode;
    private Object data;
    private Map<String, String> fieldErrors;
    private ZonedDateTime timestamp;

    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .status("error")
                .message(message)
                .errorCode(errorCode)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    public static ErrorResponse of(String errorCode, String message, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .status("error")
                .message(message)
                .errorCode(errorCode)
                .fieldErrors(fieldErrors)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    /**
     * Error codes as per FRD-001 Section 9
     */
    public static class Codes {
        public static final String EMAIL_EXISTS = "AUTH_001";
        public static final String INVALID_EMAIL = "AUTH_002";
        public static final String WEAK_PASSWORD = "AUTH_003";
        public static final String PASSWORD_MISMATCH = "AUTH_004";
        public static final String INVALID_CREDENTIALS = "AUTH_005";
        public static final String UNVERIFIED_EMAIL = "AUTH_006";
        public static final String ACCOUNT_DEACTIVATED = "AUTH_007";
        public static final String ACCOUNT_LOCKED = "AUTH_008";
        public static final String EXPIRED_VERIFICATION = "AUTH_009";
        public static final String EXPIRED_RESET_TOKEN = "AUTH_010";
        public static final String OAUTH_DENIED = "AUTH_011";
        public static final String OAUTH_ERROR = "AUTH_012";
        public static final String MISSING_FIELD = "AUTH_013";
        public static final String INVALID_TOKEN = "AUTH_014";
        public static final String SESSION_EXPIRED = "AUTH_015";
        public static final String RATE_LIMIT = "AUTH_016";
    }

    public String getStatus() {
        return this.status;
    }
    public String getMessage() {
        return this.message;
    }
    public String getErrorCode() {
        return this.errorCode;
    }
    public Object getData() {
        return this.data;
    }
    public Map<String, String> getFieldErrors() {
        return this.fieldErrors;
    }
    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public ErrorResponse() {
    }
    public ErrorResponse(String status, String message, String errorCode, Object data, Map<String, String> fieldErrors, ZonedDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
        this.fieldErrors = fieldErrors;
        this.timestamp = timestamp;
    }
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private String status;
        private String message;
        private String errorCode;
        private Object data;
        private Map<String, String> fieldErrors;
        private ZonedDateTime timestamp;

        ErrorResponseBuilder() {
        }

        public ErrorResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ErrorResponseBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public ErrorResponseBuilder fieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public ErrorResponseBuilder timestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponse build() {
            ErrorResponse instance = new ErrorResponse();
            instance.status = this.status;
            instance.message = this.message;
            instance.errorCode = this.errorCode;
            instance.data = this.data;
            instance.fieldErrors = this.fieldErrors;
            instance.timestamp = this.timestamp;
            return instance;
        }
    }
}
