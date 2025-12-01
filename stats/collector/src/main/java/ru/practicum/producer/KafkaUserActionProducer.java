package ru.practicum.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserActionProducer implements AutoCloseable {


    private final Producer<Long, SpecificRecordBase> producer;

    public void send(SpecificRecordBase message, Instant timestamp, Long eventId, String topic) {

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(topic, null,
                timestamp.toEpochMilli(), eventId, message);


        log.info("Отправка следующих данных: \n" + record.value());
        producer.send(record);
        producer.flush();
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }

}
