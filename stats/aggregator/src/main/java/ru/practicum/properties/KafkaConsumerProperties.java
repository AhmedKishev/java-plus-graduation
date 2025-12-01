package ru.practicum.properties;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.util.ConsumerProperties;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerProperties {
    ConsumerProperties consumerProperties;

    @Bean
    public Consumer<Long, UserActionAvro> userActionConsumerProperties() {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerProperties.getUserActionClientId());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getUserActionGroupId());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServer());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                consumerProperties.getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                consumerProperties.getValueDeserializer());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                consumerProperties.isEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }
}
