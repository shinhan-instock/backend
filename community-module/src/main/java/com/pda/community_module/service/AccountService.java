package com.pda.community_module.service;

import com.pda.community_module.web.dto.AccountResponseDTO;

import java.util.List;

public interface AccountService {
    List<AccountResponseDTO> getMyAccount(String userId);

    List<AccountResponseDTO> getAccount(String myUserId, String userId);
}
