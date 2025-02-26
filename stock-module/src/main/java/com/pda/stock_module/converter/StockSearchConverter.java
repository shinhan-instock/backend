package com.pda.stock_module.converter;

import com.pda.stock_module.web.dto.StockSearchResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockSearchConverter {
    public static List<StockSearchResponseDTO.StockSearchRes> toStockSearchResList(
            List<String> stockNames, List<Map<Object, Object>> stockDataList) {

        List<StockSearchResponseDTO.StockSearchRes> resultList = new ArrayList<>();
        for (int i = 0; i < stockNames.size(); i++) {
            if (stockDataList.get(i).isEmpty()) continue;

            resultList.add(
                    StockSearchResponseDTO.StockSearchRes.builder()
                            .stockName(stockNames.get(i))
                            .stockCode((String) stockDataList.get(i).get("stockCode"))
                            .currentPrice(Long.parseLong((String) stockDataList.get(i).get("price")))
                            .changeRate(Double.parseDouble((String) stockDataList.get(i).get("priceChange")))
                            .build()
            );
        }
        return resultList;
    }
}
