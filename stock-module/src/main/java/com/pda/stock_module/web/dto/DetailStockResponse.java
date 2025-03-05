package com.pda.stock_module.web.dto;

public record DetailStockResponse(
        String stockName,
        String stockCode,
        Integer price,
        String priceChange
) {
}