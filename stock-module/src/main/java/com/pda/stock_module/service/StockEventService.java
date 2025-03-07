package com.pda.stock_module.service;

import com.pda.core_module.events.CheckStockEvent;
import com.pda.core_module.events.FinishedPostEvent;
import com.pda.core_module.events.RollbackEvent;
import com.pda.core_module.events.SetMileageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockEventService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${stock.events.topic.finishedPost}")
    private String finishedPostTopic;
    @Value("${stock.events.topic.rollback}")
    private String rollbackTopic;
    @Value("${stock.commands.topic.setMileage}")
    private String setMileageTopic;


    @Transactional("kafkaTransactionManager")
    public void processSuccessfulStockEvent(CheckStockEvent event) {
        FinishedPostEvent finishedEvent = FinishedPostEvent.builder()
                .postId(event.getPostId())
                .correlationId(event.getCorrelationId())
                .build();
        kafkaTemplate.send(finishedPostTopic, finishedEvent);
        System.out.println(finishedEvent.getPostId()+"  : stock -> community 으로 감정 검증 완료 publish");
        SetMileageEvent setMileageEvent = SetMileageEvent.builder()
                .postId(event.getPostId())
                .correlationId(event.getCorrelationId())
                .userId(event.getUserId())
                .mileageAmount(10)
                .build();
        kafkaTemplate.send(setMileageTopic, setMileageEvent);
        System.out.println(finishedEvent.getPostId()+"  : stock -> piggyBank 으로 감정 검증 완료 publish");
    }

    @Transactional("kafkaTransactionManager")
    public void processRollbackEvent(CheckStockEvent event) {
        RollbackEvent rollbackEvent = RollbackEvent.builder()
                .postId(event.getPostId())
                .correlationId(event.getCorrelationId())
                .reason("Stock not found for hashtag: " + event.getHashtag())
                .build();
        System.out.println(event.getPostId()+"  : !!!!stock -> community 으로 감정 검증 싪패 publish");
        kafkaTemplate.send(rollbackTopic, rollbackEvent);
    }
}
