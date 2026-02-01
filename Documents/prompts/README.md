# BrandKit Development Prompts - Index

## Overview
This folder contains 8 comprehensive development prompt files, each breaking down a Functional Requirements Document (FRD) into 10 actionable sub-prompts with detailed testing instructions.

## Purpose
These prompts are designed to:
- **Simplify Development**: Break complex FRDs into manageable, sequential tasks
- **Ensure Quality**: Each sub-prompt includes comprehensive testing instructions
- **Enable Easy Understanding**: Clear implementation details and expected outcomes
- **Facilitate Testing**: Step-by-step test cases after each implementation

## Prompt Files

### 1. [FRD-001: User Authentication](./FRD-001-User-Authentication-Prompts.md)
**Focus**: User Registration, Login, OAuth (Google/LinkedIn), Password Management, Session Management, RBAC

**10 Sub-Prompts**:
1. Database Schema Setup
2. Email-Based Registration API
3. Email Verification Workflow
4. Google OAuth Integration
5. LinkedIn OAuth Integration
6. Login Endpoint with JWT
7. Password Reset Workflow
8. Session Management and Token Refresh
9. Role-Based Access Control (RBAC)
10. User Profile Management

**Estimated Time**: 3-4 weeks

---

### 2. [FRD-002: Product Catalog](./FRD-002-Product-Catalog-Prompts.md)
**Focus**: Product Management, Search/Filters, Pricing, Partner Associations (Internal), Discounts

**10 Sub-Prompts**:
1. Product Database Schema (with Partner Associations - Internal Only)
2. Product Listing API with Filters (NO Partner Info Exposed)
3. Product Search with Autocomplete
4. Product Detail API (Partner Details Remain Internal)
5. Admin Product Management UI
6. Dynamic Price Calculator
7. Partner Discount Management (Admin Control)
8. Product Image Management and CDN Integration
9. Product Quick View Modal
10. Product SEO and Structured Data

**Estimated Time**: 3-4 weeks

---

### 3. [FRD-003: Customization Engine](./FRD-003-Customization-Engine-Prompts.md)
**Focus**: Logo Upload, Cropping, Client-Side Preview, Server-Side High-Res Rendering, Bundle Builder

**10 Sub-Prompts**:
1. Logo Upload Component (Frontend)
2. Logo Cropping Tool with react-easy-crop
3. Client-Side Preview Rendering (HTML5 Canvas)
4. Server-Side High-Resolution Rendering (Backend)
5. Print Area Configuration (Admin)
6. Bundle Builder Workflow
7. Draft Customization Save/Load
8. Download Preview Image
9. Multi-Product Logo Application
10. Customization Validation

**Estimated Time**: 4-5 weeks

---

### 4. [FRD-004: Order Management](./FRD-004-Order-Management-Prompts.md)
**Focus**: Cart, Checkout, Order Tracking, Partner Routing (Internal), Invoicing

**10 Sub-Prompts**:
1. Shopping Cart System
2. Cart Validation Before Checkout
3. Multi-Step Checkout Flow
4. GST-Compliant Invoice Generation (with Discount Display, NO Partner Details)
5. Partner Order Routing (Internal Process - NOT Visible to Clients)
6. Order Status Tracking (Client View - NO Partner Info)
7. Delivery Partner Integration
8. Order Cancellation and Refund
9. Price Calculation with Discounts
10. Reorder Functionality

**Estimated Time**: 4-5 weeks

---

### 5. [FRD-005: Partner Dashboard](./FRD-005-Partner-Dashboard-Prompts.md)
**Focus**: Partner Portal (INTERNAL ONLY), Order Acceptance, Production Tracking, Settlements, Discounts

**CRITICAL**: This entire dashboard is INTERNAL. Partner information NEVER exposed to clients.

**10 Sub-Prompts**:
1. Partner Authentication and Access Control (Internal Portal Only)
2. Partner Dashboard Home (Internal Portal)
3. Order Notification System (Internal Communications)
4. Order List View (Partner Portal)
5. Order Acceptance/Rejection Workflow
6. Production Status Updates (Internal Portal)
7. Proof Image Upload (Internal Partner Action)
8. Shipment Creation (Internal Partner Action)
9. Commission and Settlement Dashboard (Internal Partner View)
10. Partner Discount Management (Internal Portal)

**Estimated Time**: 4-5 weeks

---

### 6. [FRD-006: Admin Panel](./FRD-006-Admin-Panel-Prompts.md)
**Focus**: User Management, Partner Management, Product Management, Order Intervention, Discount Control, Analytics

**10 Sub-Prompts**:
1. Admin Role-Based Access Control
2. Admin Dashboard Home
3. User Management Interface
4. Partner Management and Onboarding (Internal Only)
5. Product Management with Partner Assignment (Internal)
6. Order Management and Manual Intervention
7. Commission Configuration
8. Partner Discount Management and Control (Rule 2)
9. Settlement Management and Approval
10. Analytics Dashboard

**Estimated Time**: 5-6 weeks

---

### 7. [FRD-007: Payment Integration](./FRD-007-Payment-Integration-Prompts.md)
**Focus**: Razorpay Integration, Payment Session, Webhooks, Refunds, Transaction Storage

**10 Sub-Prompts**:
1. Payment Gateway Configuration (Razorpay)
2. Payment Session Initiation
3. Razorpay Hosted Checkout Page
4. Webhook Payment Verification
5. API Polling Fallback
6. Payment Status Handling
7. Refund Processing
8. Failed Payment Retry Mechanism
9. Payment Transaction Storage
10. Payment Analytics Dashboard (Admin)

**Estimated Time**: 3-4 weeks

---

### 8. [FRD-008: Notification System](./FRD-008-Notification-System-Prompts.md)
**Focus**: Email (SendGrid), SMS (Twilio), In-App Notifications, Templates, Preferences

**10 Sub-Prompts**:
1. Email Service Integration (SendGrid)
2. SMS Service Integration (Twilio)
3. Notification Template System
4. Notification Queue and Priority
5. Email Notification Workflow
6. SMS Notification Workflow
7. In-App Notification System
8. Notification Preferences
9. Admin Notification Management
10. Notification Error Handling and Retry

**Estimated Time**: 3-4 weeks

---

## How to Use These Prompts

### For Developers:
1. **Sequential Implementation**: Follow sub-prompts in order (1-10) for each FRD
2. **Test After Each Step**: Complete testing instructions before moving to next sub-prompt
3. **Reference Original FRDs**: Refer to `/Documents/FRDS/` for full specifications
4. **Integration Testing**: After all 10 sub-prompts, complete the integration testing checklist

### For Project Managers:
1. **Task Breakdown**: Use sub-prompts to create Jira/Trello tasks
2. **Time Estimation**: Each sub-prompt = 2-4 days of work (including testing)
3. **Progress Tracking**: Check off sub-prompts as completed
4. **Quality Assurance**: Ensure all testing instructions are executed

### For QA Engineers:
1. **Test Case Creation**: Each sub-prompt's testing instructions = test cases
2. **Regression Testing**: Re-run tests from previous sub-prompts after new implementations
3. **Integration Testing**: Use integration checklists at end of each FRD
4. **Security Validation**: Follow security checklists (especially for Partner info protection)

---

## Critical Security Notes

### Partner Information Protection
**CRITICAL**: Partner details (names, locations, contact info) are STRICTLY INTERNAL.

- Partner information NEVER exposed in client-facing APIs, UI, or communications
- Partner assignments stored in admin-only database tables
- Client order confirmations contain NO partner references
- Invoices do NOT include partner GSTIN or names
- Order tracking shows generic statuses, NOT partner-specific actions

**Violation of this rule is a CRITICAL security breach.**

### Discount Management (Rule 2)
- Partners DEFINE discounts (within platform limits)
- Platform admins CONTROL discounts (approve/reject/disable)
- All discount changes logged for audit
- Admin can override any partner discount setting

---

## Total Development Timeline

**MVP Complete**: ~30-35 weeks (7-8 months) for full-stack team of 4-6 developers

**Breakdown**:
- FRD-001: 3-4 weeks
- FRD-002: 3-4 weeks
- FRD-003: 4-5 weeks
- FRD-004: 4-5 weeks
- FRD-005: 4-5 weeks
- FRD-006: 5-6 weeks
- FRD-007: 3-4 weeks
- FRD-008: 3-4 weeks

**Note**: Timeline assumes parallel development streams (frontend/backend teams working simultaneously)

---

## Tech Stack Reference

### Frontend
- **Framework**: Next.js (SSG + SSR)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Animations**: Framer Motion
- **Logo Crop**: react-easy-crop
- **Preview**: HTML5 Canvas API

### Backend
- **Framework**: Java Spring Boot
- **Language**: Java
- **APIs**: REST (JWT Authentication)
- **Image Processing**: Java Imaging Library

### Database
- **Primary**: Supabase with PostgreSQL
- **Features**: Row-Level Security (RLS), Relational Schema, Indexes, Foreign Keys
- **Caching**: Redis (session storage, rate limiting)

### External Services
- **Payment**: Razorpay (primary) or PayU
- **Email**: SendGrid or AWS SES
- **SMS**: Twilio or MSG91
- **Storage**: AWS S3
- **CDN**: CloudFront or Cloudflare

---

## Support and Questions

For technical questions or clarifications:
1. Review the original FRD in `/Documents/FRDS/`
2. Check the PRD in `/Documents/PRD` for high-level context
3. Consult with technical lead or architect
4. Document blockers and raise in daily standup

---

**Document Status**: Ready for Development  
**Last Updated**: January 31, 2026  
**Maintained By**: Development Team
