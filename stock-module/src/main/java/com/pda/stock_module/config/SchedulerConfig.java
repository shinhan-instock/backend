package com.pda.stock_module.config;
import com.pda.stock_module.service.FetchRankingService;
import com.pda.stock_module.service.FetchStockListService;
import com.pda.stock_module.service.FetchStockThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 매 1분 15초마다 등락률 순위 업데이트
     */
    @Scheduled(cron = "15 */1 * * * ?")
    public void scheduleFluctuationRankUpdate() {
        fetchRankingService.updateFluctuationRanking();
    }

    /**
     * 매 1분 30초마다 수익자산지표 순위 업데이트
     */
    @Scheduled(cron = "30 */1 * * * ?")
    public void scheduleProfitAssetRankUpdate() {
        fetchRankingService.updateProfitAssetRanking();
    }

    /**
     * 매 1분 45초마다 시가총액 순위 업데이트
     */
    @Scheduled(cron = "45 */1 * * * ?")
    public void scheduleMarketCapRankUpdate() {
        fetchRankingService.updateMarketCapRanking();
    }


    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void scheduleStockDataUpdate() {
        fetchStockListService.updateStockData();
        fetchStockThemeService.updateStockThemeData();
    }
}
