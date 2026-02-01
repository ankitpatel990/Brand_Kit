# BrandKit - B2B Custom Promotional Merchandise Platform

BrandKit is a B2B-focused digital platform that enables corporate offices, event organizers, and similar clients to design, preview, and order custom-branded promotional items.

## 🏗️ Project Structure

```
Print/
├── backend/                 # Spring Boot Java Backend
│   ├── src/main/java/com/brandkit/
│   │   ├── auth/           # Authentication module (FRD-001)
│   │   │   ├── controller/ # REST API controllers
│   │   │   ├── dto/        # Data Transfer Objects
│   │   │   ├── entity/     # JPA entities
│   │   │   ├── exception/  # Custom exceptions
│   │   │   ├── repository/ # Data repositories
│   │   │   ├── security/   # Security config & JWT
│   │   │   ├── service/    # Business logic
│   │   │   └── validation/ # Custom validators
│   │   └── config/         # Application configuration
│   └── src/main/resources/
│       ├── application.yml # Configuration
│       └── db/migration/   # Flyway migrations
├── frontend/               # Next.js React Frontend
│   ├── src/
│   │   ├── app/           # Next.js App Router pages
│   │   │   ├── auth/      # Authentication pages
│   │   │   ├── dashboard/ # Client dashboard
│   │   │   ├── partner/   # Partner dashboard
│   │   │   └── admin/     # Admin dashboard
│   │   ├── components/    # React components
│   │   └── lib/           # Utilities & API client
│   └── tailwind.config.ts
├── database/              # Database migrations (Supabase)
│   └── migrations/
└── Documents/             # PRD & FRD documentation
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL (or Supabase account)
- Redis (for rate limiting)

### Backend Setup

```bash
cd backend

# Copy environment variables
cp .env.example .env
# Edit .env with your configuration

# Run with Maven
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8080`

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Copy environment variables
cp .env.example .env.local

# Run development server
npm run dev
```

The frontend will start at `http://localhost:3000`

### Database Setup

1. Create a Supabase project at https://supabase.com
2. Run the migration scripts in `database/migrations/`
3. Update your `.env` with Supabase connection details

## 📋 Implemented Features (FRD-001)

### User Authentication System

- ✅ **Sub-Prompt 1**: Database Schema with RLS
- ✅ **Sub-Prompt 2**: Email-Based Registration API
- ✅ **Sub-Prompt 3**: Email Verification Workflow
- ✅ **Sub-Prompt 4**: Google OAuth Integration
- ✅ **Sub-Prompt 5**: LinkedIn OAuth Integration
- ✅ **Sub-Prompt 6**: Login with JWT Tokens
- ✅ **Sub-Prompt 7**: Password Reset Workflow
- ✅ **Sub-Prompt 8**: Session Management & Token Refresh
- ✅ **Sub-Prompt 9**: Role-Based Access Control (RBAC)
- ✅ **Sub-Prompt 10**: User Profile Management

## 📋 Implemented Features (FRD-003)

### Customization Engine

- ✅ **Sub-Prompt 1**: Logo Upload Component - Drag-drop, validation, preview
- ✅ **Sub-Prompt 2**: Logo Cropping Tool - react-easy-crop with locked aspect ratio
- ✅ **Sub-Prompt 3**: Client-Side Preview Rendering - HTML5 Canvas real-time preview
- ✅ **Sub-Prompt 4**: Server-Side High-Resolution Rendering - 300 DPI print-ready images
- ✅ **Sub-Prompt 5**: Print Area Configuration - Admin interface for print areas
- ✅ **Sub-Prompt 6**: Bundle Builder Workflow - Multi-product customization bundles
- ✅ **Sub-Prompt 7**: Draft Customization Save/Load - 30-day draft storage
- ✅ **Sub-Prompt 8**: Download Preview Image - Watermarked preview downloads
- ✅ **Sub-Prompt 9**: Multi-Product Logo Application - Apply logo to multiple products
- ✅ **Sub-Prompt 10**: Customization Validation - Client and server-side validation

### Customization Features

- Logo upload with drag-drop (PNG, JPG, SVG, max 10MB)
- Locked aspect ratio cropping based on product print area
- Real-time preview rendering (<500ms)
- High-resolution print image generation (300 DPI)
- Draft saving with 30-day expiry
- Bundle builder for multi-product customization (up to 10 products)
- Preview download with BrandKit watermark
- Resolution warnings for low-quality logos

### Security Features

- Password hashing with bcrypt (10 rounds)
- JWT access tokens (15 min) & refresh tokens (7 days)
- HttpOnly, Secure cookies for refresh tokens
- Rate limiting (5 requests/min per IP)
- CAPTCHA after 3 failed login attempts
- Account lockout after 5 failed attempts (15 min)
- CSRF protection
- Input validation & sanitization

### Role-Based Access

| Role    | Access                                      |
|---------|---------------------------------------------|
| CLIENT  | Products, Orders (own), Profile             |
| PARTNER | Partner Dashboard, Orders, Production       |
| ADMIN   | All modules, User Management, Analytics     |

## 🔑 API Endpoints

### Authentication

| Method | Endpoint                    | Description           |
|--------|-----------------------------|-----------------------|
| POST   | `/api/auth/register`        | Register new user     |
| POST   | `/api/auth/login`           | Login with credentials|
| GET    | `/api/auth/verify-email`    | Verify email token    |
| POST   | `/api/auth/forgot-password` | Request password reset|
| POST   | `/api/auth/reset-password`  | Reset password        |
| POST   | `/api/auth/refresh`         | Refresh access token  |
| POST   | `/api/auth/logout`          | Logout (revoke token) |
| GET    | `/api/auth/profile`         | Get user profile      |
| PUT    | `/api/auth/profile`         | Update profile        |
| POST   | `/api/auth/change-password` | Change password       |
| GET    | `/api/auth/google`          | Google OAuth flow     |
| GET    | `/api/auth/linkedin`        | LinkedIn OAuth flow   |

### Admin (requires ADMIN role)

| Method | Endpoint                          | Description              |
|--------|-----------------------------------|--------------------------|
| GET    | `/api/admin/users`                | List all users           |
| GET    | `/api/admin/users/{id}`           | Get user details         |
| PUT    | `/api/admin/users/{id}/status`    | Activate/deactivate user |
| GET    | `/api/admin/users/{id}/sessions`  | View user sessions       |
| DELETE | `/api/admin/users/{id}/sessions`  | Revoke all sessions      |
| GET    | `/api/admin/stats`                | User statistics          |

## 📚 Documentation

- [PRD](./Documents/PRD) - Product Requirements Document
- [FRD-001](./Documents/FRDS/FRD-001-User-Authentication.md) - User Authentication FRD
- [Prompts](./Documents/prompts/) - Development prompts for each FRD

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- PostgreSQL (Supabase)
- Redis
- JWT (jjwt)
- Flyway

### Frontend
- Next.js 14
- React 18
- TypeScript
- Tailwind CSS
- Framer Motion
- React Hook Form + Zod
- Axios

## 📄 License

Copyright © 2026 BrandKit. All rights reserved.
