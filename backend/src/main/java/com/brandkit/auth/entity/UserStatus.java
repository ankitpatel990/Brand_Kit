package com.brandkit.auth.entity;

/**
 * User Status Enum - Account states
 * 
 * FRD-001: User Registration and Authentication System
 * - ACTIVE: Full platform access
 * - INACTIVE: Deactivated by admin
 * - PENDING_VERIFICATION: Email not yet verified
 * - LOCKED: Temporarily locked due to failed login attempts
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    PENDING_VERIFICATION,
    LOCKED
}
