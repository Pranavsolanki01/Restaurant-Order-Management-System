# NEW Restaurant Order Management System #

## Introduction ##
 A Simple Restaurant Order Management System is a basic application designed to help manage restaurant orders efficiently. 
 It allows users to add, view, and delete orders,
 providing a straightforward interface for restaurant staff.
 
## Features ##
 - Implemented User Dashboard for user's oders history.
 - Implemented seperarate Kitchen Section for order status.
 - Used single Auth. Module for login and register, Enhancing better user experience.
 - Used minimal UI for better user experience.
 - Centralized logging using ELK stack for better debugging and monitoring.
 - Implemented caching using Redis to improve performance.
 - Centralized Data for better management.

## Technologies Used ##
 - Java
 - Spring Boot
 - Kakfa
 - Redis
 - MySQL
 - MongoDB
 - Thymeleaf/Jsp
 - Docker
 - Kubernetes
 - Git/Github
 - Maven
 - Ubuntu

## Installation ##
1. Clone the repository:
   ```bash
   git clone 
   ```
2. Navigate to the project directory:
   ```bash
   cd restaurant-order-management-system
   ```
3. Build the project using Maven:
   ```bash
    mvn clean install
    ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access the application in your web browser at `http://localhost:9099`.

6. Use Docker-Compose file to build and run the containers:
   ```bash
   docker-compose up --build
   ```