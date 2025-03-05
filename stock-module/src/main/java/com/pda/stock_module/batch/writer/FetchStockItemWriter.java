//package com.pda.stock_module.batch.writer;
//
//import com.pda.stock_module.web.dto.BatchStockDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@RequiredArgsConstructor
//public class FetchStockItemWriter {
//
//    private final StringRedisTemplate redisTemplate;
//    @Bean
//    public ItemWriter<BatchStockDTO> stockItemWriter() {
//        return stockList -> {  // ✅ `stockList`는 이미 `List<BatchStockDTO>` 형태로 100개씩 들어옴
//
//            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//                for (BatchStockDTO stock : stockList) {  // ✅ 개별 객체 하나씩 처리
//                    String redisKey = "stock:" + stock.getStockName();
//                    connection.hSet(redisKey.getBytes(), "stockName".getBytes(), stock.getStockName().getBytes());
//                    connection.hSet(redisKey.getBytes(), "stockCode".getBytes(), stock.getStockCode().getBytes());
//                    connection.hSet(redisKey.getBytes(), "price".getBytes(), String.valueOf(stock.getPrice()).getBytes());
//                    connection.hSet(redisKey.getBytes(), "priceChange".getBytes(), stock.getPriceChange().getBytes());
//                    connection.expire(redisKey.getBytes(), TimeUnit.DAYS.toSeconds(1));
//                }
//                return null;
//            });
//        };
//
//    }
//
//    //마지막 그룹의 크기가 size보다 작을 수도 있음을 고려
//    private <T> List<List<T>> partitionList(List<T> list, int size) {
//        List<List<T>> partitionedList = new ArrayList<>();
//        for (int i = 0; i < list.size(); i += size) {
//            partitionedList.add(list.subList(i, Math.min(i + size, list.size())));
//        }
//        return partitionedList;
//    }
//}
