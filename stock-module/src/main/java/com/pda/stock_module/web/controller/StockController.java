package com.pda.stock_module.web.controller;


import com.pda.stock_module.service.StockQueryService;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.StockResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockQueryService stockQueryService;
    
    // 주식 상세 정보
    @GetMapping("/{stockName}")
    public ResponseEntity<DetailStockResponse> getStockDetail(
            @PathVariable String stockName
    ) {
        return ResponseEntity.ok(stockQueryService.getStockDetail(stockName));
    }

//    @PostMapping("/pigs")

}
