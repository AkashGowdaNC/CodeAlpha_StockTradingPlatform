package com.codealpha.trading;

import java.util.List;

/** All console formatting for the trading platform. */
public class ConsoleView {

    private static final String LINE = "=".repeat(78);
    private static final String THIN = "-".repeat(78);

    public void printBanner() {
        System.out.println(LINE);
        System.out.println("          STOCK TRADING PLATFORM  |  CodeAlpha Java Internship");
        System.out.println(LINE);
    }

    public void printMenu(int tradingDay, double cash) {
        System.out.println();
        System.out.println(THIN);
        System.out.printf("  TRADING DAY %-4d                        Available cash: %.2f%n", tradingDay, cash);
        System.out.println(THIN);
        System.out.println("  1. View market            4. My portfolio        7. Advance to next day");
        System.out.println("  2. Buy shares             5. Trade history       8. Save session");
        System.out.println("  3. Sell shares            6. Performance report  0. Save and exit");
        System.out.println(THIN);
        System.out.print("  Choose an option: ");
    }

    public void printMarket(List<Stock> stocks) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  LIVE MARKET DATA");
        System.out.println(LINE);
        System.out.printf("  %-7s %-28s %-10s %-11s %-10s %s%n",
                "SYMBOL", "COMPANY", "SECTOR", "PRICE", "CHANGE", "CHANGE %");
        System.out.println(THIN);

        for (Stock s : stocks) {
            System.out.printf("  %-7s %-28s %-10s %-11.2f %+-10.2f %+.2f%% (%s)%n",
                    s.getSymbol(), truncate(s.getCompanyName(), 28), s.getSector(),
                    s.getCurrentPrice(), s.getChange(), s.getChangePercent(), s.getTrendSymbol());
        }
        System.out.println(LINE);
    }

    public void printPortfolio(Portfolio portfolio, Market market) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  MY PORTFOLIO");
        System.out.println(LINE);

        if (!portfolio.hasHoldings()) {
            System.out.println("  You do not own any shares yet. Use option 2 to buy some.");
        } else {
            System.out.printf("  %-7s %-6s %-11s %-11s %-13s %-12s %s%n",
                    "SYMBOL", "QTY", "AVG COST", "MARKET", "INVESTED", "VALUE", "P&L");
            System.out.println(THIN);

            for (Holding h : portfolio.getHoldings()) {
                Stock stock = market.getStock(h.getSymbol());
                double price = stock == null ? h.getAverageBuyPrice() : stock.getCurrentPrice();
                System.out.printf("  %-7s %-6d %-11.2f %-11.2f %-13.2f %-12.2f %+.2f (%+.2f%%)%n",
                        h.getSymbol(), h.getQuantity(), h.getAverageBuyPrice(), price,
                        h.getInvestedAmount(), h.getCurrentValue(price),
                        h.getUnrealisedProfit(price), h.getReturnPercent(price));
            }
        }

        System.out.println(THIN);
        System.out.printf("  Cash balance       : %.2f%n", portfolio.getCashBalance());
        System.out.printf("  Holdings value     : %.2f%n", portfolio.getHoldingsValue(market));
        System.out.printf("  TOTAL NET WORTH    : %.2f%n", portfolio.getTotalValue(market));
        System.out.println(LINE);
    }

    public void printPerformance(Portfolio portfolio, Market market) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  PERFORMANCE REPORT");
        System.out.println(LINE);

        double overall = portfolio.getOverallReturn(market);
        System.out.printf("  Starting capital       : %.2f%n", portfolio.getInitialCapital());
        System.out.printf("  Current net worth      : %.2f%n", portfolio.getTotalValue(market));
        System.out.printf("  Amount invested        : %.2f%n", portfolio.getTotalInvested());
        System.out.printf("  Cash in hand           : %.2f%n", portfolio.getCashBalance());
        System.out.println(THIN);
        System.out.printf("  Realised profit/loss   : %+.2f   (from shares already sold)%n",
                portfolio.getRealisedProfit());
        System.out.printf("  Unrealised profit/loss : %+.2f   (on shares still held)%n",
                portfolio.getUnrealisedProfit(market));
        System.out.printf("  OVERALL RETURN         : %+.2f  (%+.2f%%)%n",
                overall, portfolio.getOverallReturnPercent(market));
        System.out.println(THIN);
        System.out.println("  Verdict                : " + verdict(overall));
        System.out.printf("  Total trades executed  : %d%n", portfolio.getTransactions().size());
        System.out.println(LINE);
    }

    private String verdict(double overall) {
        if (overall > 0)  return "In profit - your positions are working out.";
        if (overall < 0)  return "In loss - the market moved against you.";
        return "Break-even.";
    }

    public void printTransactions(List<Transaction> transactions) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  TRADE HISTORY");
        System.out.println(LINE);

        if (transactions.isEmpty()) {
            System.out.println("  No trades executed yet.");
            System.out.println(LINE);
            return;
        }

        System.out.printf("  %-5s %-6s %-8s %-7s %-12s %-13s %s%n",
                "DAY", "TYPE", "SYMBOL", "QTY", "PRICE", "TOTAL", "REALISED P&L");
        System.out.println(THIN);
        for (Transaction t : transactions) {
            String pnl = t.getType() == Transaction.Type.SELL
                    ? String.format("%+.2f", t.getRealisedProfit()) : "-";
            System.out.printf("  %-5d %-6s %-8s %-7d %-12.2f %-13.2f %s%n",
                    t.getTradingDay(), t.getType(), t.getSymbol(), t.getQuantity(),
                    t.getPricePerShare(), t.getTotalValue(), pnl);
        }
        System.out.println(LINE);
        System.out.println("  Total trades: " + transactions.size());
    }

    public void printInfo(String message)    { System.out.println("  [i] " + message); }
    public void printSuccess(String message) { System.out.println("  [OK] " + message); }
    public void printError(String message)   { System.out.println("  [!] " + message); }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + ".";
    }
}
