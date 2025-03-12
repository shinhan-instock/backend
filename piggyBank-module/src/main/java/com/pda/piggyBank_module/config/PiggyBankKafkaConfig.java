package com.pda.piggyBank_module.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class PiggyBankKafkaConfig {

    @Value("${piggyBank.commands.topic.setMileage}")
    private String setMileageTopic;

    private final static Integer TOPIC_PARTITIONS = 3;
    private final static Integer TOPIC_REPLICATION_FACTOR = 3;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic createSetMileageTopic() {
        return TopicBuilder.name(setMileageTopic)
                .partitions(TOPIC_PARTITIONS)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }
}
