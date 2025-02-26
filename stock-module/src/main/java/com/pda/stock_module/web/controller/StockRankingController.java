package com.pda.stock_module.web.controller;

import com.pda.stock_module.service.RankingListService;
import com.pda.stock_module.service.StockQueryService;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.StockRankResponse;
import com.pda.stock_module.web.dto.TopStockResponse;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockRankingController {
    private final RankingListService rankingListService;
    private final StockQueryService stockQueryService;

    // top20
    @GetMapping("/top20")
    public ResponseEntity<List<StockRankResponse>> getTop20ByFluctuation() {
        return ResponseEntity.ok(rankingListService.getTop20ByFluctuation());
    }

    // 테마별 최대 최저 top5
    @GetMapping("/{stockName}/theme")
    public ResponseEntity<List<TopStockResponse>> getTop10ByTheme(
            @PathVariable String stockName
    ) {
        return ResponseEntity.ok(stockQueryService.getTop10ByTheme(stockName));
    }

    @GetMapping("/{stockName}")
    public ResponseEntity<DetailStockResponse> getStockDetail(
            @PathVariable String stockName
    ) {
        return ResponseEntity.ok(stockQueryService.getStockDetail(stockName));
    }
}

