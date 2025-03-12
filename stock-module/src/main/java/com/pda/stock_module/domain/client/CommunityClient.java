package com.pda.stock_module.domain.client;

import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.stock_module.web.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "community-service", url = "${feign.community-url}")

public interface CommunityClient {
    @GetMapping("/accounts")
    List<AccountResponseDTO> getMyAccount(@RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("/watchList/exists")
    Boolean isStockInWatchList(@RequestParam("userId") String userId,
                               @RequestParam("stockCode") String stockCode);
}
