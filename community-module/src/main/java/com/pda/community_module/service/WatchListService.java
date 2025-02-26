package com.pda.community_module.service;

import com.pda.community_module.web.dto.WatchListRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface WatchListService {
    SseEmitter streamStockPrices(Long userId, int page, int size);
    void addWatchList(Long userId, String stockCode, String stockName);
}