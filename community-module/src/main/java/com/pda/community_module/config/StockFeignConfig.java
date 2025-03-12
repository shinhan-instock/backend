package com.pda.community_module.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockFeignConfig {
    @Bean
    public Decoder StockFeignDecoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Encoder StockFeignEncoder() {
        return new JacksonEncoder(); // JSON 직렬화 지원
    }
}
