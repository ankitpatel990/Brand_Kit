package com.brandkit.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserSession;
import com.brandkit.auth.repository.UserSessionRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token Service
 * 
 * FRD-001 FR-9: Session Management
 * - Access token validity: 15 minutes
 * - Refresh token validity: 7 days
 * - Refresh token extended: 30 days (for "Remember Me")
 * 
 * FRD-001 NFR-8: JWT tokens must include user ID, role, and expiry claims
 */
@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey secretKey;
    private final String issuer;
    private final Duration accessTokenExpiry;
    private final Duration refreshTokenExpiry;
    private final Duration refreshTokenExtended;
    private final UserSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-expiry}") Duration accessTokenExpiry,
            @Value("${app.jwt.refresh-token-expiry}") Duration refreshTokenExpiry,
            @Value("${app.jwt.refresh-token-extended}") Duration refreshTokenExtended,
            UserSessionRepository sessionRepository,
            PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.refreshTokenExtended = refreshTokenExtended;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate access token
     * FRD-001 FR-9: Access token validity 15 minutes
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiry.toMillis());

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getUserType().name())
                .claim("name", user.getFullName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Generate refresh token and store session
     * FRD-001 FR-9: Refresh token validity 7 days (30 days with Remember Me)
     */
    @Transactional
    public String generateRefreshToken(User user, boolean rememberMe, String ipAddress, String userAgent) {
        String token = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(token);
        
        Duration expiry = rememberMe ? refreshTokenExtended : refreshTokenExpiry;
        ZonedDateTime expiresAt = ZonedDateTime.now().plus(expiry);

        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(tokenHash)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .build();

        sessionRepository.save(session);
        log.debug("Created session for user {} expiring at {}", user.getId(), expiresAt);

        return token;
    }

    /**
     * Validate access token and extract claims
     */
    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("Access token expired");
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extract user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = validateAccessToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Get access token expiry in seconds
     */
    public long getAccessTokenExpirySeconds() {
        return accessTokenExpiry.toSeconds();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateAccessToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return true;
        }
    }
}
