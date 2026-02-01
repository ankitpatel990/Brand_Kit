# FRD-005: Partner Dashboard - Development Prompts (INTERNAL PORTAL)

## Overview
This document contains 10 sub-prompts for implementing the Partner Dashboard for Order Management and Fulfillment (STRICTLY INTERNAL - NOT client-accessible) with testing instructions after each prompt.

**CRITICAL**: This entire dashboard is INTERNAL ONLY. Partner information NEVER exposed to clients.

---

## Sub-Prompt 1: Partner Authentication and Access Control
**Task**: Implement secure partner login with role-based access (internal portal only).

**Implementation Details**:
- Partner role: "PARTNER" (separate from CLIENT role)
- Authentication: Reuse FRD-001 auth system, role-specific access
- Partner portal URL: `/partner/dashboard` (NOT accessible to clients)
- Access control: Middleware checks JWT token for "PARTNER" role
- First login: Complete partner profile (business details, bank account)
- Row-Level Security (RLS): Partners can ONLY access their own orders and data

**Testing Instructions**:
1. Login as partner: `POST /api/auth/login` with partner credentials
   - Expected: JWT token with role "PARTNER"
2. Access `/partner/dashboard`
   - Expected: 200 OK, partner dashboard displayed
3. Try accessing as client role
   - Expected: 403 Forbidden, "Access denied"
4. Partner tries accessing `/admin/users`
   - Expected: 403 Forbidden
5. Partner tries accessing another partner's orders
   - Expected: 403 Forbidden (RLS enforcement)
6. Verify JWT token claims: {userId, role: "PARTNER", partnerId}
7. **CRITICAL**: Partner portal NOT visible or accessible to clients in any way

---

## Sub-Prompt 2: Partner Dashboard Home (Internal Portal)
**Task**: Create partner dashboard overview with key metrics.

**Implementation Details**:
- Summary cards: Pending Orders, Active Orders, Ready to Ship, Total Revenue (this month), Active Discounts
- Recent orders table: Last 10 orders with status
- Alerts: Orders requiring action, settlement pending, performance warnings
- Discount status: Quick view of approved/disabled discounts
- Quick actions: View All Orders, View Settlements, Update Profile

**Testing Instructions**:
1. Login as partner, view dashboard
   - Expected: Summary cards with correct counts
2. Verify metrics: Pending Orders = 3
   - Database check: 3 orders with status "pending_acceptance"
3. Check recent orders table
   - Expected: Last 10 orders, sorted by date
4. Alert: "2 orders awaiting acceptance"
   - Expected: Alert displayed prominently
5. Click "View All Orders"
   - Expected: Navigate to orders list page
6. Check discount status card
   - Expected: Shows count of active/pending discounts
7. **CRITICAL**: Dashboard accessible ONLY to logged-in partners

---

## Sub-Prompt 3: Order Notification System (Internal Communications)
**Task**: Implement multi-channel notifications for partners (internal communication only).

**Implementation Details**:
- Email notification: New order assigned
- SMS notification: Order alert
- In-app notification: Badge on bell icon
- Email contents: Order ID, product, quantity, delivery address (city/state), expected ship date, print-ready image link, Accept/Reject buttons
- SMS: "New order [Order ID] for ₹[Amount]. Login to review: [link]"
- Bell icon badge: Unread count

**Testing Instructions**:
1. Admin routes order to partner
   - Expected: Email sent to partner within 10 seconds
2. Check partner email inbox
   - Expected: Email with subject "New Order [Order ID]"
3. Verify email contents: Order details, Accept/Reject buttons
4. Check SMS
   - Expected: SMS delivered to partner's phone
5. Partner logs in
   - Expected: Bell icon shows badge "1" (unread notification)
6. Click bell icon
   - Expected: Notification panel displays order alert
7. **CRITICAL**: Notifications are INTERNAL partner communications, NOT visible to clients

---

## Sub-Prompt 4: Order List View (Partner Portal)
**Task**: Implement order list for partners to view assigned orders.

**Implementation Details**:
- Table columns: Order ID, Client Name (masked: "Rajesh K."), Product, Quantity, Order Date, Expected Ship Date, Status, Actions
- Filters: Status (Pending, Accepted, In Production, Shipped), Date Range
- Search: By Order ID
- Sort: By Order Date (urgent first), Expected Ship Date
- Pagination: 20 orders per page
- Actions: Accept/Reject, Update Status, View Details

**Testing Instructions**:
1. Navigate to Orders page
   - Expected: List of partner's assigned orders
2. Filter by status "Pending"
   - Expected: Only pending orders displayed
3. Search order by ID
   - Expected: Matching order found
4. Sort by Expected Ship Date
   - Expected: Urgent orders at top
5. Try accessing order assigned to different partner
   - Expected: 403 Forbidden (RLS prevents access)
6. Check client name masking: "Rajesh Kumar" → "Rajesh K."
   - Expected: Privacy preserved
7. Click "View Details"
   - Expected: Navigate to order detail page

---

## Sub-Prompt 5: Order Acceptance/Rejection Workflow
**Task**: Implement order acceptance and rejection by partners.

**Implementation Details**:
- Accept button: Confirms order, reveals full address, locks order
- Reject button: Opens modal with reason dropdown (Insufficient capacity, Out of stock, etc.)
- Acceptance deadline: 24 hours, auto-escalates to admin if missed
- Accept: Updates order status to "partner_accepted", client notified
- Reject: Updates status to "partner_rejected", admin notified (NOT client)
- Post-acceptance: Order cannot be rejected (manual process via admin)

**Testing Instructions**:
1. View pending order, click "Accept Order"
   - Expected: Confirmation modal displayed
2. Confirm acceptance
   - Expected: Order status "partner_accepted", full address revealed
3. Check client notification
   - Expected: Email "Order accepted, in production" (NO partner name)
4. Try rejecting accepted order
   - Expected: Error "Cannot reject accepted order"
5. Reject pending order, select reason "Insufficient capacity"
   - Expected: Status "partner_rejected", admin alerted
6. Check client status
   - Expected: Client still sees "Processing" (generic, NO partner rejection visible)
7. Wait 25 hours without action
   - Expected: Admin receives escalation alert

---

## Sub-Prompt 6: Production Status Updates (Internal Portal)
**Task**: Implement status update workflow for partners.

**Implementation Details**:
- Status options: In Production, Proof Ready, Production Complete, Ready to Ship
- Update endpoint: `PUT /api/partner/orders/:orderId/status`
- Status restrictions: Cannot skip statuses (sequential progression)
- Proof Ready: Requires proof image upload
- Client notifications: Email on each status change (NO partner name)
- Timeline: Log timestamp for each status change

**Testing Instructions**:
1. Accepted order, click "Update Status" → "In Production"
   - Expected: Status updated, client notified
2. Update to "Proof Ready", prompted for proof upload
   - Expected: Proof upload modal displayed
3. Upload 3 proof images
   - Expected: Images uploaded, status updated
4. Client views order
   - Expected: "Proof ready for review" (link to view proofs)
5. Try skipping from "In Production" to "Shipped"
   - Expected: Error "Cannot skip statuses"
6. Update to "Ready to Ship"
   - Expected: Status updated, client sees "Ready to Ship"
7. Check timeline: All status changes logged with timestamps

---

## Sub-Prompt 7: Proof Image Upload (Internal Partner Action)
**Task**: Implement proof sample upload functionality.

**Implementation Details**:
- Upload interface: 1-5 images, JPG/PNG, max 5MB each
- Caption field per image (optional)
- Validation: File size, format
- Storage: AWS S3
- Client access: View proofs from order tracking page
- Purpose: Build client trust, reduce disputes

**Testing Instructions**:
1. Order status "Proof Ready", click "Upload Proof"
   - Expected: Upload interface displayed
2. Upload 3 images (front view, logo closeup, back view)
   - Expected: Images uploaded to S3
3. Add captions: "Front with logo", "Logo detail", "Back view"
   - Expected: Captions saved
4. Client views order details
   - Expected: "View Proofs" button visible
5. Client clicks "View Proofs"
   - Expected: Proof images displayed in gallery
6. Test validation: Upload 10MB file
   - Expected: Error "File too large"
7. Test invalid format: Upload PDF
   - Expected: Error "Invalid file type"

---

## Sub-Prompt 8: Shipment Creation (Internal Partner Action)
**Task**: Implement shipment marking with tracking ID entry.

**Implementation Details**:
- Shipment form: Courier Partner (dropdown), Tracking ID (text), Ship Date (date picker), Weight, Packages
- Validation: Tracking ID mandatory
- Update order status: "shipped"
- Send notifications: Client (email + SMS with tracking link)
- Store: Tracking ID, courier details in order record
- Integration: If Delhivery selected, auto-fetch tracking updates via API

**Testing Instructions**:
1. Order "Ready to Ship", click "Mark as Shipped"
   - Expected: Shipment form displayed
2. Fill form: Courier = Delhivery, Tracking ID = DELIV12345678, Ship Date = Today
3. Submit form
   - Expected: Order status "shipped", client notified
4. Check client email
   - Expected: "Order shipped! Track: [link]" (NO partner name)
5. Check client SMS
   - Expected: SMS with tracking link
6. Try submitting without tracking ID
   - Expected: Error "Tracking ID required"
7. Test Delhivery integration: Tracking updates auto-fetched
   - Expected: Order status updated when Delhivery webhook received

---

## Sub-Prompt 9: Commission and Settlement Dashboard (Internal Partner View)
**Task**: Implement commission display and settlement tracking for partners.

**Implementation Details**:
- Commission calculation: Product Amount - Platform Commission (10-15%) = Partner Earnings
- Partner discount impact: Commission calculated on discounted price
- Settlement summary: Total Earnings (all time), Pending Settlement (current period), Last Settlement, Next Settlement Date
- Settlements list: Settlement ID, Period, Amount, Status (Pending/Completed), Date
- Order breakdown: Click settlement to see included orders
- Download settlement statement (PDF)

**Testing Instructions**:
1. View order details, check commission section
   - Expected: Product Amount, Platform Commission %, Partner Earnings displayed
2. Order with partner discount 10%: Original ₹19,500, Discounted ₹17,550
   - Expected: Commission (12%) = ₹2,106, Partner Earnings = ₹15,444
3. Navigate to Settlements page
   - Expected: Summary cards with correct amounts
4. Check settlements list
   - Expected: Past settlements with status "Completed"
5. Click settlement ID
   - Expected: Order-wise breakdown displayed
6. Download settlement statement
   - Expected: PDF downloaded with all orders listed
7. Verify earnings accuracy: Sum of all partner earnings = Total in database

---

## Sub-Prompt 10: Partner Discount Management (Internal Portal)
**Task**: Implement partner discount creation and management (Rule 2).

**Implementation Details**:
- Discount dashboard: List of partner's products with discount status
- Create discount: Select product, enter discount % (validated against platform limits)
- Platform validation: Min 0%, Max 25% (or admin-defined limits)
- Status indicators: Active (approved), Disabled (admin action), Pending (awaiting approval), Rejected
- Discount impact preview: Shows how discount affects partner earnings
- Admin controls: Admin can approve/reject/disable any discount

**Testing Instructions**:
1. Navigate to Discounts page
   - Expected: List of partner's products
2. Select product, click "Add Discount"
3. Enter 10% discount
   - Expected: Validation passes (within 0-25% limit)
4. Submit discount proposal
   - Expected: Status "Pending Admin Approval"
5. Try entering 30% discount
   - Expected: Error "Discount exceeds maximum limit (25%)"
6. Admin approves discount
   - Expected: Status "Active", discount visible on client product page
7. Admin disables discount
   - Expected: Status "Disabled", clients see original price
8. View discount impact preview: Original earnings ₹15,000, With 10% discount ₹13,500
   - Expected: Impact clearly displayed

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Partner Onboarding Flow**: Admin Creates Partner → Partner Receives Credentials → First Login → Complete Profile
2. **Order Assignment Flow**: Admin Routes Order → Partner Notified (email/SMS/in-app) → Partner Accepts → Client Notified (NO partner name)
3. **Order Fulfillment Flow**: Accept → Update to In Production → Upload Proof → Client Views → Ready to Ship → Mark Shipped → Client Tracks
4. **Settlement Flow**: Orders Delivered → Commission Calculated → Settlement Generated → Admin Approves → Payout Processed → Partner Receives Payment
5. **Discount Flow**: Partner Proposes Discount → Admin Approves → Discount Active on Client Site → Admin Disables → Original Price Restored
6. **Rejection Flow**: Partner Rejects Order → Admin Notified → Admin Reassigns → Client Sees Generic "Processing" (NO rejection visible)
7. **Performance Monitoring**: Partner Views Metrics → Identifies Low Fulfillment Rate → Improves → Metrics Updated

---

## Security and Privacy Validation Checklist

- [ ] Partner portal ONLY accessible to users with "PARTNER" role
- [ ] Partners can ONLY access their own assigned orders (RLS enforced)
- [ ] Client personal details (full address) revealed ONLY after order acceptance
- [ ] Partner dashboard NOT visible or linked anywhere in client-facing UI
- [ ] Partner notifications are internal communications, NOT visible to clients
- [ ] Order status updates sent to clients without partner names or references
- [ ] Bank account details encrypted at rest
- [ ] Admin actions logged (partner assignments, discount approvals)
- [ ] Partner discount proposals validated against platform limits
- [ ] Commission calculations accurate and transparent

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 4-5 weeks (including testing)
