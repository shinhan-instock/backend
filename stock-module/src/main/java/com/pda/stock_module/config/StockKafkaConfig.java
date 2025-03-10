package com.pda.stock_module.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StockKafkaConfig {

    @Value("${stock.events.topic.finishedPost}")
    private String finishedPostTopic;
    @Value("${stock.events.topic.rollback}")
    private String rollbackTopic;
    @Value("${stock.commands.topic.setMileage}")
    private String setMileageTopic;

    @Autowired
    private Environment environment;

    private static final Integer TOPIC_PARTITIONS = 3;
    private static final Integer TOPIC_REPLICATION_FACTOR = 3;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, environment.getProperty("spring.kafka.producer.acks"));
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(environment.getProperty("spring.kafka.producer.retries", "10")));
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Integer.parseInt(environment.getProperty("spring.kafka.producer.properties.retry.backoff.ms", "1000")));
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Integer.parseInt(environment.getProperty("spring.kafka.producer.properties.delivery.timeout.ms", "12000")));
        props.put(ProducerConfig.LINGER_MS_CONFIG, Integer.parseInt(environment.getProperty("spring.kafka.producer.properties.linger.ms", "0")));
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, Integer.parseInt(environment.getProperty("spring.kafka.producer.properties.request.timeout.ms", "3000")));
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, Integer.parseInt(environment.getProperty("spring.kafka.producer.properties.max.in.flight.requests.per.connection", "5")));
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.parseBoolean(environment.getProperty("spring.kafka.producer.properties.enable.idempotence", "true")));
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, environment.getProperty("spring.kafka.producer.transaction-id-prefix"));

        return props;
    }


    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic createFinishedPostTopic() {
        return TopicBuilder.name(finishedPostTopic)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic createRollbackTopic() {
        return TopicBuilder.name(rollbackTopic)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic createSetMileageTopic() {
        return TopicBuilder.name(setMileageTopic)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }
}
