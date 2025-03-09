package com.pda.piggyBank_module.web.controller;

import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.piggyBank_module.service.MileageService;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import com.pda.piggyBank_module.web.dto.MileageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mileage")
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;


    @Operation(summary = "마일리지 조회", description = "사용자의 마일리지를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<MileageResponseDto> getMileage(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(mileageService.getMileageByUserId(userId));
    }

    @PostMapping(value = "/add", consumes = "application/json")
    public com.pda.core_module.apiPayload.ApiResponse<String> addMileage(@RequestBody MileageRequest request) {
        mileageService.addMileage(request);
        return com.pda.core_module.apiPayload.ApiResponse.onSuccess("마일리지 지급 완료");
    }

    @PostMapping("")
    public ResponseEntity<MileageResponseDto> updateMileage(@RequestBody MileageRequest.UpdateMileageRequest request) {
        String userId = request.getUserId();
        Integer updatedMileage = request.getMileage();
        return ResponseEntity.ok(mileageService.updateMileageByUserId(userId, updatedMileage));
    }
}
