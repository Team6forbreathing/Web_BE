package com.example.sleeping.user.application.command;

import com.example.sleeping.user.presentation.dto.UserRequest;

public record UserCommand(
        String userId,
        String userPw,
        String userName,
        String userGender,
        int userAge,
        int userHeight,
        int userWeight,
        boolean userComp
) {
    public static UserCommand of(UserRequest userRequest) {
        return new UserCommand(
                userRequest.userId(),
                userRequest.userPw(),
                userRequest.userName(),
                userRequest.userGender(),
                userRequest.userAge() <= 0 ? 25 : userRequest.userAge(),
                userRequest.userHeight() <= 0 ? 170 : userRequest.userHeight(),
                userRequest.userWeight() <= 0 ? 60 : userRequest.userWeight(),
                userRequest.userComp()
        );
    }
}
