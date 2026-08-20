# Multi-stage Docker Build for Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy backend pom and source code
COPY backend/pom.xml .
COPY backend/src ./src

# Package the application (skip tests for fast build)
RUN mvn clean package -DskipTests

# Lightweight Production Runtime Image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
