package com.pda.stock_module.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {

    private final Map<Long, List<Long>> userPortfolioPreferences = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Double> portfolioRevenueCache = new ConcurrentHashMap<>();
    private final Map<Long, String> rankingPriceChangeRateCache = new ConcurrentHashMap<>(); // priceChangeRate 캐시

    public SseEmitter save(String id, SseEmitter emitter) {
        emitters.put(id, emitter);
        return emitter;
    }

    public void deleteById(String id) {
        emitters.remove(id);
    }

    public Map<String, SseEmitter> findAllStartWithById(String id) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    // priceChangeRate 캐시 값 가져오기
    public String getCachedPriceChangeRate(Long rankingId) {
        return rankingPriceChangeRateCache.getOrDefault(rankingId, "0.0");
    }

    // priceChangeRate 캐시 값 업데이트
    public void updateCachedPriceChangeRate(Long rankingId, String newPriceChangeRate) {
        rankingPriceChangeRateCache.put(rankingId, newPriceChangeRate);
    }
}