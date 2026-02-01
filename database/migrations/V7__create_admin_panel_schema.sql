-- ============================================================================
-- BrandKit Database Schema - Admin Panel (FRD-006)
-- Admin Panel for Platform Management and Operations
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- ============================================================================
-- ADMIN ROLE TYPE ENUM
-- FRD-006 FR-66: Super Admin vs Operations Admin
-- ============================================================================

CREATE TYPE admin_role AS ENUM (
    'SUPER_ADMIN',       -- Full access to all features, can create/manage other admins
    'OPERATIONS_ADMIN'   -- Access to user, partner, order management; NO system settings or commission config
);

-- ============================================================================
-- ADMIN_PROFILES TABLE
-- Extended profile information for admin users
-- ============================================================================

CREATE TABLE admin_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    admin_role admin_role NOT NULL DEFAULT 'OPERATIONS_ADMIN',
    department VARCHAR(100),
    employee_id VARCHAR(50),
    can_create_admins BOOLEAN NOT NULL DEFAULT FALSE,
    can_access_system_settings BOOLEAN NOT NULL DEFAULT FALSE,
    can_configure_commission BOOLEAN NOT NULL DEFAULT FALSE,
    can_manage_discounts BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_activity_at TIMESTAMP WITH TIME ZONE,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_admin_profiles_user_id ON admin_profiles(user_id);
CREATE INDEX idx_admin_profiles_role ON admin_profiles(admin_role);
CREATE INDEX idx_admin_profiles_active ON admin_profiles(is_active);

-- ============================================================================
-- COMMISSION_CONFIGS TABLE
-- FRD-006 FR-72: Commission Configuration
-- ============================================================================

CREATE TABLE commission_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Only one default config allowed
CREATE UNIQUE INDEX idx_commission_configs_default ON commission_configs(is_default) WHERE is_default = TRUE;

-- ============================================================================
-- COMMISSION_TIERS TABLE
-- FRD-006 FR-72: Tiered Commission by Order Value
-- ============================================================================

CREATE TABLE commission_tiers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    config_id UUID NOT NULL REFERENCES commission_configs(id) ON DELETE CASCADE,
    min_order_value DECIMAL(14, 2) NOT NULL DEFAULT 0,
    max_order_value DECIMAL(14, 2),
    commission_percentage DECIMAL(5, 2) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_commission_range CHECK (min_order_value >= 0),
    CONSTRAINT chk_commission_percentage CHECK (commission_percentage >= 0 AND commission_percentage <= 100),
    CONSTRAINT chk_order_value_range CHECK (max_order_value IS NULL OR max_order_value > min_order_value)
);

-- Indexes
CREATE INDEX idx_commission_tiers_config ON commission_tiers(config_id);

-- ============================================================================
-- PARTNER_COMMISSION_OVERRIDES TABLE
-- FRD-006 FR-72: Partner-Specific Commission Rates
-- ============================================================================

CREATE TABLE partner_commission_overrides (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    commission_percentage DECIMAL(5, 2) NOT NULL,
    reason TEXT,
    effective_from DATE NOT NULL DEFAULT CURRENT_DATE,
    effective_to DATE,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_override_percentage CHECK (commission_percentage >= 0 AND commission_percentage <= 100)
);

-- Indexes
CREATE INDEX idx_partner_commission_partner ON partner_commission_overrides(partner_id);
CREATE INDEX idx_partner_commission_dates ON partner_commission_overrides(effective_from, effective_to);

-- ============================================================================
-- ADMIN_AUDIT_LOGS TABLE
-- FRD-006 FR-75, NFR-129: Comprehensive Audit Trail
-- ============================================================================

CREATE TABLE admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admin_id UUID NOT NULL REFERENCES users(id),
    action_type VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    additional_info JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for audit logs
CREATE INDEX idx_audit_logs_admin ON admin_audit_logs(admin_id);
CREATE INDEX idx_audit_logs_action ON admin_audit_logs(action_type);
CREATE INDEX idx_audit_logs_entity ON admin_audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created ON admin_audit_logs(created_at DESC);

-- ============================================================================
-- ADMIN_NOTIFICATIONS TABLE
-- FRD-006 FR-77: Admin Alerts and Notifications
-- ============================================================================

CREATE TYPE admin_notification_type AS ENUM (
    'ORDER_REJECTION',      -- Partner rejected order, needs reassignment
    'SETTLEMENT_FAILED',    -- Settlement payout failed
    'LOW_PERFORMANCE',      -- Partner fulfillment rate below threshold
    'HIGH_VALUE_ORDER',     -- Order above threshold (informational)
    'SYSTEM_ERROR',         -- Payment gateway down, API failures
    'NEW_PARTNER',          -- New partner registration
    'DISCOUNT_ABUSE',       -- Suspicious discount activity
    'USER_FLAGGED',         -- Suspicious user registration
    'BANK_VERIFICATION',    -- Partner bank needs verification
    'GENERAL'
);

CREATE TYPE admin_notification_priority AS ENUM (
    'CRITICAL',    -- Requires immediate action
    'HIGH',        -- Important but not urgent
    'MEDIUM',      -- Informational
    'LOW'          -- Can be reviewed later
);

CREATE TABLE admin_notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    notification_type admin_notification_type NOT NULL DEFAULT 'GENERAL',
    priority admin_notification_priority NOT NULL DEFAULT 'MEDIUM',
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    action_url TEXT,
    entity_type VARCHAR(50),
    entity_id UUID,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_by UUID REFERENCES users(id),
    read_at TIMESTAMP WITH TIME ZONE,
    dismissed_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_admin_notif_type ON admin_notifications(notification_type);
CREATE INDEX idx_admin_notif_priority ON admin_notifications(priority);
CREATE INDEX idx_admin_notif_unread ON admin_notifications(is_read) WHERE is_read = FALSE;
CREATE INDEX idx_admin_notif_created ON admin_notifications(created_at DESC);

-- ============================================================================
-- SYSTEM_LOGS TABLE
-- FRD-006 FR-75: System Events and Errors
-- ============================================================================

CREATE TYPE log_severity AS ENUM ('DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL');

CREATE TABLE system_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category VARCHAR(50) NOT NULL,
    severity log_severity NOT NULL DEFAULT 'INFO',
    actor_id UUID REFERENCES users(id),
    actor_type VARCHAR(20), -- 'USER', 'PARTNER', 'ADMIN', 'SYSTEM'
    action VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    metadata JSONB,
    ip_address INET,
    user_agent TEXT,
    request_id VARCHAR(36),
    duration_ms INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes (optimized for common queries)
CREATE INDEX idx_system_logs_category ON system_logs(category);
CREATE INDEX idx_system_logs_severity ON system_logs(severity);
CREATE INDEX idx_system_logs_actor ON system_logs(actor_id) WHERE actor_id IS NOT NULL;
CREATE INDEX idx_system_logs_created ON system_logs(created_at DESC);
CREATE INDEX idx_system_logs_entity ON system_logs(entity_type, entity_id) WHERE entity_id IS NOT NULL;

-- Partition by month for performance (optional - can be added later)
-- CREATE INDEX idx_system_logs_created_brin ON system_logs USING BRIN(created_at);

-- ============================================================================
-- DISCOUNT_LIMITS TABLE
-- FRD-006 FR-72b: Global and Partner-Specific Discount Limits
-- ============================================================================

CREATE TABLE discount_limits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID REFERENCES partners(id) ON DELETE CASCADE, -- NULL for global limits
    min_discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
    max_discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 25,
    auto_approve_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_suspended BOOLEAN NOT NULL DEFAULT FALSE,
    suspension_reason TEXT,
    suspended_at TIMESTAMP WITH TIME ZONE,
    suspended_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_discount_min CHECK (min_discount_percentage >= 0),
    CONSTRAINT chk_discount_max CHECK (max_discount_percentage >= min_discount_percentage),
    CONSTRAINT chk_discount_max_100 CHECK (max_discount_percentage <= 100)
);

-- Global limit (partner_id IS NULL) should be unique
CREATE UNIQUE INDEX idx_discount_limits_global ON discount_limits((partner_id IS NULL)) WHERE partner_id IS NULL;
CREATE INDEX idx_discount_limits_partner ON discount_limits(partner_id) WHERE partner_id IS NOT NULL;

-- Insert default global discount limits
INSERT INTO discount_limits (min_discount_percentage, max_discount_percentage, auto_approve_enabled)
VALUES (0, 25, FALSE);

-- ============================================================================
-- TRIGGERS
-- ============================================================================

CREATE TRIGGER trg_admin_profiles_updated_at
    BEFORE UPDATE ON admin_profiles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_commission_configs_updated_at
    BEFORE UPDATE ON commission_configs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_partner_commission_overrides_updated_at
    BEFORE UPDATE ON partner_commission_overrides
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_discount_limits_updated_at
    BEFORE UPDATE ON discount_limits
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- ROW-LEVEL SECURITY
-- ============================================================================

ALTER TABLE admin_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE commission_configs ENABLE ROW LEVEL SECURITY;
ALTER TABLE commission_tiers ENABLE ROW LEVEL SECURITY;
ALTER TABLE partner_commission_overrides ENABLE ROW LEVEL SECURITY;
ALTER TABLE admin_audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE admin_notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE system_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE discount_limits ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS Policies - Admin only access
-- ============================================================================

-- Admin Profiles - Admins can view all, Super Admin can modify
CREATE POLICY admin_profiles_service_all ON admin_profiles
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY admin_profiles_admin_read ON admin_profiles
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Commission Configs - Super Admin only for writes
CREATE POLICY commission_configs_service_all ON commission_configs
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY commission_configs_admin_read ON commission_configs
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Commission Tiers
CREATE POLICY commission_tiers_service_all ON commission_tiers
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY commission_tiers_admin_read ON commission_tiers
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Partner Commission Overrides
CREATE POLICY partner_commission_overrides_service_all ON partner_commission_overrides
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY partner_commission_overrides_admin_read ON partner_commission_overrides
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Audit Logs - Read only for all admins
CREATE POLICY admin_audit_logs_service_all ON admin_audit_logs
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY admin_audit_logs_admin_read ON admin_audit_logs
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Admin Notifications - All admins can read/update
CREATE POLICY admin_notifications_service_all ON admin_notifications
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY admin_notifications_admin_all ON admin_notifications
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- System Logs - Read only for admins
CREATE POLICY system_logs_service_all ON system_logs
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY system_logs_admin_read ON system_logs
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Discount Limits
CREATE POLICY discount_limits_service_all ON discount_limits
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY discount_limits_admin_all ON discount_limits
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Partners can read their own limits
CREATE POLICY discount_limits_partner_read ON discount_limits
    FOR SELECT
    USING (
        partner_id IS NULL -- Global limits
        OR (
            current_setting('app.current_user_type', TRUE) = 'PARTNER'
            AND partner_id IN (
                SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
            )
        )
    );

-- ============================================================================
-- SEED DEFAULT DATA
-- ============================================================================

-- Create default commission config
INSERT INTO commission_configs (name, description, is_default, is_active)
VALUES ('Default Commission', 'Standard platform commission structure', TRUE, TRUE);

-- Get the config id and insert default tiers
WITH default_config AS (
    SELECT id FROM commission_configs WHERE is_default = TRUE LIMIT 1
)
INSERT INTO commission_tiers (config_id, min_order_value, max_order_value, commission_percentage, display_order)
SELECT 
    id,
    vals.min_val,
    vals.max_val,
    vals.percentage,
    vals.ord
FROM default_config,
(VALUES 
    (0, 10000, 10.00, 1),
    (10001, 50000, 12.00, 2),
    (50001, NULL, 15.00, 3)
) AS vals(min_val, max_val, percentage, ord);

-- ============================================================================
-- FUNCTIONS
-- ============================================================================

-- Function to calculate commission for an order
CREATE OR REPLACE FUNCTION calculate_order_commission(
    p_partner_id UUID,
    p_order_amount DECIMAL(14, 2)
)
RETURNS DECIMAL(5, 2) AS $$
DECLARE
    v_commission_rate DECIMAL(5, 2);
    v_override_rate DECIMAL(5, 2);
BEGIN
    -- First check for partner-specific override
    SELECT commission_percentage INTO v_override_rate
    FROM partner_commission_overrides
    WHERE partner_id = p_partner_id
      AND effective_from <= CURRENT_DATE
      AND (effective_to IS NULL OR effective_to >= CURRENT_DATE)
    ORDER BY effective_from DESC
    LIMIT 1;
    
    IF v_override_rate IS NOT NULL THEN
        RETURN v_override_rate;
    END IF;
    
    -- Otherwise use tiered commission
    SELECT commission_percentage INTO v_commission_rate
    FROM commission_tiers ct
    JOIN commission_configs cc ON ct.config_id = cc.id
    WHERE cc.is_default = TRUE AND cc.is_active = TRUE
      AND p_order_amount >= ct.min_order_value
      AND (ct.max_order_value IS NULL OR p_order_amount <= ct.max_order_value)
    ORDER BY ct.display_order
    LIMIT 1;
    
    -- Default to 12% if no tier matches
    RETURN COALESCE(v_commission_rate, 12.00);
END;
$$ LANGUAGE plpgsql STABLE;

-- Function to log admin action
CREATE OR REPLACE FUNCTION log_admin_action(
    p_admin_id UUID,
    p_action_type VARCHAR(100),
    p_entity_type VARCHAR(50),
    p_entity_id UUID,
    p_old_values JSONB,
    p_new_values JSONB,
    p_ip_address INET DEFAULT NULL,
    p_user_agent TEXT DEFAULT NULL
)
RETURNS UUID AS $$
DECLARE
    v_log_id UUID;
BEGIN
    INSERT INTO admin_audit_logs (
        admin_id, action_type, entity_type, entity_id,
        old_values, new_values, ip_address, user_agent
    )
    VALUES (
        p_admin_id, p_action_type, p_entity_type, p_entity_id,
        p_old_values, p_new_values, p_ip_address, p_user_agent
    )
    RETURNING id INTO v_log_id;
    
    RETURN v_log_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE admin_profiles IS 'Extended admin user profiles with role-based permissions - FRD-006 FR-66';
COMMENT ON TABLE commission_configs IS 'Commission configuration sets - FRD-006 FR-72';
COMMENT ON TABLE commission_tiers IS 'Tiered commission rates by order value - FRD-006 FR-72';
COMMENT ON TABLE partner_commission_overrides IS 'Partner-specific commission rate overrides - FRD-006 FR-72';
COMMENT ON TABLE admin_audit_logs IS 'Immutable audit trail for all admin actions - FRD-006 FR-75, NFR-129';
COMMENT ON TABLE admin_notifications IS 'Admin alerts and notifications - FRD-006 FR-77';
COMMENT ON TABLE system_logs IS 'System events and error logs - FRD-006 FR-75';
COMMENT ON TABLE discount_limits IS 'Global and partner-specific discount limits - FRD-006 FR-72b';

COMMENT ON TYPE admin_role IS 'Admin role hierarchy: SUPER_ADMIN (full access), OPERATIONS_ADMIN (limited)';
COMMENT ON FUNCTION calculate_order_commission IS 'Calculates commission rate for an order based on tiers or partner overrides';
COMMENT ON FUNCTION log_admin_action IS 'Creates an immutable audit log entry for admin actions';
