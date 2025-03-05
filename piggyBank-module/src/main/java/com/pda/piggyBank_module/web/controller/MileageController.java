package com.pda.piggyBank_module.web.controller;

import com.pda.piggyBank_module.service.MileageService;
import com.pda.piggyBank_module.web.dto.MileageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pigs")
@RequiredArgsConstructor
public class MileageController {
    private final MileageService mileageService;


    @Operation(summary = "마일리지 조회", description = "사용자의 마일리지를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK, 성공"),
    })
    @GetMapping("")
    public ResponseEntity<MileageResponseDto> getMileage(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));

        return ResponseEntity.ok(mileageService.getMileageByUserId(userId));
    }

}
