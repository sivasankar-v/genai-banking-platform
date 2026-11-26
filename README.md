# GenAI Banking Platform 
An enterprise-style Banking Management System built using **Spring Boot**, **Spring Security**, **JWT Authentication**,
**PostgreSQL**, **Docker**, and **OpenAPI (Swagger)**.

This project demonstrates secure banking operations such as customer management, account management, transactions, beneficiaries, and loans.
It is designed to be extended with Generative AI capabilities for intelligent banking assistance. 

---
# Features

## Authentication
- User Registration
- User Login
- JWT Authentication
- Role-Based Authorization 

## Customer Management
- Create Customer 
- Get Customer
- Get All Customers
- Delete Customer 

## Account Management
- Create Account
- Get Account
- Get All Accounts
- Delete Account

## Transaction Management
- Deposit Money 
- Withdraw Money
- Transfer Money
- Transaction History

## Beneficiary Management
- Add Beneficiary
- Get Beneficiaries
- Delete Beneficiary

## Loan Management
- Apply Loan
- Approve Loan
- Reject Loan
- Get Loan by ID
- Get Loans by Customer 
- Get All Loans 

---

# Tech Stack 
- Java 21
- Spring Boot 3.x
- Spring Security 
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Docker
- Docker Compose
- Lombok
- OpenAPI / Swagger

# Project Structure
```
src
    |__config
    |__controller
    |__dto
       |__request
       |__response
    |__entity
    |__exception
    |__mapper
    |__repository
    |__security
    |__service
       |__impl
    |__util
```

---

# Database 

PostgreSQL

Major Tables

- users
- roles
- accounts
- transactions
- beneficiaries
- loans
# Authentication

JWT-based authentication is used. Register, then call the Login API to get a token,
and send it with every request:

```
Authorization: Bearer <JWT_TOKEN>
```

## Roles and access
- **Customers** (anyone who registers) can only see and act on their own customer profile,
  accounts, transactions, beneficiaries and loans. After registering, create your profile
  with `POST /api/customers`; it is linked to your login. `GET /api/customers/me` returns it.
- **Admins** can act on every record, and are the only ones who can list or search customers,
  deactivate customers, freeze accounts, and approve, reject or list all loans.
- The first admin is created at startup from `ADMIN_EMAIL` and `ADMIN_PASSWORD`.

# API Documentation
Swagger UI

```
http://localhost:8085/swagger-ui/index.html
```

---

# Configuration

Secrets are read from environment variables, never from committed files.

| Variable | Required | Purpose |
|---|---|---|
| `JWT_SECRET` | yes | Base64 signing key, at least 256 bits. Generate with `openssl rand -base64 64` |
| `DB_URL` | no | Defaults to `jdbc:postgresql://localhost:5432/fincore` |
| `DB_USERNAME` / `DB_PASSWORD` | no | Database login (default user `postgres`) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | no | Creates the first admin user if it does not exist |
| `JWT_EXPIRATION_MS` | no | Token lifetime, default 24 hours |

Copy `.env.example` to `.env` and fill it in. `.env` is git-ignored.

# Running Locally

```bash
git clone <repository-url>
cd genai-banking-platform
export JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
export DB_PASSWORD=<your postgres password>
./mvnw spring-boot:run
```

# Running using Docker

```bash
cp .env.example .env   # then set JWT_SECRET and the other values
docker compose up --build
```

The API is then on `http://localhost:8081`. Stop with `docker compose down`.

# Running Tests

Tests use an in-memory H2 database, so no Postgres is needed:

```bash
./mvnw test
```

---

# REST APIs
## Authentication
- POST /api/auth/register
- POST /api/auth/login

## Customers
- POST /api/customers
- GET /api/customers/me
- GET /api/customers/{id}
- PUT /api/customers/{id}
- GET /api/customers (admin)
- GET /api/customers/search?keyword= (admin)
- DELETE /api/customers/{id} (admin; marks the customer inactive)

## Accounts
- POST /api/v1/accounts
- GET /api/v1/accounts/{id}
- GET /api/v1/accounts/account-number/{accountNumber}
- GET /api/v1/accounts/customer/{customerId}
- PATCH /api/v1/accounts/{id}/close
- PATCH /api/v1/accounts/{id}/freeze (admin)

## Transactions
- POST /api/v1/transactions/deposit
- POST /api/v1/transactions/withdraw
- POST /api/v1/transactions/transfer
- GET /api/v1/transactions/account/{accountId}

## Beneficiaries
- POST /api/v1/beneficiaries
- PUT /api/v1/beneficiaries/{id}
- DELETE /api/v1/beneficiaries/{id}
- GET /api/v1/beneficiaries/customer/{customerId}

## Loans
- POST /api/v1/loans/apply (the interest rate is set by loan type)
- GET /api/v1/loans/{id}
- GET /api/v1/loans/customer/{customerId}
- PATCH /api/v1/loans/{id}/approve (admin)
- PATCH /api/v1/loans/{id}/reject (admin)
- GET /api/v1/loans (admin)

---
# Future Enhancements

- AI Banking Assistant 
- Transaction insights
- Spending Analysis 
- Budget Recommendations
- RAG-based Banking FAQ
- Email Notifications
- Redis Caching 
- kubernetes Deployment
- CI/CD Pipeline

---

# Author

**Siva Sankar Vallepu**
Software Engineer
Java | Spring Boot | PostgreSQL | Docker | Generative AI


