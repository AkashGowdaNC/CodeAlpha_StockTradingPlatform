package com.codealpha.trading;

/**
 * Thrown when a trade cannot be completed - not enough cash, not enough shares,
 * invalid quantity, and so on.
 *
 * Using a custom exception means Portfolio never has to print anything: it just
 * refuses the trade and explains why, and the UI layer decides how to show it.
 */
public class TradeException extends Exception {

    private static final long serialVersionUID = 1L;

    public TradeException(String message) {
        super(message);
    }
}
