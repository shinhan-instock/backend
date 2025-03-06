package com.pda.community_module.batch.task.likeTop10;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MileageRequest {
    private List<MileageUser> users;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MileageUser {
        private Long userId;
        private int mileage;
    }
}

