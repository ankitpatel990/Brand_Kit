# FRD-001: User Authentication - Development Prompts

## Overview
This document contains 10 sub-prompts for implementing the User Registration and Authentication System with testing instructions after each prompt.

---

## Sub-Prompt 1: Database Schema Setup
**Task**: Create the user authentication database schema in Supabase PostgreSQL with Row-Level Security (RLS).

**Implementation Details**:
- Create `users` table with columns: id (UUID), email (unique), password_hash, full_name, company_name, phone, user_type (enum: client/partner/admin), status (enum: active/inactive/pending_verification), email_verified (boolean), created_at, updated_at
- Create `user_sessions` table for JWT refresh tokens
- Create `password_resets` table for password reset tokens
- Create `email_verifications` table for email verification tokens
- Implement Row-Level Security policies
- Create indexes on email, user_type, status

**Testing Instructions**:
1. Run schema migration scripts
2. Verify all tables created: `SELECT * FROM information_schema.tables WHERE table_name IN ('users', 'user_sessions', 'password_resets', 'email_verifications')`
3. Test constraints: Try inserting duplicate email (should fail)
4. Test enum values: Insert user with invalid user_type (should fail)
5. Verify RLS policies: Login as different roles and test data access
6. Check indexes: `SELECT * FROM pg_indexes WHERE tablename = 'users'`

---

## Sub-Prompt 2: Email-Based Registration API
**Task**: Implement the email-based registration endpoint with validation and password hashing.

**Implementation Details**:
- Create Spring Boot REST endpoint: `POST /api/auth/register`
- Validate input: email format, password strength (min 8 chars, uppercase, lowercase, number, special char)
- Check email uniqueness
- Hash password using bcrypt (salt rounds: 10)
- Create user record with status "pending_verification"
- Generate email verification token (UUID, 24h expiry)
- Return success response

**Testing Instructions**:
1. Test valid registration: `POST /api/auth/register` with valid data
   - Expected: 201 Created, user created with pending_verification status
2. Test duplicate email: Register same email twice
   - Expected: 409 Conflict, error "Email already registered"
3. Test weak password: Use password "12345678"
   - Expected: 400 Bad Request, password validation error
4. Test invalid email: Use "notanemail"
   - Expected: 400 Bad Request, email format error
5. Test missing fields: Omit company_name
   - Expected: 400 Bad Request, missing required field
6. Verify password hashing: Check database - password should be bcrypt hash, not plain text
7. Verify token generation: Check email_verifications table for token entry

---

## Sub-Prompt 3: Email Verification Workflow
**Task**: Implement email verification token generation, sending, and verification endpoint.

**Implementation Details**:
- Generate verification email with link containing token
- Send email via SendGrid/AWS SES
- Create verification endpoint: `GET /api/auth/verify-email?token={token}`
- Validate token (check expiry, uniqueness)
- Update user status to "active"
- Mark token as used
- Redirect to login page with success message

**Testing Instructions**:
1. Register new user, check email inbox for verification link
2. Click verification link
   - Expected: User status updated to "active" in database
3. Try using same token again
   - Expected: 400 Bad Request, "Token already used"
4. Generate token, wait 25 hours, try verification
   - Expected: 400 Bad Request, "Token expired"
5. Test with invalid token
   - Expected: 400 Bad Request, "Invalid token"
6. Test "Resend Verification" functionality
   - Expected: New token generated, old token invalidated
7. Verify email delivery: Check SendGrid dashboard for sent emails

---

## Sub-Prompt 4: Google OAuth Integration
**Task**: Integrate Google OAuth 2.0 for social authentication.

**Implementation Details**:
- Register application with Google Cloud Console, obtain Client ID and Secret
- Implement OAuth flow: `GET /api/auth/google` redirects to Google consent screen
- Implement callback: `GET /api/auth/google/callback` receives auth code
- Exchange code for access token via Google API
- Retrieve user profile (email, name, picture)
- Check if email exists: If yes, login; if no, create user with "active" status
- Generate JWT tokens
- Redirect to dashboard

**Testing Instructions**:
1. Click "Continue with Google" button
   - Expected: Redirect to Google consent screen
2. Authorize application
   - Expected: Callback receives code, user logged in or registered
3. Test with existing email (registered via email/password)
   - Expected: Link accounts, user can login with both methods
4. Test with new email
   - Expected: New user created, additional info form displayed (company, phone)
5. Test denied authorization
   - Expected: Redirect back with error "Authorization denied"
6. Test Google API error (simulate network issue)
   - Expected: Error message "Unable to connect to Google, try again"
7. Verify user record: New users should have email_verified = true

---

## Sub-Prompt 5: LinkedIn OAuth Integration
**Task**: Integrate LinkedIn OAuth 2.0 for professional social authentication.

**Implementation Details**:
- Register application with LinkedIn Developer Portal
- Implement OAuth flow: `GET /api/auth/linkedin`
- Implement callback: `GET /api/auth/linkedin/callback`
- Retrieve user profile (email, name, profile picture)
- Pre-fill registration form with LinkedIn data
- Handle account linking for existing emails
- Generate JWT tokens

**Testing Instructions**:
1. Click "Continue with LinkedIn"
   - Expected: Redirect to LinkedIn authorization page
2. Authorize application
   - Expected: User logged in or additional info form shown
3. Test with existing email
   - Expected: Account linked successfully
4. Test profile pre-fill
   - Expected: Name and email auto-filled from LinkedIn
5. Test LinkedIn API error
   - Expected: Graceful error handling with retry option
6. Verify data mapping: Check database for correct name, email
7. Test authorization denial
   - Expected: Return to login with message

---

## Sub-Prompt 6: Login Endpoint with JWT
**Task**: Implement login endpoint with JWT token generation and rate limiting.

**Implementation Details**:
- Create endpoint: `POST /api/auth/login` (email + password)
- Verify credentials: Check email exists, compare password hash
- Check account status (active/inactive)
- Implement rate limiting: CAPTCHA after 3 failed attempts, lockout after 5 failures (15 min)
- Generate JWT access token (15 min expiry) and refresh token (7 days expiry)
- Store refresh token in HttpOnly cookie
- Update last_login timestamp
- Return user data and tokens

**Testing Instructions**:
1. Login with valid credentials
   - Expected: 200 OK, JWT tokens returned, user data in response
2. Login with incorrect password
   - Expected: 401 Unauthorized, "Invalid email or password"
3. Fail 3 times, check for CAPTCHA
   - Expected: CAPTCHA displayed on 4th attempt
4. Fail 5 times, check account lockout
   - Expected: 429 Too Many Requests, "Try again in 15 minutes"
5. Login with unverified email
   - Expected: 403 Forbidden, "Please verify your email"
6. Login with inactive account
   - Expected: 403 Forbidden, "Account deactivated"
7. Verify JWT token: Decode token, check claims (userId, role, expiry)
8. Test "Remember Me" option
   - Expected: Refresh token expiry extended to 30 days

---

## Sub-Prompt 7: Password Reset Workflow
**Task**: Implement forgot password and password reset functionality.

**Implementation Details**:
- Create endpoint: `POST /api/auth/forgot-password` (email input)
- Generate reset token (UUID, 1h expiry)
- Send reset email with link
- Create reset endpoint: `POST /api/auth/reset-password` (token + new password)
- Validate token and password strength
- Update password hash
- Invalidate all existing sessions
- Mark token as used
- Auto-login user

**Testing Instructions**:
1. Request password reset with valid email
   - Expected: Email sent with reset link (check inbox)
2. Request reset with non-existent email
   - Expected: Generic message "Reset link sent" (security - no email enumeration)
3. Click reset link, enter new password
   - Expected: Password updated, user logged in
4. Try using same token again
   - Expected: 400 Bad Request, "Token already used"
5. Wait 61 minutes, try reset
   - Expected: 400 Bad Request, "Token expired"
6. Enter weak password
   - Expected: 400 Bad Request, password validation error
7. Verify old sessions invalidated: Try using old access token
   - Expected: 401 Unauthorized

---

## Sub-Prompt 8: Session Management and Token Refresh
**Task**: Implement JWT refresh token mechanism and session expiry handling.

**Implementation Details**:
- Access token expiry: 15 minutes
- Refresh token expiry: 7 days
- Create endpoint: `POST /api/auth/refresh` (uses refresh token from cookie)
- Validate refresh token (expiry, not revoked)
- Generate new access token
- Return new access token
- Implement logout: `POST /api/auth/logout` (invalidate both tokens)
- Auto-refresh mechanism on frontend (2 min before expiry)

**Testing Instructions**:
1. Login, wait 16 minutes, make authenticated request
   - Expected: 401 Unauthorized, access token expired
2. Call refresh endpoint with valid refresh token
   - Expected: New access token generated
3. Use new access token
   - Expected: Request succeeds
4. Wait 8 days, try refresh
   - Expected: 401 Unauthorized, refresh token expired
5. Logout, try using tokens
   - Expected: 401 Unauthorized, tokens invalidated
6. Test concurrent sessions: Login from 2 devices
   - Expected: Both sessions valid independently
7. Test session expiry notification
   - Expected: User notified 2 minutes before access token expiry

---

## Sub-Prompt 9: Role-Based Access Control (RBAC)
**Task**: Implement role-based access control with middleware.

**Implementation Details**:
- Define roles: Client, Partner, Admin
- Create Spring Security configuration
- Implement authorization middleware: Check JWT token for user role
- Protect routes:
  - Client routes: /api/products, /api/orders (own orders only)
  - Partner routes: /api/partner/* (partner dashboard)
  - Admin routes: /api/admin/* (admin panel)
- Implement @PreAuthorize annotations
- Return 403 Forbidden for unauthorized access

**Testing Instructions**:
1. Login as Client, access /api/products
   - Expected: 200 OK, products returned
2. Login as Client, access /api/partner/dashboard
   - Expected: 403 Forbidden, "Access denied"
3. Login as Partner, access /api/partner/orders
   - Expected: 200 OK, partner orders returned
4. Login as Partner, access /api/admin/users
   - Expected: 403 Forbidden
5. Login as Admin, access /api/admin/users
   - Expected: 200 OK, all users returned
6. Access protected route without token
   - Expected: 401 Unauthorized
7. Access route with expired token
   - Expected: 401 Unauthorized

---

## Sub-Prompt 10: User Profile Management
**Task**: Implement user profile view and edit functionality.

**Implementation Details**:
- Create endpoint: `GET /api/auth/profile` (returns current user data)
- Create endpoint: `PUT /api/auth/profile` (update name, company, phone, profile picture)
- Email and user_type are read-only
- Change password endpoint: `POST /api/auth/change-password` (requires current password)
- Validate current password before changing
- Upload profile picture to S3/cloud storage
- Return updated user data

**Testing Instructions**:
1. Get profile: `GET /api/auth/profile`
   - Expected: 200 OK, user data returned (no password hash)
2. Update name: `PUT /api/auth/profile` with {full_name: "New Name"}
   - Expected: 200 OK, name updated in database
3. Try updating email
   - Expected: Email field ignored (read-only)
4. Upload profile picture
   - Expected: Image uploaded to S3, URL saved in database
5. Change password with correct current password
   - Expected: Password updated, can login with new password
6. Change password with wrong current password
   - Expected: 401 Unauthorized, "Current password incorrect"
7. Verify profile picture URL: Check S3 bucket, image accessible

---

## Integration Testing Checklist

After implementing all sub-prompts, perform end-to-end integration tests:

1. **Full Registration Flow**: Register → Verify Email → Login → Access Dashboard
2. **Social Auth Flow**: Google/LinkedIn → Additional Info → Dashboard
3. **Password Recovery Flow**: Forgot Password → Reset → Login with New Password
4. **Session Management**: Login → Use Access Token → Refresh Token → Logout
5. **Role-Based Access**: Login as each role, test route access permissions
6. **Security Tests**: 
   - SQL injection attempts in login
   - XSS attempts in registration
   - Token tampering (modify JWT signature)
   - CSRF token validation
7. **Performance Tests**: 
   - 100 concurrent login requests (should handle without degradation)
   - Password hashing performance (bcrypt should take ~100ms)
8. **Error Recovery**: 
   - Email service down (registration should queue email, not fail)
   - Database timeout (graceful error message)

---

## Security Validation Checklist

- [ ] Passwords stored as bcrypt hashes (never plain text)
- [ ] JWT tokens use secure secrets (min 256-bit)
- [ ] Refresh tokens stored in HttpOnly, Secure cookies
- [ ] Rate limiting active on login endpoint
- [ ] CAPTCHA shown after failed attempts
- [ ] Account lockout after 5 failed logins
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention (input sanitization)
- [ ] CSRF protection enabled
- [ ] SSL/TLS enforced on all auth endpoints

---

**Document Status**: Ready for Development
**Estimated Implementation Time**: 3-4 weeks (including testing)
