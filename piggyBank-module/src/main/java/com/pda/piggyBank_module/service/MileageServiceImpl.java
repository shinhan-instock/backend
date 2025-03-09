package com.pda.piggyBank_module.service;

import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.piggyBank_module.config.UserFeignClient;
import com.pda.piggyBank_module.converter.MileageConverter;
import com.pda.piggyBank_module.domain.Piggy;
import com.pda.piggyBank_module.repository.PiggyRepository;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import com.pda.piggyBank_module.web.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {

    private final PiggyRepository piggyRepository;
    private final MileageConverter mileageConverter;
    private final UserFeignClient userFeignClient;


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
        // OpenFeign을 통해 로그인용 userId(문자열)로 실제 사용자 엔티티 정보를 조회합니다.
        UserResponseDTO.UserRealPKResponseDto userResponse = userFeignClient.getUserByUserId(userId);
        Long actualUserId = userResponse.getId();


        Optional<Piggy> mileage = piggyRepository.findMileageByUserId(actualUserId);
        return mileage.map(mileageConverter::toDTO)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
    }

    @Override
    public MileageResponseDto updateMileageByUserId(String userId, Integer updatedMileage) {
        // OpenFeign을 통해 로그인용 userId(문자열)로 실제 사용자 엔티티 정보를 조회합니다.
        UserResponseDTO.UserRealPKResponseDto userResponse = userFeignClient.getUserByUserId(userId);
        Long actualUserId = userResponse.getId();


        piggyRepository.updateMileageByUserId(actualUserId, updatedMileage);

        Piggy updatedMileageEntity = piggyRepository.findMileageByUserId(actualUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        return new MileageResponseDto(actualUserId, updatedMileageEntity.getMileage());

    }
}
