package com.pda.stock_module.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.stock_module.domain.Ranking;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.repository.EmitterRepository;
import com.pda.stock_module.repository.RankingRepository;
import com.pda.stock_module.web.dto.StockPopularRankResponse;
import com.pda.stock_module.web.dto.StockRankResponse;
import com.pda.stock_module.web.model.StockDetailModel;
import com.pda.stock_module.web.model.StockPopularModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class RankingListService {
    private final RankingRepository rankingRepository;
    private final EmitterRepository emitterRepository;
    private final RedisCommon redisCommon;
    private final ObjectMapper objectMapper;

    // SSE 연결 설정
    public SseEmitter subscribeToStockUpdates() {
        String id = "stock_updates_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(60L * 1000 * 60)); // 60분 연결 유지

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }

    // 5초마다 가격 변화를 감지하고 알림 전송
    @Scheduled(fixedRate = 5000)
    public void monitorPriceChanges() {
        List<Ranking> rankings = rankingRepository.findAll();
        for (Ranking ranking : rankings) {
            String currentPriceChangeRate = ranking.getPriceChangeRate();
            String cachedPriceChangeRate = emitterRepository.getCachedPriceChangeRate(ranking.getId());

            // 가격 변화 감지
            if (!currentPriceChangeRate.equals(cachedPriceChangeRate)) {
                StockRankResponse response = convertToStockResponse(ranking);
                sendPriceUpdate(response);

                emitterRepository.updateCachedPriceChangeRate(ranking.getId(), currentPriceChangeRate);
            }
        }
    }

    // 실시간 업데이트 전송
    private void sendPriceUpdate(StockRankResponse response) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById("stock_updates");
        sseEmitters.forEach((key, emitter) -> {
            try {
                emitter.send(SseEmitter.event().id(key).name("price-update").data(response));
            } catch (IOException e) {
                emitterRepository.deleteById(key);
            }
        });
    }

    private StockRankResponse convertToStockResponse(Ranking ranking) {
        return new StockRankResponse(
                ranking.getStockCode(),
                ranking.getStockName(),
                ranking.getCurrentPrice() != null ? ranking.getCurrentPrice() : 0L,
                ranking.getPriceChangeRate()
        );
    }

    private StockPopularRankResponse convertToStockResponseByModel(StockPopularModel stockPopularModel) {
        return new StockPopularRankResponse(
                stockPopularModel.getStockCode(),
                stockPopularModel.getStockName(),
                stockPopularModel.getPrice() != null ? stockPopularModel.getPrice() : 0L,
                stockPopularModel.getPriceChange(),
                stockPopularModel.getScore()
        );
    }

    public List<StockRankResponse> getTop20ByFluctuation() {
        return rankingRepository.findTop20ByFluctuationRank()
                .stream()
                .limit(20)
                .map(this::convertToStockResponse)
                .collect(Collectors.toList());
    }



    // SSE 스트리밍 (5초마다 인기 종목 TOP 10 전송)
    public SseEmitter streamTop10ByPopularity() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // SSE 연결 종료 시 안전하게 정리
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료 (인기 종목 TOP 10)");
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃 (인기 종목 TOP 10)");
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            log.error("SSE 연결 오류 (인기 종목 TOP 10) - " + e.getMessage());
            scheduler.shutdown();
        });

        // 5초마다 인기 종목 TOP 10 갱신 (스케줄러 실행)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<StockPopularRankResponse> stockList = redisCommon.getStockByPopularity()
                        .stream()
                        .map(this::convertToStockResponseByModel)
                        .collect(Collectors.toList());

                // JSON 변환 후 SSE 전송
                String jsonResponse = objectMapper.writeValueAsString(stockList);
                emitter.send(SseEmitter.event().data(jsonResponse));

            } catch (IOException e) {
                log.error("SSE 전송 오류: {}", e.getMessage());
                scheduler.shutdown();
            } catch (Exception e) {
                log.error("데이터 조회 오류: {}", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }
}