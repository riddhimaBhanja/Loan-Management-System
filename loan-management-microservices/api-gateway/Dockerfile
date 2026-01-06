# Multi-stage build for API Gateway
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom first
COPY pom.xml /app-parent/pom.xml
WORKDIR /app-parent
RUN mvn install -N

# Now build the service
WORKDIR /app
COPY api-gateway/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY api-gateway/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose API Gateway port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
