# FRD-006: Admin Panel and Platform Management

---

## 1. FRD METADATA

**FRD Title**: Admin Panel for Platform Management and Operations  
**FRD ID**: FRD-006  
**Related PRD Section(s)**: 4.2.1 Core Features - Admin Panel  
**Priority**: High  
**Owner**: Product / Engineering  
**Version**: 1.0 (Updated: Global Changes Applied - Jan 30, 2026)  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Develop a comprehensive admin panel that enables platform administrators to manage users, partners (fulfillment), products, orders, commissions, analytics, partner discount controls (Rule 2), and system settings to ensure smooth platform operations, resolve issues, and drive business growth while maintaining strict partner anonymity to clients.

### Business Value
- Centralized control over all platform operations
- Enable efficient user and partner management (partner details kept internal)
- Manage partner-defined discounts with platform oversight (enable/disable, set limits, audit usage)
- Monitor platform health and business metrics
- Handle exceptions and edge cases manually
- Configure commission structures for revenue optimization
- Provide data-driven insights for strategic decisions
- Ensure platform reliability and quality control
- Protect partner anonymity while managing fulfillment ecosystem

---

## 3. SCOPE

### In Scope
- Admin authentication and role management (Super Admin, Operations Admin)
- Dashboard with key metrics and recent activity
- User management: View, activate/deactivate, search, filter clients
- **Partner management**: Onboard, view, activate/deactivate, verify bank details (internal management, NOT exposed to clients)
- Product management: Add, edit, delete products, configure print areas, associate partners (internal association)
- Order management: View all orders, filter, search, manually intervene (reassign partner, update status) - partner details visible to admin only
- **Discount Management (Rule 2 - NEW)**:
  - View all partner-defined discounts
  - Approve/reject partner discount proposals
  - Enable/disable individual discounts or partner-wide discounts
  - Set min/max discount limits (global or per-partner)
  - Audit discount usage (frequency, impact on margins)
  - Suspend partners for discount abuse
- Commission settings: Configure platform commission percentages per partner tier or order value
- Settlement management: View, approve, manually process settlements to partners
- Analytics: Revenue, orders, user acquisition, partner performance (internal only), top products
- System logs: View critical events, errors, admin actions, discount changes
- Notification templates: Configure email/SMS templates
- Platform settings: Delivery charges, tax rates, payment gateway config

### Out of Scope
- Content management system (CMS) for marketing pages (manual code updates for MVP)
- A/B testing platform for feature experimentation
- Customer support ticketing system (use external tool like Zendesk for MVP)
- Bulk import/export via UI (CSV import/export via backend scripts for MVP)
- Advanced fraud detection (basic rules only)
- Automated partner performance scoring (manual review for MVP)
- Integrated accounting system (export data to external accounting software)
- Multi-language content management (English only for MVP)

---

## 4. USER STORIES

### Admin Users
- **US-061**: As an Admin, I want to view a dashboard with key metrics so that I can monitor platform health at a glance
- **US-062**: As an Admin, I want to deactivate a problematic client account so that I can prevent misuse
- **US-063**: As an Admin, I want to onboard new partners so that I can expand our supplier network
- **US-064**: As an Admin, I want to add new products to the catalog so that I can offer more options to clients
- **US-065**: As an Admin, I want to manually reassign an order to a different partner so that I can resolve fulfillment issues
- **US-066**: As an Admin, I want to configure commission rates so that I can optimize platform revenue
- **US-067**: As an Admin, I want to approve partner settlements so that I can ensure accurate payouts
- **US-068**: As an Admin, I want to view revenue analytics so that I can track business growth
- **US-069**: As an Admin, I want to view system logs so that I can debug issues
- **US-070**: As an Admin, I want to update delivery charges so that I can adjust to market rates

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-66: Admin Authentication and Roles
The system shall provide admin-specific authentication:
- **Admin Creation**: Only existing admins can create new admin accounts (no public signup)
- **Roles**:
  - **Super Admin**: Full access to all features, can create/manage other admins
  - **Operations Admin**: Access to user, partner, order, settlement management; no access to system settings, commission config
- **Login**: Same authentication as users (FRD-001) but role-based access control
- **Access Control**: Each feature checks user role, denies access if insufficient permissions

### FR-67: Admin Dashboard Home
Admins shall see a comprehensive dashboard upon login:
- **Metrics Cards** (Top Row):
  - **Total Revenue (This Month)**: ₹X (with % change vs. last month)
  - **Total Orders (This Month)**: Count (with % change)
  - **Active Users**: Count of active clients
  - **Active Partners**: Count of active partners
- **Charts** (Middle Section):
  - **Revenue Trend**: Line graph, last 30 days, daily revenue
  - **Orders Trend**: Line graph, last 30 days, daily order count
  - **Order Status Distribution**: Pie chart (Confirmed, In Production, Shipped, Delivered)
- **Recent Activity** (Right Sidebar):
  - Last 10 orders (Order ID, Client name, Amount, Status)
  - Last 5 user registrations (Name, Email, Date)
  - Last 5 partner actions (Partner name, Action, Date)
- **Alerts** (Top Notification Bar):
  - Pending partner orders (awaiting acceptance, deadline approaching)
  - Rejected orders requiring reassignment
  - Failed settlements requiring intervention
  - Low-performing partners (fulfillment rate <85%)
- **Quick Actions** (Button Row):
  - Add Product, Onboard Partner, View All Orders, Process Settlements

### FR-68: User Management
Admins shall manage client and partner users:

**User List View**:
- **Table Columns**: User ID, Name, Email, Role (Client/Partner), Status (Active/Inactive), Registration Date, Last Login, Actions
- **Filters**:
  - Role: All, Client, Partner, Admin
  - Status: All, Active, Inactive
  - Registration Date: Last 7 days, Last 30 days, Last 3 months, Custom range
- **Search**: By name, email, user ID
- **Sort**: By registration date, last login, name
- **Pagination**: 50 users per page
- **Actions per user**:
  - View Details (opens user detail page)
  - Activate / Deactivate
  - Reset Password (send reset link to user email)
  - View Orders (for that user)

**User Detail Page**:
- **User Information**:
  - Name, Email, Phone, Company Name
  - Role, Status, Registration Date, Last Login
  - Profile Picture
  - Address(es) saved
- **Activity Summary**:
  - Total Orders: Count
  - Total Spent: ₹X
  - Average Order Value: ₹Y
  - Last Order Date
- **Order History**: Table with last 20 orders (Order ID, Date, Amount, Status)
- **Actions**:
  - Edit User Profile (admin can update name, phone, company)
  - Deactivate / Activate Account
  - Send Email (manually trigger email to user)
  - View Logs (audit trail of user actions)

**User Activation/Deactivation**:
- **Deactivate**: User cannot log in, existing sessions invalidated, reason field (optional)
- **Activate**: User can log in again
- **Confirmation Modal**: "Are you sure you want to deactivate [User Name]?"

### FR-69: Partner Management
Admins shall manage partner partners:

**Partner Onboarding**:
- **Add Partner Form**:
  - Business Name (mandatory)
  - Owner Name (mandatory)
  - Email (mandatory, unique, used for login)
  - Phone (mandatory, +91-XXXXXXXXXX)
  - Business Address (mandatory)
  - GSTIN (mandatory, validated format)
  - Categories (multi-select: Bags, Pens, T-Shirts, etc.)
  - Bank Account Details:
    - Account Holder Name
    - Bank Name
    - Account Number
    - IFSC Code
  - Commission Percentage (default 12%, configurable)
  - Max Concurrent Orders (default 20)
  - Generate Password (auto-generate secure password)
- **Workflow**:
  1. Admin fills form
  2. System validates all fields
  3. System creates partner account with role "Partner", status "Active"
  4. System sends welcome email to partner with login credentials
  5. System displays success: "Partner onboarded successfully"

**Partner List View**:
- **Table Columns**: Partner ID, Business Name, Email, Location (City), Categories, Status, Performance Score, Actions
- **Filters**:
  - Status: All, Active, Inactive
  - Categories: Bags, Pens, T-Shirts, etc.
  - Location: Gujarat, Other states
  - Performance: High (>95%), Medium (85-95%), Low (<85%)
- **Search**: By business name, email, partner ID
- **Actions per partner**:
  - View Details
  - Activate / Deactivate
  - Verify Bank Details
  - View Orders
  - Edit Commission Rate

**Partner Detail Page**:
- **Partner Information**:
  - Business Name, Owner, Email, Phone
  - Address, GSTIN
  - Categories, Commission Rate
  - Bank Account Details (masked: XXXX1234)
  - Bank Verification Status: Pending / Verified
  - Status, Registration Date
- **Performance Metrics** (pulled from FRD-005):
  - Fulfillment Rate, Avg Lead Time, Delivery Success Rate
  - Total Orders Fulfilled, Total Revenue Generated
- **Recent Orders**: Table with last 20 orders
- **Actions**:
  - Edit Partner Profile
  - Verify Bank Details (admin confirms, changes status to "Verified")
  - Adjust Commission Rate
  - Deactivate / Activate Account
  - Send Email

**Bank Details Verification**:
- Admin views unverified bank details
- Admin manually verifies via test transaction or document check
- Admin clicks "Verify Bank Details"
- System updates verification status to "Verified"
- Partner notified via email: "Bank details verified, settlements enabled"

### FR-70: Product Management
Admins shall manage product catalog:

**Product List View** (similar to client view but with admin actions):
- **Table Columns**: Product ID, Image, Name, Category, Partner, Base Price, Status, Actions
- **Filters**: Category, Partner, Status (Active/Inactive)
- **Search**: By product name, ID
- **Actions per product**:
  - View Details
  - Edit Product
  - Activate / Deactivate
  - Delete (soft delete)

**Add Product Form** (detailed in FRD-002, admin interface):
- All product fields: Name, Category, Description, Price, Material, Images, Customization settings, Print Area
- Partner Selection (dropdown of active partners)
- Pricing Tiers (dynamic table to add multiple tiers)
- Status: Active / Inactive
- **Print Area Configuration**:
  - Upload product base image
  - Visual editor: Drag rectangle on image to define print area
  - Set print area dimensions (width × height in cm)
  - System calculates aspect ratio, stores coordinates
- **Image Upload**: 3-8 images, drag-drop, reorder, set primary
- **Save as Draft / Publish**: Save for review or publish immediately

**Edit Product**:
- Same form as Add, pre-filled with existing data
- All fields editable except Product ID
- Save changes, system logs edit history

**Delete Product** (Soft Delete):
- Admin clicks "Delete" on product
- Confirmation modal: "Are you sure? This product will be hidden from clients."
- System updates status to "Deleted" (not removed from database)
- Product no longer visible to clients
- Existing orders with this product remain accessible

### FR-71: Order Management
Admins shall monitor and manage all orders:

**Order List View**:
- **Table Columns**: Order ID, Client Name, Partner Name, Products (count), Total Amount, Status, Order Date, Actions
- **Filters**:
  - Status: All, Confirmed, Partner Accepted, In Production, Shipped, Delivered, Cancelled, Payment Failed
  - Date Range: Last 7 days, Last 30 days, Last 3 months, Custom
  - Partner: Dropdown of all partners
  - Client: Search by name
- **Search**: By Order ID, Client Email
- **Sort**: By Order Date (newest first), Total Amount (high-low)
- **Pagination**: 50 orders per page
- **Actions per order**:
  - View Details
  - Reassign Partner (if order not yet accepted)
  - Update Status (manual override)
  - Cancel Order (with refund initiation)
  - Download Invoice

**Order Detail Page** (Admin View):
- **Order Information**: All details visible to client (FRD-004)
- **Additional Admin Fields**:
  - Client Email, Phone
  - Partner Email, Phone
  - Full Delivery Address
  - Payment Transaction ID, Method
  - Commission Breakdown (platform commission, partner earnings)
  - Customization Data (crop coordinates, logo URL, print-ready image URL)
- **Status Timeline**: Detailed log of status changes with timestamps
- **Admin Actions**:
  - Reassign Partner (dropdown to select alternate partner)
  - Update Status (manual override dropdown)
  - Cancel Order (with reason, initiates refund)
  - Send Email to Client / Partner
  - Add Internal Notes (not visible to client/partner)
- **Logs**: Audit trail (who changed what, when)

**Order Reassignment**:
- **Trigger**: Partner rejects order OR order stuck at "pending_acceptance"
- **Workflow**:
  1. Admin clicks "Reassign Partner" on order detail page
  2. System displays partner selector (dropdown of active partners in same category)
  3. Admin selects new partner
  4. Admin clicks "Reassign"
  5. System:
     - Updates partner assignment in order record
     - Sends notification to new partner
     - Sends internal note to original partner (if applicable)
     - Logs reassignment action
  6. System displays success: "Order reassigned to [Partner Name]"

**Order Cancellation**:
- **Workflow**:
  1. Admin clicks "Cancel Order"
  2. System displays modal with reason field (mandatory)
  3. Admin enters reason (e.g., "Client request", "Partner unavailable")
  4. Admin clicks "Confirm Cancellation"
  5. System:
     - Updates order status to "Cancelled"
     - Initiates refund via payment gateway (if payment completed)
     - Sends email to client: "Order cancelled, refund initiated"
     - Sends email to partner: "Order cancelled, no action required"
     - Logs cancellation with reason
  6. System displays: "Order cancelled, refund will be processed in 3-5 days"

### FR-72: Commission Settings
Admins shall configure platform commission:

**Commission Configuration Page**:
- **Default Commission Rate**: Single input field, percentage (default 12%)
- **Tiered Commission by Order Value**:
  - Table with rows: Order Value Range (Min-Max), Commission %
  - Example:
    - ₹0 - ₹10,000 → 10%
    - ₹10,001 - ₹50,000 → 12%
    - ₹50,001+ → 15%
  - Add/Remove tier buttons
- **Partner-Specific Commission**:
  - Override default for specific partners
  - Table: Partner Name, Custom Commission %, Reason
  - Add Partner button (modal to select partner and set rate)
- **Save Changes**: Button, updates system-wide commission calculation

**Commission Calculation Display**:
- On every order detail page (admin view), show:
  - Product Amount: ₹X
  - Commission Rule Applied: "Tiered (12% for ₹10k-50k order)"
  - Platform Commission: ₹Y
  - Partner Earnings: ₹(X-Y)

### FR-72b: Partner Discount Management and Controls (Rule 2 Implementation)
Admins shall have comprehensive control over partner-defined discounts:

**Discount Management Dashboard**:
- **Summary Cards**:
  - Total Active Discounts: Count
  - Pending Approvals: Count
  - Avg Discount Across Platform: X.X%
  - Total Discount Impact (This Month): ₹X (revenue foregone by partners)
- **Discount List View**:
  - Table Columns: Product Name, Partner Name, Discount %, Status (Active/Disabled/Pending/Rejected), Created Date, Last Modified, Actions
  - Filters: Status, Partner, Product Category, Discount Range (e.g., 5-10%, 10-15%)
  - Search: By Product Name, Partner Name
  - Sort: By discount %, date, status

**Discount Actions** (per discount):
- **Approve**: Change status from "Pending" to "Active" (discount goes live on client-facing product page)
- **Reject**: Change status to "Rejected" with reason (partner notified)
- **Disable**: Instantly disable active discount (override partner setting, discount stops showing to clients)
- **Enable**: Re-enable previously disabled discount
- **Edit Limits**: Modify min/max discount % allowed for specific partner
- **View Impact**: See order count and revenue with this discount applied

**Global Discount Controls**:
- **Set Global Limits**:
  - Min Discount %: 0%
  - Max Discount %: 25% (default)
  - Apply to all partners or per-partner overrides
- **Auto-Approval Rules**:
  - Enable auto-approval for trusted partners (discount goes live immediately without admin review)
  - Set trust threshold (e.g., partners with >50 fulfilled orders and >4.5 rating)
- **Bulk Actions**:
  - Disable all discounts for a partner (e.g., due to abuse)
  - Disable all discounts in a category
  - Approve all pending discounts from a partner

**Discount Audit and Abuse Detection**:
- **Audit Log**: Table showing all discount changes:
  - Columns: Timestamp, Partner, Product, Old %, New %, Changed By (Partner/Admin), Status Change, Reason
  - Export to CSV for compliance
- **Abuse Detection Alerts**:
  - Partner changing discounts too frequently (>5 times/day)
  - Discount set too high (>Max Limit)
  - Suspicious patterns (e.g., discount applied then removed rapidly)
  - Alert admin via dashboard notification and email
- **Partner Suspension for Abuse**:
  - Admin can suspend partner's discount privileges
  - Suspended partner cannot modify discounts (existing discounts disabled)
  - Suspension logged with reason

**Discount Impact Analytics**:
- **Charts**:
  - Orders with Discounts vs. Without (pie chart)
  - Avg Discount % Trend (line chart, last 30 days)
  - Top 10 Products by Discount Usage
  - Partner-wise Discount Comparison (bar chart)
- **Revenue Impact**:
  - Total Revenue (with discounts): ₹X
  - Estimated Revenue (without discounts): ₹Y
  - Partner-funded Discounts: ₹(Y-X)
  - Platform commission impact (commission calculated on discounted price)

**Business Rules Enforcement**:
- **BR-75a**: Partners CANNOT set discounts outside admin-defined min/max limits
- **BR-75b**: Admin can override any partner discount setting (disable immediately)
- **BR-75c**: Discount changes by admin logged for audit trail
- **BR-75d**: Partners notified via email when admin disables their discount (with reason)
- **BR-75e**: Suspended partners cannot create/modify discounts until admin reinstates
- **BR-75f**: Discount approvals required unless partner is on auto-approval list

### FR-73: Settlement Management
Admins shall manage partner settlements:

**Settlement Dashboard**:
- **Summary Cards**:
  - Pending Settlements: Count, Total Amount
  - Completed Settlements (This Month): Count, Total Amount
  - Failed Settlements: Count
- **Settlement List**:
  - Table Columns: Settlement ID, Partner Name, Period, Amount, Status, Created Date, Actions
  - Filters: Status (All, Pending, Completed, Failed), Partner, Period
  - Search: By Settlement ID, Partner Name
  - Actions per settlement:
    - View Details
    - Approve (if pending)
    - Retry (if failed)
    - Download Statement

**Settlement Detail Page**:
- **Settlement Information**:
  - Settlement ID, Partner Name
  - Period (e.g., "January 2026")
  - Total Amount, Order Count
  - Status, Created Date
  - Bank Account (masked): XXXX1234
- **Order Breakdown**: Table with all orders included:
  - Order ID, Date, Product Amount, Commission, Partner Earnings
  - Total row with sum
- **Actions**:
  - Approve Settlement (triggers payout)
  - Reject Settlement (with reason, recalculate)
  - Manually Process (admin-initiated payout)

**Approve Settlement**:
- **Workflow**:
  1. Admin reviews settlement details
  2. Admin clicks "Approve Settlement"
  3. System displays confirmation: "Approve payout of ₹X to [Partner Name]?"
  4. Admin confirms
  5. System:
     - Initiates payout via payment gateway (Razorpay X)
     - Updates status to "Processing"
     - Logs approval action
  6. Payment gateway processes (1-2 days)
  7. Webhook confirms success
  8. System updates status to "Completed"
  9. Partner receives email notification

**Failed Settlement Retry**:
- If payout fails (invalid bank account, insufficient balance in platform account):
  - System marks settlement "Failed"
  - Admin receives alert
  - Admin reviews failure reason
  - Admin clicks "Retry"
  - System re-attempts payout
  - If still fails: Admin manually resolves (contact partner for correct details)

### FR-74: Analytics Dashboard
Admins shall access business analytics:

**Revenue Analytics**:
- **Total Revenue**: Card with time filter (This Month, Last 3 Months, Last Year)
- **Revenue Trend**: Line graph, daily/weekly/monthly breakdown
- **Revenue by Category**: Bar chart (Bags: ₹X, T-Shirts: ₹Y, etc.)
- **Commission Earned**: Total platform commission, trend over time
- **Average Order Value**: Metric card with trend

**Order Analytics**:
- **Total Orders**: Card with time filter
- **Orders by Status**: Pie chart (Delivered 60%, In Production 20%, etc.)
- **Orders by Partner**: Table (Partner Name, Order Count, Total Value, Avg Fulfillment Time)
- **Top Products**: Table (Product Name, Order Count, Revenue Generated)
- **Order Funnel**: Visual funnel (Cart → Checkout → Payment → Confirmed → Delivered) with drop-off rates

**User Analytics**:
- **Total Users**: Card (Clients, Partners, Admins breakdown)
- **User Acquisition**: Line graph (signups over time)
- **Active Users**: Monthly active users (MAU), daily active users (DAU)
- **Top Clients**: Table (Client Name, Total Orders, Total Spent)
- **User Retention**: Cohort analysis (% of users who reorder)

**Partner Analytics**:
- **Partner Performance**: Table (Partner Name, Fulfillment Rate, Avg Lead Time, Total Orders, Revenue)
- **Partner Comparison**: Bar chart (compare fulfillment rates, lead times)
- **Top Partners**: By order count, revenue
- **Partner Growth**: Trend of partner count over time

**Export Data**:
- **Export to CSV**: Button for each report (orders, users, revenue)
- Generates CSV file with filtered data, downloads immediately

### FR-75: System Logs
Admins shall view system logs:

**Log Categories**:
- **User Actions**: Logins, registrations, profile updates
- **Order Events**: Order placement, status changes, cancellations
- **Partner Actions**: Order acceptance/rejection, shipments
- **Admin Actions**: User deactivations, order reassignments, commission changes
- **Errors**: Payment failures, API errors, system exceptions
- **Payment Transactions**: All payment attempts, successes, failures

**Log Viewer**:
- **Table Columns**: Timestamp, Category, User/Actor, Action, Details, Severity (Info, Warning, Error)
- **Filters**: Category, Severity, Date Range, User/Partner ID
- **Search**: By keyword in action/details
- **Sort**: By timestamp (newest first)
- **Pagination**: 100 logs per page
- **View Details**: Click log entry to see full JSON payload

**Log Export**:
- Export filtered logs to CSV for auditing

### FR-76: Platform Settings
Admins shall configure platform-wide settings:

**General Settings**:
- **Platform Name**: BrandKit (editable)
- **Support Email**: support@brandkit.com
- **Support Phone**: +91-XXXXXXXXXX
- **GST Rate**: 18% (editable)
- **Currency**: INR (read-only for MVP, multi-currency Phase 2)

**Delivery Settings**:
- **Standard Delivery Charge**: ₹100
- **Free Delivery Threshold**: ₹10,000 (free standard delivery if order > this amount)
- **Express Delivery Charge**: ₹300
- **Default Lead Time**: 7 days (used if product-specific lead time not set)

**Payment Gateway Configuration**:
- **Gateway**: Razorpay / PayU (radio select)
- **API Key**: Text field (masked)
- **API Secret**: Text field (masked)
- **Test Mode**: Toggle (enable for testing)
- **Payment Timeout**: 15 minutes

**Email/SMS Configuration**:
- **Email Provider**: SendGrid / AWS SES (dropdown)
- **SMTP Settings**: Host, Port, Username, Password (masked)
- **SMS Provider**: Twilio / MSG91 (dropdown)
- **SMS API Key**: Text field (masked)
- **Default Sender Email**: noreply@brandkit.com
- **Default Sender Name**: BrandKit Team

**Save Settings**: Button, validates and updates configuration

**Notification Templates** (Basic):
- **Template List**: Order Confirmation, Order Shipped, Partner New Order, etc.
- **Edit Template**: Click to edit email subject and body (HTML editor)
- **Variables**: {orderID}, {clientName}, {amount}, etc. auto-replaced
- **Preview**: Button to send test email/SMS

### FR-77: Admin Notifications
Admins shall receive alerts for critical events:

**Notification Bell Icon** (Top right header):
- Badge with count of unread notifications
- Click to open dropdown panel

**Notification Types**:
- New partner order rejection (action required: reassign)
- Failed settlement (action required: retry or resolve)
- Low-performing partner (fulfillment rate <85%)
- High-value order placed (>₹50,000, FYI)
- System error (payment gateway down, API failure)
- New user registration (if flagged suspicious)

**Notification Panel**:
- List of notifications (last 20)
- Each notification shows: Icon, Title, Time, "View Details" link
- Mark as read / Mark all as read buttons
- Click "View Details" navigates to relevant page (order, partner, settings)

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Admin Onboards New Partner
1. Admin receives request to onboard partner "Gujarat Jute Co."
2. Admin logs into Admin Panel
3. Admin navigates to Partners > Add Partner
4. System displays partner onboarding form
5. Admin fills all fields:
   - Business Name: Gujarat Jute Co.
   - Owner: Ramesh Patel
   - Email: ramesh@gujaratjute.com
   - Phone: +91-9876543210
   - Address: Industrial Area, Surat, Gujarat
   - GSTIN: 24ABCDE1234F1Z5
   - Categories: Bags, Diaries
   - Bank: HDFC Bank, Account: 12345678901234, IFSC: HDFC0001234
   - Commission: 12% (default)
   - Max Orders: 30
6. Admin clicks "Generate Password"
7. System auto-generates secure password: "Xyz@1234Abc"
8. Admin clicks "Onboard Partner"
9. System:
   - Validates all fields (email unique, GSTIN format correct)
   - Creates partner account (role: Partner, status: Active, bank verification: Pending)
   - Hashes password
   - Sends welcome email to ramesh@gujaratjute.com with login credentials
10. System displays success: "Partner onboarded successfully! Login credentials sent to ramesh@gujaratjute.com"
11. Admin navigates to Partner List, sees "Gujarat Jute Co." with status "Active", bank verification "Pending"
12. Admin performs test transaction to partner's bank account (₹1)
13. Admin confirms ₹1 received by partner
14. Admin clicks "Verify Bank Details" on partner detail page
15. System updates bank verification status: "Verified"
16. Partner receives email: "Bank details verified, you can now receive settlements"

**Edge Cases**:
- If email already exists: Display error "Email already registered", admin must use different email
- If GSTIN format invalid: Display error "Invalid GSTIN format"
- If bank account verification fails: Admin can edit bank details and retry
- If partner doesn't receive welcome email: Admin can manually send via "Send Email" button

### Workflow 2: Admin Manages Problematic Order
1. Partner "PrintMaster Gujarat" rejects order BK-20260123-002 (reason: "Insufficient capacity")
2. System updates order status: "partner_rejected"
3. System sends alert to admin: "Order BK-20260123-002 rejected by partner, reassignment required"
4. Admin logs into Admin Panel
5. Dashboard shows alert badge: "1 Order Requiring Action"
6. Admin clicks alert
7. System navigates to Order Detail page (BK-20260123-002)
8. Admin reviews:
   - Client: Priya Sharma (priya@company.com)
   - Product: 100 T-Shirts
   - Rejected by: PrintMaster Gujarat (reason: Insufficient capacity)
   - Order Value: ₹23,000
9. Admin decides to reassign to alternate partner
10. Admin clicks "Reassign Partner"
11. System displays partner selector dropdown (active partners in "T-Shirts" category)
12. Admin selects "TextilePro Ahmedabad" (high fulfillment rate, available capacity)
13. Admin clicks "Reassign"
14. System:
    - Updates order: Partner = TextilePro Ahmedabad, status = "pending_acceptance"
    - Sends notification to TextilePro Ahmedabad (email, SMS)
    - Logs reassignment: "Admin reassigned from PrintMaster to TextilePro"
15. System displays success: "Order reassigned to TextilePro Ahmedabad"
16. Admin adds internal note: "Reassigned due to PrintMaster capacity issue"
17. TextilePro Ahmedabad receives notification
18. Partner logs in, accepts order within 2 hours
19. System updates order status: "partner_accepted"
20. Client (Priya Sharma) receives email: "Your order is accepted and in production" (client unaware of reassignment)
21. Admin monitors order progress on dashboard

**Edge Cases**:
- If no alternate partner available: Admin manually contacts partner network, adds note "Searching for partner", client notified of delay
- If reassigned partner also rejects: Admin reassigns again or cancels order with refund
- If client requests cancellation during reassignment: Admin prioritizes cancellation, refunds immediately

### Workflow 3: Admin Configures Commission Tier
1. BrandKit decides to incentivize large orders with higher commission
2. Admin logs into Admin Panel
3. Admin navigates to Settings > Commission Configuration
4. Current setting: Flat 12% commission
5. Admin decides to implement tiered structure:
   - Small orders (<₹10k): 10% commission (encourage partners to accept small orders)
   - Medium orders (₹10k-50k): 12% commission (standard)
   - Large orders (>₹50k): 15% commission (platform earns more on high-value orders)
6. Admin clicks "Add Tier" (3 times)
7. Admin fills tier table:
   - Row 1: Min: ₹0, Max: ₹10,000, Commission: 10%
   - Row 2: Min: ₹10,001, Max: ₹50,000, Commission: 12%
   - Row 3: Min: ₹50,001, Max: ₹99,99,999, Commission: 15%
8. System validates: Tiers don't overlap, no gaps
9. Admin clicks "Save Changes"
10. System updates commission configuration globally
11. System displays success: "Commission tiers updated successfully. Changes apply to all new orders."
12. Next order placed: Order value ₹45,000
13. System calculates commission: 12% (falls in ₹10k-50k tier) = ₹5,400
14. Partner receives order notification showing: Your Earnings: ₹39,600 (₹45,000 - ₹5,400)
15. Admin views Order Detail page, sees commission breakdown:
    - Product Amount: ₹45,000
    - Commission Rule: "Tiered (12% for ₹10k-50k)"
    - Platform Commission: ₹5,400
    - Partner Earnings: ₹39,600

**Edge Cases**:
- If admin creates overlapping tiers: System displays error "Tiers cannot overlap"
- If admin leaves gap (e.g., ₹10k to ₹15k not covered): System displays error "All order values must be covered"
- If admin wants partner-specific rate: Admin uses "Partner-Specific Commission" section to override for individual partners

### Workflow 4: Admin Reviews and Approves Settlement
1. Monthly settlement date (March 5, 2026)
2. System auto-generates settlements for all partners with delivered orders in February
3. Settlement for "Gujarat Jute Co.":
   - 18 delivered orders
   - Total product amount: ₹3,20,000
   - Platform commission (12%): ₹38,400
   - Partner earnings: ₹2,81,600
4. System creates settlement record (status: "Pending")
5. Admin logs into Admin Panel
6. Dashboard shows alert: "5 Settlements Pending Approval"
7. Admin navigates to Settlements
8. System displays Settlement List with 5 pending settlements
9. Admin clicks settlement ID for "Gujarat Jute Co."
10. System displays Settlement Detail page:
    - Partner: Gujarat Jute Co., Bank: HDFC XXXX1234 (Verified)
    - Period: February 2026
    - Amount: ₹2,81,600
    - Order count: 18
    - Order breakdown table (18 rows with order IDs, amounts, commissions)
11. Admin reviews order breakdown, confirms accuracy
12. Admin verifies bank details are verified
13. Admin clicks "Approve Settlement"
14. System displays confirmation modal: "Approve payout of ₹2,81,600 to Gujarat Jute Co.?"
15. Admin clicks "Confirm"
16. System:
    - Updates settlement status: "Processing"
    - Calls Razorpay X API to initiate payout
    - Logs admin approval action
17. System displays: "Settlement approved. Payout initiated, will complete in 1-2 business days."
18. Razorpay X processes payout (1 day)
19. March 6: Razorpay X webhook confirms success
20. System:
    - Updates settlement status: "Completed"
    - Generates settlement statement PDF
    - Sends email to partner with statement
21. Partner receives ₹2,81,600 in bank account
22. Admin sees settlement status updated to "Completed" on dashboard

**Edge Cases**:
- If bank details unverified: Admin cannot approve, must verify bank first
- If payout fails (invalid account): Razorpay X webhook notifies failure, system updates status "Failed", admin receives alert
- If admin rejects settlement (discrepancy found): Admin clicks "Reject", enters reason, system recalculates, partner notified to review
- If partner disputes settlement amount: Partner contacts support, admin reviews, manually adjusts if needed, re-approves

### Workflow 5: Admin Monitors Platform via Analytics
1. Admin logs in on Monday morning
2. Admin navigates to Dashboard
3. Admin reviews weekly metrics:
   - Revenue (Last 7 days): ₹1,85,000 (+12% vs. previous week)
   - Orders: 42 (+8%)
   - New Users: 15 clients, 2 partners
4. Admin clicks "Analytics" menu
5. System displays comprehensive analytics dashboard
6. Admin reviews Revenue Analytics:
   - Revenue Trend (Last 30 days): Steady growth with spike on Feb 25 (₹45k single order)
   - Revenue by Category: T-Shirts (₹80k, 40%), Bags (₹60k, 30%), Bottles (₹30k, 15%), Others (₹15k, 15%)
7. Admin notes: T-Shirts most popular, consider expanding T-shirt partner network
8. Admin reviews Order Analytics:
   - Orders by Status: Delivered 65%, Shipped 20%, In Production 10%, Confirmed 5%
   - Top Products: "Branded T-Shirt" (28 orders), "Water Bottle Steel" (18 orders)
9. Admin reviews Partner Analytics:
   - Top Partners: PrintMaster Gujarat (15 orders, 95% fulfillment), Gujarat Jute Co. (12 orders, 92%)
   - Low-performing: TextilePro Ahmedabad (5 orders, 80% fulfillment, avg lead time 10 days)
10. Admin identifies issue with TextilePro Ahmedabad
11. Admin navigates to Partner Detail page for TextilePro
12. Admin sees: Fulfillment rate dropped last week, 1 order rejected, 1 order delayed
13. Admin sends email to partner: "We noticed performance dip, please improve or risk reduced order assignments"
14. Admin adds note to partner profile: "Under review for performance"
15. Admin returns to Analytics, exports "Orders by Partner" report to CSV for deeper analysis
16. Admin shares insights with BrandKit team in weekly meeting

**Edge Cases**:
- If analytics data not loading (database timeout): Display "Unable to load analytics, try again", admin checks system logs
- If major metrics drop: Admin investigates via order logs, identifies root cause (payment gateway issue, marketing campaign ended)

---

## 7. INPUT & OUTPUT

### Inputs

#### Partner Onboarding Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Business Name | Text | 2-200 chars | Yes |
| Owner Name | Text | 2-100 chars | Yes |
| Email | Email | Valid email, unique | Yes |
| Phone | Tel | +91-XXXXXXXXXX | Yes |
| Business Address | Textarea | 10-500 chars | Yes |
| GSTIN | Text | 15 chars, GST format | Yes |
| Categories | Multi-select | At least 1 category | Yes |
| Bank Account Holder | Text | 2-100 chars | Yes |
| Bank Name | Text | 2-100 chars | Yes |
| Account Number | Text | 8-18 digits | Yes |
| IFSC Code | Text | 11 chars, IFSC format | Yes |
| Commission % | Number | 1-50% | Yes |
| Max Concurrent Orders | Number | 1-1000 | Yes |

#### Commission Configuration
| Field | Type | Validation |
|-------|------|------------|
| Default Commission | Number | 1-50% |
| Tier Min Value | Number | ≥0, INR |
| Tier Max Value | Number | >Min Value, INR |
| Tier Commission | Number | 1-50% |

#### Order Reassignment
| Field | Type | Validation |
|-------|------|------------|
| New Partner | Dropdown | Active partner in product category |

#### Settlement Approval
| Action | Validation |
|--------|------------|
| Approve | Bank details must be verified, amount >₹0 |

### Outputs

#### Admin Dashboard Summary Response
```json
{
  "status": "success",
  "data": {
    "metrics": {
      "totalRevenueThisMonth": 520000,
      "revenueChangePercent": 15,
      "totalOrdersThisMonth": 142,
      "ordersChangePercent": 8,
      "activeUsers": 285,
      "activePartners": 12
    },
    "charts": {
      "revenueTrend": [...],
      "ordersTrend": [...],
      "orderStatusDistribution": {...}
    },
    "recentActivity": {
      "orders": [...],
      "registrations": [...],
      "partnerActions": [...]
    },
    "alerts": [
      {
        "type": "partner_rejection",
        "message": "Order BK-20260123-002 rejected, reassignment required",
        "link": "/admin/orders/BK-20260123-002"
      }
    ]
  }
}
```

#### Analytics Revenue Report Response
```json
{
  "status": "success",
  "data": {
    "period": "Last 30 days",
    "totalRevenue": 1850000,
    "platformCommission": 222000,
    "revenueTrend": [
      { "date": "2026-02-01", "revenue": 45000 },
      { "date": "2026-02-02", "revenue": 52000 },
      ...
    ],
    "revenueByCategory": {
      "T-Shirts": 800000,
      "Bags": 600000,
      "Bottles": 300000,
      "Others": 150000
    },
    "averageOrderValue": 13028,
    "topProducts": [
      { "productName": "Branded T-Shirt", "orderCount": 28, "revenue": 420000 },
      { "productName": "Water Bottle Steel", "orderCount": 18, "revenue": 270000 }
    ]
  }
}
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-51: Admin Role Hierarchy
Super Admins can create and manage all admin accounts. Operations Admins can manage users, partners, orders but cannot access system settings or commission configuration.

### BR-52: Partner Bank Verification Requirement
Partners cannot receive settlements until bank details are verified by admin. Verification is manual for MVP (test transaction or document check).

### BR-53: Order Reassignment Limitation
Orders can only be reassigned before partner acceptance. After acceptance, cancellation with refund is required instead of reassignment.

### BR-54: Commission Configuration Impact
Commission changes apply to all new orders placed after the change. Existing orders retain the commission rate at time of order placement.

### BR-55: Settlement Approval Requirement
All settlements require admin approval before payout (for MVP). Automated approval planned for Phase 2 with fraud detection rules.

### BR-56: Soft Delete for Products
Deleted products are soft-deleted (status: "Deleted") to preserve historical order data. They cannot be reactivated; admins must create new product if needed.

### BR-57: User Deactivation Effect
Deactivated users cannot log in, and all active sessions are immediately invalidated. Orders placed before deactivation remain accessible for fulfillment.

### BR-58: Log Retention
System logs are retained for 1 year for auditing and compliance. After 1 year, logs are archived or deleted based on regulatory requirements.

### BR-59: Analytics Data Freshness
Analytics data is updated in near real-time (within 5 minutes of event occurrence) for accurate decision-making.

### BR-60: Notification Priority
Critical alerts (order rejections, payment failures, system errors) are pushed to admin immediately. Informational notifications (new registrations) are batched and displayed in notification panel.

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | Admin Action |
|----------|-----------|-------------|---------|--------------|
| Duplicate partner email | ADM_001 | 409 | "Email already exists" | Use different email or update existing partner |
| Invalid GSTIN format | ADM_002 | 400 | "Invalid GSTIN format" | Correct GSTIN |
| Bank verification failed | ADM_003 | 400 | "Bank account verification failed" | Check account details, retry |
| Cannot reassign accepted order | ADM_004 | 403 | "Order already accepted by partner. Cancel order instead." | Use cancellation workflow |
| Settlement payout failed | ADM_005 | 500 | "Payout failed. Check partner bank details and retry." | Review bank details, contact partner, retry |
| Commission tier overlap | ADM_006 | 400 | "Commission tiers cannot overlap" | Adjust tier ranges |
| Analytics data unavailable | ADM_007 | 500 | "Unable to load analytics. Try again later." | Refresh page, check logs |
| User not found | ADM_008 | 404 | "User not found" | Check user ID or search again |
| Insufficient permissions | ADM_009 | 403 | "You don't have permission to access this feature" | Contact Super Admin for role upgrade |
| Log export failed | ADM_010 | 500 | "Unable to export logs. Try again." | Retry export or reduce date range |

### Error Handling Strategy
- Display clear error messages with actionable next steps
- Log all admin actions for audit trail (who, what, when, why)
- For critical errors (settlement failures), send email alert to Super Admin
- Provide retry mechanisms for transient errors
- Never expose sensitive data in error messages (bank details, passwords)

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-116**: Admin dashboard must load within 2 seconds
- **NFR-117**: User/Partner list must load within 1.5 seconds (50 records)
- **NFR-118**: Order list must load within 2 seconds (50 records)
- **NFR-119**: Analytics charts must render within 3 seconds
- **NFR-120**: CSV exports must complete within 10 seconds (up to 10,000 records)

### Scalability
- **NFR-121**: Admin panel must support 10+ concurrent admin users
- **NFR-122**: User/Partner lists must efficiently paginate 100,000+ records
- **NFR-123**: Analytics must aggregate data from 1 million+ transactions

### Security
- **NFR-124**: All admin actions logged with user ID, timestamp, action details
- **NFR-125**: Sensitive fields (bank details, API keys) masked in UI, encrypted in database
- **NFR-126**: Admin sessions expire after 30 minutes of inactivity
- **NFR-127**: Admin role changes logged and require Super Admin approval
- **NFR-128**: Payment gateway credentials stored in environment variables, never in database

### Auditability
- **NFR-129**: All admin actions recorded in audit log (immutable)
- **NFR-130**: Audit logs include: Admin user ID, action type, entity affected, old value, new value, timestamp
- **NFR-131**: Audit logs retained for 3 years per compliance requirements

### Usability
- **NFR-132**: Admin panel must be intuitive for non-technical operations staff
- **NFR-133**: Critical actions (deactivate user, cancel order, approve settlement) require confirmation modals
- **NFR-134**: Help tooltips provided for complex features (commission tiers, partner onboarding)

---

## 11. ACCEPTANCE CRITERIA

### AC-51: Admin Onboards New Partner
**Given** an admin logged into Admin Panel  
**When** the admin navigates to Partners > Add Partner  
**And** fills all required fields with valid data  
**And** clicks "Onboard Partner"  
**Then** the system creates partner account with status "Active"  
**And** sends welcome email with login credentials to partner  
**And** displays success "Partner onboarded successfully"  
**And** the partner appears in Partner List

### AC-52: Admin Verifies Partner Bank Details
**Given** partner "Gujarat Jute Co." with bank verification status "Pending"  
**When** admin performs test transaction and confirms success  
**And** admin clicks "Verify Bank Details" on partner detail page  
**Then** the system updates verification status to "Verified"  
**And** sends email to partner "Bank details verified, settlements enabled"  
**And** the partner can now receive settlement payouts

### AC-53: Admin Reassigns Rejected Order
**Given** order BK-20260123-002 rejected by partner (status: "partner_rejected")  
**When** admin navigates to order detail page  
**And** clicks "Reassign Partner"  
**And** selects alternate partner "TextilePro Ahmedabad"  
**And** clicks "Reassign"  
**Then** the system updates partner assignment  
**And** sends notification to new partner  
**And** logs reassignment action  
**And** displays success "Order reassigned to TextilePro Ahmedabad"  
**And** order status changes to "pending_acceptance"

### AC-54: Admin Configures Tiered Commission
**Given** admin on Commission Configuration page  
**When** admin adds 3 commission tiers:
- ₹0-₹10k: 10%
- ₹10k-₹50k: 12%
- ₹50k+: 15%
**And** clicks "Save Changes"  
**Then** the system validates tiers (no overlap, no gaps)  
**And** updates commission configuration  
**And** displays success "Commission tiers updated"  
**And** all new orders calculate commission based on tiers

### AC-55: Admin Approves Settlement
**Given** settlement for "Gujarat Jute Co." (amount: ₹2,81,600, status: "Pending")  
**When** admin reviews settlement details  
**And** confirms bank verification status is "Verified"  
**And** clicks "Approve Settlement"  
**Then** the system displays confirmation modal  
**When** admin confirms  
**Then** the system initiates payout via Razorpay X  
**And** updates settlement status to "Processing"  
**And** logs admin approval  
**When** payout completes (1-2 days)  
**Then** system updates status to "Completed"  
**And** partner receives email with settlement statement

### AC-56: Admin Views Analytics Dashboard
**Given** admin navigates to Analytics  
**Then** the system displays:
- Revenue Analytics: Total revenue, trend graph, revenue by category
- Order Analytics: Orders by status, top products
- User Analytics: Total users, user acquisition trend, top clients
- Partner Analytics: Partner performance table, top partners
**When** admin clicks "Export to CSV" on Revenue Analytics  
**Then** system generates CSV file with revenue data  
**And** downloads immediately to admin's device

### AC-57: Admin Deactivates Problematic User
**Given** admin views user "John Doe" (status: "Active")  
**When** admin clicks "Deactivate" on user detail page  
**Then** the system displays confirmation modal "Are you sure you want to deactivate John Doe?"  
**When** admin confirms  
**Then** the system:
- Updates user status to "Inactive"
- Invalidates all active sessions for that user
- Logs deactivation action
- Displays success "User deactivated successfully"
**And** the user cannot log in  
**And** attempting login shows "Account deactivated. Contact support."

### AC-58: Admin Adds New Product
**Given** admin navigates to Products > Add Product  
**When** admin fills product form:
- Name: "Premium Jute Bag"
- Category: Bags
- Description, Price, Material, etc.
- Uploads 5 images
- Configures print area (visual editor)
- Selects partner "Gujarat Jute Co."
- Adds 3 pricing tiers
**And** clicks "Publish"  
**Then** the system validates all fields  
**And** creates product record with status "Active"  
**And** displays success "Product published successfully"  
**And** product is immediately visible to clients in catalog

### AC-59: Admin Cancels Order with Refund
**Given** order BK-20260123-003 (status: "Confirmed", payment: "Success")  
**When** admin clicks "Cancel Order" on order detail page  
**And** enters reason "Client request"  
**And** confirms cancellation  
**Then** the system:
- Updates order status to "Cancelled"
- Initiates refund via payment gateway
- Sends email to client "Order cancelled, refund initiated"
- Sends email to partner "Order cancelled, no action required"
- Logs cancellation with reason
- Displays "Order cancelled, refund will be processed in 3-5 days"

### AC-60: Admin Receives Critical Alert
**Given** partner rejects order BK-20260123-004  
**When** system updates order status to "partner_rejected"  
**Then** system sends alert to admin:
- Notification bell badge count increases
- Alert in notification panel: "Order BK-20260123-004 rejected, reassignment required"
- Email to admin (if configured for critical alerts)
**When** admin clicks notification  
**Then** system navigates to order detail page for BK-20260123-004

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Payment Gateway**: Razorpay X or Cashfree Payouts for settlement payouts
- **Email Service**: SendGrid or AWS SES for admin notifications, partner onboarding emails
- **Analytics**: Google Analytics or Mixpanel for user behavior tracking (optional)
- **Cloud Storage**: AWS S3 for settlement statements, exported CSVs

### Internal Dependencies
- **FRD-001 (Authentication)**: Admin login and role-based access
- **FRD-002 (Product Catalog)**: Product management (add, edit, delete products)
- **FRD-004 (Order Management)**: Order viewing, reassignment, cancellation
- **FRD-005 (Partner Dashboard)**: Partner onboarding, settlement processing

### Database Tables (Supabase PostgreSQL)
- `admins`: Admin user accounts and roles (relational table with role FK)
- `audit_logs`: All admin actions for compliance (indexed by timestamp, admin_id, action_type)
- `system_logs`: System events, errors, API calls (time-series optimized, consider partitioning)
- `commission_config`: Commission tiers and partner-specific rates (normalized structure)
- `settlements`: Settlement records (referenced from FRD-005, foreign key to partners table)
- `notifications`: Admin alert/notification records
- `partner_discounts`: Partner-defined discounts (NEW - Rule 2, referenced in FR-72b)
- `discount_audit_log`: Discount change history (NEW - Rule 2, for compliance and abuse detection)
- **Row Level Security (RLS)**: Admin role has full access. Other roles have NO access to admin-specific tables.
- **Indexes**: B-tree indexes on foreign keys, timestamps, status columns for efficient filtering/sorting
- **Performance**: Use PostgreSQL views for complex analytics queries, materialized views for dashboard metrics

### API Endpoints (Admin-Only - Requires "ADMIN" or "SUPER_ADMIN" Role)
- `GET /api/admin/dashboard`: Dashboard summary
- `GET /api/admin/users`: User list
- `GET /api/admin/users/:userId`: User details
- `PUT /api/admin/users/:userId`: Update user
- `POST /api/admin/users/:userId/deactivate`: Deactivate user
- `GET /api/admin/partners`: Partner list (internal partner details visible)
- `POST /api/admin/partners`: Onboard partner (create partner account)
- `PUT /api/admin/partners/:partnerId`: Update partner details
- `POST /api/admin/partners/:partnerId/verify-bank`: Verify bank details
- `GET /api/admin/products`: Product list
- `POST /api/admin/products`: Add product (associate with partner internally)
- `PUT /api/admin/products/:productId`: Edit product
- `DELETE /api/admin/products/:productId`: Delete product (soft delete)
- `GET /api/admin/orders`: Order list (shows partner assignments)
- `GET /api/admin/orders/:orderId`: Order details (includes partner info)
- `PUT /api/admin/orders/:orderId/reassign`: Reassign partner
- `PUT /api/admin/orders/:orderId/cancel`: Cancel order
- `GET /api/admin/commissions`: Get commission config
- **NEW - Discount Management (Rule 2)**:
  - `GET /api/admin/discounts`: List all partner discounts (with filters)
  - `PUT /api/admin/discounts/:discountId/approve`: Approve pending discount
  - `PUT /api/admin/discounts/:discountId/reject`: Reject discount with reason
  - `PUT /api/admin/discounts/:discountId/disable`: Disable active discount
  - `PUT /api/admin/discounts/:discountId/enable`: Enable disabled discount
  - `POST /api/admin/discounts/limits`: Set global or partner-specific discount limits
  - `GET /api/admin/discounts/audit`: Get discount audit log (compliance)
  - `POST /api/admin/partners/:partnerId/suspend-discounts`: Suspend partner's discount privileges
  - `GET /api/admin/discounts/analytics`: Discount impact analytics
- **Security**: All endpoints validate JWT with "ADMIN" or "SUPER_ADMIN" role. Unauthorized access returns 403 Forbidden.
- `PUT /api/admin/commissions`: Update commission config
- `GET /api/admin/settlements`: Settlement list
- `POST /api/admin/settlements/:settlementId/approve`: Approve settlement
- `GET /api/admin/analytics/revenue`: Revenue analytics
- `GET /api/admin/analytics/orders`: Order analytics
- `GET /api/admin/analytics/users`: User analytics
- `GET /api/admin/analytics/partners`: Partner analytics
- `GET /api/admin/logs`: System logs
- `GET /api/admin/settings`: Get platform settings
- `PUT /api/admin/settings`: Update platform settings

---

## 13. ASSUMPTIONS

1. Admins are trained on platform operations before access
2. Super Admin role reserved for 1-2 trusted personnel (founders, CTO)
3. Operations Admins handle day-to-day tasks (order issues, user support)
4. Partner bank account verification is manual for MVP (test transaction method)
5. Analytics data refresh every 5 minutes is acceptable (not real-time)
6. Admin panel accessed from desktop/laptop (mobile-responsive but desktop-optimized)
7. BrandKit has legal agreements with partners covering commission terms
8. Platform commission structure is transparent to partners before onboarding
9. Settlement disputes are rare and handled manually via support
10. System logs and audit trails meet India's data retention compliance

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Automated settlement approval with fraud detection rules (reduce manual approval overhead)
- Advanced partner onboarding: KYC verification via DigiLocker API, video verification
- Bulk operations: Bulk user deactivation, bulk product updates via CSV import
- Advanced analytics: Predictive revenue forecasting, anomaly detection
- A/B testing platform: Test pricing tiers, commission rates
- Integrated customer support ticketing system
- Role-based custom dashboards (Operations Admin sees different metrics than Super Admin)
- Partner performance auto-scoring algorithm (flag low performers automatically)
- Mobile app for admin (iOS/Android) for on-the-go management
- Webhook management UI (configure external webhooks for integrations)
- API access management (issue API keys to third-party integrations)
- Content management system (CMS) for marketing pages, FAQs
- Multi-language admin panel (if expanding beyond English-speaking admins)
- Automated financial reconciliation (match settlements with bank statements)
- Tax report generation (GST returns, TDS certificates)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Admin panel UI/UX design, role-based access control implementation, audit log database schema, analytics dashboard wireframes
