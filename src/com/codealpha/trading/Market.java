package com.codealpha.trading;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The exchange: holds every listed Stock and moves the clock forward one
 * trading day at a time.
 */
public class Market {

    private final Map<String, Stock> stocks = new LinkedHashMap<String, Stock>();
    private final Random random = new Random();
    private int tradingDay = 1;

    public Market() {
        listDefaultStocks();
    }

    /** Sample listings so the program is usable the moment it starts. */
    private void listDefaultStocks() {
        list(new Stock("TCS",    "Tata Consultancy Services", "IT",        3850.00, 0.020));
        list(new Stock("INFY",   "Infosys Limited",           "IT",        1560.00, 0.025));
        list(new Stock("RELI",   "Reliance Industries",       "Energy",    2890.00, 0.022));
        list(new Stock("HDFC",   "HDFC Bank",                 "Banking",   1685.00, 0.018));
        list(new Stock("TATA",   "Tata Motors",               "Auto",       985.00, 0.035));
        list(new Stock("WIPR",   "Wipro Limited",             "IT",         452.00, 0.028));
        list(new Stock("SUNP",   "Sun Pharma",                "Pharma",    1240.00, 0.021));
        list(new Stock("ZOMT",   "Zomato Limited",            "Consumer",   198.00, 0.055));
    }

    public final void list(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    /** @return null when the symbol is not listed. */
    public Stock getStock(String symbol) {
        if (symbol == null) {
            return null;
        }
        return stocks.get(symbol.trim().toUpperCase());
    }

    public boolean isListed(String symbol) {
        return getStock(symbol) != null;
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<Stock>(stocks.values());
    }

    /** Moves every stock one day forward. */
    public void advanceDay() {
        for (Stock stock : stocks.values()) {
            stock.simulateNextDay(random);
        }
        tradingDay++;
    }

    public int getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(int day) {
        this.tradingDay = day;
    }
}
