package com.pda.piggyBank_module.web.controller;

import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.piggyBank_module.service.MileageService;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mileage")
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;

    @PostMapping(value = "/add", consumes = "application/json")
    public ApiResponse<String> addMileage(@RequestBody MileageRequest request) {
        mileageService.addMileage(request);
        return ApiResponse.onSuccess("마일리지 지급 완료");
    }
}
