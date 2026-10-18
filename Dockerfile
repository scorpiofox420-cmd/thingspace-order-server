# Build stage using official Maven image with Java 17
FROM maven:3.8.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the jar file using standard maven command
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
