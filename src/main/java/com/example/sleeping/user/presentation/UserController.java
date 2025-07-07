package com.example.sleeping.user.presentation;

import com.example.sleeping.global.annotation.LoginUser;
import com.example.sleeping.global.dto.Message;
import com.example.sleeping.user.application.UserService;
import com.example.sleeping.user.presentation.dto.PassChangeRequest;
import com.example.sleeping.user.presentation.dto.UserRequest;
import com.example.sleeping.user.presentation.dto.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    ObjectMapper snakeMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    
    // 자신의 유저 데이터 조회
    @GetMapping("/info")
    public ResponseEntity<?> readMyUserData(
            @LoginUser String userId
    ) {
        UserResponse userResponse = userService.readUserData(userId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    
    // 자신의 유저 데이터 업데이트
    @PatchMapping("/update")
    public ResponseEntity<?> updateMyUserData(
            @LoginUser String userId,
            @RequestBody String data
    ) throws JsonProcessingException {
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        userService.updateUserData(userId, userRequest);

        return new ResponseEntity<>(
                Message.of("User information updated successfully"),
                HttpStatus.OK
        );
    }
    
    // 자신의 비밀번호 업데이트
    @PatchMapping("/password")
    public ResponseEntity<?> updateMyUserPassword(
            @LoginUser String userId,
            @RequestBody String data
    ) throws JsonProcessingException {
        PassChangeRequest passChangeRequest = snakeMapper.readValue(data, PassChangeRequest.class);
        userService.updateUserPassword(userId, passChangeRequest);

        return new ResponseEntity<>(
                Message.of("Password updated successfully"),
                HttpStatus.OK
        );
    }
    
    // 유저 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyUserData(
            @LoginUser String userId
    ) {
        userService.deleteUserData(userId);

        return new ResponseEntity<>(
                Message.of("Account deleted successfully"),
                HttpStatus.NO_CONTENT
        );
    }
    
    // 비밀번호 검증
    @PostMapping("/pw_verify")
    public ResponseEntity<?> passwordVerifying(
            @LoginUser String userId,
            @RequestBody String data
    ) throws JsonProcessingException {
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        userService.verifyPassword(userId, userRequest.userPw());

        return new ResponseEntity<>(
                Message.of("Password is Correct!"),
                HttpStatus.OK
        );
    }
    
    // 유저 가입수 조회
    @GetMapping("/count")
    public ResponseEntity<?> userCount() {
        long count = userService.countUserNumber();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
