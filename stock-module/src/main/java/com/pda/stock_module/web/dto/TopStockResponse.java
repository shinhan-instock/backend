package com.pda.stock_module.web.dto;

public record TopStockResponse(
        String stockName,
        Integer price,
        String priceChange
) {
}
