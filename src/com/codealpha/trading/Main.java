package com.codealpha.trading;

import java.io.IOException;
import java.util.Scanner;

/**
 * TASK 2 - STOCK TRADING PLATFORM
 * CodeAlpha Java Programming Internship
 *
 * Simulates a stock market you can trade against: view live prices, buy and
 * sell shares, advance the trading day, and track how your portfolio performs.
 */
public class Main {

    private static final double STARTING_CAPITAL = 100000.0;

    private final Market market = new Market();
    private final ConsoleView view = new ConsoleView();
    private final FileStorage storage = new FileStorage();
    private final InputHelper input;

    private User user;

    public Main(Scanner scanner) {
        this.input = new InputHelper(scanner);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new Main(scanner).run();
        scanner.close();
    }

    public void run() {
        view.printBanner();
        startSession();

        boolean running = true;
        while (running) {
            view.printMenu(market.getTradingDay(), user.getPortfolio().getCashBalance());
            String choice = input.readLine("");

            switch (choice) {
                case "1": viewMarket();       break;
                case "2": buyShares();        break;
                case "3": sellShares();       break;
                case "4": viewPortfolio();    break;
                case "5": viewHistory();      break;
                case "6": viewPerformance();  break;
                case "7": advanceDay();       break;
                case "8": save(true);         break;
                case "0":
                    save(false);
                    view.printInfo("Session closed. Happy trading, " + user.getUsername() + "!");
                    running = false;
                    break;
                default:
                    view.printError("Invalid option. Please choose from the menu.");
            }
        }
    }

    /** Either resumes a saved session or creates a fresh account. */
    private void startSession() {
        try {
            if (storage.saveFileExists()) {
                User restored = storage.load(market);
                if (restored != null) {
                    this.user = restored;
                    view.printSuccess("Welcome back, " + user.getUsername()
                            + "! Session restored at trading day " + market.getTradingDay() + ".");
                    return;
                }
            }
        } catch (IOException ex) {
            view.printError("Could not read the saved session: " + ex.getMessage());
        }

        String name = input.readNonEmpty("  Enter your trader name: ");
        this.user = new User(name, STARTING_CAPITAL);
        view.printSuccess("Account created with a starting capital of "
                + String.format("%.2f", STARTING_CAPITAL) + ".");
    }

    private void viewMarket() {
        view.printMarket(market.getAllStocks());
        input.pause();
    }

    private void buyShares() {
        view.printMarket(market.getAllStocks());

        String symbol = input.readNonEmpty("  Enter the symbol you want to buy: ");
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            view.printError("'" + symbol + "' is not listed on this market.");
            return;
        }

        double cash = user.getPortfolio().getCashBalance();
        int affordable = (int) (cash / stock.getCurrentPrice());
        if (affordable < 1) {
            view.printError(String.format(
                    "You cannot afford even one share of %s at %.2f.", stock.getSymbol(), stock.getCurrentPrice()));
            return;
        }
        view.printInfo(String.format("Price %.2f | you can afford up to %d share(s).",
                stock.getCurrentPrice(), affordable));

        int quantity = input.readInt("  Quantity to buy: ", 1, affordable);
        try {
            user.getPortfolio().buy(stock, quantity, market.getTradingDay());
            view.printSuccess(String.format("Bought %d %s at %.2f  (total %.2f).",
                    quantity, stock.getSymbol(), stock.getCurrentPrice(),
                    quantity * stock.getCurrentPrice()));
        } catch (TradeException ex) {
            view.printError(ex.getMessage());
        }
    }

    private void sellShares() {
        Portfolio portfolio = user.getPortfolio();
        if (!portfolio.hasHoldings()) {
            view.printError("You have no shares to sell.");
            return;
        }
        view.printPortfolio(portfolio, market);

        String symbol = input.readNonEmpty("  Enter the symbol you want to sell: ");
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            view.printError("'" + symbol + "' is not listed on this market.");
            return;
        }

        Holding holding = findHolding(symbol);
        if (holding == null) {
            view.printError("You do not own any shares of " + stock.getSymbol() + ".");
            return;
        }

        int quantity = input.readInt("  Quantity to sell (max " + holding.getQuantity() + "): ",
                1, holding.getQuantity());
        try {
            double profit = portfolio.sell(stock, quantity, market.getTradingDay());
            view.printSuccess(String.format("Sold %d %s at %.2f  (total %.2f).",
                    quantity, stock.getSymbol(), stock.getCurrentPrice(),
                    quantity * stock.getCurrentPrice()));
            view.printInfo(String.format("Realised %s of %.2f on this trade.",
                    profit >= 0 ? "a profit" : "a loss", Math.abs(profit)));
        } catch (TradeException ex) {
            view.printError(ex.getMessage());
        }
    }

    private Holding findHolding(String symbol) {
        for (Holding h : user.getPortfolio().getHoldings()) {
            if (h.getSymbol().equalsIgnoreCase(symbol.trim())) {
                return h;
            }
        }
        return null;
    }

    private void viewPortfolio() {
        view.printPortfolio(user.getPortfolio(), market);
        input.pause();
    }

    private void viewHistory() {
        view.printTransactions(user.getPortfolio().getTransactions());
        input.pause();
    }

    private void viewPerformance() {
        view.printPerformance(user.getPortfolio(), market);
        input.pause();
    }

    /** Moves the market forward and shows what that did to the portfolio. */
    private void advanceDay() {
        double before = user.getPortfolio().getTotalValue(market);
        market.advanceDay();
        double after = user.getPortfolio().getTotalValue(market);

        view.printSuccess("Market moved to trading day " + market.getTradingDay() + ".");
        view.printInfo(String.format("Your net worth changed by %+.2f (now %.2f).",
                after - before, after));
        view.printMarket(market.getAllStocks());
        input.pause();
    }

    private void save(boolean announcePath) {
        try {
            storage.save(user, market);
            view.printSuccess("Session saved.");
            if (announcePath) {
                view.printInfo("File location: " + storage.getDataFilePath());
            }
        } catch (IOException ex) {
            view.printError("Save failed: " + ex.getMessage());
        }
    }
}
