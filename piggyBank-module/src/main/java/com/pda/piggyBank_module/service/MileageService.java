package com.pda.piggyBank_module.service;

import com.pda.piggyBank_module.web.dto.MileageRequest;
import org.springframework.transaction.annotation.Transactional;

public interface MileageService {
    @Transactional
    public void addMileage(MileageRequest request);
}
