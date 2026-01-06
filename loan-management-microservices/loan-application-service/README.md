# Loan Application Service

Microservice for managing loan types, loan applications, and document uploads.

## Overview

This service handles:
- **Loan Types**: CRUD operations for different types of loans offered
- **Loan Applications**: Creation and management of loan applications
- **Documents**: Upload/download/delete documents related to loan applications

## Architecture

### Package Structure
```
com.loanmanagement.loanapp/
├── domain/
│   ├── model/           # Entities (Loan, LoanType, Document)
│   ├── repository/      # JPA repositories
│   ├── service/         # Business logic implementations
│   └── enums/          # LoanStatus, DocumentType, EmploymentStatus
├── application/
│   ├── controller/      # REST API endpoints
│   ├── dto/            # Request/Response DTOs
│   └── mapper/         # MapStruct mappers
├── infrastructure/
│   ├── config/         # Configuration classes
│   ├── client/         # Inter-service communication
│   └── exception/      # Exception handling
└── shared/
    └── constants/      # Application constants
```

## Key Features

### 1. No JPA Relationships (Microservice Pattern)
- **Loan** entity uses `customerId` (Long) instead of User object
- **Loan** entity uses `loanTypeId` (Long) instead of LoanType object
- **Document** entity uses `loanId` and `uploadedBy` (Long) instead of objects
- Loose coupling between services

### 2. Inter-Service Communication
- `UserServiceClient` for calling auth-service
- `InternalLoanController` for exposing internal APIs to other services
- Uses RestTemplate with Eureka service discovery

### 3. File Storage
- Documents stored in `./uploads/loan-documents/`
- Organized by loan ID (e.g., `./uploads/loan-documents/{loanId}/{fileName}`)
- Supports: PDF, JPG, JPEG, PNG, DOC, DOCX
- Max file size: 10MB

### 4. Security
- JWT validation only (no authentication - handled by auth-service)
- Role-based access control using `@PreAuthorize`
- Roles: CUSTOMER, LOAN_OFFICER, ADMIN

## Database

- **Database**: `loan_app_db`
- **Port**: 3308
- **Tables**:
  - `loan_types` - Loan type configurations
  - `loans` - Loan applications
  - `documents` - Document metadata

## API Endpoints

### Loan Types
- `POST /api/loan-types` - Create loan type (ADMIN)
- `GET /api/loan-types` - Get all loan types
- `GET /api/loan-types/active` - Get active loan types
- `GET /api/loan-types/{id}` - Get loan type by ID
- `PUT /api/loan-types/{id}` - Update loan type (ADMIN)
- `DELETE /api/loan-types/{id}` - Delete loan type (ADMIN)

### Loan Applications
- `POST /api/loans` - Create loan application (CUSTOMER, ADMIN)
- `GET /api/loans/{id}` - Get loan by ID
- `GET /api/loans/my-loans` - Get customer's loans (CUSTOMER)
- `GET /api/loans/customer/{customerId}` - Get customer loans (LOAN_OFFICER, ADMIN)
- `GET /api/loans` - Get all loans (LOAN_OFFICER, ADMIN)
- `GET /api/loans/status/{status}` - Get loans by status (LOAN_OFFICER, ADMIN)

### Documents
- `POST /api/documents/upload` - Upload document (CUSTOMER, ADMIN)
- `GET /api/documents/loan/{loanId}` - Get loan documents
- `GET /api/documents/{documentId}` - Get document metadata
- `GET /api/documents/download/{documentId}` - Download document
- `DELETE /api/documents/{documentId}` - Delete document (CUSTOMER, ADMIN)

### Internal APIs (for microservices)
- `GET /api/internal/loans/{loanId}` - Get loan by ID
- `GET /api/internal/loans/customer/{customerId}` - Get customer loans
- `GET /api/internal/loans/status/{status}` - Get loans by status

## Configuration

### application.yml
```yaml
server:
  port: 8082

spring:
  application:
    name: LOAN-APPLICATION-SERVICE
  datasource:
    url: jdbc:mysql://localhost:3308/loan_app_db
    username: root
    password: root

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

file:
  upload-dir: ./uploads/loan-documents
```

## Dependencies

- Spring Boot 3.2.1
- Spring Cloud 2023.0.0
- Eureka Client (service discovery)
- Spring Data JPA
- MySQL Connector
- Spring Security + JWT
- MapStruct (DTO mapping)
- Lombok
- Common libraries:
  - common-dtos
  - common-security
  - common-exceptions

## Running the Service

1. Start Eureka Server (port 8761)
2. Start MySQL (port 3308)
3. Start Auth Service (port 8081)
4. Start this service:
   ```bash
   mvn spring-boot:run
   ```

Service will register with Eureka as `LOAN-APPLICATION-SERVICE` on port 8082.

## Enums

### LoanStatus
- `PENDING` - Application submitted
- `APPROVED` - Approved by loan officer
- `REJECTED` - Rejected by loan officer
- `DISBURSED` - Funds disbursed
- `CLOSED` - Loan closed

### DocumentType
- `ID_PROOF` - Aadhaar, PAN, Passport
- `INCOME_PROOF` - Salary slips, IT returns
- `ADDRESS_PROOF` - Utility bills
- `BANK_STATEMENT` - Bank statements
- `EMPLOYMENT_PROOF` - Employment letter
- `BUSINESS_PROOF` - GST certificate
- `OTHER` - Other documents

### EmploymentStatus
- `SALARIED`
- `SELF_EMPLOYED`
- `BUSINESS_OWNER`
- `UNEMPLOYED`
- `RETIRED`

## Notes

- This service does NOT handle loan approval/rejection (see loan-approval-service)
- This service does NOT handle loan disbursement (see loan-approval-service)
- Only handles loan application creation and document management
- User information fetched from auth-service when needed
