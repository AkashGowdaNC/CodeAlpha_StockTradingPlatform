# Explanation Script — Stock Trading Platform

Everything you need to record the LinkedIn video and answer questions confidently.

---

## PART 1 — Video Script (~2 minutes)

> **[Screen: README open]**
>
> "Hi, I'm Noor. This is Task 2 of my CodeAlpha Java internship — a stock trading
> platform simulator, built in pure Java with no external libraries.
>
> **[Run it, enter name]**
>
> You start with one lakh of virtual capital. This is the market — eight listed
> companies, each with a live price and a daily change percentage.
>
> **[Menu 1 — market]**
>
> The prices aren't hardcoded. Each stock has a volatility value, and every trading
> day the price moves by a random percentage inside that band, plus a small upward
> drift. That's a random walk — the standard textbook model for share prices. So
> Zomato, which I set at 5.5% volatility, swings much harder than HDFC Bank at 1.8%.
> The simulation behaves differently for different stocks, which is the point.
>
> **[Menu 2 — buy TCS]**
>
> Let me buy. It tells me the price and how many shares I can actually afford, and
> it will not let me exceed that — the Portfolio class throws a TradeException if
> the cash isn't there.
>
> **[Menu 7 — advance day, twice]**
>
> Now I advance the trading day. Every price moves, and it immediately reports how
> my net worth changed.
>
> **[Menu 4 — portfolio]**
>
> Here's the portfolio: quantity, my average cost, the current market price, and my
> profit and loss on each position.
>
> The average cost is the bit worth explaining. If I buy ten shares at 100 and ten
> more at 200, my average is 150 — not 200. Profit is always measured against that
> weighted average, which is how real broking apps calculate it.
>
> **[Menu 3 — sell some TCS]**
>
> When I sell, that profit becomes *realised* — actually locked in.
>
> **[Menu 6 — performance]**
>
> And the performance report separates the two: realised profit from shares I've
> sold, unrealised profit on shares I still hold. Those two always add up exactly to
> the overall return — that's my check that the accounting is correct.
>
> **[Exit and restart]**
>
> Everything saves — cash, holdings, trade history and prices — so the session
> resumes exactly where it left off.
>
> Code's on GitHub, link below. Thanks for watching."

---

## PART 2 — Demo Order (follow this exactly)

1. Enter trader name
2. `1` → view market
3. `2` → buy `TCS`, quantity `10`
4. `2` → buy `ZOMT`, quantity `50`
5. `7` → advance day (do this **twice** so prices visibly move)
6. `4` → portfolio — point out avg cost vs market price
7. `3` → sell `TCS`, quantity `5` — point out realised profit message
8. `6` → performance report — point out realised + unrealised = overall
9. `5` → trade history
10. `0` → exit, then **restart** to show the session resume

---

## PART 3 — What Each Class Does (one line each)

| Class | Responsibility |
|-------|----------------|
| `Stock` | One company — price, previous close, volatility, one day's price move |
| `Market` | The exchange — all listings, advancing the trading day |
| `Holding` | Shares owned of one stock + the weighted average buy price |
| `Transaction` | Immutable record of one completed trade |
| `Portfolio` | Cash, holdings, history — **all** the money rules live here |
| `User` | A trader and their portfolio |
| `TradeException` | Custom exception thrown when a trade is rejected |
| `FileStorage` | Saves/loads the entire session |
| `ConsoleView` | Every line of formatted output |
| `InputHelper` | Validated keyboard input |
| `Main` | Menu loop — reads a choice and delegates |

**The one-sentence version:** *"Market handles prices, Portfolio handles money, and
nothing else is allowed to touch either."*

---

## PART 4 — Likely Questions and Answers

**Q: How does the price simulation actually work?**
A: A random walk. Each stock has a volatility, say 2%. Every day I generate a random
number between −2% and +2%, add a tiny upward drift, and multiply the current price by
that. `Math.max(1.0, ...)` stops a price ever hitting zero. It's simplified, but it's
the same shape as real price movement — small moves usually, occasional big ones.

**Q: Why is Transaction immutable?**
A: All fields are `final` and there are no setters. A trade that already happened must
never change — that's how real order books work. If a trade could be edited afterwards,
the history couldn't be trusted for the P&L calculation.

**Q: Explain the average buy price.**
A: It's weighted by quantity. In `addShares` I take the existing cost — quantity times
current average — add the new purchase cost, then divide by the new total quantity.
Buy 10 at 100 and 10 at 200 and you get 150. Selling doesn't change it, only quantity,
because selling doesn't change what you originally paid for the remaining shares.

**Q: What's the difference between realised and unrealised profit?**
A: Realised is locked in — you sold, the money is in your cash balance. Unrealised is
on paper — the price went up but you still hold the shares, so it can still vanish
tomorrow. I track them separately because they're genuinely different, and the fact
that they sum to the overall return is how I verify the accounting is right.

**Q: Why a custom TradeException instead of returning false?**
A: A boolean tells you it failed but not *why*. The exception carries a specific message
— "insufficient funds, need X but only Y available" — and it means `Portfolio` never has
to print anything. It just refuses the trade and explains; the UI layer decides how to
show it. That keeps logic and presentation separate.

**Q: Why LinkedHashMap for the market?**
A: So the listings always print in the same order. With a plain HashMap the market table
would reshuffle every time you viewed it, which looks broken even though the data is fine.

**Q: What's the drift value for?**
A: 0.0004 is a slight upward bias. Without it the market is pure coin-flip noise and
long-term returns hover around zero, which makes the simulation feel pointless. Real
markets trend up slightly over time, so the drift makes it more realistic and gives the
user something to actually play against.

**Q: How does the save file work?**
A: A sectioned text format. Each line starts with a tag — `#ACCOUNT`, `#HOLDING`,
`#TRADE` or `#PRICE` — so the loader always knows what kind of line it's reading. I save
the prices too, otherwise you'd resume with your old holdings but a reset market, and
your P&L would jump for no reason.

**Q: Why double for money instead of BigDecimal?**
A: Good catch — for real financial software you'd use `BigDecimal`, because `double` has
tiny rounding errors that compound. For a simulator with display rounded to two decimals
it's not visible, but I'd switch to BigDecimal if this handled real money.

**Q: How would you extend this?**
A: Limit orders instead of only market orders, a price history chart in the console,
brokerage fees on each trade, and multiple user accounts — `User` is already a separate
class specifically so that last one wouldn't require touching `Portfolio`.

---

## PART 5 — If a Demo Goes Wrong

- **"You cannot afford even one share"** → sell something first, or pick a cheaper stock like `ZOMT`.
- **Prices don't move** → you have to press `7` to advance the day; nothing moves on its own.
- **Portfolio shows old data on startup** → that's the save file loading, which is intentional. Delete the `data` folder for a clean demo.
- **Symbol not found** → use the exact symbol from the market table (`TCS`, `INFY`, `RELI`, `HDFC`, `TATA`, `WIPR`, `SUNP`, `ZOMT`). Case doesn't matter.
