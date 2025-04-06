package com.example.sleeping.admin.presentation.dto;

import com.example.sleeping.user.domain.User;

public record UserResponse(
        Long id,
        String userId,
        String userName,
        Boolean isAuthorized
) {
    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserId(),
                user.getName(),
                !user.getRole().name().equals("USER")
        );
    }
}
