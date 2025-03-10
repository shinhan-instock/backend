package com.pda.community_module.batch.task.stockSentimentAnalysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    private String stockName;

    private Long avgScore;

    private Long postCount;
}
