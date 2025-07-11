package com.example.sleeping.admin.presentation;

import com.example.sleeping.admin.application.AdminFacade;
import com.example.sleeping.admin.application.AdminService;
import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.global.annotation.AdminUser;
import com.example.sleeping.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
    private final AdminService adminService;
    private final AdminFacade adminFacade;
    
    // 모든 유저 정보를 조회
    @GetMapping
    public ResponseEntity<?> getUserInfos(
            @AdminUser String userId,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<UserResponse> userResponses = adminService.getAllUserInfos(pageable);

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }
    
    // 유저 -> 인가 유저로 변경
    @PatchMapping("/{id}")
    public ResponseEntity<?> granting(
            @AdminUser String admin,
            @PathVariable Long id
    ) {
        adminService.granting(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 유저 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUSer(
            @AdminUser String admin,
            @PathVariable Long id
    ) {
        adminService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // 파일화 스케줄러의 상태 조회
    @GetMapping("/scheduler/file")
    public ResponseEntity<?> getFileSchedulerStatus(
            @AdminUser String admin
    ) {
        boolean status = adminFacade.getDataFileSchedulerStatus();

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    
    // 파일화 스케줄러의 상태 변경 (on-off)
    @PostMapping("/scheduler/file")
    public ResponseEntity<?> changeFileSchedulerStatus(
            @AdminUser String admin
    ) {
        adminFacade.changeDataFileScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    // 파일화 스케줄러 강제 실행
    @PostMapping("/scheduler/file/launch")
    public ResponseEntity<?> makeWeeklyFile(
            @AdminUser String userId
    ) throws IOException {
        adminFacade.launchDataFileScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    // 파일 개수 집계 스케줄러 상태 조회
    @GetMapping("scheduler/count")
    public ResponseEntity<?> getCountSchedulerStatus(
            @AdminUser String admin
    ) {
        boolean status = adminFacade.getDataCountingSchedulerStatus();

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    
    // 파일 개수 집계 스케줄러 상태 변경
    @PostMapping("/scheduler/count")
    public ResponseEntity<?> changeCountSchedulerStatus(
            @AdminUser String admin
    ) {
        adminFacade.changeDataCountingScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    // 파일 개수 집계 스케줄러 강제 실행
    @PostMapping("/scheduler/count/launch")
    public ResponseEntity<?> countingFile(
            @AdminUser String admin
    ) {
        adminFacade.launchDataCountingScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
