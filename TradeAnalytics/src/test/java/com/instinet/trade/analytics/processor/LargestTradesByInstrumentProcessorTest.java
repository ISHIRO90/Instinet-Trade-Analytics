package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.instinet.trade.analytics.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class LargestTradesByInstrumentProcessorTest {

    private LargestTradesByInstrumentProcessor processor;

    @Before
    public void setup() {
        processor = new LargestTradesByInstrumentProcessor();
    }

    @Test
    public void processMultipleTradesDifferentInstruments() {
        processMultipleTrades(processor);
        assertLargestTrade(Instrument.TAG_123_LN, new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 10000, Flag.A));
        assertLargestTrade(Instrument.TAG_ABC_LN, new Trade(1481107485791L, Instrument.TAG_ABC_LN, 200d, 10000, Flag.A));
        assertLargestTrade(Instrument.TAG_DEF_LN, new Trade(1481107485791L, Instrument.TAG_DEF_LN, 300d, 50000, Flag.G));
    }

    @Test
    public void getTradeForUntradedInstrument() {
        assertLargestTrade(Instrument.TAG_789_LN, null);
    }

    @Test
    public void processMultipleTradesConcurrently() throws InterruptedException {
        List<Trade> trades = getTrades();

        processTradesConcurrently(processor, trades);

        assertLargestTrade(Instrument.TAG_123_LN, new Trade(1481107485791L, Instrument.TAG_123_LN, 300d, 5000, Flag.A));
        assertLargestTrade(Instrument.TAG_ABC_LN, new Trade(1481107485791L, Instrument.TAG_ABC_LN, 500d, 1000, Flag.X));
        assertLargestTrade(Instrument.TAG_DEF_LN, new Trade(1481107485791L, Instrument.TAG_DEF_LN, 200d, 1000, Flag.F));
    }

    private void assertLargestTrade(Instrument instrument, Trade expectedTrade) {
        Trade actualTrade = processor.getLargestTrade(instrument);
        assertEquals(expectedTrade, actualTrade);
    }
}
