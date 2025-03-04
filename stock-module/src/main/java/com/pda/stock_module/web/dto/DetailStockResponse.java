package com.pda.stock_module.web.dto;

public record DetailStockResponse(
        String stockName,
        Integer price,
        String priceChange
) {
}