package com.pda.community_module.saga;

import com.pda.core_module.commands.FinalizePostCommand;
import com.pda.core_module.commands.RollbackPostCommand;
import com.pda.core_module.events.FinishedPostEvent;
import com.pda.core_module.events.RollbackEvent;
import com.pda.community_module.service.PostHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${community.events.topic.finishedPost}", "${community.events.topic.rollback}"}, groupId = "community-group")
public class CommunitySaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final PostHistoryService postHistoryService;
    private final String finalizePostCommandsTopic;
    private final String rollbackPostCommandsTopic;

    public CommunitySaga(KafkaTemplate<String, Object> kafkaTemplate,
                         PostHistoryService postHistoryService,
                         @Value("${community.commands.topic.finalizePost}") String finalizePostCommandsTopic,
                         @Value("${community.commands.topic.rollbackPost}") String rollbackPostCommandsTopic) {
        this.kafkaTemplate = kafkaTemplate;
//        this.postHistoryService = postHistoryService;
        this.finalizePostCommandsTopic = finalizePostCommandsTopic;
        this.rollbackPostCommandsTopic = rollbackPostCommandsTopic;
    }

    @KafkaHandler
    public void handleEvent(@Payload FinishedPostEvent event) {
        // 수신된 FinishedPostEvent에 대해 FinalizePostCommand 발행
        FinalizePostCommand command = FinalizePostCommand.builder()
                .postId(event.getPostId())
                .sentimentScore(50L) // 현재 고정값 (추후 GPT API 연동 가능)
                .build();
        kafkaTemplate.send(finalizePostCommandsTopic, command);
//        postHistoryService.addHistory(event.getPostId(), "FINALIZED");
        System.out.println("FinalizePostCommand sent for postId: " + event.getPostId());
    }

    @KafkaHandler
    public void handleEvent(@Payload RollbackEvent event) {
        // 수신된 RollbackEvent에 대해 RollbackPostCommand 발행
        RollbackPostCommand command = RollbackPostCommand.builder()
                .postId(event.getPostId())
                .reason(event.getReason())
                .build();
        kafkaTemplate.send(rollbackPostCommandsTopic, command);
//        postHistoryService.addHistory(event.getPostId(), "ROLLBACK");
        System.out.println("RollbackPostCommand sent for postId: " + event.getPostId());
    }
}
