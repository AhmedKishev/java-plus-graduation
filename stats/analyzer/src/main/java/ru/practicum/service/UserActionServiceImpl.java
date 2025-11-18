package ru.practicum.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class UserActionServiceImpl implements UserActionService {
    UserActionRepository userActionRepository;


    @Override
    public void handle(UserActionAvro userActionAvro) {
        float newMark = UserActionMapper.getMark(userActionAvro.getActionType());

        if (!userActionRepository.existsByEventIdAndUserId(userActionAvro.getEventId(), userActionAvro.getUserId())) {
            userActionRepository.save(UserActionMapper.toEntity(userActionAvro));
        } else {
            UserAction userAction = userActionRepository.findByEventIdAndUserId(userActionAvro.getEventId(), userActionAvro.getUserId());
            if (userAction.getMark() < newMark) {
                userAction.setMark(newMark);
                userAction.setTimestamp(userActionAvro.getTimestamp());
                userActionRepository.save(userAction);
            }
        }
    }


}
