//package com.pda.stock_module.batch.reader;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
//public class FetchStockItemReader {
//    private final RestTemplate restTemplate;
//
//    @Bean
//    public ItemReader<Map<String, Object>> stockItemReader() {
//        return new ItemReader<>() {
//            private final String[] markets = {"KOSPI", "KOSDAQ"};
//            private int marketIndex = 0;
//            private Iterator<Map<String, Object>> stockIterator = Collections.emptyIterator();
//
//            @Override
//            public Map<String, Object> read() {
//                if (!stockIterator.hasNext()) {
//                    if (marketIndex >= markets.length) {
//                        return null;  // 모든 데이터 처리 완료
//                    }
//
//                    String market = markets[marketIndex++];
//                    String apiUrl = "https://finance.daum.net/api/quotes/sectors?market=" + market;
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.set("User-Agent", "Mozilla/5.0");
//                    headers.set("Referer", "https://finance.daum.net/domestic/all_stocks");
//
//                    HttpEntity<String> entity = new HttpEntity<>(headers);
//                    ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
//
//                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                        List<Map<String, Object>> sectors = (List<Map<String, Object>>) response.getBody().get("data");
//                        List<Map<String, Object>> stockList = new ArrayList<>();
//                        for (Map<String, Object> sector : sectors) {
//                            List<Map<String, Object>> includedStocks = (List<Map<String, Object>>) sector.get("includedStocks");
//                            stockList.addAll(includedStocks);
//                        }
//                        stockIterator = stockList.iterator();
//                    }
//                }
//
//                return stockIterator.hasNext() ? stockIterator.next() : null;
//            }
//        };
//    }
//}
