package ru.practicum.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class AggregatorServiceProperties {

    @Value("${aggregator.topic.user-action}")
    String topicUserAction;
    @Value("${aggregator.topic.events-similarity}")
    String topicEventSimilarity;
    @Value("${spring.kafka.consumer.poll-timeout}")
    int pollTimeout;

}
