package com.codealpha.trading;

/**
 * A trader. Right now one user runs at a time, but modelling the user as its
 * own class means the program could later support multiple accounts without
 * touching Portfolio at all.
 */
public class User {

    private final String username;
    private final Portfolio portfolio;

    public User(String username, double startingCapital) {
        this.username = username;
        this.portfolio = new Portfolio(startingCapital);
    }

    public String getUsername()    { return username; }
    public Portfolio getPortfolio() { return portfolio; }
}
