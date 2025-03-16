package com.pda.community_module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.community_module.converter.WatchListConverter;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.WatchListRequestDTO;
import com.pda.core_module.apiPayload.ApiResponse;
import com.pda.community_module.config.StockServiceClient;
import com.pda.community_module.converter.StockSearchConverter;
import com.pda.community_module.domain.WatchList;
import com.pda.community_module.repository.WatchListRepository;
import com.pda.community_module.web.dto.StockResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {

    private final WatchListRepository watchListRepository;
    private final StockServiceClient stockServiceClient;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter streamStockPrices(String userId, int page, int size) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                User user = userRepository.findByUserId(userId).orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
                Page<WatchList> watchListsPage = watchListRepository.findByUserId(user.getId(), PageRequest.of(page, size));
                List<String> stockNames = watchListsPage.getContent().stream()
                        .map(WatchList::getStockName)
                        .collect(Collectors.toList());

                if (stockNames.isEmpty()) {
                    emitter.complete();
                    return;
                }

                StockResponseDTO stockResponse = stockServiceClient.getStockData(stockNames);

                List<StockResponseDTO.StockResult> stockResults = StockSearchConverter.toStockSearchResList(stockResponse);

                ApiResponse<List<StockResponseDTO.StockResult>> response = ApiResponse.onSuccess(stockResults);

                String jsonResponse = objectMapper.writeValueAsString(response);
                emitter.send(jsonResponse);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, 0, 3, TimeUnit.SECONDS);

        return emitter;
    }

    @Transactional
    @Override
    public void addWatchList(String userId, String stockCode, String stockName) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        WatchList watchListEntity = WatchListConverter.toWatchListEntity(user, stockCode, stockName);
        watchListRepository.save(watchListEntity);
    }

    @Override
    @Transactional
    public void deleteWatchList(WatchListRequestDTO.DeleteWatchListDTO requestDTO) {
        User user = userRepository.findByUserId(requestDTO.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        WatchList watchList = watchListRepository.findByUserAndStockName(user, requestDTO.getStockName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.WATCHLIST_NOT_FOUND));

        watchListRepository.delete(watchList);
    }

    @Override
    public boolean isStockInWatchList(String userId, String stockCode) {
        return watchListRepository.existsByUser_UserIdAndStockCode(userId, stockCode);

    }
}
