package com.pda.community_module.batch.task.likeTop10;

import com.pda.community_module.domain.Post;
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
public class LikeTop10StepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step likeTop10Step(
            LikeTop10Reader reader,
            LikeTop10Processor processor,
            LikeTop10Writer writer
    ) {
        return new StepBuilder("likeTop10Step", jobRepository)
                .<List<Post>, MileageRequest>chunk(1, transactionManager) // 한 번만 실행
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}

