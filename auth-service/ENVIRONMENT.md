# Environment Configuration Guide

## Overview

This authentication service uses environment variables loaded from a `.env` file for configuration management. This approach provides better security, flexibility, and deployment consistency.

## Setup Instructions

### 1. Copy Environment Template

```bash
cp .env.example .env
```

### 2. Configure Your Environment

Edit the `.env` file with your specific configuration:

```env
# ==================== SERVER CONFIGURATION ====================
SERVER_PORT=8081

# ==================== DATABASE CONFIGURATION ====================
MYSQL_HOST=localhost
MYSQL_PORT=3307
MYSQL_DATABASE=auth_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# ==================== REDIS CONFIGURATION ====================
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DATABASE=0

# ==================== JWT CONFIGURATION ====================
JWT_SECRET=your_very_long_and_secure_secret_key_here
JWT_EXPIRATION=86400000
JWT_ISSUER=restaurant-auth-service

# ==================== RATE LIMITING ====================
RATE_LIMIT_DEFAULT=100
RATE_LIMIT_LOGIN=5
RATE_LIMIT_REGISTER=3
```

## Configuration Categories

### 🌐 Server Configuration

- `SERVER_PORT`: Port on which the service runs (default: 8081)
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod, test)

### 🗄️ Database Configuration

- `MYSQL_HOST`: MySQL server hostname
- `MYSQL_PORT`: MySQL server port
- `MYSQL_DATABASE`: Database name
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

### 📊 Redis Configuration

- `REDIS_HOST`: Redis server hostname
- `REDIS_PORT`: Redis server port
- `REDIS_DATABASE`: Redis database number
- `REDIS_TIMEOUT`: Connection timeout in milliseconds
- `REDIS_POOL_*`: Connection pool settings

### 🔐 JWT Configuration

- `JWT_SECRET`: Secret key for JWT signing (minimum 32 characters)
- `JWT_EXPIRATION`: Token expiration time in milliseconds
- `JWT_ISSUER`: JWT token issuer identifier

### ⚡ Rate Limiting Configuration

- `RATE_LIMIT_DEFAULT`: Default requests per minute limit
- `RATE_LIMIT_LOGIN`: Login attempts per minute limit
- `RATE_LIMIT_REGISTER`: Registration attempts per minute limit
- `RATE_LIMIT_WINDOW`: Time window in minutes

### 📋 Logging Configuration

- `LOG_LEVEL`: Root logging level (DEBUG, INFO, WARN, ERROR)
- `SECURITY_LOG_LEVEL`: Spring Security logging level
- `APP_LOG_LEVEL`: Application-specific logging level
- `JPA_SHOW_SQL`: Show SQL queries in logs (true/false)

## Security Best Practices

### 🔒 Production Security

1. **Strong JWT Secret**: Use a cryptographically secure secret (64+ characters)
2. **Environment Isolation**: Use different `.env` files for different environments
3. **Secret Management**: Consider using external secret management services
4. **File Permissions**: Ensure `.env` file has restricted permissions (600)

### 🚫 What NOT to commit

- `.env` file (contains sensitive data)
- Production secrets
- Database passwords
- API keys

### ✅ What TO commit

- `.env.example` file (template without secrets)
- Configuration documentation
- Default development values

## Environment Validation

The application automatically validates configuration on startup:

```
🔧 Loading Environment Configuration from .env file:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🌐 Server Configuration:
   ├─ Port: 8081
   └─ Active Profile: dev
🗄️  Database Configuration:
   ├─ Host: localhost
   ├─ Port: 3307
   ├─ Database: auth_db
   └─ Username: root
🔐 JWT Configuration:
   ├─ Secret Length: 64 characters
   ├─ Expiration: 24 hours
   └─ Issuer: restaurant-auth-service
✅ All environment configurations are valid!
```

## Troubleshooting

### Common Issues

1. **Missing .env file**

   ```
   Error: Could not find .env file
   Solution: Copy .env.example to .env and configure values
   ```

2. **Invalid JWT secret**

   ```
   Error: JWT_SECRET is too short
   Solution: Use a secret with at least 32 characters
   ```

3. **Database connection failed**

   ```
   Error: Connection refused
   Solution: Check MYSQL_HOST, MYSQL_PORT, and credentials
   ```

4. **Redis connection failed**
   ```
   Error: Could not connect to Redis
   Solution: Verify REDIS_HOST and REDIS_PORT settings
   ```

### Environment Variable Priority

1. System environment variables (highest priority)
2. `.env` file variables
3. application.yml defaults (lowest priority)

## Development vs Production

### Development (.env)

```env
LOG_LEVEL=DEBUG
JPA_SHOW_SQL=true
SPRING_PROFILES_ACTIVE=dev
JWT_EXPIRATION=3600000  # 1 hour for testing
```

### Production (.env)

```env
LOG_LEVEL=INFO
JPA_SHOW_SQL=false
SPRING_PROFILES_ACTIVE=prod
JWT_EXPIRATION=86400000  # 24 hours
```

## Docker Integration

When using Docker, you can:

1. **Mount .env file**:

   ```bash
   docker run -v $(pwd)/.env:/app/.env auth-service
   ```

2. **Use environment variables**:

   ```bash
   docker run -e JWT_SECRET=your_secret auth-service
   ```

3. **Docker Compose**:
   ```yaml
   services:
     auth-service:
       image: auth-service
       env_file:
         - .env
   ```

## Monitoring

The application exposes health and configuration information through actuator endpoints:

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

These endpoints respect the `MANAGEMENT_*` environment variables for security configuration.
