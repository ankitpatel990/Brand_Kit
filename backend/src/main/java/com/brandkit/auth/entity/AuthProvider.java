package com.brandkit.auth.entity;

/**
 * Auth Provider Enum - Authentication method used
 * 
 * FRD-001: Supported authentication methods
 * - EMAIL: Traditional email/password registration
 * - GOOGLE: Google OAuth 2.0 (FR-3)
 * - LINKEDIN: LinkedIn OAuth 2.0 (FR-4)
 */
public enum AuthProvider {
    EMAIL,
    GOOGLE,
    LINKEDIN
}
