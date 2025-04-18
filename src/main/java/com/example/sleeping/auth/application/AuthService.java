package com.example.sleeping.auth.application;

import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.user.domain.User;
import com.example.sleeping.user.persistent.UserRepository;
import com.example.sleeping.user.presentation.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public void register(UserRequest userRequest) {
        if(userRequest.userId() == null || userRequest.userPw() == null || userRequest.userName() == null) {
            throw CustomException.of(AuthErrorCode.INVALID_DATA_FIELD);
        }
        if(userRepository.existsUserByUserId(userRequest.userId())) {
            throw CustomException.of(AuthErrorCode.DUPLICATION);
        }

        User user = User.of(userRequest);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(UserRequest userRequest) {
        User user = userRepository.findByUserId(userRequest.userId()).orElseThrow(
                () -> CustomException.of(AuthErrorCode.NOT_FOUND)
        );

        if(!user.checkPw(userRequest.userPw())) {
            throw CustomException.of(AuthErrorCode.WRONG_PW);
        }
    }
}
