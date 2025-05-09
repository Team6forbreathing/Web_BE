package com.example.sleeping.user.presentation.dto;


import com.example.sleeping.user.domain.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserResponse(
        String userId,
        String userName,
        String userGender,
        int userAge,
        int userHeight,
        int userWeight,
        boolean userComp,
        String userRole
) {
    public static UserResponse of(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getHeight(),
                user.getWeight(),
                user.isComp(),
                user.getRole().name()
        );
    }
}