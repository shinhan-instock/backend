package com.pda.stock_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockClosingPriceDTO {
    private String stockCode;
    private String stockName;
    private String price;
}