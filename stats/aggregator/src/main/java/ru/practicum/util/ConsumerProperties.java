package ru.practicum.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ConsumerProperties {

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

}
