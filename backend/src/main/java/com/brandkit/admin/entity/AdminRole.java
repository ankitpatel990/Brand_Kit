package com.brandkit.admin.entity;

/**
 * Admin Role Enum - Role hierarchy for admin access control
 * 
 * FRD-006 FR-66: Admin Authentication and Roles
 * - SUPER_ADMIN: Full access to all features, can create/manage other admins
 * - OPERATIONS_ADMIN: Access to user, partner, order management; NO system settings or commission config
 */
public enum AdminRole {
    SUPER_ADMIN,
    OPERATIONS_ADMIN
}
