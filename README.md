# Spring Boot Microservices eCommerce Backend 🛒🚀

A scalable, cloud-native eCommerce backend built with Java and Spring Boot microservices. This project features modular services for products, orders, payments, customers, notifications, and more—leveraging service discovery, centralized configuration, and robust messaging for modern distributed architectures.

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%3E=14-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-%3E=6.0-green)
![Kafka](https://img.shields.io/badge/Kafka-Confluent-orange)
![JUnit](https://img.shields.io/badge/JUnit-5-red)
![Eureka](https://img.shields.io/badge/Eureka-Service%20Discovery-yellow)
![Config Server](https://img.shields.io/badge/Spring%20Cloud-Config%20Server-blueviolet)

---

## Features

- **Product Service:** Manage product catalog with PostgreSQL and Flyway migrations.
- **Order Service:** Place and track orders, integrates with product, payment, and customer services.
- **Payment Service:** Handles payment processing and status updates.
- **Customer Service:** Manages customer data using MongoDB.
- **Notification Service:** Sends email notifications (MailDev for dev), listens to Kafka events.
- **Service Discovery:** Eureka server for dynamic service registration and lookup.
- **API Gateway:** Centralized routing and load balancing for all APIs.
- **Centralized Config:** Spring Cloud Config Server for unified configuration management.
- **Event-Driven:** Kafka for asynchronous communication between services.
- **Database Admin UIs:** pgAdmin and Mongo Express for DB management.
- **Dockerized:** All services and dependencies orchestrated via Docker Compose.

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.5+** (Web, Data JPA, Data MongoDB, Validation, Mail, Thymeleaf)
- **Spring Cloud** (Eureka, Config Server, OpenFeign, Gateway)
- **PostgreSQL** & **MongoDB**
- **Kafka** (Confluent Platform)
- **Docker & Docker Compose**
- **JUnit 5** (Testing)
- **Lombok** (Boilerplate reduction)
- **Flyway** (DB migrations)
- **pgAdmin**, **Mongo Express**, **MailDev**

---

## Architecture Overview

This project follows a microservices architecture:

- **Service Discovery:** All services register with Eureka for dynamic discovery.
- **API Gateway:** Routes external requests to internal services, supports load balancing and path-based routing.
- **Config Server:** Centralizes configuration for all services, loaded at startup.
- **Inter-Service Communication:** REST (via OpenFeign) and asynchronous messaging (Kafka).
- **Persistence:** PostgreSQL for transactional data (orders, products, payments), MongoDB for customer and notification data.
- **Notifications:** Kafka events trigger email notifications via the notification service.

**Service Communication Diagram:**

```
[Client] → [API Gateway] → [Product/Order/Payment/Customer Services]
                             ↑           ↓
                       [Eureka Discovery]
                             ↑           ↓
                    [Config Server]   [Kafka]
                             ↑           ↓
                  [PostgreSQL/MongoDB] [Notification Service]
```

---

## Project Structure

```
springbootEcommerceapp/
│
├── docker-compose.yml
└── services/
    ├── config.server/      # Spring Cloud Config Server
    ├── customer/           # Customer microservice (MongoDB)
    ├── discovery/          # Eureka Service Discovery
    ├── gateway/            # API Gateway
    ├── notification/       # Notification microservice (Kafka, Mail)
    ├── order/              # Order microservice (PostgreSQL, Kafka)
    ├── payment/            # Payment microservice (PostgreSQL, Kafka)
    └── product/            # Product microservice (PostgreSQL)
```

---

## Installation & Setup

1. **Clone the repository:**

   ```sh
   git clone https://github.com/yourusername/springbootEcommerceapp.git
   cd springbootEcommerceapp
   ```

2. **Configure Environment:**

   - All service configs are managed via [`services/config.server/src/main/resources/configurations`](services/config.server/src/main/resources/configurations).
   - For local overrides, edit [`services/config.server/src/main/resources/application.yml`](services/config.server/src/main/resources/application.yml) in each service or use Docker Compose environment variables.

3. **Start Infrastructure with Docker Compose:**

   ```sh
   docker-compose up -d
   ```

   This will start:

   - PostgreSQL, MongoDB, Kafka, Zookeeper
   - pgAdmin, Mongo Express, MailDev
   - (You can now run the Spring Boot services)

4. **Build and Run Microservices:**

   In each service directory (e.g., [`services/product`](services/product)):

   ```sh
   ./mvnw clean package
   java -jar target/*.jar
   ```

   Or use your IDE to run each service.

---

## Usage

- **API Gateway:**  
  Access all APIs via the gateway at [http://localhost:8222](http://localhost:8222).

  - Example: `GET /api/v1/products` → routed to Product Service

- **Service Endpoints:**  
  | Service | Base URL (via Gateway) |
  |-----------------|-------------------------------|
  | Product | `/api/v1/products` |
  | Order | `/api/v1/orders` |
  | Payment | `/api/v1/payments` |
  | Customer | `/api/v1/customers` |
  | Notification | Internal (Kafka, Email) |

- **Swagger UI:**  
  Add Swagger/OpenAPI to each service for interactive API docs (recommended).

- **Database Admin:**

  - **pgAdmin:** [http://localhost:5050](http://localhost:5050)
  - **Mongo Express:** [http://localhost:8081](http://localhost:8081)

- **MailDev (Email Testing):**  
  [http://localhost:1080](http://localhost:1080)

---

## Testing

- **Unit & Integration Tests:**  
  Each service includes tests using **JUnit 5** and **Spring Boot Test**.
  Run tests with:

  ```sh
  ./mvnw test
  ```

- **Test Coverage:**  
  Add plugins like JaCoCo for coverage reports.

---

## Results / API Examples

**Example: Create Product (POST `/api/v1/products`)**

```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 29.99,
  "quantity": 100
}
```

**Example: Order Response**

```json
{
  "orderId": 123,
  "customerId": 1,
  "orderLines": [{ "productId": 10, "quantity": 2 }],
  "status": "CONFIRMED"
}
```

---

## Future Improvements

- Add JWT-based authentication and role-based authorization
- Implement distributed tracing (Zipkin, Sleuth)
- Add Swagger/OpenAPI documentation to all services
- CI/CD pipeline for automated builds and deployments
- Horizontal scaling with Kubernetes
- Advanced monitoring (Prometheus, Grafana)
- Payment gateway integration (Stripe, PayPal)

---

## Contributing

Contributions, bug reports, and feature requests are welcome!  
Please open an issue or submit a pull request.

---

## License

This project is licensed under the MIT License.

---
