package data;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private int balance;
    private List<BetResult> results;
    //TODO: add more fields
    private String currency;
    private String nationality;

    @SuppressWarnings("unused") // for jackson
    public Player() {
    }

    public Player(final String id, final Integer balance) {
        this.id = id;
        this.balance = balance;
        results = new ArrayList<>(100);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(final int balance) {
        this.balance = balance;
    }

    public List<BetResult> getResults() {
        return results;
    }

    public void setResults(final List<BetResult> results) {
        this.results = results;
    }
}
