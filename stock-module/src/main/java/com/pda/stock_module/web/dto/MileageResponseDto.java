package com.pda.stock_module.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MileageResponseDto {
    private String userId;
    private Long mileage;

    public MileageResponseDto(String userId, Long mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }
}
