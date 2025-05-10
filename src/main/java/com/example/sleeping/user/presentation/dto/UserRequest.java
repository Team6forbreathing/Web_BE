package com.example.sleeping.user.presentation.dto;

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
