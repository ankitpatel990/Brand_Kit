package com.brandkit.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.dto.AuthResponse;
import com.brandkit.auth.dto.OAuthAdditionalInfoRequest;
import com.brandkit.auth.entity.AuthProvider;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.exception.AuthException;
import com.brandkit.auth.service.AuthService;
import com.brandkit.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth Controller
 * 
 * FRD-001 FR-3, FR-4: Google and LinkedIn OAuth Integration
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "OAuth", description = "Social authentication APIs")
public class OAuthController {
    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.frontend.dashboard-path}")
    private String dashboardPath;

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // ==================== SUB-PROMPT 4: GOOGLE OAUTH ====================

    /**
     * GET /api/auth/google
     * FRD-001 FR-3: Redirects to Google consent screen
     * Spring Security handles the OAuth flow automatically
     */
    @GetMapping("/google")
    @Operation(summary = "Google OAuth", description = "Initiate Google OAuth flow")
    public void initiateGoogleOAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    /**
     * GET /api/auth/google/callback
     * FRD-001 FR-3: Google OAuth callback
     * Handled by Spring Security OAuth2
     */
    @GetMapping("/google/callback")
    @Operation(summary = "Google OAuth callback", description = "Handle Google OAuth callback")
    public void googleCallback(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        if (oauth2User == null) {
            log.error("Google OAuth failed - no user data");
            response.sendRedirect(frontendUrl + "/auth/login?error=oauth_failed");
            return;
        }

        try {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String picture = oauth2User.getAttribute("picture");
            String googleId = oauth2User.getAttribute("sub");

            log.info("Google OAuth callback for: {}", email);

            User user = authService.findOrCreateOAuthUser(email, name, picture, 
                    AuthProvider.GOOGLE, googleId);

            // Check if user needs to complete registration
            if (user.getCompanyName() == null || user.getUserType() == null) {
                // Redirect to additional info form
                String tempToken = jwtService.generateAccessToken(user);
                response.sendRedirect(frontendUrl + "/auth/complete-registration?token=" + tempToken);
                return;
            }

            // Generate tokens and redirect to dashboard
            handleOAuthSuccess(user, request, response);

        } catch (Exception e) {
            log.error("Google OAuth error: ", e);
            response.sendRedirect(frontendUrl + "/auth/login?error=oauth_error");
        }
    }

    // ==================== SUB-PROMPT 5: LINKEDIN OAUTH ====================

    /**
     * GET /api/auth/linkedin
     * FRD-001 FR-4: Redirects to LinkedIn authorization page
     */
    @GetMapping("/linkedin")
    @Operation(summary = "LinkedIn OAuth", description = "Initiate LinkedIn OAuth flow")
    public void initiateLinkedInOAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/linkedin");
    }

    /**
     * GET /api/auth/linkedin/callback
     * FRD-001 FR-4: LinkedIn OAuth callback
     */
    @GetMapping("/linkedin/callback")
    @Operation(summary = "LinkedIn OAuth callback", description = "Handle LinkedIn OAuth callback")
    public void linkedInCallback(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RegisteredOAuth2AuthorizedClient("linkedin") OAuth2AuthorizedClient authorizedClient,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        if (oauth2User == null) {
            log.error("LinkedIn OAuth failed - no user data");
            response.sendRedirect(frontendUrl + "/auth/login?error=oauth_failed");
            return;
        }

        try {
            String email = oauth2User.getAttribute("email");
            String firstName = oauth2User.getAttribute("localizedFirstName");
            String lastName = oauth2User.getAttribute("localizedLastName");
            String name = firstName + " " + lastName;
            String linkedinId = oauth2User.getAttribute("id");

            log.info("LinkedIn OAuth callback for: {}", email);

            User user = authService.findOrCreateOAuthUser(email, name, null, 
                    AuthProvider.LINKEDIN, linkedinId);

            // Check if user needs to complete registration
            if (user.getCompanyName() == null || user.getUserType() == null) {
                String tempToken = jwtService.generateAccessToken(user);
                response.sendRedirect(frontendUrl + "/auth/complete-registration?token=" + tempToken);
                return;
            }

            handleOAuthSuccess(user, request, response);

        } catch (Exception e) {
            log.error("LinkedIn OAuth error: ", e);
            response.sendRedirect(frontendUrl + "/auth/login?error=oauth_error");
        }
    }

    /**
     * POST /api/auth/complete-registration
     * FRD-001 BR-10: Complete OAuth registration with additional info
     */
    @PostMapping("/complete-registration")
    @Operation(summary = "Complete OAuth registration", description = "Provide additional info after OAuth")
    public ResponseEntity<AuthResponse> completeRegistration(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody OAuthAdditionalInfoRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.getUserIdFromToken(token);

        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.completeOAuthRegistration(
                userId, request, ipAddress, userAgent);

        // Set refresh token cookie
        setRefreshTokenCookie(response, authResponse.getData().getRefreshToken());
        authResponse.getData().setRefreshToken(null);

        return ResponseEntity.ok(authResponse);
    }

    // ==================== HELPER METHODS ====================

    private void handleOAuthSuccess(User user, HttpServletRequest request, 
                                    HttpServletResponse response) throws IOException {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, false, ipAddress, userAgent);

        setRefreshTokenCookie(response, refreshToken);

        // Redirect to dashboard with access token
        response.sendRedirect(frontendUrl + dashboardPath + "?token=" + accessToken);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public AuthService getAuthService() {
        return this.authService;
    }
    public JwtService getJwtService() {
        return this.jwtService;
    }
    public String getFrontendUrl() {
        return this.frontendUrl;
    }
    public String getDashboardPath() {
        return this.dashboardPath;
    }
}
