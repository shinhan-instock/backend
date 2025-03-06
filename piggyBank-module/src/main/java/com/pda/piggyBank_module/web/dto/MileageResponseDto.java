package com.pda.piggyBank_module.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MileageResponseDto {
    private Long userId;
    private Long mileage;

    public MileageResponseDto(Long userId, Long mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }
}
