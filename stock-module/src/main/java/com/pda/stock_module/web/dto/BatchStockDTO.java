package com.pda.stock_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchStockDTO {
    private String stockName;
    private String stockCode;
    private Long price;
    private String priceChange;
}
