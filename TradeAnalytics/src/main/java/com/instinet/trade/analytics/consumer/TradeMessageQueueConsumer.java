package com.instinet.trade.analytics.consumer;

import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.processor.Processor;

import java.util.Collection;
import java.util.Queue;

public class TradeMessageQueueConsumer implements Consumer, Runnable {

    private final Queue<Trade> tradeMessageQueue;
    private final Collection<Processor<Trade>> processors;
    private boolean shouldStop = false;

    public TradeMessageQueueConsumer(Queue<Trade> tradeMessageQueue, Collection<Processor<Trade>> processors) {
        this.tradeMessageQueue = tradeMessageQueue;
        this.processors = processors;
    }

    @Override
    public void consume() {
        Trade trade = tradeMessageQueue.poll();
        if (trade != null) {
            processors.forEach(processor -> processor.process(trade));
        }
    }

    @Override
    public void run() {
        while (!shouldStop) {
            consume();
        }
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }
}
