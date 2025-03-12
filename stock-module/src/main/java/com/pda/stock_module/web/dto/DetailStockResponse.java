package com.pda.stock_module.web.dto;

import org.w3c.dom.Text;

public record DetailStockResponse(
        String stockName,
        String stockCode,
        Integer price,
        String priceChange,
        String description,
        boolean watchListAdded
) {
}