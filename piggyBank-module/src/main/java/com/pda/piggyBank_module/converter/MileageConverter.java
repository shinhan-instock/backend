package com.pda.piggyBank_module.converter;

import com.pda.piggyBank_module.domain.common.Piggy;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class MileageConverter {

    public MileageResponseDto toDTO(Piggy piggy) {
        return new MileageResponseDto(piggy.getUserId(), piggy.getMileage());
    }
}
