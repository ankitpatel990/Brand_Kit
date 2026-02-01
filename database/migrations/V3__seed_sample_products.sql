-- ============================================================================
-- BrandKit Sample Product Data
-- FRD-002: Product Catalog Sample Data for Testing
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- Insert sample partner (internal only)
INSERT INTO partners (id, business_name, email, phone, location, city, state, status, commission_rate, fulfillment_sla_days)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'PrintMaster Gujarat', 'partner1@printmaster.in', '+91-9876543210', 'GIDC Industrial Area', 'Ahmedabad', 'Gujarat', 'ACTIVE', 12.00, 7),
    ('22222222-2222-2222-2222-222222222222', 'Gujarat Jute Co.', 'partner2@gujtjute.in', '+91-9876543211', 'Textile Hub', 'Surat', 'Gujarat', 'ACTIVE', 10.00, 10),
    ('33333333-3333-3333-3333-333333333333', 'EcoBottle India', 'partner3@ecobottle.in', '+91-9876543212', 'Green Industrial Park', 'Vadodara', 'Gujarat', 'ACTIVE', 11.00, 5);

-- Insert sample products
INSERT INTO products (id, name, slug, category, short_description, long_description, base_price, material, eco_friendly, customization_available, customization_type, print_area_width, print_area_height, weight_grams, dimensions, available_colors, partner_id, lead_time_days, status, availability, aggregate_rating, total_reviews, total_orders, tags)
VALUES 
    -- T-Shirts
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 
     'Premium Cotton T-Shirt', 
     'premium-cotton-t-shirt',
     'T_SHIRTS',
     'High-quality 100% cotton t-shirt perfect for corporate branding. Comfortable fit with durable stitching for everyday wear.',
     'Experience premium comfort with our 100% cotton t-shirts, designed specifically for corporate branding needs. Each t-shirt features reinforced stitching, pre-shrunk fabric, and a classic fit that works for all body types. The breathable cotton material ensures comfort throughout the day, while the smooth surface provides an excellent canvas for your logo or design. Available in multiple colors to match your brand identity. Perfect for employee welcome kits, company events, and promotional giveaways.',
     250.00,
     '100% Combed Cotton, 180 GSM',
     FALSE,
     TRUE,
     'LOGO_PRINT',
     20.00,
     25.00,
     180,
     'Chest: 38-48 inches',
     ARRAY['White', 'Black', 'Navy Blue', 'Gray', 'Maroon'],
     '11111111-1111-1111-1111-111111111111',
     7,
     'ACTIVE',
     'AVAILABLE',
     4.5,
     128,
     450,
     ARRAY['premium', 'corporate', 'customizable', 'cotton']
    ),
    
    -- Bags
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 
     'Eco-Friendly Jute Tote Bag', 
     'eco-friendly-jute-tote-bag',
     'BAGS',
     'Sustainable jute tote bag with natural aesthetic. Perfect for eco-conscious corporate gifting and sustainable branding.',
     'Make a statement with our premium jute tote bags, crafted from 100% natural jute fibers. These eco-friendly bags combine sustainability with style, featuring reinforced handles and a spacious interior perfect for everyday use. The natural texture of jute provides a unique canvas for screen printing, making each bag a distinctive branded item. Ideal for corporate gifting, trade shows, and promotional campaigns where environmental responsibility matters. Each bag supports sustainable farming communities and reduces plastic waste.',
     150.00,
     'Natural Jute with Cotton Lining',
     TRUE,
     TRUE,
     'LOGO_PRINT',
     15.00,
     12.00,
     250,
     '40cm x 35cm x 12cm',
     ARRAY['Natural', 'Natural with Black Handle', 'Natural with Green Handle'],
     '22222222-2222-2222-2222-222222222222',
     10,
     'ACTIVE',
     'AVAILABLE',
     4.7,
     89,
     320,
     ARRAY['eco-friendly', 'sustainable', 'jute', 'organic', 'tote']
    ),
    
    -- Water Bottles
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 
     'Stainless Steel Water Bottle', 
     'stainless-steel-water-bottle',
     'WATER_BOTTLES',
     'Premium double-wall insulated stainless steel bottle. Keeps drinks hot for 12 hours, cold for 24 hours. Perfect for health-conscious branding.',
     'Stay hydrated in style with our premium stainless steel water bottles. Featuring double-wall vacuum insulation technology, these bottles keep your beverages at the perfect temperature all day long - hot drinks stay hot for up to 12 hours, and cold drinks remain refreshing for up to 24 hours. The food-grade 18/8 stainless steel construction is BPA-free, rust-resistant, and does not retain or impart flavors. The sleek, modern design includes a leak-proof cap and a wide mouth for easy filling and cleaning. Available in multiple finishes and sizes, perfect for corporate wellness programs and employee appreciation gifts.',
     350.00,
     '18/8 Stainless Steel, BPA-Free',
     TRUE,
     TRUE,
     'ENGRAVING',
     8.00,
     3.00,
     350,
     '500ml / 750ml capacity',
     ARRAY['Brushed Silver', 'Matte Black', 'Rose Gold', 'Navy Blue', 'Forest Green'],
     '33333333-3333-3333-3333-333333333333',
     5,
     'ACTIVE',
     'AVAILABLE',
     4.8,
     156,
     520,
     ARRAY['stainless-steel', 'insulated', 'eco-friendly', 'premium', 'hydration']
    ),
    
    -- Pens
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 
     'Executive Metal Ballpoint Pen', 
     'executive-metal-ballpoint-pen',
     'PENS',
     'Elegant metal ballpoint pen with smooth writing experience. Premium gift-quality writing instrument for professional branding.',
     'Elevate your brand with our executive metal ballpoint pens, designed for professionals who appreciate quality writing instruments. The perfectly balanced weight distribution ensures comfortable extended writing sessions, while the precision-engineered mechanism provides smooth, skip-free ink flow. The durable metal body features a sleek finish that exudes professionalism, making it an ideal choice for corporate gifts, client meetings, and business conferences. Each pen uses premium German ink cartridges for consistent, reliable performance. Comes in an elegant gift box, perfect for making a lasting impression.',
     75.00,
     'Brass with Chrome Finish',
     FALSE,
     TRUE,
     'ENGRAVING',
     5.00,
     0.50,
     35,
     '14cm length, 1cm diameter',
     ARRAY['Silver', 'Gold', 'Black', 'Blue', 'Red'],
     '11111111-1111-1111-1111-111111111111',
     3,
     'ACTIVE',
     'AVAILABLE',
     4.6,
     245,
     890,
     ARRAY['executive', 'premium', 'gift', 'metal', 'professional']
    ),
    
    -- Diaries
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 
     'Premium Leather-Bound Diary', 
     'premium-leather-bound-diary',
     'DIARIES',
     'Elegant A5 leather-bound diary with premium quality paper. Perfect for executive gifting and corporate branding.',
     'Document your success story with our premium leather-bound diaries, crafted for professionals who value both functionality and aesthetics. The genuine leather cover develops a beautiful patina over time, making each diary uniquely yours. Inside, you will find 200 pages of smooth, acid-free paper that is perfect for fountain pens, ballpoints, and even fine-tip markers without bleeding or feathering. The diary features a ribbon bookmark, expandable back pocket, and a magnetic clasp closure. Weekly and monthly planning layouts help organize busy schedules, while the elegant design makes it a statement piece on any desk. Ideal for corporate gifts, employee welcome kits, and client appreciation.',
     450.00,
     'Genuine Leather with Acid-Free Paper',
     FALSE,
     TRUE,
     'EMBROIDERY',
     12.00,
     15.00,
     450,
     'A5 Size (21cm x 14.8cm)',
     ARRAY['Brown', 'Black', 'Tan', 'Burgundy', 'Navy'],
     '22222222-2222-2222-2222-222222222222',
     7,
     'ACTIVE',
     'AVAILABLE',
     4.9,
     67,
     180,
     ARRAY['leather', 'premium', 'executive', 'planner', 'gift']
    ),
    
    -- Additional T-Shirt
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 
     'Organic Bamboo T-Shirt', 
     'organic-bamboo-t-shirt',
     'T_SHIRTS',
     'Ultra-soft organic bamboo fabric t-shirt. Naturally antibacterial and moisture-wicking. Sustainable choice for eco-conscious brands.',
     'Experience the future of sustainable fashion with our organic bamboo t-shirts. Made from 95% bamboo viscose and 5% spandex, these t-shirts offer unparalleled softness while being gentler on the planet. Bamboo is one of the worlds most sustainable resources, requiring no pesticides and minimal water to grow. The fabric is naturally antibacterial, hypoallergenic, and moisture-wicking, keeping you fresh and comfortable all day. The subtle sheen and drape of bamboo fabric elevate the look beyond ordinary cotton, making it perfect for brands that prioritize both style and sustainability.',
     350.00,
     '95% Bamboo Viscose, 5% Spandex',
     TRUE,
     TRUE,
     'LOGO_PRINT',
     18.00,
     22.00,
     160,
     'Chest: 36-46 inches',
     ARRAY['Natural White', 'Sage Green', 'Dusty Blue', 'Blush Pink', 'Charcoal'],
     '11111111-1111-1111-1111-111111111111',
     10,
     'ACTIVE',
     'AVAILABLE',
     4.7,
     43,
     150,
     ARRAY['bamboo', 'organic', 'sustainable', 'antibacterial', 'eco-friendly']
    );

-- Insert pricing tiers for each product
-- Premium Cotton T-Shirt
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 1, 49, 250.00, 0),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 2, 50, 199, 230.00, 8),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 3, 200, NULL, 210.00, 16);

-- Eco-Friendly Jute Tote Bag
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 1, 1, 49, 150.00, 0),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2, 50, 199, 135.00, 10),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 3, 200, NULL, 120.00, 20);

-- Stainless Steel Water Bottle
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 1, 1, 49, 350.00, 0),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 2, 50, 199, 315.00, 10),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 3, 200, NULL, 280.00, 20);

-- Executive Metal Ballpoint Pen
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 1, 1, 99, 75.00, 0),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 2, 100, 499, 65.00, 13),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 3, 500, NULL, 55.00, 27);

-- Premium Leather-Bound Diary
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 1, 1, 24, 450.00, 0),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 2, 25, 99, 410.00, 9),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 3, 100, NULL, 375.00, 17);

-- Organic Bamboo T-Shirt
INSERT INTO pricing_tiers (product_id, tier_number, min_quantity, max_quantity, unit_price, discount_percentage)
VALUES 
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 1, 1, 49, 350.00, 0),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 2, 50, 199, 320.00, 9),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 3, 200, NULL, 290.00, 17);

-- Insert sample product images
INSERT INTO product_images (product_id, image_url, thumbnail_url, medium_url, alt_text, display_order, is_primary)
VALUES 
    -- T-Shirt images
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://cdn.brandkit.com/products/tshirt-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/tshirt-1.jpg', 'https://cdn.brandkit.com/products/medium/tshirt-1.jpg', 'Premium Cotton T-Shirt Front View', 0, TRUE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://cdn.brandkit.com/products/tshirt-2.jpg', 'https://cdn.brandkit.com/products/thumbnails/tshirt-2.jpg', 'https://cdn.brandkit.com/products/medium/tshirt-2.jpg', 'Premium Cotton T-Shirt Back View', 1, FALSE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://cdn.brandkit.com/products/tshirt-3.jpg', 'https://cdn.brandkit.com/products/thumbnails/tshirt-3.jpg', 'https://cdn.brandkit.com/products/medium/tshirt-3.jpg', 'Premium Cotton T-Shirt Detail', 2, FALSE),
    
    -- Bag images
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://cdn.brandkit.com/products/bag-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/bag-1.jpg', 'https://cdn.brandkit.com/products/medium/bag-1.jpg', 'Eco-Friendly Jute Tote Bag Front', 0, TRUE),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://cdn.brandkit.com/products/bag-2.jpg', 'https://cdn.brandkit.com/products/thumbnails/bag-2.jpg', 'https://cdn.brandkit.com/products/medium/bag-2.jpg', 'Eco-Friendly Jute Tote Bag Detail', 1, FALSE),
    
    -- Water Bottle images
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'https://cdn.brandkit.com/products/bottle-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/bottle-1.jpg', 'https://cdn.brandkit.com/products/medium/bottle-1.jpg', 'Stainless Steel Water Bottle', 0, TRUE),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'https://cdn.brandkit.com/products/bottle-2.jpg', 'https://cdn.brandkit.com/products/thumbnails/bottle-2.jpg', 'https://cdn.brandkit.com/products/medium/bottle-2.jpg', 'Stainless Steel Water Bottle Open', 1, FALSE),
    
    -- Pen images
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'https://cdn.brandkit.com/products/pen-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/pen-1.jpg', 'https://cdn.brandkit.com/products/medium/pen-1.jpg', 'Executive Metal Ballpoint Pen', 0, TRUE),
    
    -- Diary images
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'https://cdn.brandkit.com/products/diary-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/diary-1.jpg', 'https://cdn.brandkit.com/products/medium/diary-1.jpg', 'Premium Leather-Bound Diary', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'https://cdn.brandkit.com/products/diary-2.jpg', 'https://cdn.brandkit.com/products/thumbnails/diary-2.jpg', 'https://cdn.brandkit.com/products/medium/diary-2.jpg', 'Premium Leather-Bound Diary Open', 1, FALSE),
    
    -- Bamboo T-Shirt images
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'https://cdn.brandkit.com/products/bamboo-tshirt-1.jpg', 'https://cdn.brandkit.com/products/thumbnails/bamboo-tshirt-1.jpg', 'https://cdn.brandkit.com/products/medium/bamboo-tshirt-1.jpg', 'Organic Bamboo T-Shirt', 0, TRUE);

-- Insert a sample approved discount
INSERT INTO product_discounts (product_id, partner_id, discount_percentage, discount_name, status, start_date, end_date)
VALUES 
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 15.00, 'New Year Special', 'APPROVED', NOW(), NOW() + INTERVAL '30 days');

-- Insert default discount limits
INSERT INTO discount_limits (min_discount_percentage, max_discount_percentage, category, is_active)
VALUES 
    (0, 25, NULL, TRUE);  -- Global limit: 0-25% for all categories

-- Update category product counts
UPDATE categories SET product_count = (
    SELECT COUNT(*) FROM products 
    WHERE products.category::TEXT = REPLACE(LOWER(categories.name), ' ', '_')
    AND products.status = 'ACTIVE'
);

-- ============================================================================
-- Comments
-- ============================================================================
COMMENT ON TABLE products IS 'Sample products seeded for development and testing - FRD-002';
