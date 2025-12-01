package ru.practicum.properties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerProperties {
    @Value("${spring.kafka.consumer.user-action-client-id}")
    String userActionClientId;
    @Value("${spring.kafka.consumer.user-action-group-id}")
    String userActionGroupId;
    @Value("${spring.kafka.bootstrap-server}")
    String bootstrapServer;
    @Value("${spring.kafka.consumer.key-deserializer}")
    String keyDeserializer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    String valueDeserializer;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    boolean enableAutoCommit;

    @Bean
    public Consumer<Long, UserActionAvro> userActionConsumerProperties() {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, userActionClientId);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, userActionGroupId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                valueDeserializer);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                enableAutoCommit);

        return new KafkaConsumer<>(properties);
    }
}
