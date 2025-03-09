package com.pda.piggyBank_module.service;

import com.pda.piggyBank_module.web.dto.MileageRequest;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface MileageService {
    @Transactional
    public void addMileage(MileageRequest request);
    public MileageResponseDto getMileageByUserId(String userId);

    MileageResponseDto updateMileageByUserId(String userId, Integer updatedMileage);
}
