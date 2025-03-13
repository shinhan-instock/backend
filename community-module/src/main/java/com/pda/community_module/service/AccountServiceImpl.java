package com.pda.community_module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final OwnStockRepository ownStockRepository;
    private final UserRepository userRepository;
    private final MileageClient mileageClient;
    private final ObjectMapper objectMapper;

//    @Override
//    public List<AccountResponseDTO> getMyAccount(String userId) {
//        Optional<Account> account = accountRepository.findByUserId_UserId(userId);
//        if (account.isPresent()) {
//            Long accountId = account.get().getId();
//            List<OwnStock> stocks = ownStockRepository.findByAccountId(accountId);
//
//
//            if (stocks.isEmpty()) {
//                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
//            }
//
//
//            return stocks.stream()
//                    .map(stock -> new AccountResponseDTO(
//                            stock.getStockName(),
//                            stock.getStockCode(),
//                            stock.getStockCount(),
//                            stock.getAvgPrice(),
//                            stock.getProfit()
//                    ))
//                    .collect(Collectors.toList());
//
//        }
//        throw new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND); // 계좌 개설 필수
//    }

    // SSE 스트리밍 (5초마다 보유 주식 목록 전송)
    public SseEmitter streamMyAccount(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // SSE 연결 종료 시 안전하게 정리
        emitter.onCompletion(() -> {
            log.info("✅ SSE 연결 종료 (사용자 보유 주식)");
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            log.info("⚠️ SSE 연결 타임아웃 (사용자 보유 주식)");
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            log.error("❌ SSE 연결 오류 (사용자 보유 주식) - " + e.getMessage());
            scheduler.shutdown();
        });

        // 5초마다 보유 주식 목록 갱신 (스케줄러 실행)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 🔥 사용자의 보유 주식 조회
                Optional<Account> account = accountRepository.findByUserId_UserId(userId);

                if (account.isPresent()) {
                    Long accountId = account.get().getId();
                    List<OwnStock> stocks = ownStockRepository.findByAccountId(accountId);

                    if (stocks.isEmpty()) {
                        throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
                    }

                    List<AccountResponseDTO> stockList = stocks.stream()
                            .map(stock -> new AccountResponseDTO(
                                    stock.getStockName(),
                                    stock.getStockCode(),
                                    stock.getStockCount(),
                                    stock.getAvgPrice(),
                                    stock.getProfit()
                            ))
                            .collect(Collectors.toList());

                    // JSON 변환 후 SSE 전송
                    String jsonResponse = objectMapper.writeValueAsString(stockList);
                    emitter.send(SseEmitter.event().data(jsonResponse));

                } else {
                    throw new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND);
                }

            } catch (IOException e) {
                log.error("❌ SSE 전송 오류: {}", e.getMessage());
                scheduler.shutdown();
            } catch (Exception e) {
                log.error("🚨 데이터 조회 오류: {}", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }



//    @Override
//    public List<AccountResponseDTO> getAccount(String myUserId, String userId) {
//        Account myAccount = accountRepository.findByUserId_UserId(myUserId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND)); // 계좌 개설 필수
//
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND)); // 사용자 존재 여부 확인
//
////        if (myAccount.getUser().getOpenAccount() == false) {
////            throw new GeneralException(ErrorStatus.NOT_GET_ACCOUNT);
////        }
//
//        if (user.getIsInfluencer()) {
//            Account userAccount = accountRepository.findByUserId_UserId(userId)
//                    .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));
//
//            // 사용자 계좌 확인
//            Long accountId = userAccount.getId();
//
//            List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
//            if(userOwnStock.isEmpty()) {
//                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
//            }
//
//            return userOwnStock.stream()
//                    .map(stock -> new AccountResponseDTO(
//                            stock.getStockName(),
//                            stock.getStockCode(),
//                            stock.getStockCount(),
//                            null, // avgPrice는 null 처리
//                            stock.getProfit()
//                    ))
//                    .collect(Collectors.toList());
//        } else { // 일반인 일때
//            if (user.getOpenAccount() == false) {
//                throw new GeneralException(ErrorStatus.DO_NOT_WANT_ACCOUNT);
//            }
//
//            Account userAccount = accountRepository.findByUserId_UserId(userId)
//                    .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));
//
//            // 사용자 계좌 확인
//            Long accountId = userAccount.getId();
//
//            List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
//
//            if(userOwnStock.isEmpty()) {
//                throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
//            }
//
//            return userOwnStock.stream()
//                    .map(stock -> new AccountResponseDTO(
//                            stock.getStockName(),
//                            null,
//                            null,
//                            null,
//                            null
//                    ))
//                    .collect(Collectors.toList());
//        }
//    }

// SSE 스트리밍 (5초마다 특정 사용자의 보유 주식 목록 전송)
public SseEmitter streamUserStock(String myUserId, String userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // SSE 연결 종료 시 안전하게 정리
    emitter.onCompletion(() -> {
        log.info("✅ SSE 연결 종료 (사용자 보유 주식)");
        scheduler.shutdown();
    });

    emitter.onTimeout(() -> {
        log.info("⚠️ SSE 연결 타임아웃 (사용자 보유 주식)");
        scheduler.shutdown();
    });

    emitter.onError((e) -> {
        log.error("❌ SSE 연결 오류 (사용자 보유 주식) - " + e.getMessage());
        scheduler.shutdown();
    });

    // 5초마다 보유 주식 목록 갱신 (스케줄러 실행)
    scheduler.scheduleAtFixedRate(() -> {
        try {
            // 🔥 내 계좌 확인
            Account myAccount = accountRepository.findByUserId_UserId(myUserId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND));

            // 🔥 대상 사용자 확인
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            // 🔥 대상 사용자의 계좌 조회
            if (user.getIsInfluencer()) {
                Account userAccount = accountRepository.findByUserId_UserId(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));

                Long accountId = userAccount.getId();
                List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
                if (userOwnStock.isEmpty()) {
                    throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
                }

                List<AccountResponseDTO> stockList = userOwnStock.stream()
                        .map(stock -> new AccountResponseDTO(
                                stock.getStockName(),
                                stock.getStockCode(),
                                stock.getStockCount(),
                                null, // avgPrice는 null 처리
                                stock.getProfit()
                        ))
                        .collect(Collectors.toList());

                // JSON 변환 후 SSE 전송
                String jsonResponse = objectMapper.writeValueAsString(stockList);
                emitter.send(SseEmitter.event().data(jsonResponse));

            } else { // 일반 사용자일 때
                if (!user.getOpenAccount()) {
                    throw new GeneralException(ErrorStatus.DO_NOT_WANT_ACCOUNT);
                }

                Account userAccount = accountRepository.findByUserId_UserId(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));

                Long accountId = userAccount.getId();
                List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
                if (userOwnStock.isEmpty()) {
                    throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
                }

                List<AccountResponseDTO> stockList = userOwnStock.stream()
                        .map(stock -> new AccountResponseDTO(
                                stock.getStockName(),
                                null, null, null, null
                        ))
                        .collect(Collectors.toList());

                // JSON 변환 후 SSE 전송
                String jsonResponse = objectMapper.writeValueAsString(stockList);
                emitter.send(SseEmitter.event().data(jsonResponse));
            }

        } catch (IOException e) {
            log.error("❌ SSE 전송 오류: {}", e.getMessage());
            scheduler.shutdown();
        } catch (Exception e) {
            log.error("🚨 데이터 조회 오류: {}", e.getMessage());
        }
    }, 0, 5, TimeUnit.SECONDS);

    return emitter;
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
