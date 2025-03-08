package com.pda.community_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // ✅ 생성자 추가
public class AccountResponseDTO {
    private String stockName;
    private String stockCode;
    private Long stockCount;
    private Long avgPrice;
    private Long profit;
}
