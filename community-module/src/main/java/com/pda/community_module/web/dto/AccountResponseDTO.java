package com.pda.community_module.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // ✅ 생성자 추가
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {
    private String stockName;
    private String stockCode;
    private Long stockCount;
    private Long avgPrice;
    private Double profit;

    @Getter
    @Setter
    @AllArgsConstructor // ✅ 생성자 추가
    public static class AccountResponseStreamDTO {
        private String stockName;
        private String stockCode;
        private Long stockCount;
        private Long avgPrice;
        private Double profit;
        private Double gapPrice;
    }

}
