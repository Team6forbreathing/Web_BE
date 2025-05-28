package com.example.sleeping.global.scheduler;

import com.example.sleeping.data.application.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataCountingScheduler {
    private final SensorDataService sensorDataService;

    @Scheduled(cron = "0 0 10 * * *")
    public void scheduledWork() {
        sensorDataService.dataCounting();
    }
}
