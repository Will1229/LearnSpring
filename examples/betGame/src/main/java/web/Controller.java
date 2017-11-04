package web;

import exception.AccountServiceException;
import exception.BetServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.AccountService;
import service.BetService;
import service.ServiceConfiguration;

import static service.AccountService.SUPER_USER_ID;
import static service.BetService.DEFAULT_BET_AMOUNT;

@Import(ServiceConfiguration.class)
@RestController
public class Controller {

    private BetService betService;
    private AccountService accountService;

    @Autowired
    public Controller(final BetService betService, final AccountService accountService) {
        this.betService = betService;
        this.accountService = accountService;
    }

    @GetMapping(path = "/bet")
    public Object bet() {
        return bet(SUPER_USER_ID, Integer.valueOf(DEFAULT_BET_AMOUNT));
    }

    @GetMapping(path = "/bet", params = {"id", "bet"})
    public Object bet(@RequestParam(name = "id", required = false, defaultValue = SUPER_USER_ID) final String id,
                      @RequestParam(name = "bet", required = false, defaultValue = DEFAULT_BET_AMOUNT) final Integer bet) {
        try {
            return betService.bet(id, bet);
        } catch (BetServiceException | AccountServiceException e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/player", params = {"id"})
    public Object getPlayer(@RequestParam(name = "id", defaultValue = SUPER_USER_ID) final String id) {
        try {
            return accountService.getPlayer(id);
        } catch (AccountServiceException e) {
            return handleException(e);
        }
    }

    @PostMapping(path = "/player", params = {"id", "balance"})
    public Object addPlayer(@RequestParam(name = "id", defaultValue = SUPER_USER_ID) final String id,
                            @RequestParam(name = "balance") final Integer balance) {
        try {
            return accountService.addPlayer(id, balance);
        } catch (AccountServiceException e) {
            return handleException(e);
        }
    }

    private Object handleException(final Throwable e) {
        //TODO: handle exceptions properly. e.g.: in a separate service
        return e.getMessage();
    }
}