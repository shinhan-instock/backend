package com.pda.community_module.batch.job;


import com.pda.community_module.batch.decider.MidnightDecider;
import com.pda.community_module.batch.validator.TimeFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MidnightDecider midnightDecider;

    @Bean
    public Job batchJob(
            Step stockSentimentAnalysisStep,
            Flow splitFlow
    ) {
        return new JobBuilder("batchJob", jobRepository)
                .validator(new TimeFormatJobParametersValidator(new String[]{"targetTime"}))
                .incrementer(new RunIdIncrementer())
                .start(midnightDecider)
                .on("MIDNIGHT").to(splitFlow) // 자정이면 병렬 실행
                .from(midnightDecider).on("NOT_MIDNIGHT").to(stockSentimentAnalysisStep) // 아니면 종목 감정 분석만 실행
                .end()
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
    public Flow flow1(Step likeTop10Step ) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(likeTop10Step)
//                .next(sentimentAnalysisStep)
                .build();
    }

    @Bean
    public Flow flow2(Step stockSentimentResetStep) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(stockSentimentResetStep)
                .build();
    }
}
