# Stock Portfolio Monitoring App (Spring Boot Backend)

A backend system to manage and monitor users' stock portfolios, receive real-time updates, calculate gains/losses, and trigger alerts based on custom conditions.

---

## Objective

- Manage user stock holdings
- Set price and portfolio loss alerts
- Fetch real-time stock prices via external APIs
- Calculate individual and total portfolio gains/losses

---

## Core Modules

### 1. User Management
- Register/Login

### 2. Portfolio Module
- Create and manage portfolios
- Add/Edit/Delete holdings (symbol, quantity, buy price)
- View real-time value and gain/loss

### 3. Real-Time Price Fetcher
- Scheduled jobs or REST trigger to fetch current prices
- Cache recent prices

### 4. Alerting Module
- Alerts for:
  - Stock price crosses X
  - Portfolio loss exceeds Y%
- Notification system: Email / DB log (pluggable architecture)

### 5. Gain/Loss Calculator
- Calculate:
  - Per-stock gain/loss (absolute and %)
  - Total portfolio gain/loss

---

## Tech Stack

| Layer        | Technology                               |
|--------------|-------------------------------------------|
| Language     | Java                                      |
| Framework    | Spring Boot, Spring Security, Spring Data JPA |
| Database     | MySQL                                     |
| Scheduler    | Spring Scheduler / Quartz                 |
| REST Client  | RestTemplate                              |
| Build Tool   | Maven                                     |

---

## Business Logic Flow

### Price Fetch Job (Scheduled)
1. Retrieve unique stock symbols from holdings
2. Call external API to fetch latest prices
3. Update stock price cache

### Gain/Loss Calculation
```java
gain = (currentPrice - buyPrice) * quantity;
percentage = (gain / (buyPrice * quantity)) * 100;

### Alerts Evaluation
1. Compares current stock price against user-defined conditions
2. If triggered, make alert as active

## Testing Plan

| Type              | Tool            | Focus Areas                                      |
|-------------------|------------------|--------------------------------------------------|
| **Unit Testing**   | JUnit, Mockito   | Controllers, Services, Utility classes           |
| **Integration Test** | Spring Boot      | REST APIs, Authentication flow, DB interactions |
| **Mock Testing**   | Mockito          | External API mocks (e.g., stock price services)  |
| **Alert Testing**  | Custom           | Price-based alerts, Portfolio loss triggers      |

## Sample API Endpoints

### AuthController
- `POST /user/signup` – User registration  
- `POST /user/login` – Authentication
- `PUT /user/update/{email}` - Update username or password


### PortfolioController
- `GET /api/portfolios` – View all portfolios  
- `POST /api/portfolios` – Create new portfolio  
- `GET /api/portfolios/{id}/holdings` – Get holdings for a portfolio  


### HoldingsController
- `POST /api/holdings` – Add new stock  
- `PUT /api/holdings/{id}` – Update stock info  
- `DELETE /api/holdings/{id}` – Remove a stock  


### AlertsController
- `POST /api/alerts` – Set up an alert  
- `GET /api/alerts` – View all alerts  
- `PUT /api/alerts/{id}` – Update alert   


## Example Entity Overview

### User
- `id`: Long  
- `username`: String  
- `email`: String  
- `password`: String  
- `role`: String  

---

### Portfolio
- `id`: Long  
- `userId`: Long  
- `name`: String  
- `createdDate`: Date  

---

### Holding
- `id`: Long  
- `portfolioId`: Long  
- `symbol`: String  
- `quantity`: Integer  
- `buyPrice`: Double  

---

### Alert
- `id`: Long  
- `userId`: Long  
- `symbol`: String  
- `condition`: String (e.g., `>`, `<`)  
- `threshold`: Double  
- `isActive`: Boolean  

---


