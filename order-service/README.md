# Order Service

Order Service is a Spring Boot microservice that handles order management in the Restaurant Order Management System. It provides secure APIs for placing, managing, and tracking food orders.

## Features

- 🔐 **JWT Authentication**: Secure endpoints using JWT tokens from auth-service
- 📦 **Order Management**: Place, view, update, and cancel orders
- 🔄 **Status Tracking**: Real-time order status updates (PENDING, CONFIRMED, PREPARING, etc.)
- 📨 **Event Publishing**: Kafka integration for order events
- 👤 **Role-based Access**: User and Admin role separation
- 📊 **Order Analytics**: Date range queries and order statistics
- 📝 **Comprehensive Logging**: Detailed logging for debugging and monitoring
- 🚀 **API Documentation**: OpenAPI/Swagger integration

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security** (OAuth2 Resource Server)
- **Spring Data JPA**
- **MySQL** (Production)
- **H2** (Testing)
- **Apache Kafka (KRaft)**
- **Maven**
- **Docker**
- **Kubernetes**

## Service Architecture & Data Flow

### 🏗️ System Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Auth Service  │    │  Order Service  │    │ Kitchen Service │
│    (Port 8081)  │    │   (Port 8083)   │    │   (Port 8084)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │ JWT Validation        │ Order Events          │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Apache Kafka    │
                    │   (KRaft)       │
                    │  (Port 9092)    │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │     MySQL       │
                    │   (Port 3306)   │
                    └─────────────────┘
```

### 🔄 Order Service Data Flow Diagram (DFD)

#### Level 0 - Context Diagram

```
                              ┌───────────────────────────────┐
                              │                               │
    ┌──────────┐              │         Order Service         │             ┌──────────┐
    │   User   │────────────▶│                               │────────────▶│  Admin   │
    │(Customer)│              │  • Place Orders               │             │(Manager) │
    └──────────┘              │  • View Orders                │             └──────────┘
         │                    │  • Cancel Orders              │                   │
         │                    │  • Track Status               │                   │
         ▼                    │                               │                   ▼
    ┌──────────┐              │                               │             ┌──────────┐
    │Auth Token│───────────▶ │                               │◀────────────│ Status   │
    │          │              │                               │             │ Updates  │
    └──────────┘             └────────────────────────────────┘             └──────────┘
                                           │
                                           ▼
                              ┌────────────────────────────────┐
                              │         External Systems       │
                              │   • Database (MySQL)           │
                              │   • Message Queue (Kafka KRaft)│
                              │   • Auth Service (JWT)         │
                              └────────────────────────────────┘
```

#### Level 1 - Order Service Internal Flow

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                              Order Service                                     │
│                                                                                │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   JWT       │    │  Order      │    │   Order     │    │   Event     │      │
│  │ Validation  │──▶│ Controller  │───▶│  Service    │───▶│ Publisher  │      │
│  │   Layer     │    │   (REST)    │    │ (Business)  │    │  (Kafka)    │      │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘      │
│         │                   │                   │                   │          │
│         ▼                   ▼                   ▼                   ▼          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   Auth      │    │ Request/    │    │   Order     │    │ Kafka Topic │      │
│  │  Service    │    │ Response    │    │ Repository  │    │order-events │      │
│  │    API      │    │    DTO      │    │   (JPA)     │    │             │      │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘      │
│                                                 │                              │
│                                                 ▼                              │
│                                        ┌─────────────┐                         │
│                                        │   MySQL     │                         │
│                                        │  Database   │                         │
│                                        └─────────────┘                         │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 📊 Order Processing Flow

#### 1. Order Creation Flow

```
User Request ──▶ JWT Validation ──▶ Controller ──▶ Service ──▶ Database
     │                                                │               │
     │                                                ▼               │
     │                                        Event Publisher         │
     │                                                │               │
     └────────── Response ◀─── DTO Mapping ◀─── Order Saved ◀───────┘
                                     │
                                     ▼
                              Kafka Topic
                             (order-events)
```

#### 2. Order Status Update Flow

```
Admin Request ──▶ JWT + Role Check ──▶ Controller ──▶ Service ──▶ Database
      │                                                   │             │
      │                                                   ▼             │
      │                                           Event Publisher       │
      │                                                   │             │
      └────────── Response ◀─── DTO Mapping ◀─── Status Updated ◀──────┘
                                        │
                                        ▼
                                 Kafka Topic
                                (order-events)
                                        │
                                        ▼
                              Kitchen/Delivery Services
```

#### 3. Payment Integration Flow

```
Order Created ──▶ Payment Status: PENDING ──▶ Payment Processing
     │                                                   │
     │                                                   ▼
     │                                        Payment Completed
     │                                                   │
     │                                                   ▼
     └◀──── Order Status: CONFIRMED ◀──── Auto Status Update
                     │
                     ▼
              Event Published
                     │
                     ▼
              Kitchen Notification
```

### 🔄 Service Interaction Patterns

#### Synchronous Communication

```
Client ──HTTP Request──▶ Order Service ──HTTP Request──▶ Auth Service
   ▲                           │                              │
   │                           ▼                              │
   └──HTTP Response──◀── JWT Validation ◀──HTTP Response─────┘
```

#### Asynchronous Communication

```
Order Service ──Kafka Event──▶ order-events Topic (KRaft)
                                      │
                                      ├──▶ Kitchen Service
                                      ├──▶ Delivery Service
                                      ├──▶ Notification Service
                                      └──▶ Analytics Service
```

### 🗄️ Database Schema Flow

```
┌─────────────┐         ┌─────────────┐
│    Users    │         │   Orders    │
│ (Auth DB)   │────────▶│            │
└─────────────┘         │ - id        │
                        │ - user_id   │
                        │ - status    │
                        │ - payment   │
                        └─────────────┘
                               │
                               │ 1:N
                               ▼
                        ┌─────────────┐
                        │ Order Items │
                        │             │
                        │ - id        │
                        │ - order_id  │
                        │ - menu_item │
                        │ - quantity  │
                        └─────────────┘
```

### 🔐 Security Flow

```
1. User Login ──▶ Auth Service ──▶ JWT Token Generated
                        │
                        ▼
2. API Request ──▶ Order Service ──▶ JWT Validation
                        │                    │
                        ▼                    ▼
3. Role Check ──▶ Authorization ──▶ Business Logic
                        │
                        ▼
4. Response ──▶ Client ◀── Data Processing
```

### 🚀 Event-Driven Architecture

```
Order Lifecycle Events:

ORDER_PLACED ──▶ Kitchen Service (Start Preparation)
     │
     ▼
ORDER_CONFIRMED ──▶ Payment Service (Process Payment)
     │
     ▼
ORDER_PREPARING ──▶ Notification Service (Update Customer)
     │
     ▼
ORDER_READY ──▶ Delivery Service (Assign Driver)
     │
     ▼
ORDER_DELIVERED ──▶ Analytics Service (Update Metrics)
     │
     ▼
CLEANUP_TRIGGER ──▶ Kafka Cleanup (Remove Old Events)
```

### 📈 Scalability Architecture

```
                    Kubernetes Cluster
                           │
                    ┌─────────────┐
                    │ Ingress     │
                    │ Controller  │
                    └─────────────┘
                           │
                    ┌─────────────┐
                    │Load Balancer│
                    │  (Service)  │
                    └─────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌─────────┐       ┌─────────┐       ┌─────────┐
   │ Pod 1   │       │ Pod 2   │       │ Pod 3   │
   │Order Svc│       │Order Svc│       │Order Svc│
   └─────────┘       └─────────┘       └─────────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                    ┌─────────────┐
                    │   MySQL     │
                    │ StatefulSet │
                    └─────────────┘
                           │
                    ┌─────────────┐
                    │   Kafka     │
                    │ StatefulSet │
                    │   (KRaft)   │
                    └─────────────┘
```

## Prerequisites

- Java 21+
- Maven 3.5+
- MySQL 8.0+
- Apache Kafka (KRaft)
- Auth Service running (for JWT validation)
- Docker (for containerization)
- Kubernetes (for orchestration)

## Environment Variables

Create a `.env` file in the root directory:

```env
DB_USERNAME=root
DB_PASSWORD=password
DATABASE_URL=jdbc:mysql://localhost:3306/order_service_db
JWT_SECRET=mySecretKey
JWT_EXPIRATION=86400
JWT_ISSUER_URI=http://localhost:8081/api
JWT_JWK_SET_URI=http://localhost:8081/api/.well-known/jwks.json
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SERVER_PORT=8083
```

## Database Schema

### Orders Table

- `id` (Primary Key)
- `user_id` (Foreign Key to User)
- `user_email`
- `total_price`
- `status` (PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
- `delivery_address`
- `phone_number`
- `special_instructions`
- `created_at`
- `updated_at`

### Order Items Table

- `id` (Primary Key)
- `order_id` (Foreign Key to Order)
- `menu_item_id`
- `menu_item_name`
- `quantity`
- `unit_price`
- `price` (total for this item)
- `special_requests`

## API Endpoints

### Authentication Required

All endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

### Order Management

| Method | Endpoint                    | Description                | Role        |
| ------ | --------------------------- | -------------------------- | ----------- |
| POST   | `/api/orders`               | Place a new order          | USER, ADMIN |
| GET    | `/api/orders`               | Get user's orders          | USER, ADMIN |
| GET    | `/api/orders/paginated`     | Get orders with pagination | USER, ADMIN |
| GET    | `/api/orders/{id}`          | Get specific order         | USER, ADMIN |
| DELETE | `/api/orders/{id}`          | Cancel order               | USER, ADMIN |
| GET    | `/api/orders/by-date-range` | Get orders by date range   | USER, ADMIN |
| GET    | `/api/orders/count`         | Get order count by status  | USER, ADMIN |

### Admin Only

| Method | Endpoint                      | Description              | Role  |
| ------ | ----------------------------- | ------------------------ | ----- |
| PUT    | `/api/orders/{id}/status`     | Update order status      | ADMIN |
| GET    | `/api/orders/admin/by-status` | Get all orders by status | ADMIN |

## Request/Response Examples

### Place Order

```json
POST /api/orders
{
  "orderItems": [
    {
      "menuItemId": 1,
      "menuItemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 12.99,
      "specialRequests": "Extra cheese"
    }
  ],
  "deliveryAddress": "123 Main St, City, State 12345",
  "phoneNumber": "555-0123",
  "specialInstructions": "Ring doorbell twice"
}
```

### Order Response

```json
{
  "id": 1,
  "userId": 123,
  "userEmail": "user@example.com",
  "totalPrice": 25.98,
  "status": "PENDING",
  "deliveryAddress": "123 Main St, City, State 12345",
  "phoneNumber": "555-0123",
  "specialInstructions": "Ring doorbell twice",
  "orderItems": [
    {
      "id": 1,
      "menuItemId": 1,
      "menuItemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 12.99,
      "price": 25.98,
      "specialRequests": "Extra cheese"
    }
  ],
  "createdAt": "2025-07-20T10:30:00",
  "updatedAt": "2025-07-20T10:30:00"
}
```

## Running the Application

### Local Development

1. **Start Dependencies**

   ```bash
   # Start MySQL
   docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:8.0

   # Start Kafka (KRaft mode - no Zookeeper needed)
   docker run -d --name kafka \
     -p 9092:9092 \
     -e KAFKA_ENABLE_KRAFT=yes \
     -e KAFKA_CFG_PROCESS_ROLES=broker,controller \
     -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
     -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
     -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
     -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
     -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@127.0.0.1:9093 \
     -e KAFKA_CFG_NODE_ID=1 \
     bitnami/kafka:latest
   ```

2. **Run the Application**

   ```bash
   mvn spring-boot:run
   ```

3. **Access API Documentation**
   - Swagger UI: http://localhost:8083/api/swagger-ui.html
   - OpenAPI JSON: http://localhost:8083/api/v3/api-docs

### Docker

1. **Build Image**

   ```bash
   mvn clean package
   docker build -t order-service .
   ```

2. **Run Container**
   ```bash
   docker run -p 8083:8083 \
     -e DB_USERNAME=root \
     -e DB_PASSWORD=password \
     -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/order_service_db \
     order-service
   ```

### Kubernetes Deployment

#### 1. **Namespace & ConfigMap**

```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: restaurant-system
---
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
  namespace: restaurant-system
data:
  DB_USERNAME: "root"
  KAFKA_BOOTSTRAP_SERVERS: "kafka-service:9092"
  JWT_ISSUER_URI: "http://auth-service:8081/api"
  JWT_JWK_SET_URI: "http://auth-service:8081/api/.well-known/jwks.json"
```

#### 2. **Secret for Sensitive Data**

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secret
  namespace: restaurant-system
type: Opaque
data:
  DB_PASSWORD: cGFzc3dvcmQ= # base64 encoded 'password'
  DATABASE_URL: amRiYzpteXNxbDovL215c3FsLXNlcnZpY2U6MzMwNi9vcmRlcl9zZXJ2aWNlX2Ri # base64 encoded URL
```

#### 3. **Deployment**

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: restaurant-system
  labels:
    app: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: order-service:latest
          ports:
            - containerPort: 8083
          env:
            - name: DB_USERNAME
              valueFrom:
                configMapKeyRef:
                  name: order-service-config
                  key: DB_USERNAME
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: order-service-secret
                  key: DB_PASSWORD
            - name: DATABASE_URL
              valueFrom:
                secretKeyRef:
                  name: order-service-secret
                  key: DATABASE_URL
            - name: KAFKA_BOOTSTRAP_SERVERS
              valueFrom:
                configMapKeyRef:
                  name: order-service-config
                  key: KAFKA_BOOTSTRAP_SERVERS
            - name: JWT_ISSUER_URI
              valueFrom:
                configMapKeyRef:
                  name: order-service-config
                  key: JWT_ISSUER_URI
            - name: JWT_JWK_SET_URI
              valueFrom:
                configMapKeyRef:
                  name: order-service-config
                  key: JWT_JWK_SET_URI
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /api/actuator/health
              port: 8083
            initialDelaySeconds: 60
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /api/actuator/health/readiness
              port: 8083
            initialDelaySeconds: 30
            periodSeconds: 10
```

#### 4. **Service**

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: restaurant-system
  labels:
    app: order-service
spec:
  selector:
    app: order-service
  ports:
    - protocol: TCP
      port: 8083
      targetPort: 8083
  type: ClusterIP
```

#### 5. **Horizontal Pod Autoscaler**

```yaml
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service-hpa
  namespace: restaurant-system
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
```

#### 6. **Ingress for External Access**

```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: order-service-ingress
  namespace: restaurant-system
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
spec:
  rules:
    - host: order-api.restaurant.local
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 8083
```

#### 7. **Deploy to Kubernetes**

```bash
# Apply all configurations
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f hpa.yaml
kubectl apply -f ingress.yaml

# Check deployment status
kubectl get pods -n restaurant-system
kubectl get services -n restaurant-system
kubectl get hpa -n restaurant-system

# View logs
kubectl logs -f deployment/order-service -n restaurant-system

# Port forward for local testing
kubectl port-forward service/order-service 8083:8083 -n restaurant-system
```

#### 8. **Complete Infrastructure Setup**

```bash
# Deploy MySQL
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
  namespace: restaurant-system
spec:
  serviceName: mysql-service
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: "password"
        - name: MYSQL_DATABASE
          value: "order_service_db"
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
  volumeClaimTemplates:
  - metadata:
      name: mysql-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi
---
apiVersion: v1
kind: Service
metadata:
  name: mysql-service
  namespace: restaurant-system
spec:
  selector:
    app: mysql
  ports:
  - port: 3306
    targetPort: 3306
EOF

# Deploy Kafka (KRaft)
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: kafka
  namespace: restaurant-system
spec:
  serviceName: kafka-service
  replicas: 1
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: bitnami/kafka:latest
        env:
        - name: KAFKA_ENABLE_KRAFT
          value: "yes"
        - name: KAFKA_CFG_PROCESS_ROLES
          value: "broker,controller"
        - name: KAFKA_CFG_CONTROLLER_LISTENER_NAMES
          value: "CONTROLLER"
        - name: KAFKA_CFG_LISTENERS
          value: "PLAINTEXT://:9092,CONTROLLER://:9093"
        - name: KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP
          value: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT"
        - name: KAFKA_CFG_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka-service:9092"
        - name: KAFKA_CFG_CONTROLLER_QUORUM_VOTERS
          value: "1@127.0.0.1:9093"
        - name: KAFKA_CFG_NODE_ID
          value: "1"
        ports:
        - containerPort: 9092
        - containerPort: 9093
        volumeMounts:
        - name: kafka-storage
          mountPath: /bitnami/kafka
  volumeClaimTemplates:
  - metadata:
      name: kafka-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 5Gi
---
apiVersion: v1
kind: Service
metadata:
  name: kafka-service
  namespace: restaurant-system
spec:
  selector:
    app: kafka
  ports:
  - port: 9092
    targetPort: 9092
EOF
```

#### 9. **Monitoring & Observability**

```yaml
# service-monitor.yaml (for Prometheus)
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: order-service-monitor
  namespace: restaurant-system
spec:
  selector:
    matchLabels:
      app: order-service
  endpoints:
    - port: http
      path: /api/actuator/prometheus
      interval: 30s
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Kafka Events

The service publishes the following events to the `order-events` topic:

### Order Placed Event

```json
{
  "orderId": 1,
  "userId": 123,
  "userEmail": "user@example.com",
  "eventType": "ORDER_PLACED",
  "totalPrice": 25.98,
  "status": "PENDING",
  "orderItems": [...],
  "timestamp": "2025-07-20T10:30:00"
}
```

### Order Status Changed Event

```json
{
  "orderId": 1,
  "userId": 123,
  "userEmail": "user@example.com",
  "eventType": "ORDER_STATUS_CHANGED",
  "totalPrice": 25.98,
  "status": "CONFIRMED",
  "orderItems": [...],
  "timestamp": "2025-07-20T10:35:00"
}
```

## Order Status Flow

1. **PENDING** - Order placed, awaiting confirmation
2. **CONFIRMED** - Order confirmed by restaurant
3. **PREPARING** - Food is being prepared
4. **READY** - Order ready for pickup/delivery
5. **OUT_FOR_DELIVERY** - Order is being delivered
6. **DELIVERED** - Order successfully delivered
7. **CANCELLED** - Order cancelled by user or restaurant

## Security

- JWT token validation with auth-service
- Role-based access control (USER, ADMIN)
- CORS configuration for cross-origin requests
- Input validation and sanitization
- Comprehensive error handling

## Monitoring

- Actuator endpoints for health checks
- Prometheus metrics integration
- Structured logging with correlation IDs
- Custom metrics for order processing

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
