package com.pda.community_module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.community_module.converter.WatchListConverter;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.UserRepository;
import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.community_module.config.StockServiceClient;
import com.pda.community_module.converter.StockSearchConverter;
import com.pda.community_module.domain.WatchList;
import com.pda.community_module.repository.WatchListRepository;
import com.pda.community_module.web.dto.StockResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {

    private final WatchListRepository watchListRepository;
    private final StockServiceClient stockServiceClient;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter streamStockPrices(Long userId, int page, int size) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                // 관심종목을 페이징하여 가져옴
                Page<WatchList> watchListsPage = watchListRepository.findByUserId(userId, PageRequest.of(page, size));
                List<String> stockNames = watchListsPage.getContent().stream()
                        .map(WatchList::getStockName)
                        .collect(Collectors.toList());

                if (stockNames.isEmpty()) {
                    emitter.complete();
                    return;
                }

                // FeignClient를 통해 여러 주식 데이터 요청
                StockResponseDTO stockResponse = stockServiceClient.getStockData(stockNames);
                System.out.println("📌 Feign 응답 원본: " + stockResponse);

                // DTO 변환 (StockSearchConverter 사용)
                List<StockResponseDTO.StockResult> stockResults = StockSearchConverter.toStockSearchResList(stockResponse);

                // ApiResponse를 사용해 응답 구조 적용
                ApiResponse<List<StockResponseDTO.StockResult>> response = ApiResponse.onSuccess(stockResults);

                // JSON 변환 후 SSE로 전송
                String jsonResponse = objectMapper.writeValueAsString(response);
                emitter.send(jsonResponse);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, 0, 3, TimeUnit.SECONDS); // 3초마다 갱신

        return emitter;
    }

    @Transactional
    @Override
    public void addWatchList(Long userId, String stockCode, String stockName) {
        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 관심 종목 등록 (Converter 사용)
        WatchList watchListEntity = WatchListConverter.toWatchListEntity(user, stockCode, stockName);
        watchListRepository.save(watchListEntity);
    }
}
