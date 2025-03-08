package com.pda.community_module.service;

import com.pda.community_module.domain.Account;
import com.pda.community_module.domain.OwnStock;
import com.pda.community_module.repository.AccountRepository;
import com.pda.community_module.repository.OwnStockRepository;
import com.pda.community_module.web.dto.AccountResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final OwnStockRepository ownStockRepository;

    @Override
    public List<AccountResponseDTO> getMyAccount(String userId) {
        Optional<Account> account = accountRepository.findByUserId_UserId(userId);
        if (account.isPresent()) {
            Long accountId = account.get().getId();
            List<OwnStock> stocks = ownStockRepository.findByAccountId(accountId);


            if (stocks.isEmpty()) {
                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
            }


            return stocks.stream()
                    .map(stock -> new AccountResponseDTO(
                            stock.getStockName(),
                            stock.getStockCode(),
                            stock.getStockCount(),
                            stock.getAvgPrice(),
                            stock.getProfit()
                    ))
                    .collect(Collectors.toList());

        }
        throw new GeneralException(ErrorStatus.USER_NOT_FOUND); // ✅ 서비스에서 예외 발생
    }
}
