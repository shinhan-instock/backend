package com.pda.stock_module.service;



import com.pda.stock_module.domain.Ranking;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.repository.EmitterRepository;
import com.pda.stock_module.repository.RankingRepository;
import com.pda.stock_module.web.dto.StockPopularRankResponse;
import com.pda.stock_module.web.dto.StockRankResponse;
import com.pda.stock_module.web.model.StockDetailModel;
import com.pda.stock_module.web.model.StockPopularModel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RankingListService {
    private final RankingRepository rankingRepository;
    private final EmitterRepository emitterRepository;
    private final RedisCommon redisCommon;
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

    public List<StockPopularRankResponse> getTop10ByPopularity() {
        return redisCommon.getStockByPopularity()
                .stream()
                .map(this::convertToStockResponseByModel)
                .collect(Collectors.toList());

    }
}