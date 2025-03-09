package com.pda.community_module.web.controller;

import com.pda.community_module.service.AccountService;
import com.pda.community_module.web.dto.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;


    @GetMapping("")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccount(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        List<AccountResponseDTO> response = accountService.getMyAccount(userId);

        return ResponseEntity.ok(response);
    }



}
