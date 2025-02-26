package com.pda.stock_module.web.dto;

public record StockResponse(
        String stockName,
        String stockCode,
        Integer price,
        String priceChangeRate,
        String sectorName
) {
}
