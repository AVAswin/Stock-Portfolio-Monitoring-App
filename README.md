# ✅ Stock Portfolio Monitoring App (Spring Boot Backend)

This project is a backend system for managing and monitoring stock investments. It allows users to manage portfolios, track real-time stock prices (from a database), calculate gains/losses, set alerts, and generate reports.

---

## 🎯 Objective

To build a secure and efficient backend platform where users can:

- ✅ Upload and manage stock holdings  
- ✅ Receive alerts for custom price thresholds or portfolio losses  
- ✅ View real-time stock prices (via static API)  
- ✅ Track gains/losses for each holding and overall portfolio  

---

## 📁 Core Modules

### ✅ 1. User Management Module
- User registration  
- User login  

### ✅ 2. Portfolio Module
- Create and manage multiple portfolios  
- Add/edit/delete stock holdings (symbol, quantity, buy price)  

### ✅ 3. Real-Time Price Fetcher
- Integrates with external static stock API  
- Scheduled or manual fetching of latest prices  

### ✅ 4. Alerting Module
Users can set alerts for:
- Stock price crossing a threshold  
- Portfolio loss exceeding a set percentage  
- Modular notification system (database logs)  

### ✅ 5. Gain/Loss Calculator
Calculates:
- Per-stock gain/loss (absolute and percentage)  
- Total portfolio performance  

---

## ⚙️ Tech Stack

| Layer         | Technology                        |
|---------------|-----------------------------------|
| Language       | Java                              |
| Framework      | Spring Boot, Spring Data JPA, Spring Security |
| Database       | MySQL                             |
| Scheduling     | Spring Scheduler / Quartz         |
| REST Client    | RestTemplate                      |
| JSON Mapper    | Jackson                           |
| Build Tool     | Maven                             |
| Testing        | JUnit, Mockito                    |

---

## 🧠 Business Logic Flow

### ✔ Price Fetch Job
Runs on schedule (every 5 minutes)

**Steps:**
- Retrieve all stock symbols in use  
- Call third-party API to fetch prices  
- Save to DB for access  

### ✔ Gain/Loss Calculation
For each holding:

gain = (currentPrice - buyPrice) * quantity  
percentageGain = (gain / (buyPrice * quantity)) * 100

## ✔️ Alert Evaluation

Compares current stock price against user-defined conditions.

- ✅ If triggered: Adds into DB and logs alert to console.

---

## 🧪 Testing Plan

| Type          | Tool           | Focus Areas                                 |
|---------------|----------------|---------------------------------------------|
| Unit Testing  | JUnit, Mockito | Controllers, Services, Utils                |
| Spring Boot   | Built-in       | REST APIs, DB interactions                  |
| Mock Testing  | Mockito        | External API (stock price) mocks            |
| Alert Testing | Custom/Unit    | Alert triggers, logging                     |

---

## 📤 Sample API Endpoints

### 🔐 AuthController
- `POST /user/signup` – User registration  
- `POST /user/login` – Authentication
- `PUT /user/update/{email}` - Update username or password

### 📦 PortfolioController
- `POST /portfolio/{userId}` – Add portfolio for a particular user 
- `GET /portfolio/user/{userId}` – View all portfolios of a particular user

### 📈 HoldingsController
- `POST /holdings` – Add new stock  
- `PUT /holdings/{userId}` – Update stock info  
- `DELETE /holdings//{userId}/{stockSymbol}` – Remove a stock  
- `GET /holdings/{userId}` - View all holdings of a particular user
- `GET /holdings/stocks/all` - View all stocks

### 🔔 AlertsController
- `GET /alerts` - Get alert
- `POST /alerts` - Add alert
- `GET /alerts/{userId}` - Get alerts by userId


---
```
## 🗂 Project Structure
Stock-Portfolio-Monitoring-App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/stockPortfolio/
│   │   │       ├── StockPortfolioApplication.java       # Main Spring Boot Application
│   │   │       ├── AlertManagement/                    # Manages stock alerts
│   │   │       ├── ExceptionManagement/                # Global exception handling
│   │   │       ├── HoldingsManagement/                 # Stock holdings and transactions
│   │   │       ├── PortfolioManagement/                # Portfolio management
│   │   │       └── UserManagement/                     # User operations (auth, registration)
│   └── resources/
│       └── application.properties                      # App configuration
│
└── test/
    └── java/
        └── com/example/stockPortfolio/
            ├── AlertManagement/                   # Alert module tests
            ├── HoldingsManagement/                # Holdings module tests
            ├── PortfolioManagement/               # Portfolio module tests
            └── UserManagement/                    # User module tests

---
```

## ▶️ How to Run the Project

### 🛠 Prerequisites

- Java 17+  
- Maven  
- MySQL  
- Postman or Swagger UI (for testing)

### 🚀 Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/AVAswin/Stock-Portfolio-Monitoring-App.git
   cd Stock-Portfolio-Monitoring-App
2. Create MySQL Database:
    CREATE DATABASE stockdb;
3. Edit application.properties
    # Example DB Config
    spring.datasource.url=jdbc:mysql://localhost:3306/stockdb
    spring.datasource.username=root
    spring.datasource.password=root
    
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    
    springdoc.api-docs.path=/v3/api-docs
    springdoc.swagger-ui.path=/swagger-ui.html
4.  Build and Run the Project
    ./mvnw clean install
    ./mvnw spring-boot:run
5.  Access API Documentation
    Visit: http://localhost:8080/swagger-ui/index.html

| Name                   | Role & Contributions                     |
| ---------------------- | ---------------------------------------- |
| **A.V. Aswin**         | Project Lead, Contributed to all modules |
| **Aman Yadav**         | Contributed to all modules               |
| **A Fazil Mohammad**   | Contributed to all modules               |
| **Harshitha**          | Contributed to all modules               |
| **Siddharta Banerjee** | Contributed to all modules               |









