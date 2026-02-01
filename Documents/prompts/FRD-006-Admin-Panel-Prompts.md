# FRD-006: Admin Panel - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the Admin Panel for Platform Management and Operations with testing instructions after each prompt.

---

## Sub-Prompt 1: Admin Role-Based Access Control
**Task**: Implement admin authentication with role hierarchy (Super Admin, Operations Admin).

**Implementation Details**:
- Roles: Super Admin (full access), Operations Admin (limited access)
- Super Admin: Can create/manage other admins, access all features
- Operations Admin: User/Partner/Order management, NO access to system settings or commission config
- Middleware: Check admin role for each protected route
- Admin creation: `POST /api/admin/admins` (Super Admin only)
- Access control: @PreAuthorize annotations or Spring Security filters

**Testing Instructions**:
1. Login as Super Admin
   - Expected: Access to all admin features
2. Try accessing commission settings as Operations Admin
   - Expected: 403 Forbidden
3. Super Admin creates new Operations Admin
   - Expected: New admin account created with limited permissions
4. Operations Admin tries creating another admin
   - Expected: 403 Forbidden (only Super Admin can)
5. Test route protection: Access /admin/settings without admin role
   - Expected: 401 Unauthorized
6. Verify JWT claims: {userId, role: "SUPER_ADMIN" or "OPERATIONS_ADMIN"}
7. Admin logs out
   - Expected: Session invalidated, cannot access admin panel

---

## Sub-Prompt 2: Admin Dashboard Home
**Task**: Create comprehensive admin dashboard with key metrics and alerts.

**Implementation Details**:
- Metrics cards: Total Revenue (this month), Total Orders, Active Users, Active Partners
- Charts: Revenue Trend (30 days), Orders Trend, Order Status Distribution (pie chart)
- Recent activity: Last 10 orders, last 5 user registrations, last 5 partner actions
- Alerts: Pending partner orders, rejected orders, failed settlements, low-performing partners
- Quick actions: Add Product, Onboard Partner, View All Orders, Process Settlements

**Testing Instructions**:
1. Login as admin, view dashboard
   - Expected: All metrics displayed with correct values
2. Verify revenue metric: ₹5,20,000 (this month)
   - Database check: Sum of all delivered orders in current month
3. Check charts rendering
   - Expected: Revenue trend line graph, order status pie chart
4. View alerts: "3 orders requiring reassignment"
   - Expected: Alert displayed prominently
5. Click "View All Orders"
   - Expected: Navigate to orders list page
6. Test real-time updates: Place new order, refresh dashboard
   - Expected: Order count incremented
7. Performance test: Dashboard loads within 2 seconds

---

## Sub-Prompt 3: User Management Interface
**Task**: Implement user list, search, and account management.

**Implementation Details**:
- User list: Table with columns (User ID, Name, Email, Role, Status, Registration Date, Last Login, Actions)
- Filters: Role (All, Client, Partner, Admin), Status (Active, Inactive), Date Range
- Search: By name, email, user ID
- Sort: By registration date, last login
- Pagination: 50 users per page
- Actions: View Details, Activate/Deactivate, Reset Password, View Orders

**Testing Instructions**:
1. Navigate to Users page
   - Expected: List of all users
2. Filter by role "Client"
   - Expected: Only clients displayed
3. Search by email "rajesh@company.com"
   - Expected: Matching user found
4. View user details
   - Expected: Full user profile, order history, activity summary
5. Deactivate user account
   - Expected: User status "inactive", cannot log in
6. Activate user again
   - Expected: User can log in
7. Send password reset link
   - Expected: Reset email sent to user

---

## Sub-Prompt 4: Partner Management and Onboarding (Internal Only)
**Task**: Implement partner onboarding and management (partner details NEVER exposed to clients).

**Implementation Details**:
- Onboard partner form: Business Name, Owner, Email, Phone, Address, GSTIN, Categories, Bank Details, Commission %, Max Orders
- Partner list: Table with columns (Partner ID, Business Name, Email, Location, Status, Performance, Actions)
- Actions: View Details, Activate/Deactivate, Verify Bank, View Orders, Edit Commission
- Bank verification: Manual process, test transaction or document check
- Partner detail page: Info, performance metrics, recent orders, bank details (masked)

**Testing Instructions**:
1. Admin onboards new partner "Gujarat Jute Co."
   - Expected: Partner account created with role "PARTNER"
2. Partner receives welcome email with credentials
3. Admin verifies bank details (test transaction)
   - Expected: Bank status "Verified"
4. Partner login works
   - Expected: Partner can access partner dashboard
5. Admin views partner list
   - Expected: New partner displayed
6. **CRITICAL**: Verify partner info NOT exposed in client APIs
   - Test: `GET /api/products` - NO partner names in response
7. Admin adjusts commission rate for partner
   - Expected: Commission updated, applied to new orders

---

## Sub-Prompt 5: Product Management with Partner Assignment
**Task**: Implement product CRUD with internal partner association.

**Implementation Details**:
- Add product form: All fields (name, description, price, images, print area, pricing tiers) + Partner selector (dropdown, internal only)
- Image upload: Drag-drop, 3-8 images, upload to S3
- Print area configuration: Visual editor
- Pricing tier editor: Add/remove tiers
- Partner assignment: Dropdown of active partners (INTERNAL, not visible to clients)
- Edit product: Update any field including partner reassignment
- Delete product: Soft delete (status = "deleted")

**Testing Instructions**:
1. Admin adds new product "Premium Jute Bag"
2. Select partner "Gujarat Jute Co." (internal assignment)
   - Expected: Product created, partner_id stored
3. Upload 5 images
   - Expected: Images uploaded to S3
4. Configure print area visually
   - Expected: Coordinates saved
5. Add 3 pricing tiers
   - Expected: Tiers validated, saved
6. Publish product
   - Expected: Product visible to clients (NO partner info shown)
7. Client views product
   - Expected: Product data WITHOUT partner_id or partner name
8. Admin edits product, changes partner
   - Expected: Partner updated internally, still NOT exposed to clients

---

## Sub-Prompt 6: Order Management and Manual Intervention
**Task**: Implement order list, search, reassignment, and cancellation.

**Implementation Details**:
- Order list: Table with columns (Order ID, Client Name, Partner Name (internal only), Products, Total, Status, Date, Actions)
- Filters: Status, Date Range, Partner (internal filter)
- Search: By Order ID, Client Email
- Actions: View Details, Reassign Partner, Update Status, Cancel Order, Download Invoice
- Order detail page: Full order info + admin fields (client email/phone, partner email/phone, commission breakdown, customization data)
- Reassign partner: Dropdown of alternate partners
- Cancel order: Reason field, initiates refund

**Testing Instructions**:
1. Admin views order list
   - Expected: All orders with partner names visible (admin-only view)
2. Filter by partner "PrintMaster Gujarat"
   - Expected: Orders from that partner only
3. View order details
   - Expected: Partner email/phone visible (admin view)
4. Partner rejects order, admin reassigns
   - Expected: Select new partner, order reassigned
5. Client views order
   - Expected: Status updated, NO partner name visible
6. Admin cancels order, enters reason
   - Expected: Refund initiated, client and partner notified
7. Download invoice
   - Expected: Invoice PDF WITHOUT partner details

---

## Sub-Prompt 7: Commission Configuration
**Task**: Implement commission settings with tiered structure.

**Implementation Details**:
- Default commission rate: Single % (e.g., 12%)
- Tiered commission by order value: Table with ranges and %
  - Example: ₹0-₹10k: 10%, ₹10k-₹50k: 12%, ₹50k+: 15%
- Partner-specific commission: Override for individual partners
- Save changes: Applies to future orders only
- Display commission rule on order detail page

**Testing Instructions**:
1. Admin sets default commission: 12%
   - Expected: All partners default to 12%
2. Create tiered structure (3 tiers)
   - Expected: Tiers saved, validated (no overlaps, no gaps)
3. Try creating overlapping tiers
   - Expected: Error "Tiers cannot overlap"
4. Set partner-specific rate: Gujarat Jute Co. = 10%
   - Expected: Override saved
5. Place order (₹45,000)
   - Expected: Tier 2 commission (12%) applied
6. View order details
   - Expected: Commission rule displayed "Tiered (12% for ₹10k-50k)"
7. Change commission settings, verify old orders unchanged
   - Expected: Old orders retain original commission rate

---

## Sub-Prompt 8: Partner Discount Management and Control (Rule 2)
**Task**: Implement comprehensive discount oversight for admin.

**Implementation Details**:
- Discount dashboard: Summary cards (Total Active Discounts, Pending Approvals, Avg Discount %), List of all partner discounts
- Filters: Status (Active/Disabled/Pending/Rejected), Partner, Product Category
- Actions: Approve, Reject, Disable, Enable, View Impact
- Set global limits: Min 0%, Max 25%
- Audit log: All discount changes (partner/admin actions, timestamps)
- Abuse detection alerts: Frequent changes, excessive discounts

**Testing Instructions**:
1. Partner proposes 10% discount, admin views in dashboard
   - Expected: Discount status "Pending", visible in admin panel
2. Admin approves discount
   - Expected: Status "Active", discount live on client site
3. Admin disables discount
   - Expected: Status "Disabled", clients see original price
4. Admin sets global limit: Max 20%
   - Expected: Future partner proposals validated against 20%
5. Partner tries 25% discount (exceeds new limit)
   - Expected: Error to partner, admin sees rejected proposal
6. View audit log
   - Expected: All discount actions logged
7. Abuse alert: Partner changes discount 6 times in 1 day
   - Expected: Admin receives alert, can suspend partner's discount privileges

---

## Sub-Prompt 9: Settlement Management and Approval
**Task**: Implement settlement review and payout approval.

**Implementation Details**:
- Settlement dashboard: Summary (Pending Settlements, Completed This Month, Failed), List of settlements
- Filters: Status (Pending/Completed/Failed), Partner, Period
- Actions: View Details, Approve, Retry, Download Statement
- Settlement detail page: Partner info, period, amount, order breakdown
- Approve: Triggers payout via Razorpay X
- Retry: For failed payouts

**Testing Instructions**:
1. Month-end: System generates settlements
   - Expected: Settlements created with status "Pending"
2. Admin views settlement dashboard
   - Expected: 5 pending settlements displayed
3. Click settlement for "Gujarat Jute Co."
   - Expected: Amount ₹2,81,600, 18 orders breakdown
4. Review order breakdown
   - Expected: All orders listed with commission details
5. Approve settlement
   - Expected: Payout initiated via Razorpay X
6. Check partner notification
   - Expected: Email sent with settlement statement
7. Test failed payout: Invalid bank account
   - Expected: Status "Failed", admin alerted, can retry

---

## Sub-Prompt 10: Analytics Dashboard
**Task**: Implement comprehensive business analytics for admins.

**Implementation Details**:
- Revenue analytics: Total revenue, trend graph, revenue by category, commission earned
- Order analytics: Total orders, orders by status (pie chart), top products, order funnel
- User analytics: Total users, acquisition trend, active users, top clients
- Partner analytics: Performance table (fulfillment rate, lead time), top partners
- Export data: Download reports as CSV

**Testing Instructions**:
1. Navigate to Analytics
   - Expected: All charts and metrics displayed
2. Revenue analytics: Total ₹18,50,000
   - Expected: Matches database sum
3. Revenue trend graph: Last 30 days
   - Expected: Daily revenue plotted
4. Orders by status: Pie chart
   - Expected: Delivered 65%, Shipped 20%, etc.
5. Partner analytics: Sort by fulfillment rate
   - Expected: Top performers at top
6. Export revenue report to CSV
   - Expected: CSV file downloaded
7. Performance test: Analytics loads within 3 seconds

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Admin Onboarding**: Super Admin Creates Operations Admin → New Admin Logs In → Access Limited Features
2. **User Management Flow**: Admin Searches User → Deactivates Account → User Cannot Login → Reactivates → User Logs In
3. **Partner Management Flow**: Onboard Partner → Verify Bank → Partner Logs In → Assign to Product (internal)
4. **Product Management Flow**: Add Product → Assign Partner (internal) → Publish → Client Views (NO partner info)
5. **Order Intervention Flow**: Partner Rejects → Admin Reassigns → New Partner Accepts → Client Unaware of Switch
6. **Commission Management Flow**: Set Tiers → Place Orders → Verify Tier Applied → Adjust → New Orders Use New Rate
7. **Discount Control Flow**: Partner Proposes → Admin Approves → Client Sees Discount → Admin Disables → Original Price
8. **Settlement Flow**: Review Settlements → Approve Payouts → Partner Receives Payment → Download Statements
9. **Analytics Flow**: View Metrics → Export CSV → Share with Team

---

## Security and Audit Validation Checklist

- [ ] Admin panel accessible ONLY to admin roles (Super Admin, Operations Admin)
- [ ] Role-based access enforced (Super Admin > Operations Admin)
- [ ] Partner information in admin panel NEVER exposed via client APIs
- [ ] All admin actions logged with user ID, timestamp, action details
- [ ] Sensitive fields (bank details, API keys) masked in UI, encrypted in database
- [ ] Admin sessions expire after 30 minutes of inactivity
- [ ] Partner discount approvals/rejections logged for compliance
- [ ] Settlement approvals logged and audit-ready
- [ ] Commission changes apply only to future orders (history preserved)
- [ ] Analytics data aggregated from secure database views

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 5-6 weeks (including testing)
