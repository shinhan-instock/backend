package com.pda.community_module.batch.task.likeTop10;

import com.pda.community_module.domain.Post;
import com.pda.community_module.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class LikeTop10Reader implements ItemReader<List<Post>> {

    private final PostRepository postRepository;
    private boolean isRead = false;

    @Value("#{jobParameters['targetDate'] ?: T(java.time.LocalDate).now().toString()}")
    private String targetDate;

    @Override
    public List<Post> read() {
        if (isRead) {
            return null;
        }
        isRead = true;

        // targetDate가 비어 있거나 null이면 예외 발생
        if (targetDate == null || targetDate.isBlank()) {
            throw new IllegalArgumentException("targetDate 값이 존재하지 않습니다.");
        }

        LocalDateTime startTime = LocalDateTime.parse(targetDate + "T00:00:00");
        LocalDateTime endTime = startTime.plusDays(1);

        return postRepository.findTop10LikedPosts(startTime, endTime);
    }

}
