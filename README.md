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
