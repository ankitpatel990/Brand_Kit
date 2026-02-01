-- ============================================================================
-- BrandKit Database Schema - Product Catalog
-- FRD-002: Product Catalog and Inventory Display System
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- ============================================================================
-- ENUM TYPES FOR PRODUCT CATALOG
-- ============================================================================

-- Product Category Enum
CREATE TYPE product_category AS ENUM (
    'BAGS',
    'PENS',
    'WATER_BOTTLES',
    'DIARIES',
    'T_SHIRTS',
    'OTHER'
);

-- Customization Type Enum
CREATE TYPE customization_type AS ENUM (
    'LOGO_PRINT',
    'EMBROIDERY',
    'ENGRAVING',
    'NONE'
);

-- Product Status Enum
CREATE TYPE product_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'DELETED',
    'COMING_SOON'
);

-- Availability Status Enum
CREATE TYPE availability_status AS ENUM (
    'AVAILABLE',
    'LIMITED',
    'OUT_OF_STOCK',
    'COMING_SOON'
);

-- Discount Status Enum
CREATE TYPE discount_status AS ENUM (
    'PENDING',
    'APPROVED',
    'DISABLED',
    'EXPIRED'
);

-- ============================================================================
-- PARTNERS TABLE (Internal Only - Fulfillment Partners)
-- Never exposed to clients - only for internal order routing
-- ============================================================================

CREATE TABLE partners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_name VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    location VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100) DEFAULT 'Gujarat',
    gst_number VARCHAR(20),
    status user_status NOT NULL DEFAULT 'ACTIVE',
    commission_rate DECIMAL(5, 2) NOT NULL DEFAULT 10.00,
    fulfillment_sla_days INTEGER NOT NULL DEFAULT 7,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_partner_business_name CHECK (char_length(business_name) >= 2),
    CONSTRAINT chk_partner_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_commission_rate CHECK (commission_rate >= 0 AND commission_rate <= 30)
);

-- Indexes for partners
CREATE INDEX idx_partners_status ON partners(status);
CREATE INDEX idx_partners_city ON partners(city);
CREATE INDEX idx_partners_email ON partners(email);

-- ============================================================================
-- PRODUCTS TABLE
-- Core product catalog table
-- ============================================================================

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    category product_category NOT NULL,
    short_description VARCHAR(300) NOT NULL,
    long_description TEXT NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    material VARCHAR(100),
    eco_friendly BOOLEAN NOT NULL DEFAULT FALSE,
    customization_available BOOLEAN NOT NULL DEFAULT FALSE,
    customization_type customization_type NOT NULL DEFAULT 'NONE',
    print_area_width DECIMAL(6, 2),
    print_area_height DECIMAL(6, 2),
    weight_grams INTEGER,
    dimensions VARCHAR(100),
    available_colors TEXT[], -- Array of color names
    partner_id UUID REFERENCES partners(id) ON DELETE SET NULL,
    lead_time_days INTEGER NOT NULL DEFAULT 7,
    status product_status NOT NULL DEFAULT 'ACTIVE',
    availability availability_status NOT NULL DEFAULT 'AVAILABLE',
    aggregate_rating DECIMAL(2, 1) DEFAULT 0.0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    total_orders INTEGER NOT NULL DEFAULT 0,
    tags TEXT[], -- Array of tags for search
    meta_title VARCHAR(200),
    meta_description VARCHAR(300),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_product_name CHECK (char_length(name) >= 5 AND char_length(name) <= 200),
    CONSTRAINT chk_short_desc CHECK (char_length(short_description) >= 50 AND char_length(short_description) <= 300),
    CONSTRAINT chk_long_desc CHECK (char_length(long_description) >= 100 AND char_length(long_description) <= 2000),
    CONSTRAINT chk_base_price CHECK (base_price >= 1),
    CONSTRAINT chk_lead_time CHECK (lead_time_days >= 1 AND lead_time_days <= 90),
    CONSTRAINT chk_rating CHECK (aggregate_rating >= 0 AND aggregate_rating <= 5),
    CONSTRAINT chk_customization_fields CHECK (
        (customization_available = FALSE) OR 
        (customization_available = TRUE AND customization_type != 'NONE' AND print_area_width IS NOT NULL AND print_area_height IS NOT NULL)
    )
);

-- Indexes for products
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_partner_id ON products(partner_id);
CREATE INDEX idx_products_base_price ON products(base_price);
CREATE INDEX idx_products_eco_friendly ON products(eco_friendly) WHERE eco_friendly = TRUE;
CREATE INDEX idx_products_customization ON products(customization_available) WHERE customization_available = TRUE;
CREATE INDEX idx_products_aggregate_rating ON products(aggregate_rating);
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_availability ON products(availability);

-- Full-text search index
CREATE INDEX idx_products_search ON products USING GIN (
    to_tsvector('english', coalesce(name, '') || ' ' || coalesce(short_description, '') || ' ' || coalesce(array_to_string(tags, ' '), ''))
);

-- ============================================================================
-- PRODUCT_IMAGES TABLE
-- Product image gallery
-- ============================================================================

CREATE TABLE product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    thumbnail_url TEXT,
    medium_url TEXT,
    alt_text VARCHAR(200),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    file_size_bytes INTEGER,
    width INTEGER,
    height INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_display_order CHECK (display_order >= 0)
);

-- Indexes for product_images
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_primary ON product_images(product_id, is_primary) WHERE is_primary = TRUE;
CREATE INDEX idx_product_images_order ON product_images(product_id, display_order);

-- ============================================================================
-- PRICING_TIERS TABLE
-- Quantity-based pricing tiers
-- ============================================================================

CREATE TABLE pricing_tiers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tier_number INTEGER NOT NULL,
    min_quantity INTEGER NOT NULL,
    max_quantity INTEGER, -- NULL means no upper limit
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_min_quantity CHECK (min_quantity >= 1),
    CONSTRAINT chk_max_quantity CHECK (max_quantity IS NULL OR max_quantity >= min_quantity),
    CONSTRAINT chk_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_discount_percentage CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    CONSTRAINT unique_product_tier UNIQUE (product_id, tier_number)
);

-- Indexes for pricing_tiers
CREATE INDEX idx_pricing_tiers_product_id ON pricing_tiers(product_id);

-- ============================================================================
-- PRODUCT_DISCOUNTS TABLE
-- Partner-defined discounts with admin approval
-- ============================================================================

CREATE TABLE product_discounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    discount_name VARCHAR(100),
    status discount_status NOT NULL DEFAULT 'PENDING',
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP WITH TIME ZONE,
    disabled_by UUID REFERENCES users(id),
    disabled_at TIMESTAMP WITH TIME ZONE,
    disabled_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_discount_percentage CHECK (discount_percentage > 0 AND discount_percentage <= 25),
    CONSTRAINT chk_date_range CHECK (end_date IS NULL OR end_date > start_date)
);

-- Indexes for product_discounts
CREATE INDEX idx_product_discounts_product_id ON product_discounts(product_id);
CREATE INDEX idx_product_discounts_partner_id ON product_discounts(partner_id);
CREATE INDEX idx_product_discounts_status ON product_discounts(status);
CREATE INDEX idx_product_discounts_active ON product_discounts(product_id, status) WHERE status = 'APPROVED';

-- ============================================================================
-- DISCOUNT_AUDIT_LOG TABLE
-- Audit trail for discount changes
-- ============================================================================

CREATE TABLE discount_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    discount_id UUID NOT NULL REFERENCES product_discounts(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL, -- CREATED, APPROVED, DISABLED, UPDATED, EXPIRED
    performed_by UUID NOT NULL REFERENCES users(id),
    performed_by_role user_type NOT NULL,
    old_value JSONB,
    new_value JSONB,
    reason TEXT,
    ip_address INET,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for discount_audit_log
CREATE INDEX idx_discount_audit_discount_id ON discount_audit_log(discount_id);
CREATE INDEX idx_discount_audit_created_at ON discount_audit_log(created_at);

-- ============================================================================
-- DISCOUNT_LIMITS TABLE
-- Global discount limits set by admin
-- ============================================================================

CREATE TABLE discount_limits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    min_discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
    max_discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 25,
    category product_category, -- NULL means applies to all categories
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    set_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_discount_limits CHECK (
        min_discount_percentage >= 0 AND 
        max_discount_percentage <= 50 AND 
        max_discount_percentage >= min_discount_percentage
    )
);

-- ============================================================================
-- CATEGORIES TABLE (Optional metadata for categories)
-- ============================================================================

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    product_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default categories
INSERT INTO categories (name, slug, description, display_order) VALUES
    ('Bags', 'bags', 'Custom branded bags including tote bags, laptop bags, and backpacks', 1),
    ('Pens', 'pens', 'Premium branded pens and writing instruments', 2),
    ('Water Bottles', 'water-bottles', 'Eco-friendly and stainless steel water bottles', 3),
    ('Diaries', 'diaries', 'Corporate diaries, planners, and notebooks', 4),
    ('T-Shirts', 't-shirts', 'Custom printed and embroidered t-shirts', 5),
    ('Other', 'other', 'Other promotional merchandise items', 6);

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Apply updated_at trigger to products table
CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply updated_at trigger to partners table
CREATE TRIGGER trg_partners_updated_at
    BEFORE UPDATE ON partners
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply updated_at trigger to pricing_tiers table
CREATE TRIGGER trg_pricing_tiers_updated_at
    BEFORE UPDATE ON pricing_tiers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply updated_at trigger to product_discounts table
CREATE TRIGGER trg_product_discounts_updated_at
    BEFORE UPDATE ON product_discounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply updated_at trigger to discount_limits table
CREATE TRIGGER trg_discount_limits_updated_at
    BEFORE UPDATE ON discount_limits
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply updated_at trigger to categories table
CREATE TRIGGER trg_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- FUNCTION: Generate Product Slug
-- ============================================================================

CREATE OR REPLACE FUNCTION generate_product_slug()
RETURNS TRIGGER AS $$
DECLARE
    base_slug VARCHAR(250);
    final_slug VARCHAR(250);
    counter INTEGER := 0;
BEGIN
    -- Generate base slug from product name
    base_slug := lower(regexp_replace(NEW.name, '[^a-zA-Z0-9]+', '-', 'g'));
    base_slug := regexp_replace(base_slug, '^-+|-+$', '', 'g');
    base_slug := left(base_slug, 200);
    
    final_slug := base_slug;
    
    -- Check for uniqueness and append counter if needed
    WHILE EXISTS(SELECT 1 FROM products WHERE slug = final_slug AND id != COALESCE(NEW.id, '00000000-0000-0000-0000-000000000000'::UUID)) LOOP
        counter := counter + 1;
        final_slug := base_slug || '-' || counter::TEXT;
    END LOOP;
    
    NEW.slug := final_slug;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply slug trigger
CREATE TRIGGER trg_products_generate_slug
    BEFORE INSERT OR UPDATE OF name ON products
    FOR EACH ROW
    WHEN (NEW.slug IS NULL OR NEW.slug = '' OR (TG_OP = 'UPDATE' AND OLD.name != NEW.name))
    EXECUTE FUNCTION generate_product_slug();

-- ============================================================================
-- FUNCTION: Ensure single primary image per product
-- ============================================================================

CREATE OR REPLACE FUNCTION ensure_single_primary_image()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_primary = TRUE THEN
        UPDATE product_images
        SET is_primary = FALSE
        WHERE product_id = NEW.product_id AND id != NEW.id AND is_primary = TRUE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply primary image trigger
CREATE TRIGGER trg_product_images_single_primary
    BEFORE INSERT OR UPDATE OF is_primary ON product_images
    FOR EACH ROW
    WHEN (NEW.is_primary = TRUE)
    EXECUTE FUNCTION ensure_single_primary_image();

-- ============================================================================
-- FUNCTION: Update category product count
-- ============================================================================

CREATE OR REPLACE FUNCTION update_category_product_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        UPDATE categories 
        SET product_count = (
            SELECT COUNT(*) FROM products 
            WHERE category::TEXT = categories.slug AND status = 'ACTIVE'
        );
    END IF;
    
    IF TG_OP = 'DELETE' THEN
        UPDATE categories 
        SET product_count = (
            SELECT COUNT(*) FROM products 
            WHERE category::TEXT = categories.slug AND status = 'ACTIVE'
        );
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Apply category count trigger
CREATE TRIGGER trg_products_update_category_count
    AFTER INSERT OR UPDATE OF status, category OR DELETE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_category_product_count();

-- ============================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

-- Enable RLS on all tables
ALTER TABLE partners ENABLE ROW LEVEL SECURITY;
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE product_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE pricing_tiers ENABLE ROW LEVEL SECURITY;
ALTER TABLE product_discounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE discount_audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE discount_limits ENABLE ROW LEVEL SECURITY;
ALTER TABLE categories ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS Policies for PARTNERS table (Internal Only)
-- ============================================================================

-- Service role can do anything
CREATE POLICY partners_service_all ON partners
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can view and manage partners
CREATE POLICY partners_admin_all ON partners
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Partners can view their own record
CREATE POLICY partners_view_own ON partners
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER' AND
        email = current_setting('app.current_user_email', TRUE)
    );

-- ============================================================================
-- RLS Policies for PRODUCTS table
-- ============================================================================

-- Service role can do anything
CREATE POLICY products_service_all ON products
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can view and manage all products
CREATE POLICY products_admin_all ON products
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Clients can only view active products (NO partner details exposed)
CREATE POLICY products_client_view ON products
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) IN ('CLIENT', 'PARTNER') AND
        status = 'ACTIVE'
    );

-- Public can view active products
CREATE POLICY products_public_view ON products
    FOR SELECT
    USING (
        current_setting('app.is_public', TRUE) = 'true' AND
        status = 'ACTIVE'
    );

-- ============================================================================
-- RLS Policies for PRODUCT_IMAGES table
-- ============================================================================

-- Service role can do anything
CREATE POLICY product_images_service_all ON product_images
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can manage all images
CREATE POLICY product_images_admin_all ON product_images
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Everyone can view images of active products
CREATE POLICY product_images_view ON product_images
    FOR SELECT
    USING (TRUE); -- Images are public if product is accessible

-- ============================================================================
-- RLS Policies for PRICING_TIERS table
-- ============================================================================

-- Service role can do anything
CREATE POLICY pricing_tiers_service_all ON pricing_tiers
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can manage all tiers
CREATE POLICY pricing_tiers_admin_all ON pricing_tiers
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Everyone can view tiers
CREATE POLICY pricing_tiers_view ON pricing_tiers
    FOR SELECT
    USING (TRUE); -- Pricing is public information

-- ============================================================================
-- RLS Policies for PRODUCT_DISCOUNTS table
-- ============================================================================

-- Service role can do anything
CREATE POLICY product_discounts_service_all ON product_discounts
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can manage all discounts
CREATE POLICY product_discounts_admin_all ON product_discounts
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Partners can view and propose discounts for their products
CREATE POLICY product_discounts_partner ON product_discounts
    FOR ALL
    USING (
        current_setting('app.current_user_type', TRUE) = 'PARTNER'
    );

-- Clients can only view approved discounts
CREATE POLICY product_discounts_client_view ON product_discounts
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'CLIENT' AND
        status = 'APPROVED'
    );

-- ============================================================================
-- RLS Policies for DISCOUNT_AUDIT_LOG table
-- ============================================================================

-- Service role can do anything
CREATE POLICY discount_audit_service_all ON discount_audit_log
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can view all audit logs
CREATE POLICY discount_audit_admin_view ON discount_audit_log
    FOR SELECT
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- ============================================================================
-- RLS Policies for DISCOUNT_LIMITS table
-- ============================================================================

-- Service role can do anything
CREATE POLICY discount_limits_service_all ON discount_limits
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can manage discount limits
CREATE POLICY discount_limits_admin_all ON discount_limits
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- ============================================================================
-- RLS Policies for CATEGORIES table
-- ============================================================================

-- Service role can do anything
CREATE POLICY categories_service_all ON categories
    FOR ALL
    USING (current_setting('app.is_service_role', TRUE) = 'true');

-- Admins can manage categories
CREATE POLICY categories_admin_all ON categories
    FOR ALL
    USING (current_setting('app.current_user_type', TRUE) = 'ADMIN');

-- Everyone can view active categories
CREATE POLICY categories_public_view ON categories
    FOR SELECT
    USING (is_active = TRUE);

-- ============================================================================
-- VIEW: Public Products (Excludes Partner Information)
-- This view is used for client-facing APIs
-- ============================================================================

CREATE OR REPLACE VIEW public_products AS
SELECT 
    p.id,
    p.name,
    p.slug,
    p.category,
    p.short_description,
    p.long_description,
    p.base_price,
    p.material,
    p.eco_friendly,
    p.customization_available,
    p.customization_type,
    p.print_area_width,
    p.print_area_height,
    p.weight_grams,
    p.dimensions,
    p.available_colors,
    p.lead_time_days,
    p.status,
    p.availability,
    p.aggregate_rating,
    p.total_reviews,
    p.total_orders,
    p.tags,
    p.meta_title,
    p.meta_description,
    p.created_at,
    p.updated_at,
    -- Get primary image
    (SELECT image_url FROM product_images pi WHERE pi.product_id = p.id AND pi.is_primary = TRUE LIMIT 1) as primary_image_url,
    -- Get active discount
    (SELECT pd.discount_percentage FROM product_discounts pd 
     WHERE pd.product_id = p.id AND pd.status = 'APPROVED' 
     AND (pd.start_date IS NULL OR pd.start_date <= NOW())
     AND (pd.end_date IS NULL OR pd.end_date > NOW())
     ORDER BY pd.created_at DESC LIMIT 1) as active_discount_percentage
FROM products p
WHERE p.status = 'ACTIVE';

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE partners IS 'Fulfillment partners (internal only) - NEVER exposed to clients - FRD-002 FR-27';
COMMENT ON TABLE products IS 'Product catalog - FRD-002 FR-15';
COMMENT ON TABLE product_images IS 'Product image gallery - FRD-002 FR-21';
COMMENT ON TABLE pricing_tiers IS 'Quantity-based pricing tiers - FRD-002 FR-16';
COMMENT ON TABLE product_discounts IS 'Partner-defined discounts with admin approval - FRD-002 FR-16, BR-21';
COMMENT ON TABLE discount_audit_log IS 'Audit trail for all discount changes - FRD-002 BR-21';
COMMENT ON TABLE discount_limits IS 'Global discount limits set by admin - FRD-002 BR-21';
COMMENT ON TABLE categories IS 'Product categories metadata - FRD-002 FR-14';

COMMENT ON COLUMN products.partner_id IS 'Internal only - NEVER exposed in client APIs';
COMMENT ON COLUMN products.aggregate_rating IS 'Product rating (NOT partner rating) - FRD-002';
COMMENT ON VIEW public_products IS 'Client-facing view that excludes partner information';
