package com.pda.stock_module.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class ListModel {
    private final String stockName;
    private final Integer price;
    private final String priceChange;

}
