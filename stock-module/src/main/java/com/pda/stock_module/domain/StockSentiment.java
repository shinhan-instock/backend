package com.pda.stock_module.domain;

import com.pda.stock_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "stock_sentiment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSentiment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "sentiment_score")
    private Long sentimentScore;

    @Column(name = "processed_post_count")
    private Long processedPostCount;
}
