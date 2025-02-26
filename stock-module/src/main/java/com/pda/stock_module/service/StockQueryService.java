package com.pda.stock_module.service;

import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.web.dto.TopStockResponse;
import com.pda.stock_module.web.model.ListModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.lang.model.SourceVersion;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockQueryService {
    private final RedisCommon redisCommon;


    public List<TopStockResponse> getTop10ByTheme(String stockName) {
        try {
            // Redis에서 theme 가져오기
            String theme = redisCommon.getFromHash("stock:" + stockName, "sectorName");
            if (theme == null || theme.isEmpty()) {
                throw new IllegalArgumentException("Sector name not found for stock: " + stockName);
            }
            System.out.println("theme = " + theme);

            // Redis에서 stockLists 가져오기
            List<ListModel> stockLists = redisCommon.getAllList("sector:" + theme, ListModel.class);
            if (stockLists == null || stockLists.isEmpty()) {
                throw new IllegalStateException("No stock list found for sector: " + theme);
            }

            // ListModel -> TopStockResponse로 변환
            List<TopStockResponse> topStockResponses = stockLists.stream()
                    .map(listModel -> new TopStockResponse(
                            listModel.getStockName(),
                            listModel.getPrice(),
                            listModel.getPriceChange()
                    ))
                    .collect(Collectors.toList());

            return topStockResponses;

        } catch (IllegalArgumentException | IllegalStateException e) {
            // 적절한 예외 메시지 로그 출력
            log.error("Error while fetching stock data: {}", e.getMessage());
            throw e; // 예외 재발생
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while fetching stock data.", e);
        }
    }

}
