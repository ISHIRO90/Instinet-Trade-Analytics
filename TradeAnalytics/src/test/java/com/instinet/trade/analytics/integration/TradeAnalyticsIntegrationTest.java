package com.instinet.trade.analytics.integration;

import com.instinet.trade.analytics.consumer.Consumer;
import com.instinet.trade.analytics.consumer.TradeMessageQueueConsumer;
import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.processor.InstrumentFlagPairCounter;
import com.instinet.trade.analytics.processor.LargestTradesByInstrumentProcessor;
import com.instinet.trade.analytics.processor.Processor;
import com.instinet.trade.analytics.processor.VolumeWeightedAveragePriceProcessor;
import com.instinet.trade.analytics.producer.Producer;
import com.instinet.trade.analytics.producer.TradeMessageQueueProducer;
import com.instinet.trade.analytics.util.CsvTradeParser;
import com.instinet.trade.analytics.util.TradeParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;

public class TradeAnalyticsIntegrationTest {

    private InstrumentFlagPairCounter instrumentFlagPairCounter;
    private LargestTradesByInstrumentProcessor largestTradesByInstrumentProcessor;
    private VolumeWeightedAveragePriceProcessor volumeWeightedAveragePriceProcessor;

    @Before
    public void setup() {
        //setup the trade queue and data parser
        Queue<Trade> tradeQueue = new ConcurrentLinkedQueue<>();
        TradeParser tradeParser = new CsvTradeParser(new File(Objects.requireNonNull(TradeAnalyticsIntegrationTest.class.getClassLoader().getResource("Trades.csv")).getFile()));

        //setup the different subscribers which will be listening to trade events
        instrumentFlagPairCounter = new InstrumentFlagPairCounter();
        largestTradesByInstrumentProcessor = new LargestTradesByInstrumentProcessor();
        volumeWeightedAveragePriceProcessor = new VolumeWeightedAveragePriceProcessor();

        List<Processor<Trade>> processors = new ArrayList<>();
        processors.add(instrumentFlagPairCounter);
        processors.add(largestTradesByInstrumentProcessor);
        processors.add(volumeWeightedAveragePriceProcessor);

        //setup the producer and consumer and let them run
        Producer producer = new TradeMessageQueueProducer(tradeParser, tradeQueue);
        Consumer consumer = new TradeMessageQueueConsumer(tradeQueue, processors);

        producer.produce();

        while (!tradeQueue.isEmpty()) {
            consumer.consume();
        }
    }

    @Test
    public void assertProcessors() {
        assertInstrumentFlagPairCounter();
        assertVolumeWeightedAveragePriceProcessor();
        assertLargestTradesByInstrumentProcessor();
    }

    private void assertInstrumentFlagPairCounter() {
        assertCount(Instrument.TAG_ABC_LN, Flag.E, 2);
        assertCount(Instrument.TAG_ABC_LN, Flag.G, 1);
        assertCount(Instrument.TAG_DEF_LN, Flag.F, 1);
        assertCount(Instrument.TAG_XYZ_LN, Flag.A, 1);
        assertCount(Instrument.TAG_XYZ_LN, Flag.Z, 1);
        assertCount(Instrument.TAG_123_LN, Flag.X, 1);
        assertCount(Instrument.TAG_456_LN, Flag.E, 1);
        assertCount(Instrument.TAG_456_LN, Flag.Z, 0);
    }

    private void assertVolumeWeightedAveragePriceProcessor() {
        assertVolumeWeightedAveragePrice(Instrument.TAG_123_LN, 98.6d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_456_LN, 123.4d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_ABC_LN, 119.53125d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_DEF_LN, 300d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_XYZ_LN, 200.004918699187d);
        assertVolumeWeightedAveragePrice(Instrument.TAG_789_LN, 0d);
    }

    private void assertLargestTradesByInstrumentProcessor() {
        assertLargestTrade(Instrument.TAG_123_LN, new Trade(1481107485790L, Instrument.TAG_123_LN, 98.6d, 746, Flag.X));
        assertLargestTrade(Instrument.TAG_456_LN, new Trade(1481107485790L, Instrument.TAG_456_LN, 123.4d, 567, Flag.E));
        assertLargestTrade(Instrument.TAG_ABC_LN, new Trade(1481107485790L, Instrument.TAG_ABC_LN, 120d, 500, Flag.E));
        assertLargestTrade(Instrument.TAG_DEF_LN, new Trade(1481107485790L, Instrument.TAG_DEF_LN, 300d, 500, Flag.F));
        assertLargestTrade(Instrument.TAG_XYZ_LN, new Trade(1481107485791L, Instrument.TAG_XYZ_LN, 200d, 1000, Flag.A));
        assertLargestTrade(Instrument.TAG_789_LN, null);
    }

    private void assertCount(Instrument instrument, Flag flag, int expectedCount) {
        int actualCount = instrumentFlagPairCounter.getTradeCount(flag, instrument);
        assertEquals(expectedCount, actualCount);
    }

    private void assertLargestTrade(Instrument instrument, Trade expectedTrade) {
        Trade actualTrade = largestTradesByInstrumentProcessor.getLargestTrade(instrument);
        assertEquals(expectedTrade, actualTrade);
    }

    private void assertVolumeWeightedAveragePrice(Instrument instrument, double expectedValue) {
        double actualValue = volumeWeightedAveragePriceProcessor.getVolumeWeightedAveragePrice(instrument);
        assertEquals(expectedValue, actualValue, 0);
    }
}
