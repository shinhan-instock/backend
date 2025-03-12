package com.pda.community_module.batch.task.stockSentimentAnalysis;

import com.pda.community_module.domain.Sentiment;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockSentimentAnalysisStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step stockSentimentAnalysisStep(
            StockSentimentAnalysisReader reader,
            StockSentimentAnalysisProcessor processor,
            StockSentimentAnalysisWriter writer
    ) {
        return new StepBuilder("stockSentimentAnalysisStep", jobRepository)
                .<List<Sentiment>, List<StockRequest>>chunk(1, transactionManager) // 한 번만 실행
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
