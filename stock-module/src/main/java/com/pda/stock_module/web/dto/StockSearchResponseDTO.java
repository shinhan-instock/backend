package com.pda.stock_module.web.dto;

import lombok.*;

import java.util.List;

public class StockSearchResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class StockSearchRes {
        private String stockCode;
        private String stockName;
        private Long currentPrice;
        private Double changeRate;
    }
}
