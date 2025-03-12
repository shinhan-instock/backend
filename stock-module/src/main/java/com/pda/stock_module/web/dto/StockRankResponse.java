package com.pda.stock_module.web.dto;

public record StockRankResponse (
        String stockCode,
        String stockName,
        Long price,
        String priceChangeRate

) {

}