package com.pda.stock_module.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.google.gson.Gson;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FetchStockThemeService {
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final Gson gson = new Gson(); // Gson 객체 생성

    @Transactional
    public void updateStockThemeData() {
        String[] markets = {"KOSPI", "KOSDAQ"}; // 시장 배열

        for (String market : markets) {
            String apiUrl = "https://finance.daum.net/api/quotes/sectors?fieldName=&order=&perPage=&market="
                    + market
                    + "&page=&changes=UPPER_LIMIT,RISE,EVEN,FALL,LOWER_LIMIT";

            try {
                // 요청 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
                headers.set("Referer", "https://finance.daum.net/domestic/all_stocks");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // RestTemplate 요청
                ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    List<Map<String, Object>> sectors = (List<Map<String, Object>>) body.get("data");


                    for (Map<String, Object> sector : sectors) {

                        String sectorName = (String) sector.get("sectorName"); // 업종명.
                        List<Map<String, Object>> includedStocks = (List<Map<String, Object>>) sector.get("includedStocks");

                        int size = includedStocks.size();
                        System.out.println("size = " + size);
                        List<Map<String, Object>> maxTop5 = new ArrayList<>(includedStocks.subList(0, Math.min(6, size)));
                        List<Map<String, Object>> minTop5 = new ArrayList<>(includedStocks.subList(Math.max(size - 5, 0), size));

                        List<Map<String, Object>> sortedIncludedStocks = new ArrayList<>();
                        sortedIncludedStocks.addAll(maxTop5);
                        sortedIncludedStocks.addAll(minTop5);
                        for (Map<String, Object> stockData : sortedIncludedStocks) {
                            Map<String, Object> themeStocks = new HashMap<>();

                            String stockName = (String) stockData.get("name");
                            themeStocks.put("stockName", stockName);
                            Long price = stockData.get("tradePrice") != null
                                    ? Math.round(Double.valueOf(stockData.get("tradePrice").toString()))
                                    : 0L;
                            themeStocks.put("price", price);

                            // changeRate 추출 및 변환
                            String priceChange = null;
                            Object changeRateObj = stockData.get("changeRate");
                            if (changeRateObj instanceof Double) {
                                priceChange = String.format("%.2f", (Double) changeRateObj * 100); // 소수점 둘째 자리까지 변환
                            } else if (changeRateObj instanceof String) {
                                priceChange = String.format("%.2f", Double.valueOf((String) changeRateObj) * 100);
                            }
                            themeStocks.put("priceChange", priceChange);


                            if (stockName != null) {

                                String redisKey = "sector:" + sectorName;
                                String result = gson.toJson(themeStocks);
                                redisTemplate.opsForList().leftPush(redisKey, result);

                                // 레디스 데이터 제한
                                redisTemplate.opsForList().trim(redisKey,0,9);

                                // Redis 데이터에 TTL(Time-To-Live) 설정 (예: 1일)
                                redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
                            }
                        }
                    }
                }
            } catch (HttpServerErrorException e) {
                if (e.getMessage().contains("초당 거래건수를 초과")) {
                    System.err.println("API 요청 제한 초과: " + e.getMessage());
                    try {
                        Thread.sleep(1000 * 60); // 1분 대기
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.err.println("API 호출 중 예외 발생: " + e.getMessage());
                }
            } catch (Exception e) {
                System.err.println("주식 데이터를 가져오는 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
