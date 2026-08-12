package com.codealpha.trading;

/**
 * How many shares of one stock the user owns, and what they paid on average.
 *
 * The average buy price is the key idea here: if you buy 10 shares at 100 and
 * then 10 more at 200, your average cost becomes 150. Profit is always measured
 * against this average, not against the last price you paid.
 */
public class Holding {

    private final String symbol;
    private int quantity;
    private double averageBuyPrice;

    public Holding(String symbol, int quantity, double averageBuyPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    /** Weighted-average update when more shares of the same stock are bought. */
    public void addShares(int extraQuantity, double price) {
        double existingCost = quantity * averageBuyPrice;
        double newCost = extraQuantity * price;
        this.quantity += extraQuantity;
        this.averageBuyPrice = (existingCost + newCost) / this.quantity;
    }

    /** Selling does NOT change the average buy price, only the quantity. */
    public void removeShares(int soldQuantity) {
        this.quantity -= soldQuantity;
    }

    public double getInvestedAmount() {
        return quantity * averageBuyPrice;
    }

    public double getCurrentValue(double marketPrice) {
        return quantity * marketPrice;
    }

    public double getUnrealisedProfit(double marketPrice) {
        return getCurrentValue(marketPrice) - getInvestedAmount();
    }

    public double getReturnPercent(double marketPrice) {
        if (getInvestedAmount() == 0) {
            return 0.0;
        }
        return (getUnrealisedProfit(marketPrice) / getInvestedAmount()) * 100.0;
    }

    public String getSymbol()         { return symbol; }
    public int getQuantity()          { return quantity; }
    public double getAverageBuyPrice() { return averageBuyPrice; }
}
