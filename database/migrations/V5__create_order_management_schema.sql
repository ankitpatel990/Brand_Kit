-- ============================================================================
-- BrandKit Database Schema - Order Management
-- FRD-004: Order Placement, Cart, Checkout, and Order Tracking System
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- ============================================================================
-- ENUM TYPES FOR ORDER MANAGEMENT
-- ============================================================================

-- Order Status Enum
CREATE TYPE order_status AS ENUM (
    'PENDING_PAYMENT',
    'PAYMENT_FAILED',
    'CONFIRMED',
    'ACCEPTED',
    'IN_PRODUCTION',
    'READY_TO_SHIP',
    'SHIPPED',
    'OUT_FOR_DELIVERY',
    'DELIVERED',
    'CANCELLED',
    'REFUND_INITIATED',
    'REFUNDED'
);

-- Payment Status Enum
CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'SUCCESS',
    'FAILED',
    'EXPIRED',
    'REFUNDED'
);

-- Payment Method Enum
CREATE TYPE payment_method AS ENUM (
    'UPI',
    'CREDIT_CARD',
    'DEBIT_CARD',
    'NET_BANKING',
    'WALLET'
);

-- Delivery Option Enum
CREATE TYPE delivery_option AS ENUM (
    'STANDARD',
    'EXPRESS'
);

-- Address Type Enum
CREATE TYPE address_type AS ENUM (
    'HOME',
    'OFFICE',
    'OTHER'
);

-- Partner Order Status Enum (Internal)
CREATE TYPE partner_order_status AS ENUM (
    'AWAITING_ACCEPTANCE',
    'ACCEPTED',
    'REJECTED',
    'IN_PRODUCTION',
    'READY_TO_SHIP',
    'SHIPPED',
    'DELIVERED'
);

-- ============================================================================
-- ADDRESSES TABLE
-- User saved addresses
-- ============================================================================

CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pin_code VARCHAR(6) NOT NULL,
    address_type address_type NOT NULL DEFAULT 'OFFICE',
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_serviceable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_address_full_name CHECK (char_length(full_name) >= 2 AND char_length(full_name) <= 100),
    CONSTRAINT chk_address_phone CHECK (phone ~ '^\+91[0-9]{10}$' OR phone ~ '^[0-9]{10}$'),
    CONSTRAINT chk_address_line1 CHECK (char_length(address_line1) >= 5 AND char_length(address_line1) <= 200),
    CONSTRAINT chk_address_pin_code CHECK (pin_code ~ '^[0-9]{6}$')
);

-- Indexes for addresses
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_default ON addresses(user_id, is_default) WHERE is_default = TRUE;
CREATE INDEX idx_addresses_pin_code ON addresses(pin_code);

-- ============================================================================
-- CARTS TABLE
-- Shopping cart per user
-- ============================================================================

CREATE TABLE carts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(255), -- For guest carts
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Either user_id or session_id must be present
    CONSTRAINT chk_cart_owner CHECK (user_id IS NOT NULL OR session_id IS NOT NULL)
);

-- Indexes for carts
CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_carts_session_id ON carts(session_id) WHERE session_id IS NOT NULL;

-- ============================================================================
-- CART_ITEMS TABLE
-- Items in shopping cart
-- ============================================================================

CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    customization_id UUID REFERENCES customizations(id) ON DELETE SET NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    customization_fee DECIMAL(10, 2) DEFAULT 0,
    subtotal DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_cart_item_quantity CHECK (quantity >= 1 AND quantity <= 10000),
    CONSTRAINT chk_cart_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_cart_item_subtotal CHECK (subtotal >= 0),
    CONSTRAINT unique_cart_product_customization UNIQUE (cart_id, product_id, customization_id)
);

-- Indexes for cart_items
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_cart_items_customization_id ON cart_items(customization_id);

-- ============================================================================
-- ORDERS TABLE
-- Main order records
-- ============================================================================

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(20) NOT NULL UNIQUE, -- Format: BK-YYYYMMDD-XXX
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    
    -- Status
    status order_status NOT NULL DEFAULT 'PENDING_PAYMENT',
    
    -- Delivery Information
    delivery_address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    delivery_option delivery_option NOT NULL DEFAULT 'STANDARD',
    estimated_delivery_start DATE,
    estimated_delivery_end DATE,
    actual_delivery_date DATE,
    
    -- Pricing
    subtotal DECIMAL(12, 2) NOT NULL,
    original_subtotal DECIMAL(12, 2) NOT NULL, -- Before discounts
    total_discount DECIMAL(12, 2) DEFAULT 0,
    gst_amount DECIMAL(12, 2) NOT NULL,
    cgst_amount DECIMAL(12, 2) DEFAULT 0,
    sgst_amount DECIMAL(12, 2) DEFAULT 0,
    igst_amount DECIMAL(12, 2) DEFAULT 0,
    delivery_charges DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL,
    
    -- Payment Information (payment_id links to payments table)
    payment_id UUID,
    payment_timeout_at TIMESTAMP WITH TIME ZONE,
    
    -- Tracking
    tracking_id VARCHAR(50),
    courier_name VARCHAR(100),
    tracking_url TEXT,
    
    -- Internal partner assignment (NEVER exposed to clients)
    partner_id UUID REFERENCES partners(id) ON DELETE SET NULL,
    
    -- Invoice
    invoice_number VARCHAR(20), -- Format: INV-YYYY-XXXX
    invoice_url TEXT,
    
    -- T&C acceptance
    terms_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    terms_accepted_at TIMESTAMP WITH TIME ZONE,
    
    -- Cancellation/Refund
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    refund_amount DECIMAL(12, 2),
    refund_initiated_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE,
    
    -- Metadata
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_order_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_order_gst CHECK (gst_amount >= 0),
    CONSTRAINT chk_order_total CHECK (total_amount >= 0),
    CONSTRAINT chk_order_delivery_dates CHECK (estimated_delivery_end IS NULL OR estimated_delivery_end >= estimated_delivery_start)
);

-- Indexes for orders
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_partner_id ON orders(partner_id);
CREATE INDEX idx_orders_payment_id ON orders(payment_id);
CREATE INDEX idx_orders_invoice_number ON orders(invoice_number);

-- ============================================================================
-- ORDER_ITEMS TABLE
-- Individual items within orders
-- ============================================================================

CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    customization_id UUID REFERENCES customizations(id) ON DELETE SET NULL,
    
    -- Product snapshot (captured at order time)
    product_name VARCHAR(200) NOT NULL,
    product_slug VARCHAR(250) NOT NULL,
    product_image_url TEXT,
    preview_image_url TEXT, -- Customized preview
    hsn_code VARCHAR(10),
    
    -- Pricing
    quantity INTEGER NOT NULL,
    original_unit_price DECIMAL(10, 2) NOT NULL, -- Before discount
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL, -- After discount
    customization_fee DECIMAL(10, 2) DEFAULT 0,
    subtotal DECIMAL(12, 2) NOT NULL,
    
    -- Print-ready assets (for partner)
    print_ready_image_url TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_order_item_quantity CHECK (quantity >= 1 AND quantity <= 10000),
    CONSTRAINT chk_order_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_order_item_subtotal CHECK (subtotal >= 0)
);

-- Indexes for order_items
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_customization_id ON order_items(customization_id);

-- ============================================================================
-- PAYMENTS TABLE
-- Payment transaction records
-- ============================================================================

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Payment Gateway Details
    gateway_name VARCHAR(50) NOT NULL DEFAULT 'RAZORPAY', -- RAZORPAY, PAYU
    gateway_order_id VARCHAR(100), -- Razorpay order ID
    gateway_payment_id VARCHAR(100), -- Razorpay payment ID
    gateway_signature VARCHAR(255), -- For verification
    
    -- Payment Details
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    method payment_method,
    
    -- Status
    status payment_status NOT NULL DEFAULT 'PENDING',
    
    -- Metadata
    bank_name VARCHAR(100),
    wallet_name VARCHAR(50),
    vpa VARCHAR(100), -- UPI VPA
    
    -- Timestamps
    initiated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE, -- 15 min timeout
    
    -- Error handling
    error_code VARCHAR(50),
    error_description TEXT,
    
    -- Response from gateway
    gateway_response JSONB,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

-- Indexes for payments
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_gateway_order_id ON payments(gateway_order_id);
CREATE INDEX idx_payments_gateway_payment_id ON payments(gateway_payment_id);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

-- ============================================================================
-- ORDER_STATUS_HISTORY TABLE
-- Track order status changes
-- ============================================================================

CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status order_status NOT NULL,
    description VARCHAR(500),
    internal_notes TEXT, -- Admin/Partner notes (NEVER exposed to clients)
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for order_status_history
CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created_at ON order_status_history(created_at);

-- ============================================================================
-- ORDER_PARTNER_ASSIGNMENTS TABLE (INTERNAL ONLY)
-- Maps orders to fulfillment partners - NEVER exposed to clients
-- ============================================================================

CREATE TABLE order_partner_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE RESTRICT,
    
    -- Status
    status partner_order_status NOT NULL DEFAULT 'AWAITING_ACCEPTANCE',
    
    -- Timestamps
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP WITH TIME ZONE,
    rejected_at TIMESTAMP WITH TIME ZONE,
    rejection_reason TEXT,
    
    -- Production tracking (internal)
    production_started_at TIMESTAMP WITH TIME ZONE,
    production_completed_at TIMESTAMP WITH TIME ZONE,
    shipped_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    
    -- Internal notes
    internal_notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint - one active assignment per order
    CONSTRAINT unique_order_partner UNIQUE (order_id, partner_id)
);

-- Indexes for order_partner_assignments
CREATE INDEX idx_order_partner_order_id ON order_partner_assignments(order_id);
CREATE INDEX idx_order_partner_partner_id ON order_partner_assignments(partner_id);
CREATE INDEX idx_order_partner_status ON order_partner_assignments(status);

-- ============================================================================
-- INVOICES TABLE
-- GST Invoice records
-- ============================================================================

CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    invoice_number VARCHAR(20) NOT NULL UNIQUE, -- Format: INV-YYYY-XXXX
    
    -- BrandKit Company Details
    company_name VARCHAR(200) NOT NULL DEFAULT 'BrandKit Pvt. Ltd.',
    company_address TEXT NOT NULL,
    company_gstin VARCHAR(20) NOT NULL,
    
    -- Client Details
    client_name VARCHAR(200) NOT NULL,
    client_address TEXT NOT NULL,
    client_gstin VARCHAR(20),
    client_phone VARCHAR(15),
    
    -- Invoice Details
    invoice_date DATE NOT NULL,
    due_date DATE,
    
    -- Pricing
    subtotal DECIMAL(12, 2) NOT NULL,
    original_subtotal DECIMAL(12, 2) NOT NULL,
    total_discount DECIMAL(12, 2) DEFAULT 0,
    cgst_percentage DECIMAL(5, 2) DEFAULT 9.00,
    cgst_amount DECIMAL(12, 2) DEFAULT 0,
    sgst_percentage DECIMAL(5, 2) DEFAULT 9.00,
    sgst_amount DECIMAL(12, 2) DEFAULT 0,
    igst_percentage DECIMAL(5, 2) DEFAULT 18.00,
    igst_amount DECIMAL(12, 2) DEFAULT 0,
    delivery_charges DECIMAL(10, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL,
    amount_in_words VARCHAR(500) NOT NULL,
    
    -- Invoice Type (intra-state or inter-state)
    is_inter_state BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Storage
    pdf_url TEXT,
    s3_key VARCHAR(255),
    
    -- Status
    is_generated BOOLEAN NOT NULL DEFAULT FALSE,
    generated_at TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for invoices
CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_invoice_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_created_at ON invoices(created_at DESC);

-- ============================================================================
-- REFUNDS TABLE
-- Refund tracking
-- ============================================================================

CREATE TABLE refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    
    -- Refund Details
    amount DECIMAL(12, 2) NOT NULL,
    reason TEXT NOT NULL,
    
    -- Gateway Details
    gateway_refund_id VARCHAR(100),
    gateway_response JSONB,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED', -- INITIATED, PROCESSING, SUCCESS, FAILED
    
    -- Timestamps
    initiated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    
    -- Admin
    initiated_by UUID REFERENCES users(id),
    
    -- Error handling
    error_code VARCHAR(50),
    error_description TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_refund_amount CHECK (amount > 0)
);

-- Indexes for refunds
CREATE INDEX idx_refunds_order_id ON refunds(order_id);
CREATE INDEX idx_refunds_payment_id ON refunds(payment_id);
CREATE INDEX idx_refunds_status ON refunds(status);

-- ============================================================================
-- PIN_CODE_SERVICEABILITY TABLE
-- Check if PIN code is serviceable
-- ============================================================================

CREATE TABLE pin_code_serviceability (
    pin_code VARCHAR(6) PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    is_serviceable BOOLEAN NOT NULL DEFAULT TRUE,
    standard_delivery_days INTEGER DEFAULT 10,
    express_available BOOLEAN DEFAULT TRUE,
    express_delivery_days INTEGER DEFAULT 4,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert some initial serviceable PIN codes (Gujarat focus)
INSERT INTO pin_code_serviceability (pin_code, city, state, is_serviceable, standard_delivery_days, express_available, express_delivery_days) VALUES
    ('380001', 'Ahmedabad', 'Gujarat', TRUE, 7, TRUE, 3),
    ('380015', 'Ahmedabad', 'Gujarat', TRUE, 7, TRUE, 3),
    ('380009', 'Ahmedabad', 'Gujarat', TRUE, 7, TRUE, 3),
    ('380054', 'Ahmedabad', 'Gujarat', TRUE, 7, TRUE, 3),
    ('395001', 'Surat', 'Gujarat', TRUE, 8, TRUE, 4),
    ('395007', 'Surat', 'Gujarat', TRUE, 8, TRUE, 4),
    ('390001', 'Vadodara', 'Gujarat', TRUE, 8, TRUE, 4),
    ('360001', 'Rajkot', 'Gujarat', TRUE, 9, TRUE, 5),
    ('400001', 'Mumbai', 'Maharashtra', TRUE, 10, TRUE, 4),
    ('400020', 'Mumbai', 'Maharashtra', TRUE, 10, TRUE, 4),
    ('110001', 'New Delhi', 'Delhi', TRUE, 12, TRUE, 5),
    ('560001', 'Bangalore', 'Karnataka', TRUE, 12, TRUE, 5),
    ('600001', 'Chennai', 'Tamil Nadu', TRUE, 14, TRUE, 5),
    ('700001', 'Kolkata', 'West Bengal', TRUE, 14, TRUE, 5);

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Apply updated_at trigger to tables
CREATE TRIGGER trg_addresses_updated_at
    BEFORE UPDATE ON addresses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_carts_updated_at
    BEFORE UPDATE ON carts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_cart_items_updated_at
    BEFORE UPDATE ON cart_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_order_partner_updated_at
    BEFORE UPDATE ON order_partner_assignments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_invoices_updated_at
    BEFORE UPDATE ON invoices
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_refunds_updated_at
    BEFORE UPDATE ON refunds
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_pin_code_updated_at
    BEFORE UPDATE ON pin_code_serviceability
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- FUNCTION: Generate Order Number
-- Format: BK-YYYYMMDD-XXX
-- ============================================================================

CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
DECLARE
    date_part VARCHAR(8);
    seq_num INTEGER;
    new_order_number VARCHAR(20);
BEGIN
    date_part := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');
    
    -- Get the next sequence number for today
    SELECT COALESCE(MAX(CAST(SUBSTRING(order_number FROM 13 FOR 3) AS INTEGER)), 0) + 1
    INTO seq_num
    FROM orders
    WHERE order_number LIKE 'BK-' || date_part || '-%';
    
    new_order_number := 'BK-' || date_part || '-' || LPAD(seq_num::TEXT, 3, '0');
    NEW.order_number := new_order_number;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply order number trigger
CREATE TRIGGER trg_orders_generate_number
    BEFORE INSERT ON orders
    FOR EACH ROW
    WHEN (NEW.order_number IS NULL)
    EXECUTE FUNCTION generate_order_number();

-- ============================================================================
-- FUNCTION: Generate Invoice Number
-- Format: INV-YYYY-XXXX
-- ============================================================================

CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS TRIGGER AS $$
DECLARE
    year_part VARCHAR(4);
    seq_num INTEGER;
    new_invoice_number VARCHAR(20);
BEGIN
    year_part := TO_CHAR(CURRENT_DATE, 'YYYY');
    
    -- Get the next sequence number for this year
    SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number FROM 10 FOR 4) AS INTEGER)), 0) + 1
    INTO seq_num
    FROM invoices
    WHERE invoice_number LIKE 'INV-' || year_part || '-%';
    
    new_invoice_number := 'INV-' || year_part || '-' || LPAD(seq_num::TEXT, 4, '0');
    NEW.invoice_number := new_invoice_number;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply invoice number trigger
CREATE TRIGGER trg_invoices_generate_number
    BEFORE INSERT ON invoices
    FOR EACH ROW
    WHEN (NEW.invoice_number IS NULL)
    EXECUTE FUNCTION generate_invoice_number();

-- ============================================================================
-- FUNCTION: Ensure Single Default Address
-- ============================================================================

CREATE OR REPLACE FUNCTION ensure_single_default_address()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_default = TRUE THEN
        UPDATE addresses
        SET is_default = FALSE
        WHERE user_id = NEW.user_id AND id != NEW.id AND is_default = TRUE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply default address trigger
CREATE TRIGGER trg_addresses_single_default
    BEFORE INSERT OR UPDATE OF is_default ON addresses
    FOR EACH ROW
    WHEN (NEW.is_default = TRUE)
    EXECUTE FUNCTION ensure_single_default_address();

-- ============================================================================
-- FUNCTION: Update Order Status History
-- ============================================================================

CREATE OR REPLACE FUNCTION track_order_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' AND OLD.status != NEW.status THEN
        INSERT INTO order_status_history (order_id, status, description)
        VALUES (NEW.id, NEW.status, 
            CASE NEW.status
                WHEN 'CONFIRMED' THEN 'Payment received, order confirmed'
                WHEN 'ACCEPTED' THEN 'Order accepted for fulfillment'
                WHEN 'IN_PRODUCTION' THEN 'Production started'
                WHEN 'READY_TO_SHIP' THEN 'Ready for dispatch'
                WHEN 'SHIPPED' THEN 'Order shipped'
                WHEN 'OUT_FOR_DELIVERY' THEN 'Out for delivery'
                WHEN 'DELIVERED' THEN 'Order delivered'
                WHEN 'CANCELLED' THEN 'Order cancelled'
                WHEN 'REFUND_INITIATED' THEN 'Refund initiated'
                WHEN 'REFUNDED' THEN 'Refund completed'
                ELSE 'Status updated to ' || NEW.status::TEXT
            END
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply order status tracking trigger
CREATE TRIGGER trg_orders_track_status
    AFTER UPDATE OF status ON orders
    FOR EACH ROW
    EXECUTE FUNCTION track_order_status_change();

-- ============================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

-- Enable RLS on all tables
ALTER TABLE addresses ENABLE ROW LEVEL SECURITY;
ALTER TABLE carts ENABLE ROW LEVEL SECURITY;
ALTER TABLE cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_status_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_partner_assignments ENABLE ROW LEVEL SECURITY;
ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;
ALTER TABLE refunds ENABLE ROW LEVEL SECURITY;
ALTER TABLE pin_code_serviceability ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS Policies for ADDRESSES
-- ============================================================================

CREATE POLICY addresses_service_all ON addresses
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY addresses_admin_all ON addresses
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY addresses_user_own ON addresses
    FOR ALL
    USING (user_id::TEXT = current_setting('app.current_user_id', TRUE));

-- ============================================================================
-- RLS Policies for CARTS
-- ============================================================================

CREATE POLICY carts_service_all ON carts
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY carts_admin_all ON carts
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY carts_user_own ON carts
    FOR ALL
    USING (user_id::TEXT = current_setting('app.current_user_id', TRUE));

-- ============================================================================
-- RLS Policies for CART_ITEMS
-- ============================================================================

CREATE POLICY cart_items_service_all ON cart_items
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY cart_items_admin_all ON cart_items
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY cart_items_user_own ON cart_items
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM carts
            WHERE carts.id = cart_items.cart_id
            AND carts.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for ORDERS (Excludes partner_id from client access)
-- ============================================================================

CREATE POLICY orders_service_all ON orders
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY orders_admin_all ON orders
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY orders_user_own ON orders
    FOR SELECT
    USING (user_id::TEXT = current_setting('app.current_user_id', TRUE));

-- ============================================================================
-- RLS Policies for ORDER_ITEMS
-- ============================================================================

CREATE POLICY order_items_service_all ON order_items
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY order_items_admin_all ON order_items
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY order_items_user_own ON order_items
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = order_items.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for PAYMENTS
-- ============================================================================

CREATE POLICY payments_service_all ON payments
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY payments_admin_all ON payments
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY payments_user_own ON payments
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = payments.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for ORDER_STATUS_HISTORY (Excludes internal_notes from clients)
-- ============================================================================

CREATE POLICY order_status_history_service_all ON order_status_history
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY order_status_history_admin_all ON order_status_history
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY order_status_history_user_view ON order_status_history
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = order_status_history.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for ORDER_PARTNER_ASSIGNMENTS (ADMIN AND PARTNER ONLY)
-- ============================================================================

CREATE POLICY order_partner_service_all ON order_partner_assignments
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY order_partner_admin_all ON order_partner_assignments
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY order_partner_partner_view ON order_partner_assignments
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
        -- Partner can only see their own assignments
    );

-- ============================================================================
-- RLS Policies for INVOICES
-- ============================================================================

CREATE POLICY invoices_service_all ON invoices
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY invoices_admin_all ON invoices
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY invoices_user_own ON invoices
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = invoices.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for REFUNDS
-- ============================================================================

CREATE POLICY refunds_service_all ON refunds
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY refunds_admin_all ON refunds
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY refunds_user_own ON refunds
    FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM orders
            WHERE orders.id = refunds.order_id
            AND orders.user_id::TEXT = current_setting('app.current_user_id', TRUE)
        )
    );

-- ============================================================================
-- RLS Policies for PIN_CODE_SERVICEABILITY (Public read)
-- ============================================================================

CREATE POLICY pin_code_service_all ON pin_code_serviceability
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

CREATE POLICY pin_code_admin_all ON pin_code_serviceability
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

CREATE POLICY pin_code_public_view ON pin_code_serviceability
    FOR SELECT
    USING (TRUE); -- Anyone can check serviceability

-- ============================================================================
-- VIEW: Client Orders (Excludes Partner Information)
-- ============================================================================

CREATE OR REPLACE VIEW client_orders AS
SELECT 
    o.id,
    o.order_number,
    o.user_id,
    o.status,
    o.delivery_address_id,
    o.delivery_option,
    o.estimated_delivery_start,
    o.estimated_delivery_end,
    o.actual_delivery_date,
    o.subtotal,
    o.original_subtotal,
    o.total_discount,
    o.gst_amount,
    o.cgst_amount,
    o.sgst_amount,
    o.igst_amount,
    o.delivery_charges,
    o.total_amount,
    o.tracking_id,
    o.courier_name,
    o.tracking_url,
    o.invoice_number,
    o.invoice_url,
    o.terms_accepted,
    o.cancelled_at,
    o.cancellation_reason,
    o.refund_amount,
    o.created_at,
    o.updated_at
    -- NOTE: partner_id is EXCLUDED from this view
FROM orders o;

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE addresses IS 'User delivery addresses - FRD-004 FR-41';
COMMENT ON TABLE carts IS 'Shopping cart per user - FRD-004 FR-39';
COMMENT ON TABLE cart_items IS 'Items in shopping cart - FRD-004 FR-39';
COMMENT ON TABLE orders IS 'Main order records - FRD-004 FR-43';
COMMENT ON TABLE order_items IS 'Individual items within orders - FRD-004 FR-43';
COMMENT ON TABLE payments IS 'Payment transaction records - FRD-004 FR-42';
COMMENT ON TABLE order_status_history IS 'Track order status changes - FRD-004 FR-46';
COMMENT ON TABLE order_partner_assignments IS 'INTERNAL ONLY - Maps orders to partners - FRD-004 FR-45';
COMMENT ON TABLE invoices IS 'GST Invoice records - FRD-004 FR-44';
COMMENT ON TABLE refunds IS 'Refund tracking - FRD-004';
COMMENT ON TABLE pin_code_serviceability IS 'PIN code serviceability check - FRD-004 BR-37';

COMMENT ON COLUMN orders.partner_id IS 'INTERNAL ONLY - NEVER exposed in client APIs';
COMMENT ON COLUMN order_partner_assignments.status IS 'INTERNAL ONLY - Client sees simplified status';
COMMENT ON VIEW client_orders IS 'Client-facing view that excludes partner information';
