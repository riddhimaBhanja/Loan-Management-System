-- ============================================================================
-- Loan Management System - Complete Database Initialization Script
-- This script creates schemas, tables, and comprehensive seed data
-- ============================================================================

-- ============================================================================
-- 1. CREATE DATABASES
-- ============================================================================

CREATE DATABASE IF NOT EXISTS auth_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS loan_application CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS loan_approval CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS emi_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS reporting_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================================================
-- 2. AUTH SERVICE TABLES AND DATA
-- ============================================================================

USE auth_user;

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

-- Insert Roles
INSERT IGNORE INTO roles (id, name, description) VALUES
(1, 'ADMIN', 'System administrator with full access'),
(2, 'LOAN_OFFICER', 'Loan officer who can review and approve loans'),
(3, 'CUSTOMER', 'Customer who can apply for loans');

-- Insert Users (password for all: Password@123)
-- Password hash for 'Password@123' using BCrypt: $2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6
INSERT IGNORE INTO users (id, username, email, password_hash, full_name, phone_number, is_active) VALUES
(1, 'admin', 'admin@loanmanagement.com', '$2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6', 'System Administrator', '+1234567890', true),
(2, 'officer1', 'officer1@loanmanagement.com', '$2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6', 'John Officer', '+1234567891', true),
(3, 'customer1', 'customer1@example.com', '$2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6', 'Alice Johnson', '+1234567892', true),
(4, 'customer2', 'customer2@example.com', '$2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6', 'Bob Smith', '+1234567893', true),
(5, 'customer3', 'customer3@example.com', '$2a$10$rCN.1fXqXy.3k5X6k0bK3eP1BLQqP7Q8QP4J5a5YO5gP5Z7OB5Qa6', 'Carol Williams', '+1234567894', true);

-- Assign roles to users
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- officer1 -> LOAN_OFFICER
(3, 3), -- customer1 -> CUSTOMER
(4, 3), -- customer2 -> CUSTOMER
(5, 3); -- customer3 -> CUSTOMER

-- ============================================================================
-- 3. LOAN APPLICATION SERVICE TABLES AND DATA
-- ============================================================================

USE loan_application;

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
    FOREIGN KEY (loan_type_id) REFERENCES loan_types(id),
    INDEX idx_application_number (application_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_applied_at (applied_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Documents table
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    INDEX idx_loan_id (loan_id),
    INDEX idx_document_type (document_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert Loan Types
INSERT IGNORE INTO loan_types (id, name, description, min_amount, max_amount, min_tenure_months, max_tenure_months, interest_rate, is_active) VALUES
(1, 'Personal Loan', 'Unsecured personal loan for various purposes including medical, travel, wedding, etc.', 50000.00, 500000.00, 12, 60, 12.00, true),
(2, 'Home Loan', 'Secured loan for purchasing residential property or plot with competitive interest rates', 500000.00, 10000000.00, 60, 360, 8.50, true),
(3, 'Car Loan', 'Secured loan for purchasing new or used vehicles with flexible repayment options', 100000.00, 2000000.00, 12, 84, 9.50, true),
(4, 'Education Loan', 'Loan for higher education expenses including tuition fees and living expenses', 100000.00, 2000000.00, 12, 120, 10.00, true),
(5, 'Business Loan', 'Loan for business expansion, working capital, or equipment purchase', 200000.00, 5000000.00, 12, 84, 11.50, true);

-- Insert Sample Loans
INSERT IGNORE INTO loans (id, application_number, customer_id, loan_type_id, requested_amount, approved_amount, tenure_months, interest_rate, employment_type, monthly_income, purpose, status, applied_at, reviewed_at, approved_at, reviewed_by, remarks) VALUES
-- Customer 1 (Alice Johnson - customer1) - ID 3
(1, 'LN2025010001', 3, 1, 200000.00, 200000.00, 36, 12.00, 'SALARIED', 75000.00, 'Medical expenses', 'DISBURSED', '2025-01-01 10:00:00', '2025-01-02 14:00:00', '2025-01-02 15:00:00', 2, 'Approved - Good credit history'),
(2, 'LN2025010002', 3, 3, 800000.00, NULL, 60, NULL, 'SALARIED', 75000.00, 'Purchase new car', 'APPLIED', '2025-01-05 09:30:00', NULL, NULL, NULL, NULL),

-- Customer 2 (Bob Smith - customer2) - ID 4
(3, 'LN2025010003', 4, 2, 5000000.00, 4500000.00, 240, 8.50, 'SELF_EMPLOYED', 150000.00, 'Home purchase', 'APPROVED', '2024-12-15 11:00:00', '2024-12-20 16:00:00', '2024-12-21 10:00:00', 2, 'Approved with reduced amount'),
(4, 'LN2025010004', 4, 4, 500000.00, NULL, 48, NULL, 'SELF_EMPLOYED', 150000.00, 'MBA abroad', 'UNDER_REVIEW', '2025-01-06 14:00:00', '2025-01-07 10:00:00', NULL, 2, 'Under verification'),

-- Customer 3 (Carol Williams - customer3) - ID 5
(5, 'LN2025010005', 5, 5, 1000000.00, NULL, 60, NULL, 'BUSINESS_OWNER', 200000.00, 'Business expansion', 'APPLIED', '2025-01-07 16:00:00', NULL, NULL, NULL, NULL),
(6, 'LN2025010006', 5, 1, 150000.00, NULL, 24, NULL, 'BUSINESS_OWNER', 200000.00, 'Wedding expenses', 'REJECTED', '2024-12-10 10:00:00', '2024-12-12 15:00:00', NULL, 2, 'Insufficient documentation');

-- ============================================================================
-- 4. LOAN APPROVAL SERVICE TABLES
-- ============================================================================

USE loan_approval;

-- Disbursements table
CREATE TABLE IF NOT EXISTS disbursements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL UNIQUE,
    disbursed_amount DECIMAL(15,2) NOT NULL,
    disbursement_date DATE NOT NULL,
    disbursement_method VARCHAR(50) NOT NULL,
    bank_account_number VARCHAR(50),
    bank_name VARCHAR(100),
    ifsc_code VARCHAR(20),
    transaction_reference VARCHAR(100),
    disbursed_by BIGINT NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_loan_id (loan_id),
    INDEX idx_disbursement_date (disbursement_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert Sample Disbursement for Loan 1
INSERT IGNORE INTO disbursements (id, loan_id, disbursed_amount, disbursement_date, disbursement_method, bank_account_number, bank_name, ifsc_code, transaction_reference, disbursed_by, remarks) VALUES
(1, 1, 200000.00, '2025-01-03', 'BANK_TRANSFER', '1234567890', 'HDFC Bank', 'HDFC0001234', 'TXN2025010300001', 2, 'Disbursed successfully');

-- ============================================================================
-- 5. EMI SERVICE TABLES AND DATA
-- ============================================================================

USE emi_service;

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
    payment_reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_loan_emi (loan_id, emi_number),
    INDEX idx_loan_id (loan_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- EMI Payments table
CREATE TABLE IF NOT EXISTS emi_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emi_id BIGINT NOT NULL,
    loan_id BIGINT NOT NULL,
    amount_paid DECIMAL(15,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL,
    payment_reference VARCHAR(100),
    remarks TEXT,
    FOREIGN KEY (emi_id) REFERENCES emi_schedule(id) ON DELETE CASCADE,
    INDEX idx_emi_id (emi_id),
    INDEX idx_loan_id (loan_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert EMI Schedule for Loan 1 (36 months)
-- Monthly EMI for ₹200,000 at 12% for 36 months = ₹6,644.14
INSERT IGNORE INTO emi_schedule (loan_id, emi_number, due_date, principal_amount, interest_amount, total_emi, principal_balance, status, paid_at, payment_reference) VALUES
-- EMI 1 (PAID)
(1, 1, '2025-02-03', 4644.14, 2000.00, 6644.14, 195355.86, 'PAID', '2025-02-01 10:00:00', 'PAY2025020100001'),
-- EMI 2 (PAID)
(1, 2, '2025-03-03', 4690.51, 1953.63, 6644.14, 190665.35, 'PAID', '2025-03-01 10:00:00', 'PAY2025030100001'),
-- EMI 3 (PENDING - due soon)
(1, 3, '2025-04-03', 4737.41, 1906.73, 6644.14, 185927.94, 'PENDING', NULL, NULL),
-- EMI 4 onwards (PENDING)
(1, 4, '2025-05-03', 4784.86, 1859.28, 6644.14, 181143.08, 'PENDING', NULL, NULL),
(1, 5, '2025-06-03', 4832.86, 1811.28, 6644.14, 176310.22, 'PENDING', NULL, NULL);

-- Insert more EMIs for remaining months (simplified - add more if needed)
INSERT IGNORE INTO emi_schedule (loan_id, emi_number, due_date, principal_amount, interest_amount, total_emi, principal_balance, status)
SELECT 1,
       n + 5,
       DATE_ADD('2025-02-03', INTERVAL n + 5 MONTH),
       5500.00,
       1144.14,
       6644.14,
       200000.00 - ((n + 5) * 5500.00),
       'PENDING'
FROM (
    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) numbers
WHERE n < 31;

-- Insert EMI Payments
INSERT IGNORE INTO emi_payments (emi_id, loan_id, amount_paid, payment_date, payment_method, payment_reference, remarks) VALUES
(1, 1, 6644.14, '2025-02-01 10:00:00', 'ONLINE', 'PAY2025020100001', 'Paid via net banking'),
(2, 1, 6644.14, '2025-03-01 10:00:00', 'ONLINE', 'PAY2025030100001', 'Paid via UPI');

-- ============================================================================
-- 6. NOTIFICATION SERVICE TABLES
-- ============================================================================

USE notification_service;

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    is_read BOOLEAN DEFAULT false,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample notifications
INSERT IGNORE INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read) VALUES
(3, 'Loan Application Approved', 'Your loan application LN2025010001 has been approved for ₹2,00,000.', 'LOAN_APPROVAL', 'LOAN', 1, true),
(3, 'EMI Payment Reminder', 'Your EMI of ₹6,644.14 is due on 2025-04-03.', 'EMI_REMINDER', 'EMI', 3, false),
(4, 'Loan Under Review', 'Your loan application LN2025010004 is currently under review.', 'LOAN_UNDER_REVIEW', 'LOAN', 4, false);

-- ============================================================================
-- 7. REPORTING SERVICE TABLES (if needed for caching)
-- ============================================================================

USE reporting_service;

-- Dashboard stats cache (optional)
CREATE TABLE IF NOT EXISTS dashboard_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    stats_json TEXT NOT NULL,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 8. VERIFICATION
-- ============================================================================

-- Verify data
USE auth_user;
SELECT 'AUTH SERVICE - Users' as 'Table';
SELECT u.id, u.username, u.email, u.full_name, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;

USE loan_application;
SELECT '\nLOAN APPLICATION SERVICE - Loan Types' as 'Table';
SELECT id, name, min_amount, max_amount, interest_rate FROM loan_types;

SELECT '\nLOAN APPLICATION SERVICE - Loans' as 'Table';
SELECT id, application_number, customer_id, requested_amount, status, applied_at FROM loans;

USE emi_service;
SELECT '\nEMI SERVICE - EMI Schedule (First 5)' as 'Table';
SELECT loan_id, emi_number, due_date, total_emi, status, paid_at FROM emi_schedule WHERE loan_id = 1 LIMIT 5;

-- ============================================================================
-- END OF SCRIPT
-- ============================================================================
--
-- DEFAULT CREDENTIALS (for all users):
-- Username: admin / officer1 / customer1 / customer2 / customer3
-- Password: Password@123
--
-- Test Customers:
-- - customer1 (Alice Johnson) - ID: 3 - Has 1 disbursed loan, 1 applied loan
-- - customer2 (Bob Smith) - ID: 4 - Has 1 approved loan, 1 under review loan
-- - customer3 (Carol Williams) - ID: 5 - Has 1 applied loan, 1 rejected loan
-- ============================================================================
