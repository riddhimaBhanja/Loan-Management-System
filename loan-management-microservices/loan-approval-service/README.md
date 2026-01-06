# Loan Approval Service

## Overview

The **Loan Approval Service** is a microservice responsible for managing loan approval workflows, disbursement operations, and loan closure processes in the Loan Management System.

## Features

- **Loan Approval**: Approve loan applications with approved amount and interest rate
- **Loan Rejection**: Reject loan applications with detailed reasons
- **Loan Disbursement**: Disburse approved loans and trigger EMI schedule generation
- **Loan Closure**: Close fully paid loans after EMI verification
- **Inter-service Communication**: Coordinates with Auth, Loan Application, and EMI services

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud LoadBalancer**
- **Spring Data JPA**
- **MySQL 8.0**
- **MapStruct** (DTO Mapping)
- **Lombok**
- **JWT** (Authentication)

## Architecture

### Package Structure

```
com.loanmanagement.loanapproval/
├── domain/
│   ├── model/
│   │   ├── LoanApproval.java
│   │   └── LoanDisbursement.java
│   ├── repository/
│   │   ├── LoanApprovalRepository.java
│   │   └── LoanDisbursementRepository.java
│   └── service/
│       ├── LoanApprovalService.java
│       ├── LoanApprovalServiceImpl.java
│       ├── LoanDisbursementService.java
│       ├── LoanDisbursementServiceImpl.java
│       ├── LoanClosureService.java
│       └── LoanClosureServiceImpl.java
├── application/
│   ├── controller/
│   │   ├── LoanApprovalController.java
│   │   └── InternalLoanApprovalController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ApproveLoanRequest.java
│   │   │   ├── RejectLoanRequest.java
│   │   │   └── DisburseLoanRequest.java
│   │   └── response/
│   │       ├── LoanApprovalResponse.java
│   │       └── LoanDisbursementResponse.java
│   └── mapper/
│       ├── LoanApprovalMapper.java
│       └── LoanDisbursementMapper.java
├── infrastructure/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── RestTemplateConfig.java
│   ├── client/
│   │   ├── LoanApplicationServiceClient.java
│   │   ├── UserServiceClient.java
│   │   └── EmiServiceClient.java
│   └── exception/
│       └── GlobalExceptionHandler.java
└── shared/
    └── constants/
        └── MessageConstants.java
```

## Database Schema

### loan_approvals Table

| Column          | Type         | Description                      |
|-----------------|--------------|----------------------------------|
| id              | BIGINT       | Primary key                      |
| loan_id         | BIGINT       | Reference to loan (NOT FK)       |
| approver_id     | BIGINT       | Reference to user (NOT FK)       |
| status          | VARCHAR(20)  | APPROVED or REJECTED             |
| approved_amount | DECIMAL      | Approved loan amount             |
| interest_rate   | DECIMAL(5,2) | Approved interest rate           |
| decision_date   | DATETIME     | Date of approval/rejection       |
| rejection_reason| TEXT         | Reason for rejection             |
| notes           | TEXT         | Additional notes                 |
| created_at      | DATETIME     | Record creation timestamp        |
| updated_at      | DATETIME     | Last update timestamp            |

### loan_disbursements Table

| Column              | Type         | Description                      |
|---------------------|--------------|----------------------------------|
| id                  | BIGINT       | Primary key                      |
| loan_id             | BIGINT       | Reference to loan (UNIQUE)       |
| disbursed_by        | BIGINT       | Reference to user (NOT FK)       |
| amount              | DECIMAL      | Disbursed amount                 |
| disbursement_date   | DATE         | Date of disbursement             |
| disbursement_method | VARCHAR(50)  | Method (Bank Transfer, etc.)     |
| reference_number    | VARCHAR(100) | Transaction reference            |
| remarks             | TEXT         | Additional remarks               |
| created_at          | DATETIME     | Record creation timestamp        |
| updated_at          | DATETIME     | Last update timestamp            |

## Configuration

### Database Configuration

- **Database Name**: `loan_approval_db`
- **Port**: 3309
- **DDL Mode**: `update` (auto-create tables)

### Service Registration

- **Service Name**: `LOAN-APPROVAL-SERVICE`
- **Port**: 8083
- **Eureka URL**: `http://localhost:8761/eureka/`

### Environment Variables

| Variable     | Description           | Default      |
|--------------|-----------------------|--------------|
| DB_HOST      | Database host         | localhost    |
| DB_PORT      | Database port         | 3309         |
| DB_USERNAME  | Database username     | loanapp      |
| DB_PASSWORD  | Database password     | loanpass123  |
| EUREKA_URL   | Eureka server URL     | localhost    |
| JWT_SECRET   | JWT signing secret    | (see config) |

## API Documentation

### Public APIs (Require Authentication)

#### 1. Approve Loan

**Endpoint**: `POST /api/loan-approvals/{loanId}/approve`

**Roles**: `LOAN_OFFICER`, `ADMIN`

**Request Body**:
```json
{
  "approvedAmount": 50000.00,
  "interestRate": 10.50,
  "notes": "Approved based on credit score and income verification"
}
```

**Response**: `201 CREATED`
```json
{
  "id": 1,
  "loanId": 123,
  "approverId": 5,
  "approverName": "John Officer",
  "status": "APPROVED",
  "approvedAmount": 50000.00,
  "interestRate": 10.50,
  "decisionDate": "2024-01-15T10:30:00",
  "notes": "Approved based on credit score and income verification",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Business Logic**:
1. Verify approver has LOAN_OFFICER or ADMIN role
2. Fetch loan details from loan-application-service
3. Verify loan status is PENDING or UNDER_REVIEW
4. Validate approved amount <= requested amount
5. Create LoanApproval record with status APPROVED
6. Update loan status to APPROVED in loan-application-service
7. Return approval response with approver details

---

#### 2. Reject Loan

**Endpoint**: `POST /api/loan-approvals/{loanId}/reject`

**Roles**: `LOAN_OFFICER`, `ADMIN`

**Request Body**:
```json
{
  "rejectionReason": "Insufficient credit score and high debt-to-income ratio",
  "notes": "Suggest reapplying after 6 months"
}
```

**Response**: `201 CREATED`
```json
{
  "id": 2,
  "loanId": 124,
  "approverId": 5,
  "approverName": "John Officer",
  "status": "REJECTED",
  "decisionDate": "2024-01-15T11:00:00",
  "rejectionReason": "Insufficient credit score and high debt-to-income ratio",
  "notes": "Suggest reapplying after 6 months",
  "createdAt": "2024-01-15T11:00:00"
}
```

**Business Logic**:
1. Verify approver has LOAN_OFFICER or ADMIN role
2. Fetch loan details from loan-application-service
3. Verify loan status is PENDING or UNDER_REVIEW
4. Create LoanApproval record with status REJECTED
5. Update loan status to REJECTED in loan-application-service
6. Return rejection response

---

#### 3. Disburse Loan

**Endpoint**: `POST /api/loan-approvals/{loanId}/disburse`

**Roles**: `LOAN_OFFICER`, `ADMIN`

**Request Body**:
```json
{
  "disbursementDate": "2024-01-16",
  "disbursementMethod": "Bank Transfer - NEFT",
  "referenceNumber": "TXN20240116001234",
  "remarks": "Disbursed to customer bank account"
}
```

**Response**: `201 CREATED`
```json
{
  "id": 1,
  "loanId": 123,
  "disbursedBy": 5,
  "disbursedByName": "John Officer",
  "amount": 50000.00,
  "disbursementDate": "2024-01-16",
  "disbursementMethod": "Bank Transfer - NEFT",
  "referenceNumber": "TXN20240116001234",
  "remarks": "Disbursed to customer bank account",
  "createdAt": "2024-01-16T09:00:00"
}
```

**Business Logic**:
1. Verify disburser has LOAN_OFFICER or ADMIN role
2. Fetch loan details from loan-application-service
3. Verify loan status is APPROVED
4. Validate disbursement date is not in future
5. Create LoanDisbursement record
6. Update loan status to DISBURSED in loan-application-service
7. Trigger EMI schedule generation via emi-service
8. Return disbursement response

---

#### 4. Close Loan

**Endpoint**: `POST /api/loan-approvals/{loanId}/close`

**Roles**: `LOAN_OFFICER`, `ADMIN`

**Response**: `200 OK`
```json
{
  "id": 123,
  "applicationNumber": "LN2024011500001",
  "status": "CLOSED",
  "approvedAmount": 50000.00,
  "tenureMonths": 12,
  "closedAt": "2024-12-16T10:00:00"
}
```

**Business Logic**:
1. Fetch loan details from loan-application-service
2. Verify loan status is DISBURSED
3. Verify all EMIs are paid via emi-service
4. Update loan status to CLOSED in loan-application-service
5. Return updated loan details

---

#### 5. Get Approval Details

**Endpoint**: `GET /api/loan-approvals/loan/{loanId}`

**Roles**: `LOAN_OFFICER`, `ADMIN`, `CUSTOMER`

**Response**: `200 OK`
```json
{
  "id": 1,
  "loanId": 123,
  "approverId": 5,
  "approverName": "John Officer",
  "status": "APPROVED",
  "approvedAmount": 50000.00,
  "interestRate": 10.50,
  "decisionDate": "2024-01-15T10:30:00",
  "notes": "Approved based on credit score",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

#### 6. Get Disbursement Details

**Endpoint**: `GET /api/loan-approvals/disbursement/loan/{loanId}`

**Roles**: `LOAN_OFFICER`, `ADMIN`, `CUSTOMER`

**Response**: `200 OK`
```json
{
  "id": 1,
  "loanId": 123,
  "disbursedBy": 5,
  "disbursedByName": "John Officer",
  "amount": 50000.00,
  "disbursementDate": "2024-01-16",
  "disbursementMethod": "Bank Transfer - NEFT",
  "referenceNumber": "TXN20240116001234",
  "createdAt": "2024-01-16T09:00:00"
}
```

---

### Internal APIs (No Authentication)

#### 1. Get Approval by Loan ID

**Endpoint**: `GET /api/internal/loan-approvals/loan/{loanId}`

**Description**: Internal endpoint for microservices to fetch approval details

---

#### 2. Check if Loan Approved

**Endpoint**: `GET /api/internal/loan-approvals/loan/{loanId}/is-approved`

**Response**: `200 OK`
```json
true
```

---

#### 3. Check if Loan Disbursed

**Endpoint**: `GET /api/internal/loan-approvals/loan/{loanId}/is-disbursed`

**Response**: `200 OK`
```json
true
```

---

## Inter-Service Communication

### Calls to Loan Application Service

1. **Get Loan Details**: `GET /api/internal/loans/{loanId}`
2. **Update Status to APPROVED**: `PUT /api/internal/loans/{loanId}/approve`
3. **Update Status to REJECTED**: `PUT /api/internal/loans/{loanId}/reject`
4. **Update Status to DISBURSED**: `PUT /api/internal/loans/{loanId}/disburse`
5. **Update Status to CLOSED**: `PUT /api/internal/loans/{loanId}/close`

### Calls to Auth Service

1. **Get User Details**: `GET /api/internal/users/{userId}`
2. **Verify User Roles**: Uses UserDetailsDTO to check roles

### Calls to EMI Service

1. **Generate EMI Schedule**: `POST /api/internal/emis/generate`
2. **Check All EMIs Paid**: `GET /api/internal/emis/loan/{loanId}/all-paid`

---

## Running the Service

### Prerequisites

1. **MySQL Database** running on port 3309
2. **Eureka Server** running on port 8761
3. **Auth Service** running on port 8081
4. **Loan Application Service** running on port 8082

### Build and Run

```bash
# Navigate to service directory
cd loan-approval-service

# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Docker Run

```bash
# Build Docker image
docker build -t loan-approval-service:1.0.0 .

# Run container
docker run -p 8083:8083 \
  -e DB_HOST=mysql \
  -e DB_PORT=3309 \
  -e EUREKA_URL=http://eureka-server:8761/eureka/ \
  loan-approval-service:1.0.0
```

---

## Error Handling

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Only PENDING or UNDER_REVIEW loans can be approved"
}
```

### Common Error Codes

- **400 Bad Request**: Validation errors, business rule violations
- **404 Not Found**: Loan, approval, or disbursement not found
- **409 Conflict**: Invalid state transitions
- **500 Internal Server Error**: Unexpected errors

---

## Validation Rules

### Approval Request
- Approved amount must be >= 1000
- Approved amount cannot exceed requested amount
- Interest rate must be between 0.01% and 50%
- Notes optional, max 1000 characters

### Rejection Request
- Rejection reason required, min 10, max 1000 characters
- Notes optional, max 500 characters

### Disbursement Request
- Disbursement date required, cannot be in future
- Disbursement method required, max 50 characters
- Reference number required, max 100 characters
- Remarks optional, max 500 characters

---

## State Transitions

### Loan Status Flow

```
PENDING/UNDER_REVIEW → APPROVED → DISBURSED → CLOSED
                    ↓
                  REJECTED
```

### Valid Operations by Status

| Current Status | Allowed Operations |
|----------------|--------------------|
| PENDING        | Approve, Reject    |
| UNDER_REVIEW   | Approve, Reject    |
| APPROVED       | Disburse           |
| DISBURSED      | Close              |
| REJECTED       | None               |
| CLOSED         | None               |

---

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

### Manual Testing with cURL

**Approve Loan**:
```bash
curl -X POST http://localhost:8083/api/loan-approvals/1/approve \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "approvedAmount": 50000.00,
    "interestRate": 10.50,
    "notes": "Approved"
  }'
```

**Disburse Loan**:
```bash
curl -X POST http://localhost:8083/api/loan-approvals/1/disburse \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "disbursementDate": "2024-01-16",
    "disbursementMethod": "Bank Transfer",
    "referenceNumber": "TXN123456",
    "remarks": "Disbursed successfully"
  }'
```

---

## Monitoring

### Actuator Endpoints

- **Health Check**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

### Logging

- **Level**: DEBUG for `com.loanmanagement`
- **SQL Logging**: Enabled with formatting

---

## Security

### Authentication
- JWT token validation via `JwtAuthenticationFilter`
- Token passed in `Authorization: Bearer <token>` header

### Authorization
- **LOAN_OFFICER**: Can approve, reject, disburse, close loans
- **ADMIN**: Can perform all operations
- **CUSTOMER**: Can view approval/disbursement details

### Internal Endpoints
- No authentication required
- Should be protected by service mesh/API Gateway

---

## Dependencies

### Common Libraries
- `common-dtos`: Shared DTOs (LoanDTO, UserDetailsDTO, etc.)
- `common-security`: JWT authentication filter
- `common-exceptions`: Custom exceptions (BusinessException, ResourceNotFoundException)

---

## Future Enhancements

1. **Notification Service Integration**: Send email/SMS on approval/rejection/disbursement
2. **Audit Trail**: Track all approval decisions with detailed history
3. **Approval Workflow**: Multi-level approval for high-value loans
4. **Document Verification**: Integrate with document service before approval
5. **Credit Score Check**: Integrate with external credit bureaus
6. **Automated Approval**: ML-based auto-approval for low-risk loans

---

## Support

For issues or questions, contact the development team or raise an issue in the project repository.

---

## License

Proprietary - Loan Management System
