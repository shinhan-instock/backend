//package com.pda.community_module.service;
//
//import com.pda.community_module.web.dto.GPTRequestDTO;
//import com.pda.community_module.web.dto.GPTResponseDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SentimentServiceImpl implements SentimentService{
//    private final RestTemplate restTemplate;
//    @Value("${openai.api.key}")
//    private String openaiApiKey;
//    @Override
//    public Long analyzeSentiment(String content) {
//        String prompt = "아래 문장의 감정 점수를 뽑으세요(100점 만점) (정수만 출력할것)\n" +
//                "예시 1. 입력 : 드디어 오른다 / 출력 : 98\n" +
//                "예시 2. 입력 : 드디어 시발!! / 출력 : 100\n" +
//                "예시 3. 입력 : 꽉잡아 / 출력 : 80\n" +
//                "예시 4. 입력 : 시발 / 출력 : 10\n" +
//                "예시 5. 입력 : 눈물나 / 출력 : 2\n" +
//                "예시 6. 입력 : 하 시발 / 출력 : 1\n" +
//                "예시 7. 입력 : 하 좆됐다 / 출력 : 1\n" +
//                "예시 8. 입력 : 와 좆되네 씨발 ㅋㅋ / 출력 : 80\n" +
//                "예시 9. 입력 : 이 씨발 ㅋㅋ / 출력 : 20\n" +
//                "예시 10. 입력 : 아 씨발 ㅋㅋ / 출력 : 20\n\n" +
//                "### 감정 점수 뽑을 문장\n" +
//                content;
//
//        GPTRequestDTO.Message systemMessage = new GPTRequestDTO.Message("system", "You are a sentiment analyzer.");
//        GPTRequestDTO.Message userMessage = new GPTRequestDTO.Message("user", prompt);
//        GPTRequestDTO request = GPTRequestDTO.builder()
//                .model("gpt-3.5-turbo")
//                .messages(List.of(systemMessage, userMessage))
//                .maxTokens(50)
//                .build();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + openaiApiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<GPTRequestDTO> entity = new HttpEntity<>(request, headers);
//
//        ResponseEntity<GPTResponseDTO> response = restTemplate.postForEntity(
//                "https://api.openai.com/v1/chat/completions", entity, GPTResponseDTO.class);
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            String resultText = response.getBody().getChoices().get(0).getMessage().getContent().trim();
//            try {
//                return Long.parseLong(resultText);
//            } catch (NumberFormatException e) {
//                throw new RuntimeException("Failed to parse sentiment score from GPT response: " + resultText, e);
//            }
//        } else {
//            throw new RuntimeException("GPT API call failed with status: " + response.getStatusCode());
//        }
//    }
//}
