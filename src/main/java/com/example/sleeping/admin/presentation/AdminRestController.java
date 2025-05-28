package com.example.sleeping.admin.presentation;

import com.example.sleeping.admin.application.AdminFacade;
import com.example.sleeping.admin.application.AdminService;
import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.global.annotation.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
    private final AdminService adminService;
    private final AdminFacade adminFacade;

    @GetMapping
    public ResponseEntity<?> getUserInfos(
            @AdminUser String userId,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<UserResponse> userResponses = adminService.getAllUserInfos(pageable);

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> granting(
            @AdminUser String admin,
            @PathVariable Long id
    ) {
        adminService.granting(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUSer(
            @AdminUser String admin,
            @PathVariable Long id
    ) {
        adminService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/scheduler/file")
    public ResponseEntity<?> getFileSchedulerStatus(
            @AdminUser String admin
    ) {
        boolean status = adminFacade.getDataFileSchedulerStatus();

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/scheduler/file")
    public ResponseEntity<?> changeFileSchedulerStatus(
            @AdminUser String admin
    ) {
        adminFacade.changeDataFileScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("scheduler/count")
    public ResponseEntity<?> getCountSchedulerStatus(
            @AdminUser String admin
    ) {
        boolean status = adminFacade.getDataCountingSchedulerStatus();

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/scheduler/count")
    public ResponseEntity<?> changeCountSchedulerStatus(
            @AdminUser String admin
    ) {
        adminFacade.changeDataCountingScheduler();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
