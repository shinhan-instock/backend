package com.pda.stock_module.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private String cachedAccessToken;
    private LocalDateTime tokenExpiryTime;

    private static final String TOKEN_URL = "https://openapi.koreainvestment.com:9443/oauth2/tokenP";
    private static final String APP_KEY = "PSmic2utctqoKIE7dcvyimSs10PMRE8s9vDf";
    private static final String APP_SECRET = "oc4cFZw/KQ41wnkQNy+4GXERZbsu5Tw0yqGCtITiVNxqN3pqurJJpO08KLdOPBraK5tyA6UzULoS7EMGoOoXNsyR3FpHSLa746wr1gFf5kstd9HOEtvGOndkmywr/bJ0xAWylyFKMAyE0jMSupDAbCtvfkPpL5FGWH4oLM8Xuyo3WQuk66c=";

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Access Token을 반환하며, 만료된 경우 새로 갱신.
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            return cachedAccessToken;
        }
        return requestNewAccessToken();
    }

    /**
     * 토큰 유효성 검사
     */
    private boolean isTokenValid() {
        return cachedAccessToken != null && tokenExpiryTime != null && LocalDateTime.now().isBefore(tokenExpiryTime);
    }

    /**
     * 새로운 Access Token 요청
     */
    private String requestNewAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", APP_KEY,
                "appsecret", APP_SECRET
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            cachedAccessToken = (String) responseBody.get("access_token");
            int expiresInSeconds = (int) responseBody.get("expires_in");

            // 만료 시간 설정
            tokenExpiryTime = LocalDateTime.now().plus(expiresInSeconds, ChronoUnit.SECONDS);
        }

        return cachedAccessToken;
    }
}