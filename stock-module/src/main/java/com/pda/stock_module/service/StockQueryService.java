package com.pda.stock_module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.stock_module.domain.Company;
import com.pda.stock_module.domain.StockSentiment;
import com.pda.stock_module.domain.client.CommunityClient;
import com.pda.stock_module.domain.client.MileageClient;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.repository.StockQueryRepository;
import com.pda.stock_module.repository.StockSentimentRepository;
import com.pda.stock_module.web.dto.DetailStockResponse;
import com.pda.stock_module.web.dto.MileageResponseDTO;
import com.pda.stock_module.web.dto.StockSentimentResponse;
import com.pda.stock_module.web.dto.TopStockResponse;
import com.pda.stock_module.web.model.ListModel;
import com.pda.stock_module.web.model.StockDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockQueryService {
    private final RedisCommon redisCommon;
    private final StringRedisTemplate redisTemplate;
    private final CommunityClient communityClient;
    private final StockQueryRepository stockQueryRepository;
    private final MileageClient mileageClient;
    private final ObjectMapper objectMapper;
    private final StockSentimentRepository stockSentimentRepository;


//    public List<TopStockResponse> getTop10ByTheme(String stockName) {
//        try {
//            // Redis에서 theme 가져오기
//            String theme = redisCommon.getValueFromHash("stock:" + stockName, "sectorName");
//            if (theme == null || theme.isEmpty()) {
//                throw new IllegalArgumentException("Sector name not found for stock: " + stockName);
//            }
//
//            // Redis에서 stockLists 가져오기
//            List<ListModel> stockLists = redisCommon.getAllList("sector:" + theme, ListModel.class);
//            if (stockLists == null || stockLists.isEmpty()) {
//                throw new IllegalStateException("No stock list found for sector: " + theme);
//            }
//
//            // ListModel -> TopStockResponse로 변환
//            List<TopStockResponse> topStockResponses = stockLists.stream()
//                    .map(listModel -> new TopStockResponse(
//                            listModel.getStockName(),
//                            listModel.getPrice(),
//                            listModel.getPriceChange()
//                    ))
//                    .collect(Collectors.toList());
//
//            return topStockResponses;
//
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            // 적절한 예외 메시지 로그 출력
//            log.error("Error while fetching stock data: {}", e.getMessage());
//            throw e; // 예외 재발생
//        } catch (Exception e) {
//            // 예상치 못한 예외 처리
//            log.error("Unexpected error occurred: {}", e.getMessage(), e);
//            throw new RuntimeException("An unexpected error occurred while fetching stock data.", e);
//        }
//    }

    public SseEmitter streamTop10ByTheme(String stockName) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        emitter.onCompletion(() -> {
            log.info("✅ SSE 연결 종료: " + stockName);
            scheduler.shutdown(); // 스케줄러 종료
        });

        emitter.onTimeout(() -> {
            log.info("⚠️ SSE 연결 타임아웃: " + stockName);
            scheduler.shutdown(); // 스케줄러 종료
        });

        emitter.onError((e) -> {
            log.error("❌ SSE 연결 오류: " + stockName + " - " + e.getMessage());
            scheduler.shutdown(); // 스케줄러 종료
        });

        // 5초마다 데이터 전송 (스케줄러 실행)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<TopStockResponse> stockData = fetchTopStocks(stockName);

                String jsonResponse = objectMapper.writeValueAsString(stockData);
                emitter.send(SseEmitter.event().data(jsonResponse));

            } catch (IOException e) {
                log.error("❌ SSE 전송 오류: {}", e.getMessage());
                scheduler.shutdown(); // 스케줄러 종료
            } catch (Exception e) {
                log.error("🚨 데이터 조회 오류: {}", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }

    // Redis에서 최신 Top5 주식 데이터 가져오기
    private List<TopStockResponse> fetchTopStocks(String stockName) {
        String theme = redisCommon.getValueFromHash("stock:" + stockName, "sectorName");
        if (theme == null || theme.isEmpty()) {
            throw new IllegalArgumentException("Sector name not found for stock: " + stockName);
        }

        List<ListModel> stockLists = redisCommon.getAllList("sector:" + theme, ListModel.class);
        if (stockLists == null || stockLists.isEmpty()) {
            throw new IllegalStateException("No stock list found for sector: " + theme);
        }

        return stockLists.stream()
                .map(listModel -> new TopStockResponse(
                        listModel.getStockName(),
                        listModel.getPrice(),
                        listModel.getPriceChange()
                ))
                .collect(Collectors.toList());
    }

    // 주식 상세 정보 조회
//    public DetailStockResponse getStockDetail(String userId, String stockName) {
//        try {
//            StockDetailModel stockInfo = redisCommon.getEntriesFromHash(stockName, StockDetailModel.class);
//
//            if (stockInfo.getStockCode() == null) {
//                throw new GeneralException(ErrorStatus.STOCK_NOT_FOUND); // ✅ 서비스에서 예외 발생
//            }
//
//            String key = "stocks:popular";
//
//            Company company = stockQueryRepository.findByStockName(stockName);
//            Double score = redisTemplate.opsForZSet().score(key, stockName);
//
//            if (score != null) {
//                redisTemplate.opsForZSet().incrementScore(key, stockName, 1); // stockName의 score +1
//            }
//            boolean watchListAdded;
//            if(userId==null)watchListAdded=false;
//            else watchListAdded = communityClient.isStockInWatchList(userId, stockInfo.getStockCode());
//
//            return new DetailStockResponse(
//                    stockInfo.getStockName(),
//                    stockInfo.getStockCode(),
//                    stockInfo.getPrice(),
//                    stockInfo.getPriceChange(),
//                    company.getDescription(),
//                    watchListAdded
//
//            );
//
//
//        } catch (Exception e) {
//            System.err.println("Error while fetching stock details: " + e.getMessage());
//            throw e;
//        }
//    }

    public SseEmitter streamStockDetail(String userId, String stockName) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        StockDetailModel stockInfo = redisCommon.getEntriesFromHash(stockName, StockDetailModel.class);

        if (stockInfo.getStockCode() == null) {
            throw new GeneralException(ErrorStatus.STOCK_NOT_FOUND);
        }

        String stockCode = stockInfo.getStockCode(); // 🔥 고정
        String companyDescription;

        Company company = stockQueryRepository.findByStockName(stockName);
        companyDescription = (company != null) ? company.getDescription() : "정보 없음";

        boolean watchListAdded = (userId != null) && communityClient.isStockInWatchList(userId, stockCode);

        // SSE 연결 종료 시 안전하게 정리
        emitter.onCompletion(() -> {
            log.info("✅ SSE 연결 종료: " + stockName);
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            log.info("⚠️ SSE 연결 타임아웃: " + stockName);
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            log.error("❌ SSE 연결 오류: " + stockName + " - " + e.getMessage());
            scheduler.shutdown();
        });

        // 5초마다 가격과 변동률만 업데이트 (스케줄러 실행)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                StockDetailModel updatedStockInfo = redisCommon.getEntriesFromHash(stockName, StockDetailModel.class);

                if (updatedStockInfo.getStockCode() == null) {
                    throw new GeneralException(ErrorStatus.STOCK_NOT_FOUND);
                }

                String key = "stocks:popular";
                Double score = redisTemplate.opsForZSet().score(key, stockName);

                if (score != null) {
                    redisTemplate.opsForZSet().incrementScore(key, stockName, 1);
                }

                Optional<StockSentiment> stock = stockSentimentRepository.findByStockCode(stockCode);

                Long sentimentScore = null;
                if (stock.isPresent()) {
                    sentimentScore = stock.get().getSentimentScore();
                }


                String jsonResponse = objectMapper.writeValueAsString(new StockSentimentResponse(
                        stockName,
                        stockCode,
                        updatedStockInfo.getPrice(),
                        updatedStockInfo.getPriceChange(),
                        companyDescription,
                        watchListAdded,
                        sentimentScore
                ));
                emitter.send(SseEmitter.event().data(jsonResponse));

            } catch (IOException e) {
                log.error("❌ SSE 전송 오류: {}", e.getMessage());
                scheduler.shutdown();
            } catch (Exception e) {
                log.error("🚨 데이터 조회 오류: {}", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }

    // 보유 마일리지에 해당하는 주식 시가총액 순 top10 가져오기.
//    public List<StockDetailModel> getStockByMileage(Long mileage) {
//        return redisCommon.getStockByMileage(mileage);
//    }

    // SSE 스트리밍 (5초마다 마일리지 기반 주식 Top10 전송)
    public SseEmitter streamStockByMileage(String authorizationHeader) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        MileageResponseDTO res = mileageClient.getMileage(authorizationHeader);
        Long mileage = res.getMileage();

        // SSE 연결 종료 시 안전하게 정리
        emitter.onCompletion(() -> {
            log.info("✅ SSE 연결 종료 (마일리지 기반 주식 Top10)");
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            log.info("⚠️ SSE 연결 타임아웃 (마일리지 기반 주식 Top10)");
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            log.error("❌ SSE 연결 오류 (마일리지 기반 주식 Top10) - " + e.getMessage());
            scheduler.shutdown();
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<StockDetailModel> stockList = redisCommon.getStockByMileage(mileage);

                // JSON 변환 후 SSE 전송
                String jsonResponse = objectMapper.writeValueAsString(stockList);
                emitter.send(SseEmitter.event().data(jsonResponse));

            } catch (IOException e) {
                log.error("❌ SSE 전송 오류: {}", e.getMessage());
                scheduler.shutdown();
            } catch (Exception e) {
                log.error("🚨 데이터 조회 오류: {}", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }

}
