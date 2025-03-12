package com.pda.stock_module.service.handler;

import com.pda.core_module.events.CheckStockEvent;
import com.pda.stock_module.service.StockEventService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${community.commands.topic.checkStock}", groupId = "stock-group")
public class StockCommandsHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StockEventService stockEventService;

    public StockCommandsHandler(RedisTemplate<String, Object> redisTemplate,StockEventService stockEventService) {
        this.redisTemplate = redisTemplate;
        this.stockEventService=stockEventService;
    }

    @KafkaHandler
    public void handleEvent(@Payload CheckStockEvent event) {
        System.out.println("community에서 검증 시도 message consume");
        String redisKey = "stock:" + event.getHashtag();
        Boolean exists = redisTemplate.hasKey(redisKey);
        if (exists != null && exists) {
            stockEventService.processSuccessfulStockEvent(event);
        } else {
            stockEventService.processRollbackEvent(event);
        }
    }
}
