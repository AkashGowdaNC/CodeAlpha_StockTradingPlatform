package com.codealpha.trading;

import java.util.Random;

/**
 * One tradable company on the market.
 *
 * Along with the price it remembers the PREVIOUS CLOSE, which is what lets us
 * show the daily change and the green/red movement seen on real trading apps.
 */
public class Stock {

    private final String symbol;
    private final String companyName;
    private final String sector;

    private double currentPrice;
    private double previousClose;

    /** How wildly this stock swings. 0.02 = calm, 0.06 = very volatile. */
    private final double volatility;

    public Stock(String symbol, String companyName, String sector,
                 double startingPrice, double volatility) {
        this.symbol = symbol.toUpperCase();
        this.companyName = companyName;
        this.sector = sector;
        this.currentPrice = startingPrice;
        this.previousClose = startingPrice;
        this.volatility = volatility;
    }

    /**
     * Simulates one trading day using a "random walk".
     * The new price moves by a random percentage inside the volatility band,
     * with a very small upward drift so the market is not purely random noise.
     */
    public void simulateNextDay(Random random) {
        this.previousClose = this.currentPrice;

        double drift = 0.0004;                                  // slight long-term growth
        double shock = (random.nextDouble() * 2 - 1) * volatility; // -volatility .. +volatility
        double newPrice = currentPrice * (1 + drift + shock);

        // A share price can never fall to zero or below in this simulation.
        this.currentPrice = Math.max(1.0, Math.round(newPrice * 100.0) / 100.0);
    }

    public double getChange() {
        return currentPrice - previousClose;
    }

    public double getChangePercent() {
        if (previousClose == 0) {
            return 0.0;
        }
        return (getChange() / previousClose) * 100.0;
    }

    /** Simple text arrow so the console table reads like a real ticker. */
    public String getTrendSymbol() {
        double change = getChange();
        if (change > 0)  return "UP";
        if (change < 0)  return "DOWN";
        return "--";
    }

    public String getSymbol()        { return symbol; }
    public String getCompanyName()   { return companyName; }
    public String getSector()        { return sector; }
    public double getCurrentPrice()  { return currentPrice; }
    public double getPreviousClose() { return previousClose; }

    public void setCurrentPrice(double price) {
        this.currentPrice = price;
    }

    @Override
    public String toString() {
        return symbol + " (" + companyName + ") @ " + String.format("%.2f", currentPrice);
    }
}
