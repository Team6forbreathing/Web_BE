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

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUserInfos(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::of);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUserInfosForScheduling() {
        return userRepository.findAll().stream().
                map(UserResponse::of).
                toList();
    }

    @Transactional
    public void granting(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        user.grant();
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
