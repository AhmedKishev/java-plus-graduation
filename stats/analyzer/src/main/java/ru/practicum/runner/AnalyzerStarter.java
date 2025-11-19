package ru.practicum.runner;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.processors.EventSimilarityProcessor;
import ru.practicum.processors.UserActionProcessor;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerStarter implements CommandLineRunner {
    EventSimilarityProcessor eventSimilarityProcessor;
    UserActionProcessor userActionProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread userAcionThread = new Thread(userActionProcessor);
        userAcionThread.start();

        eventSimilarityProcessor.run();
    }
}
