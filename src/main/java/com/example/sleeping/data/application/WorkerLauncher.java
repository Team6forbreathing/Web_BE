package com.example.sleeping.data.application;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerLauncher implements ApplicationListener<ApplicationReadyEvent> {
    private final AsyncQueueService asyncQueueService;
    private static final int MAX_POOL_SIZE = 2;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            asyncQueueService.processRequestsFromQueue(i); // @Async 정상 작동
        }
    }
}