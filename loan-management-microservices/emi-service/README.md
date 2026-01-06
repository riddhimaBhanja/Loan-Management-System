# EMI & Repayment Service

## Overview
The EMI & Repayment Service is a microservice responsible for managing EMI (Equated Monthly Installment) schedules, tracking payments, and calculating outstanding balances for loans.

## Features

### EMI Schedule Management
- Generate EMI schedules for disbursed loans using reducing balance method
- Calculate principal and interest components for each installment
- Track EMI status (PENDING, PAID, OVERDUE, PARTIAL_PAID)
- Automatic marking of overdue EMIs via scheduled job

### EMI Payment Tracking
- Record EMI payments with multiple payment methods
- Support for partial payments
- Transaction reference tracking for audit trail
- Payment history retrieval

### Outstanding Balance Calculation
- Calculate remaining outstanding balance for loans
- Real-time balance updates after payments
- Support for loan closure verification

## Technology Stack
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Cloud** (Eureka Client, OpenFeign)
- **Spring Data JPA** with MySQL
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction

## Database
- **Database**: `emi_db`
- **Port**: 3310
- **Tables**:
  - `emi_schedules` - EMI installment schedules
  - `emi_payments` - Payment records

## Port Configuration
- **Service Port**: 8084
- **Eureka Server**: 8761

## API Endpoints

### Public Endpoints

#### EMI Schedule
- `POST /api/emis/generate` - Generate EMI schedule (LOAN_OFFICER, ADMIN)
- `GET /api/emis/loan/{loanId}` - Get EMI schedule for a loan
- `GET /api/emis/loan/{loanId}/summary` - Get EMI summary
- `GET /api/emis/customer/{customerId}` - Get all EMIs for customer
- `GET /api/emis/overdue` - Get all overdue EMIs (LOAN_OFFICER, ADMIN)
- `GET /api/emis/customer/{customerId}/overdue` - Get overdue EMIs for customer

#### EMI Payment
- `POST /api/emis/{emiScheduleId}/pay` - Record EMI payment (LOAN_OFFICER, ADMIN)
- `GET /api/emis/loan/{loanId}/payments` - Get payment history
- `GET /api/emis/payments/{paymentId}` - Get payment details
- `GET /api/emis/payments/by-reference/{ref}` - Get payment by transaction reference

### Internal Endpoints (Inter-service Communication)
- `GET /api/internal/emis/loan/{loanId}/all-paid` - Check if all EMIs paid
- `GET /api/internal/emis/loan/{loanId}/outstanding` - Get outstanding amount
- `GET /api/internal/emis/mark-overdue` - Manually trigger overdue marking

## EMI Calculation Formula

### Reducing Balance Method
```
EMI = [P × R × (1+R)^N] / [(1+R)^N - 1]

Where:
P = Principal loan amount
R = Monthly interest rate (annual rate / 12 / 100)
N = Tenure in months
```

### Interest and Principal Breakdown
For each installment:
```
Interest Component = Outstanding Balance × Monthly Rate
Principal Component = EMI - Interest Component
Outstanding Balance = Previous Balance - Principal Component
```

## Payment Methods
- CASH
- CHEQUE
- NEFT
- RTGS
- UPI
- DEBIT_CARD
- CREDIT_CARD
- NET_BANKING
- DEMAND_DRAFT

## EMI Status Transitions
```
PENDING → PAID (full payment)
PENDING → PARTIAL_PAID (partial payment)
PENDING → OVERDUE (past due date)
OVERDUE → PAID (full payment)
PARTIAL_PAID → PAID (remaining payment)
```

## Scheduled Jobs
- **Overdue EMI Marking**: Runs daily at 1:00 AM (configurable via `emi.schedule.overdue-check-cron`)

## Inter-service Communication
- **Loan Approval Service**: Get disbursement details, notify loan closure
- **Auth Service**: Get user details for payment recording

## Configuration

### application.yml
```yaml
server:
  port: 8084

spring:
  application:
    name: EMI-SERVICE
  datasource:
    url: jdbc:mysql://localhost:3310/emi_db
    username: root
    password: root

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

emi:
  calculation:
    precision: 10
    scale: 2
  schedule:
    overdue-check-cron: "0 0 1 * * ?"
```

## Running the Service

### Prerequisites
- MySQL database running on port 3310
- Eureka server running on port 8761
- Java 17

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

## Testing

### Sample Generate EMI Request
```json
{
  "loanId": 1,
  "customerId": 2,
  "principal": 100000.00,
  "interestRate": 12.5,
  "tenureMonths": 12,
  "startDate": "2026-02-01"
}
```

### Sample Payment Request
```json
{
  "amount": 8884.88,
  "paymentDate": "2026-02-01",
  "paymentMethod": "UPI",
  "transactionReference": "UPI123456789",
  "remarks": "EMI payment via UPI"
}
```

## Business Rules

1. **EMI Generation**: Can only be done once per loan after disbursement
2. **Payment Recording**: Only LOAN_OFFICER or ADMIN can record payments
3. **Full Payment**: EMI marked as PAID when payment >= EMI amount
4. **Partial Payment**: EMI marked as PARTIAL_PAID when payment < EMI amount
5. **Transaction Reference**: Required for all payment methods except CASH
6. **Overdue Logic**: EMIs with PENDING status past due date are marked OVERDUE
7. **Loan Closure**: All EMIs must be PAID before loan can be closed

## Error Handling
- `ResourceNotFoundException` (404) - EMI/Payment not found
- `BusinessException` (400) - Business rule violations
- `MethodArgumentNotValidException` (400) - Validation errors
- `Exception` (500) - Unexpected errors

## Logging
- Debug level for service operations
- Info level for important business events
- Error level for exceptions

## Security
- JWT-based authentication
- Role-based access control (CUSTOMER, LOAN_OFFICER, ADMIN)
- Internal APIs bypass authentication for inter-service calls
- Customers can only view their own EMI data

## Dependencies
- **common-dtos**: Shared DTOs across services
- **common-security**: JWT authentication and security utilities
- **common-exceptions**: Common exception classes
