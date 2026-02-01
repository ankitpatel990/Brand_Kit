-- ============================================================================
-- BrandKit Database Schema - Partner Dashboard (FRD-005)
-- Partner Dashboard for Order Management and Fulfillment (INTERNAL PORTAL ONLY)
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- ============================================================================
-- UPDATE PARTNERS TABLE - Add Additional Fields
-- ============================================================================

ALTER TABLE partners ADD COLUMN IF NOT EXISTS user_id UUID REFERENCES users(id);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS owner_name VARCHAR(100);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS business_address TEXT;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS gstin VARCHAR(20);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS profile_picture_url TEXT;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS max_concurrent_orders INTEGER DEFAULT 20;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS is_accepting_orders BOOLEAN DEFAULT TRUE;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS bank_account_holder VARCHAR(100);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS bank_account_number VARCHAR(20);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS bank_ifsc_code VARCHAR(15);
ALTER TABLE partners ADD COLUMN IF NOT EXISTS bank_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS profile_completed BOOLEAN DEFAULT FALSE;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS first_login_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE partners ADD COLUMN IF NOT EXISTS categories TEXT[]; -- Product categories they handle

-- Index for user lookup
CREATE INDEX IF NOT EXISTS idx_partners_user_id ON partners(user_id);

-- ============================================================================
-- DISCOUNT STATUS ENUM
-- ============================================================================

CREATE TYPE discount_status AS ENUM (
    'PENDING',
    'APPROVED',
    'DISABLED',
    'REJECTED'
);

-- ============================================================================
-- PARTNER_DISCOUNTS TABLE (FRD-005 FR-64b)
-- Partner-defined discounts with admin approval
-- ============================================================================

CREATE TABLE partner_discounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    status discount_status NOT NULL DEFAULT 'PENDING',
    admin_notes TEXT,
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by UUID REFERENCES users(id),
    disabled_at TIMESTAMP WITH TIME ZONE,
    disabled_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_discount_percentage CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    CONSTRAINT unique_partner_product_discount UNIQUE (partner_id, product_id)
);

-- Indexes
CREATE INDEX idx_partner_discounts_partner_id ON partner_discounts(partner_id);
CREATE INDEX idx_partner_discounts_product_id ON partner_discounts(product_id);
CREATE INDEX idx_partner_discounts_status ON partner_discounts(status);

-- ============================================================================
-- DISCOUNT_AUDIT_LOG TABLE (FRD-005 FR-64b)
-- Audit trail for discount changes
-- ============================================================================

CREATE TABLE discount_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    old_discount DECIMAL(5, 2),
    new_discount DECIMAL(5, 2),
    old_status discount_status,
    new_status discount_status,
    changed_by UUID REFERENCES users(id),
    changed_by_role VARCHAR(20) NOT NULL, -- 'PARTNER' or 'ADMIN'
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_discount_audit_partner_id ON discount_audit_log(partner_id);
CREATE INDEX idx_discount_audit_product_id ON discount_audit_log(product_id);
CREATE INDEX idx_discount_audit_created_at ON discount_audit_log(created_at);

-- ============================================================================
-- PROOF_IMAGES TABLE (FRD-005 FR-58)
-- Sample product images uploaded by partners
-- ============================================================================

CREATE TABLE proof_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    s3_key VARCHAR(500),
    caption VARCHAR(200),
    file_size_bytes INTEGER,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_proof_images_order_id ON proof_images(order_id);
CREATE INDEX idx_proof_images_partner_id ON proof_images(partner_id);

-- ============================================================================
-- SHIPMENTS TABLE (FRD-005 FR-59)
-- Shipment details and tracking
-- ============================================================================

CREATE TABLE shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    courier_name VARCHAR(100) NOT NULL,
    tracking_id VARCHAR(100) NOT NULL,
    tracking_url TEXT,
    ship_date DATE NOT NULL,
    weight_kg DECIMAL(10, 2),
    num_packages INTEGER DEFAULT 1,
    notes TEXT,
    webhook_enabled BOOLEAN DEFAULT FALSE,
    last_tracking_update TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_shipments_order_id ON shipments(order_id);
CREATE INDEX idx_shipments_partner_id ON shipments(partner_id);
CREATE INDEX idx_shipments_tracking_id ON shipments(tracking_id);

-- ============================================================================
-- SETTLEMENT_STATUS ENUM
-- ============================================================================

CREATE TYPE settlement_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED'
);

-- ============================================================================
-- SETTLEMENTS TABLE (FRD-005 FR-61)
-- Commission settlement records
-- ============================================================================

CREATE TABLE settlements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    settlement_number VARCHAR(20) NOT NULL UNIQUE, -- Format: SET-YYYY-MM-XXX
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_orders INTEGER NOT NULL DEFAULT 0,
    total_product_amount DECIMAL(14, 2) NOT NULL DEFAULT 0,
    total_platform_commission DECIMAL(14, 2) NOT NULL DEFAULT 0,
    total_partner_earnings DECIMAL(14, 2) NOT NULL DEFAULT 0,
    status settlement_status NOT NULL DEFAULT 'PENDING',
    payment_gateway VARCHAR(50), -- RAZORPAY_X, CASHFREE
    payment_reference VARCHAR(100),
    payment_initiated_at TIMESTAMP WITH TIME ZONE,
    payment_completed_at TIMESTAMP WITH TIME ZONE,
    payment_failed_at TIMESTAMP WITH TIME ZONE,
    failure_reason TEXT,
    statement_url TEXT,
    statement_s3_key VARCHAR(500),
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_settlements_partner_id ON settlements(partner_id);
CREATE INDEX idx_settlements_status ON settlements(status);
CREATE INDEX idx_settlements_period ON settlements(period_start, period_end);
CREATE INDEX idx_settlements_created_at ON settlements(created_at);

-- ============================================================================
-- SETTLEMENT_ORDERS TABLE (FRD-005 FR-61)
-- Orders included in each settlement
-- ============================================================================

CREATE TABLE settlement_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    settlement_id UUID NOT NULL REFERENCES settlements(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    final_amount DECIMAL(12, 2) NOT NULL,
    commission_percentage DECIMAL(5, 2) NOT NULL,
    platform_commission DECIMAL(12, 2) NOT NULL,
    partner_earnings DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_settlement_order UNIQUE (settlement_id, order_id)
);

-- Indexes
CREATE INDEX idx_settlement_orders_settlement_id ON settlement_orders(settlement_id);
CREATE INDEX idx_settlement_orders_order_id ON settlement_orders(order_id);

-- ============================================================================
-- NOTIFICATION_TYPE ENUM
-- ============================================================================

CREATE TYPE notification_type AS ENUM (
    'NEW_ORDER',
    'ORDER_ACCEPTED',
    'ORDER_REJECTED',
    'STATUS_UPDATE',
    'PROOF_UPLOADED',
    'SHIPMENT_UPDATE',
    'SETTLEMENT_PROCESSED',
    'PERFORMANCE_ALERT',
    'DISCOUNT_APPROVED',
    'DISCOUNT_DISABLED',
    'SYSTEM'
);

-- ============================================================================
-- PARTNER_NOTIFICATIONS TABLE (FRD-005 FR-53)
-- In-app notifications for partners
-- ============================================================================

CREATE TABLE partner_notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    notification_type notification_type NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_partner_notifications_partner_id ON partner_notifications(partner_id);
CREATE INDEX idx_partner_notifications_is_read ON partner_notifications(partner_id, is_read);
CREATE INDEX idx_partner_notifications_created_at ON partner_notifications(created_at DESC);

-- ============================================================================
-- PARTNER_PERFORMANCE_METRICS TABLE (FRD-005 FR-63)
-- Cached performance metrics for partners
-- ============================================================================

CREATE TABLE partner_performance_metrics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id UUID NOT NULL UNIQUE REFERENCES partners(id) ON DELETE CASCADE,
    total_orders_assigned INTEGER DEFAULT 0,
    total_orders_accepted INTEGER DEFAULT 0,
    total_orders_rejected INTEGER DEFAULT 0,
    total_orders_fulfilled INTEGER DEFAULT 0,
    total_orders_delivered INTEGER DEFAULT 0,
    fulfillment_rate DECIMAL(5, 2) DEFAULT 0, -- (accepted / assigned) * 100
    average_lead_time_days DECIMAL(5, 2) DEFAULT 0, -- Avg days from accept to ship
    delivery_success_rate DECIMAL(5, 2) DEFAULT 0, -- (delivered / shipped) * 100
    average_rating DECIMAL(3, 2) DEFAULT 0, -- 0-5 scale
    total_revenue DECIMAL(14, 2) DEFAULT 0,
    last_calculated_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_partner_metrics_partner_id ON partner_performance_metrics(partner_id);

-- ============================================================================
-- PLATFORM_SETTINGS TABLE
-- Platform-wide settings including discount limits
-- ============================================================================

CREATE TABLE IF NOT EXISTS platform_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    description TEXT,
    updated_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default platform settings
INSERT INTO platform_settings (setting_key, setting_value, description) VALUES
    ('MIN_PARTNER_DISCOUNT', '0', 'Minimum discount percentage partners can offer'),
    ('MAX_PARTNER_DISCOUNT', '25', 'Maximum discount percentage partners can offer'),
    ('AUTO_APPROVE_PARTNER_DISCOUNT', 'false', 'Whether to auto-approve partner discounts'),
    ('SETTLEMENT_DAY', '5', 'Day of month for settlement processing'),
    ('SETTLEMENT_FREQUENCY', 'MONTHLY', 'Settlement frequency: WEEKLY or MONTHLY'),
    ('MIN_PAYOUT_AMOUNT', '1000', 'Minimum payout amount in INR'),
    ('ORDER_ACCEPTANCE_DEADLINE_HOURS', '24', 'Hours for partner to accept order')
ON CONFLICT (setting_key) DO NOTHING;

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Update triggers for new tables
CREATE TRIGGER trg_partner_discounts_updated_at
    BEFORE UPDATE ON partner_discounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_shipments_updated_at
    BEFORE UPDATE ON shipments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_settlements_updated_at
    BEFORE UPDATE ON settlements
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_partner_metrics_updated_at
    BEFORE UPDATE ON partner_performance_metrics
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_platform_settings_updated_at
    BEFORE UPDATE ON platform_settings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- FUNCTION: Generate Settlement Number
-- Format: SET-YYYY-MM-XXX
-- ============================================================================

CREATE OR REPLACE FUNCTION generate_settlement_number()
RETURNS TRIGGER AS $$
DECLARE
    year_month VARCHAR(7);
    seq_num INTEGER;
    new_settlement_number VARCHAR(20);
BEGIN
    year_month := TO_CHAR(CURRENT_DATE, 'YYYY-MM');
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(settlement_number FROM 13 FOR 3) AS INTEGER)), 0) + 1
    INTO seq_num
    FROM settlements
    WHERE settlement_number LIKE 'SET-' || year_month || '-%';
    
    new_settlement_number := 'SET-' || year_month || '-' || LPAD(seq_num::TEXT, 3, '0');
    NEW.settlement_number := new_settlement_number;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply settlement number trigger
CREATE TRIGGER trg_settlements_generate_number
    BEFORE INSERT ON settlements
    FOR EACH ROW
    WHEN (NEW.settlement_number IS NULL)
    EXECUTE FUNCTION generate_settlement_number();

-- ============================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

-- Enable RLS on new tables
ALTER TABLE partner_discounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE discount_audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE proof_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE shipments ENABLE ROW LEVEL SECURITY;
ALTER TABLE settlements ENABLE ROW LEVEL SECURITY;
ALTER TABLE settlement_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE partner_notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE partner_performance_metrics ENABLE ROW LEVEL SECURITY;
ALTER TABLE platform_settings ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS Policies for PARTNER_DISCOUNTS
-- ============================================================================

CREATE POLICY partner_discounts_service_all ON partner_discounts
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY partner_discounts_admin_all ON partner_discounts
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY partner_discounts_partner_own ON partner_discounts
    FOR ALL
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        AND partner_id IN (
            SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for PROOF_IMAGES
-- ============================================================================

CREATE POLICY proof_images_service_all ON proof_images
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY proof_images_admin_all ON proof_images
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY proof_images_partner_own ON proof_images
    FOR ALL
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        AND partner_id IN (
            SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- Clients can view proofs for their orders
CREATE POLICY proof_images_client_view ON proof_images
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = proof_images.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for SHIPMENTS
-- ============================================================================

CREATE POLICY shipments_service_all ON shipments
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY shipments_admin_all ON shipments
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY shipments_partner_own ON shipments
    FOR ALL
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        AND partner_id IN (
            SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for SETTLEMENTS
-- ============================================================================

CREATE POLICY settlements_service_all ON settlements
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY settlements_admin_all ON settlements
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY settlements_partner_own ON settlements
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        AND partner_id IN (
            SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for PARTNER_NOTIFICATIONS
-- ============================================================================

CREATE POLICY partner_notifications_service_all ON partner_notifications
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY partner_notifications_admin_all ON partner_notifications
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY partner_notifications_partner_own ON partner_notifications
    FOR ALL
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        AND partner_id IN (
            SELECT id FROM partners WHERE user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for PLATFORM_SETTINGS
-- ============================================================================

CREATE POLICY platform_settings_service_all ON platform_settings
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY platform_settings_admin_all ON platform_settings
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Read-only for partners (to get discount limits, etc.)
CREATE POLICY platform_settings_partner_read ON platform_settings
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'PARTNER');

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE partner_discounts IS 'Partner-defined discounts - FRD-005 FR-64b';
COMMENT ON TABLE discount_audit_log IS 'Audit trail for discount changes - FRD-005 FR-64b';
COMMENT ON TABLE proof_images IS 'Sample product images - FRD-005 FR-58';
COMMENT ON TABLE shipments IS 'Shipment details and tracking - FRD-005 FR-59';
COMMENT ON TABLE settlements IS 'Commission settlement records - FRD-005 FR-61';
COMMENT ON TABLE settlement_orders IS 'Orders included in settlements - FRD-005 FR-61';
COMMENT ON TABLE partner_notifications IS 'In-app notifications - FRD-005 FR-53';
COMMENT ON TABLE partner_performance_metrics IS 'Cached performance metrics - FRD-005 FR-63';
COMMENT ON TABLE platform_settings IS 'Platform-wide settings including discount limits';
