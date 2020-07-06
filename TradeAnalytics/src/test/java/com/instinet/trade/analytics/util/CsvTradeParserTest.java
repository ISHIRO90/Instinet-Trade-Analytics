package com.instinet.trade.analytics.util;

import com.instinet.trade.analytics.exception.DataFeedParsingException;
import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class CsvTradeParserTest {

    CsvTradeParser csvTradeParser;

    @Before
    public void setup() {
        csvTradeParser = new CsvTradeParser(new File(Objects.requireNonNull(CsvTradeParserTest.class.getClassLoader().getResource("Trades.csv")).getFile()));
    }

    @Test
    public void processCsv() throws DataFeedParsingException {
        int expectedNumberOfTrades = 8;
        List<Trade> trades = csvTradeParser.parse();
        assertEquals(expectedNumberOfTrades, trades.size());
        assertTrade(trades.get(0), Flag.A, Instrument.TAG_XYZ_LN, 1481107485791L, 200d, 1000);
        assertTrade(trades.get(1), Flag.Z, Instrument.TAG_XYZ_LN, 1481107485791L, 200.01d, 968);
        assertTrade(trades.get(2), Flag.E, Instrument.TAG_ABC_LN, 1481107485791L, 150d, 40);
        assertTrade(trades.get(3), Flag.G, Instrument.TAG_ABC_LN, 1481107485790L, 105d, 100);
        assertTrade(trades.get(4), Flag.F, Instrument.TAG_DEF_LN, 1481107485790L, 300d, 500);
        assertTrade(trades.get(5), Flag.X, Instrument.TAG_123_LN, 1481107485790L, 98.6d, 746);
        assertTrade(trades.get(6), Flag.E, Instrument.TAG_456_LN, 1481107485790L, 123.4d, 567);
        assertTrade(trades.get(7), Flag.E, Instrument.TAG_ABC_LN, 1481107485790L, 120d, 500);
    }

    private void assertTrade(Trade trade, Flag expectedFlag, Instrument expectedInstrument, long expectedTimeStamp, double expectedPrice, int expectedSize) {
        assertEquals(expectedFlag, trade.getFlag());
        assertEquals(expectedInstrument, trade.getInstrument());
        assertEquals(expectedTimeStamp, trade.getTimeStamp());
        assertEquals(expectedPrice, trade.getPrice(), 0);
        assertEquals(expectedSize, trade.getSize());
    }

}
