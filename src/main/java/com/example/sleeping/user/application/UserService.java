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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    
    // 유저 정보 읽기
    @Transactional(readOnly = true)
    public UserResponse readUserData(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        return UserResponse.of(user);
    }
    
    // 유저 정보 업데이트 하기
    @Transactional
    public void updateUserData(String userId, UserRequest userRequest) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        user.update(userRequest);
    }
    
    // 유저 비밀번호 업데이트 하기
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
    
    // 유저 정보 삭제하기
    @Transactional
    public void deleteUserData(String userId) {
        userRepository.deleteByUserId(userId);
    }
    
    // 비밀번호 검증
    @Transactional(readOnly = true)
    public void verifyPassword(String userId, String password) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        if(!user.checkPw(password)) {
            throw CustomException.of(UserErrorCode.WRONG_PASSWORD);
        }
    }
    
    // 유저 가입수 조회
    @Transactional(readOnly = true)
    public long countUserNumber() {
        User user = userRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> CustomException.of(UserErrorCode.NOT_FOUND));
        return user.getId();
    }
    
    // 마지막으로 측정한 데이터 조회
    @Transactional(readOnly = true)
    public LocalDate getLastMeasuredDate(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        return user.getLastMeasured();
    }
    
    // 마지막으로 측정한 데이터 업데이트
    @Transactional
    public void updateMeasuredDate(LocalDate date, String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        user.updateMeasureInfo(date);
    }
    
    // 어드민 동작
    // 모든 유저의 정보를 조회 (관리자 페이지에서 사용)
    @Transactional(readOnly = true)
    public Page<com.example.sleeping.admin.presentation.dto.UserResponse> getAllUserInfos(Pageable pageable) {
        return userRepository.findAll(pageable)
                   .map(com.example.sleeping.admin.presentation.dto.UserResponse::from);
    }
    
    // 모든 유저의 정보를 조회 (스케줄러에서 사용 )
    @Transactional(readOnly = true)
    public List<com.example.sleeping.admin.presentation.dto.UserResponse> getAllUserInfos() {
        return userRepository.findAll().stream()
                   .map(com.example.sleeping.admin.presentation.dto.UserResponse::from)
                   .toList();
    }
    
    // 사용자 인가
    @Transactional
    public void granting(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );
        
        user.grant();
    }
    
    // 사용자 삭제
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
