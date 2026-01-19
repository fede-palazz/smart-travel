# SmartTravel: E-Commerce Travel Platform

[![React](https://img.shields.io/badge/React-19.1.2-61DAFB?style=flat&logo=react&logoColor=white)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.8.3-3178C6?style=flat&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Leaflet](https://img.shields.io/badge/Leaflet-1.9.4-199900?style=flat&logo=leaflet&logoColor=white)](https://leafletjs.com/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-4.1.7-06B6D4?style=flat&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?style=flat&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.4-4169E1?style=flat&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Kafka](https://img.shields.io/badge/Kafka-3.3.2-231F20?style=flat&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![Keycloak](https://img.shields.io/badge/Keycloak-26.2.4-2C54A3?style=flat&logo=keycloak&logoColor=white)](https://www.keycloak.org/)

<img src="screenshots/landing.png" alt="screen_landing" style="zoom:50%;" />

## Introduction

**SmartTravel** is a modern, microservices-based e-commerce application designed for searching and purchasing travel packages, including flights, hotels, and organized tours. The platform is built with a focus on high performance, scalability, and asynchronous communication.

The application handles the entire customer journey, from browsing a public catalog to managing a shopping cart, processing secure payments, and receiving automated notifications. It features a multi-role system supporting **Customers**, **Travel Agents** (for catalog management), and **Administrators** (for user management).

## Architecture

The system follows a cloud-native **Microservices Architecture** to ensure modularity and independent scalability:

* **BFF (Backend-for-Frontend):** A dedicated layer using **GraphQL** that acts as the single entry point for the frontend, optimizing data fetching and reducing over-fetching.
* **Event-Driven Communication:** Asynchronous messaging via **RabbitMQ** ensures decoupled services. For example, order creation triggers notification events without blocking the main process.
* **Transactional Outbox Pattern:** Uses **Debezium** for Change Data Capture (CDC) to ensure reliable event publishing from the database to the message broker.

<img src="docs/screenshots/architecture_diagram.png" alt="architecture_diagram" />


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

