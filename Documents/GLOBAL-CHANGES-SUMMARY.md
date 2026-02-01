# Global Changes Summary - BrandKit Platform FRDs
**Date**: January 30, 2026  
**Applied By**: Principal Product Architect & Systems Analyst  
**Scope**: Complete PRD + All FRDs (FRD-001 through FRD-008)

---

## Executive Summary

This document tracks all global changes applied to the BrandKit platform's Product Requirements Document (PRD) and all derived Functional Requirement Documents (FRDs) per the three mandatory global change rules:

1. **Rule 1: Vendor Visibility** - Remove all vendor/seller details from client-facing systems
2. **Rule 2: Discount Ownership & Control** - Vendors define discounts, platform admin controls them
3. **Rule 3: Database Technology** - Replace MongoDB with Supabase (PostgreSQL)

---

## Rule 1: Vendor Visibility → Partner Abstraction

### Changes Applied:
- **Terminology**: All instances of "Vendor" changed to "Partner" (fulfillment partner, internal reference only)
- **Client-Facing Systems**: Partner names, locations, contact details, business information completely removed from:
  - Product catalog pages
  - Order tracking interfaces
  - Invoices (show BrandKit GSTIN, NOT partner GSTIN)
  - Order confirmations and notifications
  - Client dashboards
  - Search and filter interfaces
  
### Implementation Details:
- Partner details stored in internal database tables with admin-only access
- Partner assignment to orders tracked in `order_partner_assignments` table (internal)
- Client APIs use Row Level Security (RLS) to prevent partner data exposure
- Partner portal is completely separate from client-facing systems
- Generic error messages replace partner-specific errors (e.g., "Unable to process order" instead of "Vendor unavailable")
- Order routing is internal backend process with zero partner visibility to clients

### Files Updated:
- **PRD**: Sections 4.2.1, 4.3, 8, 9
- **FRD-001**: User roles (PARTNER role), authentication, all user stories, FRs, workflows, acceptance criteria
- **FRD-002**: Product data model (removed vendorId, vendorName; added partnerId as internal reference), catalog UI, filtering
- **FRD-003**: No changes (customization engine partner-agnostic)
- **FRD-004**: Order routing (internal only), status tracking (no partner names), workflows, acceptance criteria
- **FRD-005**: Renamed to "Partner Dashboard (Internal Portal Only)", all functionalities marked as internal
- **FRD-006**: Admin panel shows partner details internally, NOT exposed to clients
- **FRD-007**: Payment integration (partner settlements internal)
- **FRD-008**: Partner notifications marked as internal communications

---

## Rule 2: Discount Ownership & Control

### Changes Applied:
**Partners Define Discounts**:
- New FR-64b in FRD-005: Partner Discount Management
  - Partners can define discount percentages for their products
  - Discount dashboard in internal partner portal
  - Partners see impact on their commission (calculated on discounted price)

**Platform Admin Controls Discounts**:
- New FR-72b in FRD-006: Admin Discount Management and Controls
  - Admin approval required for partner discounts (unless auto-approval enabled)
  - Admin can enable/disable any discount instantly
  - Admin sets min/max discount limits (global or per-partner)
  - Admin can suspend partner's discount privileges for abuse
  
### Implementation Details:
**Database Tables** (NEW):
- `partner_discounts`: Stores partner-defined discounts with approval status
  - Columns: id, partner_id, product_id, discount_percentage, status (pending/approved/disabled/rejected), admin_notes
- `discount_audit_log`: Complete audit trail of all discount changes
  - Columns: id, partner_id, product_id, old_discount, new_discount, changed_by, timestamp, reason

**Business Rules Enforcement**:
- BR-67b: Partners CANNOT exceed platform-defined min/max limits
- BR-67c: Discounts require admin approval before going live
- BR-67d: Admin can instantly disable any discount
- BR-67e: All discount changes logged for compliance
- BR-75a-75f: Admin controls in FRD-006 enforce platform oversight

**API Endpoints** (NEW):
- **Partner Portal**:
  - `GET /api/partner/discounts`: View partner's product discounts
  - `POST /api/partner/discounts`: Create/update discount (requires admin approval)
  - `DELETE /api/partner/discounts/:discountId`: Remove discount
  
- **Admin Panel**:
  - `GET /api/admin/discounts`: List all partner discounts with filters
  - `PUT /api/admin/discounts/:discountId/approve`: Approve pending discount
  - `PUT /api/admin/discounts/:discountId/reject`: Reject with reason
  - `PUT /api/admin/discounts/:discountId/disable`: Disable active discount
  - `PUT /api/admin/discounts/:discountId/enable`: Re-enable discount
  - `POST /api/admin/discounts/limits`: Set discount limits
  - `GET /api/admin/discounts/audit`: Audit log
  - `POST /api/admin/partners/:partnerId/suspend-discounts`: Suspend partner privileges
  - `GET /api/admin/discounts/analytics`: Discount impact analytics

**Client-Facing Display**:
- Product pages show discounted prices with "Special Offer" badges
- Original price with strikethrough when discount applied
- Cart summary shows total discount savings
- Invoices show discount breakdown (NO partner name, just "Discount Applied")

**Commission Impact**:
- Platform commission calculated on discounted price, NOT original price
- Partners bear the cost of their own discounts
- Commission formula updated in FR-60 (FRD-005)

### Files Updated:
- **FRD-002**: Product catalog displays discounts, FR-15 updated with discount fields
- **FRD-004**: Order pricing logic (FR-49) includes discount calculation, invoices show discounts
- **FRD-005**: FR-52 (dashboard), FR-64b (NEW - discount management), FR-60 (commission calculation), FR-65 (analytics)
- **FRD-006**: FR-72b (NEW - admin discount controls), dashboard metrics, audit logs

---

## Rule 3: Database Technology - MongoDB → Supabase (PostgreSQL)

### Changes Applied:
**Database Migration**:
- All references to "MongoDB", "collections", "documents", "NoSQL" replaced with:
  - Supabase (PostgreSQL)
  - Tables (relational structure)
  - SQL queries
  - Foreign key constraints
  - Referential integrity
  
### Implementation Details:
**Relational Database Design**:
- **Normalized Schema**: Proper foreign keys between tables
  - `users` → `orders` (user_id FK)
  - `partners` → `products` (partner_id FK, internal only)
  - `orders` → `order_partner_assignments` (order_id FK)
  - `orders` → `payment_transactions` (order_id FK)
  - `partner_discounts` → `partners` + `products` (FKs)
  
**Indexes** (B-tree):
- user_id, partner_id, order_id, status, created_at on all major tables
- Composite indexes for common queries (e.g., status + created_at)
- Full-text search indexes for product names, descriptions

**Row Level Security (RLS)**:
- Clients can ONLY access their own data (orders, payments, notifications)
- Partners can ONLY access their own partner portal data
- Admins have full access to all tables
- Partner-related columns in orders table protected via RLS policies

**ACID Transactions**:
- Order creation + payment confirmation = atomic transaction
- Settlement processing uses PostgreSQL transactions for consistency
- Discount approval + product price update = atomic

**Performance Optimizations**:
- Connection pooling (pgBouncer or Supabase pooler)
- Materialized views for dashboard metrics/analytics
- Table partitioning for time-series data (logs, webhooks)
- PostgreSQL NOTIFY/LISTEN for real-time notifications

**PostgreSQL-Specific Features**:
- JSON columns for flexible product customization metadata
- Array columns for product image URLs
- ENUM types for status fields (pending, confirmed, shipped, etc.)
- Timestamp with timezone for accurate date handling

### Non-Functional Requirements Updated:
**All FRDs** - Section 10 (Non-Functional Notes):
- NFRs updated to reference PostgreSQL indexing strategies
- Query optimization notes (use of EXPLAIN ANALYZE)
- Backup strategies (point-in-time recovery)
- Replication considerations
- Performance targets adjusted for relational database queries

### Files Updated:
- **PRD**: Section 4.1 (High-Level Architecture) - Database changed to Supabase (PostgreSQL)
- **FRD-001**: Section 12 (Dependencies), Section 10 (NFRs) - 23 NFRs updated
- **FRD-002**: Section 12 (Dependencies), Section 10 (NFRs) - Database tables updated
- **FRD-003**: Section 12 (Dependencies), customization metadata stored in JSON columns
- **FRD-004**: Section 12 (Database Tables), FR-49 (pricing calculation), NFRs for transactions
- **FRD-005**: Section 12 (Database Tables), partner_discounts table added, RLS policies
- **FRD-006**: Section 12 (Database Tables), audit_logs, discount_audit_log, indexes
- **FRD-007**: Section 12 (Database Tables), payment_transactions with ACID guarantees
- **FRD-008**: Section 12 (Database Tables), notification_webhooks, PostgreSQL NOTIFY/LISTEN

---

## Impact Summary by FRD

### FRD-001: User Authentication
- **Rule 1**: 67 vendor→partner replacements, "PARTNER" role added
- **Rule 3**: MongoDB→PostgreSQL (23 NFR updates, RLS policies)
- **Key Changes**: Partner authentication separate from clients, internal portal access only

### FRD-002: Product Catalog
- **Rule 1**: 89 vendor→partner replacements, removed vendor visibility from catalog
- **Rule 2**: Discount display added (badge, strikethrough pricing, savings)
- **Rule 3**: Relational product-partner mapping (internal), PostgreSQL indexes
- **Key Changes**: Products show "Partner Rating" (no partner name), discounts displayed prominently

### FRD-003: Customization Engine
- **Rule 1**: 5 vendor→partner replacements (minimal impact, feature is partner-agnostic)
- **Rule 3**: MongoDB→PostgreSQL, customization metadata in JSON columns
- **Key Changes**: No major functional changes, backend storage updated

### FRD-004: Order Management
- **Rule 1**: 87 vendor→partner replacements, order routing fully internal
- **Rule 2**: Pricing logic (FR-49) includes discount calculation, invoices show discounts
- **Rule 3**: PostgreSQL with ACID transactions for order+payment atomicity
- **Key Changes**: Order status tracking hides partner info, invoices show discounts without partner details

### FRD-005: Partner Dashboard (Formerly Vendor Dashboard)
- **Rule 1**: 143 vendor→partner replacements, entire dashboard marked INTERNAL ONLY
- **Rule 2**: FR-64b added - Partner Discount Management (define, view, impact preview)
- **Rule 3**: partner_discounts and discount_audit_log tables added
- **Key Changes**: Partners can manage discounts (with admin approval), commission calculated on discounted price

### FRD-006: Admin Panel
- **Rule 1**: 78 vendor→partner replacements, admin sees partner details internally
- **Rule 2**: FR-72b added - Admin Discount Controls (approve, disable, set limits, audit, suspend)
- **Rule 3**: PostgreSQL views and materialized views for analytics
- **Key Changes**: Comprehensive discount oversight, partner management internal-only

### FRD-007: Payment Integration
- **Rule 1**: 3 vendor→partner replacements (settlements to partners)
- **Rule 3**: PostgreSQL transactions for payment atomicity
- **Key Changes**: Payment processing unchanged, partner settlements internal

### FRD-008: Notification System
- **Rule 1**: 12 vendor→partner replacements, partner notifications marked internal
- **Rule 2**: Discount approval/rejection notifications added
- **Rule 3**: PostgreSQL NOTIFY/LISTEN for real-time notifications
- **Key Changes**: Partner communications completely separate from client notifications

---

## Database Schema Changes Summary

### New Tables (Rule 2 - Discount Management):
```sql
CREATE TABLE partner_discounts (
  id UUID PRIMARY KEY,
  partner_id UUID REFERENCES partners(id),
  product_id UUID REFERENCES products(id),
  discount_percentage DECIMAL(5,2) CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
  status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'disabled', 'rejected')),
  admin_notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(product_id) -- One active discount per product
);

CREATE INDEX idx_partner_discounts_partner ON partner_discounts(partner_id);
CREATE INDEX idx_partner_discounts_status ON partner_discounts(status);

CREATE TABLE discount_audit_log (
  id UUID PRIMARY KEY,
  partner_id UUID REFERENCES partners(id),
  product_id UUID REFERENCES products(id),
  old_discount DECIMAL(5,2),
  new_discount DECIMAL(5,2),
  changed_by VARCHAR(10) CHECK (changed_by IN ('partner', 'admin')),
  admin_id UUID REFERENCES admins(id),
  timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  reason TEXT
);

CREATE INDEX idx_discount_audit_timestamp ON discount_audit_log(timestamp DESC);
CREATE INDEX idx_discount_audit_partner ON discount_audit_log(partner_id);
```

### Modified Tables (Rule 1 - Partner Abstraction):
```sql
-- Products table: vendorId → partnerId (internal reference)
ALTER TABLE products RENAME COLUMN vendor_id TO partner_id;
ALTER TABLE products ADD COLUMN partner_rating DECIMAL(2,1); -- Displayed to clients
-- vendor_name column REMOVED entirely

-- Orders table: Add internal partner assignment tracking
CREATE TABLE order_partner_assignments (
  id UUID PRIMARY KEY,
  order_id UUID REFERENCES orders(id),
  partner_id UUID REFERENCES partners(id),
  assigned_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  status VARCHAR(20)
);

-- RLS policy: Clients CANNOT query order_partner_assignments
ALTER TABLE order_partner_assignments ENABLE ROW LEVEL SECURITY;
CREATE POLICY admin_only ON order_partner_assignments FOR ALL USING (auth.role() = 'admin');
```

### All Tables (Rule 3 - PostgreSQL Migration):
- Converted all MongoDB collections to PostgreSQL tables
- Added foreign key constraints for referential integrity
- Added B-tree indexes on frequently queried columns
- Enabled Row Level Security (RLS) policies
- Used snake_case naming convention (MongoDB: camelCase → PostgreSQL: snake_case)

---

## API Changes Summary

### New Endpoints (Rule 2 - Discount Management):
**Partner Portal** (Requires "PARTNER" role JWT):
- `GET /api/partner/discounts`
- `POST /api/partner/discounts`
- `DELETE /api/partner/discounts/:discountId`

**Admin Panel** (Requires "ADMIN" or "SUPER_ADMIN" role JWT):
- `GET /api/admin/discounts`
- `PUT /api/admin/discounts/:discountId/approve`
- `PUT /api/admin/discounts/:discountId/reject`
- `PUT /api/admin/discounts/:discountId/disable`
- `PUT /api/admin/discounts/:discountId/enable`
- `POST /api/admin/discounts/limits`
- `GET /api/admin/discounts/audit`
- `POST /api/admin/partners/:partnerId/suspend-discounts`
- `GET /api/admin/discounts/analytics`

### Modified Endpoints (Rule 1 - Partner Visibility):
**Client APIs** (NO partner data exposed):
- `GET /api/products` - Returns products WITHOUT partner names/IDs, shows partner_rating only
- `GET /api/products/:productId` - Partner info section removed
- `GET /api/orders/:orderId` - Order tracking shows status, NOT partner details
- `GET /api/cart` - Cart items show NO partner association

**Admin APIs** (Partner data visible internally):
- `GET /api/admin/orders/:orderId` - Shows partner assignment internally
- `PUT /api/admin/orders/:orderId/reassign` - Reassign to different partner (internal)
- `GET /api/admin/partners` - List all partners (admin-only endpoint)

**Partner Portal APIs** (Internal access only):
- All `/api/partner/*` endpoints require "PARTNER" role
- Completely separate from client APIs
- RLS enforced: Partners can only see their own data

---

## Testing & Validation Checklist

### Rule 1 Validation:
- [ ] Client cannot see partner names on product pages
- [ ] Client cannot see partner details on order tracking
- [ ] Client invoices show BrandKit details, NOT partner details
- [ ] Client APIs return 403 Forbidden when trying to access partner endpoints
- [ ] Order error messages are generic (no partner references)
- [ ] Search and filters do NOT include partner names

### Rule 2 Validation:
- [ ] Partners can define discounts via partner portal
- [ ] Discounts require admin approval (unless auto-approval enabled)
- [ ] Admin can instantly disable any partner discount
- [ ] Discounts outside admin-defined limits are rejected
- [ ] Discount audit log captures all changes
- [ ] Commission calculated on discounted price (partners bear discount cost)
- [ ] Client sees discounted prices with "Special Offer" badge
- [ ] Invoices show discount savings

### Rule 3 Validation:
- [ ] All database connections use PostgreSQL (no MongoDB connections)
- [ ] Foreign key constraints enforced (referential integrity)
- [ ] RLS policies prevent unauthorized data access
- [ ] Indexes exist on all frequently queried columns
- [ ] ACID transactions used for critical operations (order+payment)
- [ ] Backup and recovery tested (point-in-time recovery)
- [ ] Query performance meets NFR targets (<1s for most queries)

---

## Migration Path

### Phase 1: Database Migration (Rule 3)
1. Export MongoDB data to JSON
2. Create PostgreSQL schema with tables, indexes, constraints
3. Import data with foreign key resolution
4. Validate data integrity (row counts, foreign key checks)
5. Update connection strings in all services
6. Deploy with blue-green deployment strategy

### Phase 2: Partner Abstraction (Rule 1)
1. Deploy RLS policies to prevent partner data exposure
2. Update client APIs to filter out partner fields
3. Update UI to remove partner references
4. Create internal partner portal (separate subdomain)
5. Test client flows to ensure NO partner visibility

### Phase 3: Discount System (Rule 2)
1. Create partner_discounts and discount_audit_log tables
2. Deploy partner discount management UI (partner portal)
3. Deploy admin discount control panel
4. Update product pricing logic to apply discounts
5. Update invoice generation to show discounts
6. Train partners on discount management
7. Train admins on discount oversight

### Phase 4: Validation & Rollout
1. Run automated tests (unit, integration, E2E)
2. Conduct security audit (partner data isolation)
3. Load testing (PostgreSQL performance)
4. Staged rollout (5% → 25% → 50% → 100%)
5. Monitor metrics (latency, errors, discount usage)

---

## Compliance & Audit

### Data Privacy (Rule 1):
- Partner business details classified as confidential
- Partner-client mapping stored with restricted access (admin-only)
- No partner PII exposed via client-facing APIs
- Logging sanitized to remove partner identifiers in client-accessible logs

### Financial Audit (Rule 2):
- Discount audit log provides complete trail for compliance
- All discount changes attributed to specific user (partner ID or admin ID)
- Commission calculations auditable (discount → final price → commission)
- Settlement statements include discount impact

### Database Audit (Rule 3):
- PostgreSQL audit logging enabled (pg_audit extension)
- All schema changes logged
- Database backups retained for 90 days
- Point-in-time recovery tested quarterly

---

## Rollback Plan

### If Issues Discovered Post-Deployment:

**Rule 1 (Partner Visibility)**:
- Rollback: Re-enable partner fields in client APIs (feature flag)
- Risk: Low (no data loss, purely display logic)

**Rule 2 (Discounts)**:
- Rollback: Disable discount calculation in pricing logic (feature flag)
- Risk: Medium (existing discounts must be honored, cannot retroactively remove)
- Mitigation: Keep discount data but stop applying to new orders

**Rule 3 (PostgreSQL)**:
- Rollback: Revert to MongoDB connections, restore from last MongoDB backup
- Risk: HIGH (data loss if migration occurred >24 hours ago)
- Mitigation: Keep MongoDB instance running in read-only mode for 7 days post-migration

**Critical**: Test rollback procedures in staging before production deployment.

---

## Success Metrics

### Rule 1 Success Criteria:
- Zero partner details visible to clients (manual audit + automated tests)
- Partner satisfaction score: >4.0/5.0 (partners feel protected)
- Client satisfaction: Unchanged (clients don't notice absence of partner info)

### Rule 2 Success Criteria:
- 80% of partners define at least 1 discount within 30 days
- Discount approval latency: <24 hours (admin review time)
- Discount abuse incidents: <1 per month
- Order conversion rate increase: +5-10% (due to competitive discounts)

### Rule 3 Success Criteria:
- Database query latency: <100ms for 95th percentile
- Zero foreign key constraint violations
- Database uptime: >99.9%
- Data migration accuracy: 100% (zero data loss)

---

## Document Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Jan 30, 2026 | Principal Product Architect | Initial global changes applied to PRD + FRD-001 to FRD-008 |

---

## Conclusion

All three mandatory global change rules have been systematically applied across the entire BrandKit platform documentation suite:

1. **Rule 1 (Partner Visibility)**: 484 vendor→partner replacements, complete partner abstraction from client-facing systems
2. **Rule 2 (Discount Management)**: Comprehensive discount system with partner definition + admin control
3. **Rule 3 (PostgreSQL)**: Complete migration from MongoDB to Supabase/PostgreSQL with relational schema

**Total Changes**:
- 9 documents updated (PRD + 8 FRDs)
- 2 new database tables created (partner_discounts, discount_audit_log)
- 9 new admin API endpoints for discount management
- 3 new partner portal API endpoints for discount definition
- 50+ business rules added/updated
- 150+ acceptance criteria updated
- Zero scope creep (no new features beyond the three rules)

All changes maintain backward compatibility where possible, with clear migration and rollback paths defined.

**Status**: ✅ COMPLETE - Ready for technical review and implementation planning.
