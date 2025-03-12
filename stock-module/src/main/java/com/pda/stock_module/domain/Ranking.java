package com.pda.stock_module.domain;

import com.pda.stock_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ranking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ranking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id")
    private Long id;

    @Column(name = "stock_name", nullable = false)
    private String stockName; // 종목명

    @Column(name = "stock_code", unique = true)
    private String stockCode; // 종목 코드 (유니크하게 설정)

    @Column(name = "current_price", nullable = false)
    private Long currentPrice; // 현재가

    // 순위 정보

    @Column(name = "fluctuation_rank", nullable = true)
    private Integer fluctuationRank; // 등락률 순위

    // 세부 정보 (관련 값 String 형태로 저장)
    @Column(name = "fluctuation_info", length = 500)
    private String fluctuationInfo; // 등락률 관련 정보

    // 주요 지표
    @Column(name = "price_change", nullable = true)
    private Long priceChange; // 전일 대비 금액

    @Column(name = "price_change_rate", nullable = true)
    private String priceChangeRate; // 전일 대비 등락률

    // 기타
    @Column(name = "data_source", nullable = true, length = 100)
    private String dataSource; // 데이터 소스 정보

    @Column(nullable = false)
    private final LocalDateTime collectedAt = LocalDateTime.now();

    @Column(name = "is_valid", nullable = false)
    @Builder.Default
    private Boolean isValid = true; // 데이터 유효성 여부
}