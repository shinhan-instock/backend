package com.pda.piggyBank_module.service;

import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.piggyBank_module.converter.MileageConverter;
import com.pda.piggyBank_module.domain.Piggy;
import com.pda.piggyBank_module.repository.MileageRepository;
import com.pda.piggyBank_module.repository.PiggyRepository;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {

    private final PiggyRepository piggyRepository;
    private final MileageRepository mileageRepository;
    private final MileageConverter mileageConverter;


    @Override
    @Transactional
    public void addMileage(MileageRequest request) {
        request.getUsers().forEach(user -> {
            Piggy piggy = piggyRepository.findByUserId(user.getUserId())
//                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND))
                    .orElseGet(() -> new Piggy(user.getUserId(), 0)); // 임시용
            piggy.addMileage(user.getMileage()); // 마일리지 추가
            piggyRepository.save(piggy);
        });
    }
    @Override
    public MileageResponseDto getMileageByUserId(String userId) {
        Optional<Piggy> mileage = mileageRepository.findMileageByUserId(userId);
        return mileage.map(mileageConverter::toDTO)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND)); // 값이 없으면 null 반환


    }
}
