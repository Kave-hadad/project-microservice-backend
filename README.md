# 📝 project-microservice-backend

## 📌 Overview
The Auth Service acts as a Gateway.  
It handles authentication tasks such as:
- login  
- register user and admin  
- delete user and admin by id
- get user by ID  
- get all users  
- get user by family  
- patch user and admin 
- refresh token  

It stores user credentials (email & password).  
Profile information is managed separately in the User Service.  
Auth Service validates JWT tokens and, when needed, forwards requests to the User Service.


This project is a backend microservices-based user management system built with Spring Boot.
It leverages Spring Security, REST APIs, JWT for authentication, Feign for inter-service communication, JPA for persistence, Junit & Mockito for testing, and Docker for containerization.
  
It consists of two main services:

- **Auth Service** → Handles user authentication, saves email & password in its own database, and generates JWT tokens.  
- **User Service** → Manages user profiles (CRUD operations) and stores them in a separate database.  
- All requests first go through **Auth Service** (acting as a gateway), then are forwarded to **User Service** if authentication is valid.

This architecture demonstrates **REST APIs**, **JWT-based security**, and **service-to-service communication** using **FeignClient**.

---

## 🚀 Technologies Used
- **Java 21**  
- **Spring Boot** (Web, Data JPA, Security)  
- **JWT** for authentication and authorization  
- **FeignClient** for inter-service communication  
- **PostgreSQL** (separate databases for Auth and User services)  
- **Swagger/OpenAPI** for API documentation  
- **Docker & Docker Compose** for containerization  
- **JUnit & Mockito** for Unit Testing  
- **Logback/SLF4J** for logging  
  
## 📂 Project Structure
```bash   
├── auth-service
│   ├── controller
│   ├── service
│   ├── repository
│   ├── dto
│   ├── Mapper
│   ├── security (JWT)
│   └── feign (user communication)
│
├── user-service
│   ├── controller
│   ├── service
│   ├── repository
│   ├── Mapper
│   └── dto
│
└── docker-compose.yml
   
---

## 📖 API Documentation
Swagger UI is available at: 
- **Gateway (Auth Service)** → `http://localhost:8081/swagger-ui/index.html#`
 

---

## 🐳 Running with Docker Compose
1. Make sure you have **Docker** and **Docker Compose** installed.  
2. Clone the repository:
   ```bash
  git clone https://github.com/Kave-hadad/project-microservice-backend.git 
  cd project-microservice-backend docker-compose up --build
Build and run all services:

bash
docker-compose up
Services will be available at:
gateway:
Auth Service → (http://localhost:8081/swagger-ui/index.html#)

