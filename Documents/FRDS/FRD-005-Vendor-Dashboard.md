# FRD-005: Partner Dashboard and Order Fulfillment (INTERNAL PORTAL ONLY)

---

## 1. FRD METADATA

**FRD Title**: Partner Dashboard for Order Management and Fulfillment (Internal Partner Portal - NOT Client-Facing)  
**FRD ID**: FRD-005  
**Related PRD Section(s)**: 4.2.1 Core Features - Partner Dashboard  
**Priority**: High  
**Owner**: Product / Engineering  
**Version**: 1.0 (Updated: Global Changes Applied - Jan 30, 2026)  
**Date**: January 23, 2026

**CRITICAL SECURITY NOTE**: This dashboard is STRICTLY INTERNAL and accessible ONLY to fulfillment partners. Partner information, identities, and business details are NEVER exposed to clients through any interface.  

---

## 2. OBJECTIVE

### Purpose
Develop a comprehensive INTERNAL partner-facing dashboard (NOT accessible to clients) that enables fulfillment partners to receive order notifications, accept or reject orders, track production status, upload proof samples, manage fulfillment, view commission settlements, and manage discounts they define (within platform-imposed limits) for timely and accurate product delivery.

### Business Value
- Enable partner ecosystem participation and scalability while maintaining partner anonymity to clients
- Streamline order fulfillment workflow through internal partner portal
- Provide transparency in commission and payment settlements (internal process)
- Reduce manual coordination between BrandKit platform and fulfillment partners
- Improve order accuracy and delivery timelines
- Build partner trust through clear communication and fair settlement
- Empower partners to define product discounts while platform maintains control and oversight

---

## 3. SCOPE

### In Scope
- Partner authentication and profile management (internal portal access only)
- Order notification system (email, SMS, in-app) - internal partner communications
- Order acceptance/rejection workflow (internal partner actions)
- Order details view with customization specifications and print-ready images
- Production tracking with status updates (internal workflow)
- Proof upload functionality (sample product images for client approval via platform)
- Shipment creation and tracking ID entry (internal partner action)
- Order history and filtering (internal partner view)
- Commission calculation and display (internal partner earnings)
- Commission settlement tracking (automated payouts minus platform cut)
- **Discount Management**: Partners can define discount percentages for their products (with platform-imposed min/max limits)
- **Discount Control**: Platform admins can enable/disable partner discounts, audit usage, and suspend for abuse
- Partner performance metrics (fulfillment rate, average lead time, rating) - internal tracking, NOT exposed to clients
- Basic analytics dashboard (orders, revenue, pending shipments) - internal partner view
- Partner profile editing (business details, bank account for settlements, discount preferences)

### Out of Scope
- Partner self-registration (admins onboard partners manually for MVP)
- Partner product catalog management (admins add products linked to partners)
- Real-time inventory sync with partner systems
- Partner-to-client direct communication (STRICTLY PROHIBITED - handled via BrandKit support for MVP)
- Multi-user accounts for partner businesses (single login per partner for MVP)
- Advanced analytics (revenue trends, product performance) - Phase 2
- Integration with partner's production management systems
- Partner dispute resolution workflow (manual via admin for MVP)
- Partner rating appeal process
- Commission negotiation tool (commissions set by admin)
- Direct discount negotiation with clients (partners set discounts, platform enforces limits)

---

## 4. USER STORIES

### Partner Users (Internal Portal Access Only)
- **US-048**: As a Partner, I want to receive real-time notifications for new orders (via internal portal) so that I can start production immediately
- **US-049**: As a Partner, I want to see order details including customization images (internal portal) so that I can produce accurately
- **US-050**: As a Partner, I want to accept or reject orders (internal portal action) so that I can manage my production capacity
- **US-051**: As a Partner, I want to update production status (internal portal) so that the platform can inform clients (without exposing my identity)
- **US-052**: As a Partner, I want to upload proof samples (internal portal) so that clients can approve via platform (my identity hidden)
- **US-053**: As a Partner, I want to mark orders as shipped with tracking ID (internal portal) so that clients can track delivery (without knowing my identity)
- **US-054**: As a Partner, I want to view my commission earnings (internal portal) so that I understand my revenue
- **US-055**: As a Partner, I want to track settlement payments (internal portal) so that I know when I'll be paid
- **US-056**: As a Partner, I want to see my performance metrics (internal portal) so that I can improve my service
- **US-057**: As a Partner, I want to view order history (internal portal) so that I can reference past orders
- **US-057b**: As a Partner, I want to define discount percentages for my products (internal portal) so that I can offer competitive pricing
- **US-057c**: As a Partner, I want to see which of my discounts are active/disabled (by platform admin) so that I understand pricing controls

### Admin Users
- **US-058**: As an Admin, I want to monitor partner order fulfillment (internal admin panel) so that I can ensure timely delivery
- **US-059**: As an Admin, I want to intervene if a partner rejects or delays orders (internal admin panel) so that I can reassign
- **US-060**: As an Admin, I want to approve commission settlements (internal admin panel) so that partners are paid fairly
- **US-060b**: As an Admin, I want to enable/disable partner discounts (admin panel) so that I can control platform pricing
- **US-060c**: As an Admin, I want to set min/max discount limits for partners (admin panel) so that platform margins are protected
- **US-060d**: As an Admin, I want to audit partner discount usage (admin panel) so that I can detect and suspend abuse

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-51: Partner Authentication (Internal Portal Only)
Partners shall authenticate using credentials created by admins (INTERNAL access only):
- **Login**: Email + Password (same authentication system as clients, FRD-001, but separate "Partner" role)
- **Role**: "PARTNER" role assigned (internal role, grants access to partner portal)
- **First Login**: Prompt to complete partner profile (business details, bank account, discount preferences)
- **Password Reset**: Same workflow as clients
- **Access Control**: Partner portal accessible ONLY to users with "PARTNER" role, NOT accessible to clients
- **Security**: Partner identity and business details stored securely, NEVER exposed via client-facing APIs

### FR-52: Partner Dashboard Home (Internal Portal)
Upon login, partners shall see a dashboard overview (INTERNAL portal view):
- **Summary Cards**:
  - Pending Orders (awaiting acceptance): Count
  - Active Orders (in production): Count
  - Ready to Ship: Count
  - Total Revenue (this month): Amount
  - Active Discounts: Count of products with enabled discounts
- **Recent Orders**: Table showing last 10 orders (Order ID, Product, Quantity, Status, Date)
- **Discount Status**: Quick view of discount approval status (pending/approved/disabled by admin)
- **Alerts**:
  - Orders requiring action (acceptance, proof upload, shipment)
  - Settlement payments pending
  - Low performance warnings (if fulfillment rate <90%)
- **Quick Actions**:
  - View All Orders
  - View Settlements
  - Update Profile

### FR-53: Order Notifications
Partners shall receive multi-channel notifications:
- **Email Notification** when new order assigned:
  - Subject: "New Order [Order ID] - [Product Name]"
  - Body:
    - Order ID, Date
    - Product name, Quantity
    - Client delivery address (city, state)
    - Expected ship date
    - Print-ready image download link
    - "Accept Order" and "Reject Order" buttons (links to dashboard)
- **SMS Notification**:
  - "New order [Order ID] for ₹[Amount]. Login to review: [link]"
- **In-App Notification**:
  - Badge on dashboard "Orders" menu item
  - Notification bell icon with count
  - Clicking opens notification panel with order summary

### FR-54: Order List View
Partners shall access a comprehensive order list:
- **Table Columns**:
  - Order ID (clickable to details)
  - Client Name (masked for privacy: "Rajesh K.")
  - Product Name
  - Quantity
  - Order Date
  - Expected Ship Date
  - Status (badge with color: Pending, In Production, Shipped, Delivered)
  - Actions (Accept/Reject, Update Status, View Details)
- **Filters**:
  - Status: All, Pending, Accepted, In Production, Shipped, Delivered
  - Date Range: Last 7 days, Last 30 days, Last 3 months, Custom
- **Search**: By Order ID
- **Sort**: By Order Date (newest first), Expected Ship Date (urgent first)
- **Pagination**: 20 orders per page

### FR-55: Order Details View
Partners shall see comprehensive order information:
- **Order Information**:
  - Order ID, Order Date
  - Status with timeline (similar to client view)
  - Expected Ship Date (calculated based on product lead time)
- **Product Details**:
  - Product name, Category
  - Quantity
  - Customization Type (Logo Print, Embroidery, etc.)
  - Print-ready image (high-resolution download button)
  - Product specifications (material, dimensions)
- **Client Delivery Information** (limited for privacy):
  - Delivery Address: City, State, PIN Code (full address hidden initially, revealed after acceptance)
  - Delivery Option: Standard / Express
- **Commission Information**:
  - Product Amount: ₹X
  - BrandKit Commission (10-15%): ₹Y
  - Partner Earnings: ₹(X-Y)
- **Action Buttons** (based on status):
  - If Pending: "Accept Order", "Reject Order" (with reason dropdown)
  - If Accepted: "Start Production", "Upload Proof", "Mark as Shipped"
  - If Shipped: View tracking details
- **Notes Section**: Text area for partner notes (internal, not visible to client)

### FR-56: Order Acceptance/Rejection
Partners shall accept or reject orders:
- **Acceptance Workflow**:
  1. Partner clicks "Accept Order" on pending order
  2. System displays confirmation modal: "Confirm acceptance? Expected ship date: [date]"
  3. Partner confirms
  4. System:
     - Updates order status: "partner_accepted"
     - Reveals full delivery address to partner
     - Sends email to client: "Order accepted, in production"
     - Locks order (cannot be rejected after acceptance)
- **Rejection Workflow**:
  1. Partner clicks "Reject Order"
  2. System displays modal with reason dropdown:
     - Insufficient capacity
     - Product out of stock
     - Customization not feasible
     - Pricing issue
     - Other (text field)
  3. Partner selects reason and submits
  4. System:
     - Updates order status: "partner_rejected"
     - Notifies admin immediately (for reassignment)
     - Does NOT notify client yet (admin handles)
- **Acceptance Deadline**: 24 hours from order assignment, after which auto-escalates to admin

### FR-57: Production Status Updates
Partners shall update production progress:
- **Status Options**:
  - **In Production**: Production started
  - **Proof Ready**: Sample ready for review (requires proof upload)
  - **Production Complete**: Final products ready
  - **Ready to Ship**: Packed and ready for courier pickup
- **Update Workflow**:
  1. Partner on order details page clicks "Update Status"
  2. System displays dropdown with next available statuses
  3. Partner selects status
  4. If "Proof Ready": Prompt to upload proof images
  5. Partner clicks "Update"
  6. System:
     - Updates order status
     - Logs timestamp of status change
     - Sends notification to client (email): "Your order is now [status]"
- **Status Restrictions**: Cannot skip statuses (e.g., cannot jump from "Accepted" to "Shipped" without intermediate steps)

### FR-58: Proof Upload Functionality
Partners shall upload sample product images:
- **Upload Interface**:
  - File upload: 1-5 images, JPG/PNG, max 5MB each
  - Caption field per image (optional): "Front view", "Logo closeup", etc.
  - Upload button
- **Display**:
  - After upload, images appear in order details under "Proofs" section
  - Client can view proofs from their order tracking page
- **Approval** (Phase 2 feature, for MVP just upload):
  - Client sees proofs, provides feedback via support if issues
  - Partner proceeds with production after reasonable wait (24h)
- **Purpose**: Build client trust, reduce post-delivery disputes

### FR-59: Shipment Creation
Partners shall mark orders as shipped:
- **Shipment Form**:
  - Courier Partner: Dropdown (Delhivery, BlueDart, Local Courier, Other)
  - Tracking ID: Text field (mandatory)
  - Ship Date: Date picker (defaults to today)
  - Weight: Number (kg)
  - Number of Packages: Number
  - Notes: Text area (optional)
- **Workflow**:
  1. Partner on order details clicks "Mark as Shipped"
  2. System displays shipment form
  3. Partner fills details (tracking ID mandatory)
  4. Partner clicks "Submit"
  5. System:
     - Updates order status: "shipped"
     - Stores tracking ID and courier details
     - Sends email to client with tracking link
     - Sends SMS to client
     - Marks order as "fulfilled" for partner
- **Integration**: If courier is Delhivery (integrated), auto-fetch tracking updates via API

### FR-60: Commission Calculation (Accounting for Partner Discounts)
System shall calculate partner commissions automatically (internal calculation):
- **Formula** (accounts for partner-defined discounts):
  - **Base Product Amount**: Quantity × Original Unit Price (as per product catalog)
  - **Discount Amount**: Base Amount × Discount % (if partner discount applied)
  - **Final Product Amount**: Base Amount - Discount Amount
  - **Platform Commission**: 10-15% of Final Product Amount (configured per product or partner tier by admin)
    - Example: 10% for orders <₹10,000, 12% for ₹10k-50k, 15% for >₹50k
  - **Partner Earnings**: Final Product Amount - Platform Commission
- **Display**: On order details page (internal partner portal), commission breakdown clearly shown
- **Example** (with partner discount):
  - Order: 75 T-Shirts × ₹260 (original) = ₹19,500
  - Partner Discount (11.5%): -₹2,250
  - Final Product Amount: ₹17,250
  - Platform Commission (12% of ₹17,250): ₹2,070
  - Partner Earnings: ₹15,180
- **Note**: Platform commission calculated on discounted price, not original price. Partners bear cost of their discounts.

### FR-61: Commission Settlement Dashboard (Internal Partner Portal)
Partners shall view settlement information (internal portal):
- **Settlement Summary Card**:
  - Total Earnings (All Time): ₹X
  - Pending Settlement (current period): ₹Y
  - Last Settlement: ₹Z (Date)
  - Next Settlement Date: [Date]
- **Settlement Schedule**: Weekly or Monthly (configured by admin, default: Monthly on 5th)
- **Settlements List**:
  - Table with columns: Settlement ID, Period, Amount, Status, Date
  - Status: Pending, Processing, Completed, Failed
  - Download settlement statement (PDF)
- **Order-Wise Breakdown**:
  - Click settlement ID to see list of orders included
  - Per order: Order ID, Product, Commission, Earnings

### FR-62: Automated Payout Integration
System shall automate commission payouts:
- **Payout Trigger**: On settlement schedule (e.g., monthly)
- **Process**:
  1. System calculates total partner earnings for period (all delivered orders)
  2. System generates settlement record
  3. System initiates payout via payment gateway (Razorpay X, Cashfree Payouts)
  4. Funds transferred to partner's registered bank account
  5. System updates settlement status: "completed"
  6. System sends email to partner with settlement statement
- **Bank Details**: Partners provide bank account details in profile (verified by admin)
- **Minimum Payout**: ₹1,000 (if below, carry forward to next period)

### FR-63: Partner Performance Metrics
Partners shall see their performance dashboard:
- **Metrics Displayed**:
  - **Fulfillment Rate**: (Accepted Orders / Total Assigned Orders) × 100%
  - **Average Lead Time**: Average days from order acceptance to shipment
  - **Delivery Success Rate**: (Delivered Orders / Shipped Orders) × 100%
  - **Average Rating**: Star rating from client feedback (Phase 2, placeholder for MVP)
  - **Total Orders Fulfilled**: Count
  - **Total Revenue Generated**: Sum of all earnings
- **Time Period Filter**: Last 30 days, Last 3 months, Last year, All time
- **Benchmarking**: Display platform average for comparison (e.g., "Platform Avg: 95%")
- **Performance Alerts**:
  - If Fulfillment Rate <90%: Warning "Your fulfillment rate is below target. Improve to receive more orders."
  - If Avg Lead Time > Product Lead Time + 2 days: Warning "Your delivery is slow. Clients expect faster turnaround."

### FR-64: Partner Profile Management
Partners shall manage their profile:
- **Business Information** (editable):
  - Business Name
  - Owner Name
  - Phone Number
  - Email (read-only, used for login)
  - Business Address
  - GSTIN (for invoicing)
  - Categories (Products they specialize in): Multi-select
- **Bank Account Details** (editable, admin-verified):
  - Account Holder Name
  - Bank Name
  - Account Number
  - IFSC Code
  - Verification Status: Pending / Verified (admin action)
- **Capacity Settings**:
  - Max Concurrent Orders: Number (e.g., 20 orders at a time)
  - Currently Accepting Orders: Toggle (if off, no new orders assigned)
- **Profile Picture**: Upload logo (optional)
- **Save Changes**: Button with validation

### FR-64b: Partner Discount Management (Internal Portal - Rule 2 Implementation)
Partners shall define and manage product discounts (subject to platform admin approval and limits):
- **Discount Dashboard** (internal partner portal section):
  - **My Products List**: Table showing all products assigned to this partner
  - Columns: Product Name, Category, Base Price, Current Discount %, Status (Active/Disabled/Pending)
- **Define Discount**:
  - Partner selects product from their list
  - Partner enters discount percentage (e.g., 5%, 10%, 15%)
  - **Platform Validation**: System checks against admin-defined limits (e.g., Min: 0%, Max: 25%)
  - If within limits: Discount saved as "Pending Admin Approval"
  - If outside limits: Error "Discount must be between 0% and 25%"
- **Discount Status Indicators**:
  - **Active** (green): Discount approved by admin and live on client-facing product page
  - **Disabled** (red): Discount disabled by admin (platform control)
  - **Pending** (yellow): Awaiting admin approval
  - **Rejected** (gray): Admin rejected discount proposal
- **Discount Impact Preview**:
  - Original Price: ₹250
  - Proposed Discount: 10%
  - New Client Price: ₹225
  - **Partner Commission Impact**: Shows how discount affects partner earnings (commission calculated on discounted price)
- **Bulk Discount Actions**:
  - Apply same discount % to multiple products (category-wise or all)
  - Remove all discounts
- **Admin Controls** (enforced by backend):
  - Platform admin can enable/disable any partner discount (FR-72 in FRD-006)
  - Platform admin can set min/max limits per partner or globally
  - Platform admin can audit discount usage and suspend for abuse
- **Business Rules**:
  - BR-67b: Partners CANNOT bypass platform-defined min/max discount limits
  - BR-67c: Discounts require admin approval before going live (unless auto-approval enabled for trusted partners)
  - BR-67d: Platform admin can instantly disable any discount if pricing becomes non-competitive or abusive
  - BR-67e: Discount changes logged for audit trail (partner ID, old %, new %, timestamp, admin action)

### FR-65: Analytics Dashboard (Basic)
Partners shall see basic analytics (internal portal):
- **Orders Chart**: Line graph showing order count over time (last 30 days)
- **Revenue Chart**: Bar graph showing earnings per week (last 8 weeks)
- **Top Products**: List of top 5 products fulfilled by this partner
- **Delivery Performance**: Pie chart (On-Time: X%, Delayed: Y%)
- **Discount Performance**: Chart showing orders with vs without discounts, average discount %
- **Export Data**: Button to download order history and discount usage reports as CSV

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Partner Receives and Accepts Order
1. Client places order BK-20260123-001 (75 T-Shirts, ₹20,455 total)
2. System routes order to partner "PrintMaster Gujarat" (FR-45 from FRD-004)
3. System sends notification to partner:
   - Email: "New Order BK-20260123-001 - 75 Branded T-Shirts"
   - SMS: "New order BK-20260123-001 for ₹15,180 earnings. Login to review."
4. Partner receives notification on mobile
5. Partner logs into Partner Dashboard
6. Dashboard shows alert: "1 Pending Order requiring action"
7. Partner clicks "View Orders"
8. Order list displays BK-20260123-001 with status "Pending Acceptance"
9. Partner clicks order ID to view details
10. System displays Order Details page:
    - Order ID: BK-20260123-001
    - Product: Branded T-Shirt
    - Quantity: 75
    - Expected Ship Date: Jan 30 (7 days from order)
    - Delivery: Ahmedabad, Gujarat (city only visible)
    - Print-ready image: [Download High-Res Image] button
    - Commission breakdown:
      - Product Amount: ₹17,250
      - Platform Commission (12%): ₹2,070
      - Your Earnings: ₹15,180
11. Partner downloads print-ready image, reviews customization
12. Partner confirms feasibility
13. Partner clicks "Accept Order"
14. System displays modal: "Confirm acceptance? You commit to ship by Jan 30."
15. Partner clicks "Confirm"
16. System:
    - Updates order status: "partner_accepted"
    - Reveals full delivery address to partner
    - Sends email to client: "Great news! Your order is accepted and in production."
    - Logs acceptance timestamp
17. System displays success message: "Order accepted successfully!"
18. Order status changes to "Accepted", actions now show: "Start Production", "Upload Proof"

**Edge Cases**:
- If partner doesn't respond in 24 hours: System auto-escalates to admin, admin reassigns to alternate partner
- If partner clicks "Reject Order": Modal asks for reason, admin notified, client not notified immediately
- If partner already at max capacity (setting in profile): System doesn't assign order, finds alternate partner

### Workflow 2: Partner Updates Production Status and Uploads Proof
1. Partner starts working on accepted order BK-20260123-001
2. Day 1: Partner clicks "Start Production" on order details page
3. System updates status: "in_production", notifies client via email
4. Day 3: Partner completes sample t-shirt with logo
5. Partner clicks "Update Status" → Selects "Proof Ready"
6. System prompts: "Upload proof images to proceed"
7. Partner clicks "Upload Proof"
8. System displays upload interface
9. Partner uploads 3 images:
   - Image 1: T-shirt front view with logo (caption: "Front with logo")
   - Image 2: Logo closeup (caption: "Logo detail")
   - Image 3: T-shirt back view (caption: "Back view")
10. Partner clicks "Upload"
11. System:
    - Validates images (size, format)
    - Uploads to cloud storage (AWS S3)
    - Stores image URLs in order record
    - Updates order status: "proof_ready"
    - Sends email to client: "Sample ready for review. View proofs: [link]"
12. System displays success: "Proof uploaded successfully"
13. Client receives email, views proofs from order tracking page
14. Client reviews, sends feedback via support (minor adjustment needed)
15. Partner receives feedback via email from support team
16. Day 4: Partner makes adjustment, proceeds with full production
17. Partner updates status: "Production Complete" (no proof required this time)
18. System notifies client: "Your order is ready!"
19. Day 5: Partner updates status: "Ready to Ship"

**Edge Cases**:
- If proof upload fails: Display error "Upload failed. Try again or contact support"
- If client rejects proof: Support team coordinates with partner for redo (manual process for MVP)
- If partner skips proof upload: Allowed, but encouraged for quality assurance

### Workflow 3: Partner Ships Order
1. Partner has order BK-20260123-001 status: "Ready to Ship"
2. Partner packages 75 t-shirts (3 boxes)
3. Partner calls Delhivery for pickup
4. Delhivery provides tracking ID: DELIV12345678
5. Partner logs into dashboard, navigates to order details
6. Partner clicks "Mark as Shipped"
7. System displays shipment form
8. Partner fills:
   - Courier Partner: Delhivery (dropdown selection)
   - Tracking ID: DELIV12345678
   - Ship Date: Jan 30, 2026 (date picker, defaults to today)
   - Weight: 7.5 kg
   - Number of Packages: 3
   - Notes: "Packed in waterproof boxes"
9. Partner clicks "Submit"
10. System validates tracking ID format
11. System:
    - Updates order status: "shipped"
    - Stores shipment details in order record
    - Sends email to client: "Your order has shipped! Track: DELIV12345678, Tracking Link: [link]"
    - Sends SMS to client with tracking link
    - If courier is Delhivery: Calls Delhivery API to verify tracking ID and set webhook
12. System displays success: "Order marked as shipped! Tracking ID: DELIV12345678"
13. Order status changes to "Shipped", partner sees "View Tracking" button
14. Partner clicks "View Tracking"
15. System embeds Delhivery tracking widget or redirects to Delhivery site
16. Feb 5: Delhivery delivers order
17. Delhivery webhook notifies BrandKit
18. System:
    - Updates order status: "delivered"
    - Sends email to client: "Order delivered! Thank you."
    - Marks order as "fulfilled" for partner
    - Includes order in next settlement cycle for commission payout

**Edge Cases**:
- If tracking ID invalid: Display error "Invalid tracking ID format. Please check and retry."
- If courier API unavailable: Allow manual tracking link entry, skip API validation
- If shipment delayed: Delhivery webhook sends updates, system notifies client and partner
- If delivery failed: Delhivery webhook notifies, system updates status, support coordinates with partner for re-delivery

### Workflow 4: Partner Views Settlement and Receives Payout
1. Month-end (February 5, 2026)
2. System triggers automated settlement process
3. System calculates partner "PrintMaster Gujarat" earnings for January:
   - Delivered Orders: 15
   - Total Product Amount: ₹2,50,000
   - Platform Commission (12%): ₹30,000
   - Partner Earnings: ₹2,20,000
4. System generates Settlement ID: SET-2026-02-001
5. System initiates payout via Razorpay X:
   - Beneficiary: PrintMaster Gujarat (Bank: HDFC, Account: XXXXX1234, IFSC: HDFC0001234)
   - Amount: ₹2,20,000
6. Razorpay X processes payout (1-2 business days)
7. Payout successful
8. System:
   - Updates settlement status: "completed"
   - Generates settlement statement PDF
   - Sends email to partner: "Settlement processed! ₹2,20,000 credited to your account. Statement attached."
9. Partner receives email
10. Partner logs into dashboard
11. Partner clicks "Settlements" menu
12. System displays Settlement Dashboard:
    - Total Earnings (All Time): ₹5,80,000
    - Pending Settlement (February): ₹45,000
    - Last Settlement: ₹2,20,000 (Feb 5, 2026)
    - Next Settlement: Mar 5, 2026
13. Partner sees Settlements List:
    - SET-2026-02-001, January 2026, ₹2,20,000, Completed, Feb 5
14. Partner clicks settlement ID
15. System displays order-wise breakdown:
    - BK-20260123-001, T-Shirt × 75, ₹2,070 commission, ₹15,180 earnings
    - BK-20260125-003, Mug × 100, ₹1,800 commission, ₹13,200 earnings
    - ... (15 orders listed)
16. Partner downloads settlement statement PDF for accounting

**Edge Cases**:
- If bank details not verified: Settlement status "pending_verification", partner notified to update/verify bank details
- If payout fails (invalid account): System marks settlement "failed", notifies admin, partner contacted for correct details
- If earnings below minimum (₹1,000): Settlement status "pending", amount carried forward to next cycle
- If partner disputes settlement amount: Partner contacts support, admin reviews and adjusts if needed (manual process)

### Workflow 5: Partner Monitors Performance
1. Partner logs into dashboard weekly to check metrics
2. Partner clicks "Performance" menu
3. System displays Performance Metrics dashboard:
   - Fulfillment Rate: 93% (13 accepted / 14 assigned) → "Good performance!"
   - Average Lead Time: 6.5 days → "Within target (7 days)"
   - Delivery Success Rate: 100% (13 delivered / 13 shipped)
   - Average Rating: 4.6 stars (placeholder, Phase 2 feature)
   - Total Orders Fulfilled: 13
   - Total Revenue: ₹2,20,000
4. System displays benchmark: "Platform Average: 95%"
5. Partner notices 1 order was rejected (due to capacity issue last week)
6. Partner reviews order history, finds rejected order details
7. Partner decides to increase "Max Concurrent Orders" in profile to avoid future rejections
8. Partner navigates to Profile, updates:
   - Max Concurrent Orders: 25 (increased from 20)
9. Partner saves changes
10. System validates and updates profile
11. Next week: Partner receives more orders, maintains 95%+ fulfillment rate
12. Month-end: Partner checks performance again, sees improvement:
    - Fulfillment Rate: 96% (24 accepted / 25 assigned)
13. System displays: "Excellent performance! Top 10% of partners."
14. Partner satisfied with metrics, continues operations

**Edge Cases**:
- If performance drops below 85% fulfillment: System sends warning email "Your performance is below target. Risk of fewer order assignments."
- If lead time consistently exceeds product lead time: Admin may review and adjust partner's product lead time settings or provide feedback
- If delivery failures increase: System flags partner for admin review, may temporarily pause order assignments

---

## 7. INPUT & OUTPUT

### Inputs

#### Order Acceptance/Rejection
| Action | Input | Validation |
|--------|-------|------------|
| Accept Order | None (button click) | Partner must be active, order status must be "pending" |
| Reject Order | Reason (dropdown) | Reason required |

#### Production Status Update
| Field | Type | Options |
|-------|------|---------|
| Status | Dropdown | In Production, Proof Ready, Production Complete, Ready to Ship |

#### Proof Upload
| Field | Type | Validation |
|-------|------|------------|
| Images | File upload | 1-5 images, JPG/PNG, max 5MB each |
| Captions | Text | 0-100 chars per image, optional |

#### Shipment Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Courier Partner | Dropdown | Delhivery, BlueDart, Local, Other | Yes |
| Tracking ID | Text | 5-30 alphanumeric | Yes |
| Ship Date | Date | Not in future | Yes |
| Weight (kg) | Number | 0.1-1000 | No |
| Number of Packages | Number | 1-100 | No |
| Notes | Textarea | 0-500 chars | No |

#### Partner Profile
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Business Name | Text | 2-200 chars | Yes |
| Phone | Tel | +91-XXXXXXXXXX | Yes |
| Business Address | Textarea | 10-500 chars | Yes |
| GSTIN | Text | 15 alphanumeric (GST format) | Yes |
| Bank Account Holder | Text | 2-100 chars | Yes |
| Bank Name | Text | 2-100 chars | Yes |
| Account Number | Text | 8-18 digits | Yes |
| IFSC Code | Text | 11 chars (IFSC format) | Yes |
| Max Concurrent Orders | Number | 1-1000 | Yes |
| Accepting Orders | Toggle | Boolean | Yes |

### Outputs

#### Order Details Response
```json
{
  "status": "success",
  "data": {
    "orderId": "BK-20260123-001",
    "orderDate": "2026-01-23T12:00:00Z",
    "status": "pending_acceptance",
    "product": {
      "productId": "uuid-tshirt-001",
      "name": "Branded T-Shirt",
      "category": "T-Shirts",
      "quantity": 75,
      "specifications": {
        "material": "100% Cotton",
        "sizes": ["M", "L", "XL"]
      }
    },
    "customization": {
      "type": "Logo Print",
      "printReadyImageUrl": "https://s3.brandkit.com/prints/order-123-tshirt-print.png",
      "printArea": "20cm x 25cm"
    },
    "delivery": {
      "city": "Ahmedabad",
      "state": "Gujarat",
      "pinCode": "380015",
      "fullAddress": null,
      "option": "standard",
      "expectedShipDate": "2026-01-30"
    },
    "commission": {
      "productAmount": 17250,
      "platformCommission": 2070,
      "commissionPercentage": 12,
      "partnerEarnings": 15180
    },
    "actions": ["accept", "reject"]
  }
}
```

#### Settlement Summary Response
```json
{
  "status": "success",
  "data": {
    "partnerId": "uuid-partner-123",
    "summary": {
      "totalEarningsAllTime": 580000,
      "pendingSettlement": 45000,
      "lastSettlement": {
        "amount": 220000,
        "date": "2026-02-05"
      },
      "nextSettlementDate": "2026-03-05"
    },
    "settlements": [
      {
        "settlementId": "SET-2026-02-001",
        "period": "January 2026",
        "amount": 220000,
        "status": "completed",
        "date": "2026-02-05",
        "orderCount": 15,
        "statementUrl": "https://s3.brandkit.com/settlements/SET-2026-02-001.pdf"
      }
    ]
  }
}
```

#### Performance Metrics Response
```json
{
  "status": "success",
  "data": {
    "partnerId": "uuid-partner-123",
    "period": "Last 30 days",
    "metrics": {
      "fulfillmentRate": 93,
      "averageLeadTime": 6.5,
      "deliverySuccessRate": 100,
      "averageRating": 4.6,
      "totalOrdersFulfilled": 13,
      "totalRevenue": 220000
    },
    "benchmark": {
      "platformAverageFulfillment": 95
    },
    "alerts": []
  }
}
```

#### Shipment Confirmation Email (to Client)
```
Subject: Your BrandKit Order Has Shipped! 📦

Hi Rajesh,

Great news! Your order BK-20260123-001 has been shipped and is on its way!

Order Details:
- 75 × Branded T-Shirt
- Shipped by: Delhivery
- Tracking ID: DELIV12345678
- Estimated Delivery: Feb 5, 2026

Track your shipment: [Tracking Link]

Thank you for choosing BrandKit!

Best regards,
BrandKit Team
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-41: Order Acceptance Deadline
Partners must accept or reject orders within 24 hours of assignment. After 24 hours, orders auto-escalate to admin for manual reassignment.

### BR-42: Full Address Disclosure
Full client delivery address is revealed to partner only after order acceptance to protect client privacy and prevent direct partner-client transactions bypassing platform.

### BR-43: Status Progression
Order status must progress sequentially. Partners cannot skip statuses (e.g., cannot mark as "Shipped" without going through "In Production").

### BR-44: Commission Percentage
Platform commission is 10-15% based on order value or partner tier (configured by admin). Default: 12% for most partners.

### BR-45: Settlement Schedule
Settlements occur on a fixed schedule (default: Monthly on 5th). Partners cannot request ad-hoc payouts for MVP.

### BR-46: Minimum Payout Threshold
Minimum settlement amount is ₹1,000. Earnings below threshold carry forward to next cycle.

### BR-47: Bank Details Verification
Bank account details must be verified by admin before first payout. Partners receive email notification upon verification.

### BR-48: Tracking ID Requirement
Tracking ID is mandatory for marking orders as shipped. Without tracking ID, partner cannot proceed (ensures client can track delivery).

### BR-49: Performance Impact on Order Assignment
Partners with fulfillment rate <85% may receive fewer order assignments. System deprioritizes low-performing partners in routing algorithm.

### BR-50: Order Rejection Limit
Partners can reject up to 15% of assigned orders without penalty. Exceeding 15% rejection rate triggers admin review and potential account suspension.

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Order not found | VEND_001 | 404 | "Order not found" | Return to order list |
| Already accepted by another partner | VEND_002 | 409 | "Order already assigned to another partner" | Refresh order list |
| Cannot reject after acceptance | VEND_003 | 403 | "Cannot reject accepted order. Contact support." | Contact support for issues |
| Invalid tracking ID format | VEND_004 | 400 | "Invalid tracking ID. Please check format." | Correct tracking ID |
| Proof upload failed | VEND_005 | 500 | "Image upload failed. Please retry." | Retry upload or reduce image size |
| Bank details unverified | VEND_006 | 403 | "Bank details pending verification. Settlement on hold." | Contact admin or wait for verification |
| Settlement not found | VEND_007 | 404 | "Settlement record not found" | Return to settlements list |
| Payout failed | VEND_008 | 500 | "Payout failed due to bank error. Admin notified." | Check bank details, wait for admin contact |
| Acceptance deadline passed | VEND_009 | 409 | "Order acceptance deadline passed. Order reassigned." | No action, order no longer available |
| Profile update failed | VEND_010 | 400 | "Invalid profile data. Check all fields." | Correct validation errors |
| Max capacity reached | VEND_011 | 403 | "Max concurrent orders reached. Complete existing orders first." | Fulfill pending orders or increase capacity |
| Shipment date in future | VEND_012 | 400 | "Ship date cannot be in future" | Select today or past date |

### Error Handling Strategy
- Display user-friendly error messages inline or as toast notifications
- Provide actionable next steps (buttons, links, contact support)
- Log detailed errors server-side for admin review
- For critical errors (payout failures), auto-notify admin via dashboard alert
- For transient errors (network issues), provide retry mechanism
- Never expose sensitive data (client details, bank info) in error messages

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-98**: Partner dashboard home must load within 1.5 seconds
- **NFR-99**: Order list must load within 2 seconds (20 orders)
- **NFR-100**: Order details page must load within 1.5 seconds
- **NFR-101**: Image uploads (proofs) must complete within 5 seconds per image
- **NFR-102**: Notifications (email, SMS) must be sent within 1 minute of trigger event

### Scalability
- **NFR-103**: System must support 100+ concurrent partners without degradation
- **NFR-104**: Order list must efficiently handle 10,000+ orders per partner (pagination, indexing)
- **NFR-105**: Settlement calculations must process 500+ orders per partner in <10 seconds

### Reliability
- **NFR-106**: Notification system must have 99% delivery rate (email + SMS backup)
- **NFR-107**: Payout system must have 99.9% success rate (with retry mechanism)
- **NFR-108**: Order status updates must be atomic (prevent race conditions)

### Security
- **NFR-109**: Client addresses hidden until order acceptance (privacy protection)
- **NFR-110**: Bank account details encrypted at rest (AES-256)
- **NFR-111**: Partner can only access orders assigned to them (strict access control)
- **NFR-112**: Admin actions logged for audit trail (order reassignments, manual interventions)

### Usability
- **NFR-113**: Dashboard must be mobile-responsive (partners may access on tablets/phones)
- **NFR-114**: Critical actions (accept/reject, ship) require confirmation modal (prevent accidents)
- **NFR-115**: Performance alerts should be constructive, not punitive in tone

---

## 11. ACCEPTANCE CRITERIA

### AC-41: Receive and View Order Notification
**Given** a new order BK-20260123-001 assigned to partner "PrintMaster Gujarat"  
**When** the system routes the order  
**Then** the partner receives:
- Email: "New Order BK-20260123-001 - 75 Branded T-Shirts"
- SMS: "New order BK-20260123-001 for ₹15,180. Login to review."
**And** the partner dashboard shows alert: "1 Pending Order"  
**When** the partner clicks "View Orders"  
**Then** the order list displays BK-20260123-001 with status "Pending Acceptance"

### AC-42: Accept Order
**Given** partner viewing order details for BK-20260123-001 (status: Pending)  
**When** the partner clicks "Accept Order"  
**Then** the system displays confirmation modal: "Confirm acceptance? Expected ship date: Jan 30"  
**When** the partner confirms  
**Then** the system:
- Updates order status to "partner_accepted"
- Reveals full delivery address
- Sends email to client: "Order accepted"
- Displays success "Order accepted successfully!"
**And** the order actions change to "Start Production", "Upload Proof"

### AC-43: Reject Order
**Given** partner viewing order details (status: Pending)  
**When** the partner clicks "Reject Order"  
**Then** the system displays modal with reason dropdown  
**When** the partner selects "Insufficient capacity" and submits  
**Then** the system:
- Updates order status to "partner_rejected"
- Notifies admin for reassignment
- Does NOT notify client
- Displays "Order rejected. Admin will handle reassignment."
**And** the order disappears from partner's pending list

### AC-44: Update Production Status
**Given** partner has accepted order (status: Accepted)  
**When** the partner clicks "Update Status" and selects "In Production"  
**Then** the system:
- Updates order status to "in_production"
- Logs timestamp
- Sends email to client: "Your order is now in production"
- Displays success message

### AC-45: Upload Proof Images
**Given** partner has order in production  
**When** the partner clicks "Update Status" → "Proof Ready"  
**Then** the system prompts "Upload proof images"  
**When** the partner uploads 3 images (JPG, <5MB each) with captions  
**And** clicks "Upload"  
**Then** the system:
- Validates and uploads images to S3
- Updates order status to "proof_ready"
- Sends email to client with proof viewing link
- Displays "Proof uploaded successfully"
**And** the order details page shows uploaded proofs

### AC-46: Mark Order as Shipped
**Given** partner has order ready to ship (status: Ready to Ship)  
**When** the partner clicks "Mark as Shipped"  
**Then** the system displays shipment form  
**When** the partner fills:
- Courier: Delhivery
- Tracking ID: DELIV12345678
- Ship Date: Jan 30, 2026
**And** clicks "Submit"  
**Then** the system:
- Validates tracking ID
- Updates order status to "shipped"
- Stores shipment details
- Sends email and SMS to client with tracking link
- Displays "Order marked as shipped!"
**And** the order shows "View Tracking" button

### AC-47: View Commission Breakdown
**Given** partner viewing order details  
**Then** the system displays commission section:
- Product Amount: ₹17,250
- Platform Commission (12%): ₹2,070
- Your Earnings: ₹15,180
**And** the breakdown is clearly visible and accurate

### AC-48: View Settlement Dashboard
**Given** partner navigates to "Settlements"  
**Then** the system displays:
- Total Earnings (All Time): ₹5,80,000
- Pending Settlement: ₹45,000
- Last Settlement: ₹2,20,000 (Feb 5)
- Next Settlement Date: Mar 5
- Settlements List with completed settlements
**When** the partner clicks settlement ID  
**Then** the system displays order-wise breakdown with all orders included in that settlement

### AC-49: Receive Automated Payout
**Given** settlement date arrives (Feb 5)  
**When** the system triggers settlement process  
**Then** the system:
- Calculates partner earnings for period
- Generates settlement ID: SET-2026-02-001
- Initiates payout via Razorpay X to partner's bank account
**When** payout succeeds  
**Then** the system:
- Updates settlement status to "completed"
- Sends email to partner with settlement statement
**And** the partner's bank account is credited with ₹2,20,000

### AC-50: View Performance Metrics
**Given** partner navigates to "Performance"  
**Then** the system displays:
- Fulfillment Rate: 93%
- Average Lead Time: 6.5 days
- Delivery Success Rate: 100%
- Total Orders: 13
- Total Revenue: ₹2,20,000
**And** displays benchmark: "Platform Average: 95%"  
**And** shows performance trend graph

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Email Service**: SendGrid or AWS SES for order notifications
- **SMS Service**: Twilio, MSG91 for SMS alerts
- **Payment Gateway**: Razorpay X or Cashfree Payouts for partner settlements
- **Delivery Partners**: Delhivery API for tracking verification
- **Cloud Storage**: AWS S3 for proof images and settlement statements

### Internal Dependencies
- **FRD-001 (Authentication)**: Partner login and role-based access
- **FRD-004 (Order Management)**: Partner order routing, order status updates
- **FRD-003 (Customization Engine)**: Print-ready image generation

### Database Tables (Supabase PostgreSQL)
- `partners`: Partner profiles and settings (relational table with proper foreign keys)
- `partner_orders`: Partner-specific order records (junction table linking orders to partners)
- `settlements`: Settlement records and payouts (with referential integrity to partners)
- `proof_images`: Uploaded proof images metadata (normalized structure)
- `shipments`: Shipment details and tracking (related to orders)
- `partner_discounts`: Partner-defined discounts with admin approval status (NEW - Rule 2)
  - Columns: id, partner_id, product_id, discount_percentage, status (pending/approved/disabled/rejected), created_at, updated_at, admin_notes
- `discount_audit_log`: Audit trail for discount changes (NEW - Rule 2)
  - Columns: id, partner_id, product_id, old_discount, new_discount, changed_by (partner/admin), timestamp, reason
- **Row Level Security (RLS)**: Partners can only access their own data. Admin role has full access.
- **Indexes**: B-tree indexes on partner_id, order_id, status, created_at for efficient queries

### API Endpoints (Internal Partner Portal APIs - NOT client-accessible)
- `GET /api/partner/dashboard`: Dashboard summary
- `GET /api/partner/orders`: Order list (partner's orders only, RLS enforced)
- `GET /api/partner/orders/:orderId`: Order details
- `POST /api/partner/orders/:orderId/accept`: Accept order
- `POST /api/partner/orders/:orderId/reject`: Reject order (with reason)
- `PUT /api/partner/orders/:orderId/status`: Update production status
- `POST /api/partner/orders/:orderId/proof`: Upload proof images
- `POST /api/partner/orders/:orderId/ship`: Mark as shipped with tracking
- `GET /api/partner/settlements`: Settlement dashboard
- `GET /api/partner/settlements/:settlementId`: Settlement details
- `GET /api/partner/performance`: Performance metrics
- `GET /api/partner/profile`: Get partner profile
- `PUT /api/partner/profile`: Update partner profile
- `GET /api/partner/discounts`: Get all discounts for partner's products (NEW - Rule 2)
- `POST /api/partner/discounts`: Create/update discount for product (NEW - Rule 2, requires admin approval)
- `DELETE /api/partner/discounts/:discountId`: Remove discount (NEW - Rule 2)
- **Security**: All endpoints require "PARTNER" role JWT token. Client role CANNOT access these endpoints.

---

## 13. ASSUMPTIONS

1. Partners have reliable internet access for dashboard usage
2. Partners have digital cameras or smartphones to capture proof images
3. Partners can coordinate with delivery partners (Delhivery, local couriers)
4. Partners have business bank accounts for receiving payouts
5. Partners understand basic production workflows and lead times
6. Admin manually onboards and vets partners before activation
7. Partners primarily work on desktop/laptop but may access dashboard on tablets
8. Gujarat-based partners are tech-savvy enough to use digital dashboard
9. Partners can handle print-ready images (300 DPI PNG/TIFF files)
10. Partner rejection rate remains low (<15%) for healthy platform operation

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Partner self-registration with KYC verification
- Multi-user accounts for partner businesses (manager, production staff)
- Real-time chat between partner and BrandKit support
- Partner-to-client direct communication (with moderation)
- Advanced analytics: Revenue trends, product performance, seasonal insights
- Integration with partner production management systems
- Dynamic commission negotiation based on performance
- Partner dispute resolution workflow
- Partner rating appeal process
- Capacity forecasting tool (predict busy periods)
- Bulk order acceptance (accept multiple orders at once)
- Proof approval workflow (client approves/rejects proofs formally)
- Automated quality checks (image recognition for print accuracy)
- Partner training portal (onboarding videos, best practices)
- Partner community forum (peer-to-peer knowledge sharing)
- Mobile app for partners (native iOS/Android)
- Push notifications for critical alerts
- Invoice generation for partner services (partner invoices BrandKit for reconciliation)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Partner onboarding workflow design, payout API integration (Razorpay X), notification service setup, partner UI/UX wireframes
