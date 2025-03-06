package com.pda.stock_module.batch.task.StockSentimentReset;


import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.domain.StockClosingSentiment;
import com.pda.stock_module.web.dto.StockSentimentResetDTO;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class StockSentimentResetItemProcessor implements ItemProcessor<StockSentiment, StockSentimentResetDTO> {

    @Override
    public StockSentimentResetDTO process(StockSentiment item) throws Exception {
        StockClosingSentiment closingSentiment = StockClosingSentiment.builder()
                .stockCode(item.getStockCode())
                .stockName(item.getStockName())
                .closingSentimentScore(item.getSentimentScore())
                .build();
        return new StockSentimentResetDTO(closingSentiment, item.getId());
    }
}
