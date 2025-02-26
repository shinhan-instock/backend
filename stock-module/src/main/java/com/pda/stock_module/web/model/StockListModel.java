package com.pda.stock_module.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class StockListModel {
    private final String stockName;
    private final String stockCode;
    private final Integer price;
    private final String priceChangeRate;
    private final String sectorName;

}
