package com.brandkit.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.dto.*;
import com.brandkit.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

/**
 * Authentication Controller
 * 
 * FRD-001: User Registration and Authentication System
 * REST API endpoints for all authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Value("${app.jwt.refresh-token-expiry}")
    private Duration refreshTokenExpiry;

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // ==================== SUB-PROMPT 2: REGISTRATION ====================

    /**
     * POST /api/auth/register
     * FRD-001 FR-1: Email-Based Registration
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create new user account with email and password")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== SUB-PROMPT 6: LOGIN ====================

    /**
     * POST /api/auth/login
     * FRD-001 FR-6: Login Functionality
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate with email and password")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.login(request, ipAddress, userAgent);

        // Store refresh token in HttpOnly cookie
        setRefreshTokenCookie(response, authResponse.getData().getRefreshToken(), 
                Boolean.TRUE.equals(request.getRememberMe()));

        // Don't include refresh token in response body
        authResponse.getData().setRefreshToken(null);

        return ResponseEntity.ok(authResponse);
    }

    // ==================== SUB-PROMPT 7: PASSWORD RESET ====================

    /**
     * POST /api/auth/forgot-password
     * FRD-001 FR-7: Forgot Password
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send password reset link to email")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.getEmail());
        // Always return same message for security (no email enumeration)
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "If an account with that email exists, a password reset link has been sent."
        ));
    }

    /**
     * POST /api/auth/reset-password
     * FRD-001 FR-7: Password Reset
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using token from email")
    public ResponseEntity<AuthResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.resetPassword(request, ipAddress, userAgent);

        // Store refresh token in HttpOnly cookie
        setRefreshTokenCookie(response, authResponse.getData().getRefreshToken(), false);
        authResponse.getData().setRefreshToken(null);

        return ResponseEntity.ok(authResponse);
    }

    // ==================== SUB-PROMPT 8: SESSION MANAGEMENT ====================

    /**
     * POST /api/auth/refresh
     * FRD-001 FR-9: Token Refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        
        String refreshToken = getRefreshTokenFromCookie(httpRequest);
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.refreshToken(refreshToken, ipAddress, userAgent);

        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/logout
     * FRD-001 FR-9: Logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate current session")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        
        String refreshToken = getRefreshTokenFromCookie(httpRequest);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // Clear refresh token cookie
        clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Logged out successfully"
        ));
    }

    // ==================== HELPER METHODS ====================

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token, boolean rememberMe) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Enable in production with HTTPS
        cookie.setPath("/api/auth");
        cookie.setMaxAge(rememberMe ? 30 * 24 * 60 * 60 : 7 * 24 * 60 * 60); // 30 days or 7 days
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public AuthService getAuthService() {
        return this.authService;
    }
    public long getRefreshTokenExpirySeconds() {
        return this.refreshTokenExpiry.toSeconds();
    }
}
