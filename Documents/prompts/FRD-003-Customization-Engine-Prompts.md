# FRD-003: Customization Engine - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the Logo Customization and Real-Time Preview Engine with testing instructions after each prompt.

---

## Sub-Prompt 1: Logo Upload Component (Frontend)
**Task**: Create logo upload component with validation and preview.

**Implementation Details**:
- File input: Click to select or drag-and-drop
- Supported formats: PNG, JPG, SVG (max 10MB)
- Client-side validation: File type (MIME check), file size, image dimensions (min 300×300px)
- Preview thumbnail after upload
- Progress indicator during upload
- Replace option: Upload different logo
- Error handling: Display user-friendly messages

**Testing Instructions**:
1. Drag and drop valid PNG file (2MB, 1000×1000px)
   - Expected: Upload successful, thumbnail preview shown
2. Upload 15MB file
   - Expected: Error "File exceeds 10MB limit"
3. Upload PDF file
   - Expected: Error "Invalid file type. Please upload PNG, JPG, or SVG"
4. Upload 200×200px image
   - Expected: Warning "Logo resolution is low. Recommended: 1000×1000px minimum"
5. Upload SVG with embedded script (malicious)
   - Expected: Server-side validation catches, error displayed
6. Test progress indicator: Upload 5MB file, observe progress
7. Test replace: Upload logo, click Replace, upload new logo
   - Expected: First logo replaced with new one

---

## Sub-Prompt 2: Logo Cropping Tool with react-easy-crop
**Task**: Implement logo cropping interface with locked aspect ratio.

**Implementation Details**:
- Library: react-easy-crop
- Crop overlay: Locked aspect ratio (based on product print area)
- User controls: Zoom (pinch/scroll), Pan (drag)
- Zoom range: 1x to 3x
- Visual aids: Grid overlay (rule of thirds), dimmed outside area
- Constraints: Minimum crop 100×100px
- Controls: Reset button, Apply Crop button
- Crop data: {x, y, width, height, zoom}

**Testing Instructions**:
1. Load cropping interface with uploaded logo
   - Expected: Crop overlay matches product print area aspect ratio (e.g., 4:5 for T-shirt)
2. Zoom in to 2x
   - Expected: Logo scales, crop area stays locked
3. Pan logo by dragging
   - Expected: Logo moves within frame, crop area fixed
4. Click Reset button
   - Expected: Logo returns to default zoom/position (centered)
5. Click Apply Crop
   - Expected: Crop data captured {x, y, width, height, zoom}
6. Test on mobile: Pinch to zoom, drag to pan
   - Expected: Touch gestures work smoothly
7. Try crop below 100×100px
   - Expected: Minimum size enforced

---

## Sub-Prompt 3: Client-Side Preview Rendering (HTML5 Canvas)
**Task**: Implement client-side preview generation using Canvas API.

**Implementation Details**:
- Canvas size: 800×800px (for preview)
- Process:
  1. Load product base image (PNG with transparent print area)
  2. Draw base image on canvas
  3. Load cropped logo data
  4. Draw cropped logo at print area coordinates
  5. Scale logo to match print area size
  6. Convert canvas to PNG image
- Display preview immediately after crop
- Zoom option: Click to inspect detail
- Performance: Render within 500ms

**Testing Instructions**:
1. Apply crop, trigger preview generation
   - Expected: Preview rendered within 500ms
2. Verify preview accuracy: Logo positioned correctly on product
3. Test with different product: Change from T-shirt (4:5) to Mug (1:1)
   - Expected: Crop aspect adjusts, preview accurate
4. Test with transparent PNG logo
   - Expected: Transparency preserved, background shows through
5. Test with SVG logo
   - Expected: SVG rendered as raster in preview
6. Click preview to zoom
   - Expected: Larger view displayed for detail inspection
7. Performance test: Generate 10 previews in succession
   - Expected: All render within 500ms each

---

## Sub-Prompt 4: Server-Side High-Resolution Rendering (Backend)
**Task**: Implement backend high-res image generation for print (300 DPI).

**Implementation Details**:
- Endpoint: `POST /api/customization/render-high-res`
- Input: Order ID, product ID, logo file ID, crop data {x, y, width, height, zoom}
- Load high-res product image (3600×3600px, 300 DPI)
- Load original logo file (full resolution)
- Apply crop transformation (scale crop coords for high-res)
- Resize cropped logo to print area dimensions (e.g., 1000×1250px)
- Composite logo onto product image
- Generate print-ready PNG (lossless, 300 DPI)
- Upload to AWS S3
- Return: Print image URL

**Testing Instructions**:
1. Submit render request: `POST /api/customization/render-high-res`
   - Include: order ID, product ID, logo file, crop data
   - Expected: 200 OK, print-ready image URL returned
2. Download print image from S3
   - Expected: Image is 3600×3600px, 300 DPI
3. Inspect print image: Logo positioned correctly at print area
4. Test with different crop: Change zoom from 1x to 2x
   - Expected: Logo scaled correctly in high-res
5. Test rendering time
   - Expected: Complete within 10 seconds
6. Test with missing logo file
   - Expected: 404 Not Found, "Logo file missing"
7. Test with invalid crop coordinates
   - Expected: 400 Bad Request, "Invalid crop area"
8. Verify S3 storage: Image stored with correct filename and metadata

---

## Sub-Prompt 5: Print Area Configuration (Admin)
**Task**: Create admin interface for configuring product print areas.

**Implementation Details**:
- Visual editor: Upload product base image
- Drag rectangle on image to define print area
- Set print area dimensions (width × height in cm)
- System calculates aspect ratio, stores coordinates
- Coordinates: X, Y position (top-left), Width, Height (pixels)
- Save to product record
- Validation: Print area must fit within product image bounds

**Testing Instructions**:
1. Admin uploads T-shirt base image
2. Drag rectangle over chest area (where logo goes)
   - Expected: Rectangle overlay appears
3. Set dimensions: 20cm × 25cm
   - Expected: Aspect ratio calculated (4:5)
4. Save print area
   - Expected: Coordinates saved to database
5. Test with new product: Mug
   - Drag square area (10cm × 10cm)
   - Expected: 1:1 aspect ratio
6. Try dragging print area outside image bounds
   - Expected: Rectangle constrained to image boundaries
7. Load product on client side
   - Expected: Crop tool uses saved aspect ratio

---

## Sub-Prompt 6: Bundle Builder Workflow
**Task**: Implement multi-product customization bundle feature.

**Implementation Details**:
- Workflow:
  1. User customizes Product A (T-Shirt)
  2. After preview, click "Create Bundle"
  3. System saves customization
  4. Display product selector (customizable items)
  5. User selects Product B (Water Bottle)
  6. System asks: "Use same logo?"
  7. If yes: Pre-load logo, adjust crop for new aspect ratio
  8. User crops, previews, adds to bundle
  9. Repeat for up to 10 products
  10. Review bundle summary, add to cart
- Bundle data: Array of {productId, customizationId, quantity}
- Maximum 10 products per bundle

**Testing Instructions**:
1. Customize T-Shirt, click "Create Bundle"
   - Expected: Bundle builder interface opens
2. Add Water Bottle, select "Use same logo"
   - Expected: Logo pre-loaded, crop interface shows 1:1 aspect
3. Apply crop for Water Bottle
   - Expected: Preview generated for Water Bottle
4. Add 3 more products (Pen, Diary, Bag)
   - Expected: Bundle contains 5 products
5. Try adding 11th product
   - Expected: Error "Maximum 10 products per bundle"
6. Remove product from bundle
   - Expected: Product removed, bundle updated
7. Enter bundle name: "Welcome Kit"
   - Expected: Name saved
8. Add bundle to cart
   - Expected: Single cart item with 5 linked customizations

---

## Sub-Prompt 7: Draft Customization Save/Load
**Task**: Implement save draft functionality for incomplete customizations.

**Implementation Details**:
- Save draft endpoint: `POST /api/customization/save-draft`
- Data saved: Logo file URL, crop coordinates, product ID, preview thumbnail, timestamp
- Storage: Database, linked to user ID
- Expiry: Auto-delete after 30 days
- Load draft endpoint: `GET /api/customization/draft/:draftId`
- Drafts list: User dashboard > "My Drafts"
- Edit draft: Loads customization page with saved state

**Testing Instructions**:
1. Start customization, upload logo, apply crop
2. Click "Save Draft"
   - Expected: Draft saved, success message displayed
3. Logout, login again
4. Navigate to "My Drafts"
   - Expected: Saved draft displayed with preview thumbnail
5. Click "Edit" on draft
   - Expected: Customization page loads with saved logo and crop
6. Make changes, click "Update Draft"
   - Expected: Draft updated
7. Wait 31 days (or manually update timestamp), try loading draft
   - Expected: 404 Not Found, "Draft expired"
8. Test without login: Try saving draft
   - Expected: Redirect to login page

---

## Sub-Prompt 8: Download Preview Image
**Task**: Implement preview image download for client approval.

**Implementation Details**:
- Button: "Download Preview" on preview page
- Generate PNG: 1200×1200px
- Add watermark: "BrandKit Preview" (bottom-right, 30% opacity)
- Filename: `{ProductName}_Customized_{Timestamp}.png`
- Trigger browser download
- Use case: Share with team for approval before ordering

**Testing Instructions**:
1. Complete customization, see preview
2. Click "Download Preview"
   - Expected: PNG file downloaded
3. Open downloaded file
   - Expected: 1200×1200px image with watermark
4. Verify filename format: "TShirt_Customized_20260123.png"
5. Check watermark: "BrandKit Preview" visible but subtle
6. Share file via email
   - Expected: Recipient can view customized product
7. Test on mobile: Download on smartphone
   - Expected: File saved to device

---

## Sub-Prompt 9: Multi-Product Logo Application
**Task**: Implement streamlined logo application to multiple products.

**Implementation Details**:
- After first product customization: "Apply to More Products" button
- Product selection modal: Multi-select checkboxes
- Auto-apply logo: System adjusts crop for each product's aspect ratio
- If aspect ratios differ significantly: Prompt for manual crop
- Generate previews for all products
- Review carousel: Scroll through all previews
- Accept or adjust individual crops
- Add all to bundle in one action

**Testing Instructions**:
1. Customize T-Shirt (4:5 aspect)
2. Click "Apply to More Products"
   - Expected: Product selector modal opens
3. Select 3 products: Mug (1:1), Bag (3:2), Pen (1:4)
   - Expected: Checkboxes selected
4. Click "Apply Logo"
   - Expected: System auto-crops for Mug and Bag
5. Pen aspect (1:4) differs significantly
   - Expected: Manual crop prompt for Pen
6. Manual crop Pen logo
7. View preview carousel
   - Expected: All 4 previews displayed
8. Accept all, add to bundle
   - Expected: Bundle with 4 customized products

---

## Sub-Prompt 10: Customization Validation
**Task**: Implement client and server-side validation for customizations.

**Implementation Details**:
- Client-side checks:
  - Logo uploaded (required)
  - Crop applied (required)
  - Preview generated successfully
- Server-side checks:
  - Logo file integrity (not corrupted)
  - Crop coordinates within valid bounds
  - Product active and customizable
  - Logo resolution sufficient for print (min 150 DPI at print size)
- Warnings: Low-res logo (< 150 DPI)
- Errors: Invalid crop, missing logo
- Block "Add to Cart" if validation fails

**Testing Instructions**:
1. Try adding to cart without uploading logo
   - Expected: Error "Please upload a logo"
2. Upload logo, try adding without cropping
   - Expected: Error "Please crop your logo"
3. Upload 400×400px logo (low-res)
   - Expected: Warning "Logo may appear pixelated when printed"
4. Proceed despite warning, add to cart
   - Expected: Allowed (warning, not error)
5. Tamper with crop coordinates (modify via dev tools)
   - Expected: Server-side validation catches, error returned
6. Delete logo file from server, try adding to cart
   - Expected: Error "Logo file not found"
7. Test with inactive product
   - Expected: Error "Product not available for customization"

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Single Product Customization**: Upload Logo → Crop → Preview → Add to Cart
2. **Bundle Creation**: Customize T-Shirt → Create Bundle → Add 4 More Products → Review → Add Bundle to Cart
3. **Draft Flow**: Start Customization → Save Draft → Logout → Login → Load Draft → Complete
4. **Multi-Product Logo**: Customize Product A → Apply to 5 More → Review All Previews → Accept
5. **High-Res Rendering**: Place Order → Payment → Backend Renders Print Images → Partner Receives
6. **Preview Download**: Customize → Download Preview → Share with Team → Approve
7. **Validation Flow**: Try Invalid Actions → Errors Displayed → Correct → Success
8. **Mobile Experience**: Full customization on smartphone (touch gestures, camera upload)

---

## Performance and Quality Validation Checklist

- [ ] Client-side preview renders within 500ms
- [ ] Server-side high-res renders within 10 seconds
- [ ] Logo uploads complete within 5 seconds (10MB file)
- [ ] Canvas rendering maintains 60fps during interactions
- [ ] Print-ready images are 300 DPI minimum
- [ ] Crop aspect ratios locked correctly per product
- [ ] Touch gestures work smoothly on mobile
- [ ] All images stored securely on AWS S3
- [ ] Customization data validated server-side
- [ ] Low-resolution warnings displayed appropriately

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 4-5 weeks (including testing)
