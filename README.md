# 🔐 User Management & 📓 Journal Event Microservices

This project contains two Spring Boot-based microservices:

1. **User Service** – Handles user registration, authentication, and user profile management.
2. **Journal Service** – Listens to user activity via Kafka and logs the events to a database for auditing.

---

## 🎯 Objectives

- Secure REST APIs with JWT and role-based access.
- Kafka-based event publishing and consumption.
- Maintain a log of user activity for auditing and tracing.

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT & Role-based auth)
- Apache Kafka
- PostgreSQL or MySQL
- Docker & Docker Compose

---

## 🏗️ Microservices

### 🧍 User Service

Handles authentication, user management, and event publishing.

#### 📬 Endpoints

| Method | Endpoint            | Role Required | Description                          |
|--------|---------------------|----------------|--------------------------------------|
| POST   | `/register`         | Public         | Register new user                    |
| POST   | `/token`            | Public         | Authenticate user & get JWT         |
| POST   | `/login`            | User/Admin     | Validate existing JWT               |
| GET    | `/user/fetch`       | Admin          | Fetch all registered users          |
| PUT    | `/user/update`      | User/Admin     | Update own profile                  |
| DELETE | `/user/delete/{id}` | Admin          | Delete a user by ID                 |

---

### 📝 Journal Service

Consumes user activity events and stores them in the database.

#### 🔐 Authentication

Journal Service uses **Basic Authentication**.

| Username | Password   | Role   |
|----------|------------|--------|
| `admin`  | `adminpass`| ADMIN  |
| `user`   | `userpass` | USER   |

> Use these credentials in the `Authorization` header as `Basic base64(username:password)`

#### 📬 Endpoints

| Method | Endpoint                     | Role Required | Description                          |
|--------|------------------------------|---------------|--------------------------------------|
| GET    | `/events/all`                | Admin         | View all journaled events            |
| GET    | `/events/user/{username}`    | Admin/User    | View journaled events for a user     |

---

## 🔐 Security

- JWT Token-based authentication in User Service
- Basic Auth in Journal Service
- Role-based authorization using `@PreAuthorize`
- Roles supported: `USER`, `ADMIN`

---

## 🚀 Getting Started

### 🔧 Prerequisites

- Java 17+
- Maven
- Docker + Docker Compose

### 🐳 Start Services

```bash
docker-compose up --build
