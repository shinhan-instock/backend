package com.pda.stock_module.batch.task.StockSentimentReset;


import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.repository.StockSentimentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@StepScope
public class StockSentimentResetItemReader implements ItemReader<StockSentiment> {

    private final StockSentimentRepository stockSentimentRepository;
    private Iterator<StockSentiment> iterator;

    public StockSentimentResetItemReader(StockSentimentRepository stockSentimentRepository) {
        this.stockSentimentRepository = stockSentimentRepository;
    }

    @Override
    public StockSentiment read() throws Exception {
        if (iterator == null) {
            List<StockSentiment> list = stockSentimentRepository.findAll();
            iterator = (list != null) ? list.iterator() : Collections.emptyIterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
