package ru.practicum.ewm;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.grpc.stats.controller.RecommendationsControllerGrpc;
import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class RecommendationsClient {

    private final RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public RecommendationsClient(@GrpcClient("analyzer") RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client) {
        this.client = client;
    }

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = client.getRecommendationsForUser(request);

        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = client.getSimilarEvents(request);

        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        Iterator<RecommendedEventProto> iterator = client.getInteractionsCount(request);

        return asStream(iterator);
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}