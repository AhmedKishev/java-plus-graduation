package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer implements AutoCloseable {

    private final Producer<Long, EventSimilarityAvro> producer;

    public void send(List<EventSimilarityAvro> messages, String topic) {
        messages.stream()
                .map(message -> new ProducerRecord<>(topic, null,
                        message.getTimestamp().toEpochMilli(), message.getEventA(), message))
                .forEach((x)->
                {
                    log.info("Отправлено EventSimilarityAvro.");
                    producer.send(x);
                });
    }

    public void flush() {
        producer.flush();
    }

    @Override
    public void close() {
        producer.close(Duration.ofSeconds(10));
    }
}
