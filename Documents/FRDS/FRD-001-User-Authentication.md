# FRD-001: User Registration and Authentication

---

## 1. FRD METADATA

**FRD Title**: User Registration and Authentication System  
**FRD ID**: FRD-001  
**Related PRD Section(s)**: 4.2.1 Core Features - User Registration and Authentication  
**Priority**: High  
**Owner**: Engineering  
**Version**: 1.0  
**Date**: January 23, 2026  

---

## 2. OBJECTIVE

### Purpose
Implement a secure, multi-channel authentication system that enables corporate clients, event organizers, fulfillment partners, and administrators to register, authenticate, and access role-specific functionalities within the BrandKit platform.

### Business Value
- Enable B2B-focused onboarding with professional authentication channels (Google, LinkedIn)
- Establish role-based access control for platform security and operational efficiency
- Build foundation for user tracking, analytics, and personalized experiences
- Support partner ecosystem integration through dedicated authentication flows (partner details remain internal)

---

## 3. SCOPE

### In Scope
- Email-based registration and login with password
- Social authentication via Google OAuth 2.0
- Social authentication via LinkedIn OAuth 2.0
- Role-based user accounts: Client, Admin, Partner (fulfillment partners, details not exposed externally)
- Password reset and recovery workflow
- Email verification for new registrations
- Session management using JWT tokens
- Basic user profile management (name, email, company, phone)
- Account activation/deactivation by admins
- Security features: Rate limiting, CAPTCHA, password strength validation

### Out of Scope
- Multi-factor authentication (MFA) - planned for Phase 2
- Biometric authentication
- Single Sign-On (SSO) with enterprise systems
- Account deletion by users (admin-controlled only)
- Social login via Facebook, Twitter, or other platforms
- Phone number-based OTP authentication
- Integration with third-party identity providers beyond Google/LinkedIn

---

## 4. USER STORIES

### Client Users
- **US-001**: As a Corporate Procurement Manager, I want to sign up using my company email so that I can access the platform with my professional identity
- **US-002**: As an Event Organizer, I want to log in using my Google account so that I can quickly access the platform without creating new credentials
- **US-003**: As a Client, I want to reset my password via email so that I can regain access if I forget my credentials
- **US-004**: As a Client, I want to verify my email address so that the platform can confirm my identity
- **US-005**: As a Client, I want to update my company profile information so that admins can identify my organization

### Partner Users (Fulfillment Partners - Internal)
- **US-006**: As a Partner, I want to register with my business details so that I can receive and fulfill orders
- **US-007**: As a Partner, I want to log in using LinkedIn so that I can leverage my professional network credentials

### Admin Users
- **US-008**: As an Admin, I want to log in with elevated privileges so that I can manage the platform
- **US-009**: As an Admin, I want to activate or deactivate user accounts so that I can control platform access
- **US-010**: As an Admin, I want to view all registered users by role so that I can monitor platform adoption

---

## 5. FUNCTIONAL REQUIREMENTS

### FR-1: Email-Based Registration
The system shall provide email-based registration with the following fields:
- Full Name (mandatory, 2-100 characters)
- Email Address (mandatory, valid email format)
- Password (mandatory, minimum 8 characters with at least one uppercase, one lowercase, one number, one special character)
- Company Name (mandatory for Client/Partner, 2-200 characters)
- Phone Number (optional, valid Indian format: +91-XXXXXXXXXX)
- User Type selection (Client/Partner)
- Terms & Conditions acceptance checkbox (mandatory)

### FR-2: Email Verification
The system shall send a verification email containing a unique token (valid for 24 hours) to the registered email address. Users must verify their email before accessing full platform features.

### FR-3: Google OAuth Integration
The system shall integrate Google OAuth 2.0 allowing users to register/login using their Google accounts. On first login, users shall provide additional mandatory fields: Company Name, Phone Number, User Type.

### FR-4: LinkedIn OAuth Integration
The system shall integrate LinkedIn OAuth 2.0 allowing users to register/login using their LinkedIn accounts. The system shall pre-fill Full Name and Email from LinkedIn profile and prompt for remaining mandatory fields.

### FR-5: Password Requirements
The system shall enforce password policies:
- Minimum 8 characters
- At least one uppercase letter (A-Z)
- At least one lowercase letter (a-z)
- At least one digit (0-9)
- At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)
- Cannot contain user's email or name
- Real-time validation with visual feedback

### FR-6: Login Functionality
The system shall provide login with:
- Email + Password combination
- "Remember Me" option (extends session to 30 days)
- Google/LinkedIn quick login buttons
- CAPTCHA verification after 3 failed attempts
- Account lockout for 15 minutes after 5 failed attempts

### FR-7: Password Reset Workflow
The system shall allow password reset through:
- "Forgot Password" link on login page
- Email input for reset link generation
- Reset link valid for 1 hour
- Password reset form with confirmation field
- Automatic login after successful reset

### FR-8: Role-Based Access Control
The system shall assign roles at registration with specific permissions:
- **Client**: Access catalog, customization, orders, profile (partner details hidden)
- **Partner**: Access partner dashboard, orders, production tracking, discount management (partner details remain internal, not exposed to clients)
- **Admin**: Access all modules, user management, analytics, settings, discount oversight

### FR-9: Session Management
The system shall implement JWT-based session management:
- Access token validity: 15 minutes
- Refresh token validity: 7 days
- Auto-refresh on user activity
- Logout functionality (invalidates both tokens)
- Session expiry notification 2 minutes before timeout

### FR-10: User Profile Management
Users shall be able to view and edit their profile:
- Editable: Full Name, Company Name, Phone Number, Profile Picture
- Read-only: Email Address, User Type, Registration Date
- Change Password functionality (requires current password)

### FR-11: Account Status Management (Admin)
Admins shall be able to:
- View all users in a searchable, filterable table
- Filter by role, status (active/inactive), registration date
- Activate/deactivate user accounts
- View user activity logs (last login, order history)
- Send password reset links to users

### FR-12: Security Features
The system shall implement:
- Rate limiting: Maximum 5 login attempts per IP per minute
- CAPTCHA: Google reCAPTCHA v3 on registration and after failed logins
- SSL/TLS encryption for all authentication endpoints
- Password hashing using bcrypt with salt rounds: 10
- HttpOnly and Secure flags on authentication cookies

### FR-13: Duplicate Account Prevention
The system shall prevent duplicate registrations:
- Check email uniqueness during registration
- Display error: "Email already registered" with login redirect link
- For social auth, link existing account if email matches

---

## 6. WORKFLOW / LOGIC

### Workflow 1: Email Registration Flow
1. User navigates to registration page
2. User selects "Sign Up with Email"
3. System displays registration form
4. User fills mandatory fields and selects user type
5. System validates input in real-time (email format, password strength)
6. User accepts Terms & Conditions
7. User clicks "Create Account"
8. System validates all fields server-side
9. System checks email uniqueness
10. System hashes password using bcrypt
11. System creates user record with status: "pending_verification"
12. System generates email verification token (UUID, 24h expiry)
13. System sends verification email
14. System displays: "Verification email sent to [email]"
15. User checks email and clicks verification link
16. System validates token (expiry, uniqueness)
17. System updates user status to "active"
18. System redirects to login page with success message
19. User logs in and accesses platform

**Edge Cases**:
- If email exists: Display error, offer login link
- If verification token expires: Provide "Resend Verification" option
- If required fields missing: Display inline errors, prevent submission
- If network error during registration: Show retry option, preserve form data

### Workflow 2: Social Authentication (Google) Flow
1. User navigates to login/registration page
2. User clicks "Continue with Google"
3. System redirects to Google OAuth consent screen
4. User authorizes BrandKit application
5. Google redirects back with authorization code
6. System exchanges code for access token
7. System retrieves user profile (email, name, profile picture)
8. System checks if email exists in database
   - **If exists**: Log user in, create session, redirect to dashboard
   - **If new**: Display additional info form (Company, Phone, User Type)
9. User completes additional fields
10. System creates user record with status: "active" (email pre-verified by Google)
11. System creates session (JWT tokens)
12. System redirects to appropriate dashboard based on role

**Edge Cases**:
- If user denies Google permission: Return to login with message "Authorization required"
- If Google API error: Display "Unable to connect to Google, try again"
- If email from Google already exists with different auth method: Prompt to link accounts or login with original method

### Workflow 3: Login Flow
1. User navigates to login page
2. User enters email and password
3. System validates format (client-side)
4. User clicks "Login"
5. System validates credentials (server-side)
6. System checks account status (active/inactive)
7. System checks failed attempt count
   - If <3 attempts: Proceed
   - If 3-4 attempts: Show CAPTCHA
   - If ≥5 attempts: Lock account for 15 minutes
8. System verifies password hash
9. If credentials valid:
   - Generate JWT access token (15 min)
   - Generate JWT refresh token (7 days)
   - Store refresh token in HttpOnly cookie
   - Reset failed attempt counter
   - Update last login timestamp
   - Redirect to role-specific dashboard
10. If credentials invalid:
   - Increment failed attempt counter
   - Display: "Invalid email or password"

**Edge Cases**:
- If account inactive: Display "Account deactivated, contact support"
- If email not verified: Display "Verify your email" with resend link
- If account locked: Display "Too many attempts, try after 15 minutes"
- If session exists: Skip login, redirect to dashboard

### Workflow 4: Password Reset Flow
1. User clicks "Forgot Password" on login page
2. System displays email input form
3. User enters registered email
4. User clicks "Send Reset Link"
5. System checks if email exists
6. If exists:
   - Generate reset token (UUID, 1h expiry)
   - Store token with user ID and timestamp
   - Send email with reset link
7. System displays: "Reset link sent" (regardless of email existence for security)
8. User checks email and clicks reset link
9. System validates token (expiry, usage status)
10. System displays password reset form (2 fields: new password, confirm)
11. User enters new password
12. System validates password strength
13. User clicks "Reset Password"
14. System hashes new password
15. System updates user password
16. System marks token as used
17. System invalidates all existing sessions for user
18. System displays success message
19. System auto-logs in user with new credentials

**Edge Cases**:
- If token expired: Display "Link expired, request new one"
- If token already used: Display "Link already used"
- If passwords don't match: Display inline error
- If user navigates away: Token remains valid until expiry

---

## 7. INPUT & OUTPUT

### Inputs

#### Registration Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Full Name | Text | 2-100 chars, letters and spaces only | Yes |
| Email | Email | Valid email format, unique | Yes |
| Password | Password | Min 8 chars, complexity rules | Yes |
| Confirm Password | Password | Must match password | Yes |
| Company Name | Text | 2-200 chars | Yes (Client/Partner) |
| Phone | Tel | +91-XXXXXXXXXX format | No |
| User Type | Dropdown | Client/Partner | Yes |
| T&C Acceptance | Checkbox | Must be checked | Yes |

#### Login Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Email | Email | Valid email format | Yes |
| Password | Password | Any string | Yes |
| Remember Me | Checkbox | Boolean | No |

#### Password Reset Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| Email | Email | Valid email format | Yes |

#### New Password Form
| Field | Type | Validation | Required |
|-------|------|------------|----------|
| New Password | Password | Min 8 chars, complexity rules | Yes |
| Confirm Password | Password | Must match new password | Yes |

### Outputs

#### Successful Registration
```json
{
  "status": "success",
  "message": "Registration successful. Verification email sent to user@example.com",
  "data": {
    "userId": "UUID",
    "email": "user@example.com",
    "verificationRequired": true
  }
}
```

#### Successful Login
```json
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "userId": "UUID",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "client",
    "accessToken": "JWT_ACCESS_TOKEN",
    "refreshToken": "JWT_REFRESH_TOKEN",
    "expiresIn": 900
  }
}
```

#### Error Response
```json
{
  "status": "error",
  "message": "Email already registered",
  "errorCode": "AUTH_001",
  "data": null
}
```

#### Email Verification Success
- Redirect to login page
- Display toast: "Email verified successfully. Please log in."

#### Password Reset Email Content
```
Subject: Reset Your BrandKit Password

Hi [Name],

You requested to reset your password. Click the link below to set a new password:

[Reset Link - expires in 1 hour]

If you didn't request this, please ignore this email.

Thanks,
BrandKit Team
```

---

## 8. VALIDATION & BUSINESS RULES

### BR-1: Email Uniqueness
Each email address can be associated with only one account. Attempting to register with an existing email displays an error and provides a link to login or reset password.

### BR-2: Email Verification Requirement
Users with unverified emails can log in but are redirected to a verification pending page with limited access until verification is complete.

### BR-3: Password Expiry
Passwords do not expire for MVP. Password expiry policy (90 days) planned for Phase 2.

### BR-4: Social Auth Email Matching
If a user registers with email X via email/password, then attempts to log in with Google using the same email X, the system shall link the accounts and allow authentication via both methods.

### BR-5: Role Assignment
User Type selected during registration determines the role:
- "Client" → Role: CLIENT
- "Partner" → Role: PARTNER (fulfillment partner, details not exposed to clients)
- Admin accounts can only be created by existing admins (not through public registration)

### BR-6: Session Concurrency
Multiple simultaneous sessions are allowed for the same user across different devices/browsers. Each session is tracked independently.

### BR-7: Account Deactivation Effect
Deactivated accounts cannot log in. Existing sessions are invalidated immediately upon deactivation.

### BR-8: Failed Login Lockout
After 5 consecutive failed login attempts, the account is locked for 15 minutes. Timer resets on successful login or after lockout expires.

### BR-9: Token Reuse Prevention
Email verification tokens and password reset tokens can only be used once. After use, they are marked as consumed.

### BR-10: Mandatory Fields for Social Auth
Users authenticating via Google/LinkedIn must complete mandatory fields (Company, Phone, User Type) before accessing the platform. They are redirected to a completion form after initial OAuth callback.

---

## 9. ERROR HANDLING

### Error Scenarios and Responses

| Scenario | Error Code | HTTP Status | Message | User Action |
|----------|-----------|-------------|---------|-------------|
| Email already exists | AUTH_001 | 409 | "Email already registered" | Click "Login" link or use password reset |
| Invalid email format | AUTH_002 | 400 | "Please enter a valid email address" | Correct email format |
| Weak password | AUTH_003 | 400 | "Password must meet security requirements" | Strengthen password per requirements |
| Passwords don't match | AUTH_004 | 400 | "Passwords do not match" | Re-enter matching passwords |
| Invalid credentials | AUTH_005 | 401 | "Invalid email or password" | Check credentials or reset password |
| Account not verified | AUTH_006 | 403 | "Please verify your email address" | Click "Resend verification" |
| Account deactivated | AUTH_007 | 403 | "Your account has been deactivated. Contact support." | Contact admin/support |
| Account locked | AUTH_008 | 429 | "Too many failed attempts. Try again in 15 minutes." | Wait or contact support |
| Expired verification token | AUTH_009 | 400 | "Verification link expired. Request a new one." | Click "Resend verification" |
| Expired reset token | AUTH_010 | 400 | "Password reset link expired. Request a new one." | Restart password reset process |
| OAuth authorization denied | AUTH_011 | 401 | "Authorization was denied. Please try again." | Retry OAuth or use email login |
| OAuth provider error | AUTH_012 | 502 | "Unable to connect to authentication provider" | Try again or use alternative login method |
| Missing required field | AUTH_013 | 400 | "Please fill in all required fields" | Complete missing fields |
| Invalid token | AUTH_014 | 401 | "Invalid or tampered authentication token" | Log in again |
| Session expired | AUTH_015 | 401 | "Your session has expired. Please log in again." | Redirect to login page |
| Rate limit exceeded | AUTH_016 | 429 | "Too many requests. Please try again later." | Wait 1 minute before retrying |

### Error Handling Strategy
- Display user-friendly error messages inline on forms
- Log detailed error information server-side for debugging
- Never expose system details (e.g., stack traces) to users
- Provide actionable next steps in error messages
- For security-sensitive errors (invalid credentials), use generic messaging to prevent enumeration attacks

---

## 10. NON-FUNCTIONAL NOTES

### Performance
- **NFR-1**: Login request must complete within 1.5 seconds under normal load
- **NFR-2**: Registration must complete within 2 seconds
- **NFR-3**: OAuth callback handling must complete within 2 seconds
- **NFR-4**: System must handle 100 concurrent authentication requests without degradation
- **NFR-5**: Token validation must occur in <100ms

### Security
- **NFR-6**: All authentication endpoints must use HTTPS with TLS 1.3
- **NFR-7**: Passwords must be hashed using bcrypt with salt rounds: 10
- **NFR-8**: JWT tokens must include user ID, role, and expiry claims
- **NFR-9**: Refresh tokens must be stored in HttpOnly, Secure, SameSite cookies
- **NFR-10**: Implement CSRF protection on authentication endpoints
- **NFR-11**: OAuth client secrets must be stored in environment variables, never in code
- **NFR-12**: Rate limiting must be implemented at application level (Redis-based) and firewall level

### Compliance
- **NFR-13**: User data collection must comply with India's DPDP Act (Digital Personal Data Protection Act)
- **NFR-14**: Users must explicitly consent to Terms & Conditions and Privacy Policy
- **NFR-15**: Email communications must include unsubscribe option (transactional emails exempt)
- **NFR-16**: User data must be encrypted at rest (AES-256)

### Scalability
- **NFR-17**: Authentication service must be stateless to support horizontal scaling
- **NFR-18**: Session data must be stored in distributed cache (Redis) for multi-instance deployments
- **NFR-19**: OAuth tokens must be cached for 5 minutes to reduce provider API calls

### Accessibility
- **NFR-20**: All forms must be keyboard navigable
- **NFR-21**: Form validation errors must be announced to screen readers
- **NFR-22**: Login/registration pages must meet WCAG 2.1 AA standards

---

## 11. ACCEPTANCE CRITERIA

### AC-1: Email Registration
**Given** a new user visits the registration page  
**When** the user fills all required fields with valid data and submits  
**Then** the system creates a user account with status "pending_verification"  
**And** sends a verification email  
**And** displays confirmation message "Verification email sent to [email]"

### AC-2: Email Verification
**Given** a user has registered but not verified email  
**When** the user clicks the verification link in email (within 24 hours)  
**Then** the system updates account status to "active"  
**And** displays success message  
**And** redirects to login page

### AC-3: Google OAuth Registration
**Given** a new user on the login page  
**When** the user clicks "Continue with Google" and authorizes the app  
**Then** the system retrieves user profile from Google  
**And** displays additional information form (Company, Phone, User Type)  
**And** creates account with status "active" upon form completion  
**And** logs the user in automatically

### AC-4: Successful Login
**Given** a registered user with active status on login page  
**When** the user enters correct email and password and submits  
**Then** the system validates credentials  
**And** generates JWT access token (15 min) and refresh token (7 days)  
**And** redirects to appropriate dashboard based on role (Client/Partner/Admin)

### AC-5: Password Reset
**Given** a user on the login page who forgot password  
**When** the user clicks "Forgot Password", enters email, and submits  
**Then** the system sends reset link to email (if email exists)  
**And** displays "Reset link sent" message  
**When** the user clicks reset link (within 1 hour)  
**Then** the system displays password reset form  
**When** the user enters new password meeting requirements and submits  
**Then** the system updates password  
**And** invalidates existing sessions  
**And** logs user in automatically

### AC-6: Failed Login Attempt Handling
**Given** a user on login page  
**When** the user enters incorrect credentials 3 times  
**Then** the system displays CAPTCHA on next attempt  
**When** the user fails 5 times total  
**Then** the system locks account for 15 minutes  
**And** displays "Too many failed attempts" message

### AC-7: Account Deactivation by Admin
**Given** an admin logged into the admin panel  
**When** the admin selects a user account and clicks "Deactivate"  
**Then** the system updates account status to "inactive"  
**And** invalidates all active sessions for that user  
**And** displays confirmation "Account deactivated successfully"  
**When** the deactivated user attempts to log in  
**Then** the system displays "Account deactivated. Contact support."

### AC-8: Session Expiry
**Given** a logged-in user with an active session  
**When** the access token expires (15 minutes of inactivity)  
**And** the user performs an action  
**Then** the system attempts to refresh using refresh token  
**If** refresh token valid: Generate new access token and continue  
**If** refresh token expired: Display "Session expired" and redirect to login

### AC-9: Role-Based Access Control
**Given** a user with role "Client" logged in  
**When** the user attempts to access Partner Dashboard URL directly  
**Then** the system denies access  
**And** displays "Access denied" message  
**And** redirects to Client dashboard
**Note**: Partner details and dashboard remain internal, not exposed in client-facing UI

### AC-10: Duplicate Email Prevention
**Given** a user on registration page  
**When** the user enters an email that already exists  
**And** submits the form  
**Then** the system displays error "Email already registered"  
**And** provides link "Already have an account? Login here"  
**And** does not create duplicate account

---

## 12. DEPENDENCIES & INTEGRATIONS

### External Services
- **Google OAuth 2.0**: Requires Google Cloud Project with OAuth credentials
- **LinkedIn OAuth 2.0**: Requires LinkedIn Developer Application
- **Email Service**: SMTP provider (e.g., SendGrid, AWS SES) for verification and reset emails
- **CAPTCHA**: Google reCAPTCHA v3 API key

### Internal Dependencies
- Supabase with PostgreSQL for user record storage (relational schema with Row-Level Security)
- Redis for session storage and rate limiting
- JWT library (Java: jjwt or similar)
- Spring Security for authentication/authorization
- Password hashing library (bcrypt)

### Configuration Requirements
- Environment variables for:
  - Google OAuth Client ID & Secret
  - LinkedIn OAuth Client ID & Secret
  - JWT Secret Key (minimum 256-bit)
  - Email service credentials
  - reCAPTCHA site key and secret
  - Redis connection string
  - Supabase URL and API Key (anon/service role keys)

---

## 13. ASSUMPTIONS

1. Users have access to email for verification and password reset
2. Corporate clients primarily use Google or LinkedIn for professional identity
3. Gujarat-based target audience is comfortable with English UI (Hindi support Phase 2)
4. Fulfillment partners are tech-savvy enough to complete digital registration (partner details remain internal)
5. SSL certificates are provisioned and renewed automatically
6. Email delivery is reliable (99%+ deliverability with reputable provider)
7. Supabase/PostgreSQL provides sufficient performance and scalability for user authentication workloads
7. Third-party OAuth providers (Google, LinkedIn) maintain 99.9% uptime
8. Users accessing platform use modern browsers (Chrome 90+, Firefox 88+, Safari 14+)

---

## 14. OPEN QUESTIONS / FUTURE ENHANCEMENTS

**Phase 2 Considerations:**
- Multi-factor authentication (SMS/Authenticator app)
- Biometric login (fingerprint/face recognition) for mobile app
- SSO integration with enterprise systems (Azure AD, Okta)
- Account deletion workflow with data retention policies
- Password expiry policy (90-day rotation)
- Social login via additional providers (Microsoft, Apple)
- Passwordless login (magic link via email)
- Role permission granularity (custom roles for enterprise clients)

---

**Document Status**: ✅ Ready for Review  
**Last Updated**: January 23, 2026  
**Next Steps**: Technical design, database schema design, API endpoint specification
