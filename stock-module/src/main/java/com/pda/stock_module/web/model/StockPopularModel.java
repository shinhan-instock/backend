package com.pda.stock_module.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockPopularModel {
    private final String stockName;
    private final String stockCode;
    private final Integer price;
    private final String priceChange;
    private final String sectorName;
    private final String rank;
    private final Double score;
}
