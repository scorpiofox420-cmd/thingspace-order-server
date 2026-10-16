# Stage 1: Build Java Application
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app
COPY . .

# Grant execute permissions and skip unit tests during build
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Runtime Image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
