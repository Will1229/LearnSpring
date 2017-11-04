package service;

import data.BetResult;
import data.Player;
import exception.AccountServiceException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AccountService {

    public static final String SUPER_USER_ID = "0";
    public static final String MSG_PLAYER_ID_NOT_FOUND = "Player id not found: ";
    public static final String MSG_PLAYER_ID_EXIST = "Player id already exists: ";
    public static final int SUPER_USER_BALANCE = -1;

    private static Map<String, Player> playersCache;

    public AccountService(final int accountCacheCapacity) {
        playersCache = new LinkedHashMap<>(accountCacheCapacity); // TODO: a LRU map is more proper here.
        playersCache.put(SUPER_USER_ID, new Player(SUPER_USER_ID, SUPER_USER_BALANCE));
    }

    public void updateAccount(final String id, final int deduction, final BetResult result) throws AccountServiceException {
        final Player player = getPlayer(id);
        if (player != null && player.getResults() != null) {
            player.getResults().add(result);
            if (!SUPER_USER_ID.equals(id)) {
                updateBalance(player, deduction, result.getPrize());
            }
        }
    }

    public Player getPlayer(final String id) throws AccountServiceException {
        Player player = playersCache.get(id);
        if (player == null) {
            player = load(id);
        }
        if (player == null) {
            throw new AccountServiceException(MSG_PLAYER_ID_NOT_FOUND + id);
        } else {
            synchronized (this) {
                playersCache.put(id, player);
            }
            return player;
        }
    }

    public Player addPlayer(final String id, final int balance) throws AccountServiceException {
        if (!SUPER_USER_ID.equals(id) && load(id) == null) {
            final Player player = new Player(id, balance);
            add(player);
            synchronized (this) {
                playersCache.put(id, player);
            }
        } else {
            throw new AccountServiceException(MSG_PLAYER_ID_EXIST + id);
        }
        return null;
    }

    private void updateBalance(final Player player, final int deduction, final int prize) {
        final int newBalance = player.getBalance() - deduction + prize;
        player.setBalance(newBalance);
        //TODO: update player's balance in db
    }

    private void add(final Player player) {
        //TODO: add user into db.
    }

    private Player load(final String id) {
        //TODO: load user from db.
        return null;
    }
}
