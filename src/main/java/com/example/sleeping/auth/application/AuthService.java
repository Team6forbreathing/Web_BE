package com.example.sleeping.auth.application;

import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.user.application.command.UserCommand;
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

    // 회원 가입
    @Transactional
    public void register(UserCommand userCommand) {
        if(userCommand.userId() == null || userCommand.userPw() == null || userCommand.userName() == null) {
            throw CustomException.of(AuthErrorCode.INVALID_DATA_FIELD);
        }
        if(userRepository.existsUserByUserId(userCommand.userId())) {
            throw CustomException.of(AuthErrorCode.DUPLICATION);
        }

        User user = User.of(userCommand);
        userRepository.save(user);
    }
    
    // 로그인
    @Transactional(readOnly = true)
    public String login(UserRequest userRequest) {
        User user = userRepository.findByUserId(userRequest.userId()).orElseThrow(
                () -> CustomException.of(AuthErrorCode.NOT_FOUND)
        );

        if(!user.checkPw(userRequest.userPw())) {
            throw CustomException.of(AuthErrorCode.WRONG_PW);
        }

        return user.getRole().name();
    }
}
