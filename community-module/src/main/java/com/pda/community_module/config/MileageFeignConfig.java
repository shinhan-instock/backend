package com.pda.community_module.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MileageFeignConfig {  // 기존 FeignConfig 건들지 않음

    @Bean
    public Decoder mileageFeignDecoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Encoder mileageFeignEncoder() {
        return new JacksonEncoder(); // JSON 직렬화 지원
    }
}
