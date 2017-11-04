package data;

import java.time.LocalDateTime;

public class BetResult {
    private boolean nextRoundFree;
    private int prize;
    //TODO: add more fields
    private String id;
    private LocalDateTime dateTime;

    @SuppressWarnings("unused") // for jackson
    public BetResult() {
        nextRoundFree = false;
        prize = 0;
    }

    public BetResult(final int prize, final boolean nextRoundFree) {
        this.nextRoundFree = nextRoundFree;
        this.prize = prize;
    }

    public boolean isNextRoundFree() {
        return nextRoundFree;
    }

    public void setNextRoundFree(final boolean nextRoundFree) {
        this.nextRoundFree = nextRoundFree;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(final int prize) {
        this.prize = prize;
    }

    @Override
    public String toString() {
        return "BetResult{" +
                "nextRoundFree=" + nextRoundFree +
                ", prize=" + prize +
                '}';
    }
}
