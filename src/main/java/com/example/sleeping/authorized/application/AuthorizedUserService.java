package com.example.sleeping.authorized.application;

import com.example.sleeping.admin.presentation.dto.UserResponse;
import com.example.sleeping.user.persistent.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizedUserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUserInfos(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }
}
