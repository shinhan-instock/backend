package com.pda.piggyBank_module.service;

import com.pda.piggyBank_module.domain.Piggy;
import com.pda.piggyBank_module.repository.PiggyRepository;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {

    private final PiggyRepository piggyRepository;

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
}
