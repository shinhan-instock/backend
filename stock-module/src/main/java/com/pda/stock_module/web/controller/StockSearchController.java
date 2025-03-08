package com.pda.stock_module.web.controller;

import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.stock_module.service.StockSearchService;
import com.pda.stock_module.web.dto.StockSearchResponseDTO;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks/search")
public class StockSearchController {
    private final StockSearchService stockSearchService;

    @GetMapping("/data")
    public ApiResponse<List<StockSearchResponseDTO.StockSearchRes>> getStockData(
            @RequestParam List<String> stockNames) {
        return ApiResponse.onSuccess(stockSearchService.getStockData(stockNames));
    }

    // 주식 검색창 자동 완성 API
    @GetMapping("")
    public ResponseEntity<List<String>> searchStocks(@RequestParam String stockName) {
        List<String> result = stockSearchService.searchStockName(stockName);
        return ResponseEntity.ok(result);
    }

    // 해시태그 자동완성 (글)
    @GetMapping("/hashtag")
    public ResponseEntity<List<String>> searchMyStocks(@RequestHeader("Authorization") String authorizationHeader) {
        List<String> result = stockSearchService.searchMyStockName(authorizationHeader);
        return ResponseEntity.ok(result);
    }





}
