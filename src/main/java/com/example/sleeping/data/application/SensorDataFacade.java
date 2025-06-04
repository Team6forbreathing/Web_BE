package com.example.sleeping.data.application;

import com.example.sleeping.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorDataFacade {
    private final SensorDataService sensorDataService;
    private final UserService userService;

    public List<String> getRecentData(String userId) {
        LocalDate date = userService.getLastMeasuredDate(userId);
        return sensorDataService.findFileByUserIdAndDate(userId, date);
    }
}
