# Multi-stage build for Reporting Service
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app


# Copy parent pom first
COPY pom.xml /app-parent/pom.xml
WORKDIR /app-parent
RUN mvn install -N

# Now build the service
WORKDIR /app
# Copy common libraries and build them first
COPY common-library/common-dtos /app/common-library/common-dtos
COPY common-library/common-security /app/common-library/common-security
COPY common-library/common-exceptions /app/common-library/common-exceptions

# Build and install common libraries
WORKDIR /app/common-library/common-dtos
RUN mvn clean install -DskipTests

WORKDIR /app/common-library/common-security
RUN mvn clean install -DskipTests

WORKDIR /app/common-library/common-exceptions
RUN mvn clean install -DskipTests

# Now build the reporting service
WORKDIR /app/reporting-service

# Copy pom.xml and download dependencies
COPY reporting-service/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY reporting-service/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/reporting-service/target/*.jar app.jar

# Expose Reporting Service port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
