package com.pda.stock_module.batch.task.StockClosingPrice;


import com.pda.stock_module.domain.StockClosingPrice;
import com.pda.stock_module.web.dto.StockClosingPriceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StockClosingPriceStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step stockClosingPriceStep(StockClosingPriceItemReader reader,
                                      StockClosingPriceItemProcessor processor,
                                      StockClosingPriceItemWriter writer) {
        return new StepBuilder("stockClosingPriceStep", jobRepository)
                .<StockClosingPriceDTO, StockClosingPrice>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
