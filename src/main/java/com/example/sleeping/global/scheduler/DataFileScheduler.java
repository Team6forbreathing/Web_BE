package com.example.sleeping.global.scheduler;

import com.example.sleeping.admin.application.AdminService;
import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.data.application.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataFileScheduler {
    private volatile boolean active = true;
    private final SensorDataService sensorDataService;
    private final AdminService adminService;

    @Scheduled(cron = "0 30 10 * * *")
    public void scheduledWork() throws IOException {
        if(!active) {
            return;
        }

        List<String> userIds = adminService.getAllUserInfosForScheduling()
                .stream()
                .map(UserResponse::userId)
                .toList();

        for (String userId : userIds) {
            sensorDataService.generateFilesForDate(LocalDate.now().minusDays(1), userId);
        }
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
