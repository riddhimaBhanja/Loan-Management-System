# LOAN MANAGEMENT SYSTEM - API DOCUMENTATION

> **Complete REST API documentation for the Loan Management System**

**Base URL:** `http://localhost:8080/api`
**API Version:** 1.0.0
**Total Endpoints:** 31+
**Authentication:** JWT Bearer Token
**Response Format:** JSON

---

## 📋 Table of Contents

1. [API Overview](#1-api-overview)
2. [Authentication](#2-authentication)
3. [API Endpoints](#3-api-endpoints)
4. [Request/Response Examples](#4-requestresponse-examples)
5. [Error Handling](#5-error-handling)
6. [Swagger Documentation](#6-swagger-documentation)
7. [Postman Collection](#7-postman-collection)

---

## 1. API Overview

### 1.1 Base Information

| Property | Value |
|----------|-------|
| **Protocol** | HTTP/HTTPS |
| **Base URL** | http://localhost:8080/api |
| **Content-Type** | application/json |
| **Authentication** | JWT Bearer Token |
| **Character Encoding** | UTF-8 |

### 1.2 API Categories

| Category | Endpoints | Description |
|----------|-----------|-------------|
| **Authentication** | 4 | User authentication and token management |
| **Loan Management** | 10 | Loan application and approval workflow |
| **EMI Management** | 4 | EMI schedule and payment tracking |
| **Loan Types** | 5 | Loan type configuration (Admin) |
| **User Management** | 5 | User administration (Admin) |
| **Reports** | 3 | Dashboard and analytics |

**Total:** 31+ endpoints

### 1.3 HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, DELETE |
| 201 | Created | Successful POST (resource created) |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource (e.g., username exists) |
| 500 | Internal Server Error | Server-side error |

---

## 2. Authentication

### 2.1 Authentication Flow

```
┌──────────┐                  ┌──────────┐
│  Client  │                  │  Server  │
└────┬─────┘                  └────┬─────┘
     │                             │
     │ POST /auth/login            │
     ├────────────────────────────>│
     │                             │
     │ 200 OK                      │
     │ {accessToken, refreshToken} │
     │<────────────────────────────┤
     │                             │
     │ GET /loans (with Bearer)    │
     ├────────────────────────────>│
     │                             │
     │ 200 OK {loans}              │
     │<────────────────────────────┤
     │                             │
     │ POST /auth/refresh          │
     ├────────────────────────────>│
     │                             │
     │ 200 OK {new accessToken}    │
     │<────────────────────────────┤
     │                             │
```

### 2.2 JWT Token Structure

**Access Token (24 hours):**
```json
{
  "sub": "username",
  "roles": ["CUSTOMER"],
  "iat": 1735398000,
  "exp": 1735484400
}
```

**Refresh Token (7 days):**
- Stored in database
- UUID format
- Used to get new access token

### 2.3 Authorization Header

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIl0sImlhdCI6MTczNTM5ODAwMCwiZXhwIjoxNzM1NDg0NDAwfQ.signature
```

---

## 3. API Endpoints

### 3.1 Authentication Endpoints

#### POST /auth/register
**Description:** Register a new customer account

**Authentication:** None

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

---

#### POST /auth/login
**Description:** Login with username and password

**Authentication:** None

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@loanmanagement.com",
      "fullName": "System Administrator",
      "roles": ["ADMIN"]
    }
  }
}
```

---

#### POST /auth/refresh
**Description:** Get new access token using refresh token

**Authentication:** None

**Request Body:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

---

#### POST /auth/logout
**Description:** Logout and invalidate refresh token

**Authentication:** Required (Bearer Token)

**Request Body:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

---

### 3.2 Loan Management Endpoints

#### POST /loans
**Description:** Submit a new loan application

**Authentication:** Required (CUSTOMER role)

**Request Body:**
```json
{
  "loanTypeId": 1,
  "requestedAmount": 250000,
  "tenureMonths": 24,
  "employmentStatus": "SALARIED",
  "monthlyIncome": 75000,
  "purpose": "Home renovation and furniture purchase"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Loan application submitted successfully",
  "data": {
    "id": 1,
    "applicationNumber": "LN2025001",
    "loanTypeName": "Personal Loan",
    "requestedAmount": 250000.00,
    "tenureMonths": 24,
    "status": "APPLIED",
    "customerName": "John Doe",
    "employmentStatus": "SALARIED",
    "monthlyIncome": 75000.00,
    "purpose": "Home renovation and furniture purchase",
    "appliedAt": "2025-12-28T10:30:00"
  }
}
```

---

#### GET /loans
**Description:** Get all loan applications (paginated)

**Authentication:** Required (ADMIN, LOAN_OFFICER roles)

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Items per page
- `status` (optional) - Filter by status

**Example:** `/loans?page=0&size=10&status=APPLIED`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loans retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "applicationNumber": "LN2025001",
        "loanTypeName": "Personal Loan",
        "customerName": "John Doe",
        "requestedAmount": 250000.00,
        "status": "APPLIED",
        "appliedAt": "2025-12-28T10:30:00"
      }
    ],
    "totalElements": 45,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

---

#### GET /loans/my-loans
**Description:** Get current user's loans

**Authentication:** Required (CUSTOMER role)

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response (200 OK):** Same as GET /loans

---

#### GET /loans/{id}
**Description:** Get loan details by ID

**Authentication:** Required

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan retrieved successfully",
  "data": {
    "id": 1,
    "applicationNumber": "LN2025001",
    "loanTypeName": "Personal Loan",
    "customerName": "John Doe",
    "requestedAmount": 250000.00,
    "approvedAmount": 250000.00,
    "tenureMonths": 24,
    "interestRate": 12.50,
    "emiAmount": 11786.00,
    "status": "APPROVED",
    "employmentStatus": "SALARIED",
    "monthlyIncome": 75000.00,
    "purpose": "Home renovation",
    "remarks": "Approved after document verification",
    "appliedAt": "2025-12-28T10:30:00",
    "reviewedAt": "2025-12-28T14:20:00",
    "approvedAt": "2025-12-28T14:25:00",
    "reviewedByName": "Admin User"
  }
}
```

---

#### PUT /loans/{id}/status
**Description:** Update loan status to UNDER_REVIEW

**Authentication:** Required (ADMIN, LOAN_OFFICER roles)

**Request Body:** None

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan status updated to UNDER_REVIEW",
  "data": { /* loan object */ }
}
```

---

#### POST /loans/{id}/approve
**Description:** Approve a loan application

**Authentication:** Required (ADMIN, LOAN_OFFICER roles)

**Request Body:**
```json
{
  "approvedAmount": 250000,
  "interestRate": 12.50,
  "remarks": "Approved after document verification"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan approved successfully",
  "data": { /* loan object with APPROVED status */ }
}
```

---

#### POST /loans/{id}/reject
**Description:** Reject a loan application

**Authentication:** Required (ADMIN, LOAN_OFFICER roles)

**Request Body:**
```json
{
  "remarks": "Insufficient income for requested amount"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan rejected",
  "data": { /* loan object with REJECTED status */ }
}
```

---

### 3.3 EMI Management Endpoints

#### GET /emis/loan/{loanId}
**Description:** Get EMI schedule for a loan

**Authentication:** Required

**Response (200 OK):**
```json
{
  "success": true,
  "message": "EMI schedule retrieved successfully",
  "data": [
    {
      "id": 1,
      "emiNumber": 1,
      "dueDate": "2026-01-28",
      "principalAmount": 9203.00,
      "interestAmount": 2583.00,
      "totalEmi": 11786.00,
      "principalBalance": 240797.00,
      "status": "PAID",
      "paidAt": "2026-01-28T15:30:00"
    },
    {
      "id": 2,
      "emiNumber": 2,
      "dueDate": "2026-02-28",
      "principalAmount": 9279.00,
      "interestAmount": 2507.00,
      "totalEmi": 11786.00,
      "principalBalance": 231518.00,
      "status": "PENDING",
      "paidAt": null
    }
  ]
}
```

---

#### POST /emis/{emiId}/payment
**Description:** Record EMI payment

**Authentication:** Required (ADMIN, LOAN_OFFICER roles)

**Request Body:** None

**Response (200 OK):**
```json
{
  "success": true,
  "message": "EMI payment recorded successfully",
  "data": { /* updated EMI object */ }
}
```

---

### 3.4 Loan Types Endpoints

#### GET /loan-types
**Description:** Get all active loan types

**Authentication:** None (public)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Personal Loan",
      "description": "Unsecured personal loan",
      "minAmount": 50000.00,
      "maxAmount": 500000.00,
      "minTenureMonths": 12,
      "maxTenureMonths": 60,
      "interestRate": 12.00,
      "isActive": true
    }
  ]
}
```

---

#### POST /loan-types
**Description:** Create new loan type

**Authentication:** Required (ADMIN role)

**Request Body:**
```json
{
  "name": "Vehicle Loan",
  "description": "Loan for vehicle purchase",
  "minAmount": 100000,
  "maxAmount": 2000000,
  "minTenureMonths": 12,
  "maxTenureMonths": 84,
  "interestRate": 9.5
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Loan type created successfully",
  "data": { /* loan type object */ }
}
```

---

#### PUT /loan-types/{id}
**Description:** Update loan type

**Authentication:** Required (ADMIN role)

**Request Body:** Same as POST

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan type updated successfully",
  "data": { /* updated loan type */ }
}
```

---

#### DELETE /loan-types/{id}
**Description:** Delete/deactivate loan type

**Authentication:** Required (ADMIN role)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Loan type deleted successfully",
  "data": null
}
```

---

### 3.5 User Management Endpoints

#### GET /users
**Description:** Get all users (paginated)

**Authentication:** Required (ADMIN role)

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@loanmanagement.com",
        "fullName": "System Administrator",
        "phoneNumber": "+1234567890",
        "roles": ["ADMIN"],
        "isActive": true,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "totalElements": 150,
    "totalPages": 15,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

---

#### PUT /users/{id}
**Description:** Update user details

**Authentication:** Required (ADMIN role)

**Request Body:**
```json
{
  "email": "newemail@example.com",
  "fullName": "Updated Name",
  "phoneNumber": "+9876543210",
  "isActive": true
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": { /* updated user */ }
}
```

---

### 3.6 Reports Endpoints

#### GET /reports/dashboard
**Description:** Get dashboard statistics

**Authentication:** Required

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Dashboard data retrieved",
  "data": {
    "totalUsers": 150,
    "totalLoans": 245,
    "approvedLoans": 180,
    "totalDisbursed": 45000000.00,
    "pendingReview": 20,
    "rejectedLoans": 45,
    "activeEMIs": 4320,
    "overdueEMIs": 125,
    "loansByStatus": {
      "APPLIED": 20,
      "UNDER_REVIEW": 15,
      "APPROVED": 180,
      "REJECTED": 45
    },
    "loansByType": {
      "Personal Loan": 120,
      "Home Loan": 80,
      "Car Loan": 45
    }
  }
}
```

---

## 4. Request/Response Examples

### 4.1 Complete Loan Application Flow

**Step 1: Register**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```

**Step 2: Apply for Loan**
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "loanTypeId": 1,
    "requestedAmount": 250000,
    "tenureMonths": 24,
    "employmentStatus": "SALARIED",
    "monthlyIncome": 75000,
    "purpose": "Home renovation"
  }'
```

**Step 3: Check Status**
```bash
curl -X GET http://localhost:8080/api/loans/my-loans \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Step 4: Approve (as Admin)**
```bash
curl -X POST http://localhost:8080/api/loans/1/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN" \
  -d '{
    "approvedAmount": 250000,
    "interestRate": 12.50,
    "remarks": "Approved"
  }'
```

**Step 5: View EMI Schedule**
```bash
curl -X GET http://localhost:8080/api/emis/loan/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 5. Error Handling

### 5.1 Standard Error Response

```json
{
  "success": false,
  "message": "Error description",
  "error": "ERROR_CODE",
  "timestamp": "2025-12-28T10:30:00",
  "path": "/api/loans"
}
```

### 5.2 Common Errors

**400 Bad Request:**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "requestedAmount",
      "message": "Amount must be between 50000 and 500000"
    },
    {
      "field": "tenureMonths",
      "message": "Tenure must be between 12 and 60 months"
    }
  ]
}
```

**401 Unauthorized:**
```json
{
  "success": false,
  "message": "Invalid or expired token",
  "error": "UNAUTHORIZED"
}
```

**403 Forbidden:**
```json
{
  "success": false,
  "message": "Access denied. Insufficient permissions.",
  "error": "FORBIDDEN"
}
```

**404 Not Found:**
```json
{
  "success": false,
  "message": "Loan not found with id: 999",
  "error": "NOT_FOUND"
}
```

**409 Conflict:**
```json
{
  "success": false,
  "message": "Username already exists",
  "error": "DUPLICATE_USERNAME"
}
```

---

## 6. Swagger Documentation

### 6.1 Accessing Swagger UI

**URL:** http://localhost:8080/swagger-ui.html

**Features:**
- Interactive API testing
- Request/response schemas
- Authentication support
- Example payloads
- Try it out functionality

### 6.2 OpenAPI Specification

**URL:** http://localhost:8080/v3/api-docs

**Format:** JSON

**Download:**
```bash
curl http://localhost:8080/v3/api-docs > openapi-spec.json
```

---

## 7. Postman Collection

### 7.1 Import Collection

**File:** `Loan_Management_System_API.postman_collection.json`

**Import Steps:**
1. Open Postman
2. Click Import
3. Select the JSON file
4. Collection will be imported with all requests

### 7.2 Collection Features

- Pre-configured base URL
- Auto-saved JWT tokens
- Test assertions
- Example payloads
- Environment variables

### 7.3 Collection Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `baseUrl` | API base URL | http://localhost:8080/api |
| `accessToken` | JWT access token | Auto-saved from login |
| `refreshToken` | Refresh token | Auto-saved from login |

---

## 8. Rate Limiting & Best Practices

### 8.1 Best Practices

1. **Use pagination** for list endpoints
2. **Store tokens securely** (not in localStorage if possible)
3. **Refresh tokens before expiry** to avoid interruptions
4. **Use HTTPS in production**
5. **Validate input on client side** before API calls
6. **Handle errors gracefully**
7. **Use appropriate HTTP methods** (GET for read, POST for create, etc.)

### 8.2 Performance Tips

- Use `page` and `size` parameters to limit data
- Filter by status to reduce result set
- Cache frequently accessed data (loan types)
- Use conditional requests when appropriate

---

## 9. API Versioning

**Current Version:** v1 (implicit in base URL)

**Future Versioning Strategy:**
- URL versioning: `/api/v2/loans`
- Header versioning: `Accept: application/vnd.loanmanagement.v2+json`

---

## 10. Quick Reference

### 10.1 Endpoint Summary Table

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| /auth/register | POST | No | - | Register new user |
| /auth/login | POST | No | - | Login |
| /auth/refresh | POST | No | - | Refresh token |
| /auth/logout | POST | Yes | All | Logout |
| /loans | POST | Yes | CUSTOMER | Apply for loan |
| /loans | GET | Yes | ADMIN, OFFICER | Get all loans |
| /loans/my-loans | GET | Yes | CUSTOMER | Get user's loans |
| /loans/{id} | GET | Yes | All | Get loan details |
| /loans/{id}/status | PUT | Yes | ADMIN, OFFICER | Update status |
| /loans/{id}/approve | POST | Yes | ADMIN, OFFICER | Approve loan |
| /loans/{id}/reject | POST | Yes | ADMIN, OFFICER | Reject loan |
| /emis/loan/{loanId} | GET | Yes | All | Get EMI schedule |
| /emis/{emiId}/payment | POST | Yes | ADMIN, OFFICER | Record payment |
| /loan-types | GET | No | - | Get loan types |
| /loan-types | POST | Yes | ADMIN | Create loan type |
| /loan-types/{id} | PUT | Yes | ADMIN | Update loan type |
| /loan-types/{id} | DELETE | Yes | ADMIN | Delete loan type |
| /users | GET | Yes | ADMIN | Get all users |
| /users/{id} | PUT | Yes | ADMIN | Update user |
| /reports/dashboard | GET | Yes | All | Get dashboard stats |

---

**API Documentation Version:** 1.0.0
**Last Updated:** December 28, 2025
**Interactive Docs:** http://localhost:8080/swagger-ui.html
**Postman Collection:** Loan_Management_System_API.postman_collection.json
