# Stage 1: Build with Maven 3.9 and Java 17
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw

COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight Java 17 runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xmx300m -Xms256m"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -Dserver.port=${PORT:-8080} -jar app.jar"]
