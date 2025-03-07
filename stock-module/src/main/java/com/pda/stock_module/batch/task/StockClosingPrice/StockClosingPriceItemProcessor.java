package com.pda.stock_module.batch.task.StockClosingPrice;


import com.pda.stock_module.web.dto.StockClosingPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.pda.stock_module.domain.StockClosingPrice;

@Slf4j
@Component
@StepScope
public class StockClosingPriceItemProcessor implements ItemProcessor<StockClosingPriceDTO, StockClosingPrice> {

    @Override
    public StockClosingPrice process(StockClosingPriceDTO item) throws Exception {
        try {
            Long closingPrice = Long.valueOf(item.getPrice());
            return StockClosingPrice.builder()
                    .stockCode(item.getStockCode())
                    .stockName(item.getStockName())
                    .closingPrice(closingPrice)
                    .build();
        } catch (NumberFormatException e) {
            log.error("종가 파싱 실패 - stockCode {}: {}", item.getStockCode(), e.getMessage());
            // 파싱에 실패한 경우 해당 항목은 스킵
            return null;
        }
    }
}
