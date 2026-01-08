# LOAN MANAGEMENT SYSTEM - DEPLOYMENT GUIDE

> **Complete guide for deploying the Loan Management System in various environments**

---

## Table of Contents

1. [Deployment Options](#deployment-options)
2. [Prerequisites](#prerequisites)
3. [Docker Deployment](#docker-deployment)
4. [Manual Deployment](#manual-deployment)
5. [Cloud Deployment](#cloud-deployment)
6. [Environment Configuration](#environment-configuration)
7. [Security Checklist](#security-checklist)
8. [Monitoring & Maintenance](#monitoring--maintenance)
9. [Troubleshooting](#troubleshooting)

---

## Deployment Options

### Option 1: Docker Deployment (Recommended)
- Easiest and fastest deployment
- Consistent across all environments
- Includes all dependencies
- Best for production

### Option 2: Manual Deployment
- More control over configuration
- Requires manual setup of dependencies
- Good for development/testing

### Option 3: Cloud Deployment
- AWS, Azure, Google Cloud, or DigitalOcean
- Scalable and highly available
- Includes CI/CD pipelines

---

## Prerequisites

### For Docker Deployment
- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM minimum
- 20GB disk space

### For Manual Deployment
- Java 17+ JDK
- Node.js 18+
- MySQL 8.0+
- Nginx (for frontend)
- 8GB RAM minimum
- 30GB disk space

---

## Docker Deployment

### Quick Start (5 minutes)

#### 1. Clone Repository
```bash
git clone <repository-url>
cd "Loan Management System"
```

#### 2. Configure Environment
```bash
# Copy environment template
cp .env.example .env

# Edit .env file with your configuration
nano .env
```

**Minimum required changes in .env:**
```env
# Use strong passwords in production
DB_ROOT_PASSWORD=your_strong_password_here
DB_PASSWORD=your_strong_db_password_here

# Generate a strong JWT secret (run: openssl rand -base64 64)
JWT_SECRET=your_256_bit_secret_key_here

# Set production mode
SPRING_PROFILES_ACTIVE=prod
PRODUCTION_MODE=true
```

#### 3. Build and Start Services
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

#### 4. Verify Deployment
```bash
# Check backend health
curl http://localhost:8080/actuator/health

# Check frontend
curl http://localhost:80

# Access application
# Open browser: http://localhost
```

#### 5. Access Application
- **Frontend:** http://localhost (port 80)
- **Backend API:** http://localhost:8080/api
- **Swagger Docs:** http://localhost:8080/swagger-ui.html
- **Default Login:** username: `admin`, password: `admin123`

### Docker Commands Reference

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Stop and remove volumes (CAUTION: deletes all data)
docker-compose down -v

# View logs
docker-compose logs -f [service-name]

# Restart specific service
docker-compose restart backend

# Rebuild after code changes
docker-compose up -d --build

# Execute command in container
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p

# View resource usage
docker stats
```

### Scaling Services

```bash
# Scale backend to 3 instances
docker-compose up -d --scale backend=3

# Note: Requires load balancer configuration
```

---

## Manual Deployment

### Backend Deployment

#### 1. Database Setup
```bash
# Install MySQL 8.0
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# CentOS/RHEL
sudo yum install mysql-server

# Start MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Create database and user
mysql -u root -p
```

```sql
CREATE DATABASE loan_management_db;
CREATE USER 'loanapp'@'localhost' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON loan_management_db.* TO 'loanapp'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Import schema
mysql -u root -p loan_management_db < init-db.sql
```

#### 2. Backend Configuration

Edit `loan-management-backend/src/main/resources/application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loan_management_db?useSSL=true&requireSSL=true
    username: loanapp
    password: ${DB_PASSWORD}

  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
  refresh-expiration: 604800000

logging:
  level:
    root: WARN
    com.loanmanagement: INFO
```

#### 3. Build Backend
```bash
cd loan-management-backend

# Build JAR
mvn clean package -DskipTests

# Or with tests
mvn clean package

# JAR file: target/loan-management-backend-0.0.1-SNAPSHOT.jar
```

#### 4. Run Backend

**Option A: Direct execution**
```bash
java -jar \
  -Dspring.profiles.active=prod \
  -Dserver.port=8080 \
  target/loan-management-backend-0.0.1-SNAPSHOT.jar
```

**Option B: Systemd service (Linux)**

Create `/etc/systemd/system/loan-backend.service`:
```ini
[Unit]
Description=Loan Management Backend
After=mysql.service

[Service]
Type=simple
User=loanapp
WorkingDirectory=/opt/loan-management
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=prod \
  -Xmx2g -Xms1g \
  /opt/loan-management/loan-management-backend.jar
Restart=always
RestartSec=10

Environment="SPRING_DATASOURCE_PASSWORD=your_password"
Environment="JWT_SECRET=your_secret"

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable loan-backend
sudo systemctl start loan-backend
sudo systemctl status loan-backend
```

### Frontend Deployment

#### 1. Build Frontend
```bash
cd loan-management-frontend

# Install dependencies
npm install

# Build for production
ng build --configuration production

# Output: dist/loan-management-frontend
```

#### 2. Configure Nginx

Create `/etc/nginx/sites-available/loan-management`:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/loan-management;
    index index.html;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Angular routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Proxy API requests to backend
    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 3. Deploy Frontend
```bash
# Copy build to web root
sudo mkdir -p /var/www/loan-management
sudo cp -r dist/loan-management-frontend/* /var/www/loan-management/

# Set permissions
sudo chown -R www-data:www-data /var/www/loan-management
sudo chmod -R 755 /var/www/loan-management

# Enable site
sudo ln -s /etc/nginx/sites-available/loan-management /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## Cloud Deployment

### AWS Deployment

#### Architecture
- **EC2 Instances:** Backend and Frontend
- **RDS MySQL:** Database
- **S3:** Static assets (optional)
- **CloudFront:** CDN (optional)
- **ELB:** Load Balancer
- **Route 53:** DNS

#### Steps

**1. Launch RDS MySQL Instance**
```bash
# Via AWS Console or CLI
aws rds create-db-instance \
  --db-instance-identifier loan-management-db \
  --db-instance-class db.t3.medium \
  --engine mysql \
  --engine-version 8.0 \
  --master-username admin \
  --master-user-password <strong-password> \
  --allocated-storage 20 \
  --backup-retention-period 7
```

**2. Launch EC2 Instance for Backend**
```bash
# Ubuntu 22.04 LTS, t3.medium or larger
# Security Group: Allow ports 8080, 22

# SSH into instance
ssh -i your-key.pem ubuntu@ec2-instance-ip

# Install Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# Copy JAR and run (use systemd service as shown above)
```

**3. Launch EC2 Instance for Frontend**
```bash
# Install Nginx
sudo apt update
sudo apt install nginx

# Copy built Angular app
# Configure Nginx as shown above
```

**4. Configure Load Balancer**
- Create Application Load Balancer
- Add target groups for backend and frontend
- Configure health checks
- Set up SSL/TLS certificate (AWS Certificate Manager)

**5. Configure Domain**
- Use Route 53 to point domain to Load Balancer
- Set up HTTPS with ACM certificate

### Docker on AWS (ECS/Fargate)

**1. Push images to ECR**
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag loan-management-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/loan-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/loan-backend:latest
```

**2. Create ECS Task Definition**
```json
{
  "family": "loan-management",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/loan-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:..."
        }
      ]
    }
  ]
}
```

**3. Create ECS Service**
```bash
aws ecs create-service \
  --cluster loan-management-cluster \
  --service-name loan-backend \
  --task-definition loan-management \
  --desired-count 2 \
  --launch-type FARGATE
```

---

## Environment Configuration

### Development Environment
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loan_management_db
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    root: INFO
    com.loanmanagement: DEBUG
```

### Production Environment
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=true
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    root: WARN
    com.loanmanagement: INFO
```

### Environment Variables

```bash
# Set environment variables
export DB_HOST=your-db-host
export DB_PASSWORD=your-password
export JWT_SECRET=your-secret

# Or use .env file with docker-compose
```

---

## Security Checklist

### Pre-Deployment Security

- [ ] Change all default passwords
- [ ] Generate strong JWT secret (256+ bits)
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules
- [ ] Set up database SSL connections
- [ ] Enable CORS only for trusted domains
- [ ] Disable unnecessary actuator endpoints
- [ ] Set secure HTTP headers
- [ ] Enable SQL injection protection
- [ ] Configure rate limiting
- [ ] Set up intrusion detection
- [ ] Enable audit logging
- [ ] Configure backup strategy
- [ ] Set up monitoring and alerting

### Production Security Settings

**application-prod.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: never

spring:
  security:
    require-ssl: true
```

**Nginx Security Headers:**
```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Content-Security-Policy "default-src 'self'" always;
```

---

## Monitoring & Maintenance

### Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Frontend health
curl http://localhost/health
```

### Logging

**Backend logs:**
```bash
# View logs
tail -f /var/log/loan-management/application.log

# Docker logs
docker-compose logs -f backend
```

**Nginx logs:**
```bash
# Access logs
tail -f /var/log/nginx/access.log

# Error logs
tail -f /var/log/nginx/error.log
```

### Database Backups

```bash
# Manual backup
mysqldump -u root -p loan_management_db > backup_$(date +%Y%m%d).sql

# Automated backup (cron)
# Add to crontab: crontab -e
0 2 * * * mysqldump -u root -p<password> loan_management_db > /backups/loan_db_$(date +\%Y\%m\%d).sql
```

### Performance Monitoring

**Install monitoring tools:**
- Prometheus for metrics
- Grafana for dashboards
- ELK Stack for log aggregation
- New Relic or Datadog for APM

---

## Troubleshooting

### Common Issues

#### 1. Backend won't start
```bash
# Check logs
docker-compose logs backend

# Common causes:
# - Database connection failed: Check DB_HOST, DB_PASSWORD
# - Port already in use: Change BACKEND_PORT
# - Out of memory: Increase Docker memory limit
```

#### 2. Frontend shows 404 for API calls
```bash
# Check Nginx configuration
sudo nginx -t

# Verify backend is running
curl http://localhost:8080/actuator/health

# Check proxy settings in nginx.conf
```

#### 3. Database connection timeout
```bash
# Check MySQL is running
docker-compose ps mysql

# Check connection
docker-compose exec mysql mysql -u loanapp -p -e "SELECT 1"

# Increase timeout in application.yml
spring:
  datasource:
    hikari:
      connection-timeout: 60000
```

#### 4. JWT token errors
```bash
# Ensure JWT_SECRET is set correctly
# Minimum 256 bits for HS512

# Check token expiration settings
# Verify system clocks are synchronized
```

### Performance Optimization

**Backend:**
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

server:
  tomcat:
    max-threads: 200
    max-connections: 10000
```

**Frontend:**
```bash
# Enable gzip compression
# Minify assets
# Use CDN for static files
# Enable browser caching
```

---

## Post-Deployment Checklist

- [ ] All services are running
- [ ] Health checks pass
- [ ] Can login as admin
- [ ] Can create customer account
- [ ] Can apply for loan
- [ ] Can approve/reject loan
- [ ] EMI schedule generates correctly
- [ ] All API endpoints work
- [ ] SSL certificate valid
- [ ] Backups configured
- [ ] Monitoring enabled
- [ ] Logs rotating properly
- [ ] Security scan passed
- [ ] Performance tests passed
- [ ] Documentation updated

---

## Support

For issues and questions:
- Check logs first
- Review this deployment guide
- Check [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md)
- Review [PROJECT_COMPLETION_SUMMARY.md](PROJECT_COMPLETION_SUMMARY.md)

---

**Last Updated:** December 2025
**Version:** 1.0.0
**Status:** Production-Ready
