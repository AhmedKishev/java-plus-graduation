package ru.practicum.properties;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;


@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final Environment environment;


    @Bean
    public KafkaConsumer<Long, UserActionAvro> userActionConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-server"));
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, environment.getProperty("spring.kafka.consumer.user-action-client-id"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.user-action-group-id"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.key-deserializer"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.value-deserializer"));
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, environment.getProperty("spring.kafka.consumer.enable-auto-commit", "false"));
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, environment.getProperty("spring.kafka.consumer.poll-timeout", "1000"));

        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<Long, EventSimilarityAvro> eventSimilarityAvroKafkaConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-server"));
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, environment.getProperty("spring.kafka.consumer.similarity-client-id"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.similarity-group-id"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.key-deserializer"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.similarity-deserializer"));
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, environment.getProperty("spring.kafka.consumer.enable-auto-commit", "false"));
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, environment.getProperty("spring.kafka.consumer.poll-timeout", "1000"));

        return new KafkaConsumer<>(properties);
    }


}
