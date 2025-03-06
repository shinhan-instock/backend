package com.pda.community_module.config;

import com.pda.community_module.web.dto.StockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "stock-service", url = "http://localhost:8081/stock/search",
        configuration = FeignConfig.class)
public interface StockServiceClient {
    @GetMapping("/data")
    StockResponseDTO getStockData(@RequestParam List<String> stockNames);
}
