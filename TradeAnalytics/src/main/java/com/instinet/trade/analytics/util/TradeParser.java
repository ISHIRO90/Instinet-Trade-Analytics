package com.instinet.trade.analytics.util;

import com.instinet.trade.analytics.exception.DataFeedParsingException;
import com.instinet.trade.analytics.model.Trade;

import java.util.List;

public interface TradeParser {

    List<Trade> parse() throws DataFeedParsingException;
}
