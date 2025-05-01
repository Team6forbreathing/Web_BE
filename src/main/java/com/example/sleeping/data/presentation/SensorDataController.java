package com.example.sleeping.data.presentation;

import com.example.sleeping.data.application.AsyncQueueService;
import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.data.application.dto.DataRequest;
import com.example.sleeping.data.presentation.dto.SensorData;
import com.example.sleeping.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorDataController {
    private final AsyncQueueService asyncQueueService;
    private final SensorDataService sensorDataService;

    @PostMapping
    public ResponseEntity<?> createSensorData(
            @RequestBody SensorData sensorData,
            @LoginUser String userId
    ) {
        DataRequest dataRequest = DataRequest.from(userId, sensorData);
        //asyncQueueService.addRequestToQueue(dataRequest);
        sensorDataService.writePpgDataBulk(dataRequest.data().ppgList(), userId);
        sensorDataService.writeAccDataBulk(dataRequest.data().accList(), userId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getSensorData(
            @RequestParam String dataType,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @LoginUser String userId
    ) {
        List<?> dataList = null;

        if(dataList == null) {
            throw new RuntimeException("잘못된 인수 사용");
        }

        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }
}
