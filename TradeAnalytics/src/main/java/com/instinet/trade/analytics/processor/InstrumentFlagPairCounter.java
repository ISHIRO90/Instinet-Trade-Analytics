package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Maintains a map of trade count for each instrument flag pair and updates the map when new trade events come in.
 */
public class InstrumentFlagPairCounter implements Processor<Trade> {

    private final static int DEFAULT_COUNT = 0;
    private final static int INITIAL_COUNT = 1;
    private final Map<InstrumentFlagPair, AtomicInteger> instrumentFlagTradeMap = new ConcurrentHashMap<>();

    @Override
    public void process(Trade trade) {
        InstrumentFlagPair instrumentFlagPair = new InstrumentFlagPair(trade.getFlag(), trade.getInstrument());
        if (instrumentFlagTradeMap.containsKey(instrumentFlagPair)) {
            instrumentFlagTradeMap.get(instrumentFlagPair).incrementAndGet();
        } else if (instrumentFlagTradeMap.putIfAbsent(instrumentFlagPair, new AtomicInteger(INITIAL_COUNT)) != null) {
            process(trade);
        }
    }

    public int getTradeCount(Flag flag, Instrument instrument) {
        return Optional.ofNullable(instrumentFlagTradeMap.get(new InstrumentFlagPair(flag, instrument))).map(AtomicInteger::get).orElse(DEFAULT_COUNT);
    }


    private static class InstrumentFlagPair {
        Flag flag;
        Instrument instrument;

        public InstrumentFlagPair(Flag flag, Instrument instrument) {
            this.flag = flag;
            this.instrument = instrument;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + flag.hashCode();
            result = 31 * result + instrument.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InstrumentFlagPair) {
                InstrumentFlagPair instrumentFlagPair = (InstrumentFlagPair) obj;
                return this.flag == instrumentFlagPair.flag &&
                        this.instrument == instrumentFlagPair.instrument;
            } else {
                return false;
            }
        }
    }
}
