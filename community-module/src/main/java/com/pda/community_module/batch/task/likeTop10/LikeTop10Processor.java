package com.pda.community_module.batch.task.likeTop10;

import com.pda.community_module.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LikeTop10Processor implements ItemProcessor<List<Post>, MileageRequest> {

    private static final int MILEAGE_POINTS = 30;

    @Override
    public MileageRequest process(List<Post> posts) {
        List<MileageRequest.MileageUser> users = posts.stream()
                .map(post -> new MileageRequest.MileageUser(post.getUser().getId(), MILEAGE_POINTS))
                .collect(Collectors.toList());

        return new MileageRequest(users);
    }
}

