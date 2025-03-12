package com.pda.stock_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChartResponseDTO {
    private LocalDateTime day;
    private Long stock;
    private Long sentiment;
}
