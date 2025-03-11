package com.pda.community_module.service;

import com.pda.community_module.config.MileageClient;
import com.pda.community_module.domain.Account;
import com.pda.community_module.domain.OwnStock;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.AccountRepository;
import com.pda.community_module.repository.OwnStockRepository;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.AccountResponseDTO;
import com.pda.community_module.web.dto.MileageRequestDTO;
import com.pda.community_module.web.dto.StockRequestDTO;
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
    private final MileageClient mileageClient;


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

        if (myAccount.getUser().getOpenAccount() == false) {
            throw new GeneralException(ErrorStatus.NOT_GET_ACCOUNT);
        }

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
            if (user.getOpenAccount() == false) {
                throw new GeneralException(ErrorStatus.DO_NOT_WANT_ACCOUNT);
            }

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

    @Override
    public List<AccountResponseDTO> addMyAccount(String userId, StockRequestDTO stockRequestDTO, Integer mileage) {
        Account account = accountRepository.findByUserId_UserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND)); // 계좌 개설 필수


        Long accountId = account.getId();
        List<OwnStock> stocks = ownStockRepository.findByAccountId(accountId);

        String requestStockCode = stockRequestDTO.getStockCode();
        String requestStockName = stockRequestDTO.getStockName();
        Integer requestPrice = stockRequestDTO.getPrice();

        if (mileage < requestPrice) {
            throw new GeneralException(ErrorStatus.MILEAGE_NOT_ENOUGH);
        }

        Optional<OwnStock> existingStockOpt = stocks.stream()
                .filter(stock -> stock.getStockCode().equals(requestStockCode))
                .findFirst();

        if (existingStockOpt.isPresent()) {
            // 기존 주식이 있으면 업데이트
            OwnStock existingStock = existingStockOpt.get();
            Long newStockCount = existingStock.getStockCount() + 1;
            Long newAvgPrice = (existingStock.getAvgPrice() * existingStock.getStockCount() + requestPrice) / newStockCount;
            Double profit = Math.round(((double) (requestPrice - newAvgPrice) / newAvgPrice) * 100 * 100) / 100.0;

            existingStock = OwnStock.builder()
                    .id(existingStock.getId())
                    .account(account)
                    .stockName(existingStock.getStockName())
                    .stockCode(existingStock.getStockCode())
                    .stockCount(newStockCount)
                    .avgPrice(newAvgPrice)
                    .profit(profit)
                    .build();

            ownStockRepository.updateStock(existingStock.getId(), newStockCount, newAvgPrice, profit);

        } else {
            // 새로운 주식 추가 (수익률 계산 적용)
            Double profit = ((double) (requestPrice - requestPrice) / requestPrice) * 100; // 첫 구매이므로 수익률 0%

            OwnStock newStock = OwnStock.builder()
                    .account(account)
                    .stockName(requestStockName)
                    .stockCode(requestStockCode)
                    .stockCount(1L) // 처음 추가되는 주식이므로 1주
                    .avgPrice(requestPrice.longValue()) // 첫 구매가 그대로 평균 단가
                    .profit(profit)
                    .build();

                ownStockRepository.save(newStock);
        }

        // 마일리지 차감
        MileageRequestDTO updateMileage = new MileageRequestDTO(userId, mileage - requestPrice);
        mileageClient.updateMileage(updateMileage);

        // 업데이트된 주식 리스트 반환
        List<OwnStock> updatedStocks = ownStockRepository.findByAccountId(accountId);
        return updatedStocks.stream()
                .map(stock -> new AccountResponseDTO(
                        stock.getStockName(),
                        stock.getStockCode(),
                        stock.getStockCount(),
                        stock.getAvgPrice(),
                        stock.getProfit()
                ))
                .collect(Collectors.toList());
    }
}
