package com.pda.piggyBank_module.web.controller;

import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class RootController {

    @GetMapping("/health")
    public String health() {
        return "I'm Healthy!!!";
    }

    @GetMapping("/ping")
    @Operation(summary = "테스트 API", description = "간단한 테스트용 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공")
    })
    public ApiResponse<String> ping() {
        return ApiResponse.onSuccess("Pong");
    }

    @GetMapping("/echo")
    @Operation(summary = "Echo API", description = "입력값을 그대로 반환하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공")
    })
    public ApiResponse<String> echo(@RequestParam(name = "message") String message) {
        return ApiResponse.onSuccess("Echo: " + message);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validation API", description = "입력값을 검증하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공")
    })
    public ApiResponse<String> validateInput(@RequestBody String input) {
        if (input == null || input.trim().isEmpty()) {
            return ApiResponse.onSuccess("Invalid input: 값이 비어 있습니다.");
        }
        return ApiResponse.onSuccess("Valid input: " + input);
    }

//    @GetMapping("/test")
//    public String test(){
//        return "cicd is done";
//    }
}