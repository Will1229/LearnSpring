package service;

import data.BetResult;
import exception.AccountServiceException;
import exception.BetServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BetService {

    public static final String DEFAULT_BET_AMOUNT = "10";

    private OddsService oddsService;
    private AccountService accountService;

    private static final Set<String> freeRound = new HashSet<>(1000);

    @Autowired
    public BetService(final OddsService oddsService, final AccountService accountService) {
        this.oddsService = oddsService;
        this.accountService = accountService;
    }

    public BetResult bet(final String id, final int betAmount) throws BetServiceException, AccountServiceException {
        if (validateParameters(id, betAmount)) {
            final BetResult result = oddsService.calculateResult(betAmount);
            final int deduction = freeRound.contains(id) ? 0 : betAmount;
            accountService.updateAccount(id, deduction, result);
            if (result.isNextRoundFree()) {
                freeRound.add(id);
            } else {
                freeRound.remove(id);
            }
            return result;
        }
        return null;
    }

    private boolean validateParameters(final String id, final int betAmount) throws BetServiceException, AccountServiceException {
        if (!isIdValid(id)) {
            throw new BetServiceException("Invalid ID: " + id);
        }
        if (!isBetValid(betAmount)) {
            throw new BetServiceException("Invalid bet amount: " + betAmount);
        }
        if (accountService.getPlayer(id) == null) {
            throw new BetServiceException("Error happened when getting user id: " + id);
        }
        return true;
    }

    private boolean isBetValid(final int bet) {
        //TODO: validate the format and value of bet
        return true;
    }

    private boolean isIdValid(final String id) {
        //TODO: validate the format and value of id
        return true;
    }

}
