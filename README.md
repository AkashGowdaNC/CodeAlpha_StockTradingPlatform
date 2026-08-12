<<<<<<< HEAD
# Stock Trading Platform

**CodeAlpha Java Programming Internship — Task 2**

A console-based stock market simulator in plain Java. Start with virtual capital,
watch prices move day by day, buy and sell shares, and track how your portfolio
performs over time.

---

## Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | Live market data | 8 listed stocks with price, daily change and change % |
| 2 | Buy shares | Affordability is calculated and enforced before the order |
| 3 | Sell shares | Books realised profit/loss against your average buy price |
| 4 | Portfolio view | Quantity, average cost, market price, invested, value, P&L |
| 5 | Trade history | Every executed order with day, price and realised P&L |
| 6 | Performance report | Realised vs unrealised P&L and overall return % |
| 7 | Advance trading day | Simulates the next session and reports the effect on net worth |
| 8 | Save / auto-resume | Cash, holdings, trades and prices persist in `data/portfolio.txt` |

---

## How the Price Simulation Works

Each stock has a **volatility** value. When the trading day advances, every price
moves by a random percentage inside that band, plus a very small upward drift:

```java
double drift = 0.0004;
double shock = (random.nextDouble() * 2 - 1) * volatility;
newPrice = currentPrice * (1 + drift + shock);
```

This is a simplified **random walk**, the standard textbook model of share price
movement. Zomato (`ZOMT`, volatility 0.055) therefore swings far more violently
than HDFC Bank (`HDFC`, volatility 0.018) — exactly as you would expect from a
high-growth consumer stock versus a large stable bank.

---

## How Profit Is Calculated

Profit is measured against the **weighted average buy price**, not the last price paid:

> Buy 10 shares at ₹100, then 10 more at ₹200 → average cost = ₹150.

- **Realised P&L** — profit locked in by actually selling: `(sell price − average cost) × quantity`
- **Unrealised P&L** — paper profit on shares still held: `(market price − average cost) × quantity`
- **Overall return** — `current net worth − starting capital`

These always reconcile: *realised + unrealised = overall return.*

---

## Project Structure

```
CodeAlpha_StockTradingPlatform/
├── src/com/codealpha/trading/
│   ├── Stock.java            # One company: price, previous close, volatility, daily move
│   ├── Market.java           # The exchange: all listings + advancing the trading day
│   ├── Holding.java          # Shares owned of one stock + weighted average cost
│   ├── Transaction.java      # Immutable record of one completed trade
│   ├── Portfolio.java        # Cash, holdings, trade history, all money rules
│   ├── User.java             # A trader and their portfolio
│   ├── TradeException.java   # Custom exception for rejected trades
│   ├── FileStorage.java      # Saves/loads the full session
│   ├── ConsoleView.java      # All formatted console output
│   ├── InputHelper.java      # Validated keyboard input
│   └── Main.java             # Menu loop / entry point
├── run.sh / run.bat
└── README.md
```

---

## How to Run

**Requirement:** JDK 8 or newer.

```bash
# Mac / Linux
bash run.sh

# Windows
run.bat

# Or manually
javac -d out $(find src -name "*.java")
java -cp out com.codealpha.trading.Main
```

Starting capital is **₹100,000**. Change `STARTING_CAPITAL` in `Main.java` to adjust it.

---

## Sample Output

```
==============================================================================
  LIVE MARKET DATA
==============================================================================
  SYMBOL  COMPANY                      SECTOR     PRICE       CHANGE     CHANGE %
------------------------------------------------------------------------------
  TCS     Tata Consultancy Services    IT         3889.02     +39.02     +1.01% (UP)
  ZOMT    Zomato Limited               Consumer   216.41      +18.41     +9.30% (UP)

  MY PORTFOLIO
  SYMBOL  QTY    AVG COST    MARKET      INVESTED      VALUE        P&L
  TCS     15     3799.77     3889.02     56996.50      58335.30     +1338.80 (+2.35%)
  ZOMT    100    201.21      216.41      20120.50      21641.00     +1520.50 (+7.56%)

  PERFORMANCE REPORT
  Realised profit/loss   : +422.10   (from shares already sold)
  Unrealised profit/loss : +1751.14  (on shares still held)
  OVERALL RETURN         : +2173.24  (+2.17%)
```

---

## Concepts Demonstrated

- Full OOP modelling: `Stock`, `User`, `Portfolio`, `Holding`, `Transaction`
- **Encapsulation** — `Portfolio` is the only class allowed to change cash or holdings
- **Immutability** — `Transaction` has final fields and no setters, like a real order book
- **Custom exceptions** — `TradeException` keeps validation out of the UI code
- `enum` for transaction type
- `LinkedHashMap` for insertion-ordered listings, `ArrayList` for history
- File I/O with a sectioned text format (`#ACCOUNT`, `#HOLDING`, `#TRADE`, `#PRICE`)
- `Random` for the price simulation

---

*Submitted for the CodeAlpha Java Programming Internship.*
=======
# CodeAlpha_StockTradingPlatform
>>>>>>> origin/main
