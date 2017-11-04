package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.BetResult;
import data.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import service.OddsService;

import java.io.IOException;
import java.net.URL;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static service.AccountService.MSG_PLAYER_ID_EXIST;
import static service.AccountService.MSG_PLAYER_ID_NOT_FOUND;
import static service.AccountService.SUPER_USER_BALANCE;
import static service.AccountService.SUPER_USER_ID;
import static service.BetService.DEFAULT_BET_AMOUNT;

@MockBean(OddsService.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private OddsService oddsService;

    private static final int BET_WIN_FREE = Integer.valueOf(DEFAULT_BET_AMOUNT);
    private static final int BET_WIN_NO_FREE = 5;
    private static final int BET_LOSE_FREE = 15;
    private static final int BET_LOSE_NO_FREE = 20;
    private static final int PRIZE_WIN = 20;
    private static final int PRIZE_LOSE = 0;
    private URL baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    private static int initId = 1;

    @Before
    public void before() throws Exception {
        baseUrl = new URL("http://localhost:" + port);
        when(oddsService.calculateResult(BET_WIN_FREE)).thenReturn(new BetResult(PRIZE_WIN, true));
        when(oddsService.calculateResult(BET_WIN_NO_FREE)).thenReturn(new BetResult(PRIZE_WIN, false));
        when(oddsService.calculateResult(BET_LOSE_FREE)).thenReturn(new BetResult(PRIZE_LOSE, true));
        when(oddsService.calculateResult(BET_LOSE_NO_FREE)).thenReturn(new BetResult(PRIZE_LOSE, false));
    }

    @Test
    public void defaultUserWinAndFree() throws Exception {
        final int betTimes = 10;
        IntStream.range(0, betTimes).forEach(value -> getBet());
        assertResult(SUPER_USER_ID, betTimes, PRIZE_WIN, true, SUPER_USER_BALANCE);
    }

    @Test
    public void customUserWinAndFree() throws IOException {
        final String id = generateId();
        final int initBalance = 1000;
        final int betTimes = 10;
        addPlayerAndBet(id, initBalance, betTimes, BET_WIN_FREE);
        assertResult(id, betTimes, PRIZE_WIN, true, initBalance + betTimes * PRIZE_WIN - BET_WIN_FREE);
    }

    @Test
    public void customUserLoseAndNoFree() throws IOException {
        final String id = generateId();
        final int initBalance = 1000;
        final int betTimes = 10;
        addPlayerAndBet(id, initBalance, betTimes, BET_LOSE_NO_FREE);
        assertResult(id, betTimes, PRIZE_LOSE, false, initBalance - betTimes * BET_LOSE_NO_FREE);
    }

    @Test
    public void customUserWinAndNoFree() throws IOException {
        final String id = generateId();
        final int initBalance = 1000;
        final int betTimes = 10;
        addPlayerAndBet(id, initBalance, betTimes, BET_WIN_NO_FREE);
        assertResult(id, betTimes, PRIZE_WIN, false, initBalance - betTimes * BET_WIN_NO_FREE + betTimes * PRIZE_WIN);
    }

    @Test
    public void customUserLoseAndFree() throws IOException {
        final String id = generateId();
        final int initBalance = 1000;
        final int betTimes = 10;
        addPlayerAndBet(id, initBalance, betTimes, BET_LOSE_FREE);
        assertResult(id, betTimes, PRIZE_LOSE, true, initBalance - BET_LOSE_FREE);
    }

    @Test
    public void betWithNotExistUser() {
        final String id = generateId();
        final Object result = getBet(id, BET_LOSE_NO_FREE);
        assertThat((result instanceof String), is(true));
        assertThat(result, is(MSG_PLAYER_ID_NOT_FOUND + id));
    }

    @Test
    public void multipleUsers() throws InterruptedException {
        final String winnerId = generateId();
        final String loserId = generateId();
        final int initBalance = 1000;
        final int betTimes = 10;
        Thread winner = new Thread(() -> addPlayerAndBet(winnerId, initBalance, betTimes, BET_WIN_FREE));
        Thread loser = new Thread(() -> addPlayerAndBet(loserId, initBalance, betTimes, BET_LOSE_NO_FREE));
        winner.start();
        loser.start();
        winner.join();
        loser.join();
        assertResult(winnerId, betTimes, PRIZE_WIN, true, initBalance + betTimes * PRIZE_WIN - BET_WIN_FREE);
        assertResult(loserId, betTimes, PRIZE_LOSE, false, initBalance - betTimes * BET_LOSE_NO_FREE);
    }

    @Test
    public void getDefaultPlayer() throws IOException {
        final Player player = (Player) getPlayer(SUPER_USER_ID);
        assertThat(player, is(notNullValue()));
        assertThat(player.getId(), is(SUPER_USER_ID));
        assertThat(player.getBalance(), is(-1));
    }

    @Test
    public void getNotExistPlayer() {
        String id = generateId();
        final Object result = getPlayer(id);
        assertThat((result instanceof String), is(true));
        assertThat(result, is(MSG_PLAYER_ID_NOT_FOUND + id));
    }

    @Test
    public void addPlayer() throws IOException {
        String id = generateId();
        final int balance = 100;
        addPlayer(id, balance);
        final Player player = (Player) getPlayer(id);
        assertThat(player, is(notNullValue()));
        assertThat(player.getId(), is(id));
        assertThat(player.getBalance(), is(balance));
    }

    @Test
    public void addExistingPlayer() throws IOException {
        final Object result = addPlayer(SUPER_USER_ID, 100);
        assertThat((result instanceof String), is(true));
        assertThat(result, is(MSG_PLAYER_ID_EXIST + SUPER_USER_ID));
    }

    private void addPlayerAndBet(final String id, final int initBalance, final int betTimes, final int betLoseNoFree) {
        addPlayer(id, initBalance);
        IntStream.range(0, betTimes).forEach(value -> getBet(id, betLoseNoFree));
    }

    private void assertResult(final String id, final int betTimes, final int prize, final boolean isFree, final int balance) {
        final Player player = (Player) getPlayer(id);
        assertThat(player.getResults().size(), is(betTimes));
        player.getResults().forEach(betResult -> {
            assertThat(betResult.getPrize(), is(prize));
            assertThat(betResult.isNextRoundFree(), is(isFree));
        });
        assertThat(player.getBalance(), is(balance));
    }

    private Object getBet() {
        final ResponseEntity<String> response = template.getForEntity(baseUrl.toString() + "/bet", String.class);
        return getObject(response, BetResult.class);
    }

    private Object getBet(String id, Integer bet) {
        final ResponseEntity<String> response = template.getForEntity(String.format("%s/bet?id=%s&bet=%d",
                baseUrl.toString(), id, bet), String.class);
        return getObject(response, BetResult.class);
    }


    private Object getPlayer(String id) {
        final ResponseEntity<String> response = template.getForEntity(String.format("%s/player?id=%s",
                baseUrl.toString(), id), String.class);
        return getObject(response, Player.class);
    }

    private Object addPlayer(String id, Integer balance) {
        final ResponseEntity<String> response = template.postForEntity(String.format("%s/player?id=%s&balance=%s",
                baseUrl.toString(), id, balance), null, String.class);
        return getObject(response, Player.class);
    }

    private Object getObject(final ResponseEntity<String> response, final Class<?> clazz) {
        final String body = response.getBody();
        try {
            return body == null ? null : mapper.readValue(body, clazz);
        } catch (IOException e) {
            return body;
        }
    }

    private String generateId() {
        return String.valueOf(initId++);
    }
}