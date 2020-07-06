package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Finds the largest trade for a given instrument and stores a list of largest trade sizes per instrument.
 * Subscribes to new trades and if a larger trade for an instrument comes up, then it replaces the current trade
 * for that flag.
 */
public class LargestTradesByInstrumentProcessor implements Processor<Trade> {

    public static final int INITIAL_STAMP = 1;
    private final Map<Instrument, AtomicStampedReference<Trade>> instrumentTradeMap = new ConcurrentHashMap<>();

    public Trade getLargestTrade(Instrument instrument) {
        return Optional.ofNullable(instrumentTradeMap.get(instrument)).map(AtomicStampedReference::getReference).orElse(null);
    }


    @Override
    public void process(Trade trade) {
        if (instrumentTradeMap.containsKey(trade.getInstrument())) {
            compareToExistingTrade(trade);
        } else {
            addTrade(trade);
        }
    }

    private void addTrade(Trade trade) {
        AtomicStampedReference<Trade> tradeReference = new AtomicStampedReference<>(trade, INITIAL_STAMP);
        if (instrumentTradeMap.putIfAbsent(trade.getInstrument(), tradeReference) != null) {
            process(trade);
        }
    }

    private void compareToExistingTrade(Trade trade) {
        AtomicStampedReference<Trade> currentLargestTradeReference = instrumentTradeMap.get(trade.getInstrument());
        Trade currentLargestTrade = currentLargestTradeReference.getReference();
        int stamp = currentLargestTradeReference.getStamp();
        if (getTradeSize(trade) > getTradeSize(currentLargestTradeReference.getReference())) {
            replaceLargestTrade(stamp, currentLargestTradeReference, currentLargestTrade, trade);
        }
    }

    private void replaceLargestTrade(int stamp, AtomicStampedReference<Trade> reference, Trade currentLargestTrade, Trade trade) {
        if (!reference.compareAndSet(currentLargestTrade, trade, stamp, stamp + 1)) {
            process(trade);
        }
    }

    private double getTradeSize(Trade trade) {
        return (trade.getSize() * trade.getPrice());
    }
}
