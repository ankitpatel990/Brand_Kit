# FRD-003: Customization Engine

---

## 1. FRD METADATA

**FRD Title**: Logo Customization and Real-Time Preview Engine  
**FRD ID**: FRD-003  
**Related PRD Section(s)**: 4.2.1 Core Features - Customization Engine, 4.1 High-Level Architecture (Frontend & Backend Rendering)  
**Priority**: High  
**Owner**: Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Develop a robust customization engine that enables clients to upload logos/designs, crop them to appropriate dimensions, and preview them overlaid on selected products in real-time using client-side rendering with server-side validation and high-resolution generation for print accuracy.

### Business Value
- Core differentiator: Real-time product visualization with client's branding
- Reduces design approval cycles and order revisions
- Builds client confidence before purchase
- Enables self-service customization without designer intervention
- Foundation for bundle builder and multi-product customization

---

## 3. SCOPE

### In Scope
- Logo/design upload (PNG, JPG, SVG formats, max 10MB)
- Image cropping tool with locked aspect ratio based on product print area
- Client-side preview rendering using HTML5 Canvas
- Real-time overlay of cropped logo on product base image at predefined coordinates
- Server-side re-rendering in high resolution (300 DPI) for print-ready images
- Preview generation for single products
- Bundle builder: Combine multiple products with the same logo into a kit
- Save customization for later (draft state)
- Download preview image (for approval workflows)
- Apply same logo to multiple products in one flow
- Mobile-responsive customization interface
- Customization data storage (crop coordinates, product IDs, logo URL)

### Out of Scope
- Advanced editing tools: Logo rotation, skew, color adjustments (MVP limitation per PRD)
- AI-powered logo placement suggestions (Phase 2)
- Multiple logos per product (single logo per product for MVP)
- Text overlay tool (add custom text beyond logo)
- 360° or 3D product preview rendering (static 2D images for MVP)
- Integration with design tools like Canva API (Phase 2)
- Template selection for pre-designed layouts
- Logo library/management (users upload per session)
- Real-time collaboration (multi-user editing)
- Undo/redo functionality beyond crop (single crop action only)

---

## 4. USER STORIES

### Client Users
- **US-024**: As a Corporate Procurement Manager, I want to upload my company logo so that I can see it on promotional products
- **US-025**: As an Event Organizer, I want to crop my logo to fit the print area so that it appears correctly on products
- **US-026**: As a Client, I want to see a real-time preview of my logo on a t-shirt so that I can approve the design before ordering
- **US-027**: As a Client, I want to apply the same logo to multiple products (bag, pen, bottle) so that I can create a cohesive welcome kit
- **US-028**: As a Client, I want to save my customization as a draft so that I can return later to finalize my order
- **US-029**: As a Client, I want to download a preview image so that I can share it with my team for approval
- **US-030**: As a Client, I want to build a bundle of 5 customized items so that I can order a complete kit in one transaction
- **US-031**: As a Client, I want to replace my logo if I uploaded the wrong file so that I can correct mistakes easily

### Partner Users (Fulfillment Partners - Internal)
- **US-032**: As a Partner, I want to receive print-ready high-resolution images so that I can produce accurate customized products
- **US-033**: As a Partner, I want to see exact crop coordinates so that I can align printing correctly

### Admin Users
- **US-034**: As an Admin, I want to configure print areas for each product so that logos are positioned consistently

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-28: Logo Upload
The system shall provide logo upload functionality with:
- **File Input**: Click to select or drag-and-drop
- **Supported Formats**: PNG, JPG/JPEG, SVG
- **File Size Limit**: Maximum 10MB
- **Validation**:
  - File type validation (MIME type check)
  - File size validation
  - Image dimension check (minimum 300×300px, recommended 1000×1000px)
  - Malicious file content check (server-side)
- **Upload Feedback**:
  - Progress indicator during upload
  - Success confirmation with thumbnail preview
  - Error messages for invalid files
- **Replace Option**: Allow users to upload different logo after initial upload

### FR-29: Logo Cropping Tool
The system shall implement a cropping interface using `react-easy-crop`:
- **Crop Area**:
  - Visual overlay rectangle indicating crop area
  - Locked aspect ratio based on product's print area dimensions
  - User can zoom (pinch/scroll) to adjust logo scale within crop area
  - User can pan (drag) to reposition logo within image
- **Constraints**:
  - Crop area aspect ratio matches product print area (e.g., 1:1, 4:3, 16:9)
  - Cannot rotate, resize crop area itself (locked)
  - Minimum crop area: 100×100px
- **Controls**:
  - Zoom slider: 1x to 3x
  - "Reset" button: Return to default zoom/position
  - "Apply Crop" button: Confirm crop and proceed to preview
- **Visual Feedback**:
  - Grid overlay (rule of thirds) for alignment assistance
  - Dimmed area outside crop region
  - Real-time update as user adjusts

### FR-30: Client-Side Preview Rendering
The system shall render preview using HTML5 Canvas API:
- **Process**:
  1. Load product base image (high-quality PNG with transparent print area)
  2. Load cropped logo image data (from crop tool output)
  3. Create canvas element matching product image dimensions
  4. Draw base product image on canvas
  5. Overlay cropped logo at predefined coordinates (from product metadata)
  6. Scale logo to match print area size
  7. Convert canvas to image (PNG) for display
- **Performance**:
  - Rendering must complete within 500ms on desktop
  - Smooth interaction during zoom/pan (60fps target)
- **Preview Display**:
  - Show preview immediately after "Apply Crop"
  - Display in high-quality (at least 800×800px canvas)
  - Option to zoom into preview for detail inspection
- **Update Trigger**: Preview regenerates if user edits crop

### FR-31: Server-Side High-Resolution Rendering
Upon order placement, the system shall:
- **Input**: Crop coordinates (x, y, width, height), product ID, logo file
- **Process**:
  1. Validate crop data (security check)
  2. Load high-resolution product image (300 DPI, print-ready)
  3. Load logo at original resolution
  4. Apply crop using provided coordinates
  5. Overlay logo on product image at scaled coordinates (for 300 DPI)
  6. Generate final print-ready image (TIFF or high-res PNG)
  7. Store image securely (cloud storage)
  8. Return image URL and metadata to order system
- **Output**: Print-ready image (minimum 3000×3000px, 300 DPI)
- **Storage**: Images stored with unique identifiers, associated with order ID

### FR-32: Product Print Area Configuration
Each customizable product shall have print area metadata:
- **Print Area Coordinates**: X, Y position (top-left corner) on product image
- **Print Area Dimensions**: Width × Height in pixels (for preview) and centimeters (for print)
- **Aspect Ratio**: Calculated from dimensions, enforced in crop tool
- **Example**:
  - T-Shirt: Print area 200×250px at (300, 150), aspect ratio 4:5
  - Mug: Print area 150×150px at (200, 180), aspect ratio 1:1
- **Admin Interface**: Configure print area per product (drag handles on product image)

### FR-33: Bundle Builder
The system shall allow creation of customized product bundles:
- **Workflow**:
  1. User completes customization for Product A (e.g., T-Shirt with logo)
  2. After preview, system displays "Add to Bundle" option
  3. User clicks "Add to Bundle"
  4. System saves customization (logo + crop data + product)
  5. User selects another product (e.g., Water Bottle)
  6. System asks: "Use same logo?" → Yes: Auto-apply logo / No: Upload new logo
  7. User crops logo for new product (different print area)
  8. User previews and adds to bundle
  9. Repeat for up to 10 products
  10. User reviews bundle summary (list of products with previews)
  11. User names bundle (e.g., "Employee Welcome Kit")
  12. User clicks "Add Bundle to Cart"
- **Bundle Display**:
  - Table showing all products in bundle with thumbnails
  - Quantity selector per product
  - Remove product from bundle option
  - Total price calculation for bundle
- **Bundle Limit**: Maximum 10 products per bundle

### FR-34: Save Draft Customization
The system shall allow users to save work-in-progress:
- **Trigger**: "Save Draft" button on customization page
- **Saved Data**:
  - Logo file (uploaded to server)
  - Crop coordinates
  - Product ID(s)
  - Preview image URL
  - Bundle name (if applicable)
  - Timestamp
- **Storage**: Associated with user account (user ID)
- **Retrieval**: "My Drafts" section in user dashboard
- **Draft Expiry**: Drafts auto-delete after 30 days
- **Draft Editing**: User can load draft, modify, and save again

### FR-35: Download Preview Image
The system shall provide preview download:
- **Trigger**: "Download Preview" button on preview page
- **Output**: PNG image (1200×1200px) of customized product
- **Filename**: `{ProductName}_Customized_{Timestamp}.png`
- **Use Case**: Clients can share with stakeholders for approval before ordering
- **Watermark**: Add subtle "BrandKit Preview" watermark (bottom-right, 30% opacity)

### FR-36: Customization Validation
The system shall validate customization before order:
- **Client-Side**:
  - Logo uploaded (required)
  - Crop applied (required)
  - Preview generated successfully
- **Server-Side**:
  - Logo file integrity check
  - Crop coordinates within valid bounds
  - Product still active and customizable
  - Logo resolution sufficient for print (minimum 300 DPI at print size)
- **Error Handling**:
  - If logo too low-res: Warning "Logo may appear blurry when printed. Recommend higher resolution."
  - If crop invalid: Error "Invalid crop area. Please recrop logo."

### FR-37: Multi-Product Logo Application
The system shall streamline applying logo to multiple products:
- **Flow**:
  1. User customizes first product, sees preview
  2. System displays "Apply to More Products" button
  3. User clicks, sees product selection modal
  4. User selects additional products (multi-select checkboxes)
  5. System auto-applies logo to each product with:
     - Crop adjusted for each product's print area aspect ratio
     - Auto-centering if aspect ratio matches
     - Prompt for manual crop if aspect ratios differ significantly
  6. System generates previews for all products
  7. User reviews all previews in carousel
  8. User accepts or adjusts individual crops
  9. All customizations added to bundle

### FR-38: Mobile Responsiveness
The customization interface shall adapt to mobile:
- **Upload**: Mobile-friendly file picker with camera option (take photo of logo)
- **Cropping**: Touch gestures:
  - Pinch to zoom
  - Drag to pan
  - Tap "Apply Crop" button
- **Preview**: Full-width display, pinch to zoom for detail
- **Bundle Builder**: Vertical list view on mobile
- **Performance**: Optimize image sizes for mobile bandwidth

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Single Product Customization
1. Client on product detail page clicks "Customize & Order"
2. System navigates to Customization page (`/customize/{productId}`)
3. System loads product details (name, base image, print area)
4. System displays:
   - Product preview area (large, center)
   - Logo upload section (right sidebar desktop / top mobile)
   - Instructions: "Upload your logo to get started"
5. Client clicks "Upload Logo" or drags file
6. System validates file (type, size)
7. If valid:
   - System displays cropping interface with uploaded logo
   - System calculates locked aspect ratio from product print area
   - Crop overlay appears with default zoom/position (logo centered)
8. Client adjusts zoom (slider) and pan (drag) to frame logo properly
9. Client clicks "Apply Crop"
10. System performs client-side crop:
    - Extracts cropped image data from canvas
    - Generates preview image (Canvas API)
11. System renders preview:
    - Draw product base image on canvas
    - Overlay cropped logo at print area coordinates
    - Display final preview (large, center of page)
12. System displays action buttons:
    - "Adjust Crop" (return to step 8)
    - "Download Preview"
    - "Save Draft"
    - "Add to Cart" (primary CTA)
13. Client clicks "Add to Cart"
14. System validates customization:
    - Logo uploaded: ✓
    - Crop applied: ✓
    - Preview generated: ✓
15. System prepares customization data:
    - Uploads logo to server (if not already)
    - Saves crop coordinates
    - Links to product ID
    - Generates preview URL
16. System adds customized product to cart
17. System displays success toast: "Added to cart!"
18. System offers: "Customize another product" or "View Cart"

**Edge Cases**:
- If file invalid: Display error "Invalid file type. Please upload PNG, JPG, or SVG", allow retry
- If file too large: "File exceeds 10MB limit. Please use smaller image"
- If logo too low-res: Warning "Logo resolution is low and may print blurry. Recommended: 1000×1000px minimum"
- If network error during upload: "Upload failed. Check connection and retry"
- If preview generation fails: "Unable to generate preview. Please try again"
- If user navigates away without saving: Prompt "You have unsaved changes. Save draft?"

### Workflow 2: Bundle Creation
1. Client completes single product customization (see Workflow 1, steps 1-12)
2. System displays "Create Bundle" button alongside "Add to Cart"
3. Client clicks "Create Bundle"
4. System:
   - Saves current customization as first bundle item
   - Displays "Bundle Builder" interface
   - Shows current item in bundle list (thumbnail, product name)
5. System prompts: "Add more products to your bundle (up to 10)"
6. System displays product selection grid (similar to catalog, filtered to customizable items)
7. Client selects "Water Bottle" product
8. System asks modal: "Use existing logo from T-Shirt?"
   - Option A: "Yes, use same logo" (default)
   - Option B: "No, upload different logo"
9. Client selects "Yes"
10. System:
    - Loads Water Bottle product details
    - Retrieves saved logo from T-Shirt customization
    - Calculates Water Bottle print area aspect ratio (1:1)
    - Compares to T-Shirt aspect ratio (4:5)
    - Aspect ratios differ, prompt required
11. System displays cropping interface for Water Bottle:
    - Pre-loaded with same logo
    - Adjusted crop overlay for 1:1 aspect ratio
    - Default zoom/position auto-calculated (centered)
12. Client adjusts crop for Water Bottle print area
13. Client clicks "Apply Crop"
14. System generates Water Bottle preview
15. Client reviews preview, clicks "Add to Bundle"
16. System adds Water Bottle to bundle list (now shows 2 items)
17. Client repeats steps 7-16 for "Pen" and "Diary"
18. Bundle now contains: T-Shirt, Water Bottle, Pen, Diary (4 items)
19. System displays bundle summary:
    - Table with columns: Preview, Product, Quantity, Unit Price, Subtotal
    - Bundle name input field
    - Total price calculation
20. Client enters bundle name: "Employee Welcome Kit"
21. Client adjusts quantities (e.g., 50 of each item)
22. System recalculates pricing:
    - Applies quantity tier pricing per product
    - Calculates total bundle price
23. Client clicks "Add Bundle to Cart"
24. System:
    - Validates all 4 customizations
    - Saves bundle as single cart item
    - Links all product customizations to bundle ID
25. System displays success: "Bundle added to cart!"
26. System redirects to cart page showing bundle

**Edge Cases**:
- If bundle limit reached (10 products): Disable "Add Product" button, show message "Maximum 10 products per bundle"
- If product out of stock after selection: Display "Product no longer available, remove from bundle"
- If user removes all products from bundle: Prompt "Bundle is empty. Return to customization?"
- If bundle exceeds cart value limit (if any): Error handled in cart module

### Workflow 3: Save and Load Draft
1. Client in middle of customization (logo uploaded, crop applied, preview shown)
2. Client needs to leave, clicks "Save Draft"
3. System validates: User must be logged in
   - If not logged in: Prompt "Please log in to save draft"
   - If logged in: Proceed
4. System saves draft:
   - Uploads logo to server (temp storage)
   - Saves crop coordinates (JSON: {x, y, width, height, zoom})
   - Saves product ID(s)
   - Generates preview thumbnail (400×400px)
   - Stores in `customizationDrafts` collection linked to user ID
   - Generates draft ID (UUID)
5. System displays success: "Draft saved successfully"
6. Client logs out and returns 3 days later
7. Client logs in and navigates to dashboard
8. Client clicks "My Drafts" section
9. System displays list of saved drafts:
   - Each draft shows: Preview thumbnail, Product name(s), Date saved
   - "Edit" and "Delete" buttons per draft
10. Client clicks "Edit" on "Employee Welcome Kit" draft
11. System:
    - Retrieves draft data (logo URL, crop coordinates, product IDs)
    - Loads customization page with saved state
    - Pre-populates logo (no re-upload needed)
    - Pre-fills crop coordinates
    - Regenerates preview from saved data
12. Client sees customization exactly as left 3 days ago
13. Client makes adjustment (changes crop slightly)
14. Client clicks "Update Draft" (or "Add to Cart" to finalize)
15. System updates draft with new data

**Edge Cases**:
- If draft expired (>30 days): Display "Draft expired and was deleted"
- If logo file deleted from temp storage: Display "Draft corrupted. Unable to load logo. Please start over."
- If product no longer available: Display "Product unavailable. You can still view preview but cannot order."
- If user not logged in trying to save: Redirect to login, then return to customization page

### Workflow 4: Server-Side High-Res Generation (Backend)
1. Client completes customization, adds to cart, proceeds to checkout
2. Client completes payment (FRD-004)
3. Order confirmed, system triggers order processing
4. For each customized product in order:
   - System calls backend API: `POST /api/customization/render-high-res`
   - Payload: {orderId, productId, logoFileId, cropData: {x, y, width, height, zoom}}
5. Backend receives request:
   - Validates order exists and is paid
   - Validates product allows customization
   - Validates crop data integrity (coordinates within bounds)
6. Backend retrieves:
   - High-resolution product base image (300 DPI, e.g., 3600×3600px)
   - Original uploaded logo file (full resolution)
   - Product print area metadata
7. Backend performs rendering (Java imaging library):
   - Load product image into BufferedImage
   - Load logo into BufferedImage
   - Apply crop transformation using cropData coordinates (scaled for high-res)
   - Resize cropped logo to match print area dimensions (e.g., 1000×1250px for t-shirt)
   - Composite logo onto product image at print area coordinates
   - Apply any necessary color correction or DPI adjustments
8. Backend generates print-ready file:
   - Format: PNG (lossless) or TIFF (if partner requires)
   - Resolution: 300 DPI
   - Color space: CMYK (for print) or RGB (depends on partner)
   - Filename: `{orderId}_{productId}_print.png`
9. Backend uploads print-ready image to cloud storage (AWS S3)
10. Backend stores metadata in database:
    - Order ID, Product ID, Print Image URL
    - Crop data (for reference)
    - Timestamp, Processing status: "completed"
11. Backend returns success response with print image URL
12. Order system attaches print image URL to order item
13. Fulfillment partner receives order with link to print-ready image (internal partner portal)

**Edge Cases**:
- If logo file missing: Backend returns error "Logo file not found", order flagged for manual review
- If rendering fails (library error): Backend retries once, if still fails, notifies admin
- If product image missing: Backend error, order held, admin notified
- If print image generation takes >10 seconds: Asynchronous processing, notify user "Order processing, you'll receive confirmation soon"

---

## 7. INPUT & OUTPUT

### Inputs

#### Logo Upload
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Logo File | File | PNG/JPG/SVG, max 10MB, min 300×300px | Yes |

#### Crop Data (Client-Side)
| Field | Type | Description |
|-------|------|-------------|
| x | Number | X coordinate of crop area top-left (pixels) |
| y | Number | Y coordinate of crop area top-left (pixels) |
| width | Number | Width of crop area (pixels) |
| height | Number | Height of crop area (pixels) |
| zoom | Number | Zoom level applied (1.0 - 3.0) |

#### Bundle Creation
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Bundle Name | Text | 5-100 characters | Yes |
| Products | Array | 2-10 product IDs with customization data | Yes |
| Quantities | Object | {productId: quantity}, quantity 1-10,000 per product | Yes |

### Outputs

#### Customization Preview (Client-Side)
- Canvas-rendered image (PNG, 800×800px)
- Displayed in preview area
- Downloadable on request

#### Saved Customization Data (Database)
```json
{
  "customizationId": "uuid-789",
  "userId": "uuid-123",
  "productId": "uuid-456",
  "logoFileId": "uuid-111",
  "logoUrl": "https://cdn.brandkit.com/uploads/logos/file-123.png",
  "cropData": {
    "x": 150,
    "y": 200,
    "width": 500,
    "height": 625,
    "zoom": 1.5,
    "aspectRatio": "4:5"
  },
  "previewUrl": "https://cdn.brandkit.com/previews/preview-789.png",
  "status": "draft",
  "createdAt": "2026-01-23T10:30:00Z",
  "expiresAt": "2026-02-22T10:30:00Z"
}
```

#### Bundle Data (Database)
```json
{
  "bundleId": "uuid-999",
  "userId": "uuid-123",
  "bundleName": "Employee Welcome Kit",
  "products": [
    {
      "productId": "uuid-tshirt",
      "customizationId": "uuid-789",
      "quantity": 50
    },
    {
      "productId": "uuid-bottle",
      "customizationId": "uuid-790",
      "quantity": 50
    }
  ],
  "totalPrice": 18500,
  "createdAt": "2026-01-23T11:00:00Z"
}
```

#### High-Resolution Print Image (Backend Output)
- File format: PNG or TIFF
- Resolution: 300 DPI
- Dimensions: Scaled to product specs (e.g., 3600×3600px for t-shirt)
- Storage: AWS S3 with URL returned
- Example URL: `https://s3.brandkit.com/prints/order-123-product-456-print.png`

#### Backend API Response (High-Res Rendering)
```json
{
  "status": "success",
  "message": "Print-ready image generated successfully",
  "data": {
    "orderId": "uuid-order-123",
    "productId": "uuid-456",
    "printImageUrl": "https://s3.brandkit.com/prints/order-123-product-456-print.png",
    "resolution": "3600x3600",
    "dpi": 300,
    "fileSize": "12.5 MB",
    "generatedAt": "2026-01-23T12:00:00Z"
  }
}
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-21: Logo File Requirements
Uploaded logos must meet:
- **Format**: PNG (preferred for transparency), JPG, or SVG
- **Size**: Maximum 10MB
- **Dimensions**: Minimum 300×300px, recommended 1000×1000px or higher
- **Content**: No animated images, single-layer

### BR-22: Crop Aspect Ratio Lock
Crop area aspect ratio is determined by product's print area and cannot be changed by user. For example:
- T-Shirt print area 20cm×25cm → Crop aspect ratio locked to 4:5
- Mug print area 10cm×10cm → Crop aspect ratio locked to 1:1

### BR-23: Preview vs. Print Resolution
Client-side preview is optimized for web display (typically 800×800px canvas). Print-ready images are generated server-side at 300 DPI (3000×3000px or higher) to ensure print quality.

### BR-24: Logo Reusability
A single uploaded logo can be applied to multiple products within the same session/bundle. Logo file is uploaded once and referenced by ID for subsequent customizations.

### BR-25: Customization Product Restriction
Only products with `customizationAvailable: true` can enter customization workflow. Products without this flag display "Not Customizable" and only offer standard "Add to Cart".

### BR-26: Bundle Size Limits
Bundles must contain:
- **Minimum**: 2 products
- **Maximum**: 10 products
- Enforced at bundle creation time

### BR-27: Draft Storage Duration
Saved drafts expire after 30 days. Users receive email reminder at 25 days. After expiry, drafts are permanently deleted, including uploaded logo files.

### BR-28: Customization Uniqueness
Each order item has a unique customization instance. If the same product with same logo is added to cart twice, they are stored as separate cart items with independent customization data (supports different quantities or minor variations).

### BR-29: Print Area Validation
Backend validates that crop coordinates fall within product's defined print area boundaries. Invalid coordinates reject the rendering request.

### BR-30: Logo Resolution Warning
If uploaded logo resolution is below recommended threshold (estimated <150 DPI at print size), system displays non-blocking warning: "Your logo may appear pixelated when printed. For best results, upload higher resolution (min 1000×1000px)."

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Invalid file type | CUST_001 | 400 | "Invalid file type. Please upload PNG, JPG, or SVG" | Upload correct file type |
| File too large | CUST_002 | 413 | "File exceeds 10MB limit. Please use smaller image" | Compress or use different file |
| File too small (resolution) | CUST_003 | N/A | "Logo resolution is low. May print blurry. Recommended: 1000×1000px" | Upload higher res (warning, not error) |
| Upload failed (network) | CUST_004 | 500 | "Upload failed. Please check connection and retry" | Retry upload button |
| Invalid crop coordinates | CUST_005 | 400 | "Invalid crop area. Please recrop logo" | Adjust crop and reapply |
| Crop aspect ratio mismatch | CUST_006 | 400 | "Crop aspect ratio must match product print area" | System enforces, should not occur |
| Preview generation failed | CUST_007 | 500 | "Unable to generate preview. Please try again" | Retry button, or refresh page |
| Product not customizable | CUST_008 | 403 | "This product does not support customization" | Return to catalog |
| Customization not found | CUST_009 | 404 | "Customization data not found" | Restart customization |
| Draft not found | CUST_010 | 404 | "Draft not found or expired" | Start new customization |
| Draft load failed | CUST_011 | 500 | "Unable to load draft. Please try again" | Retry or delete draft |
| Bundle limit exceeded | CUST_012 | 400 | "Maximum 10 products per bundle" | Remove products or create multiple bundles |
| Logo file missing (backend) | CUST_013 | 404 | "Logo file not found in system" | Contact support, manual review |
| High-res rendering failed | CUST_014 | 500 | "Print image generation failed. Order flagged for review" | Admin notified, user receives email |
| User not logged in (save draft) | CUST_015 | 401 | "Please log in to save draft" | Redirect to login |
| Malicious file detected | CUST_016 | 400 | "File validation failed. Please use a different image" | Upload different file |
| Canvas rendering error | CUST_017 | 500 | "Browser rendering error. Try refreshing page" | Refresh page or try different browser |

### Error Handling Strategy
- Display clear, actionable error messages
- Provide retry mechanisms for transient errors (network, upload)
- Log detailed errors server-side for debugging
- For critical failures (rendering), flag order for manual admin review
- Never expose internal system details to users
- Offer alternative actions (e.g., "Contact support" link for persistent issues)

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-52**: Logo upload must complete within 3 seconds for files up to 10MB
- **NFR-53**: Cropping interface must maintain 60fps during pan/zoom interactions
- **NFR-54**: Client-side preview generation must complete within 500ms
- **NFR-55**: Server-side high-res rendering must complete within 10 seconds per product
- **NFR-56**: Canvas rendering optimized for desktop (Chrome, Firefox, Safari latest versions)
- **NFR-57**: Mobile upload interface must handle camera input without lag

### Scalability
- **NFR-58**: System must handle 100 concurrent customization sessions without degradation
- **NFR-59**: Backend rendering queue must process up to 50 orders/minute
- **NFR-60**: Logo file storage must scale to accommodate 10,000+ uploaded files (cloud storage: AWS S3)

### Security
- **NFR-61**: Logo files scanned for malicious content (malware, executable code) before storage
- **NFR-62**: File uploads use signed URLs with 1-hour expiry for temp storage
- **NFR-63**: Crop data validated server-side to prevent coordinate manipulation
- **NFR-64**: Print-ready images stored in secure bucket with access restricted to partner portal (internal, RBAC-protected)

### Quality
- **NFR-65**: Print-ready images must be 300 DPI minimum for professional print quality
- **NFR-66**: Client-side preview must visually match final print output (±5% color accuracy)
- **NFR-67**: Backend rendering must support color space conversion (RGB to CMYK if required by partner)

### Accessibility
- **NFR-68**: File upload supports keyboard navigation (Tab to button, Enter to trigger)
- **NFR-69**: Cropping tool zoom/pan accessible via keyboard (Arrow keys to pan, +/- to zoom)
- **NFR-70**: Screen reader announces crop adjustments ("Zoom level 1.5x", "Position adjusted")
- **NFR-71**: Preview images have descriptive alt text: "Preview of [Product Name] with uploaded logo"

### Usability
- **NFR-72**: Customization workflow must be completable within 3 minutes for experienced users
- **NFR-73**: Instructions and tooltips provided at each step
- **NFR-74**: Mobile interface optimized for touch (button sizes ≥44×44px)

---

## 11. ACCEPTANCE CRITERIA

### AC-21: Upload and Validate Logo
**Given** a client on the customization page  
**When** the client uploads a PNG file (5MB, 1200×1200px)  
**Then** the system validates the file  
**And** displays thumbnail preview  
**And** displays success message "Logo uploaded successfully"  
**When** the client uploads a 15MB file  
**Then** the system rejects the file  
**And** displays error "File exceeds 10MB limit"

### AC-22: Crop Logo with Locked Aspect Ratio
**Given** a client has uploaded a logo for a T-Shirt (print area aspect ratio 4:5)  
**When** the client enters cropping interface  
**Then** the system displays crop overlay with 4:5 aspect ratio (locked)  
**And** allows zoom adjustment (1x to 3x)  
**And** allows pan by dragging logo  
**When** the client zooms to 2x and repositions logo  
**And** clicks "Apply Crop"  
**Then** the system extracts cropped image data  
**And** proceeds to preview generation

### AC-23: Generate Client-Side Preview
**Given** a client has applied crop for T-Shirt customization  
**When** the system generates preview  
**Then** the system:
- Loads T-Shirt base image
- Overlays cropped logo at defined print area coordinates
- Renders preview on canvas within 500ms
- Displays preview image (800×800px) in center of page
**And** the preview accurately shows logo placement on product

### AC-24: Create Multi-Product Bundle
**Given** a client has customized a T-Shirt with Company Logo  
**When** the client clicks "Create Bundle"  
**Then** the system saves T-Shirt customization  
**And** displays bundle builder interface  
**When** the client adds "Water Bottle" and chooses "Use same logo"  
**Then** the system loads Water Bottle customization with same logo pre-filled  
**And** adjusts crop interface for Water Bottle print area (1:1 aspect ratio)  
**When** the client applies crop and adds to bundle  
**Then** the system displays bundle summary with 2 products  
**When** the client adds 2 more products (Pen, Diary)  
**And** enters bundle name "Welcome Kit"  
**And** clicks "Add Bundle to Cart"  
**Then** the system validates all 4 customizations  
**And** adds bundle to cart as single item  
**And** displays success message "Bundle added to cart"

### AC-25: Save and Load Draft
**Given** a logged-in client in middle of customization (logo uploaded, crop applied)  
**When** the client clicks "Save Draft"  
**Then** the system saves:
- Logo file
- Crop coordinates
- Product ID
- Preview thumbnail
**And** displays "Draft saved successfully"  
**When** the client returns 2 days later  
**And** navigates to "My Drafts"  
**Then** the system displays list of saved drafts with previews  
**When** the client clicks "Edit" on the draft  
**Then** the system loads customization page with saved state  
**And** pre-populates logo and crop settings  
**And** regenerates preview from saved data  
**And** the client can continue editing or finalize

### AC-26: Download Preview Image
**Given** a client has completed customization and sees preview  
**When** the client clicks "Download Preview"  
**Then** the system generates PNG image (1200×1200px)  
**And** adds watermark "BrandKit Preview" (bottom-right, 30% opacity)  
**And** downloads file with name `TShirt_Customized_20260123.png`  
**And** the client can share image for approval

### AC-27: Server-Side High-Res Generation
**Given** a client has placed an order with customized T-Shirt  
**And** payment is confirmed  
**When** the system processes the order  
**Then** the backend API receives rendering request with:
- Order ID, Product ID, Logo file ID, Crop data  
**And** the backend:
- Loads high-res T-Shirt image (3600×3600px, 300 DPI)
- Loads original logo file
- Applies crop transformation (scaled for high-res)
- Overlays logo on T-Shirt at print area
- Generates print-ready PNG (300 DPI)
- Uploads to AWS S3
- Returns print image URL
**And** the print-ready image is attached to order  
**And** the fulfillment partner receives order with print image link (via internal partner portal)

### AC-28: Handle Low-Resolution Logo
**Given** a client uploads a logo (400×400px)  
**When** the system calculates resolution for print  
**And** estimates <150 DPI at print size  
**Then** the system displays warning:  
"Your logo may appear pixelated when printed. For best results, upload higher resolution (min 1000×1000px)"  
**And** allows client to proceed or upload different logo

### AC-29: Validate Customization Before Cart
**Given** a client attempts to add customization to cart  
**When** the system validates:
- Logo uploaded: ✓
- Crop applied: ✓
- Preview generated: ✓
**Then** the system allows "Add to Cart"  
**When** any validation fails (e.g., no logo)  
**Then** the system displays error "Please complete customization steps"  
**And** highlights missing step

### AC-30: Mobile Touch Customization
**Given** a client on mobile device (iPhone, 375×667px)  
**When** the client uploads logo via camera  
**Then** the system captures photo and loads into cropping tool  
**When** the client uses pinch gesture to zoom logo  
**And** drags to reposition  
**And** taps "Apply Crop"  
**Then** the system generates mobile-optimized preview  
**And** displays full-width on screen  
**And** all interactions work smoothly with touch gestures

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Libraries
- **Frontend**:
  - `react-easy-crop`: Cropping interface (locked aspect ratio)
  - HTML5 Canvas API: Client-side rendering
  - Browser File API: File upload handling
- **Backend**:
  - Java Imaging Library (e.g., `java.awt.image.BufferedImage` or `ImageJ`)
  - Apache Commons FileUpload: File handling
  - AWS SDK for Java: S3 integration

### Internal Dependencies
- **FRD-002 (Product Catalog)**: Retrieve product data (base image, print area metadata)
- **FRD-004 (Order Management)**: Pass customization data to order system
- **FRD-001 (Authentication)**: User login required for draft saving

### External Services
- **Cloud Storage**: AWS S3 for logo file storage and print-ready images
- **CDN**: CloudFront for fast image delivery

### Database Collections
- `customizations`: Stores customization data (crop coordinates, logo URLs)
- `customizationDrafts`: Temporary drafts (expires 30 days)
- `bundles`: Bundle configurations

### API Endpoints
- `POST /api/customization/upload-logo`: Upload logo file
- `POST /api/customization/save-draft`: Save draft customization
- `GET /api/customization/drafts`: Retrieve user's drafts
- `GET /api/customization/draft/:draftId`: Load specific draft
- `DELETE /api/customization/draft/:draftId`: Delete draft
- `POST /api/customization/render-high-res`: Generate print-ready image (backend)
- `POST /api/bundles/create`: Create product bundle

---

## 13. ASSUMPTIONS

1. Clients have access to their logo files in digital format (PNG/JPG/SVG)
2. Product base images provided with transparent print areas or clear coordinates
3. Fulfillment partners can receive and process high-resolution print images (300 DPI PNG/TIFF) via internal partner portal
4. Clients primarily use desktop for customization (mobile as secondary)
5. Browser support: Chrome 90+, Firefox 88+, Safari 14+ (HTML5 Canvas support)
6. Logo files are generally simple (single-layer, no complex transparency)
7. Print area metadata is accurately configured by admins for each product
8. AWS S3 or equivalent cloud storage is available and configured
9. Backend has sufficient processing power for real-time high-res image generation
10. Clients understand basic cropping concepts (zoom, pan)

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- 360° or 3D product previews (Three.js integration)
- Advanced editing: Logo rotation, color adjustments, filters
- Multi-logo support (different logos on front/back of product)
- Text overlay tool (add custom text alongside logo)
- AI-powered logo placement suggestions (optimal positioning)
- Design template library (pre-designed layouts for events)
- Integration with design tools (Canva API for in-app editing)
- Collaborative customization (multi-user editing with real-time sync)
- Undo/redo functionality for multi-step edits
- Logo library management (save frequently used logos per account)
- Augmented Reality (AR) preview on mobile app (scan room to visualize products)
- Batch customization (apply logo to 50 products in one action)
- Color matching tool (adjust logo colors to match product variants)
- Proof approval workflow (partner uploads sample photo for client approval before production - via internal partner portal)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Technical design for Canvas rendering, backend imaging library selection, AWS S3 configuration
