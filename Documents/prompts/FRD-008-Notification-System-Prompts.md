# FRD-008: Notification System - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the Multi-Channel Notification and Communication System with testing instructions after each prompt.

---

## Sub-Prompt 1: Email Service Integration (SendGrid)
**Task**: Set up SendGrid account and configure SMTP for email notifications.

**Implementation Details**:
- Register SendGrid account (sendgrid.com)
- Generate API Key with Mail Send permissions
- Verify sender email/domain (noreply@brandkit.com)
- Store API key in environment variable
- Configure SMTP settings: Host, Port, Username, Password
- Test connection: Send test email
- Admin settings: Email provider selection (SendGrid/AWS SES), credentials input

**Testing Instructions**:
1. Register SendGrid account
   - Expected: Account created, API key generated
2. Store in .env: SENDGRID_API_KEY, SENDGRID_FROM_EMAIL
3. Send test email: `POST /v3/mail/send`
   - Expected: Email delivered to test address
4. Verify sender email
   - Expected: "noreply@brandkit.com" verified in SendGrid dashboard
5. Admin enters credentials in settings
   - Expected: Settings saved, "Test Connection" button works
6. Test invalid API key
   - Expected: Error "Invalid API key"
7. Check SendGrid dashboard: Email stats (sent, delivered, opened)

---

## Sub-Prompt 2: SMS Service Integration (Twilio)
**Task**: Set up Twilio account and configure SMS notifications.

**Implementation Details**:
- Register Twilio account (twilio.com)
- Obtain Account SID and Auth Token
- Register Sender ID: "BRNKIT" (6-char alphanumeric, for India)
- Store credentials in environment variables
- Test SMS: Send to test number
- URL shortening: Bitly integration for tracking links
- Rate limiting: Max 5 SMS per user per hour

**Testing Instructions**:
1. Register Twilio account
   - Expected: Account created, credentials received
2. Register sender ID "BRNKIT"
   - Expected: Approved by telecom (may take 1-2 days)
3. Store in .env: TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_SENDER_ID
4. Send test SMS: `POST /Messages.json`
   - Expected: SMS delivered to test phone +91-9876543210
5. Test URL shortening: "Track: bit.ly/bk-track-123"
   - Expected: Shortened URL works, redirects to tracking page
6. Test rate limiting: Send 6 SMS to same user in 1 hour
   - Expected: 6th SMS blocked, queued for next hour
7. Check Twilio dashboard: SMS logs (sent, delivered, failed)

---

## Sub-Prompt 3: Notification Template System
**Task**: Create template management system with variable replacement.

**Implementation Details**:
- Database table: `notification_templates` (id, template_id, name, channel, subject, body, variables)
- Template variables: {userName}, {orderId}, {orderAmount}, {trackingLink}, etc.
- Create templates for all events: Order confirmed, shipped, delivered, etc.
- Variable replacement: Replace {variable} with actual data at send time
- Formatting: Support date formatting, number formatting (₹20,455.00)
- Admin interface: Template editor (HTML editor for email, plain text for SMS)

**Testing Instructions**:
1. Create template "ORDER_CONFIRMED_EMAIL"
   - Subject: "Order Confirmed - {orderId}"
   - Body: "Hi {userName}, Your order {orderId} for ₹{orderAmount} is confirmed."
2. Save template
   - Expected: Template stored in database
3. Send notification with data: {userName: "Rajesh", orderId: "BK-001", orderAmount: "20,455"}
   - Expected: Email sent "Hi Rajesh, Your order BK-001 for ₹20,455 is confirmed."
4. Test missing variable: {trackingLink} not provided
   - Expected: Fallback to empty string or default
5. Test number formatting: {orderAmount: 20455.50}
   - Expected: Displayed as "₹20,455.50"
6. Admin edits template via UI
   - Expected: Template updated, new emails use updated version
7. Create SMS template: "Order {orderId} shipped! Track: {trackingUrl} - BrandKit"

---

## Sub-Prompt 4: Notification Queue and Priority
**Task**: Implement notification queuing with priority handling.

**Implementation Details**:
- Queue: Redis or RabbitMQ
- Priority: High (immediate, <10s), Normal (batched, 1 min intervals)
- Worker: Background job processor (Spring @Async or separate service)
- High priority: Order confirmations, payment receipts, partner order alerts
- Normal priority: Marketing emails, cart reminders
- Retry mechanism: 3 retries with exponential backoff (1 min, 5 min, 15 min)
- Status tracking: Pending → Sent → Delivered/Failed

**Testing Instructions**:
1. Trigger high-priority notification: Order confirmed
   - Expected: Queued immediately, sent within 10 seconds
2. Trigger normal-priority: Cart reminder
   - Expected: Batched, sent within 1 minute
3. Worker processes queue
   - Expected: Notifications sent in priority order
4. Test failed send (email provider down)
   - Expected: Status "failed", queued for retry after 1 minute
5. Retry 3 times, all fail
   - Expected: Status "permanent_failure", admin alerted
6. Check Redis queue: View pending notifications
   - Expected: Queue contains pending items
7. Performance test: 1000 notifications queued
   - Expected: All processed without degradation

---

## Sub-Prompt 5: Email Notification Workflow
**Task**: Implement email sending workflow with tracking.

**Implementation Details**:
- Create notification record: userId, type, channel "email", template, variables, status "pending"
- Load template, replace variables, generate HTML
- Call SendGrid API: POST /v3/mail/send
- Store provider message ID
- Update status: "sent"
- Webhook: Receive delivery status (delivered, opened, bounced, spam)
- Update status based on webhook

**Testing Instructions**:
1. Trigger email: Order confirmed
   - Expected: Notification record created, status "pending"
2. Worker sends email via SendGrid
   - Expected: API call successful, message ID returned
3. Update status: "sent"
   - Expected: sentAt timestamp recorded
4. SendGrid webhook: "delivered"
   - Expected: deliveryStatus "delivered"
5. Test hard bounce (invalid email)
   - Expected: Webhook "bounced", email marked invalid
6. Test spam complaint
   - Expected: User auto-unsubscribed, notification disabled
7. Check email in inbox: Branding, content correct

---

## Sub-Prompt 6: SMS Notification Workflow
**Task**: Implement SMS sending workflow with character optimization.

**Implementation Details**:
- SMS limit: 160 characters (standard), avoid multi-part charges
- Create notification record: channel "sms", template, variables
- Load SMS template, replace variables
- URL shorten: Use Bitly for tracking links
- Call Twilio API: POST /Messages.json
- Store message SID
- Webhook: Delivery status (sent, delivered, failed)

**Testing Instructions**:
1. Trigger SMS: Order shipped
   - Expected: Notification record created
2. Template: "Order {orderId} shipped! Track: {trackingUrl} - BrandKit"
3. Variables: {orderId: "BK-001", trackingUrl: "https://brandkit.com/track/BK-001"}
4. URL shortened: bit.ly/bk-001
   - Expected: SMS text: "Order BK-001 shipped! Track: bit.ly/bk-001 - BrandKit" (58 chars)
5. Send via Twilio
   - Expected: SMS delivered to phone
6. Twilio webhook: "delivered"
   - Expected: Status updated
7. Test failed SMS (invalid phone)
   - Expected: Webhook "failed", phone marked invalid
8. Test character limit: Template > 160 chars
   - Expected: Warning "SMS exceeds 160 characters, may incur extra charges"

---

## Sub-Prompt 7: In-App Notification System
**Task**: Implement real-time in-app notifications with bell icon.

**Implementation Details**:
- Database table: `notifications` (id, user_id, type, title, message, link, read, created_at)
- Bell icon: Header component, badge shows unread count
- Dropdown panel: Last 20 notifications
- WebSocket: Real-time updates (or polling every 30s as fallback)
- Mark as read: Click notification or "Mark all as read"
- Notification history page: Full list with filters

**Testing Instructions**:
1. User logged in, order status updated
   - Expected: In-app notification created
2. WebSocket push notification
   - Expected: Bell icon badge updates "1"
3. User clicks bell icon
   - Expected: Dropdown displays notification
4. Notification content: "Order Shipped - BK-001"
   - Expected: Title, message, time ("2 hours ago")
5. Click notification
   - Expected: Navigate to order details, notification marked read
6. Badge updates: "0"
7. Test notification history page
   - Expected: All past notifications listed
8. Test "Mark all as read"
   - Expected: All unread marked read, badge clears

---

## Sub-Prompt 8: Notification Preferences
**Task**: Implement user notification preference management.

**Implementation Details**:
- Database table: `notification_preferences` (user_id, marketing_emails, cart_reminders, sms_order_updates, etc.)
- Settings page: Toggle switches for each category
- Mandatory: Order updates, account security (cannot disable)
- Optional: Marketing emails, cart reminders
- Unsubscribe link: In non-transactional emails, auto-updates preferences
- Respect preferences: Check before sending non-transactional notifications

**Testing Instructions**:
1. User navigates to Profile > Notification Preferences
   - Expected: Settings page displayed
2. Mandatory toggles grayed out (Order updates, Account security)
   - Expected: Cannot be disabled
3. User disables "Marketing emails"
   - Expected: Setting saved
4. Trigger marketing email event
   - Expected: Email NOT sent (preference respected)
5. Trigger order confirmation (transactional)
   - Expected: Email sent regardless of preferences
6. User clicks unsubscribe link in marketing email
   - Expected: Redirected to preferences page, marketing emails auto-disabled
7. Test SMS preferences: User disables "Order status SMS"
   - Expected: Only critical SMS sent (shipped, delivered)

---

## Sub-Prompt 9: Admin Notification Management
**Task**: Implement admin interface for notification logs and manual triggers.

**Implementation Details**:
- Notification logs page: Table (Notification ID, Recipient, Type, Channel, Status, Sent At, Delivery Status)
- Filters: Channel, Status (Sent/Failed/Pending), Recipient Type, Date Range
- Search: By notification ID, recipient email/phone, order ID
- Actions: View Details (full template, variables, provider response), Retry Send
- Manual trigger: Admin form (Recipient, Template, Variables, Channel)

**Testing Instructions**:
1. Admin navigates to Notifications > Logs
   - Expected: Table with all notifications
2. Filter by channel "email"
   - Expected: Only email notifications displayed
3. Filter by status "failed"
   - Expected: Failed notifications with failure reasons
4. Search by order ID "BK-001"
   - Expected: All notifications related to that order
5. Click notification, view details
   - Expected: Full template, variables, SendGrid/Twilio response
6. Retry failed notification
   - Expected: Notification resent
7. Manual trigger: Send custom email to client
   - Expected: Email sent via admin panel

---

## Sub-Prompt 10: Notification Error Handling and Retry
**Task**: Implement comprehensive error handling with retry logic.

**Implementation Details**:
- Email hard bounce: Mark email invalid, disable email notifications
- Email soft bounce: Retry 3 times
- SMS invalid number: Mark phone invalid, disable SMS
- SMS delivery failure: Retry 3 times
- Retry intervals: 1 min, 5 min, 15 min (exponential backoff)
- Permanent failure: After 3 retries, alert admin via email
- Rate limit exceeded: Queue for next hour
- Provider API down: Log error, retry automatically

**Testing Instructions**:
1. Send email to invalid address (user@invaliddomain.com)
   - Expected: Hard bounce, status "failed", email marked invalid
2. Send email, provider returns 500 error
   - Expected: Retry after 1 minute
3. Retry fails again
   - Expected: Retry after 5 minutes
4. Third retry fails
   - Expected: Status "permanent_failure", admin email sent
5. Send 6 SMS to same user in 1 hour
   - Expected: 6th SMS rate-limited, queued
6. SMS to invalid phone
   - Expected: Twilio returns error, phone marked invalid
7. SendGrid API down (simulate)
   - Expected: Error logged, notifications queued for retry
8. Provider back online
   - Expected: Queued notifications sent successfully

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Order Confirmation Flow**: Order Placed → Email Sent → SMS Sent → In-App Notification → All Delivered
2. **Partner Order Alert Flow**: Order Routed → Partner Email Sent → Partner SMS Sent → Partner Logs In → In-App Notification
3. **Shipment Notification Flow**: Order Shipped → Client Email → Client SMS → Tracking Link Works
4. **Admin Alert Flow**: Partner Rejects → Admin Email → Admin In-App Alert → Admin Takes Action
5. **Preference Respect Flow**: User Disables Marketing → Marketing Event Triggered → No Email Sent → Transactional Still Sent
6. **Retry Flow**: Email Fails → Retry 1 → Retry 2 → Retry 3 → Permanent Failure → Admin Alerted
7. **Unsubscribe Flow**: User Clicks Unsubscribe Link → Preferences Updated → Marketing Emails Stopped
8. **Manual Trigger Flow**: Admin Sends Custom Notification → Template Rendered → Email Sent → Client Receives

---

## Performance and Reliability Validation Checklist

- [ ] High-priority notifications sent within 10 seconds
- [ ] Email delivery via SendGrid within 30 seconds (provider SLA)
- [ ] SMS delivery via Twilio within 10 seconds (provider SLA)
- [ ] In-app notifications update in real-time (WebSocket) or within 30s (polling)
- [ ] System handles 1,000 notifications per minute
- [ ] Email deliverability > 95%
- [ ] SMS delivery success > 98%
- [ ] Notification queue (Redis) supports 10,000+ pending items
- [ ] Failed notifications retry automatically (3 attempts)
- [ ] Webhook signatures verified (SendGrid, Twilio)

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 3-4 weeks (including testing)
