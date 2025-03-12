package com.pda.stock_module.web.dto;

public record StockPopularRankResponse(
        String stockCode,
        String stockName,
        Long price,
        String priceChangeRate,
        Double score
) {
}
