package com.instinet.trade.analytics.model;

import java.util.Arrays;

public enum Instrument {

    TAG_123_LN("123 LN"),
    TAG_456_LN("456 LN"),
    TAG_789_LN("789 LN"),
    TAG_ABC_LN("ABC LN"),
    TAG_DEF_LN("DEF LN"),
    TAG_XYZ_LN("XYZ LN");

    private final String name;

    Instrument(String name) {
        this.name = name;
    }

    public static Instrument getForName(String name) {
        return Arrays.stream(Instrument.values()).filter(instrument -> instrument.getName().equals(name)).findFirst().get();
    }

    public String getName() {
        return name;
    }
}
