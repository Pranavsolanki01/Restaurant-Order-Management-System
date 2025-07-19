# Menu Service

A comprehensive microservice for managing restaurant menu items, built with Spring Boot, MongoDB, and Redis caching.

## Features

- **Complete CRUD operations** for menu items
- **Advanced search and filtering** capabilities
- **Redis caching** for improved performance
- **MongoDB storage** for scalable data management
- **Comprehensive validation** and error handling
- **RESTful API** with proper HTTP status codes
- **Docker support** for easy deployment
- **Sample data loader** for testing

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Data MongoDB**
- **Spring Data Redis** (Caching)
- **Spring Boot Validation**
- **Spring Boot Actuator** (Health checks)
- **Lombok** (Boilerplate reduction)
- **Maven** (Build tool)
- **Docker** (Containerization)

## API Endpoints

### Menu Item CRUD Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/menu` | Get all menu items |
| GET | `/api/menu/{id}` | Get menu item by ID |
| POST | `/api/menu` | Create new menu item |
| PUT | `/api/menu/{id}` | Update menu item |
| DELETE | `/api/menu/{id}` | Delete menu item |
| PATCH | `/api/menu/{id}/availability` | Update item availability |

### Search and Filter Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/menu/available` | Get all available items |
| GET | `/api/menu/unavailable` | Get all unavailable items |
| GET | `/api/menu/category/{category}` | Get items by category |
| GET | `/api/menu/category/{category}/available` | Get available items by category |
| GET | `/api/menu/search?query={term}` | Search items by name/description |
| GET | `/api/menu/price-range?minPrice={min}&maxPrice={max}` | Get items by price range |
| GET | `/api/menu/vegetarian` | Get vegetarian items |
| GET | `/api/menu/vegan` | Get vegan items |
| GET | `/api/menu/gluten-free` | Get gluten-free items |
| GET | `/api/menu/quick-prep?maxMinutes={minutes}` | Get quick preparation items |
| GET | `/api/menu/with-ingredient?ingredient={name}` | Get items with specific ingredient |
| GET | `/api/menu/without-allergen?allergen={name}` | Get items without specific allergen |

### Statistics and Utility Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/menu/categories` | Get all categories |
| GET | `/api/menu/stats/category-count?category={name}` | Get item count by category |
| GET | `/api/menu/stats/available-count` | Get available item count |
| GET | `/api/menu/health` | Health check endpoint |

## Data Model

### MenuItem Entity

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": "decimal",
  "category": "string",
  "isAvailable": "boolean",
  "imageUrl": "string",
  "ingredients": ["string"],
  "allergens": ["string"],
  "preparationTimeMinutes": "integer",
  "calories": "integer",
  "isVegetarian": "boolean",
  "isVegan": "boolean",
  "isGlutenFree": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "createdBy": "string",
  "updatedBy": "string"
}
```

### Available Categories

- APPETIZER
- MAIN_COURSE
- DESSERT
- BEVERAGE
- SALAD
- SOUP
- SIDE_DISH
- BREAKFAST
- LUNCH
- DINNER

## Configuration

### Application Properties

```properties
# Server Configuration
spring.application.name=menu-service
server.port=8082

# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=menu_db

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=1
```

### Environment Variables

You can override configuration using environment variables:

- `MONGO_HOST` - MongoDB host
- `MONGO_PORT` - MongoDB port
- `MONGO_DATABASE` - MongoDB database name
- `REDIS_HOST` - Redis host
- `REDIS_PORT` - Redis port

## Running the Application

### Prerequisites

- Java 21
- MongoDB (running on localhost:27017)
- Redis (running on localhost:6379)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd menu-service
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Or build and run JAR**
   ```bash
   ./mvnw clean package
   java -jar target/menu-service-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t menu-service .
   ```

2. **Run with Docker Compose** (from project root)
   ```bash
   cd ../docker
   docker-compose up -d mongo redis
   docker run -p 8082:8082 --network rms-net menu-service
   ```

## API Usage Examples

### Create Menu Item

```bash
curl -X POST http://localhost:8082/api/menu \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Grilled Chicken",
    "description": "Tender grilled chicken breast with herbs",
    "price": 22.99,
    "category": "MAIN_COURSE",
    "isAvailable": true,
    "ingredients": ["Chicken breast", "Herbs", "Olive oil"],
    "allergens": [],
    "preparationTimeMinutes": 25,
    "calories": 350,
    "isVegetarian": false,
    "isVegan": false,
    "isGlutenFree": true
  }'
```

### Get All Menu Items

```bash
curl http://localhost:8082/api/menu
```

### Search Menu Items

```bash
curl "http://localhost:8082/api/menu/search?query=chicken"
```

### Get Vegetarian Items

```bash
curl http://localhost:8082/api/menu/vegetarian
```

### Get Items by Price Range

```bash
curl "http://localhost:8082/api/menu/price-range?minPrice=10&maxPrice=25"
```

## Sample Data

The service automatically loads sample data on startup if the database is empty. This includes:

- **Appetizers**: Caesar Salad, Buffalo Wings
- **Main Courses**: Grilled Salmon, Margherita Pizza, Beef Steak
- **Desserts**: Chocolate Lava Cake, Fresh Fruit Salad
- **Beverages**: Fresh Orange Juice, Coffee

## Caching Strategy

The service uses Redis caching with the following cache keys:

- `menuItems` - Individual menu items and all items
- `availableMenuItems` - Available items by various criteria
- `menuCategories` - Items grouped by category
- `vegetarianMenuItems` - Vegetarian items
- `veganMenuItems` - Vegan items
- `glutenFreeMenuItems` - Gluten-free items
- `allCategories` - List of all categories

Cache TTL is set to 30 minutes by default.

## Health Check

Check service health:
```bash
curl http://localhost:8082/api/menu/health
```

## Error Handling

The service provides comprehensive error handling with consistent error responses:

```json
{
  "error": "ERROR_CODE",
  "message": "Error description",
  "status": 400,
  "path": "/api/menu",
  "timestamp": "2024-01-01T10:00:00"
}
```

## Performance Features

- **Redis caching** for frequently accessed data
- **MongoDB indexing** on commonly queried fields
- **Lazy loading** and pagination support
- **Connection pooling** for database connections

## Monitoring

- **Spring Boot Actuator** endpoints available at `/actuator`
- **Health checks** at `/actuator/health`
- **Metrics** at `/actuator/metrics`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.