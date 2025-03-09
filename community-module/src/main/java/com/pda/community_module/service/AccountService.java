package com.pda.community_module.service;

import com.pda.community_module.web.dto.AccountResponseDTO;
import com.pda.community_module.web.dto.StockRequestDTO;

import java.util.List;

public interface AccountService {
    List<AccountResponseDTO> getMyAccount(String userId);

    List<AccountResponseDTO> getAccount(String myUserId, String userId);

    List<AccountResponseDTO> addMyAccount(String userId, StockRequestDTO stockRequestDTO, Integer mileage);
}
