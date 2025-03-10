package com.pda.stock_module.service;

import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.repository.StockSentimentRepository;
import com.pda.stock_module.web.dto.StockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockSentimentServiceImpl implements StockSentimentService {

    private final StringRedisTemplate redisTemplate;
    private final StockSentimentRepository stockSentimentRepository;

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
}
