# PayPort 💸
> A production-ready UPI-based payment system built with Spring Boot, MySQL, and vanilla JS.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Author](#author)

---

## Overview

PayPort is a simplified UPI-based digital payment system that simulates core features of real-world payment apps like GPay and PhonePe. It supports user registration, UPI account linking, peer-to-peer money transfers secured with a UPI PIN, balance inquiry, and transaction history — all protected by JWT-based authentication.

---

## Features

- **User Registration & Login** — secure authentication with BCrypt-hashed passwords and JWT tokens
- **UPI PIN** — every transfer and balance check requires a 4-6 digit PIN (BCrypt hashed in DB)
- **Peer-to-Peer Transfer** — send money between UPI IDs with full transaction logging
- **Balance Inquiry** — PIN-protected balance check
- **Transaction History** — auto-loaded history with sent/received labels
- **Profile Page** — view name, mobile number, and UPI ID
- **Protected Routes** — all pages require a valid JWT token; unauthenticated users are redirected to login
- **Global Exception Handling** — clean JSON error responses, no stack traces exposed
- **Input Validation** — server-side validation on all request bodies
- **Dev/Prod Config Separation** — Spring profiles for local and AWS environments

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.5.3 |
| Security | Spring Security, JWT (JJWT 0.12.6), BCrypt |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA, Hibernate 6 |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Build | Maven |
| Testing | JUnit 5, Mockito |

---

## Project Structure

```
payment/
├── src/
│   ├── main/
│   │   ├── java/com/payport/payment/
│   │   │   ├── controller/         # REST endpoints
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AccountController.java
│   │   │   │   └── TransactionController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/        # Incoming request DTOs
│   │   │   │   └── response/       # Outgoing response DTOs
│   │   │   ├── exception/          # Global exception handler
│   │   │   ├── model/              # JPA entities (User, Account, Transaction)
│   │   │   ├── repository/         # Spring Data JPA repositories
│   │   │   ├── security/           # JWT filter, SecurityConfig, UserDetailsService
│   │   │   └── service/            # Business logic (AccountService, UserService)
│   │   └── resources/
│   │       ├── static/             # Frontend (HTML, CSS, JS)
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                       # Unit tests (JUnit 5 + Mockito)
└── pom.xml
```

---

## Getting Started

### Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8.0

### Local Setup

1. Clone the repository

```bash
git clone https://github.com/your-username/payport.git
cd payport/payment
```

2. Create the database

```sql
CREATE DATABASE payport_db;
```

3. Configure `application-dev.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payport_db
spring.datasource.username=root
spring.datasource.password=your_password
JWT_SECRET=your-secret-key-minimum-32-characters-long
```

4. Run the application

```bash
mvn spring-boot:run
```

5. Open in browser

```
http://localhost:8080/register.html
```

### User Flow

```
Register → Set UPI PIN → Login → Home
                                  ├── Transfer (receiver UPI + amount + PIN)
                                  ├── Balance  (PIN only)
                                  ├── History  (auto-loaded)
                                  └── Profile  (name, mobile, UPI ID)
```

---

## API Endpoints

### Auth — public, no token required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register user and link UPI account |
| POST | `/api/auth/login` | Login and receive JWT token |

### Account — public, no token required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/account/set-pin` | Set UPI PIN for an account |

### Transaction — protected, JWT token required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transaction/transfer` | Transfer money between UPI IDs |
| POST | `/api/transaction/balance` | Check balance (PIN required) |
| GET | `/api/transaction/history` | Get transaction history |

### Request / Response Examples

**Register**
```json
POST /api/auth/register
{
    "name": "Manoj Kumar",
    "mobileNumber": "9876543210",
    "password": "manoj@123",
    "upiId": "manoj@payport",
    "balance": 5000
}

Response 200:
{
    "token": "eyJhbGci...",
    "mobileNumber": "9876543210",
    "name": "Manoj Kumar",
    "upiId": "manoj@payport"
}
```

**Transfer**
```json
POST /api/transaction/transfer
Authorization: Bearer <token>
{
    "fromUpi": "manoj@payport",
    "toUpi": "test@payport",
    "amount": 500,
    "pin": "1234"
}

Response 200:
{
    "message": "Transfer successful"
}
```

**Error Response Format**
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Insufficient balance",
    "timestamp": "2026-04-21T12:00:00"
}
```

---

## Security

- Passwords hashed with **BCrypt** (never stored plain)
- UPI PIN hashed with **BCrypt** (never stored plain)
- **JWT tokens** expire after 24 hours
- All transaction endpoints require a valid Bearer token
- Every transfer and balance check requires UPI PIN verification
- CORS configured per environment
- No stack traces exposed to clients — global exception handler returns clean JSON
- Dev and production configs fully separated via Spring profiles

---

## Testing

Unit tests written with **JUnit 5** and **Mockito** — no database required, runs in under 5 seconds.

```bash
mvn test
```

**Test coverage:**

| Service | Scenarios tested |
|---|---|
| `AccountService.transfer()` | Success, insufficient balance, same UPI, zero amount, wrong PIN, sender not found |
| `AccountService.getBalance()` | Success, wrong PIN, UPI not found |
| `AccountService.setPin()` | Success, PIN already set |
| `UserService.registerUser()` | Success, duplicate mobile number |

Total: **13 test cases**

---

## Roadmap

- [x] Phase 1 — JWT auth, BCrypt, input validation, global exception handler
- [x] Phase 2 — DTO response layer, entity cleanup, Lombok refactor
- [x] Phase 3 — Frontend overhaul, UPI PIN flow, profile page
- [x] Unit tests — JUnit 5 + Mockito
- [ ] Phase 4 — Docker, GitHub Actions CI/CD, AWS EC2 + RDS deployment

---

## Author

**Y Manoj Kumar**
Integrated M.Tech CSE | VIT University

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue)](https://linkedin.com/in/manojkumaryennameedhi)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black)](https://github.com/YennameedhiManojKumar)