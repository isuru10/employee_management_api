# Build Stage
FROM amazoncorretto:21-alpine AS build
WORKDIR /app

# Copy Maven wrapper configuration and dependencies descriptor
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Resolve dependencies to cache them in the Docker layer using BuildKit cache mount
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# Copy application source code
COPY src ./src

# Compile and package application using BuildKit cache mount to reuse maven downloads
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

# Run Stage
FROM amazoncorretto:21-alpine
WORKDIR /app

# Create a non-root system group and user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create logs directory and assign permissions to non-root user
# This restricts write access only to /app/logs, preventing runtime tampering with app.jar
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

# Copy packaged jar from build stage (default owner is root, readable/executable by spring)
COPY --from=build /app/target/*.jar app.jar

# Tell Docker to run subsequent commands as the non-root user
USER spring:spring

# Expose HTTP port
EXPOSE 8080

# Healthcheck to monitor API availability
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/employees/all > /dev/null || exit 1

# Run Java with container memory awareness, garbage collection, and failure handling configurations
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:InitialRAMPercentage=50.0", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+ExitOnOutOfMemoryError", \
            "-XX:+UseG1GC", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "app.jar"]
