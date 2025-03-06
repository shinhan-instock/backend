package com.pda.piggyBank_module.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MileageResponseDto {
    private Long userId;
    private int mileage;

    public MileageResponseDto(Long userId, int mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }
}
