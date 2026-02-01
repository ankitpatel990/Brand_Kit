-- ============================================================================
-- BrandKit Database Schema - User Authentication
-- FRD-001: User Registration and Authentication System
-- Version: 1.0
-- Date: January 2026
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- ENUM TYPES
-- ============================================================================

-- User Type Enum (Client, Partner, Admin)
CREATE TYPE user_type AS ENUM ('CLIENT', 'PARTNER', 'ADMIN');

-- User Status Enum
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING_VERIFICATION', 'LOCKED');

-- Auth Provider Enum
CREATE TYPE auth_provider AS ENUM ('EMAIL', 'GOOGLE', 'LINKEDIN');

-- ============================================================================
-- USERS TABLE
-- Primary table for all user accounts
-- ============================================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),  -- NULL for OAuth-only users
    full_name VARCHAR(100) NOT NULL,
    company_name VARCHAR(200),
    phone VARCHAR(20),
    user_type user_type NOT NULL DEFAULT 'CLIENT',
    status user_status NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_picture_url TEXT,
    auth_provider auth_provider NOT NULL DEFAULT 'EMAIL',
    google_id VARCHAR(255),
    linkedin_id VARCHAR(255),
    last_login_at TIMESTAMP WITH TIME ZONE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    terms_accepted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_full_name_length CHECK (char_length(full_name) >= 2 AND char_length(full_name) <= 100),
    CONSTRAINT chk_company_name_length CHECK (company_name IS NULL OR (char_length(company_name) >= 2 AND char_length(company_name) <= 200)),
    CONSTRAINT chk_phone_format CHECK (phone IS NULL OR phone ~ '^\+91-[0-9]{10}$')
);

-- Indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_google_id ON users(google_id) WHERE google_id IS NOT NULL;
CREATE INDEX idx_users_linkedin_id ON users(linkedin_id) WHERE linkedin_id IS NOT NULL;
CREATE INDEX idx_users_created_at ON users(created_at);

-- ============================================================================
-- USER_SESSIONS TABLE
-- Stores JWT refresh tokens for session management
-- ============================================================================

CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    refresh_token_hash VARCHAR(255) NOT NULL,
    device_info TEXT,
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Constraints
    CONSTRAINT chk_expires_in_future CHECK (expires_at > created_at)
);

-- Indexes for user_sessions
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_refresh_token ON user_sessions(refresh_token_hash);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_user_sessions_active ON user_sessions(user_id) WHERE is_revoked = FALSE;

-- ============================================================================
-- PASSWORD_RESETS TABLE
-- Stores password reset tokens
-- ============================================================================

CREATE TABLE password_resets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Token expires in 1 hour as per FRD
    CONSTRAINT chk_reset_expires CHECK (expires_at > created_at)
);

-- Indexes for password_resets
CREATE INDEX idx_password_resets_token ON password_resets(token_hash);
CREATE INDEX idx_password_resets_user_id ON password_resets(user_id);
CREATE INDEX idx_password_resets_expires_at ON password_resets(expires_at);

-- ============================================================================
-- EMAIL_VERIFICATIONS TABLE
-- Stores email verification tokens
-- ============================================================================

CREATE TABLE email_verifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Token expires in 24 hours as per FRD
    CONSTRAINT chk_verify_expires CHECK (expires_at > created_at)
);

-- Indexes for email_verifications
CREATE INDEX idx_email_verifications_token ON email_verifications(token_hash);
CREATE INDEX idx_email_verifications_user_id ON email_verifications(user_id);
CREATE INDEX idx_email_verifications_expires_at ON email_verifications(expires_at);

-- ============================================================================
-- LOGIN_ATTEMPTS TABLE
-- Tracks failed login attempts for rate limiting (also stored in Redis)
-- ============================================================================

CREATE TABLE login_attempts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL,
    ip_address INET NOT NULL,
    attempted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL DEFAULT FALSE
);

-- Indexes for login_attempts
CREATE INDEX idx_login_attempts_email ON login_attempts(email);
CREATE INDEX idx_login_attempts_ip ON login_attempts(ip_address);
CREATE INDEX idx_login_attempts_time ON login_attempts(attempted_at);

-- ============================================================================
-- UPDATED_AT TRIGGER FUNCTION
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to users table
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

-- Enable RLS on all tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE password_resets ENABLE ROW LEVEL SECURITY;
ALTER TABLE email_verifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE login_attempts ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- RLS Policies for USERS table
-- ============================================================================

-- Users can view their own profile
CREATE POLICY users_select_own ON users
    FOR SELECT
    USING (id = current_setting('app.current_user_id', TRUE)::UUID);

-- Users can update their own profile (limited fields - enforced at application layer)
CREATE POLICY users_update_own ON users
    FOR UPDATE
    USING (id = current_setting('app.current_user_id', TRUE)::UUID);

-- Admins can view all users
CREATE POLICY users_admin_select ON users
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'ADMIN'
    );

-- Admins can update any user
CREATE POLICY users_admin_update ON users
    FOR UPDATE
    USING (
        current_setting('app.current_user_type', TRUE) = 'ADMIN'
    );

-- Service role can do anything (for backend service)
CREATE POLICY users_service_all ON users
    FOR ALL
    USING (
        current_setting('app.is_service_role', TRUE) = 'true'
    );

-- ============================================================================
-- RLS Policies for USER_SESSIONS table
-- ============================================================================

-- Users can view their own sessions
CREATE POLICY sessions_select_own ON user_sessions
    FOR SELECT
    USING (user_id = current_setting('app.current_user_id', TRUE)::UUID);

-- Users can revoke their own sessions
CREATE POLICY sessions_update_own ON user_sessions
    FOR UPDATE
    USING (user_id = current_setting('app.current_user_id', TRUE)::UUID);

-- Service role can do anything
CREATE POLICY sessions_service_all ON user_sessions
    FOR ALL
    USING (
        current_setting('app.is_service_role', TRUE) = 'true'
    );

-- ============================================================================
-- RLS Policies for PASSWORD_RESETS table
-- ============================================================================

-- Only service role can access password resets
CREATE POLICY password_resets_service_all ON password_resets
    FOR ALL
    USING (
        current_setting('app.is_service_role', TRUE) = 'true'
    );

-- ============================================================================
-- RLS Policies for EMAIL_VERIFICATIONS table
-- ============================================================================

-- Only service role can access email verifications
CREATE POLICY email_verifications_service_all ON email_verifications
    FOR ALL
    USING (
        current_setting('app.is_service_role', TRUE) = 'true'
    );

-- ============================================================================
-- RLS Policies for LOGIN_ATTEMPTS table
-- ============================================================================

-- Only service role can access login attempts (security data)
CREATE POLICY login_attempts_service_all ON login_attempts
    FOR ALL
    USING (
        current_setting('app.is_service_role', TRUE) = 'true'
    );

-- Admins can view login attempts
CREATE POLICY login_attempts_admin_select ON login_attempts
    FOR SELECT
    USING (
        current_setting('app.current_user_type', TRUE) = 'ADMIN'
    );

-- ============================================================================
-- FUNCTIONS FOR TOKEN MANAGEMENT
-- ============================================================================

-- Function to cleanup expired tokens (run via cron job)
CREATE OR REPLACE FUNCTION cleanup_expired_tokens()
RETURNS void AS $$
BEGIN
    -- Delete expired password reset tokens
    DELETE FROM password_resets 
    WHERE expires_at < CURRENT_TIMESTAMP;
    
    -- Delete expired email verification tokens
    DELETE FROM email_verifications 
    WHERE expires_at < CURRENT_TIMESTAMP;
    
    -- Delete expired sessions
    DELETE FROM user_sessions 
    WHERE expires_at < CURRENT_TIMESTAMP OR is_revoked = TRUE;
    
    -- Delete old login attempts (older than 24 hours)
    DELETE FROM login_attempts 
    WHERE attempted_at < CURRENT_TIMESTAMP - INTERVAL '24 hours';
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function to invalidate all user sessions
CREATE OR REPLACE FUNCTION invalidate_user_sessions(p_user_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE user_sessions 
    SET is_revoked = TRUE, revoked_at = CURRENT_TIMESTAMP
    WHERE user_id = p_user_id AND is_revoked = FALSE;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function to get failed login count for an email in the last 15 minutes
CREATE OR REPLACE FUNCTION get_failed_login_count(p_email VARCHAR(255), p_ip_address INET)
RETURNS INTEGER AS $$
DECLARE
    attempt_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO attempt_count
    FROM login_attempts
    WHERE (email = p_email OR ip_address = p_ip_address)
      AND success = FALSE
      AND attempted_at > CURRENT_TIMESTAMP - INTERVAL '15 minutes';
    
    RETURN attempt_count;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE users IS 'Primary user accounts table for BrandKit platform - FRD-001';
COMMENT ON TABLE user_sessions IS 'JWT refresh token sessions for users - FRD-001 FR-9';
COMMENT ON TABLE password_resets IS 'Password reset tokens (1hr expiry) - FRD-001 FR-7';
COMMENT ON TABLE email_verifications IS 'Email verification tokens (24hr expiry) - FRD-001 FR-2';
COMMENT ON TABLE login_attempts IS 'Login attempt tracking for rate limiting - FRD-001 FR-12';

COMMENT ON COLUMN users.user_type IS 'Role: CLIENT (catalog access), PARTNER (dashboard), ADMIN (full access)';
COMMENT ON COLUMN users.status IS 'Account status: ACTIVE, INACTIVE, PENDING_VERIFICATION, LOCKED';
COMMENT ON COLUMN users.auth_provider IS 'Primary authentication method used to create account';
COMMENT ON COLUMN users.failed_login_attempts IS 'Count of consecutive failed login attempts';
COMMENT ON COLUMN users.locked_until IS 'Account locked until this timestamp (15min after 5 failures)';
