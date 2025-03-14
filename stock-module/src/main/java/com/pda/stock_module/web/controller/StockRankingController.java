package com.pda.stock_module.web.controller;

import com.pda.stock_module.service.RankingListService;
import com.pda.stock_module.service.StockQueryService;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.StockPopularRankResponse;
import com.pda.stock_module.web.dto.StockRankResponse;
import com.pda.stock_module.web.dto.TopStockResponse;
import com.pda.stock_module.web.model.StockPopularModel;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/stocks/rankings")
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
//    @GetMapping("/{stockName}/theme")
//    public ResponseEntity<List<TopStockResponse>> getTop10ByTheme(
//            @PathVariable String stockName
//    ) {
//        return ResponseEntity.ok(stockQueryService.getTop10ByTheme(stockName));
//    }

    @GetMapping(value = "/{stockName}/theme/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTop10ByTheme(@PathVariable String stockName) {
        return stockQueryService.streamTop10ByTheme(stockName);
    }

    // TOP 10 언급 많은 종목 확인
//    @GetMapping("/top10")
//    public ResponseEntity<List<StockPopularRankResponse>> getTop10ByPopularity() {
//        return ResponseEntity.ok(rankingListService.getTop10ByPopularity());
//    }

    // SSE 스트리밍 API ( TOP 10 언급 많은 종목 확인 )
    @GetMapping(value = "/top10/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTop10ByPopularity() {
        return rankingListService.streamTop10ByPopularity();
    }
}

