-- Loan Management System - Database Initialization Script
-- This script creates the initial database schema and seed data

-- ============================================================================
-- 1. CREATE TABLES
-- ============================================================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User-Role mapping table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Loan Types table
CREATE TABLE IF NOT EXISTS loan_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    min_amount DECIMAL(15,2) NOT NULL,
    max_amount DECIMAL(15,2) NOT NULL,
    min_tenure_months INT NOT NULL,
    max_tenure_months INT NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Loans table
CREATE TABLE IF NOT EXISTS loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    loan_type_id BIGINT NOT NULL,
    requested_amount DECIMAL(15,2) NOT NULL,
    approved_amount DECIMAL(15,2),
    tenure_months INT NOT NULL,
    interest_rate DECIMAL(5,2),
    employment_type VARCHAR(50) NOT NULL,
    monthly_income DECIMAL(15,2) NOT NULL,
    purpose TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    approved_at TIMESTAMP NULL,
    reviewed_by BIGINT,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (loan_type_id) REFERENCES loan_types(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id),
    INDEX idx_application_number (application_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_applied_at (applied_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- EMI Schedule table
CREATE TABLE IF NOT EXISTS emi_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    emi_number INT NOT NULL,
    due_date DATE NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_amount DECIMAL(15,2) NOT NULL,
    total_emi DECIMAL(15,2) NOT NULL,
    principal_balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    UNIQUE KEY unique_loan_emi (loan_id, emi_number),
    INDEX idx_loan_id (loan_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refresh Tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2. INSERT SEED DATA
-- ============================================================================

-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator with full access'),
('LOAN_OFFICER', 'Loan officer who can review and approve loans'),
('CUSTOMER', 'Customer who can apply for loans')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insert Admin User (password: admin123)
INSERT INTO users (username, email, password_hash, full_name, phone_number, is_active)
VALUES ('admin', 'admin@loanmanagement.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'System Administrator', '+1234567890', true)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Insert Loan Officer User (password: officer123)
INSERT INTO users (username, email, password_hash, full_name, phone_number, is_active)
VALUES ('officer1', 'officer1@loanmanagement.com',
        '$2a$10$8K1p/H/VgSPPqZaIRxYxgeECLN.YvlGKbZKvs4Y5GvDUEGXFLqPYa',
        'John Officer', '+1234567891', true)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Assign LOAN_OFFICER role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'officer1' AND r.name = 'LOAN_OFFICER'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Insert Sample Loan Types
INSERT INTO loan_types (name, description, min_amount, max_amount, min_tenure_months, max_tenure_months, interest_rate, is_active)
VALUES
('Personal Loan', 'Unsecured personal loan for various purposes including medical, travel, wedding, etc.',
 50000.00, 500000.00, 12, 60, 12.00, true),
('Home Loan', 'Secured loan for purchasing residential property or plot with competitive interest rates',
 500000.00, 10000000.00, 60, 360, 8.50, true),
('Car Loan', 'Secured loan for purchasing new or used vehicles with flexible repayment options',
 100000.00, 2000000.00, 12, 84, 9.50, true),
('Education Loan', 'Loan for higher education expenses including tuition fees and living expenses',
 100000.00, 2000000.00, 12, 120, 10.00, true),
('Business Loan', 'Loan for business expansion, working capital, or equipment purchase',
 200000.00, 5000000.00, 12, 84, 11.50, true)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    min_amount = VALUES(min_amount),
    max_amount = VALUES(max_amount),
    interest_rate = VALUES(interest_rate);

-- ============================================================================
-- 3. VERIFICATION QUERIES (commented out, uncomment to verify)
-- ============================================================================

-- SELECT * FROM roles;
-- SELECT u.username, u.email, u.full_name, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id;
-- SELECT * FROM loan_types WHERE is_active = true;

-- ============================================================================
-- END OF SCRIPT
-- ============================================================================
