# FRD-002: Product Catalog Management - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the Product Catalog and Inventory Display System with testing instructions after each prompt.

---

## Sub-Prompt 1: Product Database Schema
**Task**: Create comprehensive product catalog database schema with partner associations (internal only).

**Implementation Details**:
- Create `products` table: id (UUID), name, category (enum), short_description, long_description, base_price, material, eco_friendly (boolean), customization_available, customization_type, print_area_width, print_area_height, partner_id (FK), status, created_at, updated_at
- Create `product_images` table: id, product_id (FK), image_url, display_order, is_primary
- Create `pricing_tiers` table: id, product_id (FK), min_quantity, max_quantity, unit_price
- Create `product_discounts` table: id, product_id (FK), partner_id (FK), discount_percentage, status (pending/approved/disabled), created_at
- Create `partners` table (internal): id, business_name, email, location, status (NOT exposed to clients)
- Implement RLS: Client APIs NEVER expose partner_id or partner details
- Create indexes on category, status, partner_id

**Testing Instructions**:
1. Run schema migration, verify all tables created
2. Insert sample product with partner association
   - Expected: Product created, partner_id stored but NOT exposed in client API
3. Test foreign key constraints: Insert product with invalid partner_id
   - Expected: Constraint violation error
4. Test enum validation: Insert product with invalid category
   - Expected: Enum constraint error
5. Verify pricing tiers: Insert tiers, check no overlaps allowed
6. Test RLS: Query products as client role
   - Expected: Partner details NOT visible in response
7. Check indexes: `SELECT * FROM pg_indexes WHERE tablename = 'products'`

---

## Sub-Prompt 2: Product Listing API with Filters
**Task**: Implement product listing endpoint with multi-filter support (no partner info exposed).

**Implementation Details**:
- Create endpoint: `GET /api/products`
- Query parameters: category, minPrice, maxPrice, material[], ecoFriendly, customizable, minRating, sort, page, limit
- Default sort: Relevance
- Pagination: 12 products per page
- Response includes: product data, pricing tiers, images (NO partner details)
- Apply filters: AND logic across categories, OR within categories
- Implement sorting: price (asc/desc), rating, newest, popular

**Testing Instructions**:
1. Get all products: `GET /api/products`
   - Expected: 200 OK, first 12 products, pagination metadata
2. Filter by category: `GET /api/products?category=T-Shirts`
   - Expected: Only T-Shirts returned
3. Filter by price range: `GET /api/products?minPrice=100&maxPrice=500`
   - Expected: Products within ₹100-₹500
4. Filter by multiple materials: `GET /api/products?material=Cotton&material=Bamboo`
   - Expected: Products with Cotton OR Bamboo
5. Combine filters: `GET /api/products?category=Bags&ecoFriendly=true&maxPrice=300`
   - Expected: Eco-friendly bags under ₹300
6. Sort by price: `GET /api/products?sort=price_asc`
   - Expected: Products sorted low to high
7. Test pagination: `GET /api/products?page=2&limit=12`
   - Expected: Products 13-24
8. **CRITICAL**: Verify NO partner info in response (no partner names, IDs, locations)

---

## Sub-Prompt 3: Product Search with Autocomplete
**Task**: Implement full-text search with autocomplete suggestions.

**Implementation Details**:
- Create endpoint: `GET /api/products/search?q={query}`
- Implement PostgreSQL full-text search or Elasticsearch integration
- Search scope: product name, description, tags, category
- Minimum 2 characters to trigger search
- Autocomplete endpoint: `GET /api/products/autocomplete?q={query}`
- Return: Top 5 matching products, matching categories
- Ranking: Exact match > Partial match > Tag match
- Debounce: 300ms on frontend

**Testing Instructions**:
1. Search "water bottle": `GET /api/products/search?q=water%20bottle`
   - Expected: Products with "water" or "bottle" in name/description
2. Search with 1 character: `GET /api/products/search?q=t`
   - Expected: 400 Bad Request, "Minimum 2 characters required"
3. Search with special characters: `GET /api/products/search?q=bag%20%26%20pen`
   - Expected: Search sanitized, results returned
4. Autocomplete: `GET /api/products/autocomplete?q=wa`
   - Expected: Suggestions list (products + categories)
5. Search non-existent product: `GET /api/products/search?q=xyz123notfound`
   - Expected: 200 OK, empty results array, fallback suggestions
6. Test search ranking: Search "branded"
   - Expected: Exact match "Branded T-Shirt" ranked first
7. Performance test: Search with 50 concurrent requests
   - Expected: Response time < 500ms

---

## Sub-Prompt 4: Product Detail API
**Task**: Implement comprehensive product detail endpoint (partner details remain internal).

**Implementation Details**:
- Create endpoint: `GET /api/products/:productId`
- Return: Full product data, all images, pricing tiers, specifications
- **DO NOT** return: partner_id, partner name, partner location
- Calculate: Applicable pricing tier based on quantity
- Include: Reviews/ratings (aggregate only, not partner ratings)
- Generate: Print-ready image preview URL
- Handle: Product not found, inactive products

**Testing Instructions**:
1. Get product details: `GET /api/products/uuid-tshirt-001`
   - Expected: 200 OK, complete product data
2. Verify response structure:
   - Present: name, description, images, pricing tiers, specifications
   - **ABSENT**: partner_id, partner name, vendor details
3. Test with invalid product ID
   - Expected: 404 Not Found, "Product not found"
4. Test with inactive product
   - Expected: 403 Forbidden or "Product not available"
5. Verify image URLs: All images accessible via CDN
6. Test pricing tiers: Check tier logic (50 units → Tier 2 price)
7. Load test: 100 concurrent requests for same product
   - Expected: Response time < 1 second

---

## Sub-Prompt 5: Admin Product Management UI
**Task**: Create admin interface for adding/editing products with partner assignment (internal only).

**Implementation Details**:
- Create endpoints:
  - `POST /api/admin/products` (create product)
  - `PUT /api/admin/products/:productId` (update product)
  - `DELETE /api/admin/products/:productId` (soft delete)
- Admin form includes: All product fields + Partner selector (dropdown)
- Image upload: 3-8 images, max 5MB each, upload to S3
- Print area configuration: Visual editor with drag handles
- Pricing tier editor: Add/remove tiers dynamically
- Partner assignment: Internal dropdown (NOT visible to clients)
- Validate: No overlapping tiers, all required fields

**Testing Instructions**:
1. Admin creates new product: `POST /api/admin/products`
   - Include: product data + partner_id (internal)
   - Expected: 201 Created, product saved with partner association
2. Verify partner association stored: Query database
   - Expected: partner_id present in products table
3. Verify client API: `GET /api/products/{newProductId}`
   - Expected: Product data returned, NO partner_id in response
4. Upload 5 images
   - Expected: Images uploaded to S3, URLs saved
5. Add 3 pricing tiers
   - Expected: Tiers saved, no overlaps
6. Try creating product without required fields
   - Expected: 400 Bad Request, validation errors
7. Update product, change partner assignment
   - Expected: Partner updated in database, still NOT exposed to clients
8. Soft delete product: `DELETE /api/admin/products/uuid`
   - Expected: Status changed to "deleted", not visible to clients

---

## Sub-Prompt 6: Dynamic Price Calculator
**Task**: Implement real-time pricing calculator based on quantity and customization.

**Implementation Details**:
- Create endpoint: `POST /api/products/:productId/calculate-price`
- Input: quantity, customization options
- Logic:
  - Find applicable pricing tier
  - Calculate: quantity × unit_price (from tier)
  - Add customization fee if applicable (+₹10-50 per unit)
  - Apply partner discount if enabled (partner-defined, platform-managed)
  - Return: unit price, subtotal, savings, discount info
- Update in real-time as quantity changes (frontend)

**Testing Instructions**:
1. Calculate price for 75 units: `POST /api/products/uuid/calculate-price` {quantity: 75}
   - Expected: Tier 2 price applied, correct total
2. Test quantity = 1
   - Expected: Base tier (Tier 1) price
3. Test quantity = 200
   - Expected: Tier 3 price applied
4. Test with customization: {quantity: 50, customization: true}
   - Expected: Tier price + customization fee
5. Test with partner discount enabled
   - Expected: Discount applied, savings displayed
6. Test quantity = 0 or negative
   - Expected: 400 Bad Request, "Invalid quantity"
7. Test quantity > 10,000 (max limit)
   - Expected: 400 Bad Request, "Max quantity is 10,000"
8. Verify savings calculation: Original - Discounted = Savings

---

## Sub-Prompt 7: Partner Discount Management (Admin Control)
**Task**: Implement partner discount system with admin approval and control.

**Implementation Details**:
- Partner proposes discount: `POST /api/partner/discounts` {product_id, discount_percentage}
- System checks: Admin-defined limits (min 0%, max 25%)
- Status: "Pending Admin Approval"
- Admin endpoints:
  - `GET /api/admin/discounts` (list all partner discounts)
  - `PUT /api/admin/discounts/:discountId/approve` (activate discount)
  - `PUT /api/admin/discounts/:discountId/disable` (override partner, disable discount)
  - `POST /api/admin/discounts/limits` (set global min/max limits)
- Discount audit log: Track all changes (partner/admin actions)

**Testing Instructions**:
1. Partner creates discount: `POST /api/partner/discounts` {product_id, discount: 10%}
   - Expected: Discount saved with status "pending"
2. Partner tries discount > 25%
   - Expected: 400 Bad Request, "Discount exceeds maximum limit"
3. Admin approves discount: `PUT /api/admin/discounts/uuid/approve`
   - Expected: Discount status = "approved", visible on client product page
4. Client views product
   - Expected: Discounted price shown, "Special Offer" badge
5. Admin disables discount: `PUT /api/admin/discounts/uuid/disable`
   - Expected: Discount no longer applied, clients see original price
6. Admin sets global limits: {min: 0%, max: 20%}
   - Expected: Future partner discounts validated against new limits
7. View audit log: `GET /api/admin/discounts/audit`
   - Expected: All discount changes logged (who, when, old/new %)

---

## Sub-Prompt 8: Product Image Management and CDN Integration
**Task**: Implement image upload, storage, and CDN delivery.

**Implementation Details**:
- Image upload: Drag-and-drop, multiple files
- Validation: JPG/PNG, max 5MB per image, min 800×800px
- Storage: AWS S3 or Google Cloud Storage
- CDN: CloudFront or Cloudflare for fast delivery
- Transformations: Generate thumbnails (200×200, 400×400, 800×800)
- Set primary image, reorder images
- Lazy loading on product listing page
- WebP format with JPG fallback

**Testing Instructions**:
1. Upload 5 images via admin panel
   - Expected: Images uploaded to S3, thumbnails generated
2. Verify CDN URLs: Images served from CDN (e.g., cdn.brandkit.com)
3. Test image validation: Upload 10MB file
   - Expected: 413 Payload Too Large error
4. Upload invalid format (PDF)
   - Expected: 415 Unsupported Media Type
5. Set primary image
   - Expected: is_primary flag updated, primary image shown first
6. Reorder images: Drag image 3 to position 1
   - Expected: display_order updated
7. Delete image
   - Expected: Image removed from S3 and database
8. Test lazy loading: Open product listing page
   - Expected: Below-fold images load as user scrolls

---

## Sub-Prompt 9: Product Quick View Modal
**Task**: Implement quick view modal for rapid product browsing.

**Implementation Details**:
- Trigger: "Quick View" button on product card (hover/click)
- Modal overlay: 800px width, centered, overlay background
- Display: Product image (single, primary), name, category, price, short description, rating
- Actions: "View Full Details" (navigate to detail page), "Customize & Order", Close (X icon)
- Keyboard accessible: Esc to close
- Prevent page navigation for rapid browsing
- **NO partner information displayed**

**Testing Instructions**:
1. Hover over product card, click "Quick View"
   - Expected: Modal opens with product summary
2. Verify modal content:
   - Present: image, name, price, description, rating
   - **ABSENT**: Partner/vendor info
3. Click "View Full Details"
   - Expected: Modal closes, navigate to full product page
4. Click "Customize & Order"
   - Expected: Navigate to customization page
5. Click X icon
   - Expected: Modal closes
6. Press Esc key
   - Expected: Modal closes
7. Click outside modal (overlay)
   - Expected: Modal closes
8. Test keyboard navigation: Tab through buttons
   - Expected: Focus visible, all buttons accessible

---

## Sub-Prompt 10: Product SEO and Structured Data
**Task**: Implement SEO optimization with Schema.org markup.

**Implementation Details**:
- Meta tags: Unique title, description per product
- Title format: "{Product Name} | BrandKit"
- Description: First 150 chars of short_description
- Schema.org Product markup: name, price, availability, rating, image
- Semantic HTML: H1 for name, H2 for sections
- Clean URLs: /products/{category}/{product-slug}
- Canonical tags: Prevent duplicate content
- XML sitemap: All active products, updated weekly
- Open Graph tags: For social media sharing

**Testing Instructions**:
1. View product page source
   - Expected: Meta tags present (<title>, <meta name="description">)
2. Validate Schema.org markup: Use Google Rich Results Test
   - Expected: Valid Product schema detected
3. Check URL structure: /products/t-shirts/branded-t-shirt
   - Expected: SEO-friendly slug format
4. Verify canonical tag: <link rel="canonical" href="...">
   - Expected: Points to correct product URL
5. Test Open Graph tags: Share product URL on Facebook/Twitter
   - Expected: Rich preview with image, title, price
6. Generate sitemap: `GET /sitemap.xml`
   - Expected: All active products listed
7. Google Search Console: Submit sitemap, check indexing
   - Expected: Products indexed by Google

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Product Browsing Flow**: Homepage → Category → Product List → Filters → Product Detail
2. **Search Flow**: Search "water" → Autocomplete → Search Results → Product Detail
3. **Admin Product Management**: Login as Admin → Add Product → Assign Partner (internal) → Publish → Verify Client View (NO partner info)
4. **Pricing Flow**: Product Detail → Select Quantity → See Tier Price → Apply Discount (if partner enabled) → Add to Cart
5. **Quick View Flow**: Product List → Quick View Modal → Customize & Order
6. **Partner Discount Flow**: Partner Proposes Discount → Admin Approves → Client Sees Discounted Price → Admin Disables → Client Sees Original Price
7. **Image Management**: Admin Uploads Images → Images on CDN → Lazy Loading on Client
8. **SEO Validation**: Google Rich Results Test → XML Sitemap → Social Media Sharing

---

## Security and Privacy Validation Checklist

- [ ] Partner information NEVER exposed in client-facing APIs
- [ ] Product-partner associations stored in database (internal only)
- [ ] Admin endpoints require admin role authorization
- [ ] Partner discount proposals validated against platform limits
- [ ] Admin can override/disable any partner discount
- [ ] Image uploads validated (size, format, malware scan)
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention (input sanitization)
- [ ] Rate limiting on search API (60 req/min per user)
- [ ] Database queries optimized (indexes, no N+1 queries)

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 3-4 weeks (including testing)
