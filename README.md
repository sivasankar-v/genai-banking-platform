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

JWT-based authentication is used.
Obtain a JWT token using the Login API.

Include the token in every request.

```
Authorization: Bearer <JWT_TOKEN>
```

# API Documentation 
Swagger UI

``` 
http://localhost:8085/swagger-ui/index.html
```

---

# Running Locally
Clone the repository 
```bash
git clone <repository-url>
```
Navigate to project

```bash
cd GenAI-Banking-Platform
```

Run 

```bash
mvn spring-boot:run
```

# Running using Docker

Build and start containers
```bash
docker compose up --build
```

```bash
docker compose down
```

---

# REST APIs
## Authentication 
- POST /api/v1/auth/register
- POST /api/v1/auth/login

## Customers
- POST /api/v1/customers
- GET /api/v1/customers
- GET /api/v1/customers/{id}
- DELETE /api/v1/customers/{id}

## Accounts

- POST /api/v1/accounts
- GET /api/v1/accounts
- GET /api/v1/accounts/{id}
- DELETE /api/v1/accounts/{id}

## Transactions 
- POST /deposit
- POST /withdraw
- POST /transfer
- GET /account/{accountId}

## Beneficiaries

- POST /beneficiaries
- GET /beneficiaries
- DELETE /beneficiaries/{id}

## Loans

- POST /loans/apply
- PATCH /loans/{id}/approve
- PATCH /loans/{id}/reject
- GET /loans
- GET /loans/{id}
- GET /loans/customer/{customerId}

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


