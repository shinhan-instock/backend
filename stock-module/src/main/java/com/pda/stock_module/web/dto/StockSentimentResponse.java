package com.pda.stock_module.web.dto;

public record StockSentimentResponse(
        String stockName,
        String stockCode,
        Integer price,
        String priceChange,
        String description,
        boolean watchListAdded,
        Long sentimentScore
) {
}