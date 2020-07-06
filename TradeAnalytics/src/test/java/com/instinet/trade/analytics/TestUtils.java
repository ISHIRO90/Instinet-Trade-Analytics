package com.instinet.trade.analytics;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.processor.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestUtils {

    private static List<Trade> trades;

    public static void processMultipleTrades(Processor<Trade> processor) {
        Trade trade1 = new Trade(1481107485791L, Instrument.TAG_123_LN, 200.5d, 950, Flag.A);
        Trade trade2 = new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 10000, Flag.A);
        Trade trade3 = new Trade(1481107485791L, Instrument.TAG_123_LN, 150d, 10000, Flag.Z);
        Trade trade4 = new Trade(1481107485791L, Instrument.TAG_123_LN, 100d, 10000, Flag.F);
        Trade trade5 = new Trade(1481107485791L, Instrument.TAG_DEF_LN, 205d, 10000, Flag.Z);
        Trade trade6 = new Trade(1481107485791L, Instrument.TAG_DEF_LN, 200d, 10000, Flag.F);
        Trade trade7 = new Trade(1481107485791L, Instrument.TAG_DEF_LN, 300d, 50000, Flag.G);
        Trade trade8 = new Trade(1481107485791L, Instrument.TAG_ABC_LN, 200d, 10000, Flag.A);

        processor.process(trade1);
        processor.process(trade2);
        processor.process(trade3);
        processor.process(trade4);
        processor.process(trade5);
        processor.process(trade6);
        processor.process(trade7);
        processor.process(trade8);
    }

    public static void processTradesConcurrently(Processor<Trade> processor, List<Trade> trades) throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (Trade trade : trades) {
            service.execute(() -> {
                processor.process(trade);
                latch.countDown();
            });
        }
        latch.await();
    }

    public static List<Trade> getTrades() {
        if (trades != null && trades.size() == 100) {
            return trades;
        }

        trades = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 205d, 100 * i, Flag.A));
            trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 300d, 500 * i, Flag.A));
            trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 150d, 150 * i, Flag.Z));
            trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 200d, 700 * i, Flag.F));
            trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 170d, 120 * i, Flag.F));
            trades.add(new Trade(1481107485791L, Instrument.TAG_DEF_LN, 200d, 100 * i, Flag.F));
            trades.add(new Trade(1481107485791L, Instrument.TAG_ABC_LN, 200d, 100 * i, Flag.A));
            trades.add(new Trade(1481107485791L, Instrument.TAG_ABC_LN, 150d, 100 * i, Flag.X));
            trades.add(new Trade(1481107485791L, Instrument.TAG_ABC_LN, 400d, 100 * i, Flag.X));
            trades.add(new Trade(1481107485791L, Instrument.TAG_ABC_LN, 500d, 100 * i, Flag.X));
        }
        return trades;
    }
}
