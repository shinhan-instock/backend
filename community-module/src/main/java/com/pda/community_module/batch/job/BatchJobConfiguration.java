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
            Step likeTop10Step,
            Step stockSentimentAnalysisStep
    ) {
        return new JobBuilder("batchJob", jobRepository)
                .validator(new TimeFormatJobParametersValidator(new String[]{"targetTime"}))
                .incrementer(new RunIdIncrementer())
                .start(midnightDecider)
                .on("MIDNIGHT").to(likeTop10Step)
                .from(midnightDecider)
                .on("NOT_MIDNIGHT").to(stockSentimentAnalysisStep)
                .end()
                .build();
    }
}
