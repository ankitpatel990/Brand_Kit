package com.brandkit.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.dto.*;
import com.brandkit.auth.entity.*;
import com.brandkit.auth.exception.AuthException;
import com.brandkit.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Authentication Service
 * 
 * FRD-001: User Registration and Authentication System
 * Core service implementing all authentication workflows:
 * - FR-1: Email-Based Registration
 * - FR-2: Email Verification
 * - FR-6: Login Functionality
 * - FR-7: Password Reset Workflow
 * - FR-9: Session Management
 * - FR-10: User Profile Management
 */
@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionRepository sessionRepository;
    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private CaptchaService captchaService;

    @Value("${app.tokens.email-verification-expiry}")
    private Duration emailVerificationExpiry;

    @Value("${app.tokens.password-reset-expiry}")
    private Duration passwordResetExpiry;

    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;

    @Value("${app.security.lockout-duration}")
    private Duration lockoutDuration;

    // ==================== SUB-PROMPT 2: EMAIL-BASED REGISTRATION ====================

    /**
     * Register new user with email and password
     * FRD-001 FR-1: Email-Based Registration
     * 
     * Workflow:
     * 1. Validate input (handled by @Valid)
     * 2. Check email uniqueness
     * 3. Hash password using bcrypt
     * 4. Create user with status ACTIVE (no email verification required)
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        // FR-13: Check email uniqueness
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw AuthException.emailExists();
        }

        // FR-5: Hash password using bcrypt
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // Create user with active status (no email verification required)
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordHash)
                .fullName(request.getFullName().trim())
                .companyName(request.getCompanyName() != null ? request.getCompanyName().trim() : null)
                .phone(request.getPhone())
                .userType(request.getUserType())
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .authProvider(AuthProvider.EMAIL)
                .termsAcceptedAt(ZonedDateTime.now())
                .build();

        user = userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        return AuthResponse.registrationSuccess(user.getId(), user.getEmail());
    }

    // ==================== SUB-PROMPT 3: EMAIL VERIFICATION ====================

    /**
     * Generate email verification token
     * FRD-001 FR-2: Token valid for 24 hours
     */
    private String generateEmailVerificationToken(User user) {
        // Invalidate any existing tokens
        emailVerificationRepository.invalidateUserTokens(user.getId(), ZonedDateTime.now());

        String token = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(token);

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(ZonedDateTime.now().plus(emailVerificationExpiry))
                .build();

        emailVerificationRepository.save(verification);
        log.debug("Email verification token generated for user: {}", user.getId());

        return token;
    }

    /**
     * Verify email with token
     * FRD-001 FR-2: Email Verification
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Email verification attempt");

        // Find all unused verification tokens and check against provided token
        // Note: We need to iterate because tokens are hashed
        EmailVerification verification = findValidVerificationToken(token);

        if (verification == null) {
            throw AuthException.invalidToken();
        }

        if (!verification.isValid()) {
            if (verification.getIsUsed() != null && verification.getIsUsed()) {
                throw AuthException.tokenAlreadyUsed();
            }
            throw AuthException.expiredVerificationToken();
        }

        // Activate user
        User user = verification.getUser();
        user.activate();
        userRepository.save(user);

        // Mark token as used
        verification.markAsVerified();
        emailVerificationRepository.save(verification);

        log.info("Email verified for user: {}", user.getId());
    }

    /**
     * Resend verification email
     * FRD-001: Workflow 1 Edge Case - Resend Verification option
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AuthException.invalidCredentials());

        if (user.getEmailVerified()) {
            log.warn("Resend verification requested for already verified user: {}", email);
            return; // Silently ignore for security
        }

        String verificationToken = generateEmailVerificationToken(user);
        emailService.sendVerificationEmail(user, verificationToken);
        log.info("Verification email resent to: {}", email);
    }

    private EmailVerification findValidVerificationToken(String token) {
        // Since tokens are hashed, we need to check each one
        // In production, consider storing token prefix for faster lookup
        return emailVerificationRepository.findAll().stream()
                .filter(v -> !Boolean.TRUE.equals(v.getIsUsed()) && passwordEncoder.matches(token, v.getTokenHash()))
                .findFirst()
                .orElse(null);
    }

    // ==================== SUB-PROMPT 6: LOGIN WITH JWT ====================

    /**
     * Login with email and password
     * FRD-001 FR-6: Login Functionality
     */
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        String email = request.getEmail().toLowerCase().trim();
        log.info("Login attempt for: {}", email);

        // FR-12: Check rate limiting
        if (rateLimitService.isRateLimited(ipAddress)) {
            recordLoginAttempt(email, ipAddress, false);
            throw AuthException.rateLimitExceeded();
        }

        // FR-6: Check if CAPTCHA is required
        if (rateLimitService.isCaptchaRequired(email)) {
            if (request.getCaptchaToken() == null || request.getCaptchaToken().isEmpty()) {
                throw AuthException.captchaRequired();
            }
            if (!captchaService.verifyCaptcha(request.getCaptchaToken(), ipAddress)) {
                recordLoginAttempt(email, ipAddress, false);
                throw AuthException.captchaFailed();
            }
        }

        // Find user
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    handleFailedLogin(email, ipAddress);
                    return AuthException.invalidCredentials();
                });

        // Check account status
        validateUserStatus(user, email, ipAddress);

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(email, ipAddress);
            throw AuthException.invalidCredentials();
        }

        // Successful login
        rateLimitService.clearFailedAttempts(email);
        user.resetFailedAttempts();
        userRepository.save(user);

        recordLoginAttempt(email, ipAddress, true);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(
                user, 
                Boolean.TRUE.equals(request.getRememberMe()),
                ipAddress, 
                userAgent
        );

        log.info("Login successful for user: {}", user.getId());

        return AuthResponse.loginSuccess(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUserType(),
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirySeconds()
        );
    }

    private void validateUserStatus(User user, String email, String ipAddress) {
        // Check if locked
        if (user.isLocked()) {
            long remainingMinutes = Duration.between(ZonedDateTime.now(), user.getLockedUntil()).toMinutes();
            throw AuthException.accountLocked((int) Math.max(1, remainingMinutes));
        }

        // Check if should lock (from rate limit service)
        if (rateLimitService.shouldLockAccount(email)) {
            user.incrementFailedAttempts(maxLoginAttempts, (int) lockoutDuration.toMinutes());
            userRepository.save(user);
            throw AuthException.accountLocked(rateLimitService.getLockoutMinutes());
        }

        // Check account status
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw AuthException.accountDeactivated();
        }
    }

    private void handleFailedLogin(String email, String ipAddress) {
        rateLimitService.recordFailedAttempt(email, ipAddress);
        rateLimitService.incrementRateLimit(ipAddress);
        recordLoginAttempt(email, ipAddress, false);
    }

    private void recordLoginAttempt(String email, String ipAddress, boolean success) {
        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .success(success)
                .build();
        loginAttemptRepository.save(attempt);
    }

    // ==================== SUB-PROMPT 7: PASSWORD RESET ====================

    /**
     * Request password reset
     * FRD-001 FR-7: Forgot Password endpoint
     * Note: Always return generic message for security (no email enumeration)
     */
    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Password reset requested for: {}", email);

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            // Invalidate existing reset tokens
            passwordResetRepository.invalidateUserTokens(user.getId(), ZonedDateTime.now());

            // Generate new token
            String token = UUID.randomUUID().toString();
            String tokenHash = passwordEncoder.encode(token);

            PasswordReset reset = PasswordReset.builder()
                    .user(user)
                    .tokenHash(tokenHash)
                    .expiresAt(ZonedDateTime.now().plus(passwordResetExpiry))
                    .build();

            passwordResetRepository.save(reset);
            emailService.sendPasswordResetEmail(user, token);
            log.info("Password reset email sent to: {}", email);
        });

        // Always log for security monitoring
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            log.debug("Password reset requested for non-existent email: {}", email);
        }
    }

    /**
     * Reset password with token
     * FRD-001 FR-7: Password Reset
     */
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request, String ipAddress, String userAgent) {
        log.info("Password reset attempt");

        PasswordReset reset = findValidResetToken(request.getToken());

        if (reset == null) {
            throw AuthException.invalidToken();
        }

        if (!reset.isValid()) {
            if (reset.getIsUsed() != null && reset.getIsUsed()) {
                throw AuthException.tokenAlreadyUsed();
            }
            throw AuthException.expiredResetToken();
        }

        User user = reset.getUser();

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Mark token as used
        reset.markAsUsed();
        passwordResetRepository.save(reset);

        // Invalidate all existing sessions
        sessionRepository.revokeAllUserSessions(user.getId(), ZonedDateTime.now());

        log.info("Password reset successful for user: {}", user.getId());

        // Auto-login
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, false, ipAddress, userAgent);

        return AuthResponse.loginSuccess(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUserType(),
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirySeconds()
        );
    }

    private PasswordReset findValidResetToken(String token) {
        return passwordResetRepository.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsUsed()) && passwordEncoder.matches(token, r.getTokenHash()))
                .findFirst()
                .orElse(null);
    }

    // ==================== SUB-PROMPT 8: SESSION MANAGEMENT ====================

    /**
     * Refresh access token
     * FRD-001 FR-9: Token Refresh
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        log.debug("Token refresh attempt");

        UserSession session = findValidSession(refreshToken);

        if (session == null || !session.isValid()) {
            throw AuthException.sessionExpired();
        }

        User user = session.getUser();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        log.debug("Token refreshed for user: {}", user.getId());

        return AuthResponse.builder()
                .status("success")
                .message("Token refreshed")
                .data(AuthResponse.AuthData.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getUserType())
                        .accessToken(accessToken)
                        .expiresIn(jwtService.getAccessTokenExpirySeconds())
                        .build())
                .build();
    }

    /**
     * Logout - invalidate session
     * FRD-001 FR-9: Logout functionality
     */
    @Transactional
    public void logout(String refreshToken) {
        UserSession session = findValidSession(refreshToken);
        if (session != null) {
            session.revoke();
            sessionRepository.save(session);
            log.info("User logged out, session revoked");
        }
    }

    /**
     * Logout from all devices
     */
    @Transactional
    public void logoutAll(UUID userId) {
        sessionRepository.revokeAllUserSessions(userId, ZonedDateTime.now());
        log.info("All sessions revoked for user: {}", userId);
    }

    private UserSession findValidSession(String refreshToken) {
        return sessionRepository.findAll().stream()
                .filter(s -> !s.getIsRevoked() && passwordEncoder.matches(refreshToken, s.getRefreshTokenHash()))
                .findFirst()
                .orElse(null);
    }

    // ==================== SUB-PROMPT 10: USER PROFILE ====================

    /**
     * Get user profile
     * FRD-001 FR-10: User Profile Management
     */
    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.invalidToken());
        return UserProfileResponse.fromEntity(user);
    }

    /**
     * Update user profile
     * FRD-001 FR-10: Editable fields only
     */
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.invalidToken());

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getCompanyName() != null) {
            user.setCompanyName(request.getCompanyName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);

        return UserProfileResponse.fromEntity(user);
    }

    /**
     * Change password
     * FRD-001 FR-10: Requires current password verification
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.invalidToken());

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw AuthException.incorrectCurrentPassword();
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate all sessions except current (handled by frontend)
        log.info("Password changed for user: {}", userId);
    }

    // ==================== OAUTH SUPPORT METHODS ====================

    /**
     * Find or create user from OAuth provider
     * FRD-001 FR-3, FR-4: Google/LinkedIn OAuth Integration
     */
    @Transactional
    public User findOrCreateOAuthUser(String email, String name, String pictureUrl, 
                                       AuthProvider provider, String providerId) {
        // Check if user exists
        return userRepository.findByEmailIgnoreCase(email)
                .map(existingUser -> {
                    // Link OAuth provider to existing account
                    linkOAuthProvider(existingUser, provider, providerId);
                    return existingUser;
                })
                .orElseGet(() -> {
                    // Create new user (needs additional info)
                    User newUser = User.builder()
                            .email(email.toLowerCase())
                            .fullName(name)
                            .profilePictureUrl(pictureUrl)
                            .authProvider(provider)
                            .status(UserStatus.ACTIVE) // OAuth users are pre-verified
                            .emailVerified(true)
                            .build();

                    if (provider == AuthProvider.GOOGLE) {
                        newUser.setGoogleId(providerId);
                    } else if (provider == AuthProvider.LINKEDIN) {
                        newUser.setLinkedinId(providerId);
                    }

                    return userRepository.save(newUser);
                });
    }

    /**
     * Complete OAuth registration with additional info
     * FRD-001 BR-10: Mandatory Fields for Social Auth
     */
    @Transactional
    public AuthResponse completeOAuthRegistration(UUID userId, OAuthAdditionalInfoRequest request,
                                                   String ipAddress, String userAgent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.invalidToken());

        user.setCompanyName(request.getCompanyName().trim());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setTermsAcceptedAt(ZonedDateTime.now());

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, false, ipAddress, userAgent);

        return AuthResponse.loginSuccess(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getUserType(),
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirySeconds()
        );
    }

    private void linkOAuthProvider(User user, AuthProvider provider, String providerId) {
        if (provider == AuthProvider.GOOGLE && user.getGoogleId() == null) {
            user.setGoogleId(providerId);
            userRepository.save(user);
            log.info("Linked Google account to user: {}", user.getId());
        } else if (provider == AuthProvider.LINKEDIN && user.getLinkedinId() == null) {
            user.setLinkedinId(providerId);
            userRepository.save(user);
            log.info("Linked LinkedIn account to user: {}", user.getId());
        }
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }
    public UserSessionRepository getSessionRepository() {
        return this.sessionRepository;
    }
    public PasswordResetRepository getPasswordResetRepository() {
        return this.passwordResetRepository;
    }
    public EmailVerificationRepository getEmailVerificationRepository() {
        return this.emailVerificationRepository;
    }
    public LoginAttemptRepository getLoginAttemptRepository() {
        return this.loginAttemptRepository;
    }
    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }
    public JwtService getJwtService() {
        return this.jwtService;
    }
    public EmailService getEmailService() {
        return this.emailService;
    }
    public RateLimitService getRateLimitService() {
        return this.rateLimitService;
    }
    public CaptchaService getCaptchaService() {
        return this.captchaService;
    }
    public Duration getEmailVerificationExpiry() {
        return this.emailVerificationExpiry;
    }
    public Duration getPasswordResetExpiry() {
        return this.passwordResetExpiry;
    }
    public int getMaxLoginAttempts() {
        return this.maxLoginAttempts;
    }
    public Duration getLockoutDuration() {
        return this.lockoutDuration;
    }
}
