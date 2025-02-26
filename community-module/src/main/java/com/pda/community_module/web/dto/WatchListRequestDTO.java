package com.pda.community_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WatchListRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddWatchListDTO {
        private Long userId;
        private String stockCode;
        private String stockName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteWatchListDTO {
        private Long userId;
        private String stockName;
    }
}
