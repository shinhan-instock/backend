package com.pda.stock_module.domain.client;


import com.pda.stock_module.web.dto.MileageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "piggy-service", url = "http://localhost:8082/mileage")
public interface MileageClient {

    @GetMapping("")
    MileageResponseDto getMileage(@RequestHeader("Authorization") String authorizationHeader);
}
