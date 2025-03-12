package com.pda.community_module.service;

import com.pda.community_module.web.dto.WatchListRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface WatchListService {
    SseEmitter streamStockPrices(String userId, int page, int size);
    void addWatchList(String userId, String stockCode, String stockName);
    void deleteWatchList(WatchListRequestDTO.DeleteWatchListDTO requestDTO);
    boolean isStockInWatchList(String userId, String stockCode);

}