package com.instinet.trade.analytics.util;

import com.instinet.trade.analytics.exception.DataFeedParsingException;
import com.instinet.trade.analytics.model.Flag;
import com.instinet.trade.analytics.model.Instrument;
import com.instinet.trade.analytics.model.Trade;

import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvTradeParser implements TradeParser {

    private static final String DELIMITER = ",";
    private static final int TIMESTAMP_INDEX = 0;
    private static final int INSTRUMENT_INDEX = 1;
    private static final int PRICE_INDEX = 2;
    private static final int SIZE_INDEX = 3;
    private static final int FLAG_INDEX = 4;
    private static final int HEADER_LINE_COUNT = 1;
    private final File file;

    private final Function<String, Trade> mapToTrade = (line) -> {
        String[] result = line.split(DELIMITER);
        return new Trade(Long.parseLong(result[TIMESTAMP_INDEX]),
                Instrument.getForName(result[INSTRUMENT_INDEX]),
                Double.parseDouble(result[PRICE_INDEX]),
                Integer.parseInt(result[SIZE_INDEX]),
                Flag.valueOf(result[FLAG_INDEX]));
    };

    public CsvTradeParser(File file) {
        this.file = file;
    }

    @Override
    public List<Trade> parse() throws DataFeedParsingException {
        List<Trade> inputList;
        try (InputStream inputStream = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            inputList = bufferedReader.lines().skip(HEADER_LINE_COUNT).map(mapToTrade).collect(Collectors.toList());
        } catch (IOException e) {
            throw new DataFeedParsingException(e.getMessage(), e);
        }
        return inputList;
    }
}
