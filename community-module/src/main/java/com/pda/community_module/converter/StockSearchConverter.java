package com.pda.community_module.converter;

import com.pda.community_module.web.dto.StockResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class StockSearchConverter {
    public static List<StockResponseDTO.StockResult> toStockSearchResList(StockResponseDTO stockResponse) {
        return stockResponse.getResultAsList().stream()
                .map(stock -> new StockResponseDTO.StockResult(
                        stock.getStockCode(),
                        stock.getStockName(),
                        stock.getCurrentPrice(),
                        stock.getChangeRate()
                ))
                .collect(Collectors.toList());
    }
}
