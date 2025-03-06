package com.pda.stock_module.web.dto;

import com.pda.stock_module.domain.StockClosingSentiment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSentimentResetDTO {
    private StockClosingSentiment closingSentiment;
    private Long originalId;
}
