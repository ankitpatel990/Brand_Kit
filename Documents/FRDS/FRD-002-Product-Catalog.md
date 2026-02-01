# FRD-002: Product Catalog Management

---

## 1. FRD METADATA

**FRD Title**: Product Catalog and Inventory Display System  
**FRD ID**: FRD-002  
**Related PRD Section(s)**: 4.2.1 Core Features - Product Catalog  
**Priority**: High  
**Owner**: Product / Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Develop a comprehensive product catalog system that enables clients to browse, search, filter, and view detailed information about customizable promotional merchandise across multiple categories, with dynamic pricing based on quantity and customization level.

### Business Value
- Showcase products in an organized, accessible manner (partner details remain internal)
- Enable quick product discovery for time-sensitive B2B clients
- Support pricing transparency and comparison
- Foundation for customization and ordering workflows
- Drive conversion through effective product presentation

---

## 3. SCOPE

### In Scope
- Product catalog with 5-10 initial items across core categories
- Category structure: Bags, Pens, Water Bottles, Diaries, T-Shirts
- Product listing page with grid/list view toggle
- Advanced filtering: Price range, Material, Product Rating, Customization Options
- Search functionality with keyword and category search
- Product detail page with specifications, images, pricing tiers
- Dynamic pricing calculator based on quantity and customization level (pricing defined by fulfillment partners, managed by platform)
- Product image gallery with zoom capability
- Responsive design for desktop and mobile web
- Sort options: Price (low-high, high-low), Rating, Newest, Popular
- Quick view modal for rapid product browsing
- Product availability status
- Discount display (partner-defined, platform-managed)

### Out of Scope
- Inventory management (partner-controlled internally)
- Product reviews and ratings (FRD-008)
- Wishlist/favorites functionality (Phase 2)
- Product comparison feature
- Recently viewed products
- Related/recommended products (AI-powered in Phase 2)
- Bulk CSV product upload (admin feature in separate FRD)
- Real-time inventory sync with partners
- Product variants (e.g., color, size) - handled as separate products in MVP
- International products or pricing
- Partner/seller information display (all partner details remain internal)

---

## 4. USER STORIES

### Client Users
- **US-011**: As a Corporate Procurement Manager, I want to browse products by category so that I can quickly find items relevant to employee welcome kits
- **US-012**: As an Event Organizer, I want to filter products by material type (eco-friendly) so that I can meet sustainability requirements
- **US-013**: As a Client, I want to see pricing tiers and applicable discounts based on quantity so that I can plan my budget for bulk orders
- **US-014**: As a Client, I want to search for specific items (e.g., "water bottle") so that I can quickly locate products
- **US-015**: As a Client, I want to view multiple product images so that I can assess quality and design options
- **US-016**: As a Client, I want to see product ratings and reviews so that I can make informed purchasing decisions
- **US-017**: As a Client, I want to see if a product is available for customization so that I can plan my branding needs
- **US-018**: As a Client, I want to sort products by price so that I can find options within my budget

### Partner Users (Internal - Fulfillment Partners)
- **US-019**: As a Partner, I want my products displayed with accurate information so that I can fulfill orders correctly
- **US-019b**: As a Partner, I want to define and manage discounts for my products so that I can attract clients and manage inventory

### Admin Users
- **US-020**: As an Admin, I want to add new products to the catalog so that I can expand offerings
- **US-021**: As an Admin, I want to edit product details so that I can keep information current
- **US-022**: As an Admin, I want to associate products with fulfillment partners (internal) so that orders route correctly
- **US-023**: As an Admin, I want to deactivate products so that unavailable items don't appear in searches
- **US-024**: As an Admin, I want to enable/disable partner discounts and set limits so that I can manage pricing strategy and prevent abuse

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-14: Category Structure
The system shall implement a hierarchical category structure:
- **Level 1 (Primary Categories)**: Bags, Pens, Water Bottles, Diaries, T-Shirts, Other
- Each product must belong to exactly one primary category
- Category navigation displayed prominently on catalog page
- Category page shows all products within that category

### FR-15: Product Data Model
Each product shall contain the following attributes:
- **Product ID**: Unique identifier (UUID)
- **Product Name**: 5-200 characters
- **Category**: Primary category (enum)
- **Short Description**: 50-300 characters (displayed in listing)
- **Long Description**: 300-2000 characters (displayed in detail page)
- **Base Price**: Numeric (INR), minimum ₹1
- **Material**: Text (e.g., "Cotton", "Stainless Steel", "Recycled Paper")
- **Eco-Friendly Flag**: Boolean
- **Customization Available**: Boolean
- **Customization Type**: Enum (Logo Print, Embroidery, Engraving, None)
- **Print Area Dimensions**: Width x Height (cm)
- **Image URLs**: Array of 3-8 image URLs
- **Partner ID**: Reference to fulfillment partner (internal only, NOT displayed to clients)
- **Product Rating**: Numeric (1.0-5.0) (aggregate product rating, NOT partner rating)
- **Lead Time**: Days (integer)
- **Status**: Active/Inactive
- **Tags**: Array of strings (e.g., "premium", "trending", "eco")
- **Discount Enabled**: Boolean (partner-defined, platform-managed)
- **Discount Percentage**: Numeric (if enabled, partner-defined with platform limits)
- **Created Date**: Timestamp
- **Last Updated**: Timestamp

**Note**: All partner details (Partner ID, name, location, ratings) remain in internal database tables and are NEVER exposed to client-facing APIs, UI, or logs.

### FR-16: Pricing Tier Structure
Each product shall have quantity-based pricing tiers (defined by fulfillment partners, managed by platform):
- **Quantity Range**: Min-Max units
- **Unit Price**: Price per item in INR (partner-defined)
- **Discount Application**: If partner discount enabled, apply on top of tier pricing
- Minimum 3 tiers:
  - Tier 1: 1-49 units → Base price
  - Tier 2: 50-199 units → Partner-defined discount (typically 5-10%)
  - Tier 3: 200+ units → Partner-defined discount (typically 10-15%)
- Admin can set maximum discount limits (e.g., no more than 20% discount)
- Display all tiers in a table format on product detail page
- Highlight applicable tier based on quantity selector
- Show "Special Discount Applied" badge if partner discount is active

### FR-17: Product Listing Page
The system shall display products in a responsive grid layout:
- **Default view**: 12 products per page (4 columns on desktop, 2 on tablet, 1 on mobile)
- **Product card** displays:
  - Primary product image
  - Product name
  - Category badge
  - Starting price ("From ₹XXX") with discount badge if applicable
  - Eco-friendly badge (if applicable)
  - Product rating (stars + numeric) - aggregate product rating, NOT partner rating
  - "Customizable" badge (if applicable)
  - "Quick View" button on hover
- Pagination controls at bottom (Previous, Page numbers, Next)
- View toggle: Grid / List view
- Loading skeleton during data fetch
- NO partner/seller information displayed anywhere on listing page

### FR-18: Search Functionality
The system shall provide search with the following capabilities:
- **Search bar**: Prominent position in header, available on all pages
- **Search input**: Minimum 2 characters to trigger search
- **Search scope**: Product name, description, tags, category
- **Results page**: Displays matching products with relevance scoring
- **No results handling**: Display "No products found" with suggestions:
  - "Try different keywords"
  - "Browse categories" links
  - Show popular products as fallback
- **Search history**: Store last 5 searches per user (session-based)
- **Autocomplete**: Suggest products and categories as user types (debounced 300ms)

### FR-19: Filtering System
The system shall provide multi-select filters:
- **Price Range**: Slider with min-max values (₹0-₹10,000 default, adjustable)
- **Material**: Checkbox list (Cotton, Polyester, Stainless Steel, Plastic, Paper, Glass, Bamboo, Other)
- **Eco-Friendly**: Toggle filter
- **Product Rating**: Minimum rating selector (3+, 4+, 4.5+, 5) - filters by aggregate product rating only
- **Customization Type**: Checkbox list (Logo Print, Embroidery, Engraving)
- **Category**: Checkbox list (all categories)
- **Lead Time**: Radio buttons (<7 days, 7-14 days, 14+ days)
- **Discounts**: Toggle to show only discounted products (partner-defined discounts)
- Filters persist during session
- "Clear All Filters" button
- Active filter count badge
- Filter application triggers immediate results update (no "Apply" button needed)
- Applied filters displayed as removable chips above product grid
- NO partner/seller filters available (all partner details internal)

### FR-20: Sorting Options
The system shall provide sorting with the following options:
- **Relevance** (default for search results)
- **Price: Low to High**
- **Price: High to Low**
- **Highest Rated**
- **Newest Arrivals**
- **Most Popular** (based on order count)
- Sorting persists during session
- Dropdown selector in toolbar above product grid

### FR-21: Product Detail Page
The system shall display comprehensive product information:
- **Image Gallery**:
  - Primary image (large display, 600x600px)
  - Thumbnail strip (4-8 images)
  - Click thumbnail to change primary image
  - Zoom on hover (2x magnification)
  - Full-screen lightbox on click
- **Product Information Section**:
  - Product name (H1)
  - Category breadcrumb (Home > Category > Product Name)
  - Short description
  - Product rating (stars + numeric) - aggregate product rating only
  - Base price (large, prominent) with discount badge if applicable
  - Eco-friendly badge (if applicable)
  - **NO partner/seller information displayed** (name, rating, profile link all removed)
- **Pricing Table**:
  - Quantity tiers with unit prices (partner-defined, platform-managed)
  - Discount information if applicable (partner-defined discount with platform limits)
  - Total price calculator
- **Specifications**:
  - Material
  - Dimensions (if applicable)
  - Weight
  - Available colors
  - Customization details (print area, technique)
  - Lead time
- **Action Buttons**:
  - "Customize & Order" (primary CTA)
  - "Add to Cart" (without customization, if allowed)
  - Share button (Copy link, Email)
- **Long Description**: Formatted text with product details

### FR-22: Quick View Modal
The system shall provide a quick view modal:
- Triggered by "Quick View" button on product card
- Modal overlay (centered, 800px width)
- Displays:
  - Product image (single, primary)
  - Product name and category
  - Price, discount badge (if applicable), and eco badge
  - Short description
  - Product rating (NOT partner rating)
  - "View Full Details" button
  - "Customize & Order" button
  - Close button (X icon)
- Prevents page navigation for rapid browsing
- Keyboard accessible (Esc to close)

### FR-23: Dynamic Price Calculator
The system shall calculate pricing dynamically:
- **Input**: Quantity selector (min: 1, max: 10,000)
- **Calculation**:
  - Determine applicable pricing tier based on quantity
  - Calculate: Quantity × Unit Price for tier
  - Add customization fee if applicable (+₹10-50 per unit based on type)
- **Output**:
  - Unit price for selected quantity
  - Total price (prominent, updated in real-time)
  - Savings vs. base tier (if applicable) - "Save ₹XXX (Y%)"
- Display on product detail page, updates as quantity changes

### FR-24: Product Availability Status
The system shall display product availability:
- **Available**: Green badge, "In Stock - Ready to Customize"
- **Limited**: Yellow badge, "Limited Availability"
- **Out of Stock**: Red badge, "Currently Unavailable" (product still viewable but CTA disabled)
- **Coming Soon**: Gray badge, "Coming Soon - Notify Me" (email capture)
- Status determined by vendor input (admin interface)

### FR-25: Responsive Behavior
The system shall adapt layout to screen size:
- **Desktop (≥1200px)**: 4-column grid, sidebar filters, full feature set
- **Tablet (768-1199px)**: 2-column grid, collapsible filters, all features
- **Mobile (<768px)**:
  - 1-column grid
  - Filters in bottom sheet modal
  - Sticky sort dropdown
  - Condensed product cards
  - Touch-optimized interactions
- Image loading optimized per device (responsive images, lazy loading)

### FR-26: Admin Product Management
Admins shall be able to manage products:
- **Add Product Form**:
  - All product fields (FR-15)
  - Image upload (3-8 images, max 5MB each, JPG/PNG)
  - Pricing tier editor (add/remove tiers) - partners define pricing, admin can override
  - Discount management: Enable/disable partner discounts, set max discount limits
  - Partner selector (dropdown) - internal assignment only, NOT displayed to clients
  - Save as Draft / Publish
- **Edit Product**: Same form pre-filled with existing data
- **Product List (Admin View)**:
  - Table with columns: Image, Name, Category, Partner (internal ref), Price, Discount Status, Status, Actions
  - Search and filter capabilities
  - Bulk actions: Activate, Deactivate, Delete, Enable/Disable Discounts
  - Inline edit for quick updates (price, status, discount limits)
- **Delete Product**: Soft delete (status: deleted, not visible to clients)
- **Image Management**: Drag-and-drop reorder, set primary image, delete images
- **Discount Oversight**: Admin can view, enable/disable, set limits (min/max %), audit usage, suspend abuse

### FR-27: Partner Association (Internal Only)
Products shall be linked to fulfillment partners (internal operations):
- Each product associated with exactly one partner
- **Partner information remains INTERNAL** - never exposed in:
  - Client-facing UI/APIs
  - Product pages
  - Invoices
  - Order confirmation emails
  - Any public-facing reports or logs
- Orders for product automatically route to associated partner (backend process)
- Admin can reassign product to different partner (internal admin panel only)
- Partner details accessible only in admin panel with proper authentication

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Browse Products by Category
1. Client lands on homepage
2. Client clicks category (e.g., "T-Shirts")
3. System fetches all active products in category
4. System displays products in grid view (default sort: Relevance)
5. Client scrolls through products
6. Client clicks "Quick View" on a product
7. System displays quick view modal with product summary
8. Client clicks "View Full Details"
9. System navigates to product detail page
10. Client views full information, pricing, and images

**Edge Cases**:
- If category empty: Display "No products in this category yet. Check back soon!"
- If network error: Display "Unable to load products. Retry" button
- If slow load: Display skeleton loaders for product cards

### Workflow 2: Search for Products
1. Client enters search term "water bottle" in search bar
2. System debounces input (300ms delay)
3. System shows autocomplete dropdown with:
   - Matching products (top 5)
   - Matching categories (if any)
4. Client selects suggestion or presses Enter
5. System navigates to search results page
6. System fetches products matching search term (name, description, tags)
7. System displays results with relevance sorting
8. System displays result count: "12 results for 'water bottle'"
9. Client applies filter: "Eco-Friendly"
10. System updates results to show only eco-friendly water bottles
11. System updates count: "5 results for 'water bottle'"

**Edge Cases**:
- If no results: Display "No products found for 'X'" with category suggestions
- If search term <2 chars: Do not trigger search, show placeholder
- If special characters: Sanitize input, search cleaned term

### Workflow 3: Filter and Sort Products
1. Client on product listing page (e.g., "All Products")
2. Client opens filter panel (desktop: sidebar, mobile: bottom sheet)
3. Client selects filters:
   - Price range: ₹100-₹500 (slider)
   - Material: Cotton, Bamboo (checkboxes)
   - Vendor Rating: 4+ stars
4. System applies filters in real-time (each selection triggers update)
5. System displays filtered products (e.g., 18 results)
6. System shows active filters as chips above grid: "₹100-₹500", "Cotton", "Bamboo", "4+ stars"
7. Client changes sort to "Price: Low to High"
8. System re-sorts filtered products by price ascending
9. Client clicks "X" on "Bamboo" filter chip
10. System removes Bamboo filter, updates results (e.g., 25 results now)

**Edge Cases**:
- If filters result in 0 products: Display "No products match your filters. Try adjusting them." with "Clear Filters" button
- If all filters cleared: Return to default view (all products, relevance sort)
- If conflicting filters (edge case): Apply all, result in empty set

### Workflow 4: View Product Details and Calculate Price
1. Client clicks product card "Branded T-Shirt"
2. System navigates to product detail page (URL: /products/{product-id})
3. System loads product data from API
4. System displays:
   - Image gallery (6 images)
   - Product name, category, description
   - Vendor: "PrintMaster Gujarat" (4.5 stars)
   - Base price: ₹250/unit
   - Pricing table:
     - 1-49 units: ₹250/unit
     - 50-199 units: ₹230/unit (8% off)
     - 200+ units: ₹210/unit (16% off)
5. Client enters quantity: 75
6. System calculates:
   - Applicable tier: 50-199 units
   - Unit price: ₹230
   - Total: 75 × ₹230 = ₹17,250
   - Savings: ₹1,500 (8%) vs. base tier
7. System updates price display in real-time
8. Client clicks "Customize & Order"
9. System redirects to Customization Engine (FRD-003) with product context

**Edge Cases**:
- If product not found (invalid ID): Display 404 page "Product not found"
- If product inactive: Display "This product is no longer available"
- If quantity exceeds max (10,000): Display error "Maximum quantity is 10,000"
- If image fails to load: Display placeholder image, allow user to retry

### Workflow 5: Admin Adds New Product
1. Admin logs into admin panel
2. Admin navigates to Products > Add New Product
3. System displays product creation form
4. Admin fills fields:
   - Name: "Premium Jute Bag"
   - Category: Bags
   - Description: "Eco-friendly jute tote bag..."
   - Material: Jute
   - Eco-Friendly: Yes
   - Customization: Logo Print
   - Base Price: ₹150
5. Admin uploads 5 images (drag & drop)
6. System validates images (size, format)
7. System uploads images to cloud storage
8. Admin adds pricing tiers:
   - 1-49: ₹150
   - 50-199: ₹135
   - 200+: ₹120
9. Admin selects vendor: "Gujarat Jute Co."
10. Admin sets lead time: 10 days
11. Admin clicks "Publish"
12. System validates all required fields
13. System creates product record in database
14. System displays success: "Product published successfully"
15. System redirects to product detail page (preview)
16. Product now visible to all clients in "Bags" category

**Edge Cases**:
- If required field missing: Highlight field, show error "This field is required"
- If image upload fails: Show error per image, allow retry
- If duplicate product name: Warn admin "Similar product exists: [name]", allow proceed or cancel
- If vendor not selected: Show error "Please select a vendor"

---

## 7. INPUT & OUTPUT

### Inputs

#### Product Search
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Search Query | Text | 2-100 characters | Yes |

#### Product Filters
| Filter | Type | Options/Range |
|--------|------|---------------|
| Price Range | Range Slider | ₹0 - ₹10,000 |
| Material | Multi-select Checkbox | Cotton, Polyester, Steel, Plastic, Paper, Glass, Bamboo, Other |
| Eco-Friendly | Toggle | Yes/No |
| Vendor Rating | Single-select | 3+, 4+, 4.5+, 5 |
| Customization Type | Multi-select Checkbox | Logo Print, Embroidery, Engraving |
| Lead Time | Single-select Radio | <7 days, 7-14 days, 14+ days |

#### Add/Edit Product Form (Admin)
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Product Name | Text | 5-200 chars | Yes |
| Category | Dropdown | Enum values | Yes |
| Short Description | Textarea | 50-300 chars | Yes |
| Long Description | Rich Text | 300-2000 chars | Yes |
| Base Price | Number | ≥₹1, max ₹100,000 | Yes |
| Material | Text | 2-50 chars | Yes |
| Eco-Friendly | Checkbox | Boolean | No |
| Customization Available | Checkbox | Boolean | No |
| Customization Type | Dropdown | Enum values | If customization = Yes |
| Print Area (W × H) | Two number inputs | 1-100 cm each | If customization = Yes |
| Images | File upload | 3-8 images, JPG/PNG, max 5MB each | Yes |
| Vendor | Dropdown | Active vendors | Yes |
| Lead Time | Number | 1-90 days | Yes |
| Pricing Tiers | Dynamic table | Min 3 tiers | Yes |
| Status | Radio | Active/Inactive | Yes |
| Tags | Tag input | 0-10 tags, 2-20 chars each | No |

### Outputs

#### Product Listing API Response
```json
{
  "status": "success",
  "data": {
    "products": [
      {
        "productId": "uuid-123",
        "name": "Branded T-Shirt",
        "category": "T-Shirts",
        "shortDescription": "Premium cotton tee for corporate branding",
        "basePrice": 250,
        "imageUrl": "https://cdn.brandkit.com/products/tshirt-1.jpg",
        "ecoFriendly": false,
        "customizable": true,
        "vendorName": "PrintMaster Gujarat",
        "vendorRating": 4.5,
        "leadTime": 7
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalProducts": 58,
      "perPage": 12
    },
    "appliedFilters": {
      "priceRange": [100, 500],
      "materials": ["Cotton"],
      "ecoFriendly": null
    }
  }
}
```

#### Product Detail API Response
```json
{
  "status": "success",
  "data": {
    "productId": "uuid-123",
    "name": "Branded T-Shirt",
    "category": "T-Shirts",
    "shortDescription": "Premium cotton tee for corporate branding",
    "longDescription": "High-quality 100% cotton t-shirt perfect for employee welcome kits...",
    "basePrice": 250,
    "material": "100% Cotton",
    "ecoFriendly": false,
    "customizable": true,
    "customizationType": "Logo Print",
    "printArea": { "width": 20, "height": 25 },
    "images": [
      "https://cdn.brandkit.com/products/tshirt-1.jpg",
      "https://cdn.brandkit.com/products/tshirt-2.jpg",
      "https://cdn.brandkit.com/products/tshirt-3.jpg"
    ],
    "vendor": {
      "vendorId": "uuid-456",
      "name": "PrintMaster Gujarat",
      "rating": 4.5,
      "location": "Ahmedabad"
    },
    "leadTime": 7,
    "pricingTiers": [
      { "minQty": 1, "maxQty": 49, "unitPrice": 250 },
      { "minQty": 50, "maxQty": 199, "unitPrice": 230 },
      { "minQty": 200, "maxQty": null, "unitPrice": 210 }
    ],
    "status": "Available",
    "tags": ["premium", "corporate", "customizable"],
    "specifications": {
      "weight": "180 GSM",
      "availableColors": ["White", "Black", "Navy", "Gray"],
      "sizes": ["S", "M", "L", "XL", "XXL"]
    }
  }
}
```

#### Price Calculation Output (Real-time)
```json
{
  "quantity": 75,
  "applicableTier": {
    "minQty": 50,
    "maxQty": 199,
    "unitPrice": 230
  },
  "unitPrice": 230,
  "subtotal": 17250,
  "customizationFee": 750,
  "totalPrice": 18000,
  "savings": {
    "amount": 1500,
    "percentage": 8
  }
}
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-11: Product Visibility
Only products with status "Active" are visible to clients in catalog and search. Inactive products are hidden but remain accessible to admins and partners (internal).

### BR-12: Pricing Tier Logic (Partner-Defined, Platform-Managed)
Pricing tiers must not overlap and must cover all quantity ranges starting from 1. Each tier must have a unit price lower than or equal to the previous tier. Maximum of 5 tiers allowed. Partners define tiers; admins can override if needed.

### BR-13: Image Requirements
Product images must be:
- Minimum 800×800 pixels (recommended 1200×1200)
- Maximum 5MB per image
- Formats: JPG, PNG (WebP for optimized storage)
- Minimum 3 images, maximum 8 images
- First image is primary (used in listings)

### BR-14: Customization Prerequisite
If "Customization Available" is Yes, then "Customization Type" and "Print Area" are mandatory fields.

### BR-15: Eco-Friendly Criteria
Products marked "Eco-Friendly" must use materials from approved list: Cotton (organic), Bamboo, Jute, Recycled Paper, Recycled Plastic, Glass (reusable). Admin must validate claims.

### BR-16: Partner Product Association (Internal Only)
Each product must be associated with exactly one active fulfillment partner. If partner is deactivated, their products become "Limited Availability" until reassigned. Partner details NEVER exposed to clients.

### BR-17: Dynamic Pricing and Discount Display
The price displayed on listing pages is the base price (Tier 1, quantity 1) with discount badge if applicable. The phrase "From ₹XXX" indicates bulk discounts are available. Partner-defined discounts clearly indicated.

### BR-18: Search Ranking Algorithm
Search results are ranked by:
1. Exact match in product name (highest weight)
2. Partial match in product name
3. Match in short description
4. Match in tags
5. Match in category
6. Product rating (aggregate product rating, NOT partner rating)

### BR-19: Filter Combination Logic
Multiple filters within the same category use OR logic (e.g., Material: Cotton OR Bamboo). Filters across categories use AND logic (e.g., Material: Cotton AND Price: ₹100-₹500).

### BR-20: Quantity Limits
Minimum order quantity: 1 unit (no MOQ enforcement per PRD).  
Maximum order quantity: 10,000 units per product per order.

### BR-21: Discount Ownership and Control (NEW)
- Discounts are DEFINED by fulfillment partners
- Platform admin can: Enable/disable discounts, Set upper limits (max %), Set lower limits (min %), Audit discount usage, Suspend discount abuse
- Discount changes by partners require platform approval if exceeding set limits
- All discount activity logged for audit purposes

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Product not found | CAT_001 | 404 | "Product not found" | Return to catalog or search |
| No products in category | CAT_002 | 200 | "No products available in this category yet" | Browse other categories |
| No search results | CAT_003 | 200 | "No products found for 'X'. Try different keywords." | Refine search or browse categories |
| Invalid product ID | CAT_004 | 400 | "Invalid product identifier" | Return to catalog |
| Image load failure | CAT_005 | N/A | "Unable to load image" | Display placeholder, retry button |
| API timeout | CAT_006 | 504 | "Request timed out. Please try again." | Retry button |
| Invalid price range | CAT_007 | 400 | "Invalid price range. Min must be less than max." | Adjust filter values |
| Invalid quantity | CAT_008 | 400 | "Quantity must be between 1 and 10,000" | Correct quantity input |
| Duplicate product name (admin) | CAT_009 | 409 | "A product with this name already exists" | Change name or proceed with warning |
| Image upload failure | CAT_010 | 413/415 | "Image too large or invalid format" | Use smaller image or correct format |
| Missing required field (admin) | CAT_011 | 400 | "Please fill in all required fields" | Complete missing fields |
| Invalid pricing tier | CAT_012 | 400 | "Pricing tiers must not overlap and must be sequential" | Fix tier ranges |
| Vendor not found | CAT_013 | 404 | "Selected vendor does not exist" | Select different vendor |
| Inactive product access | CAT_014 | 403 | "This product is no longer available" | Return to catalog |

### Error Handling Strategy
- Display user-friendly error messages inline or as toast notifications
- Provide actionable next steps (buttons, links)
- Log detailed errors server-side for debugging
- For critical errors (API down), display maintenance message with support contact
- For intermittent errors (network), provide retry mechanism with exponential backoff
- Never expose technical details (stack traces, database errors) to users

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-24**: Product listing page must load within 1.5 seconds (LCP)
- **NFR-25**: Image optimization: Serve WebP format with JPG fallback, lazy load below-the-fold images
- **NFR-26**: Implement infinite scroll or pagination (max 50 products per page load)
- **NFR-27**: Search autocomplete results must return within 300ms
- **NFR-28**: Filter/sort operations must update results within 500ms
- **NFR-29**: Product detail page must achieve 90+ Lighthouse performance score
- **NFR-30**: Implement server-side caching for frequently accessed products (Redis, 5-minute TTL)
- **NFR-31**: Use CDN for product images (CloudFront, Cloudflare)

### Scalability
- **NFR-32**: Catalog must support 10,000+ products without performance degradation
- **NFR-33**: API must handle 500 concurrent catalog requests
- **NFR-34**: Database indexes on: productId, category, status, vendorId, price
- **NFR-35**: Implement database query optimization (avoid N+1 queries, use joins efficiently)

### Accessibility
- **NFR-36**: All product images must have descriptive alt text
- **NFR-37**: Filter controls must be keyboard navigable (Tab, Enter, Space)
- **NFR-38**: Screen reader announcements for filter updates ("X products found")
- **NFR-39**: Color contrast ratio ≥ 4.5:1 for text on product cards
- **NFR-40**: Focus indicators visible on all interactive elements

### SEO
- **NFR-41**: Product pages must have unique meta titles: "{Product Name} | BrandKit"
- **NFR-42**: Meta descriptions: First 150 chars of short description
- **NFR-43**: Semantic HTML: Proper heading hierarchy (H1 for product name, H2 for sections)
- **NFR-44**: Structured data: Schema.org Product markup (name, price, availability, rating)
- **NFR-45**: Clean URLs: /products/{category}/{product-slug}
- **NFR-46**: Canonical tags to prevent duplicate content
- **NFR-47**: XML sitemap for all active products, updated weekly

### Security
- **NFR-48**: Admin product management endpoints require JWT authentication with "admin" role
- **NFR-49**: Input sanitization on all text fields to prevent XSS
- **NFR-50**: Rate limiting on search API: 60 requests per minute per user
- **NFR-51**: Image uploads validated for file type, size, and content (prevent malicious files)

---

## 11. ACCEPTANCE CRITERIA

### AC-11: Browse Products by Category
**Given** a client on the homepage  
**When** the client clicks the "T-Shirts" category  
**Then** the system navigates to the T-Shirts category page  
**And** displays all active products in that category  
**And** shows product cards with image, name, price, vendor rating  
**And** displays result count (e.g., "24 Products")

### AC-12: Search for Products
**Given** a client on any page with search bar  
**When** the client types "water" (≥2 characters)  
**Then** the system displays autocomplete suggestions within 300ms  
**And** shows matching product names and categories  
**When** the client presses Enter or selects a suggestion  
**Then** the system navigates to search results page  
**And** displays products matching "water" in name, description, or tags  
**And** shows result count (e.g., "8 results for 'water'")

### AC-13: Apply Multiple Filters
**Given** a client on product listing page  
**When** the client opens filter panel  
**And** selects "Eco-Friendly" toggle  
**And** selects "Material: Cotton, Bamboo"  
**And** sets "Price Range: ₹100-₹500"  
**Then** the system applies all filters immediately  
**And** updates product grid to show only matching products  
**And** displays active filters as chips: "Eco-Friendly", "Cotton", "Bamboo", "₹100-₹500"  
**And** shows updated result count  
**When** the client clicks "X" on "Cotton" chip  
**Then** the system removes Cotton filter  
**And** updates results to include non-cotton products

### AC-14: Sort Products
**Given** a client viewing filtered product list  
**When** the client selects "Price: Low to High" from sort dropdown  
**Then** the system re-sorts products by basePrice ascending  
**And** maintains applied filters  
**And** updates grid immediately without page reload

### AC-15: View Product Details
**Given** a client on product listing page  
**When** the client clicks a product card "Branded T-Shirt"  
**Then** the system navigates to product detail page  
**And** displays:
- Image gallery with 6 images
- Product name, category, description
- Vendor name (PrintMaster Gujarat) with 4.5-star rating
- Base price ₹250/unit
- Pricing table with 3 tiers
- Specifications (material, dimensions, lead time)
- "Customize & Order" button
- Eco-friendly badge (if applicable)

### AC-16: Calculate Dynamic Pricing
**Given** a client on product detail page with base price ₹250  
**And** pricing tiers: 1-49: ₹250, 50-199: ₹230, 200+: ₹210  
**When** the client enters quantity: 75  
**Then** the system identifies applicable tier: 50-199  
**And** calculates unit price: ₹230  
**And** calculates total: 75 × ₹230 = ₹17,250  
**And** displays savings: "Save ₹1,500 (8%)"  
**And** updates display in real-time as quantity changes

### AC-17: Quick View Product
**Given** a client on product listing page  
**When** the client hovers over a product card  
**And** clicks "Quick View" button  
**Then** the system displays modal overlay  
**And** shows product summary (image, name, price, short description, vendor rating)  
**And** provides "View Full Details" and "Customize & Order" buttons  
**When** the client clicks "View Full Details"  
**Then** the system closes modal and navigates to product detail page  
**When** the client presses Esc key  
**Then** the system closes modal

### AC-18: Admin Adds New Product
**Given** an admin logged into admin panel  
**When** the admin navigates to Products > Add New Product  
**And** fills all required fields:
- Name: "Premium Jute Bag"
- Category: Bags
- Description: "Eco-friendly jute tote..."
- Base Price: ₹150
- Material: Jute
- Vendor: Gujarat Jute Co.
**And** uploads 5 images (valid JPG, <5MB each)  
**And** adds 3 pricing tiers  
**And** clicks "Publish"  
**Then** the system validates all fields  
**And** uploads images to cloud storage  
**And** creates product record with status "Active"  
**And** displays success message "Product published successfully"  
**And** the product is immediately visible to clients in Bags category

### AC-19: Handle No Search Results
**Given** a client on search page  
**When** the client searches for "xyz123nonexistent"  
**And** no products match the search  
**Then** the system displays "No products found for 'xyz123nonexistent'"  
**And** shows suggestions: "Try different keywords" and "Browse Categories" links  
**And** displays 6 popular products as alternatives

### AC-20: Responsive Mobile Layout
**Given** a client accessing catalog on mobile device (375px width)  
**When** the page loads  
**Then** the system displays 1-column product grid  
**And** provides bottom sheet for filters (collapsed by default)  
**And** displays sticky sort dropdown at top  
**And** shows condensed product cards with essential info  
**And** enables touch-based swipe for image gallery on detail page  
**And** all interactions work with touch gestures

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **CDN**: Cloudflare or AWS CloudFront for image delivery
- **Image Storage**: AWS S3 or Google Cloud Storage for product images
- **Search Engine**: Elasticsearch (optional for advanced search) or PostgreSQL full-text search

### Internal Dependencies
- Supabase with PostgreSQL database with tables:
  - `products`: Product records with Row-Level Security (RLS)
  - `product_pricing_tiers`: Quantity-based pricing tiers (relational foreign key to products)
  - `product_discounts`: Partner-defined discounts with platform limits
  - `categories`: Category hierarchy
  - `partners`: Fulfillment partner information (internal only, never exposed to clients)
  - `product_partner_assignments`: Linking table (internal, RBAC-protected)
- API endpoints (Spring Boot REST):
  - GET `/api/products` - List products (excludes partner details)
  - GET `/api/products/:id` - Get product details (excludes partner details)
  - GET `/api/products/search` - Search products (excludes partner details)
  - POST `/api/products` - Create product (admin, includes partner assignment internally)
  - PUT `/api/products/:id` - Update product (admin, includes discount management)
  - DELETE `/api/products/:id` - Soft delete product (admin)
  - GET `/api/categories` - List categories
  - GET `/api/admin/products/:id/partner` - Get partner details (admin-only, internal)
  - PUT `/api/admin/products/:id/discount` - Manage partner discount (admin-only)
- Frontend components (Next.js):
  - ProductGrid, ProductCard, ProductDetail (all exclude partner info)
  - FilterPanel, SearchBar, PriceCalculator, DiscountBadge
  - ImageGallery, QuickViewModal

### Integration Points
- **FRD-003 (Customization Engine)**: Product detail page links to customization workflow
- **FRD-004 (Order Management)**: Add to cart functionality sends product data to cart service
- **FRD-005 (Vendor Dashboard)**: Vendor information links to vendor profiles

---

## 13. ASSUMPTIONS

1. Product images are provided by fulfillment partners in high resolution
2. Admins manually curate and approve products before publishing (no partner self-serve for MVP)
3. Pricing tiers are defined by partners, managed by platform (admins can override)
4. Product inventory is managed by partners off-platform (no real-time sync for MVP)
5. All prices are in INR only
6. Gujarat-based partners provide timely product information (partner details remain internal)
7. Clients primarily browse on desktop but mobile responsiveness is essential
8. English language only for MVP (Hindi in Phase 2)
9. Product categorization is flat (no subcategories) for MVP simplicity
10. Supabase/PostgreSQL provides sufficient query performance for product catalog operations
11. Partner details are never needed by clients for purchase decisions (platform trust model)

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Product reviews and ratings (FRD-008)
- Wishlist / Favorites functionality
- Product comparison tool (side-by-side)
- Recently viewed products (personalization)
- AI-powered product recommendations based on browsing history
- Related products section on detail page
- Product variants (color, size as dropdowns rather than separate products)
- Real-time inventory sync with vendor systems
- Bulk product import/export via CSV (admin)
- Advanced search filters: Delivery date, Vendor location
- Video product demos in gallery
- 360° product images or 3D previews (before customization)
- Multi-language support (Hindi, Gujarati)
- Multi-currency for pan-India expansion

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Database schema design for product model, API endpoint specification, UI wireframes
