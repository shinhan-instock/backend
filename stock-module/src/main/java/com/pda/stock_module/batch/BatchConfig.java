//package com.pda.stock_module.batch;
//
//import com.pda.stock_module.batch.step.FetchStockListStep;
//import com.pda.stock_module.service.FetchStockListService;
//import com.pda.stock_module.web.dto.BatchStockDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.util.Map;
//
//@Configuration
//@RequiredArgsConstructor
//public class BatchConfig {
//    private final JobRepository jobRepository;
//    private final FetchStockListService fetchStockListService;
//    private final PlatformTransactionManager transactionManager;
//    private final FetchStockListStep fetchStockListStep;
//
//    @Bean
//    public Job fetchStockJob() {
//        return new JobBuilder("fetchStockJob", jobRepository)
//                .start(fetchStockListStep.fetchStockStep())  // 하나의 Step에서 처리
//                .build();
//
//    }
//
//
//
//
//
//
//}
