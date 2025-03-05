package com.pda.piggyBank_module.service;

import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.piggyBank_module.converter.MileageConverter;
import com.pda.piggyBank_module.domain.common.Piggy;
import com.pda.piggyBank_module.repository.MileageRepository;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MileageService {
    private final MileageRepository mileageRepository;
    private final MileageConverter mileageConverter;

    public MileageResponseDto getMileageByUserId(String userId) {
        Optional<Piggy> mileage = mileageRepository.findMileageByUserId(userId);
        return mileage.map(mileageConverter::toDTO)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND)); // 값이 없으면 null 반환

    }
}
