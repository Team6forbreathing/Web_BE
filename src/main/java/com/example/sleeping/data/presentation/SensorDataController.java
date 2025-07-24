package com.example.sleeping.data.presentation;

import com.example.sleeping.data.application.AsyncQueueService;
import com.example.sleeping.data.application.SensorDataFacade;
import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.data.application.dto.DataRequest;
import com.example.sleeping.data.presentation.dto.SensorData;
import com.example.sleeping.global.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorDataController {
    private final AsyncQueueService asyncQueueService;
    private final SensorDataService sensorDataService;
    private final SensorDataFacade sensorDataFacade;

    // 센서 데이터 받아서 influxDB에 저장
    @PostMapping
    public ResponseEntity<?> createSensorData(
            @RequestBody SensorData sensorData,
            @RequestAttribute("userId") String userId
    ) {
        DataRequest dataRequest = DataRequest.from(userId, sensorData);
        asyncQueueService.addRequestToQueue(dataRequest);

        return new ResponseEntity<>(
                Message.of("sending successfully"),
                HttpStatus.CREATED
        );
    }

    // 특정 기간 동안의 센서 데이터 리스트 조회
    @GetMapping
    public ResponseEntity<?> getSensorDataFileList(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute("userId") String userId
    ) {
        List<List<String>> fileNameList = sensorDataService.readDataFileNameList(startDate, endDate, userId);
        return new ResponseEntity<>(fileNameList, HttpStatus.OK);
    }
    
    // 특정 날짜, 특정 이름의 파일 다운로드
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("file") String filename,
            @RequestAttribute("userId") String userId
    ) throws IOException {
        Resource resource = sensorDataService.getFileForDownload(date, userId, filename);

        String contentType = "application/octat-stream; charset=utf-8";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
    
    // 파일의 개수 조회
    @GetMapping("/count")
    public ResponseEntity<?> getFileCount() {
        Long count = sensorDataService.getFileCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    
    // 최근 측정한 데이터 조회
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentMeasureData(
        @RequestAttribute("userId") String userId
    ) {
        List<String> fileList = sensorDataFacade.getRecentData(userId);
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }
}
