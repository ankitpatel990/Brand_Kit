package com.brandkit.auth.entity;

/**
 * User Type Enum - Roles for access control
 * 
 * FRD-001 FR-8: Role-Based Access Control
 * - CLIENT: Access catalog, customization, orders, profile
 * - PARTNER: Access partner dashboard, orders, production tracking
 * - ADMIN: Access all modules, user management, analytics
 */
public enum UserType {
    CLIENT,
    PARTNER,
    ADMIN
}
