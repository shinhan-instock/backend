package com.pda.community_module.web.dto;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter // Lombok을 이용하여 Setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockResponseDTO {
    private boolean isSuccess;
    private String code;
    private String message;
    private List<StockResult> result;

    // result를 리스트 형태로 반환하는 메서드 추가
    public List<StockResult> getResultAsList() {
        return result != null ? result : Collections.emptyList();
    }

    @Getter
    @Setter // Lombok을 이용하여 Setter 자동 생성
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
