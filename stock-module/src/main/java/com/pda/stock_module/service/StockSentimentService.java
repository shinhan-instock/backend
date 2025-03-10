package com.pda.stock_module.service;

import com.pda.stock_module.web.dto.StockRequest;

import java.util.List;

public interface StockSentimentService {
    void addStockSentiment(List<StockRequest> stockRequestList);
}
