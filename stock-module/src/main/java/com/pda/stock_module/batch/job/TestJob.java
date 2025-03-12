//package com.pda.stock_module.batch.job;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.Step;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class TestJob {
//
//    private final JobRepository jobRepository;
//
//    @Bean
//    public Job simpleStockClosingPriceJob(Step stockClosingPriceStep) {
//        return new JobBuilder("simpleStockClosingPriceJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(stockClosingPriceStep)
//                .build();
//    }
//}
//
