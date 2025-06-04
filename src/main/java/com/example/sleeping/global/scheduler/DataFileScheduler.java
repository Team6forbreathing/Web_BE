package com.example.sleeping.global.scheduler;

import com.example.sleeping.admin.application.AdminService;
import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataFileScheduler {
    private volatile boolean active = true;
    private final SensorDataService sensorDataService;
    private final UserService userService;
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
            userService.updateMeasuredDate(LocalDate.now(), userId);
        }

        log.info("데이터 파일화 스케쥴링 동작 완료 : " + LocalDateTime.now());
    }

    public void scheduledWork(LocalDate target) throws IOException {
        if(!active) {
            return;
        }

        List<String> userIds = adminService.getAllUserInfosForScheduling()
                .stream()
                .map(UserResponse::userId)
                .toList();

        for (String userId : userIds) {
            sensorDataService.generateFilesForDate(target, userId);
        }

        log.info("데이터 파일화 스케쥴링 동작 완료 : " + LocalDateTime.now());
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
