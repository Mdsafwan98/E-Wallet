package org.userService.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.Properties;

/**
 * This class is for setting up Kafka-related beans that are used to interact with Kafka topics and perform messaging operations.
 *
 * @author safwanmohammed907@gmail.com
 */
@Configuration
public class KafkaConfig {
    /**
     * This factory is used to create Kafka producer instances that can send messages to Kafka topics.
     *
     * @return
     */
    @Bean
    ProducerFactory getProducerFactory() {
        Properties properties = new Properties();
        //server config
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //key of string type
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //value of string type
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory(properties);
    }

    /**
     * This method simplifies the interaction with Kafka topics by providing methods for sending messages.
     *
     * @return
     */
    @Bean
    KafkaTemplate<String, String> getKafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }
}
