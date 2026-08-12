package com.codealpha.trading;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The user's account: available cash, the shares they own, and every trade
 * they have ever made.
 *
 * This is the heart of the project. All the money rules live here so that the
 * menu code can stay simple.
 */
public class Portfolio {

    private final double initialCapital;
    private double cashBalance;

    private final Map<String, Holding> holdings = new LinkedHashMap<String, Holding>();
    private final List<Transaction> transactions = new ArrayList<Transaction>();

    private double realisedProfit; // profit actually booked by selling

    public Portfolio(double initialCapital) {
        this.initialCapital = initialCapital;
        this.cashBalance = initialCapital;
    }

    /**
     * Buys shares at the current market price.
     * @throws TradeException if the quantity is invalid or the cash is short.
     */
    public void buy(Stock stock, int quantity, int tradingDay) throws TradeException {
        if (quantity <= 0) {
            throw new TradeException("Quantity must be at least 1.");
        }
        double cost = quantity * stock.getCurrentPrice();
        if (cost > cashBalance) {
            throw new TradeException(String.format(
                    "Insufficient funds. Need %.2f but only %.2f is available.", cost, cashBalance));
        }

        cashBalance -= cost;

        Holding holding = holdings.get(stock.getSymbol());
        if (holding == null) {
            holdings.put(stock.getSymbol(),
                    new Holding(stock.getSymbol(), quantity, stock.getCurrentPrice()));
        } else {
            holding.addShares(quantity, stock.getCurrentPrice());
        }

        transactions.add(new Transaction(Transaction.Type.BUY, stock.getSymbol(),
                quantity, stock.getCurrentPrice(), tradingDay, 0.0));
    }

    /**
     * Sells shares at the current market price and books the profit or loss.
     * @return the profit (positive) or loss (negative) realised on this sale.
     */
    public double sell(Stock stock, int quantity, int tradingDay) throws TradeException {
        if (quantity <= 0) {
            throw new TradeException("Quantity must be at least 1.");
        }

        Holding holding = holdings.get(stock.getSymbol());
        if (holding == null) {
            throw new TradeException("You do not own any shares of " + stock.getSymbol() + ".");
        }
        if (quantity > holding.getQuantity()) {
            throw new TradeException("You only own " + holding.getQuantity()
                    + " share(s) of " + stock.getSymbol() + ".");
        }

        double proceeds = quantity * stock.getCurrentPrice();
        double profit = (stock.getCurrentPrice() - holding.getAverageBuyPrice()) * quantity;

        cashBalance += proceeds;
        realisedProfit += profit;
        holding.removeShares(quantity);

        // A fully sold position is removed so it stops showing in the portfolio.
        if (holding.getQuantity() == 0) {
            holdings.remove(stock.getSymbol());
        }

        transactions.add(new Transaction(Transaction.Type.SELL, stock.getSymbol(),
                quantity, stock.getCurrentPrice(), tradingDay, profit));
        return profit;
    }

    /** Market value of the shares only (cash excluded). */
    public double getHoldingsValue(Market market) {
        double total = 0.0;
        for (Holding holding : holdings.values()) {
            Stock stock = market.getStock(holding.getSymbol());
            if (stock != null) {
                total += holding.getCurrentValue(stock.getCurrentPrice());
            }
        }
        return total;
    }

    /** Net worth = cash + market value of every holding. */
    public double getTotalValue(Market market) {
        return cashBalance + getHoldingsValue(market);
    }

    /** Paper profit on shares still held. */
    public double getUnrealisedProfit(Market market) {
        double total = 0.0;
        for (Holding holding : holdings.values()) {
            Stock stock = market.getStock(holding.getSymbol());
            if (stock != null) {
                total += holding.getUnrealisedProfit(stock.getCurrentPrice());
            }
        }
        return total;
    }

    public double getOverallReturn(Market market) {
        return getTotalValue(market) - initialCapital;
    }

    public double getOverallReturnPercent(Market market) {
        if (initialCapital == 0) {
            return 0.0;
        }
        return (getOverallReturn(market) / initialCapital) * 100.0;
    }

    public double getTotalInvested() {
        double total = 0.0;
        for (Holding holding : holdings.values()) {
            total += holding.getInvestedAmount();
        }
        return total;
    }

    public boolean hasHoldings() {
        return !holdings.isEmpty();
    }

    public List<Holding> getHoldings() {
        return new ArrayList<Holding>(holdings.values());
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<Transaction>(transactions);
    }

    public double getCashBalance()    { return cashBalance; }
    public double getInitialCapital() { return initialCapital; }
    public double getRealisedProfit() { return realisedProfit; }

    // --- used only when restoring a saved portfolio from file ---
    public void restoreCash(double cash)          { this.cashBalance = cash; }
    public void restoreRealised(double profit)    { this.realisedProfit = profit; }
    public void restoreHolding(Holding holding)   { holdings.put(holding.getSymbol(), holding); }
    public void restoreTransaction(Transaction t) { transactions.add(t); }
}
