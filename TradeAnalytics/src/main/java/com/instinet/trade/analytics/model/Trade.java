package com.instinet.trade.analytics.model;

public class Trade {

    private final int size;
    private final Flag flag;
    private final double price;
    private final Instrument instrument;
    private final long timeStamp;

    public Trade(long timeStamp, Instrument instrument, double price, int size, Flag flag) {
        this.size = size;
        this.flag = flag;
        this.price = price;
        this.instrument = instrument;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public Flag getFlag() {
        return flag;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + size;
        result = 31 * result + flag.hashCode();
        result = 31 * result + Double.hashCode(price);
        result = 31 * result + instrument.hashCode();
        result = 31 * result + Long.hashCode(timeStamp);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trade) {
            Trade trade = (Trade) obj;
            return size == trade.getSize() &&
                    flag == trade.getFlag() &&
                    price == trade.getPrice() &&
                    timeStamp == trade.getTimeStamp() &&
                    instrument == trade.getInstrument();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "timestamp: " + timeStamp + ", symbol: " + instrument + ", price: " + price + ", size: " + size + ", flags: " + flag;
    }
}
