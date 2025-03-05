package com.pda.stock_module.web.controller;


import com.pda.stock_module.domain.client.MileageClient;
import com.pda.stock_module.service.StockQueryService;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.MileageResponseDto;
import com.pda.stock_module.web.dto.StockResponse;
import com.pda.stock_module.web.model.StockDetailModel;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockQueryService stockQueryService;
    private final MileageClient mileageClient;


    // 주식 상세 정보
    @GetMapping("/{stockName}")
    @Operation(summary = "주식 상세정보 검색", description = "검색한 주식의 정보를 보여줍니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ResponseEntity<DetailStockResponse> getStockDetail(
            @PathVariable String stockName
    ) {
        return ResponseEntity.ok(stockQueryService.getStockDetail(stockName));
    }

    @GetMapping("/pigs")
    @Operation(summary = "유저의 마일리지를 가지고, 해당하는 주식종목 top10", description = "유저가 교환할 수 있느 주식을 보여줍니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ResponseEntity<List<StockDetailModel>> getStockByMileage(@RequestHeader("Authorization") String authorizationHeader) {

        MileageResponseDto res = mileageClient.getMileage(authorizationHeader); // 마일리지 조회
        Long mileage = res.getMileage();
        System.out.println("mileage = " + mileage);

        return ResponseEntity.ok(stockQueryService.getStockByMileage(mileage));
    }

}
