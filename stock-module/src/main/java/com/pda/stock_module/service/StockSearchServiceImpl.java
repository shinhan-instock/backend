package com.pda.stock_module.service;

import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.stock_module.converter.StockSearchConverter;
import com.pda.stock_module.domain.client.OwnStockClient;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.web.dto.AccountResponseDTO;
import com.pda.stock_module.web.dto.StockSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockSearchServiceImpl implements StockSearchService {

    private final StringRedisTemplate redisTemplate;
    private final RedisCommon redisCommon;
    private final OwnStockClient ownStockClient;

    @Override
    public List<StockSearchResponseDTO.StockSearchRes> getStockData(List<String> stockNames) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

        List<Map<Object, Object>> stockDataList = stockNames.stream()
                .map(stockName -> hashOps.entries("stock:" + stockName))
                .collect(Collectors.toList());

        return StockSearchConverter.toStockSearchResList(stockNames, stockDataList);
    }

    @Override
    public List<String> searchStockName(String stockName) {
        return redisCommon.searchStocks(stockName);
    }

    @Override
    public List<String> searchMyStockName(String authorizationHeader) {
        List<AccountResponseDTO> res = ownStockClient.getMyAccount(authorizationHeader);

        if (!res.isEmpty()) {
            return res.stream()
                    .map(AccountResponseDTO::getStockName)
                    .collect(Collectors.toList());
        }

        throw new GeneralException(ErrorStatus.OWN_STOCK_NOT_FOUND);
    }
}
