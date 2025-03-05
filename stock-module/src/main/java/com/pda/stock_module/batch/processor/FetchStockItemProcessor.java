//package com.pda.stock_module.batch.processor;
//
//import com.pda.stock_module.web.dto.BatchStockDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class FetchStockItemProcessor {
//
//    @Bean
//    public ItemProcessor<Map<String, Object>, BatchStockDTO> stockItemProcessor() {
//        return stockData -> {
//            String stockCode = (String) stockData.get("symbolCode");
//            if (stockCode != null && stockCode.startsWith("A")) {
//                stockCode = stockCode.substring(1); // 'A' 제거
//            }
//
//            String stockName = (String) stockData.get("name");
//            Long price = stockData.get("tradePrice") != null
//                    ? Math.round(Double.valueOf(stockData.get("tradePrice").toString()))
//                    : 0L;
//
//            String priceChange = null;
//            Object changeRateObj = stockData.get("changeRate");
//            if (changeRateObj instanceof Double) {
//                priceChange = String.format("%.2f", (Double) changeRateObj * 100);
//            } else if (changeRateObj instanceof String) {
//                priceChange = String.format("%.2f", Double.valueOf((String) changeRateObj) * 100);
//            }
//
//            return new BatchStockDTO(stockName, stockCode, price, priceChange);
//        };
//    }
//}
