package com.pda.community_module.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableCaching
@Profile("prod")
public class RedisSentinelConfig {

    @Value("${spring.redis.sentinel.master}")
    private String sentinelMaster;

    // 콤마로 구분된 Sentinel 노드 리스트 (예: "host1:26379,host2:26379")
    @Value("${spring.redis.sentinel.nodes}")
    private String sentinelNodes;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(sentinelMaster);
        for (String node : sentinelNodes.split(",")) {
            String[] parts = node.split(":");
            String host = parts[0].trim();
            int port = Integer.parseInt(parts[1].trim());
            sentinelConfig.sentinel(host, port);
        }
        sentinelConfig.setPassword(RedisPassword.of(password));
        sentinelConfig.setSentinelPassword(RedisPassword.of(password));

        // LettuceClientConfiguration을 사용하여 RESP2 프로토콜 강제 설정
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder()
                        .protocolVersion(ProtocolVersion.RESP2)
                        .build())
                .build();

        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
