# Notification Service

A comprehensive notification and email management microservice for the Loan Management System.

## Overview

The Notification Service handles all email notifications within the loan management system. It provides:
- Template-based email notifications
- Notification history tracking
- Asynchronous email sending
- Retry mechanism for failed notifications
- REST APIs for notification management
- Internal APIs for inter-service communication

## Features

### Core Features
- **Email Notifications**: Send emails using Spring JavaMailSender
- **Template Management**: Create and manage reusable email templates with placeholders
- **Notification History**: Track all sent, pending, and failed notifications
- **Async Processing**: Non-blocking email sending using Spring's @Async
- **Retry Mechanism**: Automatic retry for failed notifications
- **Statistics**: Real-time notification statistics and metrics

### Notification Types
- `LOAN_SUBMITTED` - Loan application submission confirmation
- `LOAN_APPROVED` - Loan approval notification
- `LOAN_REJECTED` - Loan rejection notification
- `LOAN_DISBURSED` - Loan disbursement confirmation
- `EMI_DUE` - EMI payment reminder
- `EMI_PAID` - EMI payment confirmation
- `LOAN_CLOSED` - Loan closure notification
- `PAYMENT_REMINDER` - General payment reminders
- `ACCOUNT_CREATED` - New account creation
- `PASSWORD_RESET` - Password reset requests

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: MySQL
- **Email**: Spring Mail (JavaMailSender)
- **Service Discovery**: Eureka Client
- **Security**: JWT-based authentication
- **Build Tool**: Maven

## Project Structure

```
notification-service/
├── src/main/java/com/loanmanagement/notification/
│   ├── NotificationServiceApplication.java
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Notification.java
│   │   │   ├── NotificationTemplate.java
│   │   │   ├── NotificationType.java
│   │   │   └── NotificationStatus.java
│   │   ├── repository/
│   │   │   ├── NotificationRepository.java
│   │   │   └── NotificationTemplateRepository.java
│   │   └── service/
│   │       ├── NotificationService.java
│   │       ├── NotificationServiceImpl.java
│   │       ├── EmailService.java
│   │       └── EmailServiceImpl.java
│   ├── application/
│   │   ├── controller/
│   │   │   ├── NotificationController.java
│   │   │   └── InternalNotificationController.java
│   │   └── dto/
│   │       ├── request/
│   │       │   ├── SendNotificationRequest.java
│   │       │   └── CreateTemplateRequest.java
│   │       └── response/
│   │           ├── NotificationResponse.java
│   │           └── TemplateResponse.java
│   └── infrastructure/
│       ├── config/
│       │   ├── AsyncConfig.java
│       │   ├── SecurityConfig.java
│       │   └── DataInitializer.java
│       └── exception/
│           └── GlobalExceptionHandler.java
└── src/main/resources/
    └── application.yml
```

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3312/notification_db?createDatabaseIfNotExist=true
    username: loanapp
    password: loanpass123
```

### Email Configuration (SMTP)
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    default-from: noreply@loanmanagement.com
```

**Note**: The service can work without email configuration. If SMTP is not configured, notifications will be logged but not actually sent.

### Server Configuration
```yaml
server:
  port: 8086
```

### Eureka Configuration
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## API Endpoints

### Public/Internal Endpoints (No JWT Required)

#### Health Check
```
GET /api/internal/notifications/health
```

#### Send Notification (Internal)
```
POST /api/internal/notifications/send
Content-Type: application/json

{
  "recipientEmail": "user@example.com",
  "recipientName": "John Doe",
  "subject": "Test Notification",
  "body": "This is a test notification",
  "type": "LOAN_SUBMITTED",
  "userId": 1,
  "loanApplicationId": 100
}
```

#### Send Loan Submitted Notification
```
POST /api/internal/notifications/loan-submitted?email=user@example.com&name=John&loanId=123&amount=50000
```

#### Send Loan Approved Notification
```
POST /api/internal/notifications/loan-approved?email=user@example.com&name=John&loanId=123&amount=50000&approvedAmount=45000
```

#### Send EMI Due Notification
```
POST /api/internal/notifications/emi-due?email=user@example.com&name=John&emiNumber=1&amount=5000&dueDate=2024-01-15
```

### Secured Endpoints (JWT Required)

#### Get All Notifications
```
GET /api/notifications?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

#### Get Notification by ID
```
GET /api/notifications/{id}
Authorization: Bearer <JWT_TOKEN>
```

#### Get Notifications by Type
```
GET /api/notifications/type/LOAN_APPROVED?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

#### Get Notifications by Status
```
GET /api/notifications/status/SENT?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

#### Get Notification Statistics
```
GET /api/notifications/stats
Authorization: Bearer <JWT_TOKEN>
```

#### Retry Failed Notifications (Admin Only)
```
POST /api/notifications/retry
Authorization: Bearer <JWT_TOKEN>
```

### Template Management (Admin Only)

#### Create Template
```
POST /api/notifications/templates
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "custom-loan-approved",
  "type": "LOAN_APPROVED",
  "subject": "Loan Approved - {{loanId}}",
  "bodyTemplate": "<html><body><h2>Dear {{recipientName}},</h2><p>Your loan is approved!</p></body></html>",
  "description": "Custom loan approval template"
}
```

#### Get All Templates
```
GET /api/notifications/templates
Authorization: Bearer <JWT_TOKEN>
```

#### Update Template
```
PUT /api/notifications/templates/{id}
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Delete Template
```
DELETE /api/notifications/templates/{id}
Authorization: Bearer <JWT_TOKEN>
```

## Database Schema

### notifications table
- `id` (BIGINT, Primary Key)
- `recipient_email` (VARCHAR, NOT NULL)
- `recipient_name` (VARCHAR)
- `subject` (VARCHAR, NOT NULL)
- `body` (TEXT, NOT NULL)
- `type` (ENUM, NOT NULL)
- `status` (ENUM, NOT NULL)
- `sent_at` (TIMESTAMP)
- `error_message` (TEXT)
- `retry_count` (INT)
- `user_id` (BIGINT)
- `loan_application_id` (BIGINT)
- `emi_id` (BIGINT)
- `created_at` (TIMESTAMP, NOT NULL)

### notification_templates table
- `id` (BIGINT, Primary Key)
- `name` (VARCHAR, UNIQUE, NOT NULL)
- `type` (ENUM, NOT NULL)
- `subject` (VARCHAR, NOT NULL)
- `body_template` (TEXT, NOT NULL)
- `is_active` (BOOLEAN, NOT NULL)
- `description` (VARCHAR)
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP)

## Template Placeholders

Templates support the following placeholders:
- `{{recipientName}}` - Recipient's name
- `{{loanId}}` - Loan application ID
- `{{loanAmount}}` - Loan amount
- `{{approvedAmount}}` - Approved amount
- `{{emiNumber}}` - EMI number
- `{{amount}}` - Payment amount
- `{{dueDate}}` - Due date
- `{{paymentDate}}` - Payment date
- `{{disbursementDate}}` - Disbursement date
- `{{reason}}` - Rejection reason

## Default Templates

The service comes with pre-configured templates for all notification types:
1. `loan-submitted-default`
2. `loan-approved-default`
3. `loan-rejected-default`
4. `loan-disbursed-default`
5. `emi-due-default`
6. `emi-paid-default`
7. `loan-closed-default`

These are automatically created on application startup.

## Email Service

### SMTP Configuration

For Gmail:
1. Enable 2-factor authentication
2. Generate an app password
3. Use the app password in the configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

### Working Without Email

The service can work without SMTP configuration:
- Notifications are saved to the database
- Email sending is logged but skipped
- All APIs work normally
- Useful for development and testing

## Security

- JWT-based authentication for secured endpoints
- Internal endpoints (no JWT) for inter-service communication
- Role-based access control:
  - `ADMIN` - Full access including template management
  - `LOAN_OFFICER` - Read access to notifications

## Running the Service

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Eureka Server running on port 8761

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Docker (Optional)
```bash
docker-compose up notification-service
```

## Environment Variables

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 3312)
- `DB_USERNAME` - Database username (default: loanapp)
- `DB_PASSWORD` - Database password (default: loanpass123)
- `MAIL_HOST` - SMTP host (default: smtp.gmail.com)
- `MAIL_PORT` - SMTP port (default: 587)
- `MAIL_USERNAME` - Email username
- `MAIL_PASSWORD` - Email password
- `MAIL_FROM` - Default sender email
- `EUREKA_URL` - Eureka server URL
- `JWT_SECRET` - JWT secret key

## Monitoring

Access actuator endpoints:
- Health: `http://localhost:8086/actuator/health`
- Info: `http://localhost:8086/actuator/info`
- Metrics: `http://localhost:8086/actuator/metrics`

## Logging

Logs are available at DEBUG level for:
- `com.loanmanagement` - Application logs
- `org.springframework.mail` - Email sending logs
- `org.springframework.security` - Security logs

## Error Handling

All errors are handled by `GlobalExceptionHandler`:
- `ResourceNotFoundException` - 404 Not Found
- `IllegalArgumentException` - 400 Bad Request
- `MethodArgumentNotValidException` - 400 Validation Error
- `AccessDeniedException` - 403 Forbidden
- `Exception` - 500 Internal Server Error

## Integration with Other Services

Other microservices can call the internal APIs to send notifications:

```java
// Example using RestTemplate
String url = "http://notification-service/api/internal/notifications/loan-approved";
Map<String, String> params = Map.of(
    "email", "user@example.com",
    "name", "John Doe",
    "loanId", "123",
    "amount", "50000",
    "approvedAmount", "45000"
);
restTemplate.postForObject(url, null, NotificationResponse.class, params);
```

## License

Copyright (c) 2024 Loan Management System
