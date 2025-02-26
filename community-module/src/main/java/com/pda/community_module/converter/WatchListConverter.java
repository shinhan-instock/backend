package com.pda.community_module.converter;

import com.pda.community_module.domain.WatchList;
import com.pda.community_module.domain.User;

public class WatchListConverter {

    public static WatchList toWatchListEntity(User user, String stockCode, String stockName) {
        return WatchList.builder()
                .user(user)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }
}
