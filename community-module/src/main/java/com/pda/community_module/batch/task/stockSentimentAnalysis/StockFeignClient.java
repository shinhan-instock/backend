package com.pda.community_module.batch.task.stockSentimentAnalysis;

import com.pda.community_module.config.StockFeignConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "stockSentiment-service", url = "${feign.stock-url}", configuration = StockFeignConfig.class)
public interface StockFeignClient {
    @PostMapping(value = "/stocks/sentiment", consumes = "application/json")
    @Headers("Content-Type: application/json")  // JSON 요청으로 명시
    void addStockSentiment(@RequestBody List<StockRequest> stockRequestList);
}
