package com.instinet.trade.analytics.processor;

public interface Processor<T> {

    void process(T t);
}
