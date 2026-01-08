# LOAN MANAGEMENT SYSTEM - COMPLETE SETUP INSTRUCTIONS

> **Comprehensive step-by-step guide for setting up the Loan Management System**

**Version:** 1.0.0
**Last Updated:** December 28, 2025
**Estimated Setup Time:** 15-20 minutes

---

## 📋 Table of Contents

1. [Prerequisites Installation](#1-prerequisites-installation)
2. [Database Setup](#2-database-setup)
3. [Backend Setup](#3-backend-setup)
4. [Frontend Setup](#4-frontend-setup)
5. [Docker Setup](#5-docker-setup-alternative)
6. [Verification](#6-verification)
7. [Troubleshooting](#7-troubleshooting)
8. [Environment Configuration](#8-environment-configuration)

---

## 1. Prerequisites Installation

### 1.1 Java 17+ Installation

**Windows:**
```bash
# Download from https://adoptium.net/
# Or use Chocolatey
choco install temurin17
```

**macOS:**
```bash
# Using Homebrew
brew install openjdk@17
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Verify Installation:**
```bash
java -version
# Should show: openjdk version "17.x.x"
```

### 1.2 Node.js 18+ and npm Installation

**Windows:**
```bash
# Download from https://nodejs.org/
# Or use Chocolatey
choco install nodejs-lts
```

**macOS:**
```bash
# Using Homebrew
brew install node@18
```

**Linux (Ubuntu/Debian):**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

**Verify Installation:**
```bash
node --version  # Should show v18.x.x or higher
npm --version   # Should show 9.x.x or higher
```

### 1.3 MySQL 8.0+ Installation

**Windows:**
```bash
# Download from https://dev.mysql.com/downloads/mysql/
# Or use Chocolatey
choco install mysql
```

**macOS:**
```bash
# Using Homebrew
brew install mysql@8.0
brew services start mysql@8.0
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

**Verify Installation:**
```bash
mysql --version
# Should show: mysql  Ver 8.0.x

# Login to MySQL
mysql -u root -p
```

### 1.4 Git Installation

**Windows:**
```bash
# Download from https://git-scm.com/
# Or use Chocolatey
choco install git
```

**macOS:**
```bash
brew install git
```

**Linux:**
```bash
sudo apt install git
```

**Verify Installation:**
```bash
git --version
```

### 1.5 Maven 3.9+ (Optional - bundled with project)

**Windows:**
```bash
choco install maven
```

**macOS:**
```bash
brew install maven
```

**Linux:**
```bash
sudo apt install maven
```

**Note:** The project uses Maven Wrapper (`mvnw`), so Maven installation is optional.

### 1.6 Angular CLI (Optional but recommended)

```bash
npm install -g @angular/cli@17
```

**Verify:**
```bash
ng version
```

---

## 2. Database Setup

### 2.1 Start MySQL Service

**Windows:**
```bash
net start MySQL80
```

**macOS:**
```bash
brew services start mysql@8.0
# Or
mysql.server start
```

**Linux:**
```bash
sudo systemctl start mysql
```

### 2.2 Secure MySQL Installation (First Time Only)

```bash
sudo mysql_secure_installation
```

**Follow prompts:**
- Set root password
- Remove anonymous users: Yes
- Disallow root login remotely: Yes
- Remove test database: Yes
- Reload privilege tables: Yes

### 2.3 Create Database and User

**Method 1: Using MySQL Command Line**

```bash
# Login as root
mysql -u root -p

# Or on Linux if no password set
sudo mysql
```

**Run these SQL commands:**

```sql
-- Create database
CREATE DATABASE loan_management_db;

-- Create user
CREATE USER 'loanapp'@'localhost' IDENTIFIED BY 'loanpass123';

-- Grant privileges
GRANT ALL PRIVILEGES ON loan_management_db.* TO 'loanapp'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user='loanapp';

-- Exit
EXIT;
```

**Method 2: Using SQL Script**

```bash
# Save the above SQL to create-db.sql, then:
mysql -u root -p < create-db.sql
```

### 2.4 Initialize Database Schema

The application will automatically create tables on first run using JPA/Hibernate.

**OR manually initialize:**

```bash
# Navigate to project directory
cd "C:\Users\KIIT\Desktop\Loan Management System"

# Run initialization script
mysql -u loanapp -p loan_management_db < init-db.sql
```

### 2.5 Verify Database Setup

```bash
# Login as loanapp user
mysql -u loanapp -p

# Use database
USE loan_management_db;

# Check tables (should be empty initially)
SHOW TABLES;

# Exit
EXIT;
```

---

## 3. Backend Setup

### 3.1 Clone/Navigate to Project

```bash
# If cloning from Git
git clone <repository-url>
cd "Loan Management System"

# Or navigate to existing project
cd "C:\Users\KIIT\Desktop\Loan Management System"
```

### 3.2 Navigate to Backend Directory

```bash
cd loan-management-backend
```

### 3.3 Configure Application Properties

**File:** `src/main/resources/application.yml`

**Review and update if needed:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loan_management_db?useSSL=false&serverTimezone=UTC
    username: loanapp
    password: loanpass123

  jpa:
    hibernate:
      ddl-auto: update  # Creates tables automatically
    show-sql: true      # Shows SQL queries in console

jwt:
  secret: YourProductionSecretKeyForJWTTokenGenerationShouldBeAtLeast256BitsLongForHS512Algorithm
  expiration: 86400000        # 24 hours in milliseconds
  refresh-expiration: 604800000  # 7 days in milliseconds
```

**Important Configuration Options:**

- **ddl-auto:**
  - `create` - Drops and recreates tables (DANGER: loses data)
  - `update` - Updates schema without losing data (Recommended for dev)
  - `validate` - Only validates schema (Recommended for prod)

- **show-sql:**
  - `true` - Shows SQL in console (good for debugging)
  - `false` - Hides SQL (recommended for production)

### 3.4 Install Dependencies

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install

# To skip tests (faster)
./mvnw clean install -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2-3 minutes
```

### 3.5 Run Backend Application

**Method 1: Using Maven (Development)**

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using system Maven
mvn spring-boot:run
```

**Method 2: Using JAR (Production-like)**

```bash
# Build JAR
./mvnw clean package

# Run JAR
java -jar target/loan-management-backend-0.0.1-SNAPSHOT.jar
```

**Method 3: Using IDE**

- **IntelliJ IDEA:**
  1. Open `loan-management-backend` as Maven project
  2. Right-click `LoanManagementApplication.java`
  3. Select "Run 'LoanManagementApplication'"

- **Eclipse:**
  1. Import as Maven project
  2. Right-click project → Run As → Spring Boot App

**Method 4: With Custom Profile**

```bash
# Run with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Or
java -jar target/loan-management-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3.6 Verify Backend is Running

**Check Console Output:**
```
Started LoanManagementApplication in X.XXX seconds
```

**Check Health Endpoint:**
```bash
# Windows PowerShell
Invoke-WebRequest -Uri http://localhost:8080/actuator/health

# Linux/Mac or Git Bash
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

---

## 4. Frontend Setup

### 4.1 Navigate to Frontend Directory

**Open NEW terminal/command prompt** (keep backend running)

```bash
cd "C:\Users\KIIT\Desktop\Loan Management System"
cd loan-management-frontend
```

### 4.2 Install Dependencies

```bash
npm install
```

**If you encounter errors, try:**

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install

# Or use legacy peer deps
npm install --legacy-peer-deps
```

**Expected Output:**
```
added XXX packages in YYs
```

### 4.3 Configure Environment

**File:** `src/environments/environment.ts`

**Verify configuration:**

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

**For production:** `src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'http://your-production-domain.com/api'
};
```

### 4.4 Run Frontend Application

**Method 1: Using Angular CLI**

```bash
ng serve
```

**Method 2: Using npm**

```bash
npm start
```

**Method 3: With Custom Port**

```bash
ng serve --port 4200

# Or with host binding
ng serve --host 0.0.0.0 --port 4200
```

**Method 4: Production Build (Testing)**

```bash
# Build
ng build --configuration production

# Serve build (using http-server)
npm install -g http-server
http-server dist/loan-management-frontend -p 4200
```

### 4.5 Verify Frontend is Running

**Check Console Output:**
```
** Angular Live Development Server is listening on localhost:4200 **
✔ Compiled successfully.
```

**Access Application:**
```
http://localhost:4200
```

**Browser should open automatically** showing the login page.

---

## 5. Docker Setup (Alternative)

If you prefer Docker, skip sections 2-4 and follow these steps:

### 5.1 Install Docker Desktop

**Download and install:**
- Windows/Mac: https://www.docker.com/products/docker-desktop
- Linux: https://docs.docker.com/engine/install/

**Verify Installation:**
```bash
docker --version
docker-compose --version
```

### 5.2 Configure Environment

```bash
# Navigate to project root
cd "C:\Users\KIIT\Desktop\Loan Management System"

# Copy environment template
copy .env.example .env

# Edit .env with your settings
notepad .env  # Windows
nano .env     # Linux/Mac
```

**Key settings in .env:**
```env
DB_ROOT_PASSWORD=your_strong_root_password
DB_PASSWORD=your_strong_password
JWT_SECRET=your_256_bit_secret_key_here
```

**Generate strong JWT secret:**
```bash
# Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))

# Linux/Mac
openssl rand -base64 64
```

### 5.3 Build and Start Services

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

**Expected Output:**
```
Creating loan-management-mysql    ... done
Creating loan-management-backend  ... done
Creating loan-management-frontend ... done
```

### 5.4 Verify Docker Deployment

```bash
# Run verification script
# Windows
verify-deployment.bat

# Linux/Mac
chmod +x verify-deployment.sh
./verify-deployment.sh
```

**OR manually verify:**

```bash
# Check MySQL
docker-compose exec mysql mysql -u loanapp -p loan_management_db -e "SHOW TABLES;"

# Check Backend
curl http://localhost:8080/actuator/health

# Check Frontend
curl http://localhost/
```

**Access Application:**
- Frontend: http://localhost
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

---

## 6. Verification

### 6.1 Backend Verification

**1. Health Check:**
```bash
curl http://localhost:8080/actuator/health
```
Expected: `{"status":"UP"}`

**2. Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```
Should display API documentation

**3. Test Authentication Endpoint:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```
Should return 400 or 401 (endpoint exists)

### 6.2 Frontend Verification

**1. Access Application:**
```
http://localhost:4200
```
Should show login page

**2. Check Console:**
- Open browser DevTools (F12)
- No JavaScript errors
- Network tab shows successful requests

**3. Test Navigation:**
- Login page loads
- Register link works
- Forms are visible

### 6.3 Database Verification

```bash
# Login to MySQL
mysql -u loanapp -p

USE loan_management_db;

# Check tables
SHOW TABLES;

# Should see:
# - users
# - roles
# - user_roles
# - loan_types
# - loans
# - emi_schedule
# - refresh_tokens

# Check default data
SELECT * FROM roles;
SELECT * FROM users;
SELECT * FROM loan_types;

EXIT;
```

### 6.4 End-to-End Test

**Complete User Flow:**

1. **Access Frontend:** http://localhost:4200

2. **Login as Admin:**
   - Username: `admin`
   - Password: `admin123`
   - Should redirect to dashboard

3. **Create Customer:**
   - Logout
   - Click "Register"
   - Fill form:
     - Username: testuser
     - Email: test@example.com
     - Password: test123
     - Full Name: Test User
     - Phone: +1234567890
   - Submit
   - Should auto-login and redirect to dashboard

4. **Apply for Loan:**
   - Navigate to "Apply for Loan"
   - Select loan type (e.g., Personal Loan)
   - Enter details:
     - Amount: 100000
     - Tenure: 24 months
     - Employment: Salaried
     - Monthly Income: 50000
     - Purpose: Test application
   - Submit
   - Should see success message

5. **View Application:**
   - Navigate to "My Loans"
   - Should see application with status "APPLIED"

6. **Approve as Admin:**
   - Logout, login as admin
   - Navigate to "Loans"
   - Find test application
   - Click "Review"
   - Mark as "Under Review"
   - Approve with:
     - Approved Amount: 100000
     - Interest Rate: 12.5
   - Should see success message

7. **View EMI Schedule:**
   - Click on approved loan
   - Click "View EMI Schedule"
   - Should see 24 EMIs with breakdown

✅ **If all steps work, setup is complete!**

---

## 7. Troubleshooting

### 7.1 Backend Issues

**Issue:** Port 8080 already in use

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml
server:
  port: 8081
```

**Issue:** Database connection failed

```bash
# Verify MySQL is running
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl status mysql

# Check credentials in application.yml
# Try connecting manually
mysql -u loanapp -p
```

**Issue:** `java.lang.OutOfMemoryError`

```bash
# Increase heap size
java -Xmx2g -jar target/loan-management-backend-0.0.1-SNAPSHOT.jar

# Or in IDE VM options:
-Xmx2g -Xms512m
```

**Issue:** Schema validation errors

```yaml
# In application.yml, temporarily use:
spring:
  jpa:
    hibernate:
      ddl-auto: create  # WARNING: Drops existing tables!
```

### 7.2 Frontend Issues

**Issue:** Port 4200 already in use

```bash
# Use different port
ng serve --port 4201

# Or kill process
# Windows
netstat -ano | findstr :4200
taskkill /PID <process_id> /F

# Linux/Mac
lsof -ti:4200 | xargs kill -9
```

**Issue:** `npm install` fails

```bash
# Clear cache
npm cache clean --force

# Delete and reinstall
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

**Issue:** CORS errors in browser

```yaml
# In backend application.yml, verify:
spring:
  web:
    cors:
      allowed-origins: http://localhost:4200
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
```

**Issue:** `Cannot find module '@angular/...'`

```bash
# Reinstall Angular CLI
npm uninstall -g @angular/cli
npm install -g @angular/cli@17

# Clear node_modules
rm -rf node_modules package-lock.json
npm install
```

### 7.3 Database Issues

**Issue:** Access denied for user 'loanapp'

```sql
-- Login as root
mysql -u root -p

-- Recreate user
DROP USER IF EXISTS 'loanapp'@'localhost';
CREATE USER 'loanapp'@'localhost' IDENTIFIED BY 'loanpass123';
GRANT ALL PRIVILEGES ON loan_management_db.* TO 'loanapp'@'localhost';
FLUSH PRIVILEGES;
```

**Issue:** Table doesn't exist

```bash
# Delete database and recreate
mysql -u root -p

DROP DATABASE loan_management_db;
CREATE DATABASE loan_management_db;
EXIT;

# Restart backend (it will create tables)
```

### 7.4 Docker Issues

**Issue:** Container won't start

```bash
# Check logs
docker-compose logs mysql
docker-compose logs backend
docker-compose logs frontend

# Restart containers
docker-compose restart

# Rebuild
docker-compose down
docker-compose up -d --build
```

**Issue:** Out of disk space

```bash
# Clean up Docker
docker system prune -a
docker volume prune
```

---

## 8. Environment Configuration

### 8.1 Development Environment

**Backend:** `src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loan_management_db
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
logging:
  level:
    com.loanmanagement: DEBUG
```

**Frontend:** `src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

**Run:**
```bash
# Backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
ng serve
```

### 8.2 Production Environment

**Backend:** `src/main/resources/application-prod.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
logging:
  level:
    root: WARN
    com.loanmanagement: INFO
```

**Frontend:** `src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api'
};
```

**Build:**
```bash
# Backend
./mvnw clean package -Pproduction

# Frontend
ng build --configuration production
```

---

## 9. Next Steps

After successful setup:

1. **Change Default Passwords**
   - Login as admin
   - Navigate to profile
   - Change password

2. **Create Test Data**
   - Register customers
   - Create loan applications
   - Test approval workflow

3. **Explore Features**
   - Dashboard
   - Loan application
   - EMI schedules
   - Admin panel

4. **Review Documentation**
   - API Documentation: http://localhost:8080/swagger-ui.html
   - PROJECT_COMPLETION_SUMMARY.md
   - DEPLOYMENT_GUIDE.md

5. **Run Tests**
   ```bash
   cd loan-management-backend
   ./mvnw test
   ```

---

## 10. Quick Reference Commands

### Backend Commands

```bash
# Start
./mvnw spring-boot:run

# Build
./mvnw clean package

# Test
./mvnw test

# With profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Frontend Commands

```bash
# Install
npm install

# Start
ng serve

# Build
ng build

# Production build
ng build --configuration production
```

### Database Commands

```bash
# Login
mysql -u loanapp -p

# Show databases
SHOW DATABASES;

# Use database
USE loan_management_db;

# Show tables
SHOW TABLES;

# Exit
EXIT;
```

### Docker Commands

```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# Logs
docker-compose logs -f

# Restart
docker-compose restart

# Rebuild
docker-compose up -d --build
```

---

## 📞 Support

If you encounter issues not covered in this guide:

1. Check [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Troubleshooting section
2. Review application logs
3. Verify all prerequisites are installed correctly
4. Check firewall/antivirus settings
5. Ensure ports are not blocked

---

**Setup Complete!** 🎉

You now have a fully functional Loan Management System running locally.

**Access Points:**
- Frontend: http://localhost:4200
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Database: localhost:3306

**Default Login:** admin / admin123

---

**Last Updated:** December 28, 2025
**Version:** 1.0.0
**Status:** Production-Ready
