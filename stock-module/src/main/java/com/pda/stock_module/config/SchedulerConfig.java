package com.pda.stock_module.config;
import com.pda.stock_module.service.FetchRankingService;
import com.pda.stock_module.service.FetchStockListService;
import com.pda.stock_module.service.FetchStockThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final FetchRankingService fetchRankingService;
    private final FetchStockListService fetchStockListService;
    private final FetchStockThemeService fetchStockThemeService;

    /**
     * 매 1분마다 거래량 순위 업데이트
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void scheduleVolumeRankUpdate() {
        fetchRankingService.updateVolumeRanking();
    }

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void scheduleStockDataUpdate() {
        fetchStockListService.updateStockData(); // stockName, stockCode, price, priceChange, sectorName
        fetchStockThemeService.updateStockThemeData();
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void scheduledStockRankUpdate() {
        fetchStockListService.fetchAndSaveStockRank(); // 시가총액(rank) 저장.
    }
}
