package com.pda.stock_module.domain;

import com.pda.stock_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "stock_closing_price")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockClosingPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "closing_price")
    private Long closingPrice;
}
