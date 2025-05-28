package com.example.sleeping.global.scheduler;

import com.example.sleeping.data.application.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataCountingScheduler {
    private volatile boolean active = true;
    private final SensorDataService sensorDataService;

    @Scheduled(cron = "0 0 10 * * *")
    public void scheduledWork() {
        if(!active) {
            return;
        }

        sensorDataService.dataCounting();
    }

    public void turnOn() {
        this.active = true;
    }

    public void turnOff() {
        this.active = false;
    }

    public boolean getStatus() {
        return this.active;
    }
}
