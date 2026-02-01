package com.brandkit.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom Authentication Exception
 * 
 * FRD-001 Section 9: Error Handling
 * Base exception for all authentication-related errors
 */
public class AuthException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public AuthException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AuthException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    // Pre-defined exception factory methods based on FRD-001 error codes

    public static AuthException emailExists() {
        return new AuthException("AUTH_001", "Email already registered", HttpStatus.CONFLICT);
    }

    public static AuthException invalidEmail() {
        return new AuthException("AUTH_002", "Please enter a valid email address", HttpStatus.BAD_REQUEST);
    }

    public static AuthException weakPassword() {
        return new AuthException("AUTH_003", "Password must meet security requirements", HttpStatus.BAD_REQUEST);
    }

    public static AuthException passwordMismatch() {
        return new AuthException("AUTH_004", "Passwords do not match", HttpStatus.BAD_REQUEST);
    }

    public static AuthException invalidCredentials() {
        return new AuthException("AUTH_005", "Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    public static AuthException unverifiedEmail() {
        return new AuthException("AUTH_006", "Please verify your email address", HttpStatus.FORBIDDEN);
    }

    public static AuthException accountDeactivated() {
        return new AuthException("AUTH_007", "Your account has been deactivated. Contact support.", HttpStatus.FORBIDDEN);
    }

    public static AuthException accountLocked(int minutesRemaining) {
        return new AuthException("AUTH_008", 
            "Too many failed attempts. Try again in " + minutesRemaining + " minutes.", 
            HttpStatus.TOO_MANY_REQUESTS);
    }

    public static AuthException expiredVerificationToken() {
        return new AuthException("AUTH_009", "Verification link expired. Request a new one.", HttpStatus.BAD_REQUEST);
    }

    public static AuthException expiredResetToken() {
        return new AuthException("AUTH_010", "Password reset link expired. Request a new one.", HttpStatus.BAD_REQUEST);
    }

    public static AuthException oauthDenied() {
        return new AuthException("AUTH_011", "Authorization was denied. Please try again.", HttpStatus.UNAUTHORIZED);
    }

    public static AuthException oauthError() {
        return new AuthException("AUTH_012", "Unable to connect to authentication provider", HttpStatus.BAD_GATEWAY);
    }

    public static AuthException missingField(String field) {
        return new AuthException("AUTH_013", "Please fill in all required fields: " + field, HttpStatus.BAD_REQUEST);
    }

    public static AuthException invalidToken() {
        return new AuthException("AUTH_014", "Invalid or tampered authentication token", HttpStatus.UNAUTHORIZED);
    }

    public static AuthException sessionExpired() {
        return new AuthException("AUTH_015", "Your session has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
    }

    public static AuthException rateLimitExceeded() {
        return new AuthException("AUTH_016", "Too many requests. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
    }

    public static AuthException tokenAlreadyUsed() {
        return new AuthException("AUTH_014", "This link has already been used.", HttpStatus.BAD_REQUEST);
    }

    public static AuthException captchaRequired() {
        return new AuthException("AUTH_017", "CAPTCHA verification required", HttpStatus.BAD_REQUEST);
    }

    public static AuthException captchaFailed() {
        return new AuthException("AUTH_018", "CAPTCHA verification failed", HttpStatus.BAD_REQUEST);
    }

    public static AuthException incorrectCurrentPassword() {
        return new AuthException("AUTH_019", "Current password is incorrect", HttpStatus.UNAUTHORIZED);
    }

    public static AuthException accessDenied() {
        return new AuthException("AUTH_020", "Access denied", HttpStatus.FORBIDDEN);
    }
}
