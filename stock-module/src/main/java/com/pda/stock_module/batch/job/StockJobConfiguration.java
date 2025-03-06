package com.pda.stock_module.batch.job;

import com.pda.stock_module.batch.validator.TimeFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class StockJobConfiguration {

    private final JobRepository jobRepository;


    @Bean
    public Job stockJob(
            Flow splitFlow
    ){
    return new JobBuilder("stockJob",jobRepository)
            .validator(new TimeFormatJobParametersValidator(new String[]{"targetTime"}))
            .start(splitFlow)
            .build()
            .build();



}
    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2)
                .build();
    }

    @Bean
    public Flow flow1(Step stockSentimentResetStep ) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(stockSentimentResetStep)
                .build();
    }

    @Bean
    public Flow flow2(Step stockClosingPriceStep ) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(stockClosingPriceStep)
                .build();
    }


}
