package com.pda.community_module.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface WatchListService {
    SseEmitter streamStockPrices(Long userId, int page, int size);
}