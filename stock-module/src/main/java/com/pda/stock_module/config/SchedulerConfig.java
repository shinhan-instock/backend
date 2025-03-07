package com.pda.stock_module.config;
import com.pda.stock_module.domain.common.RedisCommon;
import com.pda.stock_module.service.FetchRankingService;
import com.pda.stock_module.service.FetchStockListService;
import com.pda.stock_module.service.FetchStockThemeService;
import org.springframework.core.task.TaskExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final FetchRankingService fetchRankingService;
    private final FetchStockListService fetchStockListService;
    private final FetchStockThemeService fetchStockThemeService;
    private final RedisCommon redisCommon;

    private final AtomicBoolean isInitialized = new AtomicBoolean(false); // 초기화 여부 확인


    /**
     * 애플리케이션 시작 시 한 번 실행되는 초기화 작업
     */
    @PostConstruct
    public void initializeStockData() {
            try {
                fetchStockListService.updateStockData();
                redisCommon.syncAllStocksToZSetWithScore();
                redisCommon.syncAllStocksToZSetWithReference();
                isInitialized.set(true);

                System.out.println("초기화 완료: 주식 데이터 및 ZSet 생성이 완료되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /**
     * 매 1분 15초마다 등락률 순위 업데이트
     */
    @Scheduled(cron = "15 */1 * * * ?")
    public void scheduleFluctuationRankUpdate() {
        fetchRankingService.updateFluctuationRanking();
    }


    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void scheduleStockDataUpdate() {
        if (!isInitialized.get()) {
            return; // 초기화가 끝나지 않으면 실행하지 않음
        }

        fetchStockListService.updateStockData();
        fetchStockThemeService.updateStockThemeData();
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 5000) // 1시간마다 실행 , 5초 지연 후 시작.
    public void scheduledStockRankUpdate() {
        if (!isInitialized.get()) {
            return; // 초기화가 끝나지 않으면 실행하지 않음
        }

        fetchStockListService.fetchAndSaveStockRank(); // 시가총액(rank) 저장.
        redisCommon.syncAllStocksToZSet(); // 시총 순으로 정렬하는 Zset 생성.
    }


}
