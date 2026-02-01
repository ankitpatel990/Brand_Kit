# FRD-007: Payment Gateway Integration

---

## 1. FRD METADATA

**FRD Title**: Payment Gateway Integration and Transaction Management  
**FRD ID**: FRD-007  
**Related PRD Section(s)**: 4.2.1 Core Features - Order Placement and Management (Payment), 4.1 High-Level Architecture  
**Priority**: High  
**Owner**: Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Integrate a secure, reliable payment gateway (Razorpay or PayU) to enable clients to complete transactions using multiple payment methods (UPI, Cards, Net Banking, Wallets) while ensuring PCI-DSS compliance, transaction verification, refund processing, and comprehensive payment tracking.

### Business Value
- Enable seamless checkout experience for B2B clients
- Support multiple payment methods preferred by Indian businesses
- Ensure payment security and regulatory compliance
- Automate payment verification and order confirmation
- Handle refunds efficiently for cancellations
- Provide payment analytics for financial tracking
- Build client trust through secure payment processing

---

## 3. SCOPE

### In Scope
- Integration with Razorpay (primary) or PayU (alternative) payment gateway
- Payment method support: UPI, Credit/Debit Cards (Visa, MasterCard, RuPay), Net Banking, Wallets (Paytm, PhonePe, Google Pay)
- Payment session initiation from checkout flow
- Hosted payment page (gateway-provided, PCI-DSS compliant)
- Payment verification via webhooks and API polling
- Transaction record storage (transaction ID, status, method, amount)
- Refund processing for order cancellations
- Partial and full refund support
- Payment status tracking (Pending, Success, Failed, Refunded)
- Payment analytics dashboard (admin)
- Failed payment retry mechanism
- Payment timeout handling (15 minutes)
- Test mode for development and staging environments
- GST-compliant invoicing integration with payment records

### Out of Scope
- Cash on Delivery (COD) - requires separate logistics workflow
- Buy Now Pay Later (BNPL) integrations (e.g., Capital Float) - Phase 2
- International payment methods (PayPal, Stripe for USD) - India focus for MVP
- Payment links via SMS/email (standalone, without order) - Phase 2
- Subscription/recurring payments - Phase 2 feature (FRD-009)
- Direct bank transfers (manual bank account payments)
- Cryptocurrency payments
- Payment gateway switching UI (admin selects gateway in settings, requires code deploy to change)
- Split payments (pay via multiple methods for one order)
- EMI (Equated Monthly Installment) options - Phase 2

---

## 4. USER STORIES

### Client Users
- **US-071**: As a Corporate Procurement Manager, I want to pay via UPI so that I can complete transactions quickly from my phone
- **US-072**: As a Client, I want to pay via credit card so that I can earn rewards and have expense tracking
- **US-073**: As a Client, I want to see payment confirmation immediately so that I know my order is placed
- **US-074**: As a Client, I want to retry payment if it fails so that I don't have to re-enter order details
- **US-075**: As a Client, I want to receive a refund if I cancel my order so that my payment is returned

### Admin Users
- **US-076**: As an Admin, I want to view all payment transactions so that I can track revenue and troubleshoot issues
- **US-077**: As an Admin, I want to process refunds so that I can handle cancellations fairly
- **US-078**: As an Admin, I want to see failed payments so that I can identify issues with the payment gateway

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-78: Payment Gateway Selection
The system shall support integration with:
- **Primary Gateway**: Razorpay
- **Alternative Gateway**: PayU (configurable switch via admin settings)
- **Configuration**: Admin can select gateway in Platform Settings, requires API keys (Key ID, Key Secret)
- **Test Mode**: Toggle in settings to use test API keys for development/staging

### FR-79: Payment Session Initiation
When client proceeds to payment from checkout:
- **System Actions**:
  1. Create order record in database (status: "pending_payment")
  2. Generate unique order ID (e.g., BK-20260123-001)
  3. Calculate total amount (subtotal + GST + delivery)
  4. Call payment gateway API to create payment session:
     - **Razorpay**: `POST /v1/orders` with {amount, currency: INR, receipt: orderID}
     - **PayU**: Equivalent API call
  5. Receive payment session ID (e.g., `razorpay_order_xyz123`)
  6. Store payment session ID in order record
  7. Redirect client to gateway's hosted payment page with session ID
- **Input**: Order details (amount, orderID, client email, phone)
- **Output**: Payment session URL

### FR-80: Hosted Payment Page
Payment gateway shall provide hosted checkout page:
- **Razorpay Hosted Checkout**: Gateway-provided UI with all payment methods
- **Client Experience**:
  - Sees BrandKit logo and order details (amount, order ID)
  - Selects payment method (UPI, Card, Net Banking, Wallet)
  - Completes payment (enters UPI ID, card details, or selects wallet)
  - Sees payment processing status
  - Redirected back to BrandKit on success/failure
- **Security**: All payment data handled by gateway (PCI-DSS Level 1 compliant), BrandKit never sees card numbers
- **Session Timeout**: 15 minutes, after which session expires, user must restart checkout

### FR-81: Payment Methods Support
The system shall support the following payment methods via gateway:

**UPI**:
- QR code for scanning with any UPI app
- UPI ID input field (e.g., user@oksbi)
- Supported apps: Google Pay, PhonePe, Paytm, BHIM, all bank UPI apps

**Credit/Debit Cards**:
- Visa, MasterCard, RuPay, American Express
- Input fields: Card number, Expiry (MM/YY), CVV, Cardholder name
- Save card option (tokenization via gateway for repeat customers)
- 3D Secure authentication (OTP verification for added security)

**Net Banking**:
- Dropdown list of 50+ Indian banks (SBI, HDFC, ICICI, Axis, etc.)
- Redirect to bank's net banking portal for authentication
- Return to BrandKit after payment

**Wallets**:
- Paytm, PhonePe, Amazon Pay, Mobikwik
- One-click payment if wallet logged in
- OTP verification if required by wallet

### FR-82: Payment Verification
After payment completion, the system shall verify transaction:

**Webhook Verification (Primary)**:
- Gateway sends webhook to BrandKit endpoint: `POST /api/payment/webhook`
- Webhook payload contains:
  - Payment ID, Order ID, Status (success/failed)
  - Amount, Method, Timestamp
  - Signature (HMAC-SHA256 for security)
- **System Actions**:
  1. Verify webhook signature (prevent tampering)
  2. Extract payment status
  3. If status = "success":
     - Update order status: "confirmed"
     - Store payment transaction ID
     - Trigger order confirmation workflow (FRD-004)
     - Send confirmation email to client
  4. If status = "failed":
     - Update order status: "payment_failed"
     - Display error message to client
     - Offer retry option

**API Polling (Fallback)**:
- If webhook not received within 30 seconds (network issues, firewall):
  - System polls payment gateway API: `GET /v1/payments/{paymentId}`
  - Retrieves payment status
  - Updates order accordingly
- **Polling Interval**: 3 attempts (at 30s, 60s, 90s after redirect)
- If still unverified after 90s: Mark payment "pending_verification", admin review

### FR-83: Payment Status Handling
The system shall handle all payment statuses:

**Success**:
- Order status: "confirmed"
- Client sees Order Confirmation Page
- Email/SMS sent with order details and invoice

**Failed**:
- Order status: "payment_failed"
- Client sees error page: "Payment failed. Reason: [Insufficient funds / Declined by bank / Network error]"
- Actions:
  - "Retry Payment" button (re-initiates payment session for same order)
  - "Change Payment Method" link (returns to checkout with different method)
  - "Cancel" link (cancels order, returns to cart)

**Pending Verification**:
- Order status: "pending_payment"
- Client sees: "Payment is being verified. You'll receive confirmation shortly."
- Email sent when verification completes (success or failure)

**Timeout/Abandoned**:
- If client doesn't complete payment within 15 minutes:
  - Payment session expires
  - Order status: "payment_timeout"
  - Client can retry from order history (for 24 hours)
  - After 24 hours: Order marked "expired"

### FR-84: Transaction Record Storage
The system shall store comprehensive payment records:

**Payment Transaction Model**:
- **Transaction ID**: Unique identifier (UUID)
- **Payment Gateway ID**: Gateway's payment ID (e.g., `pay_razorpay_xyz123`)
- **Order ID**: Link to order record
- **User ID**: Client who made payment
- **Amount**: Payment amount in INR (paise for Razorpay, e.g., 2045500 for ₹20,455)
- **Currency**: INR
- **Payment Method**: UPI / Card / Net Banking / Wallet
- **Method Details**: (masked) UPI ID, Last 4 digits of card, Bank name
- **Status**: Pending / Success / Failed / Refunded / Partially Refunded
- **Failure Reason**: (if failed) Insufficient funds, Declined, Network error
- **Gateway Response**: JSON payload from gateway (for debugging)
- **Webhook Received**: Boolean
- **Webhook Verified**: Boolean (signature check)
- **Created At**: Timestamp
- **Updated At**: Timestamp

**Data Security**:
- Never store full card numbers (PCI-DSS violation)
- Store only last 4 digits for reference
- Encrypt sensitive fields (UPI ID, bank name) at rest

### FR-85: Refund Processing
The system shall process refunds for cancelled orders:

**Full Refund Workflow**:
1. Admin cancels order (FRD-006, FR-71)
2. System checks: Payment status = "Success"
3. System calls refund API:
   - **Razorpay**: `POST /v1/payments/{paymentId}/refund` with {amount: null} (full refund)
4. Gateway processes refund (instant to 5-7 business days depending on method)
5. Gateway sends webhook: Refund status = "processed"
6. System updates:
   - Order status: "refund_initiated" → "refunded"
   - Payment transaction status: "refunded"
   - Create refund record (refund ID, amount, status, date)
7. System sends email to client: "Refund processed for ₹X. Credited to original payment method in 5-7 days."

**Partial Refund** (Future, for order modifications):
- Call refund API with specific amount (e.g., ₹5,000 of ₹20,000)
- Payment status: "partially_refunded"
- Use case: Client cancels 1 item from multi-item order

**Refund Timeline**:
- UPI: 1-3 business days
- Cards: 5-7 business days
- Net Banking: 5-7 business days
- Wallets: Instant to 24 hours

### FR-86: Failed Payment Retry
Clients shall be able to retry failed payments:

**Retry from Error Page**:
- Client clicks "Retry Payment" on payment failure page
- System:
  1. Retrieves existing order record
  2. Validates order still valid (not expired, products available)
  3. Initiates new payment session (new payment ID)
  4. Redirects to hosted payment page
- Client completes payment with different method or same method

**Retry from Order History**:
- Client navigates to "Orders" dashboard
- Sees order with status "Payment Failed"
- Clicks "Complete Payment" button
- Same retry workflow as above

**Retry Limit**: 3 attempts within 24 hours, after which order expires and client must place new order

### FR-87: Payment Timeout Handling
The system shall handle payment session timeouts:
- **Session Expiry**: 15 minutes from payment initiation
- **Client Behavior**: If client doesn't complete payment within 15 minutes:
  - Gateway closes payment page
  - Client redirected to timeout page: "Payment session expired. Please try again."
  - Option to restart checkout
- **Order Status**: "payment_timeout"
- **Notification**: Email sent after 1 hour if payment still incomplete: "Your order is waiting. Complete payment: [link]"
- **Auto-Expiry**: Order expires after 24 hours if payment not completed

### FR-88: Payment Analytics (Admin)
Admins shall access payment analytics:

**Payment Dashboard**:
- **Metrics Cards**:
  - Total Transactions (This Month): Count
  - Successful Payments: Count (% success rate)
  - Failed Payments: Count (% failure rate)
  - Total Revenue: ₹X
  - Refunds Processed: ₹Y
- **Charts**:
  - Payment Method Distribution: Pie chart (UPI 50%, Cards 30%, Net Banking 15%, Wallets 5%)
  - Transaction Trend: Line graph (last 30 days, daily transaction count)
  - Failure Rate Trend: Line graph (% failed payments over time)
- **Recent Transactions**: Table (last 50 transactions)
  - Transaction ID, Order ID, Client, Amount, Method, Status, Date

**Transaction List View** (Detailed):
- **Table Columns**: Transaction ID, Order ID, Client Name, Amount, Method, Status, Date, Actions
- **Filters**: Status (All, Success, Failed, Refunded), Method, Date Range
- **Search**: By Transaction ID, Order ID, Client Email
- **Actions per transaction**:
  - View Details (full gateway response, webhook logs)
  - Initiate Refund (if success and not refunded)
  - Retry Verification (if pending)

### FR-89: Gateway Configuration (Admin)
Admins shall configure payment gateway settings:

**Configuration Page** (in Platform Settings):
- **Gateway Selection**: Dropdown (Razorpay / PayU)
- **Test Mode**: Toggle (enable for staging, disable for production)
- **API Credentials** (per gateway):
  - **Razorpay**:
    - Key ID: Text field (masked)
    - Key Secret: Text field (masked)
    - Webhook Secret: Text field (masked)
  - **PayU**:
    - Merchant Key: Text field (masked)
    - Salt Key: Text field (masked)
- **Webhook URL**: Display read-only (e.g., `https://api.brandkit.com/api/payment/webhook`)
- **Supported Payment Methods**: Multi-select checkboxes
  - UPI, Cards, Net Banking, Wallets (admin can disable methods if needed)
- **Payment Timeout**: Number input (minutes, default 15)
- **Save Settings**: Button, validates API keys by test API call, saves if valid

**Test Connection**:
- Admin clicks "Test Connection" button
- System makes test API call to gateway (e.g., fetch gateway status)
- Displays success "Connection successful" or error "Invalid API keys"

### FR-90: Webhook Security
The system shall secure webhook endpoints:
- **Signature Verification**: All incoming webhooks verified using HMAC-SHA256 signature with webhook secret
- **Steps**:
  1. Receive webhook POST request
  2. Extract signature from header (e.g., `X-Razorpay-Signature`)
  3. Compute expected signature: HMAC-SHA256(webhook body, webhook secret)
  4. Compare computed signature with received signature
  5. If mismatch: Reject webhook, log security alert
  6. If match: Process webhook
- **Rate Limiting**: Max 100 webhook requests per minute per IP (prevent DDoS)
- **Idempotency**: If same webhook received twice (duplicate), process once (check transaction ID already updated)

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Successful Payment via UPI
1. Client completes checkout, clicks "Proceed to Payment"
2. System:
   - Creates order: BK-20260123-001, Amount: ₹20,455, Status: "pending_payment"
   - Calls Razorpay API: `POST /v1/orders` with {amount: 2045500 (paise), currency: "INR", receipt: "BK-20260123-001"}
   - Razorpay returns: {id: "order_razorpay_abc123", status: "created"}
   - Stores: paymentSessionId: "order_razorpay_abc123"
   - Generates Razorpay Checkout URL with session ID
3. System redirects client to Razorpay Hosted Checkout page
4. Client sees:
   - BrandKit logo, Order ID: BK-20260123-001, Amount: ₹20,455
   - Payment methods: UPI selected by default
5. Client clicks "Pay via UPI"
6. Razorpay displays UPI QR code
7. Client opens Google Pay on phone, scans QR code
8. Google Pay displays: "Pay ₹20,455 to BrandKit"
9. Client enters UPI PIN, authorizes payment
10. Google Pay confirms: "Payment successful"
11. Razorpay processes payment (2-3 seconds)
12. Razorpay sends webhook to BrandKit: `POST https://api.brandkit.com/api/payment/webhook`
    - Payload: {event: "payment.success", payload: {payment: {id: "pay_xyz789", order_id: "order_razorpay_abc123", status: "captured", amount: 2045500, method: "upi", vpa: "user@okaxis"}}}
    - Signature in header: `X-Razorpay-Signature`
13. BrandKit webhook handler:
    - Receives POST request
    - Verifies signature (compute HMAC, compare)
    - Signature valid: Proceed
    - Extracts: paymentId: "pay_xyz789", orderId: "order_razorpay_abc123", status: "captured", method: "upi"
    - Finds order by paymentSessionId = "order_razorpay_abc123"
    - Updates order:
      - Status: "confirmed"
      - Payment transaction ID: "pay_xyz789"
      - Payment method: "UPI"
      - Payment status: "success"
    - Creates transaction record:
      - transactionId: UUID
      - gatewayPaymentId: "pay_xyz789"
      - orderId: BK-20260123-001
      - amount: 20455.00
      - method: "UPI"
      - methodDetails: "user@okaxis" (masked: "user@ok***")
      - status: "success"
      - webhookReceived: true
      - webhookVerified: true
    - Triggers post-payment actions:
      - Generate invoice PDF (FRD-004, FR-44)
      - Send confirmation email with invoice
      - Send SMS: "Order confirmed"
      - Trigger partner order routing (FRD-004, FR-45)
      - Clear client's cart
14. Razorpay redirects client back to BrandKit: `https://brandkit.com/order-confirmation?order_id=BK-20260123-001&status=success`
15. System displays Order Confirmation Page:
    - "Order placed successfully! 🎉"
    - Order ID, Amount, Estimated Delivery
    - "Download Invoice" button
16. Client receives confirmation email within 30 seconds

**Edge Cases**:
- If webhook delayed (>30s): System polls Razorpay API at 30s, 60s, 90s to verify payment
- If webhook signature invalid: Reject webhook, poll API instead, log security alert
- If payment processing fails after client authorized (rare): Webhook shows status "failed", refund auto-initiated by Razorpay

### Workflow 2: Failed Payment via Credit Card
1. Client selects credit card payment method on Razorpay page
2. Client enters card details:
   - Card Number: 4111 1111 1111 1111
   - Expiry: 12/28
   - CVV: 123
   - Name: Rajesh Kumar
3. Client clicks "Pay ₹20,455"
4. Razorpay initiates card charge
5. Bank declines transaction (reason: "Insufficient funds")
6. Razorpay receives decline from bank
7. Razorpay sends webhook to BrandKit:
   - Payload: {event: "payment.failed", payload: {payment: {id: "pay_fail_123", order_id: "order_razorpay_abc123", status: "failed", error_code: "BAD_REQUEST_ERROR", error_description: "Payment declined by issuing bank"}}}
8. BrandKit webhook handler:
   - Verifies signature
   - Extracts: paymentId: "pay_fail_123", status: "failed", error: "Insufficient funds"
   - Updates order: Status: "payment_failed", Payment transaction ID: "pay_fail_123"
   - Creates transaction record: status: "failed", failureReason: "Insufficient funds"
9. Razorpay redirects client to BrandKit: `https://brandkit.com/payment-failed?order_id=BK-20260123-001&error=insufficient_funds`
10. System displays Payment Failed Page:
    - "Payment failed. Reason: Insufficient funds in your card."
    - "Please try again with a different payment method."
    - Buttons:
      - "Retry Payment" (primary CTA)
      - "Change Payment Method" (secondary)
      - "Cancel Order" (tertiary)
11. Client clicks "Retry Payment"
12. System:
    - Validates order still valid (not expired)
    - Creates new payment session (new order_id in Razorpay)
    - Redirects to Razorpay hosted page
13. Client selects UPI this time, completes payment successfully (Workflow 1)

**Edge Cases**:
- If client clicks "Cancel Order": Order status updated to "cancelled", no refund needed (payment never succeeded)
- If client doesn't retry within 24 hours: Order expires, client must place new order
- If payment fails 3 times: Display "Please contact your bank or try different payment method"

### Workflow 3: Refund Processing (Order Cancellation)
1. Client places order BK-20260123-005, pays ₹30,000 via UPI (status: "confirmed")
2. 2 hours later, client contacts support to cancel order (partner hasn't accepted yet)
3. Support team escalates to admin
4. Admin logs into Admin Panel
5. Admin navigates to order detail page (BK-20260123-005)
6. Admin clicks "Cancel Order"
7. System displays cancellation modal:
   - "Cancel order BK-20260123-005?"
   - Reason: Dropdown (Client request, Partner unavailable, Other)
   - Admin selects "Client request"
8. Admin clicks "Confirm Cancellation"
9. System:
   - Updates order status: "cancelled"
   - Retrieves payment transaction: paymentId: "pay_refund_xyz", amount: 30000, status: "success"
   - Calls Razorpay Refund API: `POST /v1/payments/pay_refund_xyz/refund` with {amount: 3000000 (paise)} (full refund)
   - Razorpay processes refund
   - Razorpay returns: {id: "rfnd_abc123", payment_id: "pay_refund_xyz", amount: 3000000, status: "processed"}
   - System stores refund record:
     - refundId: "rfnd_abc123"
     - transactionId: (link to original payment)
     - amount: 30000
     - status: "processed"
     - refundedAt: timestamp
   - Updates payment transaction status: "refunded"
   - Updates order status: "refunded"
   - Sends email to client:
     - Subject: "Order BK-20260123-005 Cancelled - Refund Processed"
     - Body: "Your order has been cancelled. Refund of ₹30,000 has been initiated. Amount will be credited to your original payment method (UPI) within 1-3 business days."
   - Logs cancellation and refund action (admin ID, timestamp, reason)
10. System displays success to admin: "Order cancelled and refund processed. Refund ID: rfnd_abc123"
11. 2 days later: Client receives ₹30,000 in bank account (UPI refund)
12. Client confirms refund receipt, satisfied with process

**Edge Cases**:
- If refund API fails (Razorpay service down): System retries 3 times, if still fails, marks refund status "pending_retry", admin notified to manually process
- If partial refund needed (e.g., cancel 1 item from 2-item order): Admin specifies partial amount in refund API call
- If refund takes longer than expected (>7 days): Client contacts support, admin checks refund status via Razorpay dashboard, escalates to gateway if stuck

### Workflow 4: Payment Timeout Handling
1. Client proceeds to payment, Razorpay page opens
2. Client gets interrupted (phone call, meeting)
3. Client leaves Razorpay page open without completing payment
4. 15 minutes pass (payment session timeout)
5. Razorpay closes payment page, displays: "Session expired. Please retry."
6. Client clicks "Retry"
7. Razorpay redirects back to BrandKit: `https://brandkit.com/payment-timeout?order_id=BK-20260123-001`
8. System:
   - Updates order status: "payment_timeout"
   - Displays timeout page: "Your payment session expired. No worries, your order is saved!"
   - Buttons:
     - "Complete Payment Now" (restart payment session)
     - "Complete Later" (saves order, sends reminder email)
9. Client clicks "Complete Later"
10. System sends reminder email:
    - Subject: "Your BrandKit Order is Waiting!"
    - Body: "Hi Rajesh, you started an order (BK-20260123-001) for ₹20,455 but didn't complete payment. Complete now: [link]"
    - Link valid for 24 hours
11. 2 hours later, client clicks link in email
12. System:
    - Validates order still valid (not expired)
    - Creates new payment session
    - Redirects to Razorpay page
13. Client completes payment this time, order confirmed

**Edge Cases**:
- If client doesn't retry within 24 hours: Order status changed to "expired", cart items released
- If client clicks "Complete Payment Now" immediately: New session created instantly, no waiting
- If multiple timeout retries (client keeps abandoning): After 3 timeouts, system suggests "Save items to cart instead?"

---

## 7. INPUT & OUTPUT

### Inputs

#### Payment Session Initiation
| Field | Type | Validation |
|-------|------|------------|
| Order ID | String | Unique, generated |
| Amount | Number | >0, INR in paise (Razorpay) |
| Currency | String | "INR" |
| Client Email | Email | Valid email |
| Client Phone | Tel | +91-XXXXXXXXXX |

#### Webhook Payload (Razorpay Example)
```json
{
  "event": "payment.success",
  "payload": {
    "payment": {
      "id": "pay_xyz789",
      "order_id": "order_razorpay_abc123",
      "status": "captured",
      "amount": 2045500,
      "currency": "INR",
      "method": "upi",
      "vpa": "user@okaxis",
      "email": "client@example.com",
      "contact": "+919876543210",
      "created_at": 1706016000
    }
  }
}
```

### Outputs

#### Payment Session Response (Razorpay)
```json
{
  "id": "order_razorpay_abc123",
  "entity": "order",
  "amount": 2045500,
  "currency": "INR",
  "receipt": "BK-20260123-001",
  "status": "created",
  "created_at": 1706015000
}
```

#### Transaction Record (Database)
```json
{
  "transactionId": "uuid-trans-123",
  "gatewayPaymentId": "pay_xyz789",
  "orderId": "BK-20260123-001",
  "userId": "uuid-user-456",
  "amount": 20455.00,
  "currency": "INR",
  "paymentMethod": "UPI",
  "methodDetails": "user@ok***",
  "status": "success",
  "failureReason": null,
  "gatewayResponse": {...},
  "webhookReceived": true,
  "webhookVerified": true,
  "createdAt": "2026-01-23T12:05:00Z",
  "updatedAt": "2026-01-23T12:05:30Z"
}
```

#### Refund API Call (Razorpay)
```json
POST /v1/payments/pay_xyz789/refund
{
  "amount": 3000000,
  "notes": {
    "reason": "Client request cancellation",
    "order_id": "BK-20260123-005"
  }
}
```

#### Refund Response (Razorpay)
```json
{
  "id": "rfnd_abc123",
  "entity": "refund",
  "payment_id": "pay_xyz789",
  "amount": 3000000,
  "currency": "INR",
  "status": "processed",
  "created_at": 1706100000
}
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-61: Payment Amount Accuracy
Payment amount must exactly match order total (subtotal + GST + delivery charges). Any discrepancy triggers payment verification failure.

### BR-62: Single Payment Per Order
Each order can have only one successful payment transaction. If a second payment attempt succeeds for same order (rare race condition), second payment is auto-refunded.

### BR-63: Webhook Signature Verification
All webhooks must have valid HMAC-SHA256 signatures. Invalid signatures are rejected and logged as security incidents.

### BR-64: Refund Time Limit
Refunds can be processed only for payments made within the last 180 days (gateway limitation). Beyond 180 days, manual bank transfer required.

### BR-65: Payment Method Availability
If a payment method is disabled by admin (e.g., Wallets turned off), it will not appear on gateway's hosted page for new orders.

### BR-66: Test Mode Isolation
Payments made in test mode use test API keys and never charge real money. Test transactions are clearly marked in admin panel and cannot be mixed with live transactions.

### BR-67: Currency Restriction
Only INR (Indian Rupees) is supported for MVP. Multi-currency planned for international expansion.

### BR-68: Idempotency for Webhooks
If the same webhook (same payment ID, same event) is received multiple times, only the first is processed. Subsequent duplicates are ignored.

### BR-69: Refund Amount Limit
Refunds cannot exceed the original payment amount. Partial refunds must be less than or equal to remaining refundable amount.

### BR-70: Payment Timeout Auto-Expiry
Orders with status "payment_timeout" that are not completed within 24 hours are auto-expired and cannot be paid for (client must place new order).

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Payment session creation failed | PAY_001 | 500 | "Unable to initiate payment. Try again." | Retry checkout or contact support |
| Invalid payment method | PAY_002 | 400 | "Selected payment method not available" | Choose different method |
| Payment declined by bank | PAY_003 | 402 | "Payment declined by your bank. Insufficient funds or limit exceeded." | Use different card/method |
| Payment timeout | PAY_004 | 408 | "Payment session expired after 15 minutes" | Restart checkout |
| Webhook verification failed | PAY_005 | 401 | "Payment verification failed. Contact support." | Admin manually verifies via gateway dashboard |
| Refund API error | PAY_006 | 500 | "Refund processing failed. Will retry automatically." | Admin checks refund status later |
| Duplicate payment detected | PAY_007 | 409 | "Payment already processed for this order" | Check order status or contact support |
| Gateway API unavailable | PAY_008 | 503 | "Payment service temporarily unavailable. Try again in 5 minutes." | Wait and retry |
| Invalid API credentials | PAY_009 | 401 | "Payment gateway configuration error. Contact admin." | Admin checks API keys in settings |
| Amount mismatch | PAY_010 | 400 | "Payment amount doesn't match order total" | Recalculate order, restart checkout |
| Refund already processed | PAY_011 | 409 | "Refund already completed for this order" | Check refund status |
| Transaction not found | PAY_012 | 404 | "Transaction record not found" | Check transaction ID or contact support |

### Error Handling Strategy
- Display user-friendly error messages on payment failure
- Log detailed gateway responses server-side for debugging
- For transient errors (gateway timeout), auto-retry after 2 seconds
- For persistent errors (invalid API keys), send alert to admin immediately
- Never expose gateway API keys or secrets in error messages
- For webhook failures, fall back to API polling to prevent order status inconsistency

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-135**: Payment session creation must complete within 2 seconds
- **NFR-136**: Webhook processing must complete within 500ms
- **NFR-137**: Refund API call must complete within 3 seconds
- **NFR-138**: Payment verification polling (if webhook fails) must complete within 5 seconds per attempt

### Reliability
- **NFR-139**: Payment gateway integration must have 99.9% uptime (dependent on Razorpay/PayU SLA)
- **NFR-140**: Webhook endpoint must handle 100 requests/second during peak (order spikes)
- **NFR-141**: Payment verification must use both webhook + API polling (redundancy)
- **NFR-142**: Failed refunds must auto-retry 3 times with exponential backoff

### Security
- **NFR-143**: All payment API calls must use HTTPS with TLS 1.3
- **NFR-144**: API keys and secrets stored in environment variables, never in code or database
- **NFR-145**: Webhook signature verification mandatory for all incoming webhooks
- **NFR-146**: BrandKit never stores full credit card numbers (PCI-DSS compliance)
- **NFR-147**: Payment transaction logs encrypted at rest (AES-256)
- **NFR-148**: Admin access to payment settings restricted to Super Admin role only

### Compliance
- **NFR-149**: Payment processing must comply with RBI (Reserve Bank of India) guidelines
- **NFR-150**: Refunds must be processed to original payment method (RBI mandate)
- **NFR-151**: Transaction records retained for 7 years per Indian tax law
- **NFR-152**: Payment gateway must be PCI-DSS Level 1 certified (Razorpay and PayU are)

### Monitoring
- **NFR-153**: Payment success/failure rates monitored in real-time
- **NFR-154**: Alerts triggered if payment failure rate >10% in any 1-hour window
- **NFR-155**: Webhook delivery failures logged and alerted to admin
- **NFR-156**: Refund processing delays (>1 hour) trigger admin alert

---

## 11. ACCEPTANCE CRITERIA

### AC-61: Initiate Payment Session via UPI
**Given** client completes checkout for order BK-20260123-001 (₹20,455)  
**When** client clicks "Proceed to Payment"  
**Then** the system creates order record with status "pending_payment"  
**And** calls Razorpay API to create payment session  
**And** receives payment session ID  
**And** redirects client to Razorpay hosted page  
**And** client sees BrandKit logo, order details, and UPI payment option

### AC-62: Complete UPI Payment Successfully
**Given** client on Razorpay hosted page  
**When** client selects UPI and scans QR code with Google Pay  
**And** authorizes payment with UPI PIN  
**Then** Google Pay confirms "Payment successful"  
**And** Razorpay sends webhook to BrandKit with status "success"  
**And** BrandKit verifies webhook signature  
**And** updates order status to "confirmed"  
**And** stores transaction record with payment ID and method "UPI"  
**And** sends confirmation email to client  
**And** redirects client to Order Confirmation Page

### AC-63: Handle Failed Card Payment
**Given** client selects credit card payment  
**When** bank declines transaction (insufficient funds)  
**Then** Razorpay sends webhook with status "failed" and reason "Insufficient funds"  
**And** BrandKit updates order status to "payment_failed"  
**And** stores transaction record with status "failed" and failure reason  
**And** redirects client to Payment Failed Page  
**And** displays error "Payment declined by bank. Insufficient funds."  
**And** provides "Retry Payment" button

### AC-64: Retry Failed Payment
**Given** client on Payment Failed Page for order BK-20260123-001  
**When** client clicks "Retry Payment"  
**Then** the system validates order still valid  
**And** creates new payment session  
**And** redirects to Razorpay page  
**When** client completes payment with UPI this time  
**Then** payment succeeds and order is confirmed

### AC-65: Process Full Refund
**Given** order BK-20260123-005 with status "confirmed" and payment "success" (₹30,000)  
**When** admin cancels order and confirms cancellation  
**Then** the system calls Razorpay Refund API with full amount  
**And** Razorpay returns refund ID "rfnd_abc123" with status "processed"  
**And** system stores refund record  
**And** updates payment transaction status to "refunded"  
**And** updates order status to "refunded"  
**And** sends email to client "Refund of ₹30,000 initiated, will credit in 1-3 days"  
**And** client receives refund in bank account within 3 days

### AC-66: Handle Payment Timeout
**Given** client redirected to Razorpay page for payment  
**When** 15 minutes pass without payment completion  
**Then** Razorpay closes session and displays "Session expired"  
**And** redirects client to BrandKit timeout page  
**And** system updates order status to "payment_timeout"  
**And** displays "Session expired. Complete payment now or later."  
**When** client clicks "Complete Payment Now"  
**Then** new payment session created and client can retry

### AC-67: Verify Webhook Signature
**Given** Razorpay sends webhook for successful payment  
**When** BrandKit webhook endpoint receives POST request  
**Then** system extracts signature from header  
**And** computes expected signature using webhook secret  
**And** compares computed signature with received signature  
**When** signatures match  
**Then** webhook is processed (order status updated)  
**When** signatures don't match  
**Then** webhook is rejected and logged as security alert

### AC-68: Fallback to API Polling
**Given** payment completed by client on Razorpay  
**When** webhook is not received within 30 seconds (network issue)  
**Then** system initiates API polling  
**And** calls Razorpay API at 30s, 60s, 90s to fetch payment status  
**When** API returns status "success"  
**Then** system updates order status to "confirmed"  
**And** proceeds with order confirmation workflow

### AC-69: Admin Views Payment Analytics
**Given** admin logs into Admin Panel  
**When** admin navigates to Payments > Analytics  
**Then** system displays:
- Total Transactions: 142
- Success Rate: 92%
- Failed Payments: 11
- Payment Method Distribution: UPI 50%, Cards 30%, etc.
- Transaction Trend (last 30 days): Line graph
**When** admin clicks "View All Transactions"  
**Then** system displays transaction list with filters and search

### AC-70: Admin Configures Payment Gateway
**Given** admin navigates to Settings > Payment Gateway  
**When** admin selects "Razorpay"  
**And** enters API Key and Secret  
**And** clicks "Test Connection"  
**Then** system makes test API call to Razorpay  
**When** API keys are valid  
**Then** system displays "Connection successful"  
**And** saves configuration  
**When** API keys are invalid  
**Then** system displays "Invalid API keys. Please check and retry"

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Payment Gateway**: Razorpay (primary) or PayU (alternative)
  - Razorpay API: https://razorpay.com/docs/api/
  - Razorpay Webhooks: payment.success, payment.failed, refund.processed
- **Refund Service**: Razorpay Refunds API
- **Razorpay X**: For partner payouts (FRD-005, FR-62)

### Internal Dependencies
- **FRD-004 (Order Management)**: Payment triggers order confirmation, cart clearing
- **FRD-006 (Admin Panel)**: Payment analytics, refund processing, gateway configuration

### Database Tables (Supabase PostgreSQL)
- `payment_transactions`: All payment records (indexed by order_id, status, timestamp)
- `refunds`: Refund records with foreign key to payment_transactions
- `payment_webhooks`: Log of all received webhooks (for audit, time-series data, consider partitioning)
- `orders`: Order records with payment status (foreign key constraints to payment_transactions)
- **ACID Transactions**: Use PostgreSQL transactions to ensure payment confirmation and order status update are atomic
- **Indexes**: B-tree indexes on order_id, transaction_id, status, gateway_order_id for fast lookups
- **Row Level Security**: Clients can only view their own payment transactions

### API Endpoints
- `POST /api/payment/initiate`: Initiate payment session (from checkout)
- `POST /api/payment/webhook`: Receive gateway webhooks
- `GET /api/payment/verify/:orderId`: Verify payment status (polling fallback)
- `POST /api/payment/refund/:transactionId`: Initiate refund (admin)
- `GET /api/payment/transactions`: List transactions (admin)
- `GET /api/payment/transactions/:transactionId`: Transaction details (admin)
- `GET /api/payment/analytics`: Payment analytics (admin)
- `POST /api/payment/retry/:orderId`: Retry failed payment (client)

### Environment Variables
```
RAZORPAY_KEY_ID=rzp_test_xxxxxxxx (or rzp_live_xxxxxxxx for production)
RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxx
RAZORPAY_WEBHOOK_SECRET=xxxxxxxxxxxxxxxxxxxx
PAYMENT_GATEWAY=razorpay (or payu)
PAYMENT_TEST_MODE=true (or false for production)
```

---

## 13. ASSUMPTIONS

1. Clients have access to at least one of the supported payment methods (UPI, Cards, Net Banking)
2. Razorpay/PayU maintain 99.9% uptime as per SLA
3. Webhooks are delivered by gateway within 30 seconds (network permitting)
4. Clients' banks support 3D Secure for card transactions
5. UPI apps (Google Pay, PhonePe) are widely used by target B2B clients
6. Refunds are processed by gateway within their stated timelines (1-7 days)
7. Platform has sufficient balance in Razorpay account for refunds (refunds deducted from account balance)
8. India-based clients only for MVP (INR currency, Indian payment methods)
9. Gateway API rate limits are sufficient for BrandKit's transaction volume
10. PCI-DSS compliance is maintained by gateway (BrandKit doesn't handle card data directly)

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- EMI (Equated Monthly Installments) for high-value orders (>₹50,000)
- Buy Now Pay Later (BNPL) integrations: Capital Float, ZestMoney for B2B credit
- Subscription/recurring payments for repeat orders (FRD-009)
- Split payments (pay via multiple methods for one order, e.g., ₹10k card + ₹10k UPI)
- International payment methods (Stripe, PayPal for USD/EUR)
- Payment links via SMS/email (standalone payments without order placement)
- Auto-debit mandates for corporate clients (monthly billing)
- Advanced fraud detection (flag suspicious transactions)
- Payment method optimization (recommend fastest/cheapest method based on client history)
- Dynamic discounts (instant cashback for UPI payments)
- Loyalty points integration (earn/redeem points during payment)
- Invoice financing (third-party financing for large B2B orders)
- Cryptocurrency payments (Bitcoin, USDC for tech-savvy clients)
- Offline payment reconciliation (bank transfer matching)
- Multi-currency support for pan-India expansion (different state currencies if applicable)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Razorpay account setup, API key generation, webhook endpoint development, payment UI/UX design, test transaction flows
