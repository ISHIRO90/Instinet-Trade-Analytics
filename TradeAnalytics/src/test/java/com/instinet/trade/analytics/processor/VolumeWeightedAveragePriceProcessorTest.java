package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.instinet.trade.analytics.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class VolumeWeightedAveragePriceProcessorTest {

    private VolumeWeightedAveragePriceProcessor processor;

    @Before
    public void setup() {
        processor = new VolumeWeightedAveragePriceProcessor();
    }

    @Test
    public void processSingleTrade() {
        Trade trade = new Trade(1481107485791L, Instrument.TAG_123_LN, 300d, 50, Flag.A);
        processor.process(trade);
        assertVolumeWeightedAveragePrice(Instrument.TAG_123_LN, 300d);
    }

    @Test
    public void processMultipleTradesDifferentInstruments() {
        processMultipleTrades(processor);

        assertVolumeWeightedAveragePrice(Instrument.TAG_123_LN, 151.55008077544426d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_ABC_LN, 200d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_DEF_LN, 272.14285714285717d);
    }

    @Test
    public void getPriceForUntradedInstrument() {
        assertVolumeWeightedAveragePrice(Instrument.TAG_XYZ_LN, 0d);
    }

    @Test
    public void processMultipleTradesConcurrently() throws InterruptedException {
        List<Trade> trades = getTrades();
        processTradesConcurrently(processor, trades);

        assertVolumeWeightedAveragePrice(Instrument.TAG_123_LN, 225.0955414012739d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_ABC_LN, 312.5d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_DEF_LN, 200d);
    }

    private void assertVolumeWeightedAveragePrice(Instrument instrument, double expectedValue) {
        double actualValue = processor.getVolumeWeightedAveragePrice(instrument);
        assertEquals(expectedValue, actualValue, 0);
    }
}
