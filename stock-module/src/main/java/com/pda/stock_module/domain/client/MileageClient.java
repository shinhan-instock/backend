package com.pda.stock_module.domain.client;


import com.pda.stock_module.web.dto.MileageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "piggy-service", url = "${feign.piggyBank-url}")
public interface MileageClient {

    @GetMapping("/mileage")
    MileageResponseDTO getMileage(@RequestHeader("Authorization") String authorizationHeader);
}
