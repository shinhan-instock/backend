package com.pda.community_module.web.dto;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockResponseDTO {
    private boolean isSuccess;
    private String code;
    private String message;
    private List<StockResult> result;

    public List<StockResult> getResultAsList() {
        return result != null ? result : Collections.emptyList();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class StockResult {
        private String stockCode;
        private String stockName;
        private Long currentPrice;
        private Double changeRate;
    }
}
