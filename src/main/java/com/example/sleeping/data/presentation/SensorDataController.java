package com.example.sleeping.data.presentation;

import com.example.sleeping.data.application.AsyncQueueService;
import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.data.application.dto.DataRequest;
import com.example.sleeping.data.presentation.dto.SensorData;
import com.example.sleeping.global.annotation.LoginUser;
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

    @PostMapping
    public ResponseEntity<?> createSensorData(
            @RequestBody SensorData sensorData,
            @LoginUser String userId
    ) {
        DataRequest dataRequest = DataRequest.from(userId, sensorData);
        asyncQueueService.addRequestToQueue(dataRequest);

        return new ResponseEntity<>(
                Message.of("sending successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getSensorDataFileList(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @LoginUser String userId
    ) throws IOException {
        List<List<String>> fileNameList = sensorDataService.readDataFileNameList(startDate, endDate, userId);
        return new ResponseEntity<>(fileNameList, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("file") String filename,
            @LoginUser String userId
    ) throws IOException {
        Resource resource = sensorDataService.getFileForDownload(date, userId, filename);

        String contentType = "application/octat-stream; charset=utf-8";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/set")
    public ResponseEntity<?> makefile(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @LoginUser String userId
    ) throws IOException {
        sensorDataService.generateFilesForDate(date, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/count")
    public ResponseEntity<?> countingFile() {
        sensorDataService.dataCounting();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getFileCount() {
        Long count = sensorDataService.getFileCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
