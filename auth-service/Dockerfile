# Dockerfile for Spring Boot auth-service
# This Dockerfile creates a production-ready container for the auth-service.
# Build the JAR file locally first with: mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

# Set metadata labels
LABEL maintainer="Restaurant Management System" \
      description="Auth Service for Restaurant Order Management System" \
      version="1.0"

# Create a non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Switch to non-root user
USER appuser

# Copy the built JAR file from local target directory with proper ownership
COPY --chown=appuser:appgroup target/auth-service-*.jar app.jar

# Expose the port the app runs on
EXPOSE 8081

# Configure JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application with optimized JVM settings
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]