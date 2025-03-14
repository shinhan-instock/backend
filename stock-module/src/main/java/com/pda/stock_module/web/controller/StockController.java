package com.pda.stock_module.web.controller;


import com.pda.stock_module.domain.client.MileageClient;
import com.pda.stock_module.service.StockQueryService;
import com.pda.stock_module.service.StockSentimentService;
import com.pda.stock_module.web.dto.*;
import com.pda.stock_module.web.model.StockDetailModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockQueryService stockQueryService;
    private final StockSentimentService stockSentimentService;
    private final MileageClient mileageClient;


    // 주식 상세 정보
//    @GetMapping("/{stockName}")
//    @Operation(summary = "주식 상세정보 검색", description = "검색한 주식의 정보를 보여줍니다.")
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
//    })
//    public ResponseEntity<DetailStockResponse> getStockDetail(
//            @RequestHeader(value="Authorization", required = false) String authorizationHeader,
//            @PathVariable String stockName
//    ) {
//        String userId;
//        if (authorizationHeader==null) {
//            userId = null;
//        } else {
//            userId = authorizationHeader.replace("Bearer ", "");
//        }
//        return ResponseEntity.ok(stockQueryService.getStockDetail(userId,stockName));
//    }

    @GetMapping(value = "/{stockName}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStockDetail(@PathVariable String stockName,
                                        @RequestHeader(value="Authorization", required = false) String authorizationHeader) {
        String userId = (authorizationHeader == null) ? null : authorizationHeader.replace("Bearer ", "");
        return stockQueryService.streamStockDetail(userId, stockName);
    }

//    @GetMapping("/pigs")
//    @Operation(summary = "유저의 마일리지를 가지고, 해당하는 주식종목 top10", description = "유저가 교환할 수 있느 주식을 보여줍니다.")
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
//    })
//    public ResponseEntity<List<StockDetailModel>> getStockByMileage(@RequestHeader("Authorization") String authorizationHeader) {
//
//        MileageResponseDTO res = mileageClient.getMileage(authorizationHeader); // 마일리지 조회
//        Long mileage = res.getMileage();
//
//        return ResponseEntity.ok(stockQueryService.getStockByMileage(mileage));
//    }

    // SSE 스트리밍 API (REST API 제거)
    @GetMapping(value = "/pigs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStockByMileage(@RequestHeader("Authorization") String authorizationHeader) {
        return stockQueryService.streamStockByMileage(authorizationHeader);
    }

    @PostMapping("/sentiment")
    public com.pda.core_module.apiPayload.ApiResponse<String> addStockSentiment(@RequestBody List<StockRequest> stockRequestList) {
        stockSentimentService.addStockSentiment(stockRequestList);
        return com.pda.core_module.apiPayload.ApiResponse.onSuccess("주식 감정 저장 완료.");
    }

    @GetMapping("/chart/{stockName}")
    @Operation(summary = "선그래프를 그리기 위해 감정지수와 종가데이터 반환", description = "감정지수와 종가데이터를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ResponseEntity<List<ChartResponseDTO>> getSentimentAndStockData(
            @PathVariable String stockName) {
        return ResponseEntity.ok(stockSentimentService.getClosingSentimentAndClosingStockData(stockName));
    }
}
