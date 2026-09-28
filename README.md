# SafePay – Distributed Digital Payments Microservices Platform

[![AWS Live Demo](https://img.shields.io/badge/AWS-Live_Demo_Available-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)](http://54.81.55.46)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

SafePay is an enterprise-grade, event-driven digital payments backend architected with **Java 21** and **Spring Boot 3**. It decouples authentication, payments, wallet balances, transaction holds, notifications, and cashback rewards into independently deployable microservices. **Apache Kafka** powers asynchronous event propagation, and **MongoDB** guarantees distributed document persistence.

---

## 🌐 Live Cloud Deployment

| Component | URL | Purpose |
| :--- | :--- | :--- |
| **Live Web Portal & API Console** | **[http://54.81.55.46](http://54.81.55.46)** | Interactive dashboard to test authentication, wallet credits, and transactions |
| **API Gateway (Edge Router)** | **[http://54.81.55.46:8080](http://54.81.55.46:8080)** | Central routing entry point with JWT Bearer validation |
| **AWS Public DNS** | **[http://ec2-54-81-55-46.compute-1.amazonaws.com](http://ec2-54-81-55-46.compute-1.amazonaws.com)** | Hosted on AWS EC2 (`us-east-1`) |

---

## 🏛️ Microservices Architecture

```
Client / Frontend (Port 80)
        │
        ▼
   API Gateway (Port 8080) ──[JWT Auth Filter]
        │
        ├──> User Service (Port 8081) ───────────> MongoDB (Users & Auth)
        │
        ├──> Wallet Service (Port 8083) ─────────> MongoDB (Balances & Holds)
        │
        ├──> Transaction Service (Port 8082) ────> MongoDB (Transactions)
        │            │
        │            └──[Publish Event]───► Kafka Topic: txn-initiated
        │                                          │
        │                                          ├──► Reward Service (Port 8089) ───────> MongoDB (Rewards)
        │                                          │
        │                                          └──► Notification Service (Port 8084) ──> MongoDB (Alerts)
```

### Services Overview

| Service | Port | Responsibilities | Database / Broker |
| :--- | :---: | :--- | :--- |
| **API Gateway** | `8080` | Spring Cloud Gateway edge router, JWT filter, CORS handling | None (Stateless) |
| **User Service** | `8081` | User registration, password encryption (BCrypt), JWT issuance | MongoDB (`safePayUser`) |
| **Wallet Service** | `8083` | Digital wallets, balances, idempotent credits/debits, payment holds | MongoDB (`walletService`) |
| **Transaction Service** | `8082` | Transaction creation, status tracking, Kafka event publisher | MongoDB & Kafka Producer |
| **Reward Service** | `8089` | Consumes `txn-initiated`, calculates & stores cashback points | Kafka Consumer & MongoDB |
| **Notification Service** | `8084` | Consumes `txn-initiated`, logs transaction alert notifications | Kafka Consumer & MongoDB |
| **Apache Kafka** | `9092` | Real-time distributed event streaming broker | ZooKeeper (`2181`) |
| **MongoDB** | `27017` | Document persistence across all microservices | Persistent volume |

---

## ⚡ Key Engineering Highlights

* **Event-Driven Architecture (EDA):** The transaction pipeline is asynchronous. Once a payment is saved, an event is dispatched to the `txn-initiated` Kafka topic without blocking the caller, allowing downstream notification and reward workers to consume events independently.
* **Idempotency & Double-Spending Prevention:** Wallet credit and debit operations utilize unique `referenceId` keys to prevent double-charging or duplicate credits during network retries.
* **Declarative Edge Security:** The API Gateway acts as a single gatekeeper, inspecting incoming requests with a custom `JwtAuthFilter` and forwarding authenticated user claims downstream.
* **Containerized Orchestration:** Multi-stage Docker builds optimize memory footprint (`-Xms128m -Xmx350m`) to ensure all 6 Spring Boot JVMs, Kafka, ZooKeeper, and MongoDB run concurrently with minimal resource overhead.

---

## 🚀 Quick Start (Local Setup)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Docker 24+ & Docker Compose v2)
- [Git](https://git-scm.com/)

### 1-Click Launch with Docker Compose
Clone the repository and launch the full multi-service stack:

```bash
# Clone the repository
git clone https://github.com/gitsofpranav/SafePay.git
cd SafePay

# Build and start all 6 services + Kafka + MongoDB + Nginx
docker compose up --build -d
```

Check the status of all running containers:
```bash
docker compose ps
```

Access the services:
* **Web Portal & API Explorer:** `http://localhost`
* **API Gateway:** `http://localhost:8080`
* **Direct Microservices:** `8081` (User), `8082` (Txn), `8083` (Wallet), `8084` (Notify), `8089` (Reward)

To stop the platform:
```bash
docker compose down
```

---

## 📡 API Reference & Test Guide

### 1. User Registration & Authentication
#### Sign Up
```bash
curl -X POST http://54.81.55.46:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pranav Dewangan",
    "email": "pranav@safepay.dev",
    "password": "SecurePassword123!"
  }'
```

#### Log In (Retrieve JWT Bearer Token)
```bash
curl -X POST http://54.81.55.46:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pranav@safepay.dev",
    "password": "SecurePassword123!"
  }'
```
*Response:*
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 2. Digital Wallet Operations
*(Include `Authorization: Bearer <TOKEN>` in request headers)*

#### Create Wallet
```bash
curl -X POST http://54.81.55.46:8080/api/v1/wallets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"userId": "USER-101"}'
```

#### Add Funds (Idempotent Credit)
```bash
curl -X POST http://54.81.55.46:8080/api/v1/wallets/credit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "userId": "USER-101",
    "amount": 1000.0,
    "referenceId": "REF-TXN-001"
  }'
```

#### Query Balance
```bash
curl -X GET http://54.81.55.46:8080/api/v1/wallets/USER-101 \
  -H "Authorization: Bearer <TOKEN>"
```

---

### 3. Payments & Kafka Event Pipeline
*(Triggers Kafka event consumed by Notification and Reward services)*

#### Dispatch Payment
```bash
curl -X POST http://54.81.55.46:8080/api/transactions/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "senderName": "Pranav Dewangan",
    "receiverName": "Asha Kumar",
    "amount": 250.0
  }'
```

#### View All Transactions
```bash
curl -X GET http://54.81.55.46:8080/api/transactions/all \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📂 Repository Structure

```text
├── api_gateway/             # Spring Cloud Gateway edge router & JWT filter
├── user_service/            # Authentication, registration & BCrypt hashing
├── transaction_service/     # Payment orchestration & Kafka producer
├── wallet_service/          # Digital wallet balance management & holds
├── notification_service/    # Kafka event listener & notification storage
├── reward_service/          # Kafka event listener & cashback calculator
├── portal/                  # Interactive HTML5 developer dashboard
├── docker-compose.yml       # Production multi-service container orchestration
├── nginx.conf               # Edge reverse proxy configuration
└── pom.xml                  # Parent Maven POM for multi-module build
```

---

## 📄 License
This project is open-source and available under the MIT License.
