# BrandKit - Functional Requirements Documents (FRDs)
## Index and Overview

**Project**: BrandKit B2B Promotional Merchandise Platform  
**Version**: 1.0  
**Date**: January 23, 2026  
**Status**: Ready for Implementation  

---

## Document Summary

This collection of Functional Requirement Documents (FRDs) breaks down the BrandKit Product Requirements Document (PRD) into **8 comprehensive, implementation-ready specifications** covering all MVP (Minimum Viable Product) features.

---

## FRD List

### 🔐 FRD-001: User Authentication and Registration System
**File**: `FRD-001-User-Authentication.md`  
**Priority**: High (Must-Have)  
**Owner**: Engineering  

**Covers**:
- Email and social (Google, LinkedIn) authentication
- Role-based access control (Client, Vendor, Admin)
- Password reset and recovery
- Email verification
- Session management (JWT)
- Account management

**Key Features**: 10 Functional Requirements | 10 Acceptance Criteria

---

### 📦 FRD-002: Product Catalog Management
**File**: `FRD-002-Product-Catalog.md`  
**Priority**: High (Must-Have)  
**Owner**: Product / Engineering  

**Covers**:
- Product catalog with categories (Bags, Pens, T-Shirts, etc.)
- Advanced filtering and search
- Dynamic pricing tiers
- Product detail pages with specifications
- Admin product management (add, edit, delete)
- Vendor association

**Key Features**: 14 Functional Requirements | 10 Acceptance Criteria

---

### 🎨 FRD-003: Customization Engine
**File**: `FRD-003-Customization-Engine.md`  
**Priority**: High (Must-Have)  
**Owner**: Engineering  

**Covers**:
- Logo upload (PNG, JPG, SVG)
- Image cropping with locked aspect ratio
- Client-side preview rendering (HTML5 Canvas)
- Server-side high-resolution image generation (300 DPI)
- Bundle builder (multiple products with same logo)
- Draft saving and loading

**Key Features**: 10 Functional Requirements | 10 Acceptance Criteria  
**Tech Stack**: React (react-easy-crop), Java (Spring Boot imaging)

---

### 🛒 FRD-004: Order Placement and Management
**File**: `FRD-004-Order-Management.md`  
**Priority**: High (Must-Have)  
**Owner**: Product / Engineering  

**Covers**:
- Shopping cart with persistence
- Multi-step checkout (address, delivery options, review)
- Delivery partner integration (Delhivery)
- Order confirmation and tracking
- GST-compliant invoice generation
- Automatic vendor order routing
- Reorder functionality

**Key Features**: 12 Functional Requirements | 10 Acceptance Criteria

---

### 🏭 FRD-005: Vendor Dashboard and Order Fulfillment
**File**: `FRD-005-Vendor-Dashboard.md`  
**Priority**: High (Must-Have)  
**Owner**: Product / Engineering  

**Covers**:
- Vendor order notifications (email, SMS, in-app)
- Order acceptance/rejection workflow
- Production status tracking
- Proof upload functionality
- Shipment creation with tracking
- Commission calculation and display
- Automated settlement payouts
- Vendor performance metrics

**Key Features**: 15 Functional Requirements | 10 Acceptance Criteria

---

### ⚙️ FRD-006: Admin Panel and Platform Management
**File**: `FRD-006-Admin-Panel.md`  
**Priority**: High (Must-Have)  
**Owner**: Product / Engineering  

**Covers**:
- Admin dashboard with key metrics
- User management (clients, vendors, admins)
- Vendor onboarding and bank verification
- Product management (CRUD operations)
- Order management and manual intervention
- Commission configuration (tiered structure)
- Settlement approval and processing
- Analytics dashboard (revenue, orders, users, vendors)
- System logs and audit trails

**Key Features**: 12 Functional Requirements | 10 Acceptance Criteria

---

### 💳 FRD-007: Payment Gateway Integration
**File**: `FRD-007-Payment-Integration.md`  
**Priority**: High (Must-Have)  
**Owner**: Engineering  

**Covers**:
- Razorpay/PayU integration
- Multiple payment methods (UPI, Cards, Net Banking, Wallets)
- Hosted payment page (PCI-DSS compliant)
- Payment verification (webhooks + API polling)
- Refund processing (full and partial)
- Failed payment retry mechanism
- Payment timeout handling
- Transaction tracking and analytics

**Key Features**: 13 Functional Requirements | 10 Acceptance Criteria  
**Compliance**: PCI-DSS, RBI guidelines

---

### 📬 FRD-008: Notification System
**File**: `FRD-008-Notification-System.md`  
**Priority**: High (Should-Have for MVP)  
**Owner**: Engineering  

**Covers**:
- Multi-channel notifications (Email, SMS, In-App)
- Transactional notifications (order updates, payment receipts)
- Operational notifications (vendor assignments, admin alerts)
- Notification templates with variable replacement
- Notification preferences and unsubscribe
- In-app notification panel with bell icon
- Delivery tracking and retry mechanism
- Admin notification management

**Key Features**: 12 Functional Requirements | 10 Acceptance Criteria  
**Providers**: SendGrid/AWS SES (Email), Twilio/MSG91 (SMS)

---

## Implementation Roadmap

### Phase 1: MVP Development (Months 1-3)

**Sprint 1-2 (Weeks 1-4): Foundation**
- FRD-001: User Authentication (2 weeks)
- Database schema design for all modules (1 week)
- Setup development environment, CI/CD (1 week)

**Sprint 3-4 (Weeks 5-8): Core Features**
- FRD-002: Product Catalog (2 weeks)
- FRD-007: Payment Integration (2 weeks)

**Sprint 5-6 (Weeks 9-12): Customization & Orders**
- FRD-003: Customization Engine (3 weeks)
- FRD-004: Order Management (3 weeks)

**Sprint 7-8 (Weeks 13-16): Vendor & Admin**
- FRD-005: Vendor Dashboard (2 weeks)
- FRD-006: Admin Panel (2 weeks)

**Sprint 9 (Weeks 17-18): Notifications & Testing**
- FRD-008: Notification System (1 week)
- Integration testing, bug fixes (1 week)

**Sprint 10 (Weeks 19-20): Beta Launch**
- User acceptance testing with 10 pilot clients (1 week)
- Performance optimization, security audit (1 week)

### Phase 2: Enhancements (Months 4-6)
- Reviews and ratings
- Recurring orders
- AI-powered recommendations
- Advanced analytics
- Mobile app development

---

## Technology Stack (from PRD)

### Frontend
- **Framework**: Next.js (TypeScript)
- **Styling**: Tailwind CSS
- **Animations**: Framer Motion
- **Image Handling**: react-easy-crop, HTML5 Canvas API

### Backend
- **Framework**: Java Spring Boot
- **Authentication**: JWT
- **REST APIs**: Spring Web
- **Image Processing**: Java Imaging Library

### Database
- **Primary**: MongoDB
- **Caching/Sessions**: Redis

### Integrations
- **Payment**: Razorpay/PayU
- **Email**: SendGrid/AWS SES
- **SMS**: Twilio/MSG91
- **Delivery**: Delhivery API
- **Cloud Storage**: AWS S3
- **CDN**: CloudFront/Cloudflare

---

## Success Metrics (from PRD)

### Acquisition
- 50 signups/month
- 20% conversion to orders

### Engagement
- 70% preview-to-cart rate
- Average order value: ₹15,000

### Retention
- 40% repeat clients quarterly

### Revenue
- ₹5 lakhs/month by Month 6
- Commission ROI >200%

### Operational
- Vendor fulfillment rate >95%
- NPS >8/10

---

## Key Business Rules Across FRDs

1. **No Minimum Order Quantity (MOQ)**: Clients can order from 1 unit (BR-20, FRD-002)
2. **Commission Structure**: 10-15% platform commission (configurable, BR-44, FRD-005)
3. **GST Compliance**: 18% GST on all orders (BR-32, FRD-004)
4. **Payment Timeout**: 15 minutes (BR-34, FRD-004; BR-67, FRD-007)
5. **Single Vendor Per Order**: MVP limitation (BR-36, FRD-004)
6. **Vendor Acceptance Deadline**: 24 hours (BR-41, FRD-005)
7. **Draft Expiry**: 30 days (BR-27, FRD-003)
8. **Settlement Schedule**: Monthly on 5th (BR-45, FRD-005)
9. **Print Resolution**: 300 DPI for print-ready images (BR-23, FRD-003)
10. **Notification Rate Limits**: 10 emails, 5 SMS per hour per user (BR-72, FRD-008)

---

## Critical Assumptions

1. **Target Market**: Gujarat-based B2B clients initially, pan-India by Year 2
2. **User Base**: Corporate procurement managers (30-45 years), event organizers (25-40 years)
3. **Vendor Network**: 5+ vendors per category pre-launch
4. **Budget**: ₹10-15 lakhs (dev + marketing)
5. **Tech Availability**: Spring Boot, Next.js developers available in India
6. **Payment Gateway**: Razorpay maintains 99.9% uptime
7. **Delivery**: Delhivery covers all serviceable PIN codes
8. **Language**: English UI for MVP (Hindi/Gujarati in Phase 2)
9. **Device**: Desktop-first, mobile-responsive (native app Phase 2)
10. **Internet**: Target users have reliable internet access

---

## Compliance & Security

### Regulatory Compliance
- **India's DPDP Act**: Data privacy and protection (NFR-13, FRD-001)
- **GST Act**: Invoice generation and tax compliance (NFR-92, FRD-004)
- **RBI Guidelines**: Payment processing regulations (NFR-149, FRD-007)
- **TRAI DND**: SMS opt-in compliance (NFR-174, FRD-008)

### Security Requirements
- **PCI-DSS Level 1**: Payment data security (via Razorpay, NFR-152, FRD-007)
- **SSL/TLS 1.3**: All communications encrypted (NFR-6, FRD-001; NFR-143, FRD-007)
- **Password Hashing**: bcrypt with 10 salt rounds (NFR-7, FRD-001)
- **JWT Tokens**: Access (15 min), Refresh (7 days) (FR-9, FRD-001)
- **Data Encryption**: AES-256 at rest for sensitive data (NFR-16, FRD-001)

---

## Next Steps for Development Team

### Immediate Actions
1. **Review all 8 FRDs** with stakeholders for feedback
2. **Setup development environment**: Repositories (GitHub), CI/CD (Jenkins/GitHub Actions), Cloud (AWS/Heroku)
3. **Database schema design**: MongoDB collections for users, products, orders, etc.
4. **API contract definition**: REST endpoints for frontend-backend communication
5. **UI/UX wireframes**: Design screens for all user flows (Figma/Adobe XD)

### Week 1 Tasks
- [ ] Provision cloud infrastructure (AWS/Heroku)
- [ ] Setup MongoDB cluster
- [ ] Configure Redis for caching
- [ ] Initialize Next.js frontend repo
- [ ] Initialize Spring Boot backend repo
- [ ] Create Razorpay account (test mode)
- [ ] Create SendGrid account
- [ ] Create Twilio account

### Week 2 Tasks
- [ ] Implement FRD-001: User authentication APIs
- [ ] Design user database schema
- [ ] Build registration and login UI
- [ ] Setup JWT token generation
- [ ] Implement password hashing

---

## FRD Quality Checklist

Each FRD in this collection includes:
- ✅ Clear, atomic, testable functional requirements
- ✅ Detailed workflow descriptions with edge cases
- ✅ Comprehensive input/output specifications
- ✅ Validation and business rules
- ✅ Error handling scenarios
- ✅ Non-functional requirements (performance, security, scalability)
- ✅ Acceptance criteria in Gherkin-style format
- ✅ Dependencies and integration points
- ✅ Assumptions and constraints
- ✅ Phase 2 enhancement considerations

---

## Document Maintenance

- **Owner**: Product Management Team
- **Review Cycle**: Bi-weekly during development
- **Change Management**: Version control via Git, changelog per FRD
- **Feedback Loop**: Weekly sync with engineering team to clarify requirements

---

## Contact & Support

For questions or clarifications on any FRD:
- **Product Manager**: product@brandkit.com
- **Technical Lead**: tech@brandkit.com
- **Documentation**: This repository

---

**End of Index**

_All FRDs are living documents and will be updated based on stakeholder feedback, technical discoveries, and changing business requirements._
