package com.example.sleeping.user.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record UserRequest(
        String userId,
        String userPw,
        String userName,
        String userGender,
        int userAge,
        int userHeight,
        int userWeight,
        boolean userComp
) {
}
