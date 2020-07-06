package com.instinet.trade.analytics.producer;

import com.instinet.trade.analytics.exception.DataFeedParsingException;
import com.instinet.trade.analytics.model.Trade;
import com.instinet.trade.analytics.util.TradeParser;

import java.util.Collection;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Stream;

/**
 * Parses trade data from a feed and feeds trades messages onto a queue.
 */
public class TradeMessageQueueProducer implements Producer, Runnable {

    private final Queue<Trade> queue;
    private final TradeParser tradeParser;

    public TradeMessageQueueProducer(TradeParser tradeParser, Queue<Trade> queue) {
        this.queue = queue;
        this.tradeParser = tradeParser;
    }


    @Override
    public void produce() {
        try {
            Optional.ofNullable(tradeParser.parse()).map(Collection::stream).orElseGet(Stream::empty).forEach(queue::add);
        } catch (DataFeedParsingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        produce();
    }
}
