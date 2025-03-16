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
import com.pda.core_module.apiPayload.code.ErrorReasonDTO;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import feign.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponse;
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
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final MileageClient mileageClient;
    private final ObjectMapper objectMapper;

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

    // SSE 스트리밍 (5초마다 보유 주식 목록 전송)
    public SseEmitter streamMyAccount(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // SSE 연결 종료 시 안전하게 정리
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료 (사용자 보유 주식)");
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃 (사용자 보유 주식)");
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            log.error("SSE 연결 오류 (사용자 보유 주식) - " + e.getMessage());
            scheduler.shutdown();
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                Optional<Account> account = accountRepository.findByUserId_UserId(userId);

                if (account.isPresent()) {
                    Long accountId = account.get().getId();
                    List<OwnStock> stocks = ownStockRepository.findByAccountId(accountId);

                    if (stocks.isEmpty()) {
                        throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
                    }

                    List<AccountResponseDTO.AccountResponseStreamDTO> stockList = stocks.stream().map(stock -> {
                        Object priceObj = redisTemplate.opsForHash().get("stock:" + stock.getStockName(), "price");
                        double currentPrice = priceObj != null ? Double.parseDouble(priceObj.toString()) : 0.0;

                        double profit = stock.getAvgPrice() > 0 ?
                                ((currentPrice - stock.getAvgPrice()) / stock.getAvgPrice()) * 100 : 0.0;

                        return new AccountResponseDTO.AccountResponseStreamDTO(
                                stock.getStockName(),
                                stock.getStockCode(),
                                stock.getStockCount(),
                                stock.getAvgPrice(),
                                profit,
                                currentPrice - stock.getAvgPrice()
                        );
                    }).collect(Collectors.toList());
                    String jsonResponse = objectMapper.writeValueAsString(stockList);
                    emitter.send(SseEmitter.event().data(jsonResponse));

                } else {
                    throw new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND);
                }

            } catch (GeneralException e) {
                log.error("데이터 조회 오류: {}", e.getMessage());
                try {
                    ErrorReasonDTO errorReason = e.getErrorReason();  // GeneralException에서 가져오기
                    String errorJson = objectMapper.writeValueAsString(errorReason);
                    emitter.send(SseEmitter.event().data(errorJson));
                } catch (IOException ioException) {
                    log.error("SSE 전송 중 오류 발생: {}", ioException.getMessage());
                }

                emitter.complete();
                scheduler.shutdown();
            } catch (IOException e) {
                log.error("SSE 전송 오류: {}", e.getMessage());
                emitter.complete(); // SSE 연결 종료
                scheduler.shutdown();
            } catch (Exception e) {
                log.error("데이터 조회 오류: {}", e.getMessage());
                emitter.complete(); // SSE 연결 종료
                scheduler.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);

        return emitter;
    }


public SseEmitter streamUserStock(String myUserId, String userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // SSE 연결 종료 시 안전하게 정리
    emitter.onCompletion(() -> {
        log.info("SSE 연결 종료 (사용자 보유 주식)");
        scheduler.shutdown();
    });

    emitter.onTimeout(() -> {
        log.info("SSE 연결 타임아웃 (사용자 보유 주식)");
        scheduler.shutdown();
    });

    emitter.onError((e) -> {
        log.error("SSE 연결 오류 (사용자 보유 주식) - " + e.getMessage());
        scheduler.shutdown();
    });

    // 5초마다 보유 주식 목록 갱신 (스케줄러 실행)
    scheduler.scheduleAtFixedRate(() -> {
        try {
            Account myAccount = accountRepository.findByUserId_UserId(myUserId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.OWN_ACCOUNT_NOT_FOUND));

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            if (user.getIsInfluencer()) {
                Account userAccount = accountRepository.findByUserId_UserId(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_ACCOUNT_NOT_FOUND));

                Long accountId = userAccount.getId();
                List<OwnStock> userOwnStock = ownStockRepository.findByAccountId(accountId);
                if (userOwnStock.isEmpty()) {
                    throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
                }

//                List<AccountResponseDTO> stockList = userOwnStock.stream()
//                        .map(stock -> new AccountResponseDTO(
//                                stock.getStockName(),
//                                stock.getStockCode(),
//                                stock.getStockCount(),
//                                stock.getAvgPrice(),
//                                stock.getProfit()
//                        ))
//                        .collect(Collectors.toList());
//
//                String jsonResponse = objectMapper.writeValueAsString(stockList);
//                emitter.send(SseEmitter.event().data(jsonResponse));

                List<AccountResponseDTO.AccountResponseStreamDTO> stockList = userOwnStock.stream().map(stock -> {
                    Object priceObj = redisTemplate.opsForHash().get("stock:" + stock.getStockName(), "price");
                    double currentPrice = priceObj != null ? Double.parseDouble(priceObj.toString()) : 0.0;

                    double profit = stock.getAvgPrice() > 0 ?
                            ((currentPrice - stock.getAvgPrice()) / stock.getAvgPrice()) * 100 : 0.0;

                    return new AccountResponseDTO.AccountResponseStreamDTO(
                            stock.getStockName(),
                            stock.getStockCode(),
                            stock.getStockCount(),
                            stock.getAvgPrice(),
                            profit,
                            currentPrice - stock.getAvgPrice()
                    );
                }).collect(Collectors.toList());
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
                                stock.getStockCode(), null, null, null
                        ))
                        .collect(Collectors.toList());

                // JSON 변환 후 SSE 전송
                String jsonResponse = objectMapper.writeValueAsString(stockList);
                emitter.send(SseEmitter.event().data(jsonResponse));
            }
        } catch (GeneralException e) {
            log.error("데이터 조회 오류: {}", e.getMessage());
            try {
                ErrorReasonDTO errorReason = e.getErrorReason();  // GeneralException에서 가져오기
                String errorJson = objectMapper.writeValueAsString(errorReason);
                emitter.send(SseEmitter.event().data(errorJson));
            } catch (IOException ioException) {
                log.error("SSE 전송 중 오류 발생: {}", ioException.getMessage());
            }

            // 응답 후 종료
            emitter.complete();
            scheduler.shutdown();
        } catch (IOException e) {
            log.error("SSE 전송 오류: {}", e.getMessage());
            emitter.complete();
            scheduler.shutdown();
        } catch (Exception e) {
            log.error("데이터 조회 오류: {}", e.getMessage());
            emitter.complete();
            scheduler.shutdown();
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
            Double profit = ((double) (requestPrice - requestPrice) / requestPrice) * 100; // 첫 구매이므로 수익률 0%

            OwnStock newStock = OwnStock.builder()
                    .account(account)
                    .stockName(requestStockName)
                    .stockCode(requestStockCode)
                    .stockCount(1L) // 처음 추가되는 주식이므로 1주
                    .avgPrice(requestPrice.longValue())
                    .profit(profit)
                    .build();

                ownStockRepository.save(newStock);
        }

        MileageRequestDTO updateMileage = new MileageRequestDTO(userId, mileage - requestPrice);
        mileageClient.updateMileage(updateMileage);

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
