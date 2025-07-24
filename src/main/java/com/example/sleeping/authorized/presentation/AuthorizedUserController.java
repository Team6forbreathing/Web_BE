package com.example.sleeping.authorized.presentation;

import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.data.application.SensorDataService;
import com.example.sleeping.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/authUser")
@RequiredArgsConstructor
public class AuthorizedUserController {
    private final UserService userService;
    private final SensorDataService sensorDataService;
    
    // 유저 정보 조회
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfos(
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<UserResponse> userResponses = userService.getAllUserInfos(pageable);

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }
    
    // 특정 유저의 특정 기간 동안의 센서 데이터 파일을 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSensorDataFileList(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable String userId
    ) {
        List<List<String>> fileNameList = sensorDataService.readDataFileNameList(startDate, endDate, userId);
        return new ResponseEntity<>(fileNameList, HttpStatus.OK);
    }
    
    // 특정 날짜의 특정 센서 데이터 파일을 다운로드
    @GetMapping("/user/{userId}/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("file") String filename,
            @PathVariable String userId
    ) throws IOException {
        Resource resource = sensorDataService.getFileForDownload(date, userId, filename);

        String contentType = "application/octat-stream; charset=utf-8";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
    
    // 특정 유저의 특정 날짜 기록에 특정 파일을 저장
    @PostMapping("/user/{userId}/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestPart("file") MultipartFile multipartFile,
            @PathVariable String userId
    ) throws IOException {
        sensorDataService.uploadFile(date, userId, multipartFile);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
