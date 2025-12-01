package ru.practicum.properties;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.Properties;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducerProperties {

    @Value("${spring.kafka.bootstrap-server}")
    String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer}")
    String keySerializer;
    @Value("${spring.kafka.producer.value-serializer}")
    String valueSerializer;

    @Bean
    public Producer<Long, EventSimilarityAvro> getProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        return new KafkaProducer<>(config);
    }
}

