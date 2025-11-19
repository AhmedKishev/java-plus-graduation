package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;

public class UserActionMapper {
    public static UserAction toEntity(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .mark(getMark(userActionAvro.getActionType()))
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .timestamp(userActionAvro.getTimestamp())
                .build();

    }

    public static float getMark(ActionTypeAvro actionType) {
        switch (actionType) {
            case REGISTER -> {
                return 0.8f;
            }
            case VIEW -> {
                return 0.4f;
            }
            case LIKE -> {
                return 1.0f;
            }
        }
        return 0.0f;
    }
}
