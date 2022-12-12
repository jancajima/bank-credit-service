package com.nttdata.bankcreditservice.config;


import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Configuration Kafka.
 */
@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    //Method Producer Factory.
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        return new DefaultKafkaProducerFactory<>(properties);
    }

    //Method of Kafka Template.
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    //Method of Topic 1
    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name("TOPIC-DEMO")
                .partitions(1)
                .replicas(1)
                .build();
    }

    //Method of Topic 2
    @Bean
    public NewTopic topic2() {
        return TopicBuilder
                .name("payCredit")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
