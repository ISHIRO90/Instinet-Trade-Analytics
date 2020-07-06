package com.instinet.trade.analytics.producer;

import com.instinet.trade.analytics.exception.DataFeedParsingException;
import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.util.TradeParser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Mockito.*;

public class TradeMessageQueueProducerTest {

    private Queue<Trade> queue;
    private List<Trade> trades;
    private TradeMessageQueueProducer producer;
    private TradeParser tradeParser;


    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws DataFeedParsingException {
        tradeParser = mock(TradeParser.class);
        trades = new ArrayList<>();
        trades.add(new Trade(1481107485791L, Instrument.TAG_123_LN, 100, 1000, Flag.F));
        trades.add(new Trade(1481107485791L, Instrument.TAG_456_LN, 105, 1000, Flag.G));
        when(tradeParser.parse()).thenReturn(trades);
        queue = mock(Queue.class);
        producer = new TradeMessageQueueProducer(tradeParser, queue);
    }

    @Test
    public void putTradesOnQueue() throws DataFeedParsingException {
        producer.produce();
        verify(tradeParser, times(1)).parse();
        trades.forEach(trade -> verify(queue, times(1)).add(trade));
    }

    @Test
    public void processNullDataFeed() throws DataFeedParsingException {
        when(tradeParser.parse()).thenReturn(null);
        producer.produce();
        verify(tradeParser, times(1)).parse();
        verifyNoInteractions(queue);
    }


}
