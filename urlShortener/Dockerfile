# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first, useful for caching
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copy the executable JAR from the build stage
COPY --from=build /app/target/*.jar urlShortener.jar

# Set environment variables with default port
ENV PORT=8080

# Expose the port
EXPOSE ${PORT}

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "urlShortener.jar"]