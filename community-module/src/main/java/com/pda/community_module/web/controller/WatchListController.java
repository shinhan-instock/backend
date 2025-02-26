package com.pda.community_module.web.controller;

import com.pda.community_module.service.WatchListService;
import com.pda.community_module.web.dto.WatchListRequestDTO;
import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/watchList")
public class WatchListController {

    private final WatchListService watchListService;

    @GetMapping("")
    @Operation(summary = "관심종목 실시간 주가 스트리밍", description = "사용자의 관심종목 주가를 SSE를 통해 실시간으로 전달합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public SseEmitter streamStockPrices(@RequestParam Long userId,
                                        @RequestParam int page,
                                        @RequestParam int size) {
        return watchListService.streamStockPrices(userId, page, size);
    }

    @PostMapping("")
    @Operation(summary = "관심종목 등록", description = "wish list를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<Void> addWatchList(@RequestBody WatchListRequestDTO requestDTO) {
        watchListService.addWatchList(requestDTO.getUserId(), requestDTO.getStockCode(), requestDTO.getStockName());
        return ApiResponse.onSuccess(null);
    }
}
