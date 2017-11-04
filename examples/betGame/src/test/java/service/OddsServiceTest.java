package service;

import data.BetResult;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class OddsServiceTest {

    private static final double WIN_FREE_ODDS = 1.0;
    private static final double WIN_NO_FREE_ODDS = 20.0;
    private static final double LOSE_NO_FREE_ODDS = 50.0;

    private OddsService oddsService;

    @Before
    public void before() {
        oddsService = spy(new OddsService(2, 30.0, 10.0, 0));
    }

    @Test
    public void winAndFree() {
        doAnswer(invocation -> WIN_FREE_ODDS).when(oddsService).generateOdds();
        final BetResult result = oddsService.calculateResult(10);
        assertThat(result.getPrize(), is(20));
        assertThat(result.isNextRoundFree(), is(true));
    }

    @Test
    public void winAndNoFree() {
        doAnswer(invocation -> WIN_NO_FREE_ODDS).when(oddsService).generateOdds();
        final BetResult result = oddsService.calculateResult(10);
        assertThat(result.getPrize(), is(20));
        assertThat(result.isNextRoundFree(), is(false));
    }

    @Test
    public void loseAndFree() {
        final List<Double> answers = Arrays.asList(LOSE_NO_FREE_ODDS, WIN_FREE_ODDS);
        final Iterator<Double> it = answers.iterator();
        doAnswer(invocation -> it.next()).when(oddsService).generateOdds();
        final BetResult result = oddsService.calculateResult(10);
        assertThat(result.getPrize(), is(0));
        assertThat(result.isNextRoundFree(), is(true));
    }

    @Test
    public void loseAndNoFree() {
        doAnswer(invocation -> LOSE_NO_FREE_ODDS).when(oddsService).generateOdds();
        final BetResult result = oddsService.calculateResult(10);
        assertThat(result.getPrize(), is(0));
        assertThat(result.isNextRoundFree(), is(false));
    }

}