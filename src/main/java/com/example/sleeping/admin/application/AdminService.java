package com.example.sleeping.admin.application;

import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.UserErrorCode;
import com.example.sleeping.user.domain.User;
import com.example.sleeping.user.persistent.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    // 모든 유저의 정보를 조회 (관리자 페이지에서 사용)
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUserInfos(Pageable pageable) {
        return userRepository.findAll(pageable)
                   .map(UserResponse::from);
    }
    
    // 모든 유저의 정보를 조회 (스케줄러에서 사용 )
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUserInfos() {
        return userRepository.findAll().stream()
                   .map(UserResponse::from)
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
