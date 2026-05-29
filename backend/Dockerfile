# Stage 1: Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml and download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy src and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built jar from the build stage
COPY --from=build /app/target/RapPhim-1.0.0.jar app.jar

# Expose backend REST API port
EXPOSE 5001

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
