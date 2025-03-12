package com.pda.stock_module.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MileageResponseDTO {
    private String userId;
    private Long mileage;

    public MileageResponseDTO(String userId, Long mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }
}
