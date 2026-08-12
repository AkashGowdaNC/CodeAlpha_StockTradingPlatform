package com.codealpha.trading;

/**
 * An immutable record of one completed trade.
 *
 * Immutable = every field is final and there are no setters. Once a trade has
 * happened it must never change, exactly like a real order book entry.
 */
public class Transaction {

    public enum Type { BUY, SELL }

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double pricePerShare;
    private final int tradingDay;
    private final double realisedProfit; // only meaningful for SELL trades

    public Transaction(Type type, String symbol, int quantity,
                       double pricePerShare, int tradingDay, double realisedProfit) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.tradingDay = tradingDay;
        this.realisedProfit = realisedProfit;
    }

    public double getTotalValue() {
        return quantity * pricePerShare;
    }

    public Type getType()             { return type; }
    public String getSymbol()         { return symbol; }
    public int getQuantity()          { return quantity; }
    public double getPricePerShare()  { return pricePerShare; }
    public int getTradingDay()        { return tradingDay; }
    public double getRealisedProfit() { return realisedProfit; }

    @Override
    public String toString() {
        return String.format("Day %d | %-4s %-6s x%-4d @ %.2f = %.2f",
                tradingDay, type, symbol, quantity, pricePerShare, getTotalValue());
    }
}
