package ru.practicum.service;

import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto userPredictionsRequestProto);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);
}
