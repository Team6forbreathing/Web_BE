package com.example.sleeping.data.presentation;

import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.data.presentation.dto.SensorData;
import com.example.sleeping.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @PostMapping
    public ResponseEntity<?> createSensorData(
            @RequestBody SensorData sensorData,
            @LoginUser String userId
    ) {
        sensorDataService.writeAccDataBulk(sensorData.accList(), userId);
        sensorDataService.writePpgDataBulk(sensorData.ppgList(), userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
