package com.pda.stock_module.service;

import com.pda.stock_module.domain.common.RedisCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FetchStockListService {
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final RedisCommon redisCommon;

    @Transactional
    public void updateStockData() {
        String[] markets = {"KOSPI", "KOSDAQ"}; // 시장 배열

        // 배치 크롤링을 위한 Map 정의. (종목명 + stockCode 전달)
        Map<String, String> stockDataMap = new HashMap<>();

        for (String market : markets) {
            String apiUrl = "https://finance.daum.net/api/quotes/sectors?fieldName=&order=&perPage=&market="
                    + market
                    + "&page=&changes=UPPER_LIMIT,RISE,EVEN,FALL,LOWER_LIMIT";

            try {
                // 요청 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
                headers.set("Referer", "https://finance.daum.net/domestic/all_stocks");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // RestTemplate 요청
                ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    List<Map<String, Object>> sectors = (List<Map<String, Object>>) body.get("data");
                    Set<String> processedStockNames = new HashSet<>(); // 이미 처리된 stockName 저장

                    for (Map<String, Object> sector : sectors) {
                        String sectorName = (String) sector.get("sectorName"); // 업종명.
                        List<Map<String, Object>> includedStocks = (List<Map<String, Object>>) sector.get("includedStocks");

                        for (Map<String, Object> stockData : includedStocks) {
                            String stockCode = (String) stockData.get("symbolCode");
                            if (stockCode != null && stockCode.startsWith("A")) {
                                stockCode = stockCode.substring(1); // 'A' 제거
                            }

                            String stockName = (String) stockData.get("name");
                            Long price = stockData.get("tradePrice") != null
                                    ? Math.round(Double.valueOf(stockData.get("tradePrice").toString()))
                                    : 0L;

                            // changeRate 추출 및 변환
                            String priceChange = null;
                            Object changeRateObj = stockData.get("changeRate");
                            if (changeRateObj instanceof Double) {
                                priceChange = String.format("%.2f", (Double) changeRateObj * 100); // 소수점 둘째 자리까지 변환
                            } else if (changeRateObj instanceof String) {
                                priceChange = String.format("%.2f", Double.valueOf((String) changeRateObj) * 100);
                            }

                            if (stockName != null && stockCode != null) {
                                // 중복된 stockName 무시
                                if (processedStockNames.contains(stockName)) {
                                    continue;
                                }
                                processedStockNames.add(stockName);


                                // Redis에 저장 (stockName을 키로 사용)
                                String redisKey = "stock:" + stockName;
                                redisTemplate.opsForHash().put(redisKey, "stockName", stockName);
                                redisTemplate.opsForHash().put(redisKey, "stockCode", stockCode);
                                redisTemplate.opsForHash().put(redisKey, "price", String.valueOf(price));
                                redisTemplate.opsForHash().put(redisKey, "priceChange", priceChange);
                                redisTemplate.opsForHash().put(redisKey, "sectorName", sectorName);

                                // Redis 데이터에 TTL(Time-To-Live) 설정 (예: 1일)
                                redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
                            }
                        }
                    }
                }

            } catch (HttpServerErrorException e) {
                if (e.getMessage().contains("초당 거래건수를 초과")) {
                    System.err.println("API 요청 제한 초과: " + e.getMessage());
                    try {
                        Thread.sleep(1000 * 60); // 1분 대기
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.err.println("API 호출 중 예외 발생: " + e.getMessage());
                }
            } catch (Exception e) {
                System.err.println("주식 데이터를 가져오는 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private static final String API_URL = "https://finance.daum.net/api/trend/market_capitalization?page={page}&perPage=30&fieldName=marketCap&order=desc&market={market}&pagination=true";

    public void fetchAndSaveStockRank() {
        String[] markets = {"KOSPI", "KOSDAQ"}; // 시장 배열

        for (String market : markets) {
            try {
                int totalPages = 1;

                HttpHeaders headers = createHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                String firstPageUrl = API_URL.replace("{market}", market).replace("{page}", "1");

                ResponseEntity<Map> firstResponse = restTemplate.exchange(firstPageUrl, HttpMethod.GET, entity, Map.class);

                if (firstResponse.getStatusCode() == HttpStatus.OK && firstResponse.getBody() != null) {
                    totalPages = (int) firstResponse.getBody().get("totalPages"); // 전체 페이지 수 가져오기

                    for (int page = 1; page <= totalPages; page++) {
                        String url = API_URL.replace("{market}", market).replace("{page}", String.valueOf(page));
                        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");

                            for (Map<String, Object> item : dataList) {
                                String stockName = (String) item.get("name");  // 주식명
                                String stockCode = (String) item.get("symbolCode"); // 종목 코드
                                Integer rank = (Integer) item.get("rank"); // 순위

                                // 🔥 Redis에 저장 ("stock:이름" -> rank)
                                String redisKey = "stock:" + stockName;
                                if (redisTemplate.hasKey(redisKey)) { // 해당하는 redisKey가 존재할 때만, 추가.
                                    redisTemplate.opsForHash().put(redisKey, "rank", String.valueOf(rank));
                                }

                            }
                        }
                    }
                    System.out.println("시가총액 크롤링 & redis 저장완료.");
                }
            } catch (Exception e) {
                System.err.println("❌ " + market + " 데이터 크롤링 중 오류 발생: " + e.getMessage());
            }
        }
        redisCommon.syncAllStocksToZSetWithScore();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
        headers.set("Referer", "https://finance.daum.net/domestic/market_cap");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.set("Cache-Control", "no-cache");
        headers.set("Pragma", "no-cache");
        return headers;
    }

    // 삭제금지. DB에 기업개요 저장하는 코드. 후에 실행
//    public void fetchStockDetailBatch(Map<String, String> stockDataMap) {
//        if (stockDataMap == null || stockDataMap.isEmpty()) {
//            System.out.println("❌ 크롤링할 주식 데이터가 없습니다.");
//            return;
//        }
//
//        // WebDriver 설정
//        System.setProperty("webdriver.chrome.driver", "stock-module/src/main/resources/chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");  // 성능 최적화
//        WebDriver driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//
//        int cnt = 0;
//        try {
//            for (Map.Entry<String, String> entry : stockDataMap.entrySet()) {
//                String stockName = entry.getKey();
//                String stockCode = entry.getValue();
//
//                String detailUrl = "https://finance.daum.net/quotes/A" + stockCode + "#home";
//                driver.get(detailUrl);
//
//                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//                Actions actions = new Actions(driver);
//
//                String companyDescription = "정보 없음";  // 기본값
//
//                // 🔥 1️⃣ 기업 개요 크롤링 (출처 포함)
//                try {
//                    WebElement companyInfoButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnCompanyInfo")));
//                    actions.moveToElement(companyInfoButton).perform(); // 마우스 오버 (hover)
//
//                    WebElement companyInfoElement = wait.until(
//                            ExpectedConditions.visibilityOfElementLocated(By.id("layerCompanyInfo"))
//                    );
//
//                    companyDescription = companyInfoElement.getText().trim();
//                    System.out.println(stockName + " 기업 개요 크롤링 성공");
//
//                } catch (Exception e) {
//                    System.out.println("❌ 기업 개요 크롤링 실패: " + e.getMessage());
//                }
//
//                // 🔥 2️⃣ MySQL에 저장 또는 업데이트
//                Optional<Company> existingCompany = companyRepository.findByStockCode(stockCode);
//
//                if (existingCompany.isPresent()) {
//                    // 기존 데이터 업데이트
//                    Company company = existingCompany.get();
//                    company.setDescription(companyDescription);
//                    companyRepository.save(company);
//                } else {
//                    // 새 데이터 저장
//                    Company newCompany = new Company(stockCode, stockName, companyDescription);
//                    companyRepository.save(newCompany);
//                }
//
//
//            }
//        } catch (Exception e) {
//            System.err.println("❌ 배치 크롤링 중 오류 발생: " + e.getMessage());
//        } finally {
//            System.out.println("크롤링 끝");
//            driver.quit();
//        }

//    }

}
