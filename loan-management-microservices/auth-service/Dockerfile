# Multi-stage build for Auth Service
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy parent pom and set it up
COPY pom.xml .

# Copy all common libraries
COPY common-library ./common-library

# Install parent and common libraries
RUN mvn install -N -DskipTests && \
    cd common-library/common-dtos && mvn clean install -DskipTests && \
    cd ../common-exceptions && mvn clean install -DskipTests && \
    cd ../common-security && mvn clean install -DskipTests

# Copy auth-service
COPY auth-service ./auth-service

# Build auth-service
WORKDIR /build/auth-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /build/auth-service/target/*.jar app.jar

# Expose Auth Service port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
