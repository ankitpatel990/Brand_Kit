# FRD-007: Payment Gateway Integration - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing Payment Gateway Integration and Transaction Management with testing instructions after each prompt.

---

## Sub-Prompt 1: Payment Gateway Configuration (Razorpay)
**Task**: Set up Razorpay account and configure API credentials.

**Implementation Details**:
- Register on Razorpay Dashboard (razorpay.com)
- Generate API Keys: Key ID and Key Secret (Test mode and Live mode)
- Configure Webhook Secret for signature verification
- Store credentials in environment variables (NOT in code)
- Admin settings page: Gateway selection (Razorpay/PayU), Test Mode toggle, API credentials input
- Test connection: Validate API keys by making test API call

**Testing Instructions**:
1. Register Razorpay account
   - Expected: Account created, dashboard accessible
2. Generate Test API keys
   - Expected: Key ID (rzp_test_xxx) and Secret received
3. Store in .env file: RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET, RAZORPAY_WEBHOOK_SECRET
4. Admin enters keys in settings page
   - Expected: Keys saved securely
5. Click "Test Connection"
   - Expected: API call to Razorpay, "Connection successful"
6. Test with invalid keys
   - Expected: Error "Invalid API keys"
7. Switch to Live mode
   - Expected: Live keys required (rzp_live_xxx)

---

## Sub-Prompt 2: Payment Session Initiation
**Task**: Implement payment session creation when user proceeds to checkout.

**Implementation Details**:
- Trigger: User clicks "Proceed to Payment" from checkout review page
- Create order record: status "pending_payment"
- Call Razorpay API: `POST /v1/orders` with {amount: (in paise), currency: "INR", receipt: orderID}
- Receive payment session ID (order_id from Razorpay)
- Store session ID in order record
- Redirect user to Razorpay Hosted Checkout page
- Session timeout: 15 minutes

**Testing Instructions**:
1. Complete checkout, click "Proceed to Payment"
   - Expected: Order created with status "pending_payment"
2. Check Razorpay API call
   - Expected: POST to /v1/orders, amount in paise (₹20,455 → 2045500)
3. Verify response: order_id returned
   - Expected: Session ID stored in database
4. User redirected to Razorpay page
   - Expected: BrandKit logo, order details, amount displayed
5. Test timeout: Wait 16 minutes
   - Expected: Session expired, user must restart
6. Test API error: Razorpay service down
   - Expected: Error "Unable to initiate payment. Try again."
7. Verify order record: paymentSessionId field populated

---

## Sub-Prompt 3: Razorpay Hosted Checkout Page
**Task**: Integrate Razorpay Hosted Checkout for payment method selection.

**Implementation Details**:
- Razorpay provides hosted UI (no custom UI needed)
- Payment methods displayed: UPI, Cards, Net Banking, Wallets
- User selects method, completes payment
- 3D Secure authentication for cards (OTP verification)
- Session timeout: 15 minutes
- Redirect back to BrandKit on success/failure

**Testing Instructions**:
1. User on Razorpay page, selects UPI
   - Expected: QR code displayed
2. Scan QR with Google Pay, authorize
   - Expected: Payment successful
3. Select Credit Card, enter test card: 4111 1111 1111 1111
   - Expected: 3D Secure OTP sent, user enters, payment succeeds
4. Test Net Banking: Select HDFC
   - Expected: Redirect to bank portal, simulate payment
5. Test Wallet: Select Paytm
   - Expected: Paytm login, payment succeeds
6. Test failed payment: Use test card for failure (5200 0000 0000 0000)
   - Expected: Payment failed, error displayed
7. Test timeout: Leave page open for 16 minutes
   - Expected: Session expired message

---

## Sub-Prompt 4: Webhook Payment Verification
**Task**: Implement webhook endpoint to receive payment status from Razorpay.

**Implementation Details**:
- Endpoint: `POST /api/payment/webhook`
- Verify webhook signature: HMAC-SHA256(webhook body, webhook secret)
- Parse payload: Extract payment ID, order ID, status (success/failed)
- If success: Update order status "confirmed", trigger post-payment workflow
- If failed: Update order status "payment_failed", notify user
- Idempotency: Handle duplicate webhooks (same payment ID)
- Return 200 OK to Razorpay (acknowledge receipt)

**Testing Instructions**:
1. User completes payment, Razorpay sends webhook
   - Expected: POST received at /api/payment/webhook
2. Verify signature: Compute HMAC, compare with header
   - Expected: Signature valid
3. Extract data: payment_id, order_id, status
   - Expected: Correct order found
4. Status = "captured" (success)
   - Expected: Order status updated to "confirmed"
5. Trigger post-payment: Send confirmation email, generate invoice
   - Expected: Email sent within 30 seconds
6. Test tampered webhook: Invalid signature
   - Expected: Webhook rejected, logged as security alert
7. Test duplicate webhook: Send same payload twice
   - Expected: Second ignored (idempotency)

---

## Sub-Prompt 5: API Polling Fallback
**Task**: Implement API polling as fallback if webhook not received.

**Implementation Details**:
- If webhook not received within 30 seconds: Start polling
- Polling intervals: 30s, 60s, 90s
- API call: `GET /v1/payments/{paymentId}`
- Retrieve payment status
- Update order status based on API response
- Stop polling after 3 attempts
- If still unverified: Mark "pending_verification", admin review

**Testing Instructions**:
1. Simulate webhook failure (disable webhook endpoint)
2. User completes payment
   - Expected: No webhook received
3. After 30 seconds: System polls Razorpay API
   - Expected: GET request to /v1/payments/{paymentId}
4. API returns status "captured"
   - Expected: Order status updated to "confirmed"
5. Test polling retry: First poll fails (network error)
   - Expected: Retry at 60s, then 90s
6. All 3 polls fail
   - Expected: Order "pending_verification", admin alerted
7. Admin manually verifies via Razorpay dashboard, updates order

---

## Sub-Prompt 6: Payment Status Handling
**Task**: Implement different payment status flows (success, failure, timeout).

**Implementation Details**:
- Success: Order Confirmation Page, email/SMS sent, cart cleared
- Failure: Payment Failed Page with retry button, reason displayed
- Timeout: Payment Timeout Page with "Complete Payment" button
- Pending: "Verifying payment..." message, email sent when verified
- Store transaction record: payment ID, method, status, amount, timestamp

**Testing Instructions**:
1. Successful payment: UPI
   - Expected: Redirect to Order Confirmation Page
2. Failed payment: Insufficient funds
   - Expected: Payment Failed Page, reason "Insufficient funds"
3. Click "Retry Payment"
   - Expected: New payment session, redirect to Razorpay
4. Timeout: Leave Razorpay page open 16 minutes
   - Expected: Timeout page, option to restart
5. Pending verification (webhook delayed)
   - Expected: "Verifying..." page, email sent after verification
6. Check transaction record: All details stored
   - Expected: payment_id, method "UPI", status "success"
7. Test cart clearing: Successful payment
   - Expected: Cart empty after order confirmation

---

## Sub-Prompt 7: Refund Processing
**Task**: Implement refund workflow for order cancellations.

**Implementation Details**:
- Admin cancels order: Trigger refund
- Call Razorpay API: `POST /v1/payments/{paymentId}/refund` with {amount: null} (full refund)
- Store refund record: refund ID, amount, status
- Update payment status: "refunded"
- Send email: "Refund processed for ₹X"
- Webhook: Refund status updates (processed)

**Testing Instructions**:
1. Admin cancels paid order
   - Expected: Refund API called automatically
2. Razorpay API returns refund ID
   - Expected: Refund record created
3. Check client email
   - Expected: "Refund of ₹20,455 initiated. Credited in 5-7 days."
4. Razorpay webhook: Refund processed
   - Expected: Status updated to "refunded"
5. Test partial refund: Cancel 1 item from 2-item order
   - Expected: Partial amount refunded
6. Test refund failure: Invalid payment ID
   - Expected: Error, admin alerted
7. Verify refund received: Check test bank account (5-7 days for real)

---

## Sub-Prompt 8: Failed Payment Retry Mechanism
**Task**: Implement retry functionality for failed payments.

**Implementation Details**:
- Retry button: On payment failed page
- Endpoint: `POST /api/payment/retry/:orderId`
- Validate order still valid (not expired, products available)
- Create new payment session (new order_id in Razorpay)
- Redirect to Razorpay page
- Retry limit: 3 attempts within 24 hours
- After 3 failures: Order expires

**Testing Instructions**:
1. Payment fails (insufficient funds), user clicks "Retry"
   - Expected: New payment session created
2. User redirected to Razorpay
   - Expected: Same order amount, new session ID
3. User changes payment method (UPI instead of card), pays
   - Expected: Payment succeeds
4. Test retry limit: Fail 3 times
   - Expected: 4th attempt blocked, "Maximum retries exceeded"
5. Test retry after 25 hours
   - Expected: Order expired, must place new order
6. Test retry with expired product
   - Expected: Error "Product no longer available"
7. From order history: "Complete Payment" button
   - Expected: Same retry workflow

---

## Sub-Prompt 9: Payment Transaction Storage
**Task**: Implement comprehensive transaction record storage.

**Implementation Details**:
- Table: `payment_transactions` with columns: id, gateway_payment_id, order_id, user_id, amount, currency, method, method_details (masked), status, failure_reason, gateway_response (JSON), webhook_received, webhook_verified, created_at, updated_at
- Store after payment attempt (success or failure)
- Security: NEVER store full card numbers (PCI-DSS)
- Store: Last 4 digits, UPI ID (masked), bank name
- Encryption: Sensitive fields encrypted at rest (AES-256)

**Testing Instructions**:
1. User pays via UPI
   - Expected: Transaction record created
2. Verify stored data:
   - Present: payment_id, order_id, amount, method "UPI"
   - Masked: UPI ID "user@ok***"
3. User pays via card
   - Expected: Only last 4 digits stored "XXXX-1111"
4. Check full card number NOT stored anywhere
   - Expected: Database query returns only masked data
5. Test failed payment storage
   - Expected: Status "failed", failure_reason "Insufficient funds"
6. Gateway response (JSON) stored
   - Expected: Full API response for debugging
7. Encryption test: Database backup restored
   - Expected: Sensitive fields still encrypted

---

## Sub-Prompt 10: Payment Analytics Dashboard (Admin)
**Task**: Implement payment analytics for admin monitoring.

**Implementation Details**:
- Metrics: Total Transactions (this month), Success Rate %, Failed Payments, Total Revenue, Refunds Processed
- Charts: Payment Method Distribution (pie), Transaction Trend (line), Failure Rate Trend
- Transaction list: Table with filters (status, method, date range), search by transaction ID/order ID
- Actions: View Details, Initiate Refund, Retry Verification

**Testing Instructions**:
1. Admin navigates to Payments > Analytics
   - Expected: All metrics displayed
2. Total Transactions: 142
   - Expected: Matches database count
3. Success Rate: 92%
   - Expected: (130 success / 142 total) × 100
4. Payment Method Distribution
   - Expected: Pie chart (UPI 50%, Cards 30%, etc.)
5. Transaction Trend: Last 30 days
   - Expected: Line graph with daily transaction counts
6. Filter by status "Failed"
   - Expected: Only failed transactions displayed
7. Click transaction, view details
   - Expected: Full gateway response, webhook logs
8. Initiate refund from admin panel
   - Expected: Refund API called, status updated

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Successful Payment Flow (UPI)**: Checkout → Payment Session → Razorpay Page → UPI Payment → Webhook → Order Confirmed
2. **Successful Payment Flow (Card)**: Checkout → Card Payment → 3D Secure OTP → Payment → Webhook → Order Confirmed
3. **Failed Payment Flow**: Payment Fails → Retry → Success
4. **Webhook Failure Flow**: Payment Success → No Webhook → API Polling → Order Confirmed
5. **Refund Flow**: Admin Cancels Order → Refund Initiated → Webhook → Client Notified → Refund Received
6. **Timeout Flow**: Session Expires → Restart Checkout → Payment Success
7. **Multiple Retry Flow**: Fail → Retry → Fail → Retry → Success
8. **Analytics Flow**: Admin Views Dashboard → Filters Transactions → Exports CSV

---

## Security and Compliance Validation Checklist

- [ ] All payment API calls use HTTPS (TLS 1.3)
- [ ] API keys stored in environment variables, NEVER in code
- [ ] Webhook signature verification mandatory
- [ ] BrandKit NEVER stores full card numbers (PCI-DSS)
- [ ] Payment data encrypted at rest (AES-256)
- [ ] Transaction logs retained for 7 years (Indian tax law)
- [ ] Refunds processed to original payment method (RBI mandate)
- [ ] Rate limiting on payment endpoints
- [ ] Idempotency for webhooks (handle duplicates)
- [ ] Payment gateway PCI-DSS Level 1 certified (Razorpay is)

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 3-4 weeks (including testing)
