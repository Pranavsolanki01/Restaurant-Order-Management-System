# Use Eclipse Temurin JDK 21 on Alpine Linux as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target directory
COPY target/auth-service-1.0-SNAPSHOT.jar app.jar

# Expose the port that the application runs on (default 8081)
EXPOSE 8081

# Create a non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Change ownership of the working directory to the app user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]