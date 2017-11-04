package service;

import data.BetResult;
import org.springframework.stereotype.Service;

import static java.lang.Math.random;

@Service
public class OddsService {

    private int prizeMultiple;
    private double winBoundary;
    private double freeBoundary;
    private int defaultPrize;

    public OddsService(final int prizeMultiple, final double winBoundary, final double freeBoundary, final int defaultPrize) {
        this.prizeMultiple = prizeMultiple;
        this.winBoundary = winBoundary;
        this.freeBoundary = freeBoundary;
        this.defaultPrize = defaultPrize;
    }

    public BetResult calculateResult(final int bet) {
        BetResult result = new BetResult(defaultPrize, false);
        final double winOdds = generateOdds();
        if (winOdds <= winBoundary) {
            result.setPrize(bet * prizeMultiple);
        }
        final double freeOdds = generateOdds();
        if (freeOdds <= freeBoundary) {
            result.setNextRoundFree(true);
        }
        return result;
    }

    double generateOdds() {
        return random() * 100;
    }
}
