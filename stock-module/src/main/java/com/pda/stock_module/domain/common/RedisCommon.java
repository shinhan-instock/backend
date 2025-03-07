package com.pda.stock_module.domain.common;

import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.pda.stock_module.web.model.StockDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;

    public String getValueFromHash(String key, String field) {
        Object result = template.opsForHash().get(key, field);

        if (result != null) {

            return result.toString();
        }
        return null;
    }



    public <T> T getEntriesFromHash(String key, Class<T> clazz) {

        Map<Object, Object> entries = template.opsForHash().entries("stock:" + key);
        if (entries != null) {
            String jsonValue = gson.toJson(entries);
            return gson.fromJson(jsonValue, clazz);
        }
        return null;
    }


    public <T> List<T> getAllList(String key, Class<T> clazz) {
        List<String> jsonValues = template.opsForList().range(key, 0, -1);
        List<T> resultSet = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T value = gson.fromJson(jsonValue, clazz);
                resultSet.add(value);
            }
        }
        return resultSet;
    }

    public void syncAllStocksToZSet() {
        template.delete("stocks:rank");

        Set<String> stockKeys = template.keys("stock:*"); // 모든 주식 키 가져오기

        if (stockKeys == null || stockKeys.isEmpty()) {
            log.info("⚠️ Redis에 저장된 주식 데이터가 없습니다.");
            return;
        }

        ZSetOperations<String, String> zSetOperations = template.opsForZSet();

        for (String stockKey : stockKeys) {
            Map<Object, Object> stockData = template.opsForHash().entries(stockKey);
            if (stockData.isEmpty()) continue;

            String stockName = stockData.get("stockName").toString();
            String stockCode = stockData.get("stockCode").toString();
            Integer price = Integer.parseInt(stockData.get("price").toString());
            String priceChange = stockData.get("priceChange").toString();
            String sectorName = stockData.get("sectorName").toString();
            String rank = stockData.get("rank") != null ? stockData.get("rank").toString() : "2000"; // 시가총액 순위

            // JSON 변환을 위해 객체 매핑
            StockDetailModel stock = new StockDetailModel(
                    stockName, stockCode, price, priceChange, sectorName, rank
            );

            // JSON 변환 후 ZSET에 추가 (marketCapRank를 score로 사용)
            String stockJson = gson.toJson(stock);
            String STOCK_ZSET_KEY = "stocks:rank";
            zSetOperations.add(STOCK_ZSET_KEY, stockJson, Double.parseDouble(stock.getRank()));
        }

        log.info("모든 주식 데이터를 Redis ZSET(시가총액 기준)에 동기화.");
    }

    // 보유 마일리지 이하의 주식 중 시가총액 순으로 Top 10 가져오기
    public List<StockDetailModel> getStockByMileage(Long mileage) {
        ZSetOperations<String, String> zSetOperations = template.opsForZSet();

        // Redis에서 marketCapRank 기준으로 정렬된 데이터 가져오기
        Set<String> stockJsonSet = zSetOperations.range("stocks:rank", 0, -1);

        if (stockJsonSet == null || stockJsonSet.isEmpty()) {
            return List.of();
        }

        // JSON을 MileageStock 객체로 변환 후 price ≤ mileage 조건 적용
        return stockJsonSet.stream()
                .map(json -> gson.fromJson(json, StockDetailModel.class))
                .filter(stock -> stock.getPrice() <= mileage) // 보유 마일리지 이하 필터링
                .sorted((s1, s2) -> Integer.compare(Integer.parseInt(s1.getRank()), Integer.parseInt(s2.getRank()))) // 시가총액 순 정렬
                .limit(10) // Top 10 가져오기
                .collect(Collectors.toList());
    }

    public void syncAllStocksToZSetWithScore() {
        String STOCK_ZSET_KEY = "stocks:autoComplete";
        template.delete(STOCK_ZSET_KEY); // 기존 데이터 초기화

        Set<String> stockKeys = template.keys("stock:*"); // 모든 주식 키 가져오기
        if (stockKeys == null || stockKeys.isEmpty()) {
            log.info("⚠️ Redis에 저장된 주식 데이터가 없습니다.");
            return;
        }

        ZSetOperations<String, String> zSetOperations = template.opsForZSet();

        for (String stockKey : stockKeys) {
            String stockName = stockKey.replace("stock:", ""); // "stock:삼성전자" -> "삼성전자"
            zSetOperations.add(STOCK_ZSET_KEY, stockName, 0); // 동일한 score 설정하여 사전순 정렬
        }

        log.info("✅ 모든 주식 데이터를 Redis ZSET에 동기화 완료.");
    }

    // 🔹 자동완성 검색 및 TTL 기반 캐시 조회
    public List<String> searchStocks(String stockName) {
        String STOCK_ZSET_KEY = "stocks:autoComplete";

        if (stockName == null || stockName.isEmpty()) {
            return Collections.emptyList();
        }

        ZSetOperations<String, String> zSetOperations = template.opsForZSet();

        Set<String> allStocks = zSetOperations.rangeByScore(STOCK_ZSET_KEY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        if (allStocks == null || allStocks.isEmpty()) {
            return Collections.emptyList();
        }

        return allStocks.stream()
                .filter(stock -> stock.startsWith(stockName))  // 검색어 기준 필터링
                .sorted()
                .limit(20)
                .collect(Collectors.toList());

    }




}
