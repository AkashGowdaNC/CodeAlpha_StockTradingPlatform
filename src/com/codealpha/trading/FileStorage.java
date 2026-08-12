package com.codealpha.trading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves the whole session (cash, holdings, trade history, market prices and the
 * current trading day) to a plain text file, and loads it back on startup.
 *
 * The file is split into sections marked with #ACCOUNT, #HOLDING, #TRADE and
 * #PRICE so the loader always knows what kind of line it is reading.
 */
public class FileStorage {

    private static final String DATA_DIR  = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "portfolio.txt";

    public void save(User user, Market market) throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create the data folder.");
        }

        Portfolio portfolio = user.getPortfolio();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(DATA_FILE));

            writer.write("#ACCOUNT|" + user.getUsername()
                    + "|" + portfolio.getInitialCapital()
                    + "|" + portfolio.getCashBalance()
                    + "|" + portfolio.getRealisedProfit()
                    + "|" + market.getTradingDay());
            writer.newLine();

            for (Holding h : portfolio.getHoldings()) {
                writer.write("#HOLDING|" + h.getSymbol() + "|" + h.getQuantity()
                        + "|" + h.getAverageBuyPrice());
                writer.newLine();
            }

            for (Transaction t : portfolio.getTransactions()) {
                writer.write("#TRADE|" + t.getType() + "|" + t.getSymbol() + "|" + t.getQuantity()
                        + "|" + t.getPricePerShare() + "|" + t.getTradingDay()
                        + "|" + t.getRealisedProfit());
                writer.newLine();
            }

            for (Stock s : market.getAllStocks()) {
                writer.write("#PRICE|" + s.getSymbol() + "|" + s.getCurrentPrice());
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public boolean saveFileExists() {
        return new File(DATA_FILE).exists();
    }

    /** @return the restored User, or null if there was no saved session. */
    public User load(Market market) throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return null;
        }

        User user = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 0) {
                    continue;
                }
                try {
                    if (p[0].equals("#ACCOUNT") && p.length >= 6) {
                        user = new User(p[1], Double.parseDouble(p[2]));
                        user.getPortfolio().restoreCash(Double.parseDouble(p[3]));
                        user.getPortfolio().restoreRealised(Double.parseDouble(p[4]));
                        market.setTradingDay(Integer.parseInt(p[5]));

                    } else if (p[0].equals("#HOLDING") && p.length >= 4 && user != null) {
                        user.getPortfolio().restoreHolding(new Holding(
                                p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3])));

                    } else if (p[0].equals("#TRADE") && p.length >= 7 && user != null) {
                        user.getPortfolio().restoreTransaction(new Transaction(
                                Transaction.Type.valueOf(p[1]), p[2],
                                Integer.parseInt(p[3]), Double.parseDouble(p[4]),
                                Integer.parseInt(p[5]), Double.parseDouble(p[6])));

                    } else if (p[0].equals("#PRICE") && p.length >= 3) {
                        Stock stock = market.getStock(p[1]);
                        if (stock != null) {
                            stock.setCurrentPrice(Double.parseDouble(p[2]));
                        }
                    }
                } catch (RuntimeException ignored) {
                    // A single damaged line is skipped rather than killing the load.
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return user;
    }

    public String getDataFilePath() {
        return new File(DATA_FILE).getAbsolutePath();
    }
}
