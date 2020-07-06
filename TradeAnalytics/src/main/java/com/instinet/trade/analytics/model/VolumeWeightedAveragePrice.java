package com.instinet.trade.analytics.model;

public class VolumeWeightedAveragePrice {

    private final long totalVolume;
    private final double totalPriceVolume;
    private final double volumeWeightedAveragePrice;

    public VolumeWeightedAveragePrice(long totalVolume, double totalPriceVolume, double volumeWeightedAveragePrice) {
        this.totalVolume = totalVolume;
        this.totalPriceVolume = totalPriceVolume;
        this.volumeWeightedAveragePrice = volumeWeightedAveragePrice;
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public double getTotalPriceVolume() {
        return totalPriceVolume;
    }

    public double getVolumeWeightedAveragePrice() {
        return volumeWeightedAveragePrice;
    }
}
