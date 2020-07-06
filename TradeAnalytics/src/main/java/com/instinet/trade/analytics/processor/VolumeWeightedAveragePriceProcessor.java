package com.instinet.trade.analytics.processor;

import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.model.VolumeWeightedAveragePrice;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Keeps track of the volume weight average price (VWAP) on a per instrument basis and recalculates whenever
 * a new trade comes in.
 */
public class VolumeWeightedAveragePriceProcessor implements Processor<Trade> {

    private final static int INITIAL_COUNT = 1;
    private final static double DEFAULT_COUNT = 0.0d;
    private final Map<Instrument, AtomicStampedReference<VolumeWeightedAveragePrice>> priceVolumeTrackerMap = new ConcurrentHashMap<>();

    public double getVolumeWeightedAveragePrice(Instrument instrument) {
        return Optional.ofNullable(priceVolumeTrackerMap.get(instrument)).map(AtomicStampedReference::getReference).map(VolumeWeightedAveragePrice::getVolumeWeightedAveragePrice).orElse(DEFAULT_COUNT);
    }

    @Override
    public void process(Trade trade) {
        if (priceVolumeTrackerMap.containsKey(trade.getInstrument())) {
            recalculateAndSetVwap(trade);
        } else {
            calculateAndAddVwap(trade);
        }
    }

    private void calculateAndAddVwap(Trade trade) {
        long size = trade.getSize();
        double price = trade.getPrice();
        double priceVolume = size * price;
        VolumeWeightedAveragePrice vwap = new VolumeWeightedAveragePrice(size, priceVolume, price);

        if (priceVolumeTrackerMap.putIfAbsent(trade.getInstrument(), new AtomicStampedReference<>(vwap, INITIAL_COUNT)) != null) {
            process(trade);
        }
    }

    private void recalculateAndSetVwap(Trade trade) {
        AtomicStampedReference<VolumeWeightedAveragePrice> reference = priceVolumeTrackerMap.get(trade.getInstrument());
        int stamp = reference.getStamp();

        VolumeWeightedAveragePrice oldVwap = reference.getReference();

        long oldTotalVolume = oldVwap.getTotalVolume();
        double oldTotalPriceVolume = oldVwap.getTotalPriceVolume();

        double priceVolumeProduct = trade.getSize() * trade.getPrice();

        long newTotalVolume = oldTotalVolume + trade.getSize();
        double newTotalPriceVolume = oldTotalPriceVolume + priceVolumeProduct;
        double newVolumeWeightedAveragePrice = (newTotalPriceVolume / newTotalVolume);

        VolumeWeightedAveragePrice newVwap = new VolumeWeightedAveragePrice(newTotalVolume, newTotalPriceVolume, newVolumeWeightedAveragePrice);

        if (!reference.compareAndSet(oldVwap, newVwap, stamp, stamp + 1)) {
            recalculateAndSetVwap(trade);
        }
    }
}
