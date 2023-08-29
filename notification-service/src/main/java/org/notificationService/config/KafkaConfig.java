package org.notificationService.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Properties;

/**
 * This class is for setting up Kafka-related beans that are used to interact with Kafka topics and perform messaging operations.
 *
 * @author safwanmohammed907@gmail.com
 */
@Configuration
public class KafkaConfig {
    /**
     * This factory is used to create Kafka consumer instances that can subscribe to Kafka topics and receive messages.
     *
     * @return
     */
    @Bean
    public ConsumerFactory getConsumerFactory() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory(properties);
    }
}
