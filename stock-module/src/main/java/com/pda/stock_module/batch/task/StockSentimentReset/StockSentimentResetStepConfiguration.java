package com.pda.stock_module.batch.task.StockSentimentReset;

import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.web.dto.StockSentimentResetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StockSentimentResetStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;


    @Bean
    public Step stockSentimentResetStep(
            StockSentimentResetItemReader stockSentimentResetItemReader,
            StockSentimentResetItemProcessor stockSentimentResetItemProcessor,
            StockSentimentResetItemWriter stockSentimentResetItemWriter
    ) {
        return new StepBuilder("stockSentimentResetStep", jobRepository)
                .<StockSentiment, StockSentimentResetDTO>chunk(100,platformTransactionManager)
                .reader(stockSentimentResetItemReader)  // Reader
                .processor(stockSentimentResetItemProcessor)  // Processor
                .writer(stockSentimentResetItemWriter)  // Writer
                .build();
    }

}
