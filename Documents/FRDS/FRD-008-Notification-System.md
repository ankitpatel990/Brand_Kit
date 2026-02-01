# FRD-008: Notification System

---

## 1. FRD METADATA

**FRD Title**: Multi-Channel Notification and Communication System  
**FRD ID**: FRD-008  
**Related PRD Section(s)**: 4.2.2 Enhanced Features - Notifications  
**Priority**: High (Should-Have for MVP)  
**Owner**: Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Develop a reliable, multi-channel notification system that keeps clients, partners, and admins informed of important events through email, SMS, and in-app notifications, ensuring timely communication for order updates, system alerts, and transactional messages.

### Business Value
- Keep clients informed of order progress, building trust
- Enable partners to respond quickly to order assignments
- Alert admins to critical issues requiring intervention
- Reduce support inquiries through proactive communication
- Improve user engagement and retention
- Comply with transactional communication requirements
- Enhance user experience through timely, relevant notifications

---

## 3. SCOPE

### In Scope
- Email notifications via SMTP provider (SendGrid, AWS SES)
- SMS notifications via SMS provider (Twilio, MSG91)
- In-app notifications (bell icon with notification panel)
- Notification types:
  - **Transactional**: Order confirmation, shipment updates, payment receipts
  - **Operational**: Partner order assignments, admin alerts
  - **System**: Password resets, email verification, account changes
- Notification templates (HTML email, SMS text, in-app cards)
- Template variables (order ID, user name, amount, etc.)
- Notification preferences (user settings: enable/disable non-transactional notifications)
- Notification history (user can view past notifications)
- Delivery tracking (sent, delivered, failed status)
- Retry mechanism for failed deliveries
- Admin notification management (view logs, manually trigger notifications)
- Email branding (BrandKit logo, colors, footer)

### Out of Scope
- Push notifications (mobile app feature, Phase 2)
- WhatsApp Business API notifications (Phase 2)
- Real-time chat/messaging (Phase 2)
- Notification scheduling (send at specific time) - all immediate for MVP
- Marketing/promotional emails (separate email marketing tool)
- Advanced personalization (AI-powered content) - basic template variables only
- Notification analytics (open rates, click rates) - Phase 2
- Multi-language notifications (English only for MVP)
- Voice call notifications (IVR for critical alerts) - Phase 2
- Slack/Teams integration for admin alerts - Phase 2

---

## 4. USER STORIES

### Client Users
- **US-079**: As a Client, I want to receive email confirmation when I place an order so that I have proof of purchase
- **US-080**: As a Client, I want to receive SMS when my order ships so that I can track delivery
- **US-081**: As a Client, I want to see in-app notifications for order updates so that I stay informed without checking email
- **US-082**: As a Client, I want to disable marketing emails but keep order notifications so that I'm not spammed
- **US-083**: As a Client, I want to view my notification history so that I can reference past messages

### Partner Users
- **US-084**: As a Partner, I want to receive email and SMS when I get a new order so that I can respond quickly
- **US-085**: As a Partner, I want in-app notifications for urgent order updates so that I don't miss important alerts

### Admin Users
- **US-086**: As an Admin, I want to receive alerts for failed payments so that I can investigate issues
- **US-087**: As an Admin, I want to manually trigger notifications so that I can communicate with users if needed
- **US-088**: As an Admin, I want to view notification delivery logs so that I can troubleshoot delivery failures

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-91: Notification Channels
The system shall support three notification channels:

**Email**:
- Provider: SendGrid (primary) or AWS SES (alternative)
- Format: HTML emails with responsive design
- From Address: noreply@brandkit.com
- From Name: BrandKit Team
- Reply-To: support@brandkit.com (for transactional emails)
- Includes unsubscribe link (for non-transactional emails)

**SMS**:
- Provider: Twilio (primary) or MSG91 (alternative)
- Format: Plain text, max 160 characters (standard SMS) or 640 characters (long SMS)
- Sender ID: BRNKIT (6-character alphanumeric, registered with telecom)
- Only for Indian mobile numbers (+91-XXXXXXXXXX)
- URL shortening for links (e.g., track order links)

**In-App**:
- Notification bell icon in header (all pages)
- Badge showing unread count
- Dropdown panel with recent notifications (last 20)
- Notification types: Info, Success, Warning, Error (color-coded)
- Click notification to navigate to relevant page
- Mark as read / Mark all as read functionality

### FR-92: Notification Event Triggers
The system shall send notifications for the following events:

**Client Notifications**:
1. **Account**:
   - Registration successful (Email: Welcome message)
   - Email verification (Email: Verification link)
   - Password reset requested (Email: Reset link)
   - Password changed successfully (Email: Security alert)
   - Account deactivated (Email: Deactivation notice)

2. **Orders**:
   - Order confirmed (Email + SMS: Order details, invoice PDF)
   - Payment failed (Email: Failure reason, retry link)
   - Order accepted by partner (Email: Production started)
   - Order in production (Email: Status update)
   - Proof ready (Email: View proof link)
   - Order shipped (Email + SMS: Tracking ID, tracking link)
   - Out for delivery (SMS: Delivery ETA)
   - Order delivered (Email + SMS: Delivery confirmation)
   - Order cancelled (Email: Cancellation reason, refund info)
   - Refund processed (Email: Refund amount, timeline)

3. **Draft/Cart**:
   - Draft saved (Email: Draft saved confirmation)
   - Cart abandoned (Email: Reminder after 24 hours, "Complete your order")

**Partner Notifications** (INTERNAL COMMUNICATIONS ONLY - NOT visible to clients):
1. **Orders** (Internal partner portal notifications):
   - New order assigned (Email + SMS + In-App: Order details, accept/reject links via internal partner portal)
   - Order reassigned to you (Email + In-App: New assignment notification)
   - Order cancelled by client (Email: Cancellation notice, NO client contact info shared)
   - Settlement processed (Email: Settlement statement PDF, internal financial communication)
   - Settlement failed (Email: Failure reason, action required via partner portal)
   - Discount approved/rejected by admin (Email + In-App: Admin decision on discount proposal)

2. **Performance** (Internal partner metrics):
   - Low fulfillment rate warning (Email: Performance alert, internal metric tracking)
   - Bank details verified (Email: Verification confirmation, internal process)
   - Discount suspended for abuse (Email: Admin action notification, policy violation)

**Admin Notifications**:
1. **Alerts** (Email + In-App):
   - Partner rejected order (Alert: Reassignment required)
   - Payment gateway error (Alert: System issue)
   - Failed settlement (Alert: Manual intervention required)
   - Low-performing partner (Alert: Review required)
   - New user registration (Info: Monitoring)
   - High-value order (>₹50,000) (Info: Monitoring)

### FR-93: Notification Templates
The system shall use templates for consistent messaging:

**Template Structure**:
- **Template ID**: Unique identifier (e.g., "ORDER_CONFIRMED")
- **Template Name**: Human-readable (e.g., "Order Confirmation")
- **Channel**: Email / SMS / In-App
- **Subject** (Email only): Template with variables
- **Body**: HTML (email), Plain text (SMS), Markdown (in-app)
- **Variables**: Placeholders replaced with dynamic data
  - {userName}, {userEmail}, {orderId}, {orderAmount}, {trackingLink}, {invoiceUrl}, etc.
- **Priority**: High (immediate send) / Normal (queue batching)

**Example Email Template** (Order Confirmation):
```html
Subject: Order Confirmed - {orderId}

Hi {userName},

Thank you for your order! Your order has been confirmed and is being prepared.

Order Details:
- Order ID: {orderId}
- Total Amount: ₹{orderAmount}
- Estimated Delivery: {estimatedDelivery}

[View Order] [Download Invoice]

We'll notify you when your order ships.

Best regards,
BrandKit Team

---
BrandKit | support@brandkit.com | +91-XXXXXXXXXX
```

**Example SMS Template** (Order Shipped):
```
Your order {orderId} has shipped! Track here: {trackingUrl} - BrandKit
```

### FR-94: Template Variable Replacement
The system shall dynamically replace template variables:
- **Syntax**: `{variableName}` in template
- **Replacement**: At notification send time, replace with actual data
- **Example**:
  - Template: "Hi {userName}, your order {orderId} for ₹{orderAmount} is confirmed."
  - Data: {userName: "Rajesh", orderId: "BK-20260123-001", orderAmount: "20,455"}
  - Output: "Hi Rajesh, your order BK-20260123-001 for ₹20,455 is confirmed."
- **Fallback**: If variable missing, replace with empty string or default (e.g., {userName} → "Customer")
- **Formatting**: Support number formatting (₹20,455.00), date formatting (Jan 23, 2026)

### FR-95: Notification Delivery
The system shall reliably deliver notifications:

**Sending Logic**:
1. **Trigger Event**: System event occurs (e.g., order confirmed)
2. **Create Notification Record**:
   - notificationId (UUID)
   - userId (recipient)
   - type (order_confirmed)
   - channel (email/sms/in-app)
   - template ID
   - variables (JSON object)
   - status (pending)
   - priority (high/normal)
   - createdAt (timestamp)
3. **Queue Notification**:
   - High priority: Send immediately (within 10 seconds)
   - Normal priority: Batch send (every 1 minute, up to 100 notifications)
4. **Render Template**:
   - Load template for notification type and channel
   - Replace variables with actual data
   - Generate final message (HTML email, SMS text)
5. **Send via Provider**:
   - **Email**: Call SendGrid API `POST /v3/mail/send`
   - **SMS**: Call Twilio API `POST /2010-04-01/Accounts/{AccountSid}/Messages.json`
   - **In-App**: Store in database, push to user's notification panel (WebSocket or polling)
6. **Update Status**:
   - If send successful: status = "sent", sentAt = timestamp
   - If send failed: status = "failed", failureReason, retryCount++
7. **Retry Failed**:
   - Retry up to 3 times with exponential backoff (1 min, 5 min, 15 min)
   - After 3 failures: status = "permanent_failure", alert admin

**Delivery Tracking**:
- **Email**: SendGrid webhook reports: delivered, opened, clicked, bounced, spam
- **SMS**: Twilio webhook reports: sent, delivered, failed, undelivered
- **In-App**: Marked delivered when user logs in and notification appears

### FR-96: Notification Preferences (Client)
Clients shall manage notification preferences:

**Preference Settings Page**:
- **Email Preferences**:
  - Order updates (mandatory, cannot disable)
  - Account security (mandatory)
  - Marketing emails (optional, toggle on/off)
  - Cart reminders (optional, toggle on/off)
- **SMS Preferences**:
  - Critical updates (order shipped, delivered) (mandatory)
  - Order status updates (optional)
- **In-App Notifications**: Always enabled (non-intrusive)

**Preference Enforcement**:
- Transactional emails (order confirmation, payment receipt) always sent (legal requirement)
- Non-transactional emails (marketing, cart reminders) respect preferences
- SMS only sent for enabled categories or mandatory events
- Unsubscribe link in all non-transactional emails updates preferences automatically

### FR-97: In-App Notification Panel
Users shall access in-app notifications:

**Notification Bell Icon**:
- Located in header (top right), visible on all pages after login
- Badge showing unread count (e.g., "3")
- Click to open dropdown panel

**Notification Panel**:
- Dropdown overlay, max-width 400px, max-height 500px
- Header: "Notifications" with "Mark all as read" link
- List of notifications (last 20):
  - Each notification shows:
    - Icon (color-coded by type: blue=info, green=success, yellow=warning, red=error)
    - Title (e.g., "Order Confirmed")
    - Short message (e.g., "Your order BK-20260123-001 is confirmed")
    - Time (e.g., "2 hours ago", "Jan 23")
    - Unread indicator (blue dot)
  - Click notification: Mark as read, navigate to related page (e.g., order details)
- Footer: "View All" link (navigates to full notification history page)
- Empty state: "No new notifications"
- Real-time update: New notifications appear without page refresh (via WebSocket or polling every 30s)

**Notification History Page**:
- Full list of all notifications (paginated, 50 per page)
- Filters: Type (All, Orders, Account, System), Status (All, Unread, Read)
- Search: By order ID, keyword
- Bulk actions: Mark all as read, Delete read notifications

### FR-98: Admin Notification Management
Admins shall manage notifications:

**Notification Logs Page**:
- Table with columns: Notification ID, Recipient, Type, Channel, Status, Sent At, Delivery Status
- Filters: Channel, Status (Sent, Failed, Pending), Recipient Type (Client/Partner/Admin), Date Range
- Search: By notification ID, recipient email/phone, order ID
- Actions per notification:
  - View Details (full template, variables, delivery logs)
  - Retry Send (if failed)
  - View Provider Response (SendGrid/Twilio API response)

**Manual Notification Trigger**:
- Admin can manually send notification to specific user
- Form:
  - Recipient: User selector (dropdown or email input)
  - Template: Dropdown (select from predefined templates)
  - Variables: JSON input field (provide template variables)
  - Channel: Email / SMS / In-App
- Click "Send Notification"
- System validates, renders, sends notification
- Use case: Admin wants to send custom order update to client

**Notification Analytics** (Basic):
- Metrics cards:
  - Total Notifications Sent (This Month)
  - Delivery Success Rate (%)
  - Failed Notifications (count)
  - Average Delivery Time
- Charts:
  - Notifications by Type (pie chart)
  - Delivery Trend (line graph, last 30 days)

### FR-99: Email Branding and Design
Emails shall be professionally designed:

**Email Template Design**:
- **Header**:
  - BrandKit logo (centered, 200px width)
  - Brand colors: Primary blue (#2563eb), Secondary gray (#6b7280)
- **Body**:
  - Responsive HTML (mobile-friendly, 600px max width)
  - Clean typography: Sans-serif font (Arial, Helvetica)
  - Clear call-to-action buttons (blue, rounded, 44px height)
  - Order details in table format (bordered, easy to read)
  - Icons for visual interest (checkmark for success, warning for alerts)
- **Footer**:
  - BrandKit contact info (email, phone, address)
  - Social media links (placeholder for Phase 2)
  - Legal links: Privacy Policy, Terms & Conditions
  - Unsubscribe link (non-transactional emails only)
  - Copyright notice: "© 2026 BrandKit Pvt. Ltd."

**Email Testing**:
- Templates tested across email clients (Gmail, Outlook, Apple Mail, mobile apps)
- Dark mode compatibility (images with transparent backgrounds, text readable)

### FR-100: SMS Optimization
SMS shall be concise and effective:

**SMS Best Practices**:
- **Length**: Keep under 160 characters to avoid multi-part SMS charges
- **Clarity**: Clear, actionable message
- **Branding**: End with "- BrandKit" for brand recognition
- **Links**: Use URL shortener (bit.ly, BrandKit custom domain) to save characters
- **Timing**: Send during business hours (9 AM - 8 PM IST) unless critical

**Example Optimized SMS**:
- Original: "Dear Customer, Your order BK-20260123-001 has been shipped via Delhivery. You can track your order using this link: https://www.delhivery.com/track/shipment/DELIV12345678. Thank you for choosing BrandKit!"
- Optimized: "Order BK-20260123-001 shipped! Track: bit.ly/bk-track-123 - BrandKit" (65 chars)

### FR-101: Notification Rate Limiting
The system shall prevent notification spam:
- **Rate Limits**:
  - Email: Max 10 emails per user per hour (prevent accidental loops)
  - SMS: Max 5 SMS per user per hour (cost control)
  - In-App: No limit (non-intrusive)
- **Exception**: Critical notifications (order confirmed, payment receipts) bypass rate limits
- **If limit exceeded**: Queue notification for next hour, log warning, alert admin

### FR-102: Error Handling and Retries
The system shall handle delivery failures:

**Failure Scenarios**:
- **Email**:
  - Hard bounce (invalid email): Mark email invalid, notify admin, disable email notifications for user
  - Soft bounce (mailbox full): Retry 3 times
  - Spam complaint: Immediately unsubscribe user, log incident
- **SMS**:
  - Invalid phone number: Mark phone invalid, notify admin
  - Carrier rejection (number blocked): Disable SMS for user
  - Delivery failed (network issue): Retry 3 times
- **In-App**: Store in database, always succeeds (displayed on next login)

**Retry Logic**:
- Retry 1: After 1 minute
- Retry 2: After 5 minutes
- Retry 3: After 15 minutes
- After 3 failures: Mark permanent_failure, alert admin (email to admin@brandkit.com)

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Client Places Order - Confirmation Notification
1. Client completes payment for order BK-20260123-001 (₹20,455)
2. Payment successful, order status updated to "confirmed"
3. System triggers notification event: `ORDER_CONFIRMED`
4. Notification service receives event with data:
   - userId: uuid-client-123
   - userEmail: rajesh@company.com
   - userPhone: +91-9876543210
   - userName: Rajesh Kumar
   - orderId: BK-20260123-001
   - orderAmount: 20455
   - estimatedDelivery: Feb 5-12, 2026
   - invoiceUrl: https://s3.brandkit.com/invoices/INV-2026-0001.pdf
5. System creates 3 notification records (email, SMS, in-app):

**Email Notification**:
- templateId: "ORDER_CONFIRMED_EMAIL"
- channel: "email"
- priority: "high"
- status: "pending"

6. System queues email notification (high priority, send immediately)
7. Notification worker picks up notification within 10 seconds
8. Worker loads template "ORDER_CONFIRMED_EMAIL":
   - Subject: "Order Confirmed - {orderId}"
   - Body: HTML template with order details
9. Worker replaces variables:
   - {userName} → Rajesh Kumar
   - {orderId} → BK-20260123-001
   - {orderAmount} → 20,455
   - {estimatedDelivery} → Feb 5-12, 2026
   - {invoiceUrl} → https://...
10. Worker generates final email HTML
11. Worker calls SendGrid API:
    - `POST /v3/mail/send`
    - Payload: {to: "rajesh@company.com", from: "noreply@brandkit.com", subject: "...", html: "..."}
12. SendGrid returns success: {message_id: "sg-msg-123"}
13. Worker updates notification record:
    - status: "sent"
    - providerMessageId: "sg-msg-123"
    - sentAt: 2026-01-23T12:05:30Z
14. 2 minutes later, SendGrid webhook: Email delivered
15. System updates notification: deliveryStatus: "delivered"

**SMS Notification**:
16. System queues SMS notification
17. Worker loads template "ORDER_CONFIRMED_SMS":
    - Body: "Order {orderId} confirmed for ₹{orderAmount}. Track: {trackingUrl} - BrandKit"
18. Worker replaces variables, generates SMS text (68 chars)
19. Worker calls Twilio API:
    - `POST /Messages.json`
    - Payload: {To: "+919876543210", From: "BRNKIT", Body: "Order BK-20260123-001 confirmed..."}
20. Twilio returns success: {sid: "SM-twilio-456"}
21. Worker updates notification: status: "sent", sentAt: timestamp
22. 30 seconds later, Twilio webhook: SMS delivered
23. System updates notification: deliveryStatus: "delivered"

**In-App Notification**:
24. System creates notification record in database:
    - userId: uuid-client-123
    - type: "order_confirmed"
    - title: "Order Confirmed"
    - message: "Your order BK-20260123-001 for ₹20,455 is confirmed"
    - link: "/orders/BK-20260123-001"
    - read: false
    - createdAt: timestamp
25. Client is currently logged in (WebSocket connected)
26. System pushes notification via WebSocket
27. Client's browser receives notification, updates bell icon badge: 1
28. Client clicks bell icon, sees notification in dropdown
29. Client clicks notification, navigates to order details page, notification marked read

**Edge Cases**:
- If email bounces (invalid email): Mark status "failed", reason "hard_bounce", no retry
- If SMS fails (invalid number): Mark status "failed", reason "invalid_phone", no retry
- If SendGrid API timeout: Retry after 1 minute (up to 3 times)
- If client not logged in: In-app notification stored, displayed on next login

### Workflow 2: Partner Receives New Order - Multi-Channel Alert
1. Admin routes order BK-20260123-002 to partner "PrintMaster Gujarat"
2. System triggers notification event: `VENDOR_NEW_ORDER`
3. Notification service receives event with data:
   - partnerId: uuid-partner-123
   - partnerEmail: partner@printmaster.com
   - partnerPhone: +91-9123456780
   - partnerName: PrintMaster Gujarat
   - orderId: BK-20260123-002
   - productName: Water Bottle × 100
   - orderAmount: 15000
   - acceptanceDeadline: Jan 24, 2026 12:00 PM
   - acceptLink: https://brandkit.com/partner/orders/BK-20260123-002/accept
4. System creates email, SMS, and in-app notifications

**Email**:
5. Worker loads template "VENDOR_NEW_ORDER_EMAIL"
6. Replaces variables
7. Sends via SendGrid:
   - Subject: "New Order BK-20260123-002 - 100 Water Bottles"
   - Body: HTML with order details, "Accept Order" and "Reject Order" buttons
8. Email sent successfully

**SMS**:
9. Worker loads template "VENDOR_NEW_ORDER_SMS"
10. Replaces variables: "New order BK-20260123-002 for ₹15,000. Login to review: bit.ly/bk-v-123 - BRNKIT"
11. Sends via Twilio to +91-9123456780
12. SMS delivered

**In-App**:
13. Creates notification in database, type: "partner_new_order", priority: "high"
14. Partner is currently on dashboard
15. WebSocket pushes notification, bell icon badge updates: 1
16. Partner clicks bell, sees: "New Order BK-20260123-002"
17. Partner clicks notification, navigates to order details, accepts order

**Edge Cases**:
- If email fails (soft bounce): Retry 3 times with backoff
- If SMS fails (carrier rejection): Log failure, send in-app notification only, alert admin
- If partner doesn't see in-app notification (offline): Email and SMS ensure they're informed

### Workflow 3: Admin Alert - Settlement Failed
1. Automated settlement process fails for partner "Gujarat Jute Co." (invalid bank account)
2. System triggers notification event: `ADMIN_SETTLEMENT_FAILED`
3. Notification service receives event with data:
   - adminId: uuid-admin-001 (Super Admin)
   - adminEmail: admin@brandkit.com
   - partnerName: Gujarat Jute Co.
   - settlementId: SET-2026-02-001
   - settlementAmount: 220000
   - failureReason: "Invalid account number"
4. System creates email and in-app notifications (no SMS for admin alerts)

**Email**:
5. Worker loads template "ADMIN_SETTLEMENT_FAILED_EMAIL"
6. Replaces variables
7. Sends via SendGrid:
   - Subject: "[URGENT] Settlement Failed - Gujarat Jute Co."
   - Body: HTML with alert details, "Review Settlement" button
   - Priority: High (red flag in subject)
8. Email sent to admin@brandkit.com

**In-App**:
9. Creates notification: type: "admin_alert", severity: "error" (red color-coded)
10. Admin is logged in, WebSocket pushes notification
11. Bell icon badge updates with red dot (critical alert)
12. Admin clicks bell, sees: "Settlement Failed - Gujarat Jute Co."
13. Admin clicks notification, navigates to settlement detail page
14. Admin reviews failure reason, corrects partner's bank account, retries settlement

**Edge Cases**:
- If admin not logged in: Email ensures they're alerted
- If email fails: In-app notification stored, admin sees on next login, plus retry email delivery
- If multiple admins exist: Send to all admins with role "Super Admin"

### Workflow 4: Client Changes Notification Preferences
1. Client logs into account
2. Client navigates to Profile > Notification Preferences
3. System displays preference settings:
   - Order updates: ✓ Enabled (mandatory, grayed out)
   - Account security: ✓ Enabled (mandatory, grayed out)
   - Marketing emails: ✓ Enabled (toggle)
   - Cart reminders: ✓ Enabled (toggle)
   - SMS - Order shipped: ✓ Enabled (toggle)
4. Client disables "Marketing emails" (toggles off)
5. Client clicks "Save Preferences"
6. System updates user preferences in database:
   - marketingEmailsEnabled: false
7. System displays success toast: "Preferences saved"
8. Next time a marketing email event triggers (e.g., promotional offer):
   - System checks user preferences: marketingEmailsEnabled = false
   - System skips email notification for this user
9. Transactional emails (order updates) continue to be sent (not affected by preferences)

**Edge Cases**:
- If user clicks unsubscribe link in email: Automatically disables that category (e.g., marketing emails)
- If user tries to disable mandatory notifications: UI prevents action, shows tooltip "This notification cannot be disabled"

---

## 7. INPUT & OUTPUT

### Inputs

#### Notification Event Data
| Field | Type | Description |
|-------|------|-------------|
| eventType | String | E.g., "ORDER_CONFIRMED", "VENDOR_NEW_ORDER" |
| userId | UUID | Recipient user ID |
| userEmail | Email | Recipient email |
| userPhone | Tel | Recipient phone (+91-XXXXXXXXXX) |
| userName | String | Recipient name |
| templateVariables | JSON Object | Dynamic data for template (orderId, amount, etc.) |
| priority | String | "high" (immediate) or "normal" (batched) |
| channels | Array | ["email", "sms", "in-app"] |

#### Manual Notification Trigger (Admin)
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Recipient Email/User ID | Email or UUID | Valid user | Yes |
| Template | Dropdown | Existing template ID | Yes |
| Variables (JSON) | JSON | Valid JSON object | Yes |
| Channel | Multi-select | Email/SMS/In-App | Yes |

### Outputs

#### Notification Record (Database)
```json
{
  "notificationId": "uuid-notif-123",
  "userId": "uuid-user-456",
  "type": "order_confirmed",
  "channel": "email",
  "templateId": "ORDER_CONFIRMED_EMAIL",
  "variables": {
    "userName": "Rajesh Kumar",
    "orderId": "BK-20260123-001",
    "orderAmount": "20,455"
  },
  "subject": "Order Confirmed - BK-20260123-001",
  "body": "Hi Rajesh Kumar, Thank you for your order...",
  "status": "sent",
  "providerMessageId": "sg-msg-123",
  "priority": "high",
  "sentAt": "2026-01-23T12:05:30Z",
  "deliveryStatus": "delivered",
  "deliveredAt": "2026-01-23T12:06:00Z",
  "failureReason": null,
  "retryCount": 0,
  "createdAt": "2026-01-23T12:05:00Z"
}
```

#### In-App Notification (API Response)
```json
{
  "status": "success",
  "data": {
    "notifications": [
      {
        "notificationId": "uuid-notif-789",
        "type": "order_shipped",
        "title": "Order Shipped",
        "message": "Your order BK-20260123-001 has shipped!",
        "link": "/orders/BK-20260123-001",
        "icon": "truck",
        "severity": "info",
        "read": false,
        "createdAt": "2026-01-30T10:00:00Z"
      }
    ],
    "unreadCount": 3
  }
}
```

#### SendGrid API Call (Email)
```json
POST https://api.sendgrid.com/v3/mail/send
Headers: {
  "Authorization": "Bearer SG.xxxxxxxxxxxx",
  "Content-Type": "application/json"
}
Body: {
  "personalizations": [{
    "to": [{"email": "rajesh@company.com", "name": "Rajesh Kumar"}]
  }],
  "from": {"email": "noreply@brandkit.com", "name": "BrandKit Team"},
  "reply_to": {"email": "support@brandkit.com"},
  "subject": "Order Confirmed - BK-20260123-001",
  "content": [{
    "type": "text/html",
    "value": "<html>...</html>"
  }]
}
```

#### Twilio API Call (SMS)
```json
POST https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
Headers: {
  "Authorization": "Basic base64(AccountSid:AuthToken)"
}
Body: {
  "To": "+919876543210",
  "From": "BRNKIT",
  "Body": "Order BK-20260123-001 shipped! Track: bit.ly/bk-track-123 - BrandKit"
}
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-71: Transactional Notification Immunity
Transactional notifications (order confirmation, payment receipts, password resets) must always be sent, regardless of user preferences. Only marketing/promotional notifications respect opt-out preferences.

### BR-72: Notification Rate Limiting
Maximum 10 emails and 5 SMS per user per hour to prevent spam. Critical notifications (order confirmed, payment receipts) bypass rate limits.

### BR-73: Mandatory Fields in Notifications
All notifications must include: Recipient (email/phone/userId), Event Type, Template ID, Priority. Missing fields cause notification creation failure.

### BR-74: SMS Character Limit
SMS messages limited to 160 characters for standard SMS. Longer messages split into multiple SMS (charged per part). Encourage templates under 160 chars.

### BR-75: Email Deliverability
Hard bounces (invalid email) disable email notifications for that user. Soft bounces (mailbox full) trigger up to 3 retries before failure.

### BR-76: In-App Notification Retention
In-app notifications retained for 30 days. After 30 days, notifications auto-deleted (except unread critical notifications).

### BR-77: Notification Priority Queueing
High-priority notifications (order updates, partner assignments) sent immediately (within 10 seconds). Normal-priority (promotional, reminders) batched every 1 minute.

### BR-78: Admin Alert Recipients
Admin alerts sent to all users with role "Super Admin". Operations Admins receive in-app notifications but not email alerts (to reduce noise).

### BR-79: Webhook Verification
All delivery status webhooks (SendGrid, Twilio) verified using signature/authentication to prevent spoofing.

### BR-80: Unsubscribe Enforcement
If user unsubscribes from a category via email link, preferences updated immediately and no more emails sent for that category within 1 minute.

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | Message | Admin Action |
|----------|-----------|---------|--------------|
| Email hard bounce (invalid email) | NOTIF_001 | "Email invalid, notifications disabled" | Review user email, update if needed |
| SMS invalid phone number | NOTIF_002 | "Phone number invalid, SMS disabled" | Review user phone, update if needed |
| SendGrid API failure | NOTIF_003 | "Email provider unavailable, retrying" | Check SendGrid status, retry |
| Twilio API failure | NOTIF_004 | "SMS provider unavailable, retrying" | Check Twilio status, retry |
| Template not found | NOTIF_005 | "Notification template missing" | Create missing template |
| Template rendering error (variable missing) | NOTIF_006 | "Failed to render template, missing variable" | Fix template or provide missing variable |
| Rate limit exceeded | NOTIF_007 | "Notification rate limit exceeded for user" | Review notification triggers, check for loops |
| Delivery timeout (no webhook after 1 hour) | NOTIF_008 | "Delivery status unknown" | Check provider dashboard manually |
| Spam complaint | NOTIF_009 | "User marked email as spam, unsubscribed" | Review email content, improve quality |
| Permanent failure (3 retries exhausted) | NOTIF_010 | "Notification delivery failed permanently" | Admin manually contacts user or investigates |

### Error Handling Strategy
- Log all failures with detailed error messages server-side
- Retry transient errors (API timeouts, network issues) up to 3 times
- For permanent errors (invalid email/phone), update user record to prevent future failures
- Alert admin via email if critical notifications fail (order confirmations)
- Display user-friendly error messages for manual notification triggers (admin)
- Never expose provider API keys or secrets in error logs

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-157**: High-priority notifications sent within 10 seconds of event trigger
- **NFR-158**: Email delivery via SendGrid within 30 seconds (provider SLA)
- **NFR-159**: SMS delivery via Twilio within 10 seconds (provider SLA)
- **NFR-160**: In-app notification polling every 30 seconds (if WebSocket not available)
- **NFR-161**: Notification history page loads within 1.5 seconds (50 notifications)

### Scalability
- **NFR-162**: System must handle 1,000 notifications per minute during peak
- **NFR-163**: Notification queue (Redis/RabbitMQ) must support 10,000+ pending notifications
- **NFR-164**: Email and SMS APIs must handle burst traffic (100 requests/second)

### Reliability
- **NFR-165**: Notification delivery success rate >98%
- **NFR-166**: Webhook processing must be idempotent (handle duplicate webhooks)
- **NFR-167**: Failed notifications retried automatically with exponential backoff
- **NFR-168**: Critical notifications (order confirmed) have 99.5% delivery guarantee

### Security
- **NFR-169**: Email templates sanitized to prevent XSS (escape user-generated content)
- **NFR-170**: SMS content sanitized to prevent injection attacks
- **NFR-171**: Webhook endpoints verify signatures (SendGrid: HMAC, Twilio: signature validation)
- **NFR-172**: API keys for SendGrid/Twilio stored in environment variables, encrypted at rest

### Compliance
- **NFR-173**: Unsubscribe link included in all non-transactional emails (CAN-SPAM Act)
- **NFR-174**: SMS sent only to users who provided phone numbers (TRAI DND compliance)
- **NFR-175**: User preferences honored within 1 minute of change (GDPR-like compliance)

### Monitoring
- **NFR-176**: Notification delivery rates monitored in real-time (dashboard)
- **NFR-177**: Alerts triggered if delivery success rate drops below 95% in any 1-hour window
- **NFR-178**: Failed notifications logged and reviewed daily by admin

---

## 11. ACCEPTANCE CRITERIA

### AC-71: Send Order Confirmation Email
**Given** client places order BK-20260123-001 and payment succeeds  
**When** order status updates to "confirmed"  
**Then** system triggers ORDER_CONFIRMED notification  
**And** creates email notification with template "ORDER_CONFIRMED_EMAIL"  
**And** replaces template variables (userName, orderId, orderAmount)  
**And** sends email via SendGrid within 10 seconds  
**And** email delivered to client's inbox  
**And** notification status updated to "sent" and "delivered"

### AC-72: Send Partner New Order SMS
**Given** new order BK-20260123-002 routed to partner "PrintMaster Gujarat"  
**When** partner assignment completes  
**Then** system triggers VENDOR_NEW_ORDER notification  
**And** creates SMS notification with template "VENDOR_NEW_ORDER_SMS"  
**And** sends SMS via Twilio to partner's phone  
**And** SMS delivered within 10 seconds  
**And** partner receives SMS: "New order BK-20260123-002 for ₹15,000. Login to review: [link] - BRNKIT"

### AC-73: Display In-App Notification
**Given** client logged into dashboard  
**When** order status updates to "shipped"  
**Then** system creates in-app notification  
**And** pushes notification via WebSocket to client's browser  
**And** bell icon badge updates to show unread count  
**When** client clicks bell icon  
**Then** dropdown panel displays notification: "Order Shipped - BK-20260123-001"  
**When** client clicks notification  
**Then** browser navigates to order details page  
**And** notification marked as read

### AC-74: Respect Notification Preferences
**Given** client has disabled "Marketing emails" in preferences  
**When** system triggers marketing email event (promotional offer)  
**Then** system checks user preferences: marketingEmailsEnabled = false  
**And** system skips email notification for this user  
**And** no email sent  
**When** system triggers transactional email event (order confirmed)  
**Then** email sent regardless of preferences (mandatory notification)

### AC-75: Retry Failed Email Delivery
**Given** SendGrid API returns 500 error (service unavailable)  
**When** system attempts to send email notification  
**Then** email marked as "failed" with retryCount = 1  
**And** system retries after 1 minute  
**When** retry also fails  
**Then** retryCount = 2, retry after 5 minutes  
**When** third retry succeeds  
**Then** email sent, status updated to "sent"  
**When** third retry also fails  
**Then** status = "permanent_failure", admin receives alert email

### AC-76: Handle Email Hard Bounce
**Given** email sent to invalid address (user@invaliddomain.com)  
**When** SendGrid webhook reports "hard bounce"  
**Then** system updates notification: deliveryStatus = "bounced"  
**And** updates user record: emailValid = false  
**And** disables email notifications for this user  
**And** admin notified via dashboard alert: "User email invalid"

### AC-77: Admin Manually Triggers Notification
**Given** admin logged into Admin Panel  
**When** admin navigates to Notifications > Send Manual Notification  
**And** fills form:
- Recipient: rajesh@company.com
- Template: ORDER_SHIPPED
- Variables: {"orderId": "BK-20260123-001", "trackingId": "DELIV12345"}
- Channel: Email
**And** clicks "Send Notification"  
**Then** system validates input  
**And** creates notification record  
**And** renders template with provided variables  
**And** sends email via SendGrid  
**And** displays success "Notification sent successfully to rajesh@company.com"

### AC-78: Display Notification History
**Given** client navigates to Profile > Notification History  
**Then** system displays list of past notifications (last 50)  
**And** shows: Type, Title, Date, Status (Read/Unread)  
**When** client filters by Type: "Orders"  
**Then** list updates to show only order-related notifications  
**When** client clicks "Mark all as read"  
**Then** all unread notifications marked as read  
**And** bell icon badge resets to 0

### AC-79: Admin Views Notification Analytics
**Given** admin navigates to Notifications > Analytics  
**Then** system displays:
- Total Notifications Sent (This Month): 1,245
- Delivery Success Rate: 97.8%
- Failed Notifications: 27
- Notifications by Type: Pie chart (Orders 60%, Account 20%, Partner 15%, Admin 5%)
- Delivery Trend: Line graph (last 30 days)
**When** admin clicks "Failed Notifications"  
**Then** system displays list of 27 failed notifications with failure reasons

### AC-80: Enforce SMS Rate Limit
**Given** client triggers multiple order events in quick succession (testing)  
**When** 6 SMS notifications triggered for same user within 1 hour  
**Then** system sends first 5 SMS (rate limit: 5/hour)  
**And** 6th SMS marked as "rate_limited", queued for next hour  
**And** admin receives alert: "SMS rate limit exceeded for user [email]"

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Email Provider**: SendGrid (API v3) or AWS SES
  - SendGrid Webhooks: delivered, opened, clicked, bounced, spam_report
- **SMS Provider**: Twilio (API 2010-04-01) or MSG91
  - Twilio Webhooks: sent, delivered, failed, undelivered
- **URL Shortener**: Bitly API (for SMS link shortening)
- **WebSocket**: Socket.io or native WebSockets for real-time in-app notifications

### Internal Dependencies
- **FRD-001 (Authentication)**: User data (email, phone, name)
- **FRD-004 (Order Management)**: Order events (confirmed, shipped, delivered)
- **FRD-005 (Partner Dashboard)**: Partner events (new order, settlement)
- **FRD-006 (Admin Panel)**: Admin alerts (order issues, system errors)
- **FRD-007 (Payment)**: Payment events (success, failed, refund)

### Database Tables (Supabase PostgreSQL)
- `notifications`: Notification records (all channels, indexed by user_id, status, created_at)
- `notification_preferences`: User notification preferences (foreign key to users table)
- `notification_templates`: Email/SMS/In-App templates (versioned for A/B testing)
- `notification_webhooks`: Delivery status webhooks (audit log, time-series data, consider partitioning by date)
- **Indexes**: B-tree indexes on user_id, type, status, created_at for efficient notification retrieval
- **Row Level Security**: Users can only access their own notifications. Partners can only access their partner portal notifications.
- **Performance**: Use PostgreSQL NOTIFY/LISTEN for real-time in-app notifications (WebSocket alternative)

### API Endpoints
- `POST /api/notifications/send`: Trigger notification (internal/admin)
- `GET /api/notifications/user/:userId`: Get user's notifications (in-app)
- `PUT /api/notifications/:notificationId/read`: Mark notification as read
- `PUT /api/notifications/read-all`: Mark all as read for user
- `GET /api/notifications/preferences`: Get user's notification preferences
- `PUT /api/notifications/preferences`: Update user's notification preferences
- `POST /api/notifications/webhook/sendgrid`: SendGrid webhook endpoint
- `POST /api/notifications/webhook/twilio`: Twilio webhook endpoint
- `GET /api/admin/notifications/logs`: Notification logs (admin)
- `GET /api/admin/notifications/analytics`: Notification analytics (admin)
- `POST /api/admin/notifications/manual-send`: Manually trigger notification (admin)

### Environment Variables
```
SENDGRID_API_KEY=SG.xxxxxxxxxxxxxxxxxxxx
SENDGRID_FROM_EMAIL=noreply@brandkit.com
SENDGRID_FROM_NAME=BrandKit Team
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=xxxxxxxxxxxxxxxxxxxx
TWILIO_SENDER_ID=BRNKIT
NOTIFICATION_QUEUE_URL=redis://localhost:6379 (or RabbitMQ)
WEBSOCKET_SERVER_URL=wss://brandkit.com/ws
```

---

## 13. ASSUMPTIONS

1. Clients and partners have valid, accessible email addresses
2. Most clients/partners in Gujarat have smartphones with data/WiFi for in-app notifications
3. SendGrid/Twilio maintain 99.9% uptime as per SLA
4. Email deliverability remains >95% (dependent on sender reputation)
5. SMS delivery in India is reliable (major carriers: Airtel, Jio, VI, BSNL)
6. Clients check email within 24 hours for important order updates
7. Partners respond to SMS alerts within 1 hour for urgent order assignments
8. WebSocket connections stable for most users (fallback to polling if needed)
9. English language notifications acceptable for target audience (Hindi Phase 2)
10. Notification costs (SendGrid, Twilio) are within budget (estimated ₹5,000/month for MVP)

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Push notifications (mobile app: iOS, Android)
- WhatsApp Business API integration (order updates via WhatsApp)
- Real-time chat/messaging (client-partner, client-support)
- Notification scheduling (send at specific time, e.g., next business day)
- Advanced personalization (AI-powered content, user behavior-based)
- Notification analytics: Open rates, click rates, conversion tracking
- A/B testing for notification content (optimize messaging)
- Multi-language support (Hindi, Gujarati for regional clients)
- Voice call notifications (IVR for critical alerts, e.g., payment failures)
- Slack/Microsoft Teams integration (admin alerts to team channels)
- Notification templates visual editor (admin can edit templates via UI)
- Rich media in emails (embedded videos, interactive elements)
- Smart notification grouping (bundle multiple updates into one digest)
- Predictive send times (AI determines best time to send based on user behavior)
- Two-way SMS (clients can reply to SMS to perform actions, e.g., "TRACK" to get tracking link)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: SendGrid account setup, Twilio account setup, notification queue (Redis/RabbitMQ) configuration, email template design, WebSocket implementation for real-time in-app notifications
