package com.pda.community_module.batch.task.likeTop10;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeTop10Writer implements ItemWriter<MileageRequest> {

    private final MileageFeignClient mileageFeignClient;

    @Override
    public void write(Chunk<? extends MileageRequest> items) {
        for (MileageRequest request : items) {
            mileageFeignClient.addMileage(request); // ✅ Chunk<T>를 순회하면서 처리
        }
    }
}
