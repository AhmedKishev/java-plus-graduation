package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.producer.KafkaUserActionProducer;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class UserActionHandlerImpl implements UserActionHandler {

    @Value("${topic.user-action}")
    private String topic;

    private final KafkaUserActionProducer kafkaUserActionProducer;

    @Override
    public void handle(UserActionProto userActionProto) {
        var toAvro = UserActionAvro.newBuilder()
                .setEventId(userActionProto.getEventId())
                .setUserId(userActionProto.getUserId())
                .setActionType(toType(userActionProto.getActionType()))
                .setTimestamp((mapTimestampToInstant(userActionProto)))
                .build();

        kafkaUserActionProducer.send(toAvro, mapTimestampToInstant(userActionProto), userActionProto.getEventId(), topic);
    }

    public Instant mapTimestampToInstant(UserActionProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    private ActionTypeAvro toType(ActionTypeProto actionType) {
        switch (actionType) {
            case ACTION_LIKE -> {
                return ActionTypeAvro.LIKE;
            }
            case UNRECOGNIZED -> {
                return null;
            }
            case ACTION_VIEW -> {
                return ActionTypeAvro.VIEW;
            }
            case ACTION_REGISTER -> {
                return ActionTypeAvro.REGISTER;
            }
        }
        return null;
    }
}
