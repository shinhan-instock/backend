package com.pda.stock_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    private String stockName;

    private Long avgScore;

    private Long postCount;
}
