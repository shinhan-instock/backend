package com.pda.stock_module.web.controller;

import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.stock_module.service.StockSearchService;
import com.pda.stock_module.web.dto.StockSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock/search")
public class StockSearchController {
    private final StockSearchService stockSearchService;

    @GetMapping("/data")
    public ApiResponse<List<StockSearchResponseDTO.StockSearchRes>> getStockData(
            @RequestParam List<String> stockNames) {
        return ApiResponse.onSuccess(stockSearchService.getStockData(stockNames));
    }
}
