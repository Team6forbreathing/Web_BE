package com.example.sleeping.user.application;

import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.UserErrorCode;
import com.example.sleeping.user.domain.User;
import com.example.sleeping.user.persistent.UserRepository;
import com.example.sleeping.user.presentation.dto.PassChangeRequest;
import com.example.sleeping.user.presentation.dto.UserRequest;
import com.example.sleeping.user.presentation.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse readUserData(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        return UserResponse.of(user);
    }

    @Transactional
    public void updateUserData(String userId, UserRequest userRequest) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        user.update(userRequest);
    }

    @Transactional
    public void updateUserPassword(String userId, PassChangeRequest passChangeRequest) {
        if(passChangeRequest.currentPassword() == null || passChangeRequest.newPassword() == null) {
            throw CustomException.of(UserErrorCode.PASS_NO_NULL);
        }

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        if(!user.checkPw(passChangeRequest.currentPassword())) {
                throw CustomException.of(UserErrorCode.WRONG_PASSWORD);
        }

        user.updatePw(passChangeRequest.newPassword());
    }

    @Transactional
    public void deleteUserData(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public void verifyPassword(String userId, String password) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        if(!user.checkPw(password)) {
            throw CustomException.of(UserErrorCode.WRONG_PASSWORD);
        }
    }

    @Transactional(readOnly = true)
    public long countUserNumber() {
        User user = userRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> CustomException.of(UserErrorCode.NOT_FOUND));
        return user.getId();
    }

    @Transactional(readOnly = true)
    public LocalDate getLastMeasuredDate(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        return user.getLastMeasured();
    }

    @Transactional
    public void updateMeasuredDate(LocalDate date, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        user.updateMeasureInfo(date);
    }
}
