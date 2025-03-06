//package com.pda.stock_module.batch.step;
//
//import com.pda.stock_module.batch.processor.FetchStockItemProcessor;
//import com.pda.stock_module.batch.reader.FetchStockItemReader;
//import com.pda.stock_module.batch.writer.FetchStockItemWriter;
//import com.pda.stock_module.web.dto.BatchStockDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class FetchStockListStep {
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final FetchStockItemReader fetchStockItemReader;
//    private final FetchStockItemProcessor fetchStockItemProcessor;
//    private final FetchStockItemWriter fetchStockItemWriter;
//    @Bean
//    public Step fetchStockStep() {
//        return new StepBuilder("fetchStockStep", jobRepository)
//                .<Map<String, Object>, BatchStockDTO>chunk(100, transactionManager) // Chunk 단위 설정 (100개씩 처리)
//                .reader(fetchStockItemReader.stockItemReader())  // Reader
//                .processor(fetchStockItemProcessor.stockItemProcessor())  // Processor
//                .writer(fetchStockItemWriter.stockItemWriter())  // Writer
//                .build();
//    }
//}
