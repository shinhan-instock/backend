package com.pda.community_module.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WatchListRequestDTO {
    private Long userId;
    private String stockCode;
    private String stockName;
}
