# Auth Service Docker Guide

This directory contains Docker configuration for containerizing the Spring Boot auth-service.

## Files

- `Dockerfile` - Production-ready Dockerfile (requires pre-built JAR)
- `Dockerfile.multistage` - Multi-stage build Dockerfile (builds from source)
- `.dockerignore` - Docker ignore file to optimize build context

## Prerequisites

1. Java 21
2. Maven 3.9+
3. Docker

## Building the Application

### Method 1: Pre-build JAR (Recommended for development)

```bash
# Build the JAR file locally
mvn clean package -DskipTests

# Build Docker image
docker build -t auth-service:latest .
```

### Method 2: Multi-stage build (CI/CD friendly)

```bash
# Build using multi-stage Dockerfile
docker build -f Dockerfile.multistage -t auth-service:latest .
```

## Running the Container

### Basic run
```bash
docker run -p 8081:8081 auth-service:latest
```

### With environment variables
```bash
docker run -p 8081:8081 \
  -e SERVER_PORT=8081 \
  -e MYSQL_HOST=host.docker.internal \
  -e MYSQL_PORT=3307 \
  -e MYSQL_DATABASE=auth_db \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=host.docker.internal \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=your_jwt_secret_key_here \
  -e JWT_EXPIRATION=3600000 \
  -e JWT_ISSUER=restaurant-auth-service \
  -e SPRING_PROFILES_ACTIVE=dev \
  auth-service:latest
```

### Using environment file
```bash
# Create .env file with required variables
docker run -p 8081:8081 --env-file .env auth-service:latest
```

## Environment Variables

The application requires several environment variables. See `.env.example` for a complete list.

### Required Variables:
- `SERVER_PORT` - Application port (default: 8081)
- `MYSQL_HOST` - MySQL database host
- `MYSQL_PORT` - MySQL database port
- `MYSQL_DATABASE` - MySQL database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `REDIS_HOST` - Redis cache host
- `REDIS_PORT` - Redis cache port
- `JWT_SECRET` - JWT secret key
- `JWT_EXPIRATION` - JWT token expiration time
- `JWT_ISSUER` - JWT issuer

## Docker Compose

You can use the existing docker-compose.yml in the parent directory to run the entire stack:

```bash
cd ../docker
docker-compose up -d
```

## Security Features

The Docker container includes several security best practices:

1. **Non-root user**: Application runs as `appuser` (UID 1001)
2. **Minimal base image**: Uses Alpine Linux for smaller attack surface
3. **Resource limits**: JVM configured with container-aware settings
4. **Proper file ownership**: Files owned by the application user

## Health Checks

The application includes Spring Boot Actuator for health monitoring. Once running, check health at:

```
http://localhost:8081/actuator/health
```

## Troubleshooting

### Port conflicts
If port 8081 is already in use, map to a different port:
```bash
docker run -p 8082:8081 auth-service:latest
```

### Database connectivity
Ensure database is running and accessible. For local development with Docker:
```bash
# Start database first
cd ../docker && docker-compose up -d mysql redis

# Then start auth service
docker run --network docker_rms-net -p 8081:8081 auth-service:latest
```

### Memory issues
Adjust JVM memory settings:
```bash
docker run -p 8081:8081 \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  auth-service:latest
```

## Building for Production

For production deployments:

1. Use the standard Dockerfile (pre-built JAR)
2. Build JAR with production profile
3. Use specific image tags (not `latest`)
4. Set appropriate resource limits

```bash
# Build for production
mvn clean package -DskipTests -Pprod

# Build Docker image with version tag
docker build -t auth-service:1.0.0 .

# Tag for registry
docker tag auth-service:1.0.0 your-registry/auth-service:1.0.0
```