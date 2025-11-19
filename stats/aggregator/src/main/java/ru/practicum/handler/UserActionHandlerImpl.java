package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserActionHandlerImpl implements UserActionHandler {

    /// вклад пользователей по событию
    private final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();

    /// суммарный показатель по событию
    private final Map<Long, Double> eventWeights = new HashMap<>();

    /// пространство событий
    private final Map<Long, Map<Long, Double>> minWeightsSum = new HashMap<>();

    @Value("${application.action-weight.view}")
    private float view;
    @Value("${application.action-weight.register}")
    private float register;
    @Value("${application.action-weight.like}")
    private float like;

    public List<EventSimilarityAvro> calculateSimilarity(UserActionAvro userActionAvro) {
        List<EventSimilarityAvro> eventSimilarity = new ArrayList<>();
        Long userId = userActionAvro.getUserId();
        Long eventId = userActionAvro.getEventId();
        log.info("Расчитываем сообщение " + userActionAvro);
        double diff = updateEventAction(userActionAvro);
        if (diff > 0) {
            eventWeights.merge(eventId, diff, Double::sum);

            Set<Long> anotherEvents = eventActions.keySet().stream()
                    .filter(id -> !Objects.equals(id, eventId))
                    .collect(Collectors.toSet());
            log.info("Расчитываем схожесть с " + anotherEvents.size());
            for (Long ae : anotherEvents) {
                double result = getMinWeightsSum(eventId, ae, diff, userId) /
                        (Math.sqrt(eventWeights.get(eventId)) * Math.sqrt(eventWeights.get(ae)));

                if (result > 0.0) {
                    eventSimilarity.add(EventSimilarityAvro.newBuilder()
                            .setEventA(Math.min(eventId, ae))
                            .setEventB(Math.max(eventId, ae))
                            .setScore(result)
                            .setTimestamp(Instant.now())
                            .build());
                }
            }
        }
        return eventSimilarity;
    }

    private Double updateEventAction(UserActionAvro userActionAvro) {
        Long eventId = userActionAvro.getEventId();
        Long userId = userActionAvro.getUserId();
        Double result = Double.valueOf(getActionType(userActionAvro.getActionType()));

        Map<Long, Double> userActions = eventActions.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userActions.get(userId);

        if (oldWeight == null || result > oldWeight) {

            userActions.put(userId, result);
            eventActions.put(eventId, userActions);

            double diff = oldWeight == null ? result : result - oldWeight;
            return diff;
        }
        return 0.0;
    }

    private Double getMinWeightsSum(Long eventA, Long eventB, Double diff, Long userId) {
        Long first = Math.min(eventA, eventB);
        Long second = Math.max(eventA, eventB);
        Double weightA = eventActions.get(eventA).get(userId);
        Double weightB = eventActions.get(eventB).get(userId);
        if (weightB == null) {
            return 0.0;
        }

        Map<Long, Double> innerMap = minWeightsSum.computeIfAbsent(first, k -> new HashMap<>());
        Double weight = innerMap.get(second);
        if (weight == null) {
            weight = calculateMinWeightSum(eventA, eventB);
            innerMap.put(eventB, weight);
            minWeightsSum.put(first, innerMap);
            return weight;
        }

        double newWeight;
        if (weightA > weightB && (weightA - diff) < weightB) {
            newWeight = weight + (weightB - (weightA - diff));
        } else if (weightA <= weightB) {
            newWeight = weight + diff;
        } else {
            return weight;
        }
        minWeightsSum.get(first).put(second, newWeight);
        return newWeight;
    }

    private Double calculateMinWeightSum(Long eventA, Long eventB) {
        List<Double> weights = new ArrayList<>();
        Map<Long, Double> userActionsA = eventActions.get(eventA);
        Map<Long, Double> userActionsB = eventActions.get(eventB);
        userActionsA.forEach((aUser, aWeight) -> {
            if (userActionsB.containsKey(aUser)) {
                weights.add(Math.min(aWeight, userActionsB.get(aUser)));
            }
        });
        if (weights.isEmpty()) {
            return 0.0;
        }
        return weights.stream().mapToDouble(Double::doubleValue).sum();
    }

    private Float getActionType(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case VIEW -> view;
            case REGISTER -> register;
            case LIKE -> like;
        };
    }
}
