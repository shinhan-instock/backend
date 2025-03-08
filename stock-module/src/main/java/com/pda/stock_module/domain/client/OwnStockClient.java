package com.pda.stock_module.domain.client;

import com.pda.stock_module.web.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "ownStock-service", url = "http://localhost:8080/accounts")

public interface OwnStockClient {
    @GetMapping("")
    List<AccountResponseDTO> getMyAccount(@RequestHeader("Authorization") String authorizationHeader);

}
