package com.instinet.trade.analytics.consumer;

import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.processor.Processor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Mockito.*;

public class TradeMessageQueueConsumerTest {

    private Consumer consumer;
    private Queue<Trade> tradeQueue;
    private List<Processor<Trade>> processors;
    private Trade trade;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        processors = new ArrayList<>();
        processors.add((Processor<Trade>) mock(Processor.class));
        processors.add((Processor<Trade>) mock(Processor.class));
        processors.add((Processor<Trade>) mock(Processor.class));

        trade = new Trade(1481107485791L, Instrument.TAG_123_LN, 100, 1000, Flag.F);

        tradeQueue = mock(Queue.class);
        when(tradeQueue.poll()).thenReturn(trade);

        consumer = new TradeMessageQueueConsumer(tradeQueue, processors);
    }

    @Test
    public void consumeMessagesFromQueue() {
        consumer.consume();
        verify(tradeQueue, times(1)).poll();
        processors.forEach(processor -> verify(processor, times(1)).process(trade));
    }

    @Test
    public void pollingReturnsNull() {
        when(tradeQueue.poll()).thenReturn(null);
        consumer.consume();
        verify(tradeQueue, times(1)).poll();
        processors.forEach(Mockito::verifyNoInteractions);
    }
}
