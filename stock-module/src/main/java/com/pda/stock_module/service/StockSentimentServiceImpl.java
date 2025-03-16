package com.pda.stock_module.service;

import com.pda.stock_module.domain.StockClosingPrice;
import com.pda.stock_module.domain.StockClosingSentiment;
import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.repository.StockClosingPriceRepository;
import com.pda.stock_module.repository.StockClosingSentimentRepository;
import com.pda.stock_module.repository.StockSentimentRepository;
import com.pda.stock_module.web.dto.ChartResponseDTO;
import com.pda.stock_module.web.dto.StockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockSentimentServiceImpl implements StockSentimentService {

    private final StringRedisTemplate redisTemplate;
    private final StockSentimentRepository stockSentimentRepository;
    private final StockClosingSentimentRepository stockClosingSentimentRepository;
    private final StockClosingPriceRepository stockClosingPriceRepository;

    @Override
    @Transactional
    public void addStockSentiment(List<StockRequest> stockRequestList) {

        stockRequestList.forEach(stockRequest -> {
            String stockCodeKey = "stock:" + stockRequest.getStockName();
            String stockCode = (String) redisTemplate.opsForHash().get(stockCodeKey, "stockCode");
            System.out.println("stockCode = " + stockCode);

            if (stockCode == null) {
                System.out.println("Stock code not found for: " + stockRequest.getStockName());
                return; // stockCode 없으면 처리 안함
            }

            Optional<StockSentiment> existingSentimentOpt = stockSentimentRepository.findByStockCode(stockCode);

            StockSentiment stockSentiment;
            if (existingSentimentOpt.isPresent()) {
                stockSentiment = existingSentimentOpt.get();

                long oldSentimentScore = stockSentiment.getSentimentScore();
                long oldPostCount = stockSentiment.getProcessedPostCount();
                long newPostCount = stockRequest.getPostCount();

                // 새로운 평균 감성 점수 계산
                long newSentimentScore = (oldSentimentScore * oldPostCount + stockRequest.getAvgScore() * newPostCount)
                        / (oldPostCount + newPostCount);

                stockSentiment.setSentimentScore(newSentimentScore);
                stockSentiment.setProcessedPostCount(oldPostCount + newPostCount);

            } else {
                // 기존 데이터 없으면 새로운 객체 생성
                stockSentiment = StockSentiment.builder()
                        .stockCode(stockCode)
                        .stockName(stockRequest.getStockName())
                        .sentimentScore(stockRequest.getAvgScore())
                        .processedPostCount(stockRequest.getPostCount())
                        .build();
            }

            stockSentimentRepository.save(stockSentiment);
        });
    }

    public List<ChartResponseDTO> getClosingSentimentAndClosingStockData(String stockName) {
        LocalDateTime today = LocalDate.now().atStartOfDay(); // 오늘 날짜의 00:00:00
        LocalDateTime twoMonthsAgo = today.minusMonths(2);

        List<StockClosingSentiment> stockClosingSentiments = stockClosingSentimentRepository.findByStockNameAndCreatedAtBetween(stockName, twoMonthsAgo, today);
        List<StockClosingPrice> stockClosingPrices = stockClosingPriceRepository.findByStockNameAndCreatedAtBetween(stockName, twoMonthsAgo, today);

        // stockClosingSentiments 리스트 출력
        System.out.println("StockClosingSentiments 데이터:");
        stockClosingSentiments.forEach(System.out::println);

        // stockClosingPrices 리스트 출력
        System.out.println("StockClosingPrices 데이터:");
        stockClosingPrices.forEach(System.out::println);

        // Map으로 변환 (날짜를 Key로 설정)
        Map<LocalDateTime, Long> sentimentMap = stockClosingSentiments.stream()
                .collect(Collectors.toMap(StockClosingSentiment::getCreatedAt, StockClosingSentiment::getClosingSentimentScore));
        Map<LocalDateTime, Long> stockMap = stockClosingPrices.stream()
                .collect(Collectors.toMap(StockClosingPrice::getCreatedAt, StockClosingPrice::getClosingPrice));

        // 결과 리스트 생성
        List<ChartResponseDTO> chartData = new ArrayList<>();
        for (LocalDateTime date = twoMonthsAgo; !date.isAfter(today); date = date.plusDays(1)) {
            Long sentiment = sentimentMap.getOrDefault(date, null); // 데이터가 없으면 기본값 0
            Long stock = stockMap.getOrDefault(date, null);
            chartData.add(new ChartResponseDTO(date, stock, sentiment));
        }

        return chartData;
    }
}
