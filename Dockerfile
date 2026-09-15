# Stage 1: Build the Spring Boot application using Maven
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight JRE runtime container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose HTTP port (Render, Railway, Heroku dynamically assign PORT)
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=h2 -Dserver.port=${PORT} -jar app.jar"]
