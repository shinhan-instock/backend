package com.pda.stock_module.batch.task.StockClosingPrice;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import com.pda.stock_module.domain.StockClosingPrice;
import com.pda.stock_module.repository.StockClosingPriceRepository;

import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class StockClosingPriceItemWriter implements ItemWriter<StockClosingPrice> {

    private final StockClosingPriceRepository stockClosingPriceRepository;

    @Override
    public void write(Chunk<? extends StockClosingPrice> chunk) throws Exception {
        List<? extends StockClosingPrice> items = chunk.getItems();
        stockClosingPriceRepository.saveAll(items);
    }

}
