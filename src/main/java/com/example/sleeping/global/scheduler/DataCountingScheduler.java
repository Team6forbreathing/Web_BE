package com.example.sleeping.global.scheduler;

import com.example.sleeping.data.application.SensorDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
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

        log.info("데이터 집계 스케쥴링 동작 완료 : " + LocalDateTime.now());
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
