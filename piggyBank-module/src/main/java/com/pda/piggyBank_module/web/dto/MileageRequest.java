package com.pda.piggyBank_module.web.dto;

import lombok.*;

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
