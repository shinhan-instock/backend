package com.pda.community_module.config;


import com.pda.community_module.web.dto.MileageRequestDTO;
import com.pda.community_module.web.dto.MileageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "piggy-service", url = "http://localhost:8082/mileage", configuration = MileageFeignConfig.class)
public interface MileageClient {

    @GetMapping("")
    MileageResponseDTO getMileage(@RequestHeader("Authorization") String authorizationHeader);

    @PostMapping(value = "", consumes = "application/json")
    void updateMileage(@RequestBody MileageRequestDTO mileageRequestDTO);

}
