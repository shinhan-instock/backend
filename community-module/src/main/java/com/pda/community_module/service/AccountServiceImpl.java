package com.pda.community_module.service;

import com.pda.community_module.domain.Account;
import com.pda.community_module.domain.OwnStock;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.AccountRepository;
import com.pda.community_module.repository.OwnStockRepository;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.AccountResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import feign.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
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
    private final UserRepository userRepository;


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
        throw new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND); // 계좌 개설 필수
    }


    @Override
    public List<AccountResponseDTO> getAccount(String myUserId, String userId) {
        Account myAccount = accountRepository.findByUserId_UserId(myUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND)); // 계좌 개설 필수

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND)); // 사용자 존재 여부 확인

        if (user.getIsInfluencer()) {
            Account userAccount = accountRepository.findByUserId_UserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));

            // 사용자 계좌 확인
            Long accountId = userAccount.getId();

            List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
            if(userOwnStock.isEmpty()) {
                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
            }

            return userOwnStock.stream()
                    .map(stock -> new AccountResponseDTO(
                            stock.getStockName(),
                            stock.getStockCode(),
                            stock.getStockCount(),
                            null, // avgPrice는 null 처리
                            stock.getProfit()
                    ))
                    .collect(Collectors.toList());
        } else { // 일반인 일때
            Account userAccount = accountRepository.findByUserId_UserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));

            // 사용자 계좌 확인
            Long accountId = userAccount.getId();

            List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);

            if(userOwnStock.isEmpty()) {
                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
            }

            return userOwnStock.stream()
                    .map(stock -> new AccountResponseDTO(
                            stock.getStockName(),
                            null,
                            null,
                            null,
                            null
                    ))
                    .collect(Collectors.toList());
        }
    }
}
