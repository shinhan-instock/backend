package com.pda.stock_module.config;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.service.FetchRankingService;
import com.pda.stock_module.service.FetchStockListService;
import com.pda.stock_module.service.FetchStockThemeService;
import io.lettuce.core.resource.Delay;
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
    private final RedisCommon redisCommon;

    /**
     * 매 1분 15초마다 등락률 순위 업데이트
     */
    @Scheduled(cron = "15 */1 * * * ?")
    public void scheduleFluctuationRankUpdate() {
        fetchRankingService.updateFluctuationRanking();
    }


    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void scheduleStockDataUpdate() {
        fetchStockListService.updateStockData(); // stockName, stockCode, price, priceChange, sectorName
        fetchStockThemeService.updateStockThemeData();
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 5000) // 1시간마다 실행 , 5초 지연 후 시작.
    public void scheduledStockRankUpdate() {
        fetchStockListService.fetchAndSaveStockRank(); // 시가총액(rank) 저장.
        redisCommon.syncAllStocksToZSet(); // 시총 순으로 정렬하는 Zset 생성.
    }


}
