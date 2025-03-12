package com.pda.community_module.batch.task.stockSentimentAnalysis;

import com.pda.community_module.batch.task.likeTop10.MileageRequest;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.Sentiment;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@StepScope
public class StockSentimentAnalysisProcessor implements ItemProcessor<List<Sentiment>, List<StockRequest>> {

    @Override
    public List<StockRequest> process(List<Sentiment> item) throws Exception {

        Map<String, List<Sentiment>> groupedByStock = item.stream()
                .filter(sentiment -> sentiment.getPost() != null && sentiment.getPost().getHashtag() != null)
                .collect(Collectors.groupingBy(sentiment -> sentiment.getPost().getHashtag()));

        // 그룹화된 데이터 기반으로 StockRequest 리스트 생성
        return groupedByStock.entrySet().stream()
                .map(entry -> {
                    String stockName = entry.getKey();
                    List<Sentiment> stockSentiments = entry.getValue();

                    long totalScore = stockSentiments.stream().mapToLong(Sentiment::getSentimentScore).sum();
                    long postCount = stockSentiments.size();
                    long avgScore = postCount > 0 ? totalScore / postCount : 0;

                    return new StockRequest(stockName, avgScore, postCount);
                })
                .collect(Collectors.toList());
    }
}
