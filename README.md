# Smart Travel: Your trip, just a click away

[![Angular](https://img.shields.io/badge/Angular-17.3.0-DD0031?style=flat&logo=angular&logoColor=white)](https://angular.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.8.3-3178C6?style=flat&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PrimeNG](https://img.shields.io/badge/PrimeNG-17.18.0-03C4E8?style=flat)](https://primeng.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?style=flat&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.9.3-4695EB?style=flat&logo=quarkus&logoColor=white)](https://quarkus.io/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GraphQL](https://img.shields.io/badge/GraphQL-16.8.1-E10098?style=flat&logo=graphql&logoColor=white)](https://graphql.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0.8-47A248?style=flat&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13.1-FF6600?style=flat&logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-26.0.0-2496ED?style=flat&logo=docker&logoColor=white)](https://www.docker.com/)
[![Jenkins](https://img.shields.io/badge/Jenkins-2.452-D24939?style=flat&logo=jenkins&logoColor=white)](https://www.jenkins.io/)
[![OKD](https://img.shields.io/badge/OKD-4.15-EE0000?style=flat&logo=redhatopenshift&logoColor=white)](https://www.okd.io/)


<img src="docs/screenshots/homepage.png" alt="homepage" style="zoom:50%;" />
<img src="docs/screenshots/flights.png" alt="flights" style="zoom:50%;" />
<img src="docs/screenshots/checkout.png" alt="checkout" style="zoom:50%;" />
<img src="docs/screenshots/packages.png" alt="packages" style="zoom:50%;" />
<img src="docs/screenshots/packages_grid.png" alt="packages_grid" style="zoom:50%;" />
<img src="docs/screenshots/orders_list.png" alt="orders_list" style="zoom:50%;" />

## Introduction

**SmartTravel** is a modern, microservices-based e-commerce application designed for searching and purchasing travel packages, including flights, hotels, and organized tours. The platform is built with a focus on high performance, scalability, and asynchronous communication.

The application handles the entire customer journey, from browsing a public catalog to managing a shopping cart, processing secure payments, and receiving automated notifications. It features a multi-role system supporting **Customers**, **Travel Agents** (for catalog management), and **Administrators** (for user management).

## Architecture

The system follows a cloud-native **Microservices Architecture** to ensure modularity and independent scalability:

* **BFF (Backend-for-Frontend):** A dedicated layer using **GraphQL** that acts as the single entry point for the frontend, optimizing data fetching and reducing over-fetching.
* **Event-Driven Communication:** Asynchronous messaging via **RabbitMQ** ensures decoupled services. For example, order creation triggers notification events without blocking the main process.
* **Transactional Outbox Pattern:** Uses **Debezium** for Change Data Capture (CDC) to ensure reliable event publishing from the database to the message broker.

<img src="docs/diagrams/architecture_diagram.png" alt="architecture_diagram" />


## Technology Stack

### **Backend**

* **Frameworks:** Java with **Spring Boot** (for User and Order services) and **Quarkus** (for Travel Catalog and Notification services).
* **APIs:** REST (microservices) and **GraphQL** (exposed by BFF to frontend).

### **Frontend**

* **Framework:** **Angular**.
* **UI Components:** **PrimeNG** for a responsive and professional user interface.
* **State Management:** **RxJS** for reactive data handling.

### **Data & Messaging**

* **Database:** **MongoDB** (NoSQL) for flexible storage of multilingual travel descriptions and variable destination details.
* **Message Broker:** **RabbitMQ**.

### **DevOps & Infrastructure**

* **CI/CD:** Automated pipelines built with **Jenkins**.
* **Containerization:** **Docker** for service isolation.
* **Orchestration:** **OKD (OpenShift Kubernetes Distribution)** for managing deployment, scaling, and networking.

## Microservices Breakdown

1. **User Service (Spring Boot):** Handles registration, login, and profile management.
2. **Travel Catalog Service (Quarkus):** Manages package information, pricing, and availability.
3. **Order & Payment Service (Spring Boot):** Manages the checkout flow and integrates with external providers like **PayPal**.
4. **Notification Service (Quarkus):** Sends automated email confirmations via **Mailpit** upon successful orders.

## Database Structure

<img src="docs/diagrams/db.png" alt="db_diagram" />

## Payment Flow

### Order creation

<img src="docs/diagrams/order_creation_sequence_diagram.png" alt="order_creation_sequence_diagram" />

### Order capture

<img src="docs/diagrams/order_capture_sequence_diagram.png" alt="order_capture_sequence_diagram" />

### Email notification

<img src="docs/diagrams/order_notification_sequence_diagram.png" alt="order_notification_sequence_diagram" />

## Future Roadmap

* **Reviews System:** Allowing customers to rate and review their trips.
* **Map Integration:** Visualizing accommodations and activities via geolocation services.
* **Push Notifications:** Real-time updates for mobile and web users.
* **Multi-currency Support:** Full internationalization for global users.

## Credentials

### User credentials
~~~
CUSTOMER
email                               password
caleb.johnson@gmail.com             12345
charlotte.williams@gmail.com        12345

AGENT
email                               password
maria.lopez@smart-travel.com        12345
michael.anderson@smart-travel.com   12345

ADMIN
email                               password
alexander.smith@smart-travel.com    12345
amelia.thompson@smart-travel.com    12345
~~~

### PayPal

~~~
email:      g15-personal@wa2.polito.it
password:   bellapass
~~~

