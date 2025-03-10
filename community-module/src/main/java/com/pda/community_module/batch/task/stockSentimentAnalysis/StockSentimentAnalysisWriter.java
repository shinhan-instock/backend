package com.pda.community_module.batch.task.stockSentimentAnalysis;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@StepScope
public class StockSentimentAnalysisWriter implements ItemWriter<List<StockRequest>> {

    private final StockFeignClient stockFeignClient;

    @Override
    public void write(Chunk<? extends List<StockRequest>> chunk) throws Exception {
        List<StockRequest> stockRequests = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        stockFeignClient.addStockSentiment(stockRequests);
    }
}
