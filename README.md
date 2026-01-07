# Loan Management System 


---

## Project Overview

The **Loan Management System** is an enterprise-grade, full-stack application built using microservices architecture. It provides end-to-end loan lifecycle management from application submission to repayment tracking.



---

## Key Capabilities

| Capability                        | Description                                                            |
| --------------------------------- | ---------------------------------------------------------------------- |
| **Loan Application Management**   | Submit, track, and manage loan applications throughout their lifecycle |
| **Multi-Level Approval Workflow** | Configurable approval hierarchies with integrated risk assessment      |
| **EMI Schedule Generation**       | Automated EMI calculation, schedule generation, and repayment tracking |
| **Document Management**           | Secure document upload, storage, and verification                      |
| **Real-Time Notifications**       | Email and SMS alerts for application updates and EMI reminders         |
| **Analytics & Reporting**         | Comprehensive dashboards and business intelligence reports             |
| **Role-Based Access Control**     | Fine-grained permissions for Admin, Loan Officer, and Customer roles   |
| **Audit Trail**                   | Complete tracking and logging of all system activities                 |

---



### Project Status
- **Version**: 1.0.0
- **Status**: Production Ready
- **License**: Proprietary

---

## System Architecture

### Architecture Diagram

`
![1000275924](https://github.com/user-attachments/assets/11909046-1a22-4ec2-a88c-60e210afd958)

`
---


### Microservices Overview

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| **Service Discovery (Eureka)** | 8761 | - | Service registration and discovery |
| **API Gateway** | 8080 | - | Single entry point, routing, security |
| **Auth Service** | 8081 | auth_db | User authentication, authorization, JWT management |
| **Loan Application Service** | 8082 | loan_db | Loan applications, documents, loan types |
| **Loan Approval Service** | 8083 | approval_db | Approval workflows, credit checks, risk assessment |
| **EMI Service** | 8084 | emi_db | EMI schedules, payment tracking, penalties |
| **Notification Service** | 8085 | notification_db | Email/SMS notifications, templates |
| **Reporting Service** | 8086 | reporting_db | Analytics, dashboards, business intelligence |

---
## Docker Containers running

![DockerRunning](https://github.com/user-attachments/assets/637bbb03-5339-4e6b-bd1f-2cb179e0360b)

## Auth and User service JACOCO Report

![jacoco_auth-service](https://github.com/user-attachments/assets/659cf4eb-1e19-447b-9cdd-f2a120b94bcc)

## Loan Application Service JACOCO Report

![jacoco_loan-application-service](https://github.com/user-attachments/assets/3e45b9c2-b0dc-480e-b70e-2c3321c470b2)




## Technology Stack

### Backend Technologies

#### Core Framework
- **Java**: 17 (LTS)
- **Spring Boot**: 3.2.x
- **Spring Cloud**: 2023.0.x
- **Maven**: 3.9.x

#### Spring Boot Modules
- **Spring Web**: REST API development
- **Spring Data JPA**: Database persistence
- **Spring Security**: Authentication & authorization
- **Spring Cloud Gateway**: API Gateway implementation
- **Spring Cloud Netflix Eureka**: Service discovery
- **Spring Cloud OpenFeign**: Inter-service communication
- **Spring Cloud Circuit Breaker**: Resilience4j integration

#### Security
- **JWT (JSON Web Tokens)**: Stateless authentication
- **BCrypt**: Password encryption
- **JJWT**: JWT library
- **Spring Security**: Security framework

#### Database
- **MySQL**: 8.0+ (Primary database)
- **H2**: In-memory database (Testing)
- **Hibernate**: ORM framework

#### API Documentation
- **Springdoc OpenAPI**: 2.3.0
- **Swagger UI**: Interactive API documentation

#### Testing
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing
- **H2 Database**: Test database

#### Build & DevOps
- **Maven**: Build automation
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

#### Additional Libraries
- **Lombok**: Reduce boilerplate code
- **MapStruct**: Object mapping
- **Jackson**: JSON processing
- **SLF4J & Logback**: Logging
- **Jakarta Validation**: Bean validation

### Frontend Technologies

#### Core Framework
- **Angular**: 17.x
- **TypeScript**: 5.x
- **RxJS**: 7.x (Reactive programming)
- **Node.js**: 18+ (Development server)
- **npm**: 10+ (Package manager)

#### UI & Styling
- **Bootstrap**: 5.x (CSS framework)
- **Angular Material**: UI components (optional)
- **Font Awesome**: Icons
- **SCSS**: CSS preprocessor

#### State Management & HTTP
- **HttpClient**: API communication
- **RxJS Operators**: Asynchronous data handling
- **LocalStorage**: Token persistence

#### Routing & Guards
- **Angular Router**: Navigation
- **Route Guards**: Access control
- **Lazy Loading**: Performance optimization

#### Form Handling
- **Reactive Forms**: Form validation
- **Template-driven Forms**: Simple forms
- **Custom Validators**: Business logic validation

#### Development Tools
- **Angular CLI**: Project scaffolding
- **TypeScript Compiler**: Type checking
- **ESLint**: Code linting
- **Prettier**: Code formatting

---

## Project Structure

### Backend Structure

```
loan-management-microservices/
│
├── api-gateway/                          # API Gateway Service
│   ├── src/main/java/com/loanmanagement/gateway/
│   │   ├── config/
│   │   │   ├── GatewayConfig.java       # Gateway routing configuration
│   │   │   └── OpenApiConfig.java       # Swagger configuration
│   │   ├── filter/
│   │   │   └── JwtAuthenticationFilter.java  # JWT validation filter
│   │   ├── util/
│   │   │   └── JwtUtil.java             # JWT utility methods
│   │   └── ApiGatewayApplication.java   # Main application class
│   ├── src/main/resources/
│   │   └── application.yml              # Gateway configuration
│   └── pom.xml
│
├── service-discovery/                    # Eureka Server
│   ├── src/main/java/com/loanmanagement/discovery/
│   │   └── ServiceDiscoveryApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── auth-service/                         # Authentication Service
│   ├── src/main/java/com/loanmanagement/auth/
│   │   ├── application/                 # Application Layer
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java  # Login, register, token refresh
│   │   │   │   ├── UserController.java  # User management
│   │   │   │   └── InternalUserController.java  # Inter-service APIs
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   └── UpdateUserRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java
│   │   │   │       └── UserResponse.java
│   │   │   └── mapper/
│   │   │       └── UserMapper.java      # MapStruct mapper
│   │   │
│   │   ├── domain/                      # Domain Layer
│   │   │   ├── model/
│   │   │   │   ├── User.java           # User entity
│   │   │   │   ├── UserRole.java       # Role entity
│   │   │   │   └── Role.java           # Role enum
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── UserRoleRepository.java
│   │   │   └── service/
│   │   │       ├── AuthService.java    # Interface
│   │   │       ├── AuthServiceImpl.java
│   │   │       ├── UserService.java
│   │   │       └── UserServiceImpl.java
│   │   │
│   │   ├── infrastructure/              # Infrastructure Layer
│   │   │   ├── config/
│   │   │   │   ├── JpaConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── RestTemplateConfig.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   └── client/
│   │   │       └── NotificationServiceClient.java
│   │   │
│   │   ├── shared/                      # Shared utilities
│   │   │   ├── constants/
│   │   │   │   └── MessageConstants.java
│   │   │   └── util/
│   │   │       └── ValidationUtils.java
│   │   │
│   │   └── AuthServiceApplication.java
│   │
│   ├── src/main/resources/
│   │   ├── application.yml             # Service configuration
│   │   └── db/migration/               # Flyway migrations (optional)
│   │
│   ├── src/test/java/                  # Test classes
│   │   └── com/loanmanagement/auth/
│   │       ├── controller/
│   │       ├── service/
│   │       └── repository/
│   │
│   └── pom.xml
│
├── loan-application-service/            # Loan Application Service
│   ├── src/main/java/com/loanmanagement/loanapp/
│   │   ├── application/
│   │   │   ├── controller/
│   │   │   │   ├── LoanController.java
│   │   │   │   ├── LoanTypeController.java
│   │   │   │   ├── DocumentController.java
│   │   │   │   └── InternalLoanController.java
│   │   │   ├── dto/
│   │   │   └── mapper/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── LoanApplication.java
│   │   │   │   ├── LoanType.java
│   │   │   │   ├── Document.java
│   │   │   │   └── LoanStatus.java
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── infrastructure/
│   │   │   ├── config/
│   │   │   ├── security/
│   │   │   ├── storage/
│   │   │   │   └── FileStorageService.java
│   │   │   └── client/
│   │   └── LoanApplicationServiceApplication.java
│   └── pom.xml
│
├── loan-approval-service/               # Loan Approval Service
│   ├── src/main/java/com/loanmanagement/approval/
│   │   ├── application/
│   │   │   └── controller/
│   │   │       ├── LoanApprovalController.java
│   │   │       └── WorkflowController.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── LoanApproval.java
│   │   │   │   ├── ApprovalStatus.java
│   │   │   │   └── CreditCheck.java
│   │   │   └── service/
│   │   │       ├── ApprovalService.java
│   │   │       ├── CreditScoringService.java
│   │   │       └── RiskAssessmentService.java
│   │   ├── infrastructure/
│   │   └── LoanApprovalServiceApplication.java
│   └── pom.xml
│
├── emi-service/                         # EMI & Repayment Service
│   ├── src/main/java/com/loanmanagement/emi/
│   │   ├── application/
│   │   │   └── controller/
│   │   │       ├── EmiScheduleController.java
│   │   │       ├── EmiPaymentController.java
│   │   │       └── InternalEmiController.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── EmiSchedule.java
│   │   │   │   ├── EmiPayment.java
│   │   │   │   ├── EmiStatus.java
│   │   │   │   └── PaymentMethod.java
│   │   │   └── service/
│   │   │       ├── EmiScheduleService.java
│   │   │       ├── EmiPaymentService.java
│   │   │       └── EmiCalculationService.java
│   │   ├── infrastructure/
│   │   └── EmiServiceApplication.java
│   └── pom.xml
│
├── notification-service/                # Notification Service
│   ├── src/main/java/com/loanmanagement/notification/
│   │   ├── application/
│   │   │   └── controller/
│   │   │       └── NotificationController.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Notification.java
│   │   │   │   ├── NotificationType.java
│   │   │   │   └── EmailTemplate.java
│   │   │   └── service/
│   │   │       ├── EmailService.java
│   │   │       ├── SmsService.java
│   │   │       └── NotificationService.java
│   │   ├── infrastructure/
│   │   └── NotificationServiceApplication.java
│   └── pom.xml
│
├── reporting-service/                   # Reporting Service
│   ├── src/main/java/com/loanmanagement/reporting/
│   │   ├── application/
│   │   │   └── controller/
│   │   │       ├── DashboardController.java
│   │   │       └── ReportController.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   └── service/
│   │   │       ├── DashboardService.java
│   │   │       └── ReportGenerationService.java
│   │   ├── infrastructure/
│   │   └── ReportingServiceApplication.java
│   └── pom.xml
│
├── common-dtos/                         # Shared DTOs
│   ├── src/main/java/com/loanmanagement/common/dto/
│   │   ├── ApiResponse.java
│   │   ├── GenerateEmiRequest.java
│   │   ├── LoanStatusUpdateRequest.java
│   │   └── PageResponse.java
│   └── pom.xml
│
├── common-security/                     # Shared Security Components
│   ├── src/main/java/com/loanmanagement/common/security/
│   │   ├── JwtUtil.java
│   │   └── SecurityUtils.java
│   └── pom.xml
│
├── common-exceptions/                   # Shared Exception Classes
│   ├── src/main/java/com/loanmanagement/common/exception/
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── ValidationException.java
│   └── pom.xml
│
├── docker-compose.yml                   # Docker Compose configuration
├── pom.xml                             # Parent POM
├── README.md                           # This file
└── SWAGGER_DOCUMENTATION.md            # API documentation guide
```

### Frontend Structure

```
loan-management-frontend/
│
├── src/
│   ├── app/
│   │   │
│   │   ├── core/                       # Core Module (Singleton Services)
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts      # Authentication guard
│   │   │   │   └── role.guard.ts      # Authorization guard
│   │   │   │
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts    # Add JWT token to requests
│   │   │   │   ├── error.interceptor.ts   # Global error handling
│   │   │   │   └── loading.interceptor.ts # Loading indicator
│   │   │   │
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts        # Authentication service
│   │   │   │   ├── user.service.ts        # User management
│   │   │   │   ├── loan.service.ts        # Loan operations
│   │   │   │   ├── emi.service.ts         # EMI operations
│   │   │   │   ├── notification.service.ts
│   │   │   │   └── storage.service.ts     # LocalStorage wrapper
│   │   │   │
│   │   │   └── models/
│   │   │       ├── user.model.ts
│   │   │       ├── loan.model.ts
│   │   │       ├── emi.model.ts
│   │   │       └── api-response.model.ts
│   │   │
│   │   ├── modules/                    # Feature Modules
│   │   │   │
│   │   │   ├── auth/                   # Authentication Module
│   │   │   │   ├── login/
│   │   │   │   │   ├── login.component.ts
│   │   │   │   │   ├── login.component.html
│   │   │   │   │   └── login.component.scss
│   │   │   │   ├── register/
│   │   │   │   │   ├── register.component.ts
│   │   │   │   │   ├── register.component.html
│   │   │   │   │   └── register.component.scss
│   │   │   │   └── auth.module.ts
│   │   │   │
│   │   │   ├── dashboard/              # Dashboard Module
│   │   │   │   ├── dashboard.component.ts
│   │   │   │   ├── dashboard.component.html
│   │   │   │   ├── dashboard.component.scss
│   │   │   │   └── dashboard.module.ts
│   │   │   │
│   │   │   ├── loan/                   # Loan Management Module
│   │   │   │   ├── loan-list/
│   │   │   │   │   ├── loan-list.component.ts
│   │   │   │   │   ├── loan-list.component.html
│   │   │   │   │   └── loan-list.component.scss
│   │   │   │   ├── loan-apply/
│   │   │   │   │   ├── loan-apply.component.ts
│   │   │   │   │   ├── loan-apply.component.html
│   │   │   │   │   └── loan-apply.component.scss
│   │   │   │   ├── loan-detail/
│   │   │   │   │   ├── loan-detail.component.ts
│   │   │   │   │   ├── loan-detail.component.html
│   │   │   │   │   └── loan-detail.component.scss
│   │   │   │   ├── loan-review/
│   │   │   │   │   └── loan-review.component.ts
│   │   │   │   ├── document-upload/
│   │   │   │   │   └── document-upload.component.ts
│   │   │   │   ├── document-list/
│   │   │   │   │   └── document-list.component.ts
│   │   │   │   └── loan.module.ts
│   │   │   │
│   │   │   ├── emi/                    # EMI Management Module
│   │   │   │   ├── emi-schedule/
│   │   │   │   │   ├── emi-schedule.component.ts
│   │   │   │   │   ├── emi-schedule.component.html
│   │   │   │   │   └── emi-schedule.component.scss
│   │   │   │   ├── my-emi-schedule/
│   │   │   │   │   └── my-emi-schedule.component.ts
│   │   │   │   ├── overdue-emis/
│   │   │   │   │   └── overdue-emis.component.ts
│   │   │   │   └── emi.module.ts
│   │   │   │
│   │   │   ├── admin/                  # Admin Module
│   │   │   │   ├── user-management/
│   │   │   │   │   ├── user-management.component.ts
│   │   │   │   │   ├── user-management.component.html
│   │   │   │   │   └── user-management.component.scss
│   │   │   │   ├── loan-type-management/
│   │   │   │   │   └── loan-type-management.component.ts
│   │   │   │   ├── reports-dashboard/
│   │   │   │   │   └── reports-dashboard.component.ts
│   │   │   │   └── admin.module.ts
│   │   │   │
│   │   │   ├── profile/                # User Profile Module
│   │   │   │   ├── user-profile/
│   │   │   │   │   ├── user-profile.component.ts
│   │   │   │   │   ├── user-profile.component.html
│   │   │   │   │   └── user-profile.component.scss
│   │   │   │   └── profile.module.ts
│   │   │   │
│   │   │   ├── home/                   # Home Module
│   │   │   │   ├── home.component.ts
│   │   │   │   ├── home.component.html
│   │   │   │   ├── home.component.scss
│   │   │   │   └── home.module.ts
│   │   │   │
│   │   │   └── loan-types/             # Loan Types Module
│   │   │       ├── loan-types.component.ts
│   │   │       ├── loan-types.component.html
│   │   │       └── loan-types.module.ts
│   │   │
│   │   ├── shared/                     # Shared Module
│   │   │   ├── components/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── header/
│   │   │   │   │   │   ├── header.component.ts
│   │   │   │   │   │   ├── header.component.html
│   │   │   │   │   │   └── header.component.scss
│   │   │   │   │   ├── footer/
│   │   │   │   │   │   └── footer.component.ts
│   │   │   │   │   └── sidebar/
│   │   │   │   │       └── sidebar.component.ts
│   │   │   │   │
│   │   │   │   ├── loading-spinner/
│   │   │   │   │   └── loading-spinner.component.ts
│   │   │   │   │
│   │   │   │   ├── confirmation-dialog/
│   │   │   │   │   └── confirmation-dialog.component.ts
│   │   │   │   │
│   │   │   │   ├── page-header/
│   │   │   │   │   └── page-header.component.ts
│   │   │   │   │
│   │   │   │   └── card/
│   │   │   │       └── card.component.ts
│   │   │   │
│   │   │   ├── pipes/
│   │   │   │   ├── date-format.pipe.ts
│   │   │   │   ├── currency-format.pipe.ts
│   │   │   │   └── status-badge.pipe.ts
│   │   │   │
│   │   │   ├── directives/
│   │   │   │   ├── highlight.directive.ts
│   │   │   │   └── tooltip.directive.ts
│   │   │   │
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── app.component.ts            # Root component
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   ├── app.config.ts               # Application configuration
│   │   └── app.routes.ts               # Route configuration
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │
│   ├── environments/
│   │   ├── environment.ts              # Development environment
│   │   └── environment.prod.ts         # Production environment
│   │
│   ├── index.html
│   ├── main.ts
│   └── styles.scss
│
├── angular.json                        # Angular configuration
├── package.json                        # Node dependencies
├── tsconfig.json                       # TypeScript configuration
├── tsconfig.app.json
└── README.md
```

---



## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd loan-management-microservices
```

### 2. Database Setup

#### Create Databases

```sql
-- Connect to MySQL
mysql -u root -p

-- Create databases
CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE loan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE approval_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE emi_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE reporting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user (recommended)
CREATE USER 'loanapp_user'@'localhost' IDENTIFIED BY 'loanapp_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON auth_db.* TO 'loanapp_user'@'localhost';
GRANT ALL PRIVILEGES ON loan_db.* TO 'loanapp_user'@'localhost';
GRANT ALL PRIVILEGES ON approval_db.* TO 'loanapp_user'@'localhost';
GRANT ALL PRIVILEGES ON emi_db.* TO 'loanapp_user'@'localhost';
GRANT ALL PRIVILEGES ON notification_db.* TO 'loanapp_user'@'localhost';
GRANT ALL PRIVILEGES ON reporting_db.* TO 'loanapp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Configure Database Connection

Update `application.yml` in each service with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/[database_name]?useSSL=false&serverTimezone=UTC
    username: loanapp_user
    password: loanapp_password
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 3. Backend Setup

#### Install Backend Dependencies

```bash
# From the root directory (loan-management-microservices)
mvn clean install -DskipTests
```

This will:
- Download all Maven dependencies
- Build common modules (common-dtos, common-security, common-exceptions)
- Build all microservices
- Create JAR files in each service's `target/` directory

#### Configuration Files

Each service has an `application.yml` file in `src/main/resources/`:

**Example: auth-service/src/main/resources/application.yml**
```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service

  datasource:
    url: jdbc:mysql://localhost:3306/auth_db?useSSL=false&serverTimezone=UTC
    username: loanapp_user
    password: loanapp_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

# Eureka Client Configuration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true

# JWT Configuration
jwt:
  secret: YourSuperSecretKeyForJWTTokenGenerationChangeThisInProduction
  expiration: 86400000  # 24 hours in milliseconds

# Logging
logging:
  level:
    com.loanmanagement: DEBUG
    org.springframework: INFO
```

### 4. Frontend Setup

```bash
# Navigate to frontend directory
cd ../loan-management-frontend

# Install dependencies
npm install

# Verify Angular CLI installation
ng version
```

#### Configure API Endpoint

Update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',  // API Gateway URL
  apiTimeout: 30000
};
```

For production (`src/environments/environment.prod.ts`):

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-production-domain.com',
  apiTimeout: 30000
};
```

---

## Running the Application

### Option 1: Manual Start (Development)

#### Start Backend Services (In Order)

1. **Start Service Discovery (Eureka Server)**
   ```bash
   cd service-discovery
   mvn spring-boot:run
   ```
   - Wait for: "Started ServiceDiscoveryApplication"
   - Access: http://localhost:8761

2. **Start API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```
   - Wait for: "Started ApiGatewayApplication"
   - Access: http://localhost:8080

3. **Start Auth Service**
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```
   - Wait for: "Started AuthServiceApplication"

4. **Start Other Services (Can be started in parallel)**
   ```bash
   # Terminal 1
   cd loan-application-service
   mvn spring-boot:run

   # Terminal 2
   cd loan-approval-service
   mvn spring-boot:run

   # Terminal 3
   cd emi-service
   mvn spring-boot:run

   # Terminal 4
   cd notification-service
   mvn spring-boot:run

   # Terminal 5
   cd reporting-service
   mvn spring-boot:run
   ```

#### Start Frontend

```bash
cd loan-management-frontend
npm start
# or
ng serve
```

Access: http://localhost:4200

### Option 2: Using Start Script (Windows)

```bash
# From root directory
start-all-services.bat
```

This script starts all services in order with proper delays.

### Option 3: Using Docker Compose

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up -d --build
```

---

## API Documentation

### Accessing Swagger UI

#### Centralized Documentation (Recommended)
- **URL**: http://localhost:8080/swagger-ui.html
- **Description**: Aggregated documentation for all microservices
- **Features**:
  - Dropdown to select different services
  - Interactive API testing
  - Request/response examples
  - Model schemas

#### Individual Service Documentation

| Service | Swagger UI |
|---------|------------|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Loan Application | http://localhost:8082/swagger-ui.html |
| Loan Approval | http://localhost:8083/swagger-ui.html |
| EMI Service | http://localhost:8084/swagger-ui.html |
| Notification | http://localhost:8085/swagger-ui.html |
| Reporting | http://localhost:8086/swagger-ui.html |

### Using Swagger for API Testing

1. **Authenticate**:
   - Go to Auth Service documentation
   - Use `/api/auth/login` endpoint
   - Copy the JWT token from response

2. **Authorize**:
   - Click "Authorize" button (top right)
   - Enter: `Bearer <your-jwt-token>`
   - Click "Authorize" then "Close"

3. **Test Endpoints**:
   - Expand any endpoint
   - Click "Try it out"
   - Fill parameters
   - Click "Execute"

### Postman Collection

Import the Postman collection for easier testing:
- File: `docs/postman/Loan-Management-System.postman_collection.json`

---

## Database Schema

### Auth Service (auth_db)

**Table: users**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Table: user_roles**
```sql
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_role (user_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Loan Application Service (loan_db)

**Table: loan_applications**
```sql
CREATE TABLE loan_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    loan_type_id BIGINT NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    tenure_months INT NOT NULL,
    purpose TEXT,
    status VARCHAR(50) DEFAULT 'DRAFT',
    application_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Table: loan_types**
```sql
CREATE TABLE loan_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    min_amount DECIMAL(15,2),
    max_amount DECIMAL(15,2),
    min_tenure_months INT,
    max_tenure_months INT,
    interest_rate DECIMAL(5,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Table: documents**
```sql
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_application_id BIGINT NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### EMI Service (emi_db)

**Table: emi_schedules**
```sql
CREATE TABLE emi_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    emi_number INT NOT NULL,
    emi_amount DECIMAL(15,2) NOT NULL,
    principal_component DECIMAL(15,2),
    interest_component DECIMAL(15,2),
    outstanding_balance DECIMAL(15,2),
    due_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_loan_id (loan_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    UNIQUE KEY unique_loan_emi (loan_id, emi_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Table: emi_payments**
```sql
CREATE TABLE emi_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emi_schedule_id BIGINT NOT NULL,
    loan_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50),
    transaction_reference VARCHAR(100) UNIQUE,
    paid_by BIGINT NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emi_schedule_id) REFERENCES emi_schedules(id),
    INDEX idx_loan_id (loan_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Security Implementation

### Authentication Flow

1. **User Login**
   - User sends credentials to `/api/auth/login`
   - Auth Service validates credentials
   - JWT token generated with user details and roles
   - Token returned to client with expiry time

2. **Token Structure**
   ```json
   {
     "sub": "username",
     "userId": 123,
     "roles": ["ROLE_CUSTOMER"],
     "iat": 1704067200,
     "exp": 1704153600
   }
   ```

3. **Subsequent Requests**
   - Client includes token in Authorization header:
     ```
     Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     ```
   - API Gateway validates token
   - User context passed to downstream services

### Authorization

#### Role-Based Access Control (RBAC)

**Roles**:
- `ROLE_CUSTOMER`: Regular users
- `ROLE_LOAN_OFFICER`: Loan processing staff
- `ROLE_APPROVER`: Approval authority
- `ROLE_ADMIN`: System administrators

**Endpoint Protection**:
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/users")
public ResponseEntity<List<UserResponse>> getAllUsers() {
    // Only accessible by ADMIN
}

@PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
@PostMapping("/loans/{id}/review")
public ResponseEntity<ApiResponse> reviewLoan(@PathVariable Long id) {
    // Accessible by LOAN_OFFICER and ADMIN
}
```

### Security Features

1. **Password Encryption**
   - BCrypt hashing with salt
   - Minimum 8 characters with complexity requirements

2. **JWT Token Security**
   - HMAC-SHA256 signature
   - Short expiration time (24 hours)
   - Refresh token mechanism

3. **API Gateway Security**
   - JWT validation filter
   - Rate limiting
   - CORS configuration
   - Request/response logging

4. **SQL Injection Prevention**
   - JPA/Hibernate parameterized queries
   - Input validation

5. **XSS Protection**
   - Output encoding
   - Content Security Policy headers

6. **HTTPS**
   - SSL/TLS encryption in production
   - Secure cookie flags

---

## User Roles & Permissions

### Customer Role
**Capabilities**:
- Register and login
- Apply for loans
- Upload documents
- View own loan applications
- Track application status
- View EMI schedule
- Make EMI payments
- View payment history
- Update profile

**Restrictions**:
- Cannot view other users' loans
- Cannot approve/reject loans
- Cannot access admin features

### Loan Officer Role
**Capabilities**:
- All Customer capabilities
- View all loan applications
- Review loan applications
- Verify documents
- Perform credit assessment
- Request additional information
- Recommend approval/rejection
- Generate reports

**Restrictions**:
- Cannot approve/reject loans (only recommend)
- Cannot manage users
- Cannot configure loan types

### Approver Role
**Capabilities**:
- All Loan Officer capabilities
- Approve loan applications
- Reject loan applications
- Set approval conditions
- Override risk assessments
- Disburse loans
- View approval history

**Restrictions**:
- Cannot manage users
- Cannot configure system settings

### Admin Role
**Capabilities**:
- All system capabilities
- User management (create, update, delete)
- Role assignment
- Loan type configuration
- Interest rate management
- System configuration
- View all reports
- Access logs and audit trails

---

## Features

### 1. User Management
- User registration with email verification
- Secure login with JWT authentication
- Profile management
- Password reset functionality
- Role-based dashboards

### 2. Loan Application
- Multi-step application form
- Document upload (PDF, JPEG, PNG)
- Application draft save
- Loan calculator
- Real-time eligibility check
- Application tracking

### 3. Loan Approval Workflow
- Multi-level approval process
- Credit score integration
- Risk assessment
- Document verification
- Approval/rejection with comments
- Conditional approval
- Loan disbursement

### 4. EMI Management
- Automated EMI schedule generation
- Payment due reminders
- Online payment integration
- Payment history
- Overdue tracking
- Penalty calculation
- Early payment options

### 5. Document Management
- Secure document upload
- Document categorization
- Version control
- Document verification status
- Download capability

### 6. Notifications
- Email notifications
- SMS alerts (optional)
- In-app notifications
- Notification preferences
- Notification history

### 7. Reporting & Analytics
- Loan portfolio dashboard
- Payment analytics
- Default risk reports
- User activity reports
- Custom report generation
- Export to PDF/Excel

### 8. Admin Features
- User management
- Loan type configuration
- Interest rate management
- System configuration
- Audit logs
- Backup management

---

## Testing

### Backend Testing

#### Unit Tests

```bash
# Run all tests
mvn test

# Run tests for specific service
cd auth-service
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceImplTest

# Run with coverage report
mvn clean test jacoco:report
```

**Test Coverage**:
- Target: 80% code coverage
- Reports: `target/site/jacoco/index.html`

#### Integration Tests

```bash
# Run integration tests
mvn verify

# Run with test profile
mvn test -Pintegration-tests
```

#### Test Structure

```
src/test/java/
├── controller/      # Controller layer tests
├── service/         # Service layer tests
├── repository/      # Repository layer tests
├── integration/     # Integration tests
└── util/           # Test utilities
```

### Frontend Testing

#### Unit Tests

```bash
# Run all tests
npm test

# Run with coverage
npm run test:coverage

# Run specific test file
ng test --include='**/auth.service.spec.ts'
```

#### End-to-End Tests

```bash
# Run E2E tests
npm run e2e

# Run with specific configuration
ng e2e --configuration=staging
```

---

