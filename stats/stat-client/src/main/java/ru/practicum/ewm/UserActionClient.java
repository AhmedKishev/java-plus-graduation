package ru.practicum.ewm;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.grpc.stats.controller.UserActionControllerGrpc;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;

import java.time.Instant;

@Service
public class UserActionClient {
    private final UserActionControllerGrpc.UserActionControllerBlockingStub userActionStub;

    public UserActionClient(@GrpcClient("collector") UserActionControllerGrpc.UserActionControllerBlockingStub client) {
        this.userActionStub = client;
    }

    public void collectUserAction(Long eventId, Long userId, ActionTypeProto type, Instant instant) {
        UserActionProto request = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(type)
                .setTimestamp(mapToTimestamp(instant))
                .build();

        userActionStub.collectUserAction(request);
    }

    private Timestamp mapToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
