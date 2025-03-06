package com.pda.piggyBank_module.config;

import com.pda.piggyBank_module.web.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserFeignClient {
    @GetMapping("/users/getPK/{userId}")
    UserResponseDTO.UserRealPKResponseDto getUserByUserId(@PathVariable("userId") String userId);
}
