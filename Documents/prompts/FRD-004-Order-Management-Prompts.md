# FRD-004: Order Management - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the Order Placement, Cart, Checkout, and Tracking System with testing instructions after each prompt.

---

## Sub-Prompt 1: Shopping Cart System
**Task**: Implement shopping cart with add/remove/update functionality.

**Implementation Details**:
- Database: `carts` table (user_id, created_at), `cart_items` table (cart_id, product_id, customization_id, quantity)
- Cart persistence: Logged-in users (database), Guest users (localStorage)
- Endpoints:
  - `POST /api/cart/add` - Add item to cart
  - `PUT /api/cart/item/:itemId` - Update quantity
  - `DELETE /api/cart/item/:itemId` - Remove item
  - `GET /api/cart` - Get user's cart
- Real-time price recalculation on quantity change
- Cart icon badge: Show item count
- Session-based for guests, database for logged-in users

**Testing Instructions**:
1. Add customized T-Shirt (qty 75) to cart
   - Expected: Cart item created, badge shows "1"
2. Update quantity to 150
   - Expected: Item updated, price recalculated with new tier
3. Add second product (Water Bottle)
   - Expected: Cart has 2 items, badge shows "2"
4. Remove T-Shirt
   - Expected: Cart has 1 item, badge shows "1"
5. Logout, login
   - Expected: Cart persists (database cart)
6. Guest adds item, clears cache
   - Expected: Cart lost (localStorage limitation for guests)
7. Test empty cart: Remove all items
   - Expected: "Your cart is empty" message displayed

---

## Sub-Prompt 2: Cart Validation Before Checkout
**Task**: Implement pre-checkout validation for cart items.

**Implementation Details**:
- Validations:
  - Product availability (still active)
  - Partner availability (internal check, not visible to client)
  - Quantity limits (1-10,000)
  - Pricing updates (recalculate based on current tiers)
  - Customization integrity (verify data exists)
- Endpoint: `POST /api/checkout/validate`
- Remove problematic items or display errors
- Prevent checkout if validation fails

**Testing Instructions**:
1. Add item to cart, admin deactivates product, try checkout
   - Expected: Error "Product no longer available", item flagged
2. Add item, quantity 50, admin changes tier pricing, try checkout
   - Expected: Price recalculated automatically
3. Add item, quantity 15,000 (exceeds limit)
   - Expected: Error "Quantity must be between 1 and 10,000"
4. Add customized item, delete customization data (simulate), try checkout
   - Expected: Error "Customization data missing"
5. Add item with partner P1, admin deactivates P1 (internal), try checkout
   - Expected: Generic error "Unable to process order" (NO partner details exposed)
6. All validations pass
   - Expected: Proceed to checkout flow
7. Test with 10 items in cart
   - Expected: All items validated successfully

---

## Sub-Prompt 3: Multi-Step Checkout Flow
**Task**: Implement 3-step checkout: Address → Delivery → Review.

**Implementation Details**:
- Step 1: Delivery Address
  - New address form or saved addresses selection
  - Validation: All mandatory fields, PIN code serviceability check
- Step 2: Delivery Options
  - Standard (7-14 days, ₹100) vs Express (3-5 days, ₹300)
  - Display estimated delivery date
- Step 3: Order Review
  - Summary: Items, address, delivery option, price breakdown
  - Terms & Conditions checkbox (mandatory)
  - "Proceed to Payment" button

**Testing Instructions**:
1. Step 1: Enter new address
   - Expected: Form validated, proceed to Step 2
2. Select saved address
   - Expected: Address pre-filled, proceed to Step 2
3. Try proceeding with invalid PIN code (000000)
   - Expected: Error "PIN code not serviceable"
4. Step 2: Select Standard Delivery
   - Expected: Estimated delivery date displayed
5. Step 3: Review order summary
   - Expected: All details correct (items, address, delivery)
6. Try proceeding without accepting T&C
   - Expected: Error "Please accept Terms & Conditions"
7. Complete checkout flow
   - Expected: Redirect to payment gateway

---

## Sub-Prompt 4: GST-Compliant Invoice Generation
**Task**: Implement automatic GST invoice generation (PDF).

**Implementation Details**:
- Library: iText (Java backend) or jsPDF (frontend)
- Invoice contents: BrandKit GSTIN, client details, order ID, itemized list, HSN codes, tax breakdown (CGST 9%, SGST 9% or IGST 18%), total, amount in words
- Discount display: Show original price, discount, final price
- Storage: AWS S3
- Attach to order confirmation email
- Download endpoint: `GET /api/orders/:orderId/invoice`

**Testing Instructions**:
1. Place order, payment successful
   - Expected: Invoice PDF generated automatically
2. Download invoice
   - Expected: PDF contains all required GST fields
3. Verify invoice structure:
   - Present: BrandKit GSTIN, client name, order ID, items with HSN, CGST/SGST breakdown, total in words
4. Verify discount display: Original ₹19,500, Discount ₹2,250, Final ₹17,250
5. Test inter-state order (different state)
   - Expected: IGST 18% instead of CGST+SGST
6. Test invoice download link in email
   - Expected: PDF downloads successfully
7. Verify S3 storage: Invoice stored with correct naming

---

## Sub-Prompt 5: Partner Order Routing (Internal Process)
**Task**: Implement automatic order routing to partners (backend, NOT visible to clients).

**Implementation Details**:
- Routing logic (INTERNAL ONLY):
  1. Identify product-partner association (internal database)
  2. Check partner status (active, accepting orders)
  3. Validate partner capacity (internal metric)
  4. Assign order to partner (internal record)
- Create `order_partner_assignments` table (admin-only access)
- Send notification to partner (internal partner portal)
- Client sees generic "Order confirmed" (NO partner details)
- Admin can view partner assignment in admin panel

**Testing Instructions**:
1. Place order for T-Shirt (partner: P-GJ-001 internally)
   - Expected: Order routed to P-GJ-001, recorded in order_partner_assignments
2. Check client order confirmation email
   - Expected: NO partner name, NO partner contact info
3. Check partner notification (internal partner portal)
   - Expected: Partner receives order details
4. Admin views order
   - Expected: Admin sees partner assignment (internal view)
5. Test with product from different partner
   - Expected: Correctly routed to respective partner
6. Test with inactive partner
   - Expected: Admin alerted, manual intervention required (generic message to client)
7. **CRITICAL**: Client APIs NEVER return partner details
   - Test: `GET /api/orders/:orderId` - NO partner_id, partner_name in response

---

## Sub-Prompt 6: Order Status Tracking (Client View)
**Task**: Implement order status tracking without exposing partner information.

**Implementation Details**:
- Status progression: Pending Payment → Confirmed → Processing (internally: partner_accepted) → In Production → Shipped → Delivered
- Client dashboard: Orders list with status filter
- Order detail page: Progress bar, timeline, tracking info (if shipped)
- NO partner names, locations, or identifying info displayed
- Email/SMS notifications on status changes
- Reorder functionality: Add same items to cart

**Testing Instructions**:
1. Place order, view Orders dashboard
   - Expected: Order listed with status "Confirmed"
2. Partner accepts order (internal), check client view
   - Expected: Status shows "Processing" (NOT "partner_accepted")
3. Partner updates to "In Production", check client view
   - Expected: Status "In Production", NO partner reference
4. Partner ships order, check client view
   - Expected: Status "Shipped", tracking ID visible, NO partner name
5. Test progress bar: Visual timeline with completed steps highlighted
6. Click "Reorder" on delivered order
   - Expected: Items added to cart with same customizations
7. **CRITICAL**: Verify NO partner details anywhere in client-facing UI or emails

---

## Sub-Prompt 7: Delivery Partner Integration
**Task**: Integrate delivery partner (Delhivery) for shipment tracking.

**Implementation Details**:
- API: Delhivery REST API
- Create shipment: `POST /api/coreapi/v1/packages/json/`
- Get tracking ID from Delhivery
- Store tracking ID in order record
- Webhook: Receive status updates (in-transit, delivered)
- Track shipment: `GET /api/track/:trackingId`
- Display tracking link to client
- Update order status based on delivery partner updates

**Testing Instructions**:
1. Partner marks order shipped, provides tracking ID
2. System creates shipment with Delhivery
   - Expected: Delhivery returns tracking ID
3. Client views order details
   - Expected: Tracking ID and link displayed
4. Client clicks tracking link
   - Expected: Redirected to Delhivery tracking page
5. Delhivery webhook: "In Transit"
   - Expected: Order status updated, client notified
6. Delhivery webhook: "Delivered"
   - Expected: Order status "Delivered", client notified
7. Test with invalid tracking ID
   - Expected: Error "Tracking information unavailable"

---

## Sub-Prompt 8: Order Cancellation and Refund
**Task**: Implement order cancellation workflow with refund processing.

**Implementation Details**:
- Admin cancels order: `POST /api/admin/orders/:orderId/cancel`
- Reason field (mandatory)
- Check payment status
- Initiate refund via payment gateway (Razorpay)
- Update order status: "cancelled" → "refunded"
- Send notifications: Client (refund info), Partner (cancel notice, NO client details)
- Refund timeline: 3-7 business days

**Testing Instructions**:
1. Place order, admin cancels before partner acceptance
   - Expected: Order status "cancelled", refund initiated
2. Refund API call to Razorpay
   - Expected: Refund ID returned, status "processed"
3. Check client email
   - Expected: "Order cancelled, refund of ₹X initiated"
4. Check partner notification
   - Expected: "Order cancelled, no action required" (NO client details)
5. Test cancellation after partner acceptance (complex scenario)
   - Expected: Manual process, support intervention
6. Track refund status
   - Expected: Client sees "Refund Initiated" → "Refunded"
7. Verify refund received in client's bank account (5-7 days)

---

## Sub-Prompt 9: Price Calculation with Discounts
**Task**: Implement dynamic price calculation with partner discounts.

**Implementation Details**:
- Base price calculation: Quantity × Unit Price (from tier)
- Partner discount application: If enabled, apply discount % (partner-defined, platform-managed)
- Customization fee: +₹10-50 per unit
- GST: 18% on subtotal
- Delivery charges: Standard ₹100 (free > ₹10k), Express ₹300
- Display: Original price (strikethrough), discount savings, final price
- Total: Subtotal + GST + Delivery

**Testing Instructions**:
1. Add product (qty 75, base ₹260, tier price ₹230)
   - Expected: Tier price applied
2. Partner discount 11.5% enabled
   - Expected: Further discount, final ₹230 - 11.5% = ₹203.95
3. Add customization (Logo Print, +₹20)
   - Expected: Unit price ₹223.95
4. Calculate total: 75 × ₹223.95 = ₹16,796.25
5. Add GST (18%): ₹3,023.33
6. Add delivery (Standard): ₹100
7. Total: ₹19,919.58
   - Expected: Displayed with discount savings highlighted
8. Order > ₹10,000
   - Expected: Free standard delivery

---

## Sub-Prompt 10: Reorder Functionality
**Task**: Implement one-click reorder for past orders.

**Implementation Details**:
- Reorder button: On delivered orders
- Endpoint: `POST /api/orders/:orderId/reorder`
- Retrieve order items and customizations
- Validate: Products still active, customizations accessible
- Add items to cart (same customizations)
- Client can adjust quantities before checkout
- Handle unavailable items gracefully

**Testing Instructions**:
1. View past order (delivered), click "Reorder"
   - Expected: Items added to cart
2. Check cart
   - Expected: All items with original customizations
3. Adjust quantity, proceed to checkout
   - Expected: New prices calculated
4. Test with discontinued product
   - Expected: "Some items unavailable" message, available items added
5. Test with missing customization data
   - Expected: Error "Customization unavailable, please re-upload logo"
6. Test with changed pricing
   - Expected: Notification "Prices have been updated"
7. Complete reorder
   - Expected: New order placed successfully

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Full Order Flow**: Add to Cart → Checkout (3 steps) → Payment → Order Confirmed → Invoice Generated
2. **Partner Routing Flow**: Order Placed → Routed to Partner (internal) → Client Sees Generic Status (NO partner info)
3. **Tracking Flow**: Order Shipped → Tracking ID → Client Tracks → Delivery → Status Updated
4. **Cancellation Flow**: Admin Cancels → Refund Initiated → Client Refunded
5. **Reorder Flow**: View Past Order → Reorder → Adjust → Checkout → New Order
6. **Discount Pricing Flow**: Product with Partner Discount → Add to Cart → See Savings → Checkout → Discounted Price Paid
7. **Multi-Partner Cart Blocking**: Add Item from Partner A → Try Adding Item from Partner B → Error (internal check, generic message to client)

---

## Security and Privacy Validation Checklist

- [ ] Partner details NEVER exposed in client-facing APIs, UI, or emails
- [ ] Partner assignments stored in admin-only database table
- [ ] Order routing logic internal, not visible to clients
- [ ] Client order confirmation emails contain NO partner information
- [ ] Invoices do NOT include partner GSTIN or names
- [ ] Cart persistence secure (database for logged-in, encrypted for guests)
- [ ] Checkout validation prevents unauthorized quantity/price manipulation
- [ ] Payment integration secure (handled by gateway, not BrandKit)
- [ ] Refund processing tracked and logged
- [ ] Delivery partner integration does NOT expose internal partner info

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 4-5 weeks (including testing)
