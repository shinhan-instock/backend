package com.pda.stock_module.service;

import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.stock_module.domain.Company;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.repository.StockQueryRepository;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.TopStockResponse;
import com.pda.stock_module.web.model.ListModel;
import com.pda.stock_module.web.model.StockDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockQueryService {
    private final RedisCommon redisCommon;
    private final StringRedisTemplate redisTemplate;

    private final StockQueryRepository stockQueryRepository;

    public List<TopStockResponse> getTop10ByTheme(String stockName) {
        try {
            // Redis에서 theme 가져오기
            String theme = redisCommon.getValueFromHash("stock:" + stockName, "sectorName");
            if (theme == null || theme.isEmpty()) {
                throw new IllegalArgumentException("Sector name not found for stock: " + stockName);
            }

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

    // 주식 상세 정보 조회
    public DetailStockResponse getStockDetail(String stockName) {
        try {
            StockDetailModel stockInfo = redisCommon.getEntriesFromHash(stockName, StockDetailModel.class);

            if (stockInfo.getStockCode() == null) {
                throw new GeneralException(ErrorStatus.STOCK_NOT_FOUND); // ✅ 서비스에서 예외 발생
            }

            String key = "stocks:popular";

            Company company = stockQueryRepository.findByStockName(stockName);
            Double score = redisTemplate.opsForZSet().score(key, stockName);

            if (score != null) {
                redisTemplate.opsForZSet().incrementScore(key, stockName, 1); // stockName의 score +1
            }

            return new DetailStockResponse(
                    stockInfo.getStockName(),
                    stockInfo.getStockCode(),
                    stockInfo.getPrice(),
                    stockInfo.getPriceChange(),
                    company.getDescription()
            );


        } catch (Exception e) {
            System.err.println("Error while fetching stock details: " + e.getMessage());
            throw e;
        }
    }

    // 보유 마일리지에 해당하는 주식 시가총액 순 top10 가져오기.
    public List<StockDetailModel> getStockByMileage(Long mileage) {
        return redisCommon.getStockByMileage(mileage);
    }
}
