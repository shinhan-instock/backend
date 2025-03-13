package com.pda.community_module.service;

import com.pda.community_module.web.dto.GPTRequestDTO;
import com.pda.community_module.web.dto.GPTResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService{
    private final RestTemplate restTemplate;
    @Value("${openai.api.key}")
    private String openaiApiKey;
    @Override
    public Long analyzeSentiment(String content) {
        String prompt = "아래 문장의 감정 점수를 뽑으세요(100점 만점) (정수만 출력할것)\n" +
                "예시 1. 입력 : 드디어 오른다 / 출력 : 98\n" +
                "예시 2. 입력 : 드디어 시발!! / 출력 : 100\n" +
                "예시 3. 입력 : 꽉잡아 / 출력 : 80\n" +
                "예시 4. 입력 : 시발 / 출력 : 10\n" +
                "예시 5. 입력 : 눈물나 / 출력 : 2\n" +
                "예시 6. 입력 : 하 시발 / 출력 : 1\n" +
                "예시 7. 입력 : 하 좆됐다 / 출력 : 1\n" +
                "예시 8. 입력 : 와 좆되네 씨발 ㅋㅋ / 출력 : 80\n" +
                "예시 9. 입력 : 이 씨발 ㅋㅋ / 출력 : 20\n" +
                "예시 10. 입력 : 아 씨발 ㅋㅋ / 출력 : 20\n\n" +
                "예시 11. 입력 : 개추 / 출력 : 95\n\n" +
                "예시 12. 입력 : 개추천ㅋㅋ / 출력 : 95\n\n" +
                "예시 13. 입력 : ㅁㅊ 떡상ㅋㅋ / 출력 : 95\n\n" +
                "예시 14. 입력 : 떡락할 줄 알았는데 반등 개꿀 ㅋㅋ / 출력 : 90\n" +
                "예시 15. 입력 : 하 씨발 개같네 / 출력 : 1\n" +
                "예시 16. 입력 : 물렸다... 어떡하냐 / 출력 : 11\n" +
                "예시 17. 입력 : 존버했더니 기적이네 / 출력 : 92\n" +
                "예시 18. 입력 : 하락장이네..ㅠ / 출력 : 20\n" +
                "예시 19. 입력 : 기관들 또 던지네 ㅋㅋ / 출력 : 30\n" +
                "예시 20. 입력 : 개미들 개패네 ㅋㅋ / 출력 : 40\n" +
                "예시 21. 입력 : 와 드디어 본전이다 ㅠㅠ / 출력 : 60\n" +
                "예시 22. 입력 : 미친... 따상 가냐? / 출력 : 100\n" +
                "예시 23. 입력 : 설거지 당했다 ㅋㅋ / 출력 : 5\n" +
                "예시 24. 입력 : 씨발... 또 물렸다 / 출력 : 1\n" +
                "예시 25. 입력 : 저항선 뚫으면 개떡상 / 출력 : 95\n" +
                "예시 26. 입력 : 이거 왜 이러냐? / 출력 : 30\n"+
                "### 감정 점수 뽑을 문장\n" +
                content;

        GPTRequestDTO.Message systemMessage = new GPTRequestDTO.Message("system", "You are a sentiment analyzer.");
        GPTRequestDTO.Message userMessage = new GPTRequestDTO.Message("user", prompt);
        GPTRequestDTO request = GPTRequestDTO.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(systemMessage, userMessage))
                .maxTokens(50)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GPTRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GPTResponseDTO> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", entity, GPTResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String resultText = response.getBody().getChoices().get(0).getMessage().getContent().trim();
            // 정규 표현식을 사용해 숫자만 추출
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(resultText);
            if (matcher.find()) {
                return Long.parseLong(matcher.group());
            } else {
                throw new RuntimeException("Failed to parse sentiment score from GPT response: " + resultText);
            }
        } else {
            throw new RuntimeException("GPT API call failed with status: " + response.getStatusCode());
        }
    }
}
