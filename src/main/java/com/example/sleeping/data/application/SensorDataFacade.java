package com.example.sleeping.data.application;

import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.example.sleeping.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorDataFacade {
    private final InfluxDbService influxDbService;
    private final FileCudService fileCudService;
    private final FileReadService fileReadService;
    private final UserService userService;

    public List<String> getRecentData(String userId) {
        LocalDate date = userService.getLastMeasuredDate(userId);
        return fileReadService.findFileByUserIdAndDate(userId, date);
    }
    
    public List<List<String>> readDataFileNameList(LocalDate start, LocalDate end, String userId) {
        return fileReadService.readDataFileNameList(start, end, userId);
    }
    
    
    public Resource getFileForDownload(LocalDate date, String userId, String filename) throws IOException {
        return fileReadService.getFileForDownload(date, userId, filename);
    }
    
    public Long getFileCount() {
        return fileReadService.getFileCount();
    }
    
    // 인가 사용자 기능
    public void uploadFile(LocalDate date, String userId, MultipartFile multipartFile) throws IOException {
        fileCudService.uploadFile(date, userId, multipartFile);
    }
    
    // 스케줄러
    public void dataCounting() {
        fileCudService.dataCounting();
    }
    
    public boolean generateFilesForDate(LocalDate target, String userId) throws IOException {
        List<AccMeasurement> accMeasurements = influxDbService.queryByOneUnitAcc(target, userId);
        List<PpgMeasurement> ppgMeasurements = influxDbService.queryByOneUnitPpg(target, userId);
        
        return fileCudService.generateFilesForDate(
            accMeasurements, ppgMeasurements, target, userId
        );
    }
}
