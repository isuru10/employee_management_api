# Build Stage
FROM amazoncorretto:21-alpine AS build
WORKDIR /app

# Copy Maven wrapper configuration and dependencies descriptor
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Resolve dependencies to cache them in the Docker layer
RUN ./mvnw dependency:go-offline -B

# Copy application source code
COPY src ./src

# Compile and package application
RUN ./mvnw clean package -DskipTests

# Run Stage
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy packaged jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Execute Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
