package com.pda.stock_module.batch.task.StockSentimentReset;


import com.pda.stock_module.domain.StockClosingSentiment;
import com.pda.stock_module.repository.StockClosingSentimentRepository;
import com.pda.stock_module.repository.StockSentimentRepository;
import com.pda.stock_module.web.dto.StockSentimentResetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@StepScope
@RequiredArgsConstructor
public class StockSentimentResetItemWriter implements ItemWriter<StockSentimentResetDTO> {

    private final StockClosingSentimentRepository stockClosingSentimentRepository;
    private final StockSentimentRepository stockSentimentRepository;

    @Override
    public void write(Chunk<? extends StockSentimentResetDTO> chunk) throws Exception {
        List<StockClosingSentiment> closingSentiments = chunk.getItems().stream()
                .map(StockSentimentResetDTO::getClosingSentiment)
                .collect(Collectors.toList());
        stockClosingSentimentRepository.saveAll(closingSentiments);

        List<Long> idsToDelete = chunk.getItems().stream()
                .map(StockSentimentResetDTO::getOriginalId)
                .collect(Collectors.toList());
        stockSentimentRepository.deleteAllById(idsToDelete);
    }
}
