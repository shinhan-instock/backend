package com.pda.stock_module.batch.task.StockClosingPrice;

import com.pda.stock_module.web.dto.StockClosingPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@StepScope
public class StockClosingPriceItemReader implements ItemReader<StockClosingPriceDTO> {

    private final StringRedisTemplate redisTemplate;
    private Iterator<String> keyIterator;

    public StockClosingPriceItemReader(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public StockClosingPriceDTO read() throws Exception {
        if (keyIterator == null) {
            Set<String> keys = redisTemplate.keys("stock:*");
            keyIterator = (keys != null) ? keys.iterator() : Collections.emptyIterator();
        }
        if (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Map<Object, Object> hash = redisTemplate.opsForHash().entries(key);
            if (hash.isEmpty()) {
                log.warn("빈 데이터 발견: key={}", key);
                return null;
            }
            String stockCode = (String) hash.get("stockCode");
            String stockName = (String) hash.get("stockName");
            String price = (String) hash.get("price");
            if (stockCode == null || stockName == null || price == null) {
                log.warn("필수 필드 누락: key={} - {}", key, hash);
                return null;
            }
            return new StockClosingPriceDTO(stockCode, stockName, price);
        }
        return null;
    }
}
