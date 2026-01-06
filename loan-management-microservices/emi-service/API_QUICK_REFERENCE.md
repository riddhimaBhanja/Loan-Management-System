# EMI Service - API Quick Reference

## Base URL
```
http://localhost:8084
```

## Authentication
All endpoints (except `/actuator/**` and `/api/internal/**`) require JWT Bearer token in Authorization header:
```
Authorization: Bearer <jwt-token>
```

---

## EMI Schedule Endpoints

### 1. Generate EMI Schedule
**POST** `/api/emis/generate`

**Roles**: LOAN_OFFICER, ADMIN

**Request Body**:
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

**Response**: Array of EMI schedules
```json
[
  {
    "id": 1,
    "loanId": 1,
    "customerId": 2,
    "emiNumber": 1,
    "emiAmount": 8884.88,
    "principalComponent": 7801.55,
    "interestComponent": 1083.33,
    "dueDate": "2026-02-01",
    "outstandingBalance": 92198.45,
    "status": "PENDING",
    "createdAt": "2026-01-02T10:00:00"
  }
]
```

---

### 2. Get EMI Schedule for Loan
**GET** `/api/emis/loan/{loanId}`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/loan/1`

**Response**: Array of EMI schedules

---

### 3. Get EMI Summary
**GET** `/api/emis/loan/{loanId}/summary`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/loan/1/summary`

**Response**:
```json
{
  "loanId": 1,
  "customerId": 2,
  "totalEmis": 12,
  "paidEmis": 3,
  "pendingEmis": 8,
  "overdueEmis": 1,
  "totalAmount": 106618.56,
  "paidAmount": 26654.64,
  "pendingAmount": 79963.92,
  "outstandingAmount": 73345.36
}
```

---

### 4. Get Customer EMIs
**GET** `/api/emis/customer/{customerId}`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/customer/2`

**Response**: Array of all EMI schedules for the customer

---

### 5. Get Overdue EMIs
**GET** `/api/emis/overdue`

**Roles**: LOAN_OFFICER, ADMIN

**Response**: Array of overdue EMI schedules

---

### 6. Get Customer Overdue EMIs
**GET** `/api/emis/customer/{customerId}/overdue`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/customer/2/overdue`

**Response**: Array of overdue EMI schedules for the customer

---

## EMI Payment Endpoints

### 7. Record EMI Payment
**POST** `/api/emis/{emiScheduleId}/pay`

**Roles**: LOAN_OFFICER, ADMIN

**Path Variable**: `emiScheduleId` - The EMI schedule ID to pay

**Request Body**:
```json
{
  "amount": 8884.88,
  "paymentDate": "2026-02-01",
  "paymentMethod": "UPI",
  "transactionReference": "UPI123456789",
  "remarks": "Payment received via UPI"
}
```

**Response**:
```json
{
  "id": 1,
  "emiScheduleId": 1,
  "loanId": 1,
  "emiNumber": 1,
  "amount": 8884.88,
  "paymentDate": "2026-02-01",
  "paymentMethod": "UPI",
  "transactionReference": "UPI123456789",
  "paidBy": 5,
  "paidByName": "John Officer",
  "remarks": "Payment received via UPI",
  "createdAt": "2026-02-01T14:30:00"
}
```

---

### 8. Get Payment History
**GET** `/api/emis/loan/{loanId}/payments`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/loan/1/payments`

**Response**: Array of payment records

---

### 9. Get Payment by ID
**GET** `/api/emis/payments/{paymentId}`

**Roles**: CUSTOMER, LOAN_OFFICER, ADMIN

**Example**: `/api/emis/payments/1`

**Response**: Payment details

---

### 10. Get Payment by Transaction Reference
**GET** `/api/emis/payments/by-reference/{transactionReference}`

**Roles**: LOAN_OFFICER, ADMIN

**Example**: `/api/emis/payments/by-reference/UPI123456789`

**Response**: Payment details

---

## Internal Endpoints (Inter-service Communication)

### 11. Check if All EMIs Paid
**GET** `/api/internal/emis/loan/{loanId}/all-paid`

**Authentication**: None (Internal only)

**Example**: `/api/internal/emis/loan/1/all-paid`

**Response**: `true` or `false`

---

### 12. Get Outstanding Amount
**GET** `/api/internal/emis/loan/{loanId}/outstanding`

**Authentication**: None (Internal only)

**Example**: `/api/internal/emis/loan/1/outstanding`

**Response**: `73345.36`

---

### 13. Mark Overdue EMIs (Manual Trigger)
**GET** `/api/internal/emis/mark-overdue`

**Authentication**: None (Internal only)

**Response**: Number of EMIs marked as overdue (e.g., `5`)

---

## Payment Methods

| Method | Code | Requires Transaction Reference |
|--------|------|-------------------------------|
| Cash | `CASH` | No |
| Cheque | `CHEQUE` | Yes |
| NEFT | `NEFT` | Yes |
| RTGS | `RTGS` | Yes |
| UPI | `UPI` | Yes |
| Debit Card | `DEBIT_CARD` | Yes |
| Credit Card | `CREDIT_CARD` | Yes |
| Net Banking | `NET_BANKING` | Yes |
| Demand Draft | `DEMAND_DRAFT` | Yes |

---

## EMI Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | EMI not yet paid, due date not passed |
| `PAID` | Full payment received |
| `OVERDUE` | Payment not received, past due date |
| `PARTIAL_PAID` | Partial payment received |

---

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2026-01-02T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "EMI has already been paid",
  "validationErrors": {
    "field": "error message"
  }
}
```

### Common Error Codes

| Status | Error Type | Description |
|--------|-----------|-------------|
| 400 | Bad Request | Validation failed or business rule violation |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Unexpected server error |

---

## cURL Examples

### Generate EMI Schedule
```bash
curl -X POST http://localhost:8084/api/emis/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "loanId": 1,
    "customerId": 2,
    "principal": 100000.00,
    "interestRate": 12.5,
    "tenureMonths": 12,
    "startDate": "2026-02-01"
  }'
```

### Get EMI Schedule
```bash
curl -X GET http://localhost:8084/api/emis/loan/1 \
  -H "Authorization: Bearer <token>"
```

### Record Payment
```bash
curl -X POST http://localhost:8084/api/emis/1/pay \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 8884.88,
    "paymentDate": "2026-02-01",
    "paymentMethod": "UPI",
    "transactionReference": "UPI123456789",
    "remarks": "Payment via UPI"
  }'
```

### Check if All EMIs Paid (Internal)
```bash
curl -X GET http://localhost:8084/api/internal/emis/loan/1/all-paid
```

---

## Health Check
```bash
curl http://localhost:8084/actuator/health
```

**Response**:
```json
{
  "status": "UP"
}
```

---

## Notes

1. **Transaction References**: Required for all payment methods except CASH
2. **Duplicate Checks**: Transaction references must be unique
3. **Partial Payments**: Supported - EMI marked as PARTIAL_PAID if amount < EMI amount
4. **Overdue Marking**: Automatic daily at 1:00 AM, or manual via internal endpoint
5. **Authorization**: Customers can only view their own EMIs, officers/admins can view all
6. **Precision**: All monetary values use 2 decimal places
7. **Date Format**: ISO-8601 (YYYY-MM-DD for dates, YYYY-MM-DDTHH:mm:ss for timestamps)
