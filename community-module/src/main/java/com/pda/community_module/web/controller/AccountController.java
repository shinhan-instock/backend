package com.pda.community_module.web.controller;

import com.pda.community_module.config.MileageClient;
import com.pda.community_module.service.AccountService;
import com.pda.community_module.web.dto.AccountResponseDTO;
import com.pda.community_module.web.dto.MileageResponseDTO;
import com.pda.community_module.web.dto.StockRequestDTO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final MileageClient mileageClient;


    @GetMapping("")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccount(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        List<AccountResponseDTO> response = accountService.getMyAccount(userId);

        return ResponseEntity.ok(response);
    }

    // SSE 스트리밍 API (REST API 제거)
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMyAccount(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = authorizationHeader.replace("Bearer ", "");
        return accountService.streamMyAccount(userId);
    }

    @PostMapping("")
    public ResponseEntity<List<AccountResponseDTO>> addMyAccount(@RequestHeader("Authorization") String authorizationHeader,
                                                                @RequestBody StockRequestDTO stockRequestDTO) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        MileageResponseDTO res = mileageClient.getMileage(authorizationHeader); // 마일리지 조회
        Integer mileage = res.getMileage();
        List<AccountResponseDTO> response = accountService.addMyAccount(userId, stockRequestDTO, mileage);

        return ResponseEntity.ok(response);    }


}
