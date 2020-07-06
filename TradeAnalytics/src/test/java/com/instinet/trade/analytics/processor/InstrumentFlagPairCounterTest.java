package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.instinet.trade.analytics.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class InstrumentFlagPairCounterTest {

    InstrumentFlagPairCounter counter;

    @Before
    public void setup() {
        counter = new InstrumentFlagPairCounter();
    }

    @Test
    public void processSingleTrade() {
        Trade trade = new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 1000, Flag.A);
        counter.process(trade);
        assertCount(Instrument.TAG_123_LN, Flag.A, 1);
    }

    @Test
    public void processMultipleTradesSamePair() {
        Trade trade1 = new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 1000, Flag.A);
        Trade trade2 = new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 10000, Flag.A);

        counter.process(trade1);
        counter.process(trade2);

        assertCount(Instrument.TAG_123_LN, Flag.A, 2);
    }

    @Test
    public void processMultipleTradesDifferentPairs() {
        processMultipleTrades(counter);

        assertCount(Instrument.TAG_123_LN, Flag.A, 2);
        assertCount(Instrument.TAG_123_LN, Flag.F, 1);
        assertCount(Instrument.TAG_123_LN, Flag.Z, 1);
        assertCount(Instrument.TAG_ABC_LN, Flag.A, 1);
        assertCount(Instrument.TAG_DEF_LN, Flag.F, 1);
    }

    @Test
    public void getTradeCountForUntradedPair() {
        assertCount(Instrument.TAG_XYZ_LN, Flag.Z, 0);
    }

    @Test
    public void processMultipleTradesConcurrently() throws InterruptedException {
        List<Trade> trades = getTrades();

        processTradesConcurrently(counter, trades);

        assertCount(Instrument.TAG_123_LN, Flag.A, 20);
        assertCount(Instrument.TAG_123_LN, Flag.F, 20);
        assertCount(Instrument.TAG_123_LN, Flag.Z, 10);
        assertCount(Instrument.TAG_ABC_LN, Flag.A, 10);
        assertCount(Instrument.TAG_DEF_LN, Flag.F, 10);
    }

    private void assertCount(Instrument instrument, Flag flag, int expectedCount) {
        int actualCount = counter.getTradeCount(flag, instrument);
        assertEquals(expectedCount, actualCount);
    }

}
