package com.pda.community_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class StockRequestDTO {
    private String stockName;
    private String stockCode;
    private Integer price;
}
