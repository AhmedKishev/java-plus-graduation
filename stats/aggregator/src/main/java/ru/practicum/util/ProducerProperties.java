package ru.practicum.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ProducerProperties {
    @Value("${spring.kafka.bootstrap-server}")
    String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer}")
    String keySerializer;
    @Value("${spring.kafka.producer.value-serializer}")
    String valueSerializer;
}
