-- FRD-003: Customization Engine Database Schema
-- Migration: V4__create_customization_schema.sql

-- Customization Drafts Table
CREATE TABLE IF NOT EXISTS customization_drafts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    -- Logo file information
    logo_file_url TEXT NOT NULL,
    logo_file_name VARCHAR(255) NOT NULL,
    logo_file_size BIGINT NOT NULL,
    logo_dimensions JSONB, -- {width: number, height: number}
    
    -- Crop data
    crop_data JSONB NOT NULL, -- {x, y, width, height, zoom, aspectRatio}
    cropped_image_url TEXT NOT NULL,
    
    -- Preview
    preview_image_url TEXT,
    
    -- Bundle information (if part of bundle)
    bundle_id UUID,
    bundle_name VARCHAR(255),
    
    -- Metadata
    status VARCHAR(50) DEFAULT 'draft', -- draft, completed
    expires_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '30 days'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customization_draft_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_customization_draft_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Customizations Table (completed customizations)
CREATE TABLE IF NOT EXISTS customizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    -- Logo file information
    logo_file_id UUID NOT NULL, -- Reference to uploaded file
    logo_file_url TEXT NOT NULL,
    logo_file_name VARCHAR(255) NOT NULL,
    logo_file_size BIGINT NOT NULL,
    logo_dimensions JSONB,
    
    -- Crop data
    crop_data JSONB NOT NULL,
    cropped_image_url TEXT NOT NULL,
    
    -- Preview and print images
    preview_image_url TEXT,
    print_image_url TEXT, -- High-res print-ready image (300 DPI)
    
    -- Status
    status VARCHAR(50) DEFAULT 'pending', -- pending, processing, completed, failed
    print_image_generated_at TIMESTAMP,
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customization_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_customization_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Bundles Table
CREATE TABLE IF NOT EXISTS bundles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bundle_name VARCHAR(255) NOT NULL,
    
    -- Bundle metadata
    total_price DECIMAL(10, 2),
    product_count INTEGER NOT NULL DEFAULT 0,
    
    -- Status
    status VARCHAR(50) DEFAULT 'draft', -- draft, completed
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bundle_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bundle Items Table (links customizations to bundles)
CREATE TABLE IF NOT EXISTS bundle_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bundle_id UUID NOT NULL REFERENCES bundles(id) ON DELETE CASCADE,
    customization_id UUID NOT NULL REFERENCES customizations(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bundle_item_bundle FOREIGN KEY (bundle_id) REFERENCES bundles(id),
    CONSTRAINT fk_bundle_item_customization FOREIGN KEY (customization_id) REFERENCES customizations(id),
    CONSTRAINT fk_bundle_item_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_bundle_item UNIQUE (bundle_id, customization_id)
);

-- Logo Files Table (stores uploaded logo files)
CREATE TABLE IF NOT EXISTS logo_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- File information
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL, -- image/png, image/jpeg, image/svg+xml
    file_url TEXT NOT NULL, -- S3 URL or storage path
    
    -- Image metadata
    width INTEGER,
    height INTEGER,
    
    -- Security
    is_validated BOOLEAN DEFAULT FALSE, -- Server-side validation flag
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP, -- For temporary files
    
    CONSTRAINT fk_logo_file_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for performance
CREATE INDEX idx_customization_drafts_user_id ON customization_drafts(user_id);
CREATE INDEX idx_customization_drafts_product_id ON customization_drafts(product_id);
CREATE INDEX idx_customization_drafts_expires_at ON customization_drafts(expires_at);
CREATE INDEX idx_customization_drafts_status ON customization_drafts(status);

CREATE INDEX idx_customizations_user_id ON customizations(user_id);
CREATE INDEX idx_customizations_product_id ON customizations(product_id);
CREATE INDEX idx_customizations_status ON customizations(status);

CREATE INDEX idx_bundles_user_id ON bundles(user_id);
CREATE INDEX idx_bundles_status ON bundles(status);

CREATE INDEX idx_bundle_items_bundle_id ON bundle_items(bundle_id);
CREATE INDEX idx_bundle_items_customization_id ON bundle_items(customization_id);

CREATE INDEX idx_logo_files_user_id ON logo_files(user_id);
CREATE INDEX idx_logo_files_expires_at ON logo_files(expires_at);

-- Row Level Security (RLS) policies
ALTER TABLE customization_drafts ENABLE ROW LEVEL SECURITY;
ALTER TABLE customizations ENABLE ROW LEVEL SECURITY;
ALTER TABLE bundles ENABLE ROW LEVEL SECURITY;
ALTER TABLE bundle_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE logo_files ENABLE ROW LEVEL SECURITY;

-- RLS Policies: Users can only access their own customizations
CREATE POLICY customization_drafts_user_policy ON customization_drafts
    FOR ALL
    USING (auth.uid() = user_id::text);

CREATE POLICY customizations_user_policy ON customizations
    FOR ALL
    USING (auth.uid() = user_id::text);

CREATE POLICY bundles_user_policy ON bundles
    FOR ALL
    USING (auth.uid() = user_id::text);

CREATE POLICY bundle_items_user_policy ON bundle_items
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM bundles
            WHERE bundles.id = bundle_items.bundle_id
            AND bundles.user_id::text = auth.uid()
        )
    );

CREATE POLICY logo_files_user_policy ON logo_files
    FOR ALL
    USING (auth.uid() = user_id::text);

-- Function to auto-delete expired drafts
CREATE OR REPLACE FUNCTION cleanup_expired_drafts()
RETURNS void AS $$
BEGIN
    DELETE FROM customization_drafts
    WHERE expires_at < CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_customization_drafts_updated_at
    BEFORE UPDATE ON customization_drafts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customizations_updated_at
    BEFORE UPDATE ON customizations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bundles_updated_at
    BEFORE UPDATE ON bundles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
