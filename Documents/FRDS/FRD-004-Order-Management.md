# FRD-004: Order Placement and Management

---

## 1. FRD METADATA

**FRD Title**: Order Placement, Cart, Checkout, and Order Tracking System  
**FRD ID**: FRD-004  
**Related PRD Section(s)**: 4.2.1 Core Features - Order Placement and Management  
**Priority**: High  
**Owner**: Product / Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Develop a comprehensive order management system that enables clients to add customized products to cart, complete checkout with address and delivery options, process payments, and track orders from placement through fulfillment, while automatically routing orders to appropriate fulfillment partners (internal process, partner details not exposed to clients).

### Business Value
- Enable seamless B2B transaction completion
- Automate order routing to fulfillment partners based on location, availability, and pricing (internal process)
- Provide transparency through order tracking (without exposing partner details)
- Generate GST-compliant invoices for corporate clients
- Foundation for recurring revenue through repeat orders
- Support platform revenue through commission tracking

---

## 3. SCOPE

### In Scope
- Shopping cart system with add/remove/update quantity
- Cart persistence (logged-in users)
- Checkout flow: Address entry, delivery options, order review
- Delivery partner integration (e.g., Delhivery for pan-India, local couriers for Gujarat)
- Payment processing: UPI, Credit/Debit Cards, Net Banking via Razorpay/PayU
- GST-compliant invoice generation (PDF)
- Order confirmation email with invoice attachment
- Automatic partner order routing (internal process) based on:
  - Product-partner association (internal database)
  - Partner location proximity to delivery address
  - Partner availability/capacity
  - Competitive pricing
  - **Partner details NEVER exposed to clients** - all routing internal
- Order status tracking (Client view): Placed, Confirmed, In Production, Shipped, Delivered (no partner information shown)
- Order management dashboard (Client): View all orders, reorder, download invoices (partner details hidden)
- Order notifications: Email and SMS for status updates (no partner details)
- Support for both single products and bundles in cart
- Display applied discounts (partner-defined, platform-managed)
- No MOQ (Minimum Order Quantity) enforcement per PRD

### Out of Scope
- International shipping (India only for MVP)
- Cash on Delivery (COD) payment option
- Buy Now Pay Later (BNPL) integrations (Phase 2)
- Order cancellation by client after partner confirmation (manual process via support)
- Partial order fulfillment (all items must ship together for MVP)
- Real-time inventory sync with partners (partner confirms availability after order)
- Order splitting across multiple partners (single partner per order for MVP)
- Bulk order discounts negotiation tool (pricing/discounts are partner-defined with platform limits)
- Integration with client ERP/procurement systems (Phase 2)
- Partner/seller information display anywhere in client-facing systems

---

## 4. USER STORIES

### Client Users
- **US-035**: As a Corporate Procurement Manager, I want to add multiple customized items to my cart so that I can review my order before purchasing
- **US-036**: As an Event Organizer, I want to enter my delivery address so that products are shipped to my event venue
- **US-037**: As a Client, I want to choose delivery speed (standard/express) so that I can meet my deadline
- **US-038**: As a Client, I want to pay securely via UPI or card so that I can complete my purchase quickly
- **US-039**: As a Client, I want to receive a GST-compliant invoice so that I can process reimbursement
- **US-040**: As a Client, I want to track my order status so that I know when to expect delivery
- **US-041**: As a Client, I want to reorder a previous order so that I can quickly purchase the same items again
- **US-042**: As a Client, I want to save my address so that I don't have to re-enter it for future orders
- **US-043**: As a Client, I want to see total cost breakdown (subtotal, discounts, taxes, delivery) so that I understand pricing
- **US-043b**: As a Client, I want to see applied discounts clearly so that I know I'm getting the best price

### Partner Users (Fulfillment Partners - Internal)
- **US-044**: As a Partner, I want to receive order notifications immediately (via internal partner portal) so that I can start production
- **US-045**: As a Partner, I want to see order details (products, customization, quantity, delivery address) via internal portal so that I can fulfill accurately

### Admin Users
- **US-046**: As an Admin, I want to view all orders so that I can monitor platform activity
- **US-047**: As an Admin, I want to manually intervene in order routing if needed so that I can handle exceptions
- **US-047b**: As an Admin, I want to view partner assignments for orders (internal only) so that I can manage fulfillment

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-39: Shopping Cart
The system shall provide a shopping cart with the following capabilities:
- **Add to Cart**: Add single customized product or bundle
- **Cart Item Display**:
  - Product thumbnail (with customization preview)
  - Product name and category
  - Customization indicator (e.g., "With your logo")
  - Quantity selector (1-10,000 per item)
  - Unit price (based on quantity tier)
  - Subtotal per item (quantity × unit price)
  - Remove button
- **Update Quantity**: Real-time price recalculation as quantity changes
- **Remove Item**: Delete from cart with confirmation
- **Cart Summary**:
  - Subtotal (sum of all items)
  - GST (18% for promotional items)
  - Delivery charges (calculated based on weight/location)
  - Total amount
- **Cart Persistence**: For logged-in users, cart saved to database, accessible across sessions/devices
- **Guest Cart**: For non-logged-in users, cart stored in browser localStorage (lost on logout/clear cache)
- **Empty Cart Handling**: Display "Your cart is empty" with link to browse products
- **Cart Icon Badge**: Header cart icon shows item count

### FR-40: Cart Validation
The system shall validate cart before checkout:
- **Product Availability**: Check if all products still active
- **Partner Availability**: Verify associated fulfillment partners are active (internal check)
- **Quantity Limits**: Ensure quantities within acceptable range (1-10,000)
- **Pricing Updates**: Recalculate prices based on current tiers (in case pricing changed)
- **Customization Integrity**: Verify customization data exists and is valid
- **If validation fails**: Display errors, allow user to remove problematic items or update quantities

### FR-41: Checkout Flow
The system shall implement a multi-step checkout:

**Step 1: Delivery Address**
- **New Address Form**:
  - Full Name (recipient)
  - Phone Number (+91-XXXXXXXXXX)
  - Address Line 1 (mandatory)
  - Address Line 2 (optional)
  - City (mandatory, dropdown with Indian cities)
  - State (mandatory, dropdown)
  - PIN Code (mandatory, 6-digit validation)
  - Address Type: Home/Office/Other
  - Mark as default address (checkbox)
- **Saved Addresses**: Display user's saved addresses as selectable cards
- **Validation**: All mandatory fields required, PIN code validated for serviceability
- **Edit/Delete Saved Addresses**: Allow management of address book

**Step 2: Delivery Options**
- **Standard Delivery**: 7-14 days, ₹100 flat rate (or free for orders >₹10,000)
- **Express Delivery**: 3-5 days, ₹300
- **Display**: Estimated delivery date range for each option
- **Default**: Standard selected by default
- **Availability**: Check if express delivery available for selected PIN code

**Step 3: Order Review**
- **Order Summary**:
  - List all cart items with quantities and prices
  - Delivery address (view-only, with "Change" link)
  - Delivery option (view-only, with "Change" link)
  - Price breakdown:
    - Subtotal: ₹X
    - GST (18%): ₹Y
    - Delivery: ₹Z
    - **Total: ₹(X+Y+Z)**
- **Terms & Conditions**: Checkbox "I agree to Terms & Conditions" (mandatory)
- **Proceed to Payment Button**: Enabled only if T&C accepted

### FR-42: Payment Integration
The system shall integrate payment gateway (Razorpay or PayU):
- **Payment Methods**:
  - UPI (QR code, UPI ID)
  - Credit/Debit Cards (Visa, MasterCard, RuPay)
  - Net Banking (all major banks)
  - Wallets (Paytm, PhonePe, Google Pay)
- **Payment Flow**:
  1. User clicks "Proceed to Payment"
  2. System creates order record with status "pending_payment"
  3. System initiates payment gateway session
  4. User redirected to payment gateway (Razorpay hosted page)
  5. User completes payment
  6. Payment gateway redirects back to BrandKit with transaction status
  7. System verifies payment with gateway API (webhook or polling)
  8. If payment success: Update order status to "confirmed", send confirmation email
  9. If payment failed: Update order status to "payment_failed", allow retry
- **Security**: All payment data handled by gateway (PCI-DSS compliant), BrandKit never stores card details
- **Payment Timeout**: 15 minutes, after which order marked "expired"

### FR-43: Order Confirmation
Upon successful payment, the system shall:
- **Generate Order ID**: Unique alphanumeric (e.g., BK-20260123-001)
- **Update Order Status**: "confirmed"
- **Send Confirmation Email** to client with:
  - Order ID
  - Order date
  - Items ordered (with preview images)
  - Delivery address
  - Estimated delivery date
  - Total amount paid
  - GST invoice (PDF attachment)
  - Track order link
- **Send SMS Notification**: "Your BrandKit order BK-20260123-001 for ₹X confirmed. Track: [link]"
- **Display Confirmation Page**:
  - Success message: "Order placed successfully!"
  - Order ID
  - Estimated delivery date
  - Download invoice button
  - Track order button
  - Continue shopping link
- **Clear Cart**: Remove all items from cart

### FR-44: GST Invoice Generation
The system shall auto-generate GST-compliant invoices:
- **Invoice Format**: PDF
- **Invoice Number**: Auto-increment (INV-2026-0001)
- **Invoice Date**: Order date
- **Invoice Contents**:
  - BrandKit company details (Name, Address, GSTIN)
  - Client details (Name, Address, GSTIN if provided)
  - Order ID
  - Itemized list: Product name, HSN code, Quantity, Unit Price, Taxable Amount
  - Tax breakdown: CGST 9%, SGST 9% (or IGST 18% for inter-state)
  - Total amount in words
  - Terms & Conditions
  - Digital signature (image)
- **Storage**: Store PDF in cloud (AWS S3), associate with order
- **Access**: Client can download from order details page anytime

### FR-45: Partner Order Routing (Internal Process)
The system shall automatically route orders to fulfillment partners (internal operations only):
- **Routing Logic** (backend process, NOT visible to clients):
  1. For each product in order, identify associated partner (from internal product-partner mapping table)
  2. If order contains products from multiple partners (not in MVP scope, but future):
     - For MVP: Reject order if products span multiple partners, display error "Unable to process order, please try again" (generic message, no partner details exposed)
  3. Validate partner status (active, accepting orders) - internal database check
  4. If partner location matches client location (same state): Prioritize for faster delivery (internal scoring)
  5. Check partner's current order load (if capacity data available)
  6. Assign order to partner (internal database record)
- **Partner Notification**: Send email and SMS to partner via internal partner portal with order details and print-ready images
- **Partner Acceptance**: Partner must confirm order within 24 hours (handled in FRD-005 - internal partner portal)
- **Fallback**: If partner rejects or doesn't respond, admin manually reassigns to alternate partner (internal admin panel)
- **Client Communication**: Clients only see generic order status updates, NEVER partner names, locations, or any identifying information

### FR-46: Order Status Tracking
The system shall track orders through the following statuses:
- **Pending Payment**: Order created, awaiting payment
- **Payment Failed**: Payment unsuccessful
- **Confirmed**: Payment received, order confirmed
- **Accepted**: Partner confirmed order (internally tracked as "partner_accepted", displayed to client as "Accepted" or "Processing")
- **In Production**: Partner started production (displayed to client without partner reference)
- **Ready to Ship**: Production complete, preparing for dispatch
- **Shipped**: Handed over to delivery partner, tracking ID generated
- **Out for Delivery**: In transit, near destination
- **Delivered**: Successfully delivered to client
- **Cancelled**: Order cancelled (admin/partner action - internal)
- **Refund Initiated**: Refund process started (for cancellations)

### FR-47: Client Order Dashboard
Clients shall access an order management dashboard:
- **Order List**:
  - Table with columns: Order ID, Date, Items (count), Status, Total, Actions
  - Filters: Status (All, Pending, Shipped, Delivered), Date Range
  - Search by Order ID
  - Sort by Date (newest first)
  - Pagination (10 orders per page)
- **Order Details Page**:
  - Order ID, Date, Status badge
  - Progress bar (visual timeline: Confirmed → In Production → Shipped → Delivered)
  - Items list with preview images and quantities
  - Delivery address
  - Tracking information (if shipped): Courier name, Tracking ID, Track link
  - Price breakdown
  - Download invoice button
  - Download preview images button (for all customized items)
  - Contact support button
- **Reorder Functionality**:
  - "Reorder" button on past orders
  - Clicking reorder adds same items with same customizations to cart
  - User can adjust quantities and proceed to checkout
- **Order Status Notifications**:
  - Email sent at each status change
  - SMS sent for key milestones: Confirmed, Shipped, Delivered

### FR-48: Delivery Partner Integration
The system shall integrate with delivery partners:
- **Partners**: Delhivery (pan-India), Local Gujarat couriers
- **Integration Points**:
  - Create shipment: POST order details to partner API
  - Receive tracking ID
  - Webhook for status updates (e.g., in-transit, delivered)
  - Track shipment: GET tracking details via API
- **Tracking ID Storage**: Store in order record, display to client
- **Status Sync**: Update order status based on delivery partner webhooks
- **Fallback**: If API unavailable, manual tracking link generation

### FR-49: Price Calculation Logic
The system shall calculate prices dynamically:
- **Base Price**: Determined by product pricing tier based on quantity (partner-defined, platform-managed)
- **Discount Application**: If partner discount enabled for product, apply discount percentage (partner-defined, platform limits enforced)
- **Discounted Price**: Base Price × (1 - Discount %)
- **Customization Fee**: ₹10-50 per unit (based on customization type: Logo Print, Embroidery, Engraving)
- **Item Subtotal**: quantity × (discounted price + customization fee)
- **Cart Subtotal**: Sum of all item subtotals
- **Discount Savings**: Display total savings from partner discounts
- **GST**: 18% of subtotal (promotional products HSN code)
- **Delivery Charges**:
  - Standard: ₹100 (free if order >₹10,000)
  - Express: ₹300
- **Total**: Subtotal + GST + Delivery Charges
- **Display Requirements**:
  - Show original price with strikethrough if discount applied
  - Show discount percentage and savings amount
  - Show "Special Offer" or "Partner Discount" badge
  - Total savings prominently displayed in cart summary

### FR-50: Order Search and Filtering (Client)
Clients shall be able to find orders quickly:
- **Search**: By Order ID (exact match)
- **Filters**:
  - Status: All, Confirmed, In Production, Shipped, Delivered, Cancelled
  - Date Range: Last 7 days, Last 30 days, Last 3 months, Custom range
- **Results**: Update table in real-time as filters applied
- **Clear Filters**: Reset to show all orders

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Add to Cart and Checkout
1. Client completes customization for "Branded T-Shirt" (quantity: 75)
2. Client clicks "Add to Cart"
3. System:
   - Validates customization data
   - Calculates price: 75 × ₹230 = ₹17,250 (Tier 2 pricing)
   - Adds item to cart (database if logged in, localStorage if guest)
   - Updates cart badge: (1)
4. System displays success toast: "Added to cart!"
5. Client clicks cart icon
6. System displays cart page:
   - Item: Branded T-Shirt (With your logo), Qty: 75, ₹230/unit, Subtotal: ₹17,250
   - Cart total: ₹17,250
7. Client clicks "Proceed to Checkout"
8. System validates cart (all items active, customizations valid)
9. System navigates to Checkout Step 1: Delivery Address
10. Client selects saved address "Office - Ahmedabad" or enters new address:
    - Name: Rajesh Kumar
    - Phone: +91-9876543210
    - Address: 123 Corporate Plaza, S.G. Highway
    - City: Ahmedabad, State: Gujarat, PIN: 380015
11. Client clicks "Continue"
12. System validates address (PIN code serviceable)
13. System navigates to Step 2: Delivery Options
14. System displays:
    - Standard Delivery: ₹100, 7-14 days (Est. delivery: Feb 5-12)
    - Express Delivery: ₹300, 3-5 days (Est. delivery: Jan 27-30)
15. Client selects "Standard Delivery"
16. Client clicks "Continue"
17. System navigates to Step 3: Order Review
18. System displays order summary:
    - Items: Branded T-Shirt × 75
    - Delivery: Office - Ahmedabad
    - Delivery Option: Standard (₹100)
    - Price Breakdown:
      - Subtotal: ₹17,250
      - GST (18%): ₹3,105
      - Delivery: ₹100
      - **Total: ₹20,455**
19. Client checks "I agree to Terms & Conditions"
20. Client clicks "Proceed to Payment"
21. System:
    - Creates order record (status: "pending_payment")
    - Generates Order ID: BK-20260123-001
    - Initiates Razorpay payment session (amount: ₹20,455)
    - Redirects to Razorpay payment page
22. Client selects payment method: UPI
23. Client scans QR code with Google Pay
24. Client authorizes payment in Google Pay app
25. Payment successful
26. Razorpay redirects back to BrandKit with success status
27. System:
    - Receives payment confirmation webhook from Razorpay
    - Verifies payment with Razorpay API
    - Updates order status: "confirmed"
    - Triggers backend high-res image generation (FRD-003)
    - Generates GST invoice (PDF)
    - Sends confirmation email with invoice
    - Sends SMS: "Order BK-20260123-001 confirmed for ₹20,455. Track: [link]"
    - Clears cart
28. System displays Order Confirmation Page:
    - "Order placed successfully! 🎉"
    - Order ID: BK-20260123-001
    - Estimated Delivery: Feb 5-12
    - Buttons: "Download Invoice", "Track Order", "Continue Shopping"
29. Client downloads invoice and shares with finance team

**Edge Cases**:
- If payment fails: Display error "Payment failed. Please retry." with "Retry Payment" button
- If payment timeout (15 min): Display "Payment session expired. Please place order again."
- If product becomes inactive during checkout: Remove from cart, notify user "Product no longer available"
- If PIN code not serviceable: Display "We don't deliver to this PIN code yet. Please try different address."
- If partner unavailable after payment: Admin notified, order held for manual reassignment (client sees generic "processing" message)

### Workflow 2: Partner Order Routing (Backend - Internal Process Only)
**IMPORTANT**: This entire workflow is INTERNAL ONLY. Partner details are NEVER exposed to clients.

1. Order status updated to "confirmed" (payment success)
2. System triggers order routing service (backend internal process)
3. For each product in order:
   - Product: Branded T-Shirt (ID: uuid-tshirt-001)
   - Retrieve product-partner mapping from internal database: Partner = Internal Reference P-GJ-001 (ID: uuid-partner-123)
4. System checks partner status (internal database): Active = true, AcceptingOrders = true
5. System retrieves (internal scoring algorithm):
   - Partner location: Ahmedabad, Gujarat (internal data)
   - Client delivery location: Ahmedabad, Gujarat
   - Match: Same city (priority scoring +10)
6. System checks partner capacity (order count in last 7 days): 15 orders (within capacity)
7. System assigns order to partner (internal database record):
   - Create entry in order_partner_assignments table (admin-only access)
   - Link to main order: BK-20260123-001
   - Status: "awaiting_partner_acceptance"
8. System retrieves print-ready images for customization (generated in FRD-003)
9. System sends email to partner (via INTERNAL partner portal):
   - Subject: "New Order BK-20260123-001 - 75 Branded T-Shirts"
   - Body:
     - Order ID: BK-20260123-001
     - Product: Branded T-Shirt
     - Quantity: 75
     - Delivery Address: Office - Ahmedabad, Gujarat 380015 (NO client contact details)
     - Expected Ship Date: Feb 3 (7 days from order)
     - Print-ready image: [Secure internal download link]
     - Accept/Reject buttons (internal portal only)
10. System sends SMS to partner (via internal partner communication system): "New order BK-20260123-001. Login to partner portal to review."
11. Partner logs into INTERNAL Partner Portal (FRD-005 - NOT accessible to clients)
12. Partner sees order notification (internal dashboard)
13. Partner reviews order details and print image (internal view)
14. Partner clicks "Accept Order" (internal portal action)
15. System updates (internal database):
    - Partner order status: "accepted" (order_partner_assignments table)
    - Main order status: "accepted" (client sees "Processing" or "Accepted" without partner reference)
    - Client notified via email: "Your order has been accepted and will begin production soon" (NO partner name mentioned)
16. Partner begins production (internal process)

**Edge Cases** (All handled INTERNALLY, generic messages to clients):
- If order has products from multiple partners (not in MVP): Display error "Unable to process order. Please contact support" (NO mention of "multiple partners")
- If partner rejects order: Admin notified via internal dashboard alert, manually reassigns to alternate partner. Client sees "Order processing" status (no partner details exposed)
- If partner doesn't respond in 24 hours: Auto-escalate to admin for manual routing (internal). Client notified "Your order is being processed"
- If no active partner for product: Order held, admin notified (internal), client emailed "Order processing will take slightly longer, we'll update you soon" (generic message)

### Workflow 3: Order Tracking (Client View)
1. Client receives order confirmation email with "Track Order" link
2. Client clicks link or navigates to dashboard > Orders
3. System displays Order List page with all client's orders
4. Client sees recent order: BK-20260123-001, Status: "Processing", Date: Jan 23 (internal status is "partner_accepted" but client sees generic "Processing")
5. Client clicks order row
6. System navigates to Order Details page
7. System displays:
   - Order ID: BK-20260123-001
   - Status: Processing (internal: partner_accepted)
   - Progress bar: ●Confirmed → ●Processing → ○In Production → ○Shipped → ○Delivered
   - Items:
     - Branded T-Shirt (preview image), Qty: 75, ₹230/unit
   - Delivery Address: Office - Ahmedabad, Gujarat 380015
   - Estimated Delivery: Feb 5-12
   - Price: ₹20,455
   - Buttons: "Download Invoice", "Contact Support"
8. 3 days later (Jan 26), partner updates status to "In Production" (via internal partner portal)
9. System:
   - Updates order status: "in_production"
   - Sends email to client: "Your order is now in production"
10. Client receives email, clicks track link
11. System displays updated progress bar: ●Confirmed → ●Processing → ●In Production → ○Shipped → ○Delivered (NO partner details shown)
12. 7 days later (Jan 30), partner marks "Ready to Ship" (internal partner portal action)
13. Partner generates shipment with Delhivery (internal process, partner uses their own Delhivery account)
14. Delhivery API returns tracking ID: DELIV12345678
15. System:
    - Updates order status: "shipped"
    - Stores tracking ID
    - Sends email to client: "Your order has shipped! Track: DELIV12345678, Tracking Link: [link]"
    - Sends SMS: "Order BK-20260123-001 shipped. Track: [link]"
16. Client clicks tracking link
17. System redirects to Delhivery tracking page (or embedded tracking widget)
18. Client sees real-time tracking: "In Transit - Expected delivery Feb 5"
19. Feb 5, Delhivery delivers order
20. Delhivery webhook notifies BrandKit: Order delivered
21. System:
    - Updates order status: "delivered"
    - Sends email: "Your order has been delivered! Thank you for choosing BrandKit."
    - Prompts for review (FRD-008 in Phase 2)
22. Client confirms receipt and uses products for employee welcome event

**Edge Cases**:
- If tracking ID not available: Display "Shipment prepared, tracking will be available soon"
- If delivery delayed: System sends proactive email "Delivery delayed, new ETA: [date]"
- If delivery failed (Delhivery webhook): Update status to "delivery_failed", client & admin notified for resolution
- If client reports non-receipt: "Contact Support" button initiates ticket (manual resolution)

### Workflow 4: Reorder Functionality
1. Client logs into dashboard
2. Client navigates to Orders section
3. Client views past order from 2 months ago: BK-20250923-005 (Status: Delivered)
   - Items: T-Shirt × 50, Water Bottle × 50 (Welcome Kit bundle)
4. Client clicks "Reorder" button
5. System:
   - Retrieves order items and customization data
   - Validates products still active
   - Validates customization (logo file still accessible)
6. System adds items to cart:
   - T-Shirt × 50 (With previous logo)
   - Water Bottle × 50 (With previous logo)
7. System displays success toast: "2 items added to cart from previous order"
8. Client navigates to cart
9. Client sees reordered items with customization previews
10. Client updates quantity: T-Shirt × 100, Water Bottle × 100
11. System recalculates prices based on new quantity tiers
12. Client proceeds to checkout and completes order (Workflow 1)

**Edge Cases**:
- If product discontinued: Display "Some items unavailable, added available items only"
- If customization file missing: Prompt "Customization unavailable, please re-upload logo"
- If pricing changed significantly: Display notification "Prices have been updated since your last order"

---

## 7. INPUT & OUTPUT

### Inputs

#### Delivery Address Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Full Name | Text | 2-100 chars | Yes |
| Phone Number | Tel | +91-XXXXXXXXXX (10 digits) | Yes |
| Address Line 1 | Text | 5-200 chars | Yes |
| Address Line 2 | Text | 0-200 chars | No |
| City | Dropdown | Valid Indian cities | Yes |
| State | Dropdown | Indian states | Yes |
| PIN Code | Number | 6 digits, serviceable | Yes |
| Address Type | Radio | Home/Office/Other | Yes |
| Mark as Default | Checkbox | Boolean | No |

#### Cart Item Update
| Field | Type | Validation |
|-------|------|------------|
| Quantity | Number | 1-10,000 |

### Outputs

#### Cart Summary Response
```json
{
  "status": "success",
  "data": {
    "cartId": "uuid-cart-123",
    "userId": "uuid-user-456",
    "items": [
      {
        "cartItemId": "uuid-item-789",
        "productId": "uuid-tshirt-001",
        "productName": "Branded T-Shirt",
        "customizationId": "uuid-cust-111",
        "previewUrl": "https://cdn.brandkit.com/previews/preview-789.png",
        "quantity": 75,
        "unitPrice": 230,
        "subtotal": 17250
      }
    ],
    "pricing": {
      "subtotal": 17250,
      "gst": 3105,
      "deliveryCharges": 100,
      "total": 20455
    }
  }
}
```

#### Order Confirmation Response
```json
{
  "status": "success",
  "message": "Order placed successfully",
  "data": {
    "orderId": "BK-20260123-001",
    "orderDate": "2026-01-23T12:00:00Z",
    "status": "confirmed",
    "items": [
      {
        "productId": "uuid-tshirt-001",
        "productName": "Branded T-Shirt",
        "quantity": 75,
        "unitPrice": 230,
        "customizationId": "uuid-cust-111",
        "previewUrl": "https://cdn.brandkit.com/previews/preview-789.png"
      }
    ],
    "deliveryAddress": {
      "name": "Rajesh Kumar",
      "phone": "+91-9876543210",
      "addressLine1": "123 Corporate Plaza, S.G. Highway",
      "city": "Ahmedabad",
      "state": "Gujarat",
      "pinCode": "380015"
    },
    "deliveryOption": "standard",
    "estimatedDelivery": "2026-02-05 to 2026-02-12",
    "pricing": {
      "originalSubtotal": 19500,
      "discount": 2250,
      "discountPercentage": 11.54,
      "subtotal": 17250,
      "gst": 3105,
      "deliveryCharges": 100,
      "total": 20455,
      "totalSavings": 2250
    },
    "payment": {
      "method": "UPI",
      "transactionId": "RAZORPAY_TXN_123456",
      "status": "success",
      "timestamp": "2026-01-23T12:05:00Z"
    },
    "invoiceUrl": "https://s3.brandkit.com/invoices/INV-2026-0001.pdf"
  }
}
```

#### Order Tracking Response
```json
{
  "status": "success",
  "data": {
    "orderId": "BK-20260123-001",
    "currentStatus": "shipped",
    "statusHistory": [
      { "status": "confirmed", "timestamp": "2026-01-23T12:00:00Z", "description": "Payment received" },
      { "status": "accepted", "timestamp": "2026-01-23T14:30:00Z", "description": "Order accepted for fulfillment" },
      { "status": "in_production", "timestamp": "2026-01-26T09:00:00Z", "description": "Production started" },
      { "status": "shipped", "timestamp": "2026-01-30T11:00:00Z", "description": "Order shipped" }
    ],
    "trackingInfo": {
      "courierName": "Delhivery",
      "trackingId": "DELIV12345678",
      "trackingUrl": "https://www.delhivery.com/track?id=DELIV12345678",
      "estimatedDelivery": "2026-02-05"
    }
  }
}
```

#### GST Invoice (PDF Content)
```
BrandKit Pvt. Ltd.
GSTIN: 24XXXXX1234X1ZX
Address: 456 Business Park, Ahmedabad, Gujarat 380001

INVOICE
Invoice No: INV-2026-0001
Invoice Date: 23-Jan-2026
Order ID: BK-20260123-001

Bill To:
Rajesh Kumar
123 Corporate Plaza, S.G. Highway
Ahmedabad, Gujarat 380015
Phone: +91-9876543210

---------------------------------------------------
S.No | Product       | HSN  | Qty | Rate  | Discount | Amount
---------------------------------------------------
1    | Branded T-Shirt| 6109 | 75  | ₹260  | 11.5%   | ₹17,250
                                (₹230 after discount)
---------------------------------------------------
                           Original Total: ₹19,500
                         Discount Savings: ₹2,250
                               Subtotal: ₹17,250
                          CGST (9%): ₹1,552.50
                          SGST (9%): ₹1,552.50
                       Delivery Charges: ₹100
                           -------------------
                           Total: ₹20,455.00
                           
Amount in Words: Twenty Thousand Four Hundred Fifty-Five Rupees Only

Terms & Conditions:
1. Goods once sold cannot be returned.
2. Delivery within 7-14 business days.

For BrandKit Pvt. Ltd.
[Digital Signature]
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-31: Cart Item Quantity
Minimum quantity per item: 1. Maximum quantity per item: 10,000. No MOQ enforcement as per PRD.

### BR-32: GST Calculation
GST is 18% (CGST 9% + SGST 9% for intra-state, IGST 18% for inter-state) applied to subtotal of all items. Delivery charges are also taxable.

### BR-33: Delivery Charges
Standard Delivery: ₹100 (free if order subtotal >₹10,000).  
Express Delivery: ₹300 (no free shipping).  
Charges calculated based on order subtotal, not item count.

### BR-34: Payment Timeout
Payment session expires after 15 minutes. If user doesn't complete payment, order status remains "pending_payment" and can be retried from order history for 24 hours, after which it's marked "expired".

### BR-35: Order Confirmation Dependency
Order status changes to "confirmed" only after successful payment verification via payment gateway webhook AND API confirmation. Relying on client-side redirect alone is insufficient due to possible tampering.

### BR-36: Partner Routing for Single Partner Orders (Internal Process)
For MVP, all items in an order must belong to products from the same fulfillment partner (internal check). If cart contains products from multiple partners, checkout is blocked with generic error message "Unable to process order, please contact support" (no partner details exposed to client).

### BR-37: Address Serviceability
Before proceeding to payment, system must validate that delivery PIN code is serviceable by selected delivery partner. If not, display error and prevent checkout.

### BR-38: Invoice Generation Timing
GST invoice is generated immediately after payment confirmation. Invoice number is auto-incremented and unique per fiscal year. Partner details are NOT included in client-facing invoices.

### BR-39: Order Modification Restriction
Once order status changes to "accepted" (internal: partner_accepted), client cannot modify or cancel order via self-service. Must contact support for changes (manual process). Partner information never disclosed to client during support interactions.

### BR-40: Reorder Validation
Reorder functionality validates that products are still active and customization files are accessible. If either fails, only available items are added to cart with notification.

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Cart empty at checkout | ORD_001 | 400 | "Your cart is empty" | Add items to cart |
| Product unavailable in cart | ORD_002 | 400 | "Some products are no longer available" | Remove unavailable items |
| Invalid quantity | ORD_003 | 400 | "Quantity must be between 1 and 10,000" | Adjust quantity |
| PIN code not serviceable | ORD_004 | 400 | "We don't deliver to this PIN code yet" | Try different address or contact support |
| Payment failed | ORD_005 | 402 | "Payment failed. Please retry" | Retry payment or use different method |
| Payment timeout | ORD_006 | 408 | "Payment session expired" | Restart checkout |
| Payment gateway error | ORD_007 | 502 | "Payment service unavailable. Try again later" | Retry after some time |
| Multiple partners in cart (internal) | ORD_008 | 400 | "Unable to process order. Please contact support" | Contact support (generic error, no partner details exposed) |
| Partner unavailable (internal) | ORD_009 | 500 | "Order processing delayed. We'll notify you soon" | Wait for admin resolution (client never sees partner info) |
| Address validation failed | ORD_010 | 400 | "Invalid address. Please check details" | Correct address fields |
| Invoice generation failed | ORD_011 | 500 | "Invoice unavailable. Contact support" | Download via support or wait for retry |
| Order not found | ORD_012 | 404 | "Order not found" | Check order ID or return to orders list |
| Tracking unavailable | ORD_013 | 404 | "Tracking information not yet available" | Check back later |
| Delivery failed | ORD_014 | N/A | "Delivery attempted but failed. Courier will retry" | Wait for next delivery attempt or contact support |
| Reorder product unavailable | ORD_015 | 400 | "Some items from previous order are unavailable" | Proceed with available items or browse catalog |

### Error Handling Strategy
- Display clear, user-friendly error messages
- Provide actionable next steps (buttons, links)
- For payment failures, always offer retry option
- For critical errors (payment gateway down), display maintenance message with ETA
- Log all errors server-side for admin review
- For partner-related errors (internal failures), auto-notify admin for manual intervention. Show generic "Order processing" message to clients
- Never expose sensitive payment details or internal errors to users

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-75**: Cart page must load within 1 second
- **NFR-76**: Checkout flow each step must load within 1.5 seconds
- **NFR-77**: Payment initiation must occur within 2 seconds of "Proceed to Payment" click
- **NFR-78**: Order confirmation email must be sent within 30 seconds of payment success
- **NFR-79**: Invoice PDF generation must complete within 5 seconds
- **NFR-80**: Order list page must load within 1.5 seconds (showing 10 orders)

### Scalability
- **NFR-81**: System must handle 200 concurrent checkouts without degradation
- **NFR-82**: Order tables in PostgreSQL must efficiently query orders (B-tree indexes on user_id, order_id, status, created_at)
- **NFR-82b**: Use PostgreSQL connection pooling (pgBouncer or Supabase pooler) for concurrent checkout operations
- **NFR-83**: Payment webhook processing must handle 100 requests/second during peak

### Reliability
- **NFR-84**: Payment verification must use both webhook and polling (redundancy)
- **NFR-85**: Order confirmation emails must have 99% delivery rate (use reliable SMTP provider)
- **NFR-86**: Cart data persistence must have 99.9% reliability (PostgreSQL with point-in-time recovery enabled)
- **NFR-86b**: Use PostgreSQL transactions with ACID guarantees for order creation and payment confirmation

### Security
- **NFR-87**: All payment data handled by PCI-DSS compliant gateway, BrandKit never stores card details
- **NFR-88**: Payment webhook must validate signature to prevent tampering
- **NFR-89**: Order details accessible only to order owner (userId match) and admins
- **NFR-89b**: Partner assignment data (order_partner_assignments table) accessible ONLY to admins and partner portal (internal), NEVER exposed via client APIs
- **NFR-90**: Delivery address data encrypted at rest (PostgreSQL encryption, AES-256)
- **NFR-91**: Invoice PDFs must be accessible only via signed URLs with 1-hour expiry
- **NFR-91b**: Use PostgreSQL Row Level Security (RLS) to prevent client access to partner-related columns in orders table

### Compliance
- **NFR-92**: GST invoice must comply with Indian GST Act requirements (all mandatory fields)
- **NFR-93**: Invoice must be generated and stored for 7 years per tax regulations
- **NFR-94**: Payment receipts must meet RBI guidelines

### Accessibility
- **NFR-95**: Checkout flow keyboard navigable (Tab, Enter, Space)
- **NFR-96**: Form errors announced to screen readers
- **NFR-97**: Order status progress bar accessible (ARIA labels for each step)

---

## 11. ACCEPTANCE CRITERIA

### AC-31: Add to Cart
**Given** a client has customized a product (T-Shirt, quantity: 75)  
**When** the client clicks "Add to Cart"  
**Then** the system validates customization  
**And** calculates price: 75 × ₹230 = ₹17,250  
**And** adds item to cart (database if logged in)  
**And** updates cart badge to show item count  
**And** displays success toast "Added to cart!"

### AC-32: Update Cart Quantity
**Given** a client has T-Shirt (qty: 75) in cart  
**When** the client changes quantity to 150  
**Then** the system recalculates:
- Applies new pricing tier (50-199 units: ₹230/unit)
- New subtotal: 150 × ₹230 = ₹34,500
**And** updates cart summary in real-time  
**And** updates GST and total accordingly

### AC-33: Complete Checkout Flow
**Given** a logged-in client with items in cart  
**When** the client clicks "Proceed to Checkout"  
**Then** the system displays Delivery Address step  
**When** the client selects saved address "Office - Ahmedabad"  
**And** clicks "Continue"  
**Then** the system displays Delivery Options step  
**When** the client selects "Standard Delivery (₹100)"  
**And** clicks "Continue"  
**Then** the system displays Order Review step with:
- Items, address, delivery option
- Price breakdown: Subtotal, GST (18%), Delivery, Total
**When** the client checks "I agree to T&C"  
**And** clicks "Proceed to Payment"  
**Then** the system creates order (status: "pending_payment")  
**And** redirects to Razorpay payment page

### AC-34: Process Successful Payment
**Given** a client on Razorpay payment page for order BK-20260123-001 (₹20,455)  
**When** the client completes payment via UPI  
**And** payment is successful  
**Then** Razorpay redirects back to BrandKit  
**And** BrandKit receives payment webhook  
**And** the system:
- Verifies payment with Razorpay API
- Updates order status to "confirmed"
- Generates Order ID: BK-20260123-001
- Generates GST invoice PDF
- Sends confirmation email with invoice attachment
- Sends SMS: "Order confirmed, Track: [link]"
- Clears cart
- Displays Order Confirmation Page with order details

### AC-35: Automatic Partner Routing (Internal Process)
**Given** order BK-20260123-001 is confirmed (payment success)  
**When** the system processes partner routing (INTERNAL backend process)  
**Then** the system:
- Identifies product partner from internal database: P-GJ-001 (UUID: uuid-partner-123)
- Validates partner status: Active (internal database check)
- Checks location match: Ahmedabad (client) = Ahmedabad (partner location from internal database)
- Assigns order to partner (creates record in order_partner_assignments table, admin-only access)
- Sends email to partner via internal partner portal with order details and print-ready images
- Sends SMS to partner via internal partner communication system
- Updates internal order status: "awaiting_partner_acceptance"
- **Client sees status**: "Processing" (NO partner reference)
**When** partner logs into INTERNAL partner portal and accepts order  
**Then** internal order status updates to "partner_accepted"  
**And** client sees status "Processing" or "In Production" (NO partner name mentioned)
**And** client notified "Your order has been accepted and will begin production soon" (generic message, NO partner reference)

### AC-36: Track Order Status (No Partner Information Shown)
**Given** a client with confirmed order BK-20260123-001  
**When** the client navigates to Orders dashboard  
**Then** the system displays order list with BK-20260123-001, Status: "Processing" (internal status: partner_accepted, NOT shown to client)  
**When** the client clicks order row  
**Then** the system displays Order Details page with (NO partner details shown):
- Progress bar: ●Confirmed → ●Processing → ○In Production → ○Shipped → ○Delivered
- Item details with preview image
- Delivery address
- Estimated delivery date
- Download invoice button (invoice contains NO partner details)
**When** partner updates status to "Shipped" with tracking ID (via INTERNAL partner portal)  
**Then** the system:
- Updates status to "shipped"
- Sends email and SMS to client with tracking link (NO partner name mentioned)
- Displays tracking info on order details page (courier info only, NO partner reference)

### AC-37: Generate GST Invoice (With Discount Information, No Partner Details)
**Given** order BK-20260123-001 is confirmed  
**When** the system generates invoice  
**Then** the invoice PDF contains:
- Invoice No: INV-2026-0001
- BrandKit GSTIN and address (NOT partner GSTIN)
- Client name and delivery address
- Order ID: BK-20260123-001
- Itemized list: T-Shirt × 75, HSN: 6109, Original: ₹260/unit, Discount: 11.5%, Final: ₹230/unit, ₹17,250
- Original Subtotal: ₹19,500
- Discount Savings: ₹2,250
- Subtotal after discount: ₹17,250
- Tax breakdown: CGST 9% (₹1,552.50), SGST 9% (₹1,552.50)
- Delivery: ₹100
- Total: ₹20,455
- Amount in words
- Terms & Conditions
- **IMPORTANT**: NO partner/seller name, GSTIN, or any identifying information
**And** the invoice is stored in S3  
**And** the client can download from order details page

### AC-38: Reorder Previous Order
**Given** a client views past order BK-20250923-005 (T-Shirt × 50, Bottle × 50, Status: Delivered)  
**When** the client clicks "Reorder"  
**Then** the system:
- Validates products still active
- Validates customization data accessible
- Adds 2 items to cart with same customizations
- Displays success "2 items added to cart from previous order"
**When** the client navigates to cart  
**Then** the cart displays both items with customization previews  
**And** the client can adjust quantities and proceed to checkout

### AC-39: Handle Payment Failure
**Given** a client on Razorpay payment page  
**When** payment fails (insufficient funds, cancelled, timeout)  
**Then** Razorpay redirects back to BrandKit with failure status  
**And** the system:
- Updates order status to "payment_failed"
- Displays error page "Payment failed. Please retry"
- Provides "Retry Payment" button
**When** the client clicks "Retry Payment"  
**Then** the system re-initiates payment session for same order

### AC-40: Block Multi-Partner Cart (Internal Check)
**Given** a client has T-Shirt (internal partner: P-GJ-001) in cart  
**When** the client tries to add Water Bottle (internal partner: P-GJ-002, different partner)  
**Then** the system (internal backend check):
- Detects products from different partners (internal database check)
- Displays generic error: "Unable to process these items together. Please complete your current order first"
- Prevents adding the second product
- Suggests "Complete current order or remove existing items"
**IMPORTANT**: NO partner names, IDs, or references are shown to client. Error message is generic.

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Payment Gateway**: Razorpay or PayU (UPI, Cards, Net Banking, Wallets)
- **Delivery Partners**: Delhivery API (pan-India), local courier APIs
- **Email Service**: SendGrid or AWS SES for order notifications
- **SMS Service**: Twilio, MSG91 for SMS notifications
- **PDF Generation**: Library like jsPDF (frontend) or iText (Java backend)

### Internal Dependencies
- **FRD-001 (Authentication)**: User login for cart persistence, order history
- **FRD-002 (Product Catalog)**: Product data (price, name, images, discount information)
- **FRD-003 (Customization Engine)**: Customization data for order items, print-ready images
- **FRD-005 (Partner Dashboard)**: Partner order acceptance, status updates (internal partner portal, NOT client-facing)

### Database Tables (Supabase PostgreSQL)
- `carts`: User carts (items, quantities) - relational table with foreign keys
- `orders`: Main order records with partner_id (internal, NOT exposed to clients)
- `order_items`: Individual items within orders with discount information
- `addresses`: Saved user addresses - normalized relational structure
- `invoices`: Invoice metadata and URLs (no partner details in client-facing invoices)
- `payments`: Payment transaction records with referential integrity
- `order_partner_assignments`: Internal table mapping orders to partners (admin-only access)

### API Endpoints
- `POST /api/cart/add`: Add item to cart
- `PUT /api/cart/item/:itemId`: Update cart item quantity
- `DELETE /api/cart/item/:itemId`: Remove cart item
- `GET /api/cart`: Get user's cart
- `POST /api/checkout/validate`: Validate cart before checkout
- `POST /api/orders/create`: Create order (pre-payment)
- `POST /api/orders/:orderId/payment`: Initiate payment
- `POST /api/payment/webhook`: Razorpay webhook handler
- `GET /api/orders`: Get user's order list
- `GET /api/orders/:orderId`: Get order details
- `GET /api/orders/:orderId/invoice`: Download invoice PDF
- `POST /api/orders/:orderId/reorder`: Reorder functionality
- `POST /api/orders/:orderId/route-partner`: Partner routing (internal admin/system API only, NOT client-facing)

---

## 13. ASSUMPTIONS

1. Clients have valid Indian delivery addresses
2. Payment gateway (Razorpay) maintains 99.9% uptime
3. Delivery partners provide reliable APIs for shipment creation and tracking
4. Partners (fulfillment) respond to order notifications via internal partner portal within 24 hours
5. GST rates remain stable at 18% for promotional products
6. Clients prefer digital invoices (PDF) over physical
7. Email and SMS services have high deliverability (>95%)
8. Orders are fulfilled within estimated lead times provided by partners (internal SLA management)
9. Cart persistence is critical for B2B clients who plan purchases over days
10. Inter-state orders are less common initially (Gujarat focus), so IGST handling is basic
11. Partner information is strictly internal and NEVER exposed to clients through any interface
12. PostgreSQL (Supabase) provides sufficient performance for order management workloads with proper indexing

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Order cancellation workflow (client-initiated before partner acceptance)
- Partial refunds and return management
- Buy Now Pay Later (BNPL) integration (e.g., Capital Float for B2B)
- Cash on Delivery (COD) option (with verification for B2B)
- Multi-partner order splitting (automatic routing to 2+ partners per order, maintaining partner anonymity to clients)
- Bulk discounts negotiation tool - partners set discounts, platform manages negotiations (client requests quote for large orders)
- Recurring orders / Subscription model (monthly kit deliveries)
- Integration with client ERP/procurement systems (API for order sync, NO partner data exposed)
- Advanced tracking: Real-time GPS for local deliveries
- Estimated delivery date machine learning (based on historical partner performance - internal analytics)
- Order insurance option (protection against loss/damage)
- Loyalty points and rewards program (partner-funded, platform-managed)
- Promo codes and discount coupons (partner-defined, admin-controlled)
- Invoice financing for large B2B orders
- Export invoices for international expansion
- Advanced PostgreSQL features: table partitioning by date, materialized views for analytics, query optimization

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Payment gateway integration setup, Razorpay API testing, delivery partner API integration, invoice PDF template design
