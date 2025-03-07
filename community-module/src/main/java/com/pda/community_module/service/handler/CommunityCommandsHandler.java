package com.pda.community_module.service.handler;

import com.pda.core_module.commands.FinalizePostCommand;
import com.pda.core_module.commands.RollbackPostCommand;
import com.pda.community_module.service.PostService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${community.commands.topic.finalizePost}", "${community.commands.topic.rollbackPost}"}, groupId = "community-command-group")
public class CommunityCommandsHandler {

    private final PostService postService;

    public CommunityCommandsHandler(PostService postService) {
        this.postService = postService;
    }

    @KafkaHandler
    public void handleCommand(@Payload FinalizePostCommand command) {

        System.out.println("글 검증 완료");
        postService.finalizePost(command.getPostId(), command.getSentimentScore());
    }

    @KafkaHandler
    public void handleCommand(@Payload RollbackPostCommand command) {
        System.out.println("글 검증 실패");
        postService.rollbackPost(command.getPostId(), command.getReason());
    }
}
