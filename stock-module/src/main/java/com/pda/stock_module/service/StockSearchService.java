package com.pda.stock_module.service;


import com.pda.stock_module.web.dto.StockSearchResponseDTO;

import java.util.List;

public interface StockSearchService {
    List<StockSearchResponseDTO.StockSearchRes> getStockData(List<String> stockNames);
}