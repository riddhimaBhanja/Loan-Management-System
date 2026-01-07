# Loan Management System

Enterprise-grade microservices application for end-to-end loan lifecycle management with role-based access control, automated workflows, and real-time notifications.

## Tech Stack

**Backend**
- Java 17, Spring Boot 3.x
- Spring Cloud (Gateway, Eureka)
- Spring Security with JWT
- REST APIs
- MySQL/PostgreSQL
- Docker & Docker Compose
- Maven

**Frontend**
- Angular 17
- TypeScript
- RxJS
- Bootstrap

## Architecture

Microservices-based system with the following components:

**API Gateway** - Single entry point, routing, and load balancing

**Service Registry** - Eureka for service discovery and health monitoring

**Auth Service** - Authentication, authorization, JWT management, user roles

**Loan Application Service** - Loan requests, document upload, application tracking, loan type management

**Loan Approval Service** - Multi-level approval workflows, risk assessment, credit scoring

**EMI Service** - Payment schedules, installment tracking, payment processing, penalty management

**Notification Service** - Email/SMS notifications for updates, reminders, approvals

**Reporting Service** - Analytics dashboards, portfolio reports, business intelligence

## Setup & Run

**Prerequisites**
- Docker & Docker Compose
- Java 17+ (local development)
- Node.js 18+ & npm (frontend)
- MySQL/PostgreSQL (local development)

**Using Docker**

```bash
docker-compose up -d
```

Access points:
- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

**Local Development**

```bash
# Backend services
cd [service-name] && mvn spring-boot:run

# Frontend
cd loan-management-frontend
npm install && npm start
```

## User Roles & Features

**Admin**
- User and role management
- System configuration
- Loan type and interest rate setup
- Complete system oversight

**Loan Officer**
- Application review
- Document verification
- Credit assessment
- Application processing

**Approver**
- Approval/rejection workflows
- Risk evaluation
- Multi-level authorization
- Compliance verification

**Customer**
- Loan application submission
- Document upload
- Application status tracking
- EMI payment and history

## Security

- JWT-based authentication and authorization
- Role-based access control (RBAC)
- BCrypt password encryption
- API Gateway security filters
- HTTPS support
- Request/response validation
- SQL injection and XSS protection
- CORS configuration
- Token refresh mechanism

## API Documentation

Swagger/OpenAPI available at:
- API Gateway: http://localhost:8080/swagger-ui.html

